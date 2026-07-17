package ui;

import model.Customer;
import service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerPanel extends JPanel {

    private final BookingService bookingService;
    private final DefaultTableModel tableModel;

    public CustomerPanel(BookingService bookingService) {
        this.bookingService = bookingService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Email", "Phone", "Address", "# Bookings"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Customer c : bookingService.getCustomerDAO().findAll()) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                    c.getAddress(), c.getBookingIds().size()
            });
        }
    }
}
