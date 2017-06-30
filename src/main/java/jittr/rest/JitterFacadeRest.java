package jittr.rest;

import static jittr.rest.SharedConstants.JITTERS;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jittr.db.JitterRepository;
import jittr.db.JitterRepositoryCustom;
import jittr.domain.Jitter;
import jittr.dto.JitterDto;

/**
 * Implementation of {@link Jitter}'s repository RESTful facade.
 * 
 * @author Grehov
 *
 */

@RestController
@RequestMapping(JITTERS)
public class JitterFacadeRest extends AbstractFacade<Jitter, JitterDto, JitterRepository> {
    
    public final static String MERGE = "/merge";
    public final static String ALL = "/all";
    
    @Autowired
    JitterRepositoryCustom repositoryCustom;
    
    /**
     * See {@link AbstractFacade#AbstractFacade()}
     */
    protected JitterFacadeRest() {
    }
    
    /**
     * See {@link AbstractFacade#AbstractFacade
     * (org.springframework.data.jpa.repository.JpaRepository, JitterRepository, Class, Class)}
     */
    @Autowired
    protected
    JitterFacadeRest(final JitterRepository repository, final JitterRepository jitterRepository) {
        super(repository, jitterRepository, Jitter.class, JitterDto.class);
    }
    
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JitterDto> save(@RequestBody final JitterDto jitterDto, final UriComponentsBuilder ucb) 
            throws DomainObjValidationError {
        this.setResponsePath(JITTERS);
        return super.save(jitterDto, ucb);
    }    

    /**
     * Returns the {@link Jitter} with given {@link Principal},
     * provided via the query parameter, from the repository. 
     * 
     * @param principal client application's {@link Principal}.
     * @return {@link Jitter} with given {@link Principal}.
     * 
     * @throws IllegalArgumentException if given {@link Principal} is {@literal null}.
     * @throws JitterNotFoundException if {@link Jitter} with given {@link Principal} 
     *         not found.
     */
    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, 
        produces = MediaType.APPLICATION_JSON_VALUE)
    public JitterDto findOne(final Principal principal) {
        Assert.notNull(principal, SharedConstants.VALUE_NOT_NULL);
        return convertToDto(repository.findByUsername(principal.getName())
                .orElseThrow(() -> new JitterNotFoundException(principal.getName())));
    }  
    

    /**
     * See {@link AbstractFacade#findAll(UriComponentsBuilder, String)}
     */
    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(method = RequestMethod.GET, value = ALL, 
       consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JitterDto>> findAll(final String username, 
            final UriComponentsBuilder ucb) {
        return super.findAll(ucb);
    } 
    
    /**
     * Merges given {@link Jitter} with repository.
     *  
     * @param principal client application's {@link Principal}.
     * @param jitter {@link JitterDto} to be merged.
     * @param ucb
     * @return {@link ResponseEntity} with merged Jitter.
     * @throws DomainObjValidationError if {@link JitterDto}'s to merge rep. invariant is broken.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = MERGE, 
          consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JitterDto> merge(final Principal principal, 
            @RequestBody final JitterDto jitter, final UriComponentsBuilder ucb) 
            throws DomainObjValidationError {
        Assert.notNull(principal, SharedConstants.VALUE_NOT_NULL); 
        Assert.notNull(jitter, SharedConstants.VALUE_NOT_NULL); 
        Assert.notNull(ucb, SharedConstants.VALUE_NOT_NULL);       
        this.validateJitter(principal);
        this.validate(jitter.getClass().toString(), jitter);
        
        JitterDto merged = convertToDto(repositoryCustom.merge(convertToEntity(jitter)));
        return new ResponseEntity<JitterDto>(merged, getHttpHeaders(merged, ucb, JITTERS),
                HttpStatus.CREATED);
    }  
    
}
