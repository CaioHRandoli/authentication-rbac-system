package com.project.login.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateDto {
    @NotBlank(message = "Role name cannot be blank")
    private String role;
}
