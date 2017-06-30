package jittr.dto;

import static jittr.domain.SharedConstants.*;
import static jittr.domain.Jitter.*;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jittr.domain.Jitter;
import jittr.domain.Jitter.Role;
import jittr.security.BCryptPasswordDeserializer;


/**
 * The DTO for {@link Jitter}. 
 */
public class JitterDto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected static final int FULLNAME_MIN_LENGTH = 5;
    protected static final int FULLNAME_MAX_LENGTH = 32;
    protected static final String FULLNAME_PATTERN = "^[A-Za-z0-9-\\s]{5,32}$";
    protected static final String VALIDATE_NOTE_FULLNAME_SIZE = "{fullname.size}";
    protected static final String VALIDATE_NOTE_FULLNAME_PATTERN = "{fullname.chars}";
    
    protected static final int PASS_MIN_LENGTH = 5;
    protected static final int PASS_MAX_LENGTH = 60;
    protected static final String VALIDATE_NOTE_PASS_SIZE = "{pass.size}";
    protected static final String EXLUDED_FIELD = "email";

    @NotNull
    private Long id;

    @NotNull
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, 
          message = VALIDATE_NOTE_USERNAME_SIZE)
    @Pattern(regexp = USERNAME_PATTERN, message = VALIDATE_NOTE_USERNAME_PATTERN)
    private String username;

    @NotNull
    @Size(min = PASS_MIN_LENGTH, max = PASS_MAX_LENGTH, message = VALIDATE_NOTE_PASS_SIZE)
    @JsonDeserialize(using = BCryptPasswordDeserializer.class )
    private String password;

    @NotNull
    private Role role;

    @NotNull
    @Size(min = FULLNAME_MIN_LENGTH, max = FULLNAME_MAX_LENGTH, 
          message = VALIDATE_NOTE_FULLNAME_SIZE)
    @Pattern(regexp = FULLNAME_PATTERN, message = VALIDATE_NOTE_FULLNAME_PATTERN)
    private String fullName;

    @Email
    private String email;
    
    /**
     * {@link Jitter#Jitter()}
     */
    public JitterDto() {
    }   

    /**
     * {@link Jitter#Jitter(Long, String, String, String, String, jittr.domain.Jitter.Role)}
     */
    public JitterDto(final Long id, final String username, final String password, final String fullName, 
            final String email, final jittr.domain.Jitter.Role roleJitter) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = roleJitter;
    }
    
    /**
     * Copy constructor.
     */
    public JitterDto(final JitterDto jitter) {
        this(jitter.getId(), jitter.getUsername(), jitter.getPassword(), jitter.getFullName(), 
                jitter.getEmail(), jitter.getRole());
    }

    /**
     * {@link Jitter#getId()}
     */
    public Long getId() { return this.id; }
   
    /**
     * {@link Jitter#setId(Long)}
     */
    public void setId(final Long id) { this.id = id; }
    
    /**
     * {@link Jitter#getUsername()}
     */
    public String getUsername() { return this.username; }
    
    /**
     * {@link Jitter#setUsername(String)}
     */
    public void setUsername(final String username) { this.username = username; }
    
    /**
     * @return {@link Jitter#getPassword()}
     */
    public String getPassword() { return this.password; }

    /**
     * {@link Jitter#setPassword(String)s}
     */
    public void setPassword(final String password) { this.password = password; }

    /**
     * {@link Jitter#getFullName()}
     */
    public String getFullName() { return this.fullName; }
    
    /**
     * {@link Jitter#setFullName(String)}
     */
    public void setFullName(final String fullName) { this.fullName = fullName; }

    /**
     * {@link Jitter#getEmail()}
     */
    public String getEmail() { return this.email; }
    
    /**
     * {@link Jitter#setEmail(String)}
     */
    public void setEmail(final String email) { this.email = email; }
 
    /**
     * {@link Jitter#getRole()}
     */
    public Role getRole() { return this.role; }

    /**
     * {@link Jitter#setRole(jittr.domain.Jitter.Role)}
     */
    public void setRole(final Role role) { this.role = role; }
    
    @Override
    public boolean equals(Object that) {
        boolean result = EqualsBuilder.reflectionEquals(this, that, ID_FIELD, FIELD_FULLNAME, 
                FIELD_EMAIL, FIELD_ROLL);
        return result;
    }
    
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, ID_FIELD, FIELD_FULLNAME, 
                FIELD_EMAIL, FIELD_ROLL);
    }
}
