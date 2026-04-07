package com.ems.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ModernTextField extends JTextField {
    private int radius = 10;
    private Color bgColor = new Color(245, 246, 248); // Subtle Gray/Beige
    private Color borderColor = new Color(200, 200, 200);

    public ModernTextField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setMargin(new Insets(10, 10, 10, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(bgColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
        
        g2.setColor(borderColor);
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        
        super.paintComponent(g);
    }
}
