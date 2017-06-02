package jittr.rest;

import static jittr.rest.SharedConstants.JITTERS;

import java.net.URI;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import jittr.domain.Jitter;
import jittr.dto.JitterDto;

@RestController
@RequestMapping(JITTERS)
public class JitterFacadeRest extends AbstractFacade<Jitter, JitterDto, JitterRepository> {
    
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
    public ResponseEntity<JitterDto> save(final Principal jitterPrincipal, @RequestBody final JitterDto jitterDto, 
            final UriComponentsBuilder ucb) 
            throws DomainObjValidationError {
        return super.save(jitterPrincipal, jitterDto, ucb);
    }    

    /**
     * Returns the {@link Jitter} with given {@link Principal},
     * provided via the query parameter, from the repository. 
     * 
     * @param principal {@link Jitter}'s {@link Principal}.
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
        Assert.notNull(principal, VALUE_NOT_NULL);
        return convertToDto(repository.findByUsername(principal.getName())
                .orElseThrow(() -> new JitterNotFoundException(principal.getName())));
    }  
}
