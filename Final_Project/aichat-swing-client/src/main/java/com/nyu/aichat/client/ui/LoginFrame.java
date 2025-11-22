package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.api.ApiClient;
import com.nyu.aichat.client.api.ApiException;
import com.nyu.aichat.client.model.UserSession;
import com.nyu.aichat.client.util.ConfigLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Login/Signup window.
 * Handles user authentication and opens MainChatFrame on success.
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox signupCheckbox;
    private JLabel errorLabel;
    private ApiClient apiClient;
    private ExecutorService executorService;
    
    public LoginFrame() {
        this.apiClient = new ApiClient();
        this.executorService = Executors.newCachedThreadPool();
        setupUI();
    }
    
    private void setupUI() {
        setTitle("AI Chat - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("AI Chat", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Signup checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        signupCheckbox = new JCheckBox("New user? Sign up");
        formPanel.add(signupCheckbox, gbc);
        
        // Login/Signup button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signupCheckbox.isSelected()) {
                    onSignupClick();
                } else {
                    onLoginClick();
                }
            }
        });
        formPanel.add(loginButton, gbc);
        
        // Error label
        gbc.gridy = 4;
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(errorLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Update button text when checkbox changes
        signupCheckbox.addActionListener(e -> {
            loginButton.setText(signupCheckbox.isSelected() ? "Sign Up" : "Login");
        });
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void onLoginClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        loginButton.setEnabled(false);
        errorLabel.setText("Logging in...");
        
        executorService.execute(() -> {
            try {
                ApiClient.LoginResponse response = apiClient.login(username, password);
                SwingUtilities.invokeLater(() -> {
                    handleAuthSuccess(response);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    handleAuthError(e.getMessage());
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                });
            }
        });
    }
    
    private void onSignupClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        if (username.length() < 3 || username.length() > 20) {
            showError("Username must be between 3 and 20 characters");
            return;
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, digits, and underscores");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }
        
        loginButton.setEnabled(false);
        errorLabel.setText("Signing up...");
        
        executorService.execute(() -> {
            try {
                ApiClient.LoginResponse response = apiClient.signup(username, password);
                SwingUtilities.invokeLater(() -> {
                    handleAuthSuccess(response);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    handleAuthError(e.getMessage());
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                });
            }
        });
    }
    
    private void handleAuthSuccess(ApiClient.LoginResponse response) {
        UserSession session = new UserSession(response.getUserId(), response.getUsername());
        
        // Open main chat frame
        SwingUtilities.invokeLater(() -> {
            MainChatFrame mainFrame = new MainChatFrame(session, apiClient);
            mainFrame.setVisible(true);
            dispose(); // Close login window
        });
    }
    
    private void handleAuthError(String errorMessage) {
        showError(errorMessage);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        executorService.shutdown();
    }
}

