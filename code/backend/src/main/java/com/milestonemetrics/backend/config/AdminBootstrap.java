package com.milestonemetrics.backend.config;

import com.milestonemetrics.backend.entity.Role;
import com.milestonemetrics.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Safe, documented way to create the first administrator without hardcoding
 * credentials or exposing secrets: set the {@code ADMIN_EMAIL} environment
 * variable to the email of an already-registered user. On startup that user is
 * promoted to ADMIN (idempotent). They must then log in again so their new JWT
 * carries the ADMIN role. If ADMIN_EMAIL is unset, this does nothing.
 */
@Configuration
public class AdminBootstrap {

    @Bean
    @Order(2)
    CommandLineRunner promoteAdmin(UserRepository userRepository,
                                   @Value("${admin.bootstrap-email:}") String adminEmail) {
        return args -> {
            if (adminEmail == null || adminEmail.isBlank()) return;
            userRepository.findByEmail(adminEmail.trim()).ifPresent(user -> {
                if (user.getRole() != Role.ADMIN) {
                    user.setRole(Role.ADMIN);
                    userRepository.save(user);
                    System.out.println("[AdminBootstrap] Promoted " + adminEmail + " to ADMIN. "
                            + "They must log in again to receive an admin token.");
                }
            });
        };
    }
}
