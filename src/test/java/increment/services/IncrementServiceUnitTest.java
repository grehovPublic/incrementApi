package increment.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import increment.services.IncrementService;


@RunWith(MockitoJUnitRunner.class)
public class IncrementServiceUnitTest  {   

    private static IncrementService incrementService ;   
    
    public IncrementServiceUnitTest() {
        incrementService = new IncrementServiceImpl();
    }
    
    /*
     * Testing strategy for:
     *   BigInteger increment(int incremented);
     *   
     * Partitions:
     *    incremented: = Integer.Min, < 0, = 0, > 0, Integer.Max;
     *    
     *    # returns ResponseEntity with according http status: OK;
     *          response.content: = Integer.Min + 1, < 0, = 0, Integer.Max + 1;
     *          throws IllegalArgumentException
     */
    
    
    @Test
    public void testIncrement_InegerMin_IntegerMinPluseOne() {         
        final BigInteger incremented = incrementService.increment(Integer.MIN_VALUE);
        
        assertThat(incremented).isEqualTo(BigInteger.valueOf(Integer.MIN_VALUE + 1));
    }
    
    final static int NEGATIVE_VAL = -1;
    
    @Test
    public void testIncrement_NegativeVal_NegativeValPlusOne() {                
        final BigInteger incremented = incrementService.increment(NEGATIVE_VAL);
        
        assertThat(incremented).isEqualTo(BigInteger.valueOf(NEGATIVE_VAL + 1));
    }
    
    @Test
    public void testIncrement_ZeroVal_One() throws Exception {         
        final BigInteger incremented = incrementService.increment(0);
        assertThat(incremented).isEqualTo(BigInteger.valueOf(1));
    }
    
    final static int POSITIVE_VAL = 5;
    
    @Test
    public void testIncrement_PositiveVal_PositiveValPlusOne() {                
        final BigInteger incremented = incrementService.increment(POSITIVE_VAL);
        
        assertThat(incremented).isEqualTo(BigInteger.valueOf(POSITIVE_VAL + 1));
    }
    
    
    @Test
    public void testIncrement_InegerMax_IntegerMaxPluseOne() throws Exception {         
        final BigInteger incremented = incrementService.increment(Integer.MAX_VALUE);
        
        assertThat(incremented).isEqualTo(BigInteger.valueOf((long)Integer.MAX_VALUE + 1L));
    }
}
