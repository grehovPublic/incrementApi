package increment.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.springframework.web.context.WebApplicationContext;

import increment.Increment;
import static increment.api.IncrementController.*;



@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Increment.class},
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IncrementControllerIntegrationTest  {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    public IncrementControllerIntegrationTest() {
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
    
    /*
     * Testing strategy for:
     *   public ResponseEntity<BigInteger> increment(@RequestBody 
     *              final Optional<Integer> optIncremented) throws IllegalArgumentException 
     * 
     * Partitions:
     *    optIncremented.content: = null, = Integer.Min, < 0, = 0, Integer.Max;
     *    
     *    # returns ResponseEntity with according http status: OK;
     *          response.content: = Integer.Min + 1, < 0, = 0, Integer.Max, Integer.Max + 1;
     *          throws IllegalArgumentException
     */
    
    @Test
    public void testIncrement_NullContent_Exception() throws Exception {         
        mockMvc.perform(patch(INCREMENT)
                .content(this.json(null))
                .contentType(contentType))
                .andExpect(status().isNoContent());
    }
    
    @Test
    public void testIncrement_InegerMin_IntegerMinPluseOne() throws Exception {         
        mockMvc.perform(patch(INCREMENT)
                .content(this.json(Integer.MIN_VALUE))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Integer.MIN_VALUE + 1)));
    }
    
    final static int NEGATIVE_VAL = -1;
    
    @Test
    public void testIncrement_NegativeVal_NegativeValPlusOne() throws Exception {         
        mockMvc.perform(patch(INCREMENT)
                .content(this.json(NEGATIVE_VAL))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(NEGATIVE_VAL + 1)));
    }
    
    @Test
    public void testIncrement_ZeroVal_One() throws Exception {         
        mockMvc.perform(patch(INCREMENT)
                .content(this.json(0))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
    }
    
    @Test
    public void testIncrement_InegerMax_IntegerMaxPluseOne() throws Exception {         
        mockMvc.perform(patch(INCREMENT)
                .content(this.json(Integer.MAX_VALUE))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is((long)Integer.MAX_VALUE + 1L)));
    }
    
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
