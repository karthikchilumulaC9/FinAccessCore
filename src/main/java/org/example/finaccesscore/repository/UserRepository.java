package org.example.finaccesscore.repository;

import org.example.finaccesscore.model.Role;
import org.example.finaccesscore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role AND u.active = true")
    long countByRolesContainingAndActiveTrue(@Param("role") Role role);
}
