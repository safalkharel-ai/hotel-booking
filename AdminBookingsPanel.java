package ui;

import model.Booking;
import service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminBookingsPanel extends JPanel {

    private final BookingService bookingService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public AdminBookingsPanel(BookingService bookingService) {
        this.bookingService = bookingService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(
                new Object[]{"Booking #", "Customer ID", "Room #", "Check-in", "Check-out", "Total (Rs.)", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        JButton checkInBtn = new JButton("Check-in Selected");
        checkInBtn.addActionListener(e -> doAction(bookingService::checkIn));
        JButton checkOutBtn = new JButton("Check-out Selected");
        checkOutBtn.addActionListener(e -> doAction(bookingService::checkOut));
        JButton cancelBtn = new JButton("Cancel Selected");
        cancelBtn.addActionListener(e -> doAction(bookingService::cancelBooking));

        bottom.add(refreshBtn);
        bottom.add(checkInBtn);
        bottom.add(checkOutBtn);
        bottom.add(cancelBtn);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Booking b : bookingService.getBookingDAO().findAll()) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(), b.getCustomerId(), b.getRoomNumber(),
                    b.getCheckInDate(), b.getCheckOutDate(), b.getTotalAmount(), b.getStatus()
            });
        }
    }

    private interface BookingAction {
        boolean apply(int bookingId);
    }

    private void doAction(BookingAction action) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking first.");
            return;
        }
        int bookingId = (int) tableModel.getValueAt(row, 0);
        boolean ok = action.apply(bookingId);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Action could not be completed for this booking's current status.");
        }
        refresh();
    }
}
