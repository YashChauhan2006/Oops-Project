package com.ems.ui;

import com.ems.model.User;
import com.ems.service.AuthService;
import com.ems.ui.components.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignupFrame extends JFrame {
    private ModernTextField nameField, emailField, academicField, imagePathField;
    private ModernPasswordField passwordField;
    private JComboBox<User.Role> roleCombo;
    private CustomButton signupBtn, uploadBtn;
    private JButton loginLink;
    private AuthService authService = new AuthService();

    public SignupFrame() {
        setTitle("EMS | Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 30, 8, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel header = new JLabel("Create Account", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(new Color(0, 86, 179));
        gbc.gridy = 0;
        mainPanel.add(header, gbc);

        // Name
        gbc.gridy++;
        mainPanel.add(new JLabel("Full Name"), gbc);
        nameField = new ModernTextField(20);
        gbc.gridy++;
        mainPanel.add(nameField, gbc);

        // Email
        gbc.gridy++;
        mainPanel.add(new JLabel("Email Address"), gbc);
        emailField = new ModernTextField(20);
        gbc.gridy++;
        mainPanel.add(emailField, gbc);

        // Password
        gbc.gridy++;
        mainPanel.add(new JLabel("Password"), gbc);
        passwordField = new ModernPasswordField(20);
        gbc.gridy++;
        mainPanel.add(passwordField, gbc);

        // Role
        gbc.gridy++;
        mainPanel.add(new JLabel("Account Type"), gbc);
        roleCombo = new JComboBox<>(new User.Role[]{User.Role.STUDENT, User.Role.TEACHER});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleCombo.setBackground(Color.WHITE);
        gbc.gridy++;
        mainPanel.add(roleCombo, gbc);

        // Academic Details
        gbc.gridy++;
        mainPanel.add(new JLabel("Academic Details (e.g. B.Tech, Semester 4)"), gbc);
        academicField = new ModernTextField(20);
        gbc.gridy++;
        mainPanel.add(academicField, gbc);

        // Image Upload
        gbc.gridy++;
        mainPanel.add(new JLabel("Profile Image"), gbc);
        JPanel imagePanel = new JPanel(new BorderLayout(10, 0));
        imagePanel.setBackground(Color.WHITE);
        imagePathField = new ModernTextField(15);
        imagePathField.setEditable(false);
        uploadBtn = new CustomButton("Browse");
        uploadBtn.setPreferredSize(new Dimension(80, 40));
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(uploadBtn, BorderLayout.EAST);
        gbc.gridy++;
        mainPanel.add(imagePanel, gbc);

        // Signup Button
        signupBtn = new CustomButton("SIGN UP");
        signupBtn.setPreferredSize(new Dimension(0, 45));
        gbc.gridy++;
        gbc.insets = new Insets(20, 30, 10, 30);
        mainPanel.add(signupBtn, gbc);

        // Login Redirect
        JPanel footer = new JPanel(new FlowLayout());
        footer.setBackground(Color.WHITE);
        footer.add(new JLabel("Already have an account?"));
        loginLink = new JButton("Login");
        loginLink.setForeground(new Color(0, 86, 179));
        loginLink.setBorder(null);
        loginLink.setContentAreaFilled(false);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footer.add(loginLink);
        
        gbc.gridy++;
        mainPanel.add(footer, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Actions
        signupBtn.addActionListener(this::handleSignup);
        uploadBtn.addActionListener(this::handleImageUpload);
        loginLink.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }

    private void handleImageUpload(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            imagePathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void handleSignup(ActionEvent e) {
        String name = nameField.getText();
        String email = emailField.getText();
        String pass = new String(passwordField.getPassword());
        User.Role role = (User.Role) roleCombo.getSelectedItem();
        String academic = academicField.getText();
        String imagePath = imagePathField.getText();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill required fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = new User();
        user.setFullName(name);
        user.setEmail(email);
        user.setPassword(pass);
        user.setRole(role);
        user.setAcademicDetails(academic);
        user.setProfileImagePath(imagePath);

        if (authService.register(user)) {
            JOptionPane.showMessageDialog(this, "Registration Successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed. Email might be in use.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
