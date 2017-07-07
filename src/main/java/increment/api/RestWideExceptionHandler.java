package increment.api;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import increment.services.IncrementServiceImpl;

/**
 * Global REST controller exceptions handlers.
 * 
 * @author Grehov
 *  
 */
@ControllerAdvice
@RestController
public class RestWideExceptionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestWideExceptionHandler.class);
    
    /**
     * Global controllers exceptions handler for 'IllegalArgument' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return response with details describing.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> illegalControllerArgumentHandler(IllegalArgumentException e) {
        LOG.error("No content in request body found. Details: {}", e);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
    
    /**
     * Global controllers exceptions handler for 'UsernameNotFoundException' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return response with details describing.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> UsernameNotFoundExceptionHandler(UsernameNotFoundException e) {
        LOG.error("User not found. Details: {}", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }
}

