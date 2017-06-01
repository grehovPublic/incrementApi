package jittr.security;

//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
//import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import jittr.db.JitterRepository;
//
///**
// * Configuration of methods' level security.
// * 
// * @author Grehov
// *
// */
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled=true)
//public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
//    
//    @Autowired
//    JitterRepository jitterRepository;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {      
//        auth.userDetailsService(userDetailsService())
//        .passwordEncoder(new BCryptPasswordEncoder());
//  }
//    
//    @Bean
//    UserDetailsService userDetailsService() {
//        return (username) -> jitterRepository
//                .findByUsername(username)
//                .map(u -> new User(u.getUsername(), u.getPassword(), true, true, true, true,
//                        AuthorityUtils.createAuthorityList(u.getRole().toString())))
//                .orElseThrow(
//                        () -> new UsernameNotFoundException("could not find the user '"
//                                + username + "'"));
//    }
//  
//  @Override
//  protected MethodSecurityExpressionHandler createExpressionHandler() {
//      DefaultMethodSecurityExpressionHandler expressionHandler =
//      new DefaultMethodSecurityExpressionHandler();
//      expressionHandler.setPermissionEvaluator(
//      new JittlePermissionEvaluator());
//      return expressionHandler;
//  }
//}
