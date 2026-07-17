package ui;

import model.Booking;
import model.BookingStatus;
import service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MyBookingsPanel extends JPanel {

    private final BookingService bookingService;
    private final int customerId;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public MyBookingsPanel(BookingService bookingService, int customerId) {
        this.bookingService = bookingService;
        this.customerId = customerId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(
                new Object[]{"Booking #", "Room #", "Check-in", "Check-out", "Total (Rs.)", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        JButton cancelBtn = new JButton("Cancel Selected Booking");
        cancelBtn.addActionListener(e -> cancelSelected());
        bottom.add(refreshBtn);
        bottom.add(cancelBtn);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Booking b : bookingService.getBookingDAO().findByCustomer(customerId)) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(), b.getRoomNumber(), b.getCheckInDate(),
                    b.getCheckOutDate(), b.getTotalAmount(), b.getStatus()
            });
        }
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking first.");
            return;
        }
        int bookingId = (int) tableModel.getValueAt(row, 0);
        BookingStatus status = (BookingStatus) tableModel.getValueAt(row, 5);
        if (status == BookingStatus.CHECKED_OUT) {
            JOptionPane.showMessageDialog(this, "Cannot cancel a completed stay.");
            return;
        }
        boolean ok = bookingService.cancelBooking(bookingId);
        JOptionPane.showMessageDialog(this, ok ? "Booking cancelled." : "Could not cancel booking.");
        refresh();
    }
}
