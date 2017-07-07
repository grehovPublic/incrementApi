package increment.security;

import java.util.Arrays;
import java.util.Optional;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import increment.api.RestWideExceptionHandler;

/**
 * Configuration of web level security.
 * 
 * @author Grehov
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends  WebSecurityConfigurerAdapter  {
    
    private static final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);
    
    @Value("${increment.client-username}")
    private String clientUsername ;
    
    @Value("${increment.client-password}")
    private String clientPassword ;
    
    private static final String REST_CLIENT_ROLE = "USER";
    
    private static final String API_SUBPATH = "/api/**";
    
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
 
    @Autowired
    private IncrementApiSavedRequestAwareAuthenticationSuccessHandler
      authenticationSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {   
        auth.userDetailsService(userDetailsService())
        .passwordEncoder(new BCryptPasswordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception { 
        http
            .csrf().disable()
            .exceptionHandling()
        .and()
            .httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
            .authorizeRequests()
            .antMatchers(API_SUBPATH).authenticated()//.hasRole(REST_CLIENT_ROLE)
        .and()
            .formLogin()
            .successHandler(authenticationSuccessHandler)
            .failureHandler(new SimpleUrlAuthenticationFailureHandler())
        .and()
            .logout();
    }
    
    @Bean
    protected UserDetailsService userDetailsService() {   
        return (username) -> { 
            LOG.info("Authentication attempt with username {}", username);           
            return Optional.ofNullable(userDeatailsManager()
                .loadUserByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Could not find the user '"
                                + username + "'"));
            };
    } 
    
    @Bean
    protected InMemoryUserDetailsManager userDeatailsManager() {           
        return new InMemoryUserDetailsManager();
    } 
    
    
    @PostConstruct
    protected void initialize() {    
        userDeatailsManager().createUser(
                new User(clientUsername, 
                new BCryptPasswordEncoder().encode(clientPassword),
                true, true, true, true, Arrays.asList(new SimpleGrantedAuthority(REST_CLIENT_ROLE))));
    }
}
