package com.project.login.services;

import com.project.login.dtos.LoginRequestDto;
import com.project.login.dtos.LoginResponseDto;
import com.project.login.models.UserModel;
import com.project.login.repositories.UserRepository;
import com.project.login.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        UserModel user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Invalid username or password."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Error: Invalid username or password.");
        }

        String token = tokenService.generateToken(user);

        long expiresInHours = Duration.ofHours(2).toHours();

        return new LoginResponseDto(token, "Bearer", expiresInHours);
    }
}
