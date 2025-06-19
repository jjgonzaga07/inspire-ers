package com.inspire.ers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin123";

    public Login() {
        setTitle("INSPIRE EMPLOYEE RECORDS SYSTEM"); 
    
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // Set window icon
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/inspirelogo2.png"));
        Image resizedIcon = icon.getScaledInstance(35,35,Image.SCALE_SMOOTH);
        setIconImage(resizedIcon);
        

        // Load background image
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/images/deepocean5.jpg"));
        

        // Create panel with background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Transparent login panel
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Title labels
        JLabel titleLabel = new JLabel("Inspire ERS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("ADMIN ONLY");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(Color.WHITE);

        // Input fields
        usernameField = new JTextField(15);
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBackground(new Color(0, 0, 0, 100));
        usernameField.setOpaque(false);

        passwordField = new JPasswordField(15);
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBackground(new Color(0, 0, 0, 100));
        passwordField.setOpaque(false);

        // Labels for fields
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
       JButton loginButton = new JButton("Login");
            loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginButton.setFocusPainted(false);
            loginButton.setBackground(new Color(30, 144, 255)); // Dodger Blue (default)
            loginButton.setForeground(Color.WHITE);
            loginButton.setOpaque(true);
            loginButton.setBorderPainted(false);

            // Hover effect
            loginButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    loginButton.setBackground(new Color(0, 125, 250)); // Darker blue on hover
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    loginButton.setBackground(new Color(30, 144, 255)); // Original blue
                }
            });

            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                    new HomePage().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Login.this,
                            "Invalid username or password!",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });


        // Add components to login panel
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(subtitleLabel);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);

        // Add login panel to background
        backgroundPanel.add(loginPanel);
        setContentPane(backgroundPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
