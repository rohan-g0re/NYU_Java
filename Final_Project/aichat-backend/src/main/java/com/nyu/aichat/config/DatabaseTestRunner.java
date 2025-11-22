package com.nyu.aichat.config;

import com.nyu.aichat.entity.User;
import com.nyu.aichat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Temporary test component to verify database connectivity.
 * Remove this class after confirming database works correctly.
 * 
 * To enable: Add --spring.profiles.active=test-db to VM options or run with:
 * java -jar app.jar --spring.profiles.active=test-db
 */
@Component
@Profile("test-db")
public class DatabaseTestRunner implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Database Connectivity Test ===");
        
        // Test: Create a user
        User testUser = new User("test_user_" + System.currentTimeMillis(), "test_hash");
        testUser = userRepository.save(testUser);
        System.out.println("✓ Created test user with ID: " + testUser.getId());
        
        // Test: Read the user back
        User found = userRepository.findById(testUser.getId()).orElse(null);
        if (found != null) {
            System.out.println("✓ Successfully read user: " + found.getUsername());
        } else {
            System.out.println("✗ Failed to read user");
        }
        
        // Test: Find by username
        User foundByUsername = userRepository.findByUsername(testUser.getUsername()).orElse(null);
        if (foundByUsername != null) {
            System.out.println("✓ Successfully found user by username");
        } else {
            System.out.println("✗ Failed to find user by username");
        }
        
        // Cleanup: Delete test user
        userRepository.delete(testUser);
        System.out.println("✓ Cleaned up test user");
        
        System.out.println("=== Database test completed successfully ===");
        System.out.println("You can remove DatabaseTestRunner.java after confirming everything works.");
    }
}

