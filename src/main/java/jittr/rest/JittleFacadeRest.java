package jittr.rest;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jittr.db.JitterRepository;
import jittr.db.JittleRepository;
import jittr.domain.Jitter;
import jittr.domain.Jittle;
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
    
    /**
     * See {@link AbstractFacade#AbstractFacade()}
     */
    protected JittleFacadeRest() {
    }
    
    /**
     * See {@link AbstractFacade#AbstractFacade
     * (org.springframework.data.jpa.repository.JpaRepository, JitterRepository, Class, Class)}
     */
    @Autowired
    protected
    JittleFacadeRest(final JittleRepository repository, final JitterRepository jitterRepository) {
        super(repository, jitterRepository, Jittle.class, JittleDto.class);
    }
    
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JittleDto> save(final Principal jitterPrincipal, 
            @RequestBody Collection<JittleDto> jittles) 
            throws IllegalArgumentException, DomainObjValidationError {
        return super.save(jitterPrincipal, jittles);
    }
    
    /**
     * Pulls {@link Jittle}s with given {@link Jitter}'s {@link Principal}
     * provided via the query parameter, from the repository. 
     * 
     * @param jitterPrincipal the {@link Jitter}'s {@link Principal}, 
     * whose {@link Jittle}s to be returned.
     * @return the list of found {@link JittleDto}s. May be empty.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     * @throws JitterNotFoundException if {@link Jitter} with given {@link Principal} 
     *         not found.
     */
    @ResponseStatus(HttpStatus.FOUND)   
    @RequestMapping(method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, 
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JittleDto> pull(final Principal jitterPrincipal) {
        Assert.notNull(jitterPrincipal, SharedConstants.VALUE_NOT_NULL);
        
        validateJitter(jitterPrincipal);
        final List<Jittle> jittles = repository.findByJitterUsername(jitterPrincipal.getName());
        repository.deleteByJitterUsername(jitterPrincipal.getName());        
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
        Assert.notNull(jittleDtoList, SharedConstants.VALUE_NOT_NULL);     
        return jittleDtoList.stream()
                .map(jittleDto -> convertToEntity(jittleDto))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JittleDto> convertToDtos(Collection<Jittle> jittleList) {
        Assert.notNull(jittleList, SharedConstants.VALUE_NOT_NULL);     
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

