package com.project.login.services;

import com.project.login.dtos.LoginRequestDto;
import com.project.login.dtos.LoginResponseDto;
import com.project.login.models.UserModel;
import com.project.login.repositories.UserRepository;
import com.project.login.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

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
    @DisplayName("Must authenticate successfully and return JWT token")
    void loginSuccess() {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("test");
        requestDto.setPassword("test");

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(userModel));
        when(passwordEncoder.matches("test", "test")).thenReturn(true);
        when(tokenService.generateToken(userModel)).thenReturn("jwt-valid-token");

        LoginResponseDto response = authService.login(requestDto);

        assertNotNull(response);
        assertEquals("jwt-valid-token", response.getToken());
    }

    @Test
    @DisplayName("Should throw exception when username is not found")
    void loginUserNotFound() {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("wrong");
        requestDto.setPassword("password123");

        when(userRepository.findByUsername("wrong")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            authService.login(requestDto);
        });

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when password does not match")
    void loginInvalidPassword() {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUsername("user");
        requestDto.setPassword("wrong_password");

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(userModel));
        when(passwordEncoder.matches("wrong_password", "password123")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            authService.login(requestDto);
        });

        verify(tokenService, never()).generateToken(any(UserModel.class));
    }
}
