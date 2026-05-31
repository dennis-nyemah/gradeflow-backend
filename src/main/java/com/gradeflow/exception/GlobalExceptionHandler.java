package com.gradeflow.exception;

import com.gradeflow.dto.Dto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler
 *
 * This class handles all exceptions thrown across the application
 * in a centralized and consistent way.
 *
 * It ensures that:
 * - API responses are uniform
 * - Sensitive stack traces are not exposed to clients
 * - Frontend always receives structured error messages
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid login attempts (bad credentials).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Dto.ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new Dto.ErrorResponse("Invalid username or password"));
    }

    /**
     * Handles invalid arguments passed to service/controller methods.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Dto.ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Dto.ErrorResponse(ex.getMessage()));
    }

    /**
     * Handles validation errors from @Valid annotations.
     *
     * Extracts the first field error and returns a readable message
     * to the frontend.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Dto.ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Dto.ErrorResponse(message));
    }

    /**
     * Handles unauthorized access attempts (role-based security violations).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Dto.ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Dto.ErrorResponse("Access denied"));
    }

    /**
     * Handles runtime exceptions thrown in service layer.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Dto.ErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Dto.ErrorResponse(ex.getMessage()));
    }

    /**
     * Catch-all handler for unexpected system errors.
     *
     * Prevents stack traces from leaking to clients.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Dto.ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Dto.ErrorResponse("An unexpected error occurred"));
    }
}