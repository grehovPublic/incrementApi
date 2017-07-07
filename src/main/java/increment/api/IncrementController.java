package increment.api;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import increment.services.IncrementService;

/**
 * Implementation of RESTful endpoint, which increments given value.
 * See {@link IncrementService#increment(int)}.
 * 
 * @author Grehov
 *
 */

@RestController
public class IncrementController {
    
    final static String INCREMENT = "/api/increment";
    
    protected final IncrementService incrementService;  

    /**
     * Injection constructor.
     * 
     * @param incrementService service performing incrementing. 
     */
    @Autowired
    protected IncrementController(final IncrementService incrementService) {
        this.incrementService = incrementService;
    }
    
    /**
     * Endpoint performing incrementing of given value. 
     * See {@link IncrementService#increment(int)}.
     * 
     * @param optIncremented {@link Optional} with value to be incremented.
     * 
     * @return {@link ResponseEntity} with incremented value and OK status.
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PATCH, value = INCREMENT, 
                    consumes = MediaType.APPLICATION_JSON_VALUE, 
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigInteger> increment(@RequestBody 
            final Optional<Integer> optIncremented) throws IllegalArgumentException {
        BigInteger incremented = this.incrementService.increment(optIncremented
                .orElseThrow(() -> new IllegalArgumentException()));
        
        return new ResponseEntity<BigInteger>(incremented, HttpStatus.OK);
    }    
}
