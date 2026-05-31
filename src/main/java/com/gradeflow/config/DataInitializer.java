package com.gradeflow.config;

import com.gradeflow.entity.User;
import com.gradeflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer is responsible for seeding the database with default data
 * when the application starts for the first time.
 *
 * In this case, it ensures that a default IT Administrator account exists.
 *
 * This runs automatically on application startup using Spring Boot's
 * ApplicationRunner interface.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Admin username loaded from environment variables.
     */
    @Value("${ADMIN_USERNAME}")
    private String adminUsername;

    /**
     * Admin password loaded from environment variables.
     */
    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    /**
     * Runs automatically when the Spring Boot application starts.
     *
     * Logic:
     * - Checks if a user with the admin username already exists
     * - If not, creates a default IT Administrator account
     * - Password is securely encoded using PasswordEncoder
     *
     * This prevents duplicate admin creation on every restart.
     */
    @Override
    public void run(ApplicationArguments args) {

        // Check if admin already exists in database
        if (!userRepository.existsByUsername(adminUsername)) {

            // Create default IT admin user
            User it = User.builder()
                    .fullName("IT Administrator")
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(User.Role.IT)
                    .build();

            // Save to database
            userRepository.save(it);

            log.info("✅ Default IT account created.");
        }
    }
}