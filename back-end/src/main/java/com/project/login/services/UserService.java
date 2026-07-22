package com.project.login.services;

import com.project.login.dtos.UserRequestDto;
import com.project.login.dtos.UserResponseDto;
import com.project.login.enums.RoleEnum;
import com.project.login.models.RoleModel;
import com.project.login.models.UserModel;
import com.project.login.repositories.RoleRepository;
import com.project.login.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new RuntimeException("Error: Username is already in use!");
        }

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        RoleModel defaultRole = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_USER not found. Please initialize roles first."));

        UserModel newUser = new UserModel();
        newUser.setUsername(userRequestDto.getUsername());
        newUser.setEmail(userRequestDto.getEmail());

        newUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        newUser.setRoles(Set.of(defaultRole));

        UserModel savedUser = userRepository.save(newUser);

        return convertToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: User not found with ID: " + id));
        return convertToDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UserRequestDto userRequestDto) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: User not found for update."));

        if (!user.getUsername().equals(userRequestDto.getUsername()) && userRepository.existsByUsername(userRequestDto.getUsername())) {
            throw new RuntimeException("Error: New username is already in use!");
        }
        if (!user.getEmail().equals(userRequestDto.getEmail()) && userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RuntimeException("Error: New email is already in use!");
        }

        user.setUsername(userRequestDto.getUsername());
        user.setEmail(userRequestDto.getEmail());

        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        UserModel updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Error: User not found for deletion.");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDto updateUserRole(UUID id, String roleName) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: User not found with ID: " + id));

        RoleEnum roleEnum;
        try {
            roleEnum = RoleEnum.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error: Invalid role name: " + roleName);
        }

        RoleModel targetRole = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Error: Role not found in database: " + roleEnum));

        Set<RoleModel> userRoles = user.getRoles();

        userRoles.clear();

        userRoles.add(targetRole);

        UserModel updatedUser = userRepository.save(user);

        return convertToDto(updatedUser);
    }

    private UserResponseDto convertToDto(UserModel userModel) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(userModel.getId());
        dto.setUsername(userModel.getUsername());
        dto.setEmail(userModel.getEmail());
        dto.setRoles(userModel.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet()));

        return dto;
    }
}
