package jittr.domain;

/**
 * Domain constants.
 * @author Grehov
 *
 */
public interface SharedConstants {

    String ENHANCED_SEQ = "enhanced-sequence";
    
    int USERNAME_MIN_LENGTH = 1;
    int USERNAME_MAX_LENGTH = 32;
    String USERNAME_PATTERN = "^[A-Za-z0-9_-]{1,32}$";
    String VALIDATE_NOTE_USERNAME_SIZE = "{username.size}";
    String VALIDATE_NOTE_USERNAME_PATTERN = "{username.chars}";
    
    String COLNAME_USERNAME = "username";  
    String COLNAME_EMAIL = "email"; 
    
    String ID_FIELD = "id";
}
