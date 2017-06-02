package jittr.rest;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Qualifier;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.modelmapper.ModelMapper;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.dao.DuplicateKeyException;

import jittr.db.JitterRepository;
import jittr.domain.Jitter;

/**
 * Abstract class for all implementations of domain repositories' RESTfull facades.
 * 
 * @author Grehov
 *
 * @param <T>
 *              the type of the domain entity to handle.
 * @param <TD>
 *              the DTO type for domain entity to handle.             
 * @param <I>
 *              the type of the domain entity's repository interface.
 */
public abstract class AbstractFacade<T, TD, I extends JpaRepository<T, Long>> {
    
    protected final static String VALUE_NOT_NULL = "The value must not be null.";
    
    protected I repository;
    
    protected Class<T> entityClass;
    
    protected Class<TD> dtoClass;
    
    @Resource(name="defaultValidator")
    protected Validator validator;
    
    protected ModelMapper modelMapper;
    
    private JitterRepository jitterRepository;
    
    /**
     * Constructor for testitng.
     */
    protected AbstractFacade() {
        this.modelMapper = new ModelMapper();
    }
  
    /**
     * Repository injection constructor.
     * 
     * @param repository repository of type {@link I} for domain 
     * entity of type {@link T}.
     * @param entityClass class object of {@link T} entity.
     * @param dtoClass class object of {@link TD} object.
     */
    AbstractFacade(final I repository, final JitterRepository jitterRepository, 
            final Class<T> entityClass, final Class<TD> dtoClass) {
        this();
        this.repository = repository;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
        this.jitterRepository = jitterRepository;
    }
    
    /**
     * Persists the {@link TD} object for domain entity of type {@link T},
     * provided via the query parameter to the repository.
     * 
     * @param jitterPrincipal {@link Principal} of current {@link Jitter}.
     * @param dto {@link TD} object to save.
     * @param ucb builder for {@link org.springframework.web.util.UriComponents}
     * @return {@link ResponseEntity} response with according 'http' status.
     *         Contains saved {@link TD} object.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}
     * @throws DomainObjValidationError if entity's dto to store rep. invariant is broken.
     */
    public ResponseEntity<TD> save(final Principal jitterPrincipal, final TD dto, 
            final UriComponentsBuilder ucb) 
            throws DomainObjValidationError {
        Assert.notNull(jitterPrincipal, VALUE_NOT_NULL);
        Assert.notNull(dto, VALUE_NOT_NULL); 
        Assert.notNull(ucb, VALUE_NOT_NULL); 
        
        validateJitter(jitterPrincipal);  
        validate(dto.getClass().toString(), dto);  
        T entity = convertToEntity(dto);
        T saved = repository.save(entity);
      
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ucb.path("/jittles/")
            .path(String.valueOf(saved))
            .build()
            .toUri();
        headers.setLocation(locationUri);     
        ResponseEntity<TD> responseEntity = 
                new ResponseEntity<TD>(convertToDto(saved), headers, HttpStatus.CREATED);
        return responseEntity;
    }
    
    /**
     * Persists (adds new, updates present) the {@link Collection} of {@link TD} objects 
     * for {@link T} domain entities provided via the query parameter to the repository.  
     * 
     * @param jitterPrincipal {@link Principal} of current {@link Jitter}.
     * @param entities {@link Collection} of {@link TD} objects for domain entities to save.
     * @return {@link ResponseEntity} response with according 'http' status.
     * 
     * @throws IllegalArgumentException if anyargument is {@literal null}.
     * @throws DomainObjValidationError if any entity's dto to store rep. invariant is broken.
     * @throws DuplicateKeyException if arument's list contains duplicates.
     */
    public ResponseEntity<TD> save(final Principal jitterPrincipal, final Collection<TD> dtoList) 
            throws IllegalArgumentException, DomainObjValidationError {
        Assert.notNull(dtoList, VALUE_NOT_NULL);
        Assert.notNull(jitterPrincipal, VALUE_NOT_NULL);
        
        validateJitter(jitterPrincipal);       
        validate(dtoList);          
        repository.save(convertToEntities(dtoList));   
        ResponseEntity<TD> responseEntity = new ResponseEntity<TD>(HttpStatus.CREATED);
        return responseEntity;
    }

    /**
     * Removes the domain entity with given id, provided via the query parameter,
     * from the repository.
     * 
     * @param id unique identifier of the entity to be removed
     * @return response with whether succeed removing 'http' status.
     * 
     * @throws IllegalArgumentException if id is {@literal null}.
     */
    public ResponseEntity<TD> remove(final Long id) {
        Assert.notNull(id, VALUE_NOT_NULL);
        repository.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Finds all {@link T} entities from repository.
     * 
     * @return {@link List} of objects of type {@link TD}.
     */
    public List<TD> findAll() {
      return convertToDtos(repository.findAll());
    }
   
    /**
     * Finds the domain entity of type {@link T} with given id,
     * provided via the query parameter, from the repository. 
     * 
     * @param id unique identifier of the entity to be returned.
     * @return {@link TD} object.
     * 
     * @throws IllegalArgumentException if id is {@literal null}.
     */
    public TD findOne(final long id) {
        Assert.notNull(id, VALUE_NOT_NULL);
        return convertToDto(repository.findOne(id));
    }

    /**
     * Returns the total amount of domain entities of type {@link T} 
     * in the repository. 
     * 
     * @return total amount.
     */
    public long count() {
        return repository.count();
    }
    
    /**
     * Checks domain objects for their representation invariant continuity.
     * 
     * @param objectName {@link T} name.
     * @param validated validated domain object of type {@link T}.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     * @throws DomainObjValidationError if rep. invariant of the validated object is broken.
     */
    protected void validate(final String objectName, final TD validated) 
            throws DomainObjValidationError {
        Assert.notNull(objectName, VALUE_NOT_NULL);
        Assert.notNull(validated, VALUE_NOT_NULL);   
        
        BeanPropertyBindingResult bindingResult = 
                new BeanPropertyBindingResult(validated, objectName);
        validator.validate(validated, bindingResult);
 
        if (bindingResult.hasErrors()) {
            throw new DomainObjValidationError(bindingResult.getFieldErrors());
        }
    }
    
    /**
     * Checks the collection of domain objects for their representation invariant continuity.
     * 
     * @param validatedList validated collection of domain objects of type {@link T}.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     * @throws DomainObjValidationError if rep. invariant of any validated object is broken.
     */
    protected void validate(final Collection<TD> validatedList) 
            throws DomainObjValidationError {
        for (TD validated : validatedList) { 
            Assert.notNull(validated, VALUE_NOT_NULL);
            validate(validated.getClass().getName(), validated);
        }
    }
    
    /**
     * Converts {@link T} entity to {@link TD} object.
     * 
     * @param entity {@link T} entity to convert.
     * @return {@link TD} object.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    protected TD convertToDto(final T entity) {
        Assert.notNull(entity, VALUE_NOT_NULL);
        return  modelMapper.map(entity, dtoClass);
    }
    
    /**
     * Converts {@link TD} object to {@link T} entity object.
     * 
     * @param dto {@link TD} to convert to entity.
     * @return entity of type {@link T}.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    protected T convertToEntity(final TD dto) {
        Assert.notNull(dto, VALUE_NOT_NULL);
        return modelMapper.map(dto, entityClass);
    }
    
    /**
     * Converts list of {@link TD} objects to the list of {@link T} entity objects.
     * 
     * @param dtoList list of {@link TD} objects to convert to {@link T} entities.
     * @return list of of enttites type {@link T}.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    protected List<T> convertToEntities(final Collection<TD> dtoList) {
        Assert.notNull(dtoList, VALUE_NOT_NULL);
        
        return dtoList.stream()
                .map(dto -> convertToEntity(dto))
                .collect(Collectors.toList());
    }
    
    /**
     * Converts the list of {@link T} entity objects to the list of {@link TD} objects.
     * 
     * @param entityList list of {@link T} entities to convert to {@link TD} objects.
     * @return list of of dto objects of type {@link TD}.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    protected List<TD> convertToDtos(final Collection<T> entityList) {
        Assert.notNull(entityList, VALUE_NOT_NULL);
        
        return entityList.stream()
                .map(entity -> convertToDto(entity))
                .collect(Collectors.toList());
    }
    
    /**
     * Validates jitter with given username.
     * @param idUser {@link Jitter}'s username.
     * 
     * @throws JitterNotFoundException if no {@link Jitter} found 
     * with given username.
     */
    protected void validateJitter(final Principal principal) {
        final String usernameJitter = principal.getName();
        this.jitterRepository.findByUsername(usernameJitter).orElseThrow(
                () -> new JitterNotFoundException(usernameJitter));
    }
}

