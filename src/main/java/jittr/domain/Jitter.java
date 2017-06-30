package jittr.domain;

import static jittr.domain.SharedConstants.*;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jittr.security.BCryptPasswordDeserializer;


/**
 * This datatype represents a user of the application.
 * 
 * @author Grehov
 *
 */
@Entity
public class Jitter implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    protected static final String ID_JTR_GENERATOR = "ID_JITTERS_GENERATOR";
    protected static final String JTR_SEQ = "jitters_seq";
    
    protected static final int PASS_BCRYPT_LENGTH = 60;
    
    protected static final String COLNAME_PASSWORD = "password";  
    protected static final String COLNAME_FULLNAME = "fullname";     
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_FULLNAME = "fullName";
    public static final String FIELD_ROLL = "roll";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = COLNAME_USERNAME, nullable = false, unique = true, length = USERNAME_MAX_LENGTH)
    private String username;


    @JsonDeserialize(using = BCryptPasswordDeserializer.class )
    @Column(name = COLNAME_PASSWORD, nullable = false, length = PASS_BCRYPT_LENGTH)
    private String password;
    
    /**
     *  {@link Jitter}'s roles.
     */
    public static enum Role {ROLE_JITTER, ROLE_ADMIN};

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = COLNAME_FULLNAME)
    private String fullName;

    @Column(name = COLNAME_EMAIL)
    private String email;
   
    /**
     * Default constructor for JPA.
     */
    private Jitter() {}

    /**
     * Make a {@link Jitter} with auto-generated unique id.
     * 
     * @param id
     *            unique identifier for the user.
     * @param username
     *         jitter's username. 1 - 32 characters, is case-insensitive.
     *         Required to be a Twitter username as defined by getUsername().
     * @param password
     *         jitter's password. 8 - 16 any characters, is case-sensitive.
     * @param fullName
     *         jitter's full name. 1 - 32 characters.
     *         Required to be valid name as defined by etFullName().
     * @param email
     *         Required to be a valid email address.
     */
    public Jitter(Long id, String username, String password, String fullName, 
            String email, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    /**
     * @return unique identifier of this {@link Jitter}. Is auto-generated.
     */
    public Long getId() { return this.id; }
    
    /**
     * Sets {@link Jitter}'s id.
     * 
     * @param id see {@link Jitter#getId()}.
     */
    public void setId(final Long id) { this.id = id; }
    
    /**
     * @return {@link Jitter}'s username, based on Tweeter username rules. Must be unique.
     *         A Twitter username is a nonempty sequence of letters (A-Z or
     *         a-z), digits, underscore ("_"), or hyphen ("-").
     *         Twitter usernames are case-insensitive, so "jbieber" and "JBieBer"
     *         are equivalent.
     */
    public String getUsername() { return this.username; }
    
    /**
     * Sets {@link Jitter}'s username.
     * 
     * @param username see {@link Jitter#getUsername()}
     */
    public void setUsername(final String username) { this.username = username; }

    /**
     * @return {@link Jitter}'s password. 5 - 60 any characters, is case-sensitive.
     */
    public String getPassword() { return this.password; }
    
    /**
     * Sets {@link Jitter}'s password.
     * 
     * @param password see {@link Jitter#getPassword()}
     */
    public void setPassword(final String password) { this.password = password; }

    /**
     * @return {@link Jitter}'s full name, 5 - 32 characters.
     *         The sequence of letters (A-Z or a-z), digits, gaps (" "), or hyphen ("-").
     *         Is case-insensitive, so "jbieber One" and "JBieBer one"
     *         are equivalent.
     */
    public String getFullName() { return this.fullName; }
    
    /**
     * Sets {@link Jitter}'s full name.
     * 
     * @param fullName see {@link Jitter#getFullName()}
     */
    public void setFullName(final String fullName) { this.fullName = fullName; }

    /**
     * @return {@link Jitter}'s email. A valid email address.
     */
    public String getEmail() { return this.email; }
    
    /**
     * Sets {@link Jitter}'s email.
     * @param email see {@link Jitter#getEmail()}.
     */
    public void setEmail(final String email) { this.email = email; }
 
    /**
     * @return {@link Jitter}'s (owner of this {@link Jittle}) {@link Role}.
     */
    public Role getRole() { return this.role; }
    
    /**
     * Sets {@link Jitter}'s {@link Role}.
     * 
     * @param role see {@link Jitter#getRole()}.
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
