package ui;

import model.Booking;
import model.Customer;
import model.Person;
import model.Room;
import service.BookingService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class BookingPanel extends JPanel {

    private final BookingService bookingService;
    private final Person loggedInUser;
    private final boolean isAdmin;

    private JComboBox<Room> roomBox;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JTextField customerIdField;
    private JLabel resultLabel;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookingPanel(BookingService bookingService, Person loggedInUser, boolean isAdmin) {
        this.bookingService = bookingService;
        this.loggedInUser = loggedInUser;
        this.isAdmin = isAdmin;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Room:"), gbc);
        roomBox = new JComboBox<>();
        refreshRooms();
        gbc.gridx = 1;
        add(roomBox, gbc);
        row++;

        if (isAdmin) {
            gbc.gridx = 0; gbc.gridy = row;
            add(new JLabel("Customer ID:"), gbc);
            customerIdField = new JTextField(10);
            gbc.gridx = 1;
            add(customerIdField, gbc);
            row++;
        }

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Check-in (yyyy-MM-dd):"), gbc);
        checkInField = new JTextField(LocalDate.now().plusDays(1).format(FMT), 12);
        gbc.gridx = 1;
        add(checkInField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Check-out (yyyy-MM-dd):"), gbc);
        checkOutField = new JTextField(LocalDate.now().plusDays(2).format(FMT), 12);
        gbc.gridx = 1;
        add(checkOutField, gbc);
        row++;

        JButton bookBtn = new JButton("Confirm Booking");
        bookBtn.addActionListener(e -> handleBooking());
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(bookBtn, gbc);
        row++;

        JButton refreshBtn = new JButton("Refresh Room List");
        refreshBtn.addActionListener(e -> refreshRooms());
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(refreshBtn, gbc);
        row++;

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        add(resultLabel, gbc);
    }

    private void refreshRooms() {
        roomBox.removeAllItems();
        for (Room r : bookingService.getRoomDAO().findAll()) {
            roomBox.addItem(r);
        }
    }

    private void handleBooking() {
        try {
            Room selectedRoom = (Room) roomBox.getSelectedItem();
            if (selectedRoom == null) {
                resultLabel.setText("No room selected.");
                return;
            }

            LocalDate checkIn = LocalDate.parse(checkInField.getText().trim(), FMT);
            LocalDate checkOut = LocalDate.parse(checkOutField.getText().trim(), FMT);

            int customerId;
            if (isAdmin) {
                customerId = Integer.parseInt(customerIdField.getText().trim());
                if (bookingService.getCustomerDAO().findById(customerId) == null) {
                    resultLabel.setText("No customer found with that ID.");
                    return;
                }
            } else {
                customerId = loggedInUser.getId();
            }

            Booking booking = bookingService.createBooking(customerId, selectedRoom.getRoomNumber(), checkIn, checkOut);
            resultLabel.setForeground(new Color(0, 128, 0));
            resultLabel.setText("Booked! " + booking + " for " + booking.getNights() + " night(s).");
        } catch (DateTimeParseException ex) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Invalid date format. Use yyyy-MM-dd.");
        } catch (NumberFormatException ex) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Customer ID must be a number.");
        } catch (Exception ex) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText(ex.getMessage());
        }
    }
}
