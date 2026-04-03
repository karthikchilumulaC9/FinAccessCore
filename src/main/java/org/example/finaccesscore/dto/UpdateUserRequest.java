package org.example.finaccesscore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.finaccesscore.model.Role;

import java.util.Set;

import static org.example.finaccesscore.constants.AppConstants.*;

@Data
public class UpdateUserRequest {
    @Size(min = 3, max = 50, message = USERNAME_SIZE)
    private String username;

    @Email(message = EMAIL_INVALID)
    private String email;

    @Size(min = 8, message = PASSWORD_SIZE)
    private String password;
    
    private Set<Role> roles;
    private Boolean active;
}
