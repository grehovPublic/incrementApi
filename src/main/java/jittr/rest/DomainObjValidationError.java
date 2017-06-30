package jittr.rest;

import org.springframework.util.Assert;
import org.springframework.validation.FieldError;

import static jittr.rest.SharedConstants.VALUE_NOT_NULL;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Representation of a list for domain objects' rep. invariants validations errors.
 * 
 * @author Grehov
 *
 */
public class DomainObjValidationError extends Exception {
 
    private static final long serialVersionUID = 1L;
    
    /**
     * A list for domain objects' rep. invariants validations errors.
     */
    @NotNull
    private List<FieldError> fieldErrors;
 
    /**
     * Make {@link DomainObjValidationError} object.
     * 
     * @param fieldErrors list of validations errors.
     * 
     * @throws IllegalArgumentException if argument is {@literal null}.
     */
    public DomainObjValidationError(List<FieldError> fieldErrors) {
        Assert.notNull(fieldErrors,  VALUE_NOT_NULL);
        this.fieldErrors = fieldErrors;
    }
 
    /**
     * Getter.
     * 
     * @return returns list of validations errors.
     */
    public List<FieldError> getFieldErrors() {
        return this.fieldErrors; 
    }
}