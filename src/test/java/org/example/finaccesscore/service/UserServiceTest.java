package org.example.finaccesscore.service;

import org.example.finaccesscore.dto.CreateUserRequest;
import org.example.finaccesscore.dto.UserResponse;
import org.example.finaccesscore.model.User;
import org.example.finaccesscore.model.Role;
import org.example.finaccesscore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void testCreateUserWithDefaultRole() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setEmail("testuser@example.com");
        request.setPassword("password");
        request.setRoles(Set.of(Role.ROLE_VIEWER));

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setRoles(Set.of(Role.ROLE_VIEWER));

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse createdUser = userService.createUser(request);
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        assertNotNull(createdUser.getRoles());
        assertTrue(createdUser.getRoles().contains(Role.ROLE_VIEWER));
    }

    @Test
    public void testCreateUserWithSpecifiedRole() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("adminuser");
        request.setEmail("admin@example.com");
        request.setPassword("password");
        request.setRoles(Set.of(Role.ROLE_ADMIN));

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse createdUser = userService.createUser(request);
        assertNotNull(createdUser);
        assertTrue(createdUser.getRoles().contains(Role.ROLE_ADMIN));
        assertFalse(createdUser.getRoles().contains(Role.ROLE_VIEWER), "Should NOT have default role if role is specified");
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        UserResponse retrievedUser = userService.getUserById(1L);
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.getUsername());
    }
}
