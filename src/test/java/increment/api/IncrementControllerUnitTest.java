package increment.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import increment.services.IncrementService;


@RunWith(MockitoJUnitRunner.class)
public class IncrementControllerUnitTest  {
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private IncrementService incrementServiceMock;
    
    @InjectMocks
    private IncrementController controller;    
    
    
    public IncrementControllerUnitTest() {
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
        thrown.expect(IllegalArgumentException.class);

        when(incrementServiceMock.increment(Matchers.anyInt()))
                .thenReturn(null);
        controller.increment(Optional.ofNullable(null));
    }
    
    @Test
    public void testIncrement_InegerMin_IntegerMinPluseOne() throws Exception {         
        when(incrementServiceMock.increment(Integer.MIN_VALUE))
                .thenReturn(BigInteger.valueOf(Integer.MIN_VALUE + 1));
        final BigInteger incremented = 
                controller.increment(Optional.of(Integer.MIN_VALUE)).getBody();
        assertThat(incremented).isEqualTo(BigInteger.valueOf(Integer.MIN_VALUE + 1));
    }
    
    final static int NEGATIVE_VAL = -1;
    
    @Test
    public void testIncrement_NegativeVal_NegativeValPlusOne() throws Exception {                
        when(incrementServiceMock.increment(NEGATIVE_VAL))
                .thenReturn(BigInteger.valueOf(NEGATIVE_VAL + 1));
        final BigInteger incremented = 
                controller.increment(Optional.of(NEGATIVE_VAL)).getBody();
        assertThat(incremented).isEqualTo(BigInteger.valueOf(NEGATIVE_VAL + 1));
    }
    
    @Test
    public void testIncrement_ZeroVal_One() throws Exception {         
        when(incrementServiceMock.increment(0))
                .thenReturn(BigInteger.valueOf(1));
        final BigInteger incremented = 
                controller.increment(Optional.of(0)).getBody();
        assertThat(incremented).isEqualTo(BigInteger.valueOf(1));
    }
    
    @Test
    public void testIncrement_InegerMax_IntegerMaxPluseOne() throws Exception {         
        when(incrementServiceMock.increment(Integer.MAX_VALUE))
                .thenReturn(BigInteger.valueOf((long)Integer.MAX_VALUE + 1L));
        final BigInteger incremented = 
                controller.increment(Optional.of(Integer.MAX_VALUE)).getBody();
        assertThat(incremented).isEqualTo(BigInteger.valueOf((long)Integer.MAX_VALUE + 1L));
    }
}
