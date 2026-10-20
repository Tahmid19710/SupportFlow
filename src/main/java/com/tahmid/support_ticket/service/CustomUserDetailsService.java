package com.tahmid.support_ticket.service;

import com.tahmid.support_ticket.entity.AppUser;
import com.tahmid.support_ticket.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public CustomUserDetailsService(
            AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException(
                    "Email must not be empty."
            );
        }

        String normalizedEmail = email
                .trim()
                .toLowerCase(Locale.ROOT);

        AppUser user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Invalid email or password."
                        )
                );

        if (user.getRole() == null) {
            throw new UsernameNotFoundException(
                    "User role is not configured."
            );
        }

        if (user.getPasswordHash() == null
                || user.getPasswordHash().isBlank()) {
            throw new UsernameNotFoundException(
                    "User credentials are not configured."
            );
        }

        return User.withUsername(normalizedEmail)
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
    }
}