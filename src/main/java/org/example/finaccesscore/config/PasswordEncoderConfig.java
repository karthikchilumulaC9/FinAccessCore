package org.example.finaccesscore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use Spring Security's default DelegatingPasswordEncoder
        // This automatically handles {bcrypt}, {noop}, etc. prefixes
        // and defaults to bcrypt for passwords without prefix
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}