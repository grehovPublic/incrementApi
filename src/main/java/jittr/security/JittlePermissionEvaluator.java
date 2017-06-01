package jittr.security;

import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jittr.domain.Jittle;

/**
 * A permission evaluator class provides the logic behind hasPermission().
 * 
 * @author Grehov
 *
 */
public class JittlePermissionEvaluator implements PermissionEvaluator {
    
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_JITTER = "ROLE_JITTER";
    
    private static final GrantedAuthority ADMIN_AUTHORITY = 
            new SimpleGrantedAuthority(ROLE_ADMIN);
    private static final GrantedAuthority JITTER_AUTHORITY = 
            new SimpleGrantedAuthority(ROLE_JITTER);
    
    public boolean hasPermission(Authentication authentication, 
                    Object target, Object permission) {
                
        if (target instanceof Jittle) {
            Jittle jittle = (Jittle) target;
            String username = jittle.getJitter().getUsername();
            if ("delete".equals(permission)) {
                return isAdmin(authentication) || 
                        (username.equals(authentication.getName()) && isJitter(authentication));
            }
        }
        throw new UnsupportedOperationException(
        "hasPermission not supported for object <" + target
        + "> and permission <" + permission + ">");
    }
            
    public boolean hasPermission(Authentication authentication,
            Serializable targetId, String targetType, Object permission) {
        throw new UnsupportedOperationException();
    }
    
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(ADMIN_AUTHORITY);
    }
    
    private boolean isJitter(Authentication authentication) {
        return authentication.getAuthorities().contains(JITTER_AUTHORITY);
    }

}
