package com.project.login.services;
import com.project.login.models.UserModel;
import com.project.login.repositories.UserRepository;
import com.project.login.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserModel userModel;

    @BeforeEach
    void setUp() {
        userModel = new UserModel();
        userModel.setUsername("test");
        userModel.setEmail("test@email.com");
        userModel.setPassword("test");
        userModel.setRoles(Collections.emptySet());
    }

    @Test
    @DisplayName("Must load user by username successfully")
    void loadUserByUsernameSuccess() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(userModel));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test");

        assertNotNull(userDetails);
        assertEquals("test", userDetails.getUsername());
        assertEquals("test", userDetails.getPassword());
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void loadUserByUsernameNotFound() {
        when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("invalid");
        });
    }
}
