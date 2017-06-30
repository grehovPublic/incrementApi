package jittr.rest;

import static jittr.domain.SharedConstants.USERNAME_MAX_LENGTH;
import static jittr.domain.SharedConstants.USERNAME_MIN_LENGTH;
import static jittr.domain.SharedConstants.VALIDATE_NOTE_USERNAME_SIZE;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
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
    
    public static final String DEFAULT_USERNAME = "jittr";
    public static final Long DEFAULT_ID = 1L;
    
    protected I repository;
    
    protected Class<T> entityClass;
    
    protected Class<TD> dtoClass;
    
    @Resource(name="defaultValidator")
    protected Validator validator;
    
    protected ModelMapper modelMapper;
    
    private JitterRepository jitterRepository;
    
    private String responsPath;

    /**
     * Constructor for testitng.
     */
    protected AbstractFacade() {
        this.modelMapper = new ModelMapper();
        this.responsPath = null;
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
     * @param dto {@link TD} object to save.
     * @param ucb builder for {@link org.springframework.web.util.UriComponents}
     * @return {@link ResponseEntity} response with according 'http' status.
     *         Contains saved {@link TD} object.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}
     * @throws DomainObjValidationError if entity's dto to store rep. invariant is broken.
     */
    public ResponseEntity<TD> save(final TD dto, final UriComponentsBuilder ucb) 
            throws DomainObjValidationError {
        Assert.notNull(dto, SharedConstants.VALUE_NOT_NULL); 
        Assert.notNull(ucb, SharedConstants.VALUE_NOT_NULL); 
        
        validate(dto.getClass().toString(), dto);  
        T entity = convertToEntity(dto);
        TD saved = convertToDto(repository.save(entity));
        return new ResponseEntity<TD>(saved, HttpStatus.CREATED);
    }
    
    /**
     * Persists (adds new, updates present) the {@link Collection} of {@link TD} objects 
     * for {@link T} domain entities provided via the query parameter to the repository.  
     * 
     * @param principal {@link Principal} of current client application.
     * @param entities {@link Collection} of {@link TD} objects for domain entities to save.
     * @return {@link ResponseEntity} response with according 'http' status.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     * @throws DomainObjValidationError if any entity's dto to store rep. invariant is broken.
     * @throws DuplicateKeyException if arument's list contains duplicates.
     */
    public ResponseEntity<TD> save(final Principal principal, final Collection<TD> dtoList) 
            throws IllegalArgumentException, DomainObjValidationError {
        Assert.notNull(dtoList, SharedConstants.VALUE_NOT_NULL);
        Assert.notNull(principal, SharedConstants.VALUE_NOT_NULL);
        
        validateJitter(principal);       
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
        Assert.notNull(id, SharedConstants.VALUE_NOT_NULL);
        repository.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Finds all {@link T} entities from repository.
     *
     * @param ucb builder for {@link org.springframework.web.util.UriComponents}
     * 
     * @return {@link ResponseEntity} response with according 'http' status.
     *         Contains list with found unique {@link TD} objects.
     *         
     * @throws IllegalArgumentException if any argument is {@literal null}. 
     */
    public ResponseEntity<List<TD>> findAll(final UriComponentsBuilder ucb) { 
        Assert.notNull(ucb, SharedConstants.VALUE_NOT_NULL); 
        
        List<TD> found = convertToDtos(repository.findAll());
        return new ResponseEntity<List<TD>>(found, HttpStatus.FOUND);
    }
    
    /**
     * Finds all {@link T} accessible (free and personal) entities from repository for 
     * {@link Jitter} with given username.
     *
     * @param username {@link Jitter}'s unique username. See {@link Jitter#getUsername()}.
     * @param ucb builder for {@link org.springframework.web.util.UriComponents}
     * 
     * @return {@link ResponseEntity} response with according 'http' status.
     *         Contains list with found unique {@link TD} objects. May be empty.
     *         
     * @throws IllegalArgumentException if any argument is {@literal null}, 
     *         if username violates defined constraints. 
     * @throws JitterNotFoundException if jitter with given username not found. 
     */
    public ResponseEntity<List<TD>> findByUsername(final String username,  
            final UriComponentsBuilder ucb) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Finds all {@link T} entities from repository with free access for any
     * visitor.
     *
     * @param ucb builder for {@link org.springframework.web.util.UriComponents}
     * 
     * @return {@link ResponseEntity} response with according 'http' status.
     *         Contains list with found unique {@link TD} objects. May be empty.
     *         
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    public ResponseEntity<List<TD>> findByUsernameDefault(final UriComponentsBuilder ucb) {
        throw new UnsupportedOperationException();
    }
   
    /**
     * Finds the domain entity of type {@link T} with given id,
     * provided via the query parameter, from the repository. 
     * 
     * @param id unique identifier of the entity to be returned.
     * @param ucb builder for {@link org.springframework.web.util.UriComponents}
     * @return {@link TD} object.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     * @throws EntityNotFoundException if entity with given id not found.
     */
    public ResponseEntity<TD> findOne(final Long id, final UriComponentsBuilder ucb) {
        Assert.notNull(id, SharedConstants.VALUE_NOT_NULL);
        Assert.notNull(ucb, SharedConstants.VALUE_NOT_NULL);
        
        Optional<T> foundEntity = Optional.ofNullable(repository.findOne(id));
        TD found = convertToDto(foundEntity
                .orElseThrow(() -> new EntityNotFoundException(id.toString())));
        return new ResponseEntity<TD>(found, HttpStatus.FOUND);
    }
    
    /**
     * Returns {@link TD} object for default domain entity of type {@link T}.
     * 
     * @param ucb builder for {@link org.springframework.web.util.UriComponents}
     * @return {@link TD} object.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    public ResponseEntity<TD> findOneDefault(final UriComponentsBuilder ucb) {
        Assert.notNull(ucb, SharedConstants.VALUE_NOT_NULL);
        
        Optional<T> foundEntity = Optional.ofNullable(repository.findOne(DEFAULT_ID));
        TD found = convertToDto(foundEntity
                .orElseThrow(() -> new EntityNotFoundException(DEFAULT_ID.toString())));
        return new ResponseEntity<TD>(found, HttpStatus.FOUND);
    }

    /**
     * Returns the total amount of domain entities of type {@link T} 
     * in the repository. s
     * 
     * @return total amount.
     */
    public long count() {
        return repository.count();
    }
    
    /*
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
        Assert.notNull(objectName, SharedConstants.VALUE_NOT_NULL);
        Assert.notNull(validated, SharedConstants.VALUE_NOT_NULL);   
        
        BeanPropertyBindingResult bindingResult = 
                new BeanPropertyBindingResult(validated, objectName);
        validator.validate(validated, bindingResult);
 
        if (bindingResult.hasErrors()) {
            throw new DomainObjValidationError(bindingResult.getFieldErrors());
        }
    }
    
    /*
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
            Assert.notNull(validated, SharedConstants.VALUE_NOT_NULL);
            validate(validated.getClass().getName(), validated);
        }
    }
    
    /*
     * Converts {@link T} entity to {@link TD} object.
     * 
     * @param entity {@link T} entity to convert.
     * @return {@link TD} object.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    protected TD convertToDto(final T entity) {
        Assert.notNull(entity, SharedConstants.VALUE_NOT_NULL);
        return  modelMapper.map(entity, dtoClass);
    }
    
    /*
     * Converts {@link TD} object to {@link T} entity object.
     * 
     * @param dto {@link TD} to convert to entity.
     * @return entity of type {@link T}.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    protected T convertToEntity(final TD dto) {
        Assert.notNull(dto, SharedConstants.VALUE_NOT_NULL);
        return modelMapper.map(dto, entityClass);
    }
    
    /*
     * Converts list of {@link TD} objects to the list of {@link T} entity objects.
     * 
     * @param dtoList list of {@link TD} objects to convert to {@link T} entities.
     * @return list of of enttites type {@link T}.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    protected List<T> convertToEntities(final Collection<TD> dtoList) {
        Assert.notNull(dtoList, SharedConstants.VALUE_NOT_NULL);
        
        return dtoList.stream()
                .map(dto -> convertToEntity(dto))
                .collect(Collectors.toList());
    }
    
    /*
     * Converts the list of {@link T} entity objects to the list of {@link TD} objects.
     * 
     * @param entityList list of {@link T} entities to convert to {@link TD} objects.
     * @return list of of dto objects of type {@link TD}.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    protected List<TD> convertToDtos(final Collection<T> entityList) {
        Assert.notNull(entityList, SharedConstants.VALUE_NOT_NULL);
        
        return entityList.stream()
                .map(entity -> convertToDto(entity))
                .collect(Collectors.toList());
    }
    
    /*
     * Validates client application with given principals.
     * @param principal client application's principal.
     * 
     * @throws JitterNotFoundException if no {@link Jitter} found 
     * with given username.
     */
    protected void validateJitter(final Principal principal) {
        this.validateJitter(principal.getName());
    }
    
    /*
     * Validates Jitter with given username
     * .
     * @param username Jitter's unique username.
     * 
     * @throws JitterNotFoundException if no {@link Jitter} found 
     * with given username.
     */
    protected void validateJitter(final String username) {
        Assert.notNull(username, SharedConstants.VALUE_NOT_NULL); 
        Assert.hasLength(username, SharedConstants.VALUE_NOT_EMPTY); 
        Assert.isTrue((username.length() >= USERNAME_MIN_LENGTH) &&
                (username.length() <= USERNAME_MAX_LENGTH), VALIDATE_NOTE_USERNAME_SIZE);
        
        this.jitterRepository.findByUsername(username).orElseThrow(
                () -> new JitterNotFoundException(username));
    }
    
    /*
     * Produce HTTP headers for response.
     */
    protected HttpHeaders getHttpHeaders(final TD dto, 
            final UriComponentsBuilder ucb, final String url) {
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = ucb.path(url)
            .path(String.valueOf(dto))
            .build()
            .toUri();
        headers.setLocation(locationUri);   
        return headers;
    }
    
    protected String getResponsPath() {
        return responsPath;
    }

    protected void setResponsePath(String responsPath) {
        this.responsPath = responsPath;
    }
}

