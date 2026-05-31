package com.gradeflow.security;

import com.gradeflow.entity.User;
import com.gradeflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom UserDetailsService implementation
 *
 * This class is responsible for loading user authentication data
 * from the database during login and JWT authentication.
 *
 * It is a core component of Spring Security's authentication flow.
 *
 * Responsibilities:
 * - Fetch user from database by username
 * - Convert domain User entity into Spring Security UserDetails
 * - Attach roles as granted authorities (ROLE_*)
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user details from database for authentication.
     *
     * This method is triggered during:
     * - Login authentication
     * - JWT authentication (token validation)
     *
     * @param username the username provided during login
     * @return Spring Security UserDetails object
     * @throws UsernameNotFoundException if user does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Fetch user from database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert application User → Spring Security UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}