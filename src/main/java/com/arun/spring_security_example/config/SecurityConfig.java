package com.arun.spring_security_example.config;

// This call is used for customizing Security Filter Chain.
// using spring-boot-starter-security dependency let spring boot provide various security filters

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // By using this, we are bypassing the security login page. comment @@EnableWebSecurity and @Bean to see the security login page.
    // since we have created this class, now we are handling security here only. We don't write any logic in below method
    // Since we are providing our own SecurityFilterChain configuration,
    // Spring Boot's default security configuration is overridden.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
/**
        // this line disabled the security login form (provided by spring boot)
        // Without this, we need to generate and send csrf token in the request header to call not-GET call
        httpSecurity.csrf(customizer -> customizer.disable());

        // this line enables customize authorization of http request
        httpSecurity.authorizeHttpRequests(req -> req.anyRequest().authenticated());

        // This line enables default security login form
        httpSecurity.formLogin(Customizer.withDefaults());

        // This line enables HTTP Basic Authentication.
        // When a client (e.g., Postman or a browser) accesses a secured endpoint,
        // it must send the username and password in the Authorization header.
        // Useful for REST APIs and testing.
        httpSecurity.httpBasic(Customizer.withDefaults());

        // This line configures Spring Security to use Stateless Session Management.
        // Spring Security will not create or maintain an HTTP Session.
        // Every request must contain authentication information (such as Basic Auth or a JWT token).
        // This approach is commonly used in REST APIs.
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
**/
        // Above all code can be written by using builder pattern
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    // UserDetailsService verifies the user details.
    // In the below method, we are storing user details in user1 and user2, and passing into InMemoryUserDetailsManager
    // for user verification. But we are using hard coded values here.
    // If we want to use database for user verification, then we won't use this method
//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user1 = User
//                .withDefaultPasswordEncoder()
//                .username("arun")
//                .password("kumar")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user2 = User
//                .withDefaultPasswordEncoder()
//                .username("naveen")
//                .password("kumar")
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user1, user2);
//    }

    // If we want to work with database for getting user details, then we cqn use AuthenticationProvider.
    // By default, it takes an unauthenticated object and returns an authenticated object
    // But in this method we are customising AuthenticatedProvider

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // there are many authentication providers are available, and below is one of them
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

}
