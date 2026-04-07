package com.ems.ui;

import com.ems.model.User;
import com.ems.service.AuthService;
import com.ems.ui.components.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private ModernTextField emailField;
    private ModernPasswordField passwordField;
    private CustomButton loginBtn;
    private JButton signupLink;
    private AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("EMS | Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Main Container
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 30, 10, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Branding
        JLabel logoLabel = new JLabel("Examination Management System", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(new Color(0, 86, 179));
        gbc.gridy = 0;
        mainPanel.add(logoLabel, gbc);

        JLabel subtitle = new JLabel("Login to your account", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(subtitle, gbc);

        // Inputs
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 30, 5, 30);
        mainPanel.add(new JLabel("Email Address"), gbc);
        
        emailField = new ModernTextField(20);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 30, 15, 30);
        mainPanel.add(emailField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(5, 30, 5, 30);
        mainPanel.add(new JLabel("Password"), gbc);

        passwordField = new ModernPasswordField(20);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 30, 20, 30);
        mainPanel.add(passwordField, gbc);

        // Login Button
        loginBtn = new CustomButton("LOGIN");
        loginBtn.setPreferredSize(new Dimension(0, 45));
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 30, 10, 30);
        mainPanel.add(loginBtn, gbc);

        // Signup Redirect
        JPanel footer = new JPanel(new FlowLayout());
        footer.setBackground(Color.WHITE);
        footer.add(new JLabel("Don't have an account?"));
        signupLink = new JButton("Sign Up");
        signupLink.setForeground(new Color(0, 86, 179));
        signupLink.setBorder(null);
        signupLink.setContentAreaFilled(false);
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footer.add(signupLink);
        
        gbc.gridy = 7;
        mainPanel.add(footer, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Actions
        loginBtn.addActionListener(this::handleLogin);
        signupLink.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            this.dispose();
        });
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authService.login(email, pass);
        if (user != null) {
            redirectByUserRole(user);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void redirectByUserRole(User user) {
        switch (user.getRole()) {
            case ADMIN:
                new AdminDashboard(user).setVisible(true);
                break;
            case TEACHER:
                new TeacherDashboard(user).setVisible(true);
                break;
            case STUDENT:
                new StudentDashboard(user).setVisible(true);
                break;
        }
    }
}
