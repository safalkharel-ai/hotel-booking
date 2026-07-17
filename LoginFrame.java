package ui;

import model.Admin;
import model.Customer;
import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    private JTextField loginEmailField;
    private JPasswordField loginPasswordField;

    private JTextField regNameField, regEmailField, regPhoneField, regAddressField;
    private JPasswordField regPasswordField;

    public LoginFrame() {
        setTitle("Hotel Booking System - Login");
        setSize(480, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Login", buildLoginPanel());
        tabs.addTab("Register (New Customer)", buildRegisterPanel());

        JLabel header = new JLabel("Grand Java Hotel", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        JLabel hint = new JLabel("Admin demo login -> admin@hotel.com / admin123", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        add(hint, BorderLayout.SOUTH);
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);
        loginEmailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(loginEmailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        loginPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(loginPasswordField, gbc);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> handleLogin());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        return panel;
    }

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Full Name:", "Email:", "Phone:", "Password:", "Address:"};
        JTextField[] fields = new JTextField[5];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
            if (i == 3) {
                regPasswordField = new JPasswordField(20);
                gbc.gridx = 1;
                panel.add(regPasswordField, gbc);
            } else {
                JTextField tf = new JTextField(20);
                fields[i] = tf;
                gbc.gridx = 1;
                panel.add(tf, gbc);
            }
        }
        regNameField = fields[0];
        regEmailField = fields[1];
        regPhoneField = fields[2];
        regAddressField = fields[4];

        JButton registerBtn = new JButton("Create Account");
        registerBtn.addActionListener(e -> handleRegister());
        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);

        return panel;
    }

    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.");
            return;
        }

        Admin admin = authService.loginAsAdmin(email, password);
        if (admin != null) {
            dispose();
            new MainDashboard(admin).setVisible(true);
            return;
        }

        Customer customer = authService.loginAsCustomer(email, password);
        if (customer != null) {
            dispose();
            new MainDashboard(customer).setVisible(true);
            return;
        }

        JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
    }

    private void handleRegister() {
        try {
            String name = regNameField.getText().trim();
            String email = regEmailField.getText().trim();
            String phone = regPhoneField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String address = regAddressField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name, email and password are required.");
                return;
            }

            Customer customer = authService.register(name, email, phone, password, address);
            JOptionPane.showMessageDialog(this, "Account created! You can now log in as " + customer.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
