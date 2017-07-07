package increment.services;

import java.math.BigInteger;

/**
 * Representation of service directly providing incrementing operation.
 * 
 * @author Grehov
 *
 */
public interface IncrementService {   
    
    /**
     * Increments given value as it defined at
     * <a href="https://en.wikipedia.org/wiki/Increment">increment</a> and
     * <a href="https://en.wikipedia.org/wiki/Increment_and_decrement_operators">
     * Increment and decrement operators</a>.
     * 
     * @param incremented value to be incremented.
     * @return incremented value.
     * 
     */
    BigInteger increment( int incremented);
}
