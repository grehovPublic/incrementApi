package jittr.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.WebApplicationContext;

import jittr.Jittr;
import jittr.db.JitterRepository;
import jittr.db.JittleRepository;
import jittr.db.jpa.JpaConfig;
import jittr.domain.Jitter;
import jittr.domain.Jittle;
import jittr.domain.Jitter.Role;
import jittr.domain.Jittle.TargetQueue;
import jittr.dto.JitterDto;
import jittr.dto.JittleDto;
import jittr.rest.JittleFacadeRest;


@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jittr.class, JpaConfig.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JittleFacadeRestIntTest  {
    
    @Autowired
    JittleFacadeRest controller;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private String userName = "bdussault";

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Jitter jitter;
    Principal correctPrincipal;
    Principal wrongPrincipal;
    private List<Jittle> jittles = new ArrayList<>();
    private List<JittleDto> jittlesDto = new ArrayList<>();


    @Autowired
    private JittleRepository jittleRepository;
    
    @Autowired
    private JitterRepository jitterRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    public JittleFacadeRestIntTest() {
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
     *    jitterPrincipal: = null, correct, wrong;
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
    public void testNullPrincipal_BadRequest() throws Exception {        
        mockMvc.perform(post("/jittles").principal(correctPrincipal)
                .content(this.json(null))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testWrongPrincipalNullList_BadRequest() throws Exception {        
        mockMvc.perform(post("/jittles").principal(correctPrincipal)
                .content(this.json(null))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    } 
    
    @Test
    @Transactional
    public void testWrongPrincipalEmptyList_NotFound() throws Exception {        
        mockMvc.perform(post("/jittles").principal(wrongPrincipal)
                .content(this.json(new ArrayList<JittleDto>()))
                .contentType(contentType))
                .andExpect(status().isNotFound());
        
        assertEquals(jittles, this.jittleRepository.findAll());
    } 
    
    @Test
    @Transactional
    public void testCorrectPrincipalOneItemListValidItem_Created() throws Exception {   
        List<JittleDto> oneItemList = Arrays.asList(jittlesDto.get(0));
        mockMvc.perform(post("/jittles").principal(correctPrincipal)
                .content(this.json(oneItemList))
                .contentType(contentType))
                .andExpect(status().isCreated());
    } 
    
    @Test
    @Transactional
    public void testTwoItemsListNullItemValidItem_BadRequest() throws Exception {   
        List<JittleDto> twoItemList = new ArrayList<JittleDto>();
        twoItemList.add(jittlesDto.get(0));
        twoItemList.add(null);
        
        mockMvc.perform(post("/jittles").principal(correctPrincipal)
                .content(this.json(twoItemList))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @Transactional
    public void testBrokenItem_BadRequest() throws Exception {          
        mockMvc.perform(post("/jittles").principal(correctPrincipal)
                .content(this.json(Arrays.asList(new JittleDto())))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @Transactional
    public void testTwoValidItemsInique_Created() throws Exception {          
        mockMvc.perform(post("/jittles").principal(correctPrincipal)
                .content(this.json(jittlesDto.subList(0, 3)))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }
    
    @Test
    @Transactional
    public void testTwoValidItemsDuplcate_BadRequest() throws Exception {          
        mockMvc.perform(post("/jittles").principal(correctPrincipal)
                .content(this.json(Arrays.asList(jittlesDto.subList(0, 3))))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    
    /*
     * Testing strategy for 
     *  public List<JittleDto> pull(final Principal jitterPrincipal,
            @PathVariable final TargetQueue tQueue) :
     * 
     * Partitions:
     *    jitterPrincipal: = null, correct, wrong;
     *    tQueue = TRAIN_RAW, TRAIN_GRADED, BUILD_MAP, VIEW_RAW, VIEW_GRADED;
     *    
     *    # returns ResponseEntity with according http status: 
     *          BAD_REQUEST, NOT_FOUND;
     *          containing list of JittleDto objects. May be empty.
     *          list.size = 0, = 1, > 1;
     */
    
    
    

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
