package org.example.finaccesscore;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashGenerator {

    @Test
    public void generatePasswordHash() {
        String password = "password123";
        
        // Test with BCryptPasswordEncoder directly
        BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();
        String bcryptHash = bCryptEncoder.encode(password);
        System.out.println("BCrypt Hash: " + bcryptHash);
        System.out.println("BCrypt Matches: " + bCryptEncoder.matches(password, bcryptHash));
        
        // Test with DelegatingPasswordEncoder
        PasswordEncoder delegatingEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String delegatingHash = delegatingEncoder.encode(password);
        System.out.println("\nDelegating Hash: " + delegatingHash);
        System.out.println("Delegating Matches: " + delegatingEncoder.matches(password, delegatingHash));
        
        // Test if delegating encoder can match bcrypt hash with prefix
        String withPrefix = "{bcrypt}" + bcryptHash;
        System.out.println("\nWith Prefix: " + withPrefix);
        System.out.println("Delegating Matches with Prefix: " + delegatingEncoder.matches(password, withPrefix));
        
        // Test the existing hash from database
        String existingHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        System.out.println("\nExisting Hash: " + existingHash);
        System.out.println("BCrypt Matches Existing: " + bCryptEncoder.matches(password, existingHash));
        System.out.println("Delegating Matches Existing with Prefix: " + delegatingEncoder.matches(password, "{bcrypt}" + existingHash));
    }
}
