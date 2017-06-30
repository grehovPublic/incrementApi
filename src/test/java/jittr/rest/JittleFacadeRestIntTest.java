package jittr.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import jittr.db.JittleRepository;
import jittr.domain.Jittle;
import jittr.dto.JittleDto;
import jittr.rest.JittleFacadeRest;
import static jittr.rest.SharedConstants.*;


@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jittr.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JittleFacadeRestIntTest  {
    
    @Autowired
    JittleFacadeRest controller;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    Principal correctPrincipal;
    Principal wrongPrincipal;
    private List<Jittle> jittles;
    private List<JittleDto> jittlesDto;

    @Autowired
    private JittleRepository jittleRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    public JittleFacadeRestIntTest() {
        jittles = new ArrayList<>();
        jittlesDto = new ArrayList<>();
        
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
        jittles = this.jittleRepository.findAll();
        jittlesDto = this.controller.convertToDtos(jittles);
    } 

    
    /*
     * Testing strategy for 
     * JittlesFacadeRest#save(final Principal jitterPrincipal, 
     *                        @RequestBody final Collection<JittleDto> jittles):
     * 
     * Partitions:
     *    jitterPrincipal: = correct, wrong;
     *    jittles = null;
     *    jittles.size: = 0,  = 1, > 1;
     *    jitles.jittle: = null, any rep. invariant is broken, valid JitlleDto;
     *    jittles: all unique, has duplicates, any given jittle present in repository;
     *    
     *    # returns ResponseEntity with according http status:
     *          BAD_REQUEST, NOT_FOUND, CREATED.
     *          Saves exact given list.
     */
    
    @Test
    @Transactional
    public void testSave_WrongPrincipalNullList_BadRequest() throws Exception {        
        mockMvc.perform(post(JITTLES).principal(wrongPrincipal)
                .content(this.json(null))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testSave_WrongPrincipalEmptyList_NotFound() throws Exception {        
        mockMvc.perform(post(JITTLES).principal(wrongPrincipal)
                .content(this.json(new ArrayList<JittleDto>()))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    } 
    
    @Test
    @Transactional
    public void testSave_CorrectPrincipalEmptyList_Created() throws Exception {        
        mockMvc.perform(post(JITTLES).principal(correctPrincipal)
                .content(this.json(new ArrayList<JittleDto>()))
                .contentType(contentType))
                .andExpect(status().isCreated());
        
        assertEquals(jittles, this.jittleRepository.findAll());
    } 
    
    @Test
    @Transactional
    public void testSave_CorrectPrincipalOneItemListValidItem_Created() throws Exception {   
        List<JittleDto> onePresentItemList = jittlesDto.subList(0, 2);
        onePresentItemList.get(0).setCountry("BRAZIL");
        mockMvc.perform(post(JITTLES).principal(correctPrincipal)
                .content(this.json(onePresentItemList))
                .contentType(contentType))
                .andExpect(status().isCreated());
        
        List<Jittle> updatedJittles = this.jittleRepository.findAll();        
        assertEquals(jittles.size(), updatedJittles.size());
        assertEquals(updatedJittles.get(0).getCountry(), onePresentItemList.get(0).getCountry());
    } 
    
    @Test
    @Transactional
    public void testSave_TwoItemsListNullItemValidItem_BadRequest() throws Exception {   
        List<JittleDto> twoItemList = new ArrayList<JittleDto>();
        twoItemList.add(jittlesDto.get(0));
        twoItemList.add(null);
        
        mockMvc.perform(post(JITTLES).principal(correctPrincipal)
                .content(this.json(twoItemList))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @Transactional
    public void testSave_BrokenItem_BadRequest() throws Exception {          
        mockMvc.perform(post(JITTLES).principal(correctPrincipal)
                .content(this.json(Arrays.asList(new JittleDto())))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @Transactional
    public void testSave_TwoValidItemsInique_Created() throws Exception {  
        List<JittleDto> twoNewItemList = jittlesDto.subList(0, 3);
        twoNewItemList.get(0).setId(2000L);
        twoNewItemList.get(1).setId(2001L);
        mockMvc.perform(post(JITTLES).principal(correctPrincipal)
                .content(this.json(twoNewItemList))
                .contentType(contentType))
                .andExpect(status().isCreated());
        
        List<Jittle> updatedJittles = this.jittleRepository.findAll();  
        assertEquals(updatedJittles.size() - jittles.size(), 2);
        assertEquals(updatedJittles.get(updatedJittles.size() - 2).getId().longValue(), 2000L);    
        assertEquals(updatedJittles.get(updatedJittles.size() - 1).getId().longValue(), 2001L); 
    }
    
    @Test
    @Transactional
    public void testSave_TwoValidItemsDuplcate_BadRequest() throws Exception {          
        mockMvc.perform(post(JITTLES).principal(correctPrincipal)
                .content(this.json(Arrays.asList(jittlesDto.subList(0, 3))))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    
    /*
     * Testing strategy for 
     *  public List<JittleDto> pull(final Principal jitterPrincipal) :
     * 
     * Partitions:
     *    jitterPrincipal: = correct, wrong;
     *    
     *    # returns ResponseEntity with according http status: 
     *          BAD_REQUEST, NOT_FOUND, FOUND;
     *          containing list of JittleDto objects.
     *          list.size = 0, = 1, > 1;
     *          list.items aren't present in repository anymore.
     */
    
    @Test
    @Transactional
    public void testPull_WrongPrincipal_NotFound() throws Exception {        
        mockMvc.perform(get(JITTLES).principal(wrongPrincipal)
                .contentType(contentType))
                .andExpect(status().isNotFound());
    } 
    
    @Test
    @Transactional
    public void testPull_CorrectPrincipal_FoundEmptyList() throws Exception {        
        mockMvc.perform(get(JITTLES).principal(correctPrincipal)
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(0)));
        
        List<Jittle> updatedJittles = this.jittleRepository.findAll();        
        assertEquals(jittles, updatedJittles);
    }
    
    @Test
    @Transactional
    public void testPull_FoundOneItemList() throws Exception {  
        Principal hasOneJittlePrincipal = new Principal() {
            @Override
            public String getName() {
                return "mwalls";
            }
        };
        
        mockMvc.perform(get(JITTLES).principal(hasOneJittlePrincipal)
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].jitter.username", is(hasOneJittlePrincipal.getName())));
        
        List<Jittle> updatedJittles = this.jittleRepository.findAll();        
        assertFalse(updatedJittles.stream()
                        .anyMatch(jittle -> jittle.getJitter()
                                        .getUsername()
                                        .equals(hasOneJittlePrincipal.getName())));
    }
    
    @Test
    @Transactional
    public void testPull_FoundThreeItemList() throws Exception {  
        Principal hasThreeJittlesPrincipal = new Principal() {
            @Override
            public String getName() {
                return "habuma";
            }
        };
        
        mockMvc.perform(get(JITTLES).principal(hasThreeJittlesPrincipal)
                .contentType(contentType))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].jitter.username", is(hasThreeJittlesPrincipal.getName())))
                .andExpect(jsonPath("$[1].jitter.username", is(hasThreeJittlesPrincipal.getName())))
                .andExpect(jsonPath("$[2].jitter.username", is(hasThreeJittlesPrincipal.getName())));
        
        List<Jittle> updatedJittles = this.jittleRepository.findAll();        
        assertFalse(updatedJittles.stream()
                        .anyMatch(jittle -> jittle.getJitter()
                                        .getUsername()
                                        .equals(hasThreeJittlesPrincipal.getName())));
    }
    

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
