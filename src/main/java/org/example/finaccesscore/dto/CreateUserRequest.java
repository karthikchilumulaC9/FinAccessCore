package org.example.finaccesscore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.finaccesscore.model.Role;

import java.util.Set;

import static org.example.finaccesscore.constants.AppConstants.*;

@Data
public class CreateUserRequest {
    @NotBlank(message = USERNAME_REQUIRED)
    @Size(min = 3, max = 50, message = USERNAME_SIZE)
    private String username;

    @Email(message = EMAIL_INVALID)
    @NotBlank(message = EMAIL_REQUIRED)
    private String email;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = 8, message = PASSWORD_SIZE)
    private String password;

    @NotNull(message = ROLES_REQUIRED)
    private Set<Role> roles;
    
    private Boolean active = true;
}
