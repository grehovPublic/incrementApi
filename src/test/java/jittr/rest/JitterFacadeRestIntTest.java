package jittr.rest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import jittr.Jittr;
import jittr.db.JitterRepository;
import jittr.db.JittleRepository;
import jittr.db.jpa.JpaConfig;
import jittr.domain.Jitter;
import jittr.domain.Jittle;
import jittr.dto.JitterDto;
import jittr.dto.JittleDto;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jittr.class, JpaConfig.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JitterFacadeRestIntTest {

    @Autowired
    JitterFacadeRest controller;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    Principal correctPrincipal;
    Principal wrongPrincipal;
    private List<Jitter> jitters = new ArrayList<>();
    private List<JitterDto> jittersDto = new ArrayList<>();
    
    @Autowired
    private JitterRepository jitterRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    public JitterFacadeRestIntTest() {
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
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        jitters = this.jitterRepository.findAll();
        jittersDto = this.controller.convertToDtos(jitters);
    } 
    
    /*
     * Testing strategy for:
     * public ResponseEntity<JitterDto> save(final Principal jitterPrincipal, 
     *          final JitterDto jitterDto, final UriComponentsBuilder ucb) :
     *        
     * Partitions:
     *    jitterPrincipal: = correct, wrong;
     *    jitter: = null, any rep. invariant is broken, valid JitterDto, 
     *              new jitter, present jitter;
     *    
     *    # returns ResponseEntity with according http status:
     *          BAD_REQUEST, NOT_FOUND, CREATED.
     *          Saves exact given jitter.
     */
    
    @Test
    @Transactional
    public void testSave_WrongPrincipalNullJitter_BadRequest() throws Exception {        
        mockMvc.perform(post("/jitters").principal(wrongPrincipal)
                .content(this.json(null))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testSave_WrongPrincipalValidJitter_NotFound() throws Exception {        
        mockMvc.perform(post("/jitters").principal(wrongPrincipal)
                .content(this.json(jittersDto.get(0)))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    } 
    
    @Test
    @Transactional
    public void testSave_CorrectPrincipalInvalidJitter_BadRequest() throws Exception {        
        mockMvc.perform(post("/jitters").principal(correctPrincipal)
                .content(this.json(new JitterDto()))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testSave_CorrectPrincipalValidPresentJitter_Created() throws Exception {  
        JitterDto presentJitterDto = jittersDto.get(0);
        presentJitterDto.setFullName("Updated Name");
        
        mockMvc.perform(post("/jitters").principal(correctPrincipal)
                .content(this.json(presentJitterDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName", is(presentJitterDto.getFullName())));

        assertEquals(jitters.size(), this.jitterRepository.findAll().size());
    } 
    
    @Test
    @Transactional
    public void testSave_CorrectPrincipalValidNewJitter_Created() throws Exception {  
        JitterDto newJitterDto = jittersDto.get(0);
        newJitterDto.setId(1000L);
        newJitterDto.setFullName("New Name");
        
        mockMvc.perform(post("/jitters").principal(correctPrincipal)
                .content(this.json(newJitterDto))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName", is(newJitterDto.getFullName())));

        List<Jitter> upadtedJitterList = this.jitterRepository.findAll();
        assertEquals(jitters.size() + 1, upadtedJitterList.size());
        assertEquals(newJitterDto.getFullName(), 
                upadtedJitterList.get(upadtedJitterList.size() - 1).getFullName());
    } 
    
    /*
     * Testing strategy for:
     * public JitterDto findOne(final Principal principal):
     *        
     * Partitions:
     *    jitterPrincipal: = correct, wrong;
     *    
     *    # returns ResponseEntity with according http status:
     *          NOT_FOUND, FOUND.
     *          containing found valid JitterDto.
     */
    
    @Test
    @Transactional
    public void testFindeOne_WrongPrincipal_NotFound() throws Exception {        
        mockMvc.perform(get("/jitters").principal(wrongPrincipal)
                .contentType(contentType))
                .andExpect(status().isNotFound());
    } 
    
    @Test
    @Transactional
    public void testFindeOne_CorrectPrincipal_Found() throws Exception {        
        mockMvc.perform(get("/jitters").principal(correctPrincipal)
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.username", is(correctPrincipal.getName())));
    } 
    
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


}
