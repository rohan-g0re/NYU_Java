package com.nyu.aichat.service;

import com.nyu.aichat.dto.response.LoginResponse;
import com.nyu.aichat.entity.User;
import com.nyu.aichat.exception.UnauthorizedException;
import com.nyu.aichat.exception.UserNotFoundException;
import com.nyu.aichat.exception.ValidationException;
import com.nyu.aichat.repository.UserRepository;
import com.nyu.aichat.util.Constants;
import com.nyu.aichat.util.EntityMapper;
import com.nyu.aichat.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for user authentication operations (signup and login).
 */
@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Creates a new user account.
     * 
     * @param username The desired username
     * @param rawPassword The plain text password (will be hashed)
     * @return LoginResponse containing user ID and username
     * @throws ValidationException if username/password invalid or username already exists
     */
    public LoginResponse signup(String username, String rawPassword) {
        // Validate input
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(rawPassword);
        
        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            logger.warn("Signup attempt with existing username: {}", username);
            throw new ValidationException(Constants.ERROR_USERNAME_EXISTS);
        }
        
        // Hash password
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        // Create user (@PrePersist handles createdAt)
        User user = new User(username, hashedPassword);
        user = userRepository.save(user);
        
        logger.info("New user created: {}", username);
        return EntityMapper.toLoginResponse(user);
    }
    
    /**
     * Authenticates a user and returns their login information.
     * 
     * @param username The username
     * @param rawPassword The plain text password
     * @return LoginResponse containing user ID and username
     * @throws UserNotFoundException if username doesn't exist
     * @throws UnauthorizedException if password is incorrect
     */
    public LoginResponse login(String username, String rawPassword) {
        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Login attempt with non-existent username: {}", username);
                    return new UserNotFoundException("No user exists with this username");
                });
        
        // Verify password
        if (!passwordEncoder.matches(rawPassword, user.getPassHash())) {
            logger.warn("Failed login attempt for username: {}", username);
            throw new UnauthorizedException(Constants.ERROR_INVALID_CREDENTIALS);
        }
        
        logger.info("User logged in successfully: {}", username);
        return EntityMapper.toLoginResponse(user);
    }
}

