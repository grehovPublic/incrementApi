package jittr.rest;

import static jittr.rest.SharedConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jittr.db.JitterRepository;
import jittr.db.JudgeResearchRepository;
import jittr.domain.Jitter;
import jittr.domain.JudgeResearch;
import jittr.domain.JudgeResearchBrand;
import jittr.domain.JudgeResearchLearning;
import jittr.dto.JitterDto;
import jittr.dto.JudgeResearchBrandDto;
import jittr.dto.JudgeResearchDto;
import jittr.dto.JudgeResearchLearningDto;
import jittr.dto.JudgeResearchDto;

/**
 * Implementation of {@link IJudgeResearch}'s repository RESTful facade.
 * 
 * @author Grehov
 *
 */

@RestController
public class JudgeResearchFacadeRest 
             extends AbstractFacade<JudgeResearch, JudgeResearchDto, JudgeResearchRepository> {
    
    final static String RESEARCH = "/api/research/{id}";
    final static String RESEARCH_DEFAULT = "/api/research";
    final static String RESEARCHES_BY_USERNAME = "/api/researches/{username}";
    final static String RESEARCHES = "/api/researches";
    
    @Autowired
    JudgeResearchRepository researchRepository;
    
    @Autowired
    JitterRepository jitterRepository;
    
    /**
     * See {@link AbstractFacade#AbstractFacade()}
     */
    protected JudgeResearchFacadeRest() {
    }
    
    /**
     * See {@link AbstractFacade#AbstractFacade
     * (org.springframework.data.jpa.repository.JpaRepository, JitterRepository, Class, Class)}
     */
    @Autowired
    protected
    JudgeResearchFacadeRest(final JudgeResearchRepository repository, 
            final JitterRepository jitterRepository) {
        super(repository, jitterRepository, JudgeResearch.class, JudgeResearchDto.class);
    }
    
    @Override
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = RESEARCHES, 
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JudgeResearchDto> save(@RequestBody final JudgeResearchDto researchDto, 
            final UriComponentsBuilder ucb) throws DomainObjValidationError {
        this.setResponsePath(RESEARCHES);
        return super.save(researchDto, ucb);
    }    


    /**
     * 
     * @param id container with unique identifier of the entity to be returned
     *        provided via {@link PathVariable}. 
     * @param ucb builder for {@link org.springframework.web.util.UriComponents}
     * @return {@link JudgeRepository} object with given id or with default id,
     *         if id isn't provided;
     * 
     * @throws EntityNotFoundException if entity with given id not found.
     */
    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(method = RequestMethod.GET, value = {RESEARCH, RESEARCH_DEFAULT},
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JudgeResearchDto> findOne(@PathVariable Optional<Long> id, 
            final UriComponentsBuilder ucb) {
        Long idValue = id.orElse(DEFAULT_ID);
        this.setResponsePath(RESEARCHES + "/" + idValue + "/");
        return super.findOne(idValue, ucb);
    }  

    @Override
    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(method = RequestMethod.GET, value = RESEARCHES_BY_USERNAME,
       consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JudgeResearchDto>> findByUsername(
            @PathVariable final String username, final UriComponentsBuilder ucb) {
        Assert.notNull(ucb, SharedConstants.VALUE_NOT_NULL); 
        this.validateJitter(username);
        
        this.setResponsePath(RESEARCHES);     
        List<JudgeResearchDto> found = convertToDtos(repository
                .findByJitterUsernameIn(Arrays.asList(username, DEFAULT_USERNAME))
                .orElse( new ArrayList<JudgeResearch>()));
        return new ResponseEntity<List<JudgeResearchDto>>(found, HttpStatus.FOUND);
    } 
    
    @Override
    @ResponseStatus(HttpStatus.FOUND)
    @RequestMapping(method = RequestMethod.GET, value = RESEARCHES,
       consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JudgeResearchDto>> 
                findByUsernameDefault(final UriComponentsBuilder ucb) {
        Assert.notNull(ucb, SharedConstants.VALUE_NOT_NULL); 
        
        this.setResponsePath(RESEARCHES);             
        List<JudgeResearchDto> found = convertToDtos(repository
                .findByJitterUsername(DEFAULT_USERNAME)
                .orElse( new ArrayList<JudgeResearch>()));
        return new ResponseEntity<List<JudgeResearchDto>>(found, HttpStatus.FOUND);
    }
    
    @Override
    protected JudgeResearchDto convertToDto(final JudgeResearch research) {
        JudgeResearchDto jDto = modelMapper.map(research, 
                mapResearchClassEntityToDto(research.getState()));
        jDto.setJitter(modelMapper.map(research.getJitter(), JitterDto.class));
        return jDto;
    }
    
    @Override
    protected List<JudgeResearchDto> convertToDtos(Collection<JudgeResearch> researchList) {
        Assert.notNull(researchList, SharedConstants.VALUE_NOT_NULL);     
        return researchList.stream()
                .map(research -> convertToDto(research))
                .collect(Collectors.toList());
    }
    
    @Override
    protected JudgeResearch convertToEntity(final JudgeResearchDto jDto) {
        JudgeResearch research =  modelMapper.map(jDto, 
                this.mapResearchClassDtoToEntity(jDto.getState()));
        research.setJitter(modelMapper.map(jDto.getJitter(), Jitter.class));
        return research;
    }
   
    @Override
    protected List<JudgeResearch> convertToEntities(Collection<JudgeResearchDto> researchDtoList) {
        Assert.notNull(researchDtoList, SharedConstants.VALUE_NOT_NULL);     
        return researchDtoList.stream()
                .map(researchDto -> convertToEntity(researchDto))
                .collect(Collectors.toList());
    }
    
    /*
     * Returns Dto class object according to state of researching.
     */
    private Class<? extends JudgeResearchDto> mapResearchClassEntityToDto(JudgeResearch.State state) {
        switch (state) {
        case BRANDNAME:
            return JudgeResearchBrandDto.class;
        case LEARNING:
            return JudgeResearchLearningDto.class;
        case PAYMENT:
            throw new IllegalStateException();
        case PROCESSING:
            throw new IllegalStateException();
        case READY:
            throw new IllegalStateException();
        case TREE:
            throw new IllegalStateException();
        default:
            throw new IllegalStateException();
        }
    }
    
    /*
     * Returns entity class object according to state of researching.
     */
    private Class<? extends JudgeResearch> mapResearchClassDtoToEntity(JudgeResearch.State state) {
        switch (state) {
        case BRANDNAME:
            return JudgeResearchBrand.class;
        case LEARNING:
            return JudgeResearchLearning.class;
        case PAYMENT:
            throw new IllegalStateException();
        case PROCESSING:
            throw new IllegalStateException();
        case READY:
            throw new IllegalStateException();
        case TREE:
            throw new IllegalStateException();
        default:
            throw new IllegalStateException();
        }
    }
}
