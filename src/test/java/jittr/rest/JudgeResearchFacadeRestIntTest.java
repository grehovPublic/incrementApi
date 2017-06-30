package jittr.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import jittr.Jittr;
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

import static jittr.rest.SharedConstants.*;
import static jittr.domain.SharedConstants.*;
import static jittr.rest.JudgeResearchFacadeRest.*;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jittr.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JudgeResearchFacadeRestIntTest {   
    
    private JudgeResearch researchApple = new JudgeResearchBrand("Apple");
    private JudgeResearch researchSamsung = new JudgeResearchLearning("Samsung");
    
    private JudgeResearchDto researchAppleDto;
    private JudgeResearchDto researchSamsungDto;
    
    private long researchesCount;

    @Autowired
    JudgeResearchFacadeRest controller;
    

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private StringHttpMessageConverter stringConverter;
    
    @Autowired
    private JudgeResearchRepository researchRepository;
    
    @Autowired
    private JitterRepository jitterRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private final Principal correctPrincipal;
    private final Principal wrongPrincipal;
    
    public JudgeResearchFacadeRestIntTest() {
        correctPrincipal = new Principal() {
            @Override
            public String getName() {
                return "jittr";
            }
        };     
        
        wrongPrincipal = new Principal() {
            @Override
            public String getName() {
                return "jittrr";
            }
        }; 
    }

    @Autowired
    void setConverters(List<HttpMessageConverter<?>>  converters) {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();           

        this.mappingJackson2HttpMessageConverter = converters.stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);     
    }



    @Before
    public void setup() throws Exception {
        
        Jitter jitter = jitterRepository.findByUsername("habuma")
                .orElseThrow(() -> new IllegalStateException());
        researchApple.setJitter(jitter);
        researchApple = researchRepository.save(researchApple);
        researchSamsung.setJitter(jitter);
        researchSamsung = researchRepository.save(researchSamsung); 

        researchAppleDto = controller.convertToDto(researchApple);
        researchSamsungDto = controller.convertToDto(researchSamsung);
        
        researchesCount = researchRepository.count();
    } 
    
    
    /*
     * Testing strategy for:
     *  public ResponseEntity<JudgeResearchDto> save(@RequestBody final JudgeResearchDto researchDto, 
            final UriComponentsBuilder ucb):
     *        
     * Partitions:
     *    researchDto: = null, any rep. invariant is broken, valid researchDto, 
     *                 new researchDto, present researchDto;
     *                 researchDto implemented by: JudgeResearchBrandDto
     *                                             JudgeResearchLearningDto
     *    
     *    # returns ResponseEntity with according http status:
     *          BAD_REQUEST, CREATED.
     *          Saves exact given researchDto.
     */
    
    @Test
    @Transactional
    public void testSave_NullJitter_BadRequest() throws Exception {        
        mockMvc.perform(post(RESEARCHES)
                .content(this.json(null))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testSave_InvalidResearchBrand_BadRequest() throws Exception {        
        mockMvc.perform(post(RESEARCHES)
                .content(this.json(new JudgeResearchBrandDto("")))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testSave_ValidPresentResearchBrand_Created() throws Exception {  
        researchAppleDto.setName("Updated Name");
        
        mockMvc.perform(post(RESEARCHES).principal(correctPrincipal)
                .content(this.json(researchAppleDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(researchAppleDto.getName())))
                .andExpect(jsonPath("$.state", is(researchAppleDto.getState().toString())));
        
        assertThat(this.researchRepository.count()).isEqualTo(this.researchesCount);
    } 
    
    @Test
    @Transactional
    public void testSave_ValidNewResearchBrand_Created() throws Exception {  
        researchAppleDto.setName("Updated Name");
        researchAppleDto.setId(1000L);
        
        mockMvc.perform(post(RESEARCHES).principal(correctPrincipal)
                .content(this.json(researchAppleDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(researchAppleDto.getName())))
                .andExpect(jsonPath("$.state", is(researchAppleDto.getState().toString())));
        
        assertThat(this.researchRepository.count()).isEqualTo(this.researchesCount + 1);
    }
    
    @Test
    @Transactional
    public void testSave_InvalidResearchLearning_BadRequest() throws Exception {        
        mockMvc.perform(post(RESEARCHES)
                .content(this.json(new JudgeResearchLearningDto("")))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testSave_ValidPresentResearchLearning_Created() throws Exception {  
        researchSamsungDto.setName("Updated Name");
        
        mockMvc.perform(post(RESEARCHES).principal(correctPrincipal)
                .content(this.json(researchSamsungDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(researchSamsungDto.getName())))
                .andExpect(jsonPath("$.state", is(researchSamsungDto.getState().toString())));
        
        assertThat(this.researchRepository.count()).isEqualTo(this.researchesCount);
    } 
    
    @Test
    @Transactional
    public void testSave_ValidNewResearchLearning_Created() throws Exception {  
        researchSamsungDto.setName("Updated Name");
        researchSamsungDto.setId(1000L);
        
        mockMvc.perform(post(RESEARCHES).principal(correctPrincipal)
                .content(this.json(researchSamsungDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(researchSamsungDto.getName())))
                .andExpect(jsonPath("$.state", is(researchSamsungDto.getState().toString())));
        
        assertThat(this.researchRepository.count()).isEqualTo(this.researchesCount + 1);
    }
    
    /*
     * Testing strategy for:
     *      public ResponseEntity<JudgeResearchDto> findOne(final Long id, 
     *      final UriComponentsBuilder ucb :
     *        
     * Partitions:
     *    id: = null, = Long.Min, = 0, = Long.Max 
     *    
     *    # returns ResponseEntity with according http status: FOUND.
     *              contains found valid JudgeResearchDto' sub type;
     *              or throws EntityNotFoundException if entity not found.
     */
    
    @Test
    @Transactional
    public void testfindOne_NullId_FoundDefault() throws Exception {        
        mockMvc.perform(get(RESEARCH_DEFAULT)
                .contentType(contentType))
                .andExpect(status().isFound());
    } 
    
    @Test
    @Transactional
    public void testfindOne_MinId_NotFound() throws Exception {        
        mockMvc.perform(get(RESEARCH, "" + Long.MIN_VALUE)
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @Transactional
    public void testfindOne_MaxId_NotFound() throws Exception {        
        mockMvc.perform(get(RESEARCH, "" + Long.MAX_VALUE)
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @Transactional
    public void testfindOneCorrectId_Found() throws Exception {        
        mockMvc.perform(get(RESEARCH, researchAppleDto.getId().toString())
                .contentType(contentType))
                .andExpect(status().isFound())       
                .andExpect(jsonPath("$.name", is(researchAppleDto.getName())))
                .andExpect(jsonPath("$.state", is(researchAppleDto.getState().toString())));
        
        mockMvc.perform(get(RESEARCH, researchSamsungDto.getId().toString())
                .contentType(contentType))
                .andExpect(status().isFound())       
                .andExpect(jsonPath("$.name", is(researchSamsungDto.getName())))
                .andExpect(jsonPath("$.state", is(researchSamsungDto.getState().toString())));
    }
    
    
    
    /*
     * Testing strategy for:
     *     public ResponseEntity<List<JudgeResearchDto>> findByUsername(@RequestBody final String username, 
     *      final UriComponentsBuilder ucb) :
     *        
     * Partitions:
     *    username.size: = 0, > 0, =Int.Max 
     *    username.value: null, present, not present
     *    
     *    # returns - {@link ResponseEntity} response with according 'http' status:
     *                     FOUND, NOT_FOUND, BAD_REQUEST
     *                     Contains list with found unique researches:
     *                          lists.size = 0;
     *                          list.size > 0;
     */
    
    @Test
    @Transactional
    public void testFindByUsername_NullName_BadRequest() throws Exception {        
        mockMvc.perform(get(RESEARCHES_BY_USERNAME, "")
                .contentType(contentType))
                .andExpect(status().is3xxRedirection());
    }  
   
    
    @Test
    @Transactional
    public void testFindByUsername_NameSizeThreeNotPresent_NotFound() throws Exception {        
        mockMvc.perform(get(RESEARCHES_BY_USERNAME, StringUtils.repeat("x", USERNAME_MAX_LENGTH))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    } 
    

    @Test
    @Transactional
    public void testFindByUsername_NameTooBigSize_BadRequest() throws Exception {        
        mockMvc.perform(get(RESEARCHES_BY_USERNAME, StringUtils.repeat("X", USERNAME_MAX_LENGTH + 1))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @Transactional
    public void testFindByUsername_NameThousandSize_BadRequest() throws Exception {        
        mockMvc.perform(get(RESEARCHES_BY_USERNAME, StringUtils.repeat("X", 1000))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @Transactional
    public void testFindByUsername_ValidNamePresentEmptyList_Found() throws Exception {        
        mockMvc.perform(get(RESEARCHES_BY_USERNAME, "admin")
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    @Transactional
    public void testFindByUsername_ValidNamePresentListSizeTwo_Found() throws Exception {    
        mockMvc.perform(get(RESEARCHES_BY_USERNAME, "habuma")
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].state", is(this.researchApple.getState().toString())))
                .andExpect(jsonPath("$[0].name", is(this.researchApple.getName())))
                .andExpect(jsonPath("$[1].name", is(this.researchSamsung.getName())))
                .andExpect(jsonPath("$[1].state", is(this.researchSamsung.getState().toString())));
    }
    
    /*
     * Testing strategy for:
     *     public ResponseEntity<List<JudgeResearchDto>> findByUsernameDefault( 
     *      final UriComponentsBuilder ucb) :
     *        
     * Partitions:
     *    
     *    # returns - {@link ResponseEntity} response with according 'http' status:
     *                     FOUND, NOT_FOUND, BAD_REQUEST
     *                     Contains list with found unique researches:
     *                          lists.size = 0;
     *                          list.size > 0;
     */

    
    @Test
    @Transactional
    public void testFindByUsernameDef_ListSizeTwo_Found() throws Exception {  
        Jitter defJitter = jitterRepository.findByUsername("jittr")
                .orElseThrow(() -> new IllegalStateException());
        
        mockMvc.perform(get(RESEARCHES)
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].state", is(this.researchApple.getState().toString())))
                .andExpect(jsonPath("$[0].name", is(this.researchApple.getName())))
                .andExpect(jsonPath("$[0].jitter.username", is(defJitter.getUsername())))
                .andExpect(jsonPath("$[1].name", is(this.researchSamsung.getName())))
                .andExpect(jsonPath("$[1].state", is(this.researchSamsung.getState().toString())))
                .andExpect(jsonPath("$[1].jitter.username", is(defJitter.getUsername())));
    }
    

    protected String json(Object o) throws IOException {
        StringUtils a;
       
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
