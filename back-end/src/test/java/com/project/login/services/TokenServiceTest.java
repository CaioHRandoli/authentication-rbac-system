package com.project.login.services;

import com.project.login.models.UserModel;
import com.project.login.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private UserModel userModel;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", "my-secret-123456789");

        userModel = new UserModel();
        userModel.setId(UUID.randomUUID());
        userModel.setUsername("testuser");
        userModel.setEmail("test@email.com");
        userModel.setRoles(Collections.emptySet());
    }

    @Test
    @DisplayName("Must generate a valid token and validate it successfully")
    void generateAndValidateTokenSuccess() {
        String token = tokenService.generateToken(userModel);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String subject = tokenService.validateToken(token);
        assertEquals("testuser", subject);
    }

    @Test
    @DisplayName("Should return empty string when validating an invalid token")
    void validateTokenInvalidReturnsEmpty() {
        String invalidToken = "invalid-token";

        String subject = tokenService.validateToken(invalidToken);

        assertTrue(subject == null || subject.isEmpty());
    }
}
