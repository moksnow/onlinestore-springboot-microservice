package org.moksnow.discoveryserver.config;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig/* extends WebSecurityConfigurerAdapter */ {

// configurer method:
//    authenticationManagerBuilder.inMemoryAuthentication()passwordEncoder(NoOpPasswordEncoder.getInstance())
//    .withUser(username variable).password(password var).authorities("USER");

    // configurer method2:
//    httpSecurity.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
}
