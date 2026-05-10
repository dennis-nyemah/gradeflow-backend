package com.gradeflow.config;

import com.gradeflow.entity.User;
import com.gradeflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByUsername("admin")) {
            User sponsor = User.builder()
                    .fullName("School Admin")
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.SPONSOR)
                    .build();
            userRepository.save(sponsor);
            log.info("✅ Default sponsor account created — username: admin / password: admin123");
            log.info("   ⚠ Change this password immediately in production!");
        }
    }
}