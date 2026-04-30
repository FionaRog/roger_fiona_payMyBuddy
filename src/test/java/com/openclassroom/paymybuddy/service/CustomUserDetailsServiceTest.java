package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("Should load user details when user exists")
    void shouldLoadUserDetailsWhenUserExists() {

        com.openclassroom.paymybuddy.model.User user = new com.openclassroom.paymybuddy.model.User();
        user.setEmail("test@mail.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("test@mail.com");

        assertEquals("test@mail.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void shouldThrowExceptionWhenUserIsNotFound() {

        when(userRepository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("unknown@mail.com")
        );
    }
}
