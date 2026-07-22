package com.project.login.services;

import com.project.login.dtos.UserResponseDto;
import com.project.login.enums.RoleEnum;
import com.project.login.models.RoleModel;
import com.project.login.models.UserModel;
import com.project.login.repositories.RoleRepository;
import com.project.login.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserModel userModel;
    private RoleModel roleModel;

    @BeforeEach
    void setUp() {
        roleModel = new RoleModel();
        roleModel.setId(UUID.randomUUID());
        roleModel.setName(RoleEnum.ROLE_USER);

        userModel = new UserModel();
        userModel.setId(UUID.randomUUID());
        userModel.setUsername("test");
        userModel.setEmail("test@gmail.com");
        userModel.setPassword("test");
        userModel.setRoles(Set.of(roleModel));
    }

    @Test
    @DisplayName("Must convert UserModel to UserResponseDto successfully")
    void convertToDtoSuccess() {
        when(userRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));

        UserResponseDto response = userService.getUserById(userModel.getId());

        assertNotNull(response);
        assertEquals(userModel.getId(), response.getId());
        assertEquals("test", response.getUsername());
        assertEquals("test@gmail.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_USER"));
        assertEquals(1, response.getRoles().size());
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void getUserByIdNotFound() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserById(randomId);
        });

        assertEquals("Error: User not found with ID: " + randomId, exception.getMessage());
    }

    @Test
    @DisplayName("Must register a new user successfully")
    void registerUserSuccess() {
        com.project.login.dtos.UserRequestDto requestDto = new com.project.login.dtos.UserRequestDto();
        requestDto.setUsername("newuser");
        requestDto.setEmail("new@email.com");
        requestDto.setPassword("plain_password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(roleRepository.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(roleModel));
        when(passwordEncoder.encode("plain_password")).thenReturn("hashed_password");

        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel userToSave = invocation.getArgument(0);
            userToSave.setId(UUID.randomUUID());
            return userToSave;
        });

        UserResponseDto response = userService.registerUser(requestDto);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("newuser", response.getUsername());
        assertEquals("new@email.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_USER"));

        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists during registration")
    void registerUserThrowsExceptionWhenUsernameExists() {
        com.project.login.dtos.UserRequestDto requestDto = new com.project.login.dtos.UserRequestDto();
        requestDto.setUsername("existinguser");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(requestDto);
        });

        assertEquals("Error: Username is already in use!", exception.getMessage());
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists during registration")
    void registerUserThrowsExceptionWhenEmailExists() {
        com.project.login.dtos.UserRequestDto requestDto = new com.project.login.dtos.UserRequestDto();
        requestDto.setUsername("newuser");
        requestDto.setEmail("existing@email.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(requestDto);
        });

        assertEquals("Error: Email is already in use!", exception.getMessage());
        verify(userRepository, never()).save(any(UserModel.class));
    }
}
