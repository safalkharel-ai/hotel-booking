package ui;

import model.BookingStatus;
import service.BookingService;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {

    private final BookingService bookingService;
    private final JLabel totalRoomsLabel = new JLabel();
    private final JLabel occupiedLabel = new JLabel();
    private final JLabel availableLabel = new JLabel();
    private final JLabel totalCustomersLabel = new JLabel();
    private final JLabel totalBookingsLabel = new JLabel();
    private final JLabel activeBookingsLabel = new JLabel();
    private final JLabel revenueLabel = new JLabel();

    public ReportPanel(BookingService bookingService) {
        this.bookingService = bookingService;

        setLayout(new GridLayout(0, 1, 5, 12));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        Font font = new Font("SansSerif", Font.PLAIN, 16);
        for (JLabel l : new JLabel[]{totalRoomsLabel, occupiedLabel, availableLabel,
                totalCustomersLabel, totalBookingsLabel, activeBookingsLabel, revenueLabel}) {
            l.setFont(font);
        }
        revenueLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        revenueLabel.setForeground(new Color(0, 110, 0));

        add(totalRoomsLabel);
        add(occupiedLabel);
        add(availableLabel);
        add(totalCustomersLabel);
        add(totalBookingsLabel);
        add(activeBookingsLabel);
        add(revenueLabel);

        JButton refreshBtn = new JButton("Refresh Report");
        refreshBtn.addActionListener(e -> refresh());
        add(refreshBtn);

        refresh();
    }

    public void refresh() {
        long totalRooms = bookingService.getRoomDAO().findAll().size();
        long occupied = bookingService.getOccupiedRoomCount();
        long available = totalRooms - occupied;
        long totalCustomers = bookingService.getCustomerDAO().findAll().size();
        long totalBookings = bookingService.getBookingDAO().findAll().size();
        long activeBookings = bookingService.getBookingDAO().findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN)
                .count();
        double revenue = bookingService.getTotalRevenue();

        totalRoomsLabel.setText("Total Rooms: " + totalRooms);
        occupiedLabel.setText("Occupied Rooms: " + occupied);
        availableLabel.setText("Available Rooms: " + available);
        totalCustomersLabel.setText("Registered Customers: " + totalCustomers);
        totalBookingsLabel.setText("Total Bookings (all time): " + totalBookings);
        activeBookingsLabel.setText("Active Bookings: " + activeBookings);
        revenueLabel.setText("Total Revenue: Rs. " + String.format("%.2f", revenue));
    }
}
