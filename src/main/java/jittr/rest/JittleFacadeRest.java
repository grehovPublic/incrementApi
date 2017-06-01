package jittr.rest;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jittr.db.JitterRepository;
import jittr.db.JittleRepository;
import jittr.domain.Jitter;
import jittr.domain.Jittle;
import jittr.domain.Jittle.TargetQueue;
import jittr.dto.JitterDto;
import jittr.dto.JittleDto;

import static jittr.rest.SharedConstants.*;

/**
 * Implementation of {@link Jittle}'s repository RESTful facade.
 * 
 * @author Grehov
 *
 */
@RestController
@RequestMapping(JITTLES)
public class JittleFacadeRest extends AbstractFacade<Jittle, JittleDto, JittleRepository> {
    
    protected static final String FIND_ONE = "/{id}";
    protected static final String COUNT = "/count";
    protected static final String PULL_BY_IDJITTER_TQUEUE = "/byjitter/{idJitter}{tQueue}";
    
    /**
     * See {@link AbstractFacade#AbstractFacade()}
     */
    protected JittleFacadeRest() {
    }
    
    @Autowired
    protected
    JittleFacadeRest(final JittleRepository repository, final JitterRepository jitterRepository) {
        super(repository, jitterRepository, Jittle.class, JittleDto.class);
    }
    
    @Override
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<JittleDto> save(final Principal jitterPrincipal, 
            @RequestBody Collection<JittleDto> jittles) 
            throws IllegalArgumentException, DomainObjValidationError {
        return super.save(jitterPrincipal, jittles);
    }


    @Override
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    public List<JittleDto> findAll() {
        return super.findAll();
    }

    @Override
    @RequestMapping(value = FIND_ONE, method = RequestMethod.GET, 
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public JittleDto findOne(@PathVariable final long id) throws IllegalArgumentException {     
        return super.findOne(id);
    }
 
    @Override
    @RequestMapping(value = COUNT, method = RequestMethod.GET, 
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public long count() {
        return super.count();
    }
    
    /**
     * Pulls {@link Jittle}s with given {@link Jitter}'s id and target queue,
     * provided via the query parameters, from the repository. 
     * 
     * @param jitterPrincipal the {@link Jitter}'s {@link Principal}, 
     * whose {@link Jittle}s to be returned.
     * @param tQueue target queue holding these {@link Jittle}s. 
     * @return the list of found {@link JittleDto}s. May be empty.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     * @throws EmptyResultDataAccessException if no{@link Jitter} found.
     */
    @RequestMapping(value = PULL_BY_IDJITTER_TQUEUE, method = RequestMethod.GET, 
                    consumes = MediaType.APPLICATION_JSON_VALUE, 
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JittleDto> pull(final Principal jitterPrincipal,
            @PathVariable final TargetQueue tQueue) {
        Assert.notNull(jitterPrincipal, VALUE_NOT_NULL);
        Assert.notNull(tQueue, VALUE_NOT_NULL);
        
        validateJitter(jitterPrincipal);
        final List<Jittle> jittles = 
                repository.findByJitterUsernameAndTQueue(jitterPrincipal.getName(), tQueue);
        repository.deleteByJitterUsernameAndTQueue(jitterPrincipal.getName(), tQueue);        
        return convertToDtos(jittles);
    }  

    @Override
    protected JittleDto convertToDto(final Jittle jittle) {
        JittleDto jDto =  modelMapper.map(jittle, JittleDto.class);
        jDto.setJitter(modelMapper.map(jittle.getJitter(), JitterDto.class));
        return jDto;
    }
    
    @Override
    protected Jittle convertToEntity(final JittleDto jDto) {
        Jittle jittle =  modelMapper.map(jDto, Jittle.class);
        jittle.setJitter(modelMapper.map(jDto.getJitter(), Jitter.class));
        return jittle;
    }
   
    @Override
    protected List<Jittle> convertToEntities(Collection<JittleDto> jittleDtoList) {
        Assert.notNull(jittleDtoList, VALUE_NOT_NULL);     
        return jittleDtoList.stream()
                .map(jittleDto -> convertToEntity(jittleDto))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JittleDto> convertToDtos(Collection<Jittle> jittleList) {
        Assert.notNull(jittleList, VALUE_NOT_NULL);     
        return jittleList.stream()
                .map(jittle -> convertToDto(jittle))
                .collect(Collectors.toList());
    }
  
    /**
     * {@link Jittle}s controller's exceptions handler for 'DuplicateJittle' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return error with details describing.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   
    public Error handleDuplicateJittle(DuplicateKeyException e) {
      return new Error("Duplicate jittle: " + e.getMessage());
    }
}

