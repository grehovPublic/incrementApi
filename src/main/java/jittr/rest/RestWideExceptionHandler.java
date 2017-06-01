package jittr.rest;

import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
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
     * @return error with details describing.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error illegalControllerArgumentHandler(IllegalArgumentException e) {
        return new Error("Illegal Argument: " + e.getMessage());
    }
    
 
    /**
     * Global controllers exceptions handler for 'NotValidDomainEntity' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return error with details describing.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error validateControllerArgumentHandler(MethodArgumentNotValidException e) {
        return new Error("Argument's validation error: " + e.getMessage());
    }
    
    /**
     * Global controllers exceptions handler for {@link DomainObjValidationError} case.
     * Transforms {@link DomainObjValidationError} exception to {@link FormValidationErrorDto}.
     * Sets according 'http' status.
     * 
     * @param validationError handled {@link DomainObjValidationError} exception.
     * @return error with details describing.
     */
    @ExceptionHandler(DomainObjValidationError.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public FormValidationErrorDto handleFormValidationError(DomainObjValidationError validationError) {        
        FormValidationErrorDto dto = new FormValidationErrorDto();

        validationError.getFieldErrors()
        .forEach(fieldError -> {
            Stream.of(fieldError.getCodes())
                .forEach(fieldErrorCode -> 
                    dto.addFieldError(fieldError.getField(), fieldErrorCode));
        });     

        return dto;
    }
    
    /**
     * Global controllers exceptions handler for 'EntityNotFound' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return error with details describing.
     */
    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)   
    public Error hanleEntityNotFound(EmptyResultDataAccessException e) {
      return new Error("Entity(s) not found: " + e.getMessage());
    }
    
    /**
     * Global controllers exceptions handler for 'JiiterNotFound' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return error with details describing.
     */
    @ExceptionHandler(JitterNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)   
    public Error hanleJitterNotFound(JitterNotFoundException e) {
      return new Error("User not found: " + e.getMessage());
    }
    
    /**
     * Global controllers exceptions handler for 'DuplicateEntity' case.
     * Sets according to the case 'http' status.
     * 
     * @param e handled exception.
     * @return error with details describing.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)   
    public Error handleDuplicateEntity(DuplicateKeyException e) {
      return new Error("Duplicate entity: " + e.getMessage());
    }
}

