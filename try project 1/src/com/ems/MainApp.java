package com.ems;

import com.ems.ui.LoginFrame;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        // Set Look and Feel to System Default for better native compatibility
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch the application starting from the Login Screen
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
