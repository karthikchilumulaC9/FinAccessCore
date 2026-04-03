package org.example.finaccesscore.dto;

import lombok.Data;

import org.example.finaccesscore.model.Role;
import java.time.Instant;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;
    private Boolean active;
    private Instant createdAt;
}
