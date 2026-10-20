package com.tahmid.support_ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // Public routes
                        .requestMatchers(
                                "/",
                                "/register",
                                "/login",
                                "/error",
                                "/css/**"
                        ).permitAll()

                        // Admin-only routes
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // Customer-only routes
                        .requestMatchers(
                                "/tickets/**",
                                "/messages"
                        )
                        .hasRole("CUSTOMER")

                        // Any logged-in user can access dashboard
                        .requestMatchers("/dashboard")
                        .authenticated()

                        // Everything else also requires authentication
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}