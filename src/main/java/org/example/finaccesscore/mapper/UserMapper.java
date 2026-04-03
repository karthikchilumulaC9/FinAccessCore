package org.example.finaccesscore.mapper;

import org.example.finaccesscore.dto.CreateUserRequest;
import org.example.finaccesscore.dto.RegisterUserRequest;
import org.example.finaccesscore.dto.UpdateUserRequest;
import org.example.finaccesscore.dto.UserResponse;
import org.example.finaccesscore.model.Role;
import org.example.finaccesscore.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserMapper {

    public User toEntity(RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRoles(Set.of(Role.ROLE_VIEWER)); // Default role for self-registration
        user.setActive(true);
        return user;
    }

    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRoles(request.getRoles());
        user.setActive(request.getActive() != null ? request.getActive() : true);
        return user;
    }

    public void applyUpdate(UpdateUserRequest request, User user) {
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRoles() != null) {
            user.setRoles(request.getRoles());
        }
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoles(user.getRoles());
        response.setActive(user.getActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
