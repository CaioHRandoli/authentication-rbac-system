package com.project.login.config;

import com.project.login.enums.RoleEnum;
import com.project.login.models.RoleModel;
import com.project.login.models.UserModel;
import com.project.login.repositories.RoleRepository;
import com.project.login.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                RoleModel newRole = new RoleModel();
                newRole.setName(roleEnum);
                roleRepository.save(newRole);
                System.out.println("DataInitializer: Role " + roleEnum.name() + " successfully initialized!");
            }
        }

        RoleModel adminRole = getRole(RoleEnum.ROLE_ADMIN);
        RoleModel managerRole = getRole(RoleEnum.ROLE_MANAGER);
        RoleModel userRole = getRole(RoleEnum.ROLE_USER);

        createUserIfNotExists("admin", "admin@gmail.com", "admin", adminRole);
        createUserIfNotExists("test1", "test1@gmail.com", "test1", managerRole);
        createUserIfNotExists("test2", "test2@gmail.com", "test2", userRole);
    }

    private RoleModel getRole(RoleEnum roleEnum) {
        return roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Role " + roleEnum + " not found in database."));
    }

    private void createUserIfNotExists(String username, String email, String rawPassword, RoleModel role) {
        if (!userRepository.existsByUsername(username)) {
            UserModel user = new UserModel();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRoles(Set.of(role));

            userRepository.save(user);
            System.out.println("DataInitializer: User " + username + " successfully created!");
        } else {
            System.out.println("DataInitializer: User " + username + " already exists.");
        }
    }
}