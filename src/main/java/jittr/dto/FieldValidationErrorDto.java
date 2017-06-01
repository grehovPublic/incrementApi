package jittr.dto;

import java.io.Serializable;

import org.junit.Assert;

/**
 * DTO class for {@link org.springframework.validation.FieldError}
 * @author Grehov
 *
 */
public final class FieldValidationErrorDto implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String path;

    private String message;
 
    /**
     * Makes a new {@link FieldValidationErrorDto} object.
     * @param path  specified at {@link FieldValidationErrorDto#getPath()}.
     * @param message specified at {@link FieldValidationErrorDto#getMessage()}.
     * 
     * @throws IllegalArgumentException if any argument is {@literal null}.
     */
    public FieldValidationErrorDto(String path, String message) {
        Assert.assertNotNull(path);
        Assert.assertNotNull(message);
        this.path = path;
        this.message = message;
    }
 
    /**
     * Getter
     * 
     * @return 
     */
    public String getPath() {
        return this.path;
    }
    
    /**
     * Getter 
     * 
     * @return error message.
     */
    public String getMessage() {
        return this.message;
    }
}
