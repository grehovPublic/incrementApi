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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import jittr.Jittr;
import jittr.db.JitterRepository;
import jittr.domain.Jitter;
import jittr.dto.JitterDto;
import static jittr.rest.SharedConstants.*;
import static jittr.rest.JitterFacadeRest.*;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jittr.class},
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
        mockMvc.perform(post(JITTERS).principal(wrongPrincipal)
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
        mockMvc.perform(post(JITTERS).principal(correctPrincipal)
                .content(this.json(new JitterDto()))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testSave_CorrectPrincipalValidPresentJitter_Created() throws Exception {  
        JitterDto presentJitterDto = jittersDto.get(0);
        presentJitterDto.setFullName("Updated Name");
        
        mockMvc.perform(post(JITTERS).principal(correctPrincipal)
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
        newJitterDto.setUsername("NewName");
        newJitterDto.setFullName("NewName");
        
        mockMvc.perform(post(JITTERS).principal(correctPrincipal)
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
        mockMvc.perform(get(JITTERS).principal(correctPrincipal)
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.username", is(correctPrincipal.getName())));
    } 
    
    /*
     * Testing strategy for:
     * public ResponseEntity<JitterDto> merge(final Principal principal, 
            @RequestBody final JitterDto jitter, final UriComponentsBuilder ucb) :
     *        
     * Partitions:
     *    principal: = correct, wrong;
     *    jitter: = null, any rep. invariant is broken, valid JitterDto, 
     *              new jitter, present jitter;
     *    
     *    # returns ResponseEntity with according http status:
     *          BAD_REQUEST, CREATED.
     *          Merges exact given jitter.
     */
    
    @Test
    @Transactional
    public void testMerge_WrongPrincipalNullJitter_BadRequest() throws Exception {        
        mockMvc.perform(post(JITTERS + MERGE).principal(wrongPrincipal)
                .content(this.json(null))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testMerge_WrongPrincipalValidJitter_NotFound() throws Exception {        
        mockMvc.perform(post(JITTERS + MERGE).principal(wrongPrincipal)
                .content(this.json(jittersDto.get(0)))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    } 
    
    @Test
    @Transactional
    public void testMerge_CorrectPrincipalInvalidJitter_BadRequest() throws Exception {        
        mockMvc.perform(post(JITTERS + MERGE).principal(correctPrincipal)
                .content(this.json(new JitterDto()))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testMerge_CorrectPrincipalValidPresentJitter_Found() throws Exception {  
        JitterDto present = jittersDto.stream()
                                         .filter((j) -> j.getUsername().equals("jittr"))
                                         .findAny()
                                         .orElseThrow(() -> new IllegalStateException());
        
        JitterDto updated = new JitterDto(present);
        updated.setFullName("Updated Name");
        updated.setPassword("NewPassword");
        updated.setRole(Jitter.Role.ROLE_ADMIN);
        
        mockMvc.perform(post(JITTERS + MERGE).principal(correctPrincipal)
                .content(this.json(updated))
                .contentType(contentType))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName", is(updated.getFullName())))
                
                .andExpect(jsonPath("$.password", is(present.getPassword())))
                .andExpect(jsonPath("$.role", is(present.getRole().toString())));

        assertEquals(jitters.size(), this.jitterRepository.findAll().size());
    } 
    
    @Test
    @Transactional
    public void testMerge_CorrectPrincipalValidNewJitter_Created() throws Exception {  
        JitterDto newJitterDto = jittersDto.get(0);
        newJitterDto.setId(1000L);
        newJitterDto.setUsername("NewName");
        newJitterDto.setFullName("NewName");
        
        mockMvc.perform(post(JITTERS + MERGE).principal(correctPrincipal)
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
     *  public List<JitterDto> findAll() :
     *        
     * Partitions:    
     *    # returns list of all Jitters in repository. Don't contains duplicates:
     *          list size: =0, =1, >1 
     */
    
    @Test
    @Transactional
    public void testGetAll_SizeMoreThanOne() throws Exception {        
        mockMvc.perform(get(JITTERS + ALL)
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$[0].username", is(jittersDto.get(0).getUsername())))
                .andExpect(jsonPath("$[5].username", is(jittersDto.get(5).getUsername())));
    } 
    
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


}
