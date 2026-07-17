package ui;

import model.Person;
import service.BookingService;

import javax.swing.*;
import java.awt.*;

/**
 * Single dashboard window whose tabs adapt based on the logged-in
 * Person's role (POLYMORPHISM: works identically for Admin or Customer,
 * behavior differs via getRole()/instanceof checks).
 */
public class MainDashboard extends JFrame {

    private final BookingService bookingService = new BookingService();

    public MainDashboard(Person loggedInUser) {
        boolean isAdmin = "ADMIN".equals(loggedInUser.getRole());

        setTitle("Grand Java Hotel - " + (isAdmin ? "Admin Dashboard" : "Customer Portal"));
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcome = new JLabel("  Welcome, " + loggedInUser.getName() + "  (" + loggedInUser.getRole() + ")");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 16));
        welcome.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(welcome, BorderLayout.WEST);
        JPanel logoutPanel = new JPanel();
        logoutPanel.add(logoutBtn);
        top.add(logoutPanel, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Rooms", new RoomPanel(bookingService, isAdmin));
        tabs.addTab("Book a Room", new BookingPanel(bookingService, loggedInUser, isAdmin));

        if (isAdmin) {
            tabs.addTab("All Bookings", new AdminBookingsPanel(bookingService));
            tabs.addTab("Customers", new CustomerPanel(bookingService));
            tabs.addTab("Reports", new ReportPanel(bookingService));
        } else {
            tabs.addTab("My Bookings", new MyBookingsPanel(bookingService, loggedInUser.getId()));
        }

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }
}
