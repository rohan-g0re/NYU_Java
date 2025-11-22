package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.api.ApiClient;
import com.nyu.aichat.client.api.ApiException;
import com.nyu.aichat.client.model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        this.executorService = Executors.newFixedThreadPool(4);
        setupUI();
    }
    
    private void setupUI() {
        setTitle("AI Chat - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("AI Chat Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Signup checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        signupCheckbox = new JCheckBox("New user? Sign up");
        formPanel.add(signupCheckbox, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> onLoginClick());
        buttonPanel.add(loginButton);
        
        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(errorLabel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Enter key support
        getRootPane().setDefaultButton(loginButton);
        
        add(mainPanel);
    }
    
    private void onLoginClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        loginButton.setEnabled(false);
        errorLabel.setText("Connecting...");
        
        executorService.submit(() -> {
            try {
                ApiClient.LoginResponse response;
                if (signupCheckbox.isSelected()) {
                    response = apiClient.signup(username, password);
                } else {
                    response = apiClient.login(username, password);
                }
                
                SwingUtilities.invokeLater(() -> {
                    handleAuthSuccess(response);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    handleAuthError(e.getMessage());
                });
            }
        });
    }
    
    private void handleAuthSuccess(ApiClient.LoginResponse response) {
        UserSession session = new UserSession(response.getUserId(), response.getUsername());
        MainChatFrame mainFrame = new MainChatFrame(session);
        mainFrame.setVisible(true);
        dispose();
    }
    
    private void handleAuthError(String errorMessage) {
        loginButton.setEnabled(true);
        showError(errorMessage);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }
}

