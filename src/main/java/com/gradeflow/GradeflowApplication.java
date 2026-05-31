package com.gradeflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the GradeFlow application.
 *
 * Responsibilities:
 * - Bootstraps the Spring Boot application
 * - Initializes Spring context and dependency injection
 * - Starts the embedded web server
 * - Loads application configuration
 */
@SpringBootApplication
public class GradeflowApplication {

	public static void main(String[] args) {
        SpringApplication.run(GradeflowApplication.class, args);
    }
}
