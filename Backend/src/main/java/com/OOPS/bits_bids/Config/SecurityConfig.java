package com.OOPS.bits_bids.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserJPAConfig userJPAConfig;

    @Bean
    @Order(1)
    SecurityFilterChain h2ConsoleFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher("/h2-console/**"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(withDefaults()).disable())
                .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain createUserSecurityChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .securityMatcher(AntPathRequestMatcher.antMatcher("/user/sign-up/**"))
                .authorizeHttpRequests(auth -> auth.requestMatchers(AntPathRequestMatcher.antMatcher("/user/sign-up/**")).permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/user/sign-up/**")))
                .headers(headers -> headers.frameOptions(withDefaults()).disable())
                .build();
    }

    // This checks for the permissions of certain requests
    @Bean
    @Order(3)
    SecurityFilterChain apiSecurityFilterChain (HttpSecurity httpSecurity) throws Exception{

        return httpSecurity
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .userDetailsService(userJPAConfig)
                .csrf(AbstractHttpConfigurer::disable) // csrf disable temporarily for testing purposes. withDefaults() static method is to be used.
                .formLogin(withDefaults())
                .httpBasic(withDefaults())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
