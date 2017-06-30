package jittr.rest;

import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jittr.dto.FormValidationErrorDto;

/**
 * Global REST controllers exceptions handlers.
 * 
 * @author Grehov
 *  
 */
@ControllerAdvice
@RestController
public class RestWideExceptionHandler {
    
    @Resource
    private MessageSource messageSource;
    
    /**
     * Global controllers exceptions handler for 'IllegalArgument' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return response with details describing.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> illegalControllerArgumentHandler(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Illegal Argument: " + e.getMessage());
    }
    
 
    /**
     * Global controllers exceptions handler for 'NotValidDomainEntity' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return response with details describing.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> validateControllerArgumentHandler(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Argument's validation error: " + e.getMessage());
    }
    
    /**
     * Global controllers exceptions handler for {@link DomainObjValidationError} case.
     * Transforms {@link DomainObjValidationError} exception to {@link FormValidationErrorDto}.
     * Sets according 'http' status.
     * 
     * @param validationError handled {@link DomainObjValidationError} exception.
     * @return response with details describing.
     */
    @ExceptionHandler(DomainObjValidationError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleFormValidationError(DomainObjValidationError validationError) {        
        FormValidationErrorDto dto = new FormValidationErrorDto();

        validationError.getFieldErrors()
        .forEach(fieldError -> {
            Stream.of(fieldError.getCodes())
                .forEach(fieldErrorCode -> 
                    dto.addFieldError(fieldError.getField(), fieldErrorCode));
        });     
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Argument's validation error: " + dto);
    }
    
    /**
     * Global controllers exceptions handler for 'EntityNotFound' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return response with details describing.
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)   
    public ResponseEntity<?> hanleEntityNotFound(EmptyResultDataAccessException e) {
      return ResponseEntity
              .status(HttpStatus.NOT_FOUND)
              .body("Entity(s) not found: " + e.getMessage());
    }
    
    /**
     * Global controllers exceptions handler for 'EntityNotFoundException' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return response with details describing.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)   
    public ResponseEntity<?> hanleJitterNotFound(EntityNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found: " + e.getMessage());
    }
    
    /**
     * Global controllers exceptions handler for 'JiiterNotFound' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return response with details describing.
     */
    @ExceptionHandler(JitterNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)   
    public ResponseEntity<?> hanleJitterNotFound(JitterNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found: " + e.getMessage());
    }
    
    /**
     * Global controllers exceptions handler for 'DuplicateEntity' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return response with details describing.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   
    public ResponseEntity<?> handleDuplicateEntity(DuplicateKeyException e) {
      return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body("Duplicate entity: " + e.getMessage());
    }
}

