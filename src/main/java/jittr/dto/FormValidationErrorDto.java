package jittr.dto;

import static jittr.rest.SharedConstants.VALUE_NOT_NULL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import jittr.rest.DomainObjValidationError;


/**
 * DTO class for {@link DomainObjValidationError}
 * 
 * @author Grehov
 *
 */
public class FormValidationErrorDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotNull
    private List<FieldValidationErrorDto> fieldErrors = new ArrayList<FieldValidationErrorDto>();

    /**
     * Makes an empty {@link FormValidationErrorDto} object.
     */
    public FormValidationErrorDto() {
 
    }
    
    /**
     * {@link DomainObjValidationError#getFieldErrors()}
     */
    public List<FieldValidationErrorDto> getFieldErrors() {
        return fieldErrors;
    }
 
    /**
     * Adds new {@link FieldValidationErrorDto} to the list of errors.
     * @param path
     * @param message {@link }
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     */
    public void addFieldError(String path, String message) {
        Assert.notNull(path,  VALUE_NOT_NULL);
        Assert.notNull(message,  VALUE_NOT_NULL);
        
        FieldValidationErrorDto fieldError = new FieldValidationErrorDto(path, message);
        fieldErrors.add(fieldError);
    }
}
