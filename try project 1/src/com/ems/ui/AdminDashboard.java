package com.ems.ui;

import com.ems.model.User;
import com.ems.service.UserService;
import com.ems.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private User admin;
    private UserService userService = new UserService();
    private JTable userTable;
    private DefaultTableModel userModel;

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("EMS | Admin Dashboard - " + admin.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(8, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(245, 246, 248));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel welcome = new JLabel("Admin Panel");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        CustomButton viewStudentsBtn = new CustomButton("Manage Students");
        CustomButton viewTeachersBtn = new CustomButton("Manage Teachers");
        CustomButton logoutBtn = new CustomButton("Logout");
        logoutBtn.setBackgroundColor(new Color(220, 53, 69));

        sidebar.add(welcome);
        sidebar.add(new JLabel("Admin: " + admin.getFullName()));
        sidebar.add(new JLabel());
        sidebar.add(viewStudentsBtn);
        sidebar.add(viewTeachersBtn);
        sidebar.add(new JLabel());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // Main Area
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainArea.setBackground(Color.WHITE);

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainArea.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Full Name", "Email", "Details"};
        userModel = new DefaultTableModel(cols, 0);
        userTable = new JTable(userModel);
        userTable.setRowHeight(30);
        mainArea.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        CustomButton deleteBtn = new CustomButton("DELETE SELECTED USER");
        deleteBtn.setBackgroundColor(new Color(220, 53, 69));
        btnPanel.add(deleteBtn);
        mainArea.add(btnPanel, BorderLayout.SOUTH);

        add(mainArea, BorderLayout.CENTER);

        // Actions
        viewStudentsBtn.addActionListener(e -> refreshUsers(User.Role.STUDENT));
        viewTeachersBtn.addActionListener(e -> refreshUsers(User.Role.TEACHER));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        
        deleteBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                int userId = (int) userTable.getValueAt(row, 0);
                if (userService.deleteUser(userId)) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully.");
                    userModel.removeRow(row);
                }
            }
        });

        refreshUsers(User.Role.STUDENT);
    }

    private void refreshUsers(User.Role role) {
        userModel.setRowCount(0);
        List<User> users = userService.getUsersByRole(role);
        for (User u : users) {
            userModel.addRow(new Object[]{u.getUserId(), u.getFullName(), u.getEmail(), u.getAcademicDetails()});
        }
    }
}
