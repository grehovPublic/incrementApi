package jittr.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.junit.Assert;

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
        Assert.assertNotNull(path);
        Assert.assertNotNull(message);
        
        FieldValidationErrorDto fieldError = new FieldValidationErrorDto(path, message);
        fieldErrors.add(fieldError);
    }
}
