package ui;

import model.Room;
import model.RoomType;
import service.BookingService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RoomPanel extends JPanel {

    private final BookingService bookingService;
    private final DefaultTableModel tableModel;
    private final JTable table;

    public RoomPanel(BookingService bookingService, boolean isAdmin) {
        this.bookingService = bookingService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new DefaultTableModel(
                new Object[]{"Room #", "Type", "Floor", "Price/Night", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(refreshBtn);

        if (isAdmin) {
            JButton addBtn = new JButton("Add Room");
            addBtn.addActionListener(e -> addRoom());
            JButton deleteBtn = new JButton("Delete Selected Room");
            deleteBtn.addActionListener(e -> deleteSelectedRoom());
            bottom.add(addBtn);
            bottom.add(deleteBtn);
        }

        add(bottom, BorderLayout.SOUTH);
        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        for (Room r : bookingService.getRoomDAO().findAll()) {
            tableModel.addRow(new Object[]{
                    r.getRoomNumber(), r.getType(), r.getFloor(),
                    "Rs. " + r.getPricePerNight(),
                    r.isAvailable() ? "Available" : "Occupied"
            });
        }
    }

    private void addRoom() {
        JTextField numberField = new JTextField();
        JComboBox<RoomType> typeBox = new JComboBox<>(RoomType.values());
        JTextField floorField = new JTextField();

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Room Number:")); form.add(numberField);
        form.add(new JLabel("Room Type:")); form.add(typeBox);
        form.add(new JLabel("Floor:")); form.add(floorField);

        int result = JOptionPane.showConfirmDialog(this, form, "Add New Room", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int number = Integer.parseInt(numberField.getText().trim());
            int floor = Integer.parseInt(floorField.getText().trim());
            RoomType type = (RoomType) typeBox.getSelectedItem();

            if (bookingService.getRoomDAO().findByNumber(number) != null) {
                JOptionPane.showMessageDialog(this, "A room with that number already exists.");
                return;
            }

            bookingService.getRoomDAO().addRoom(new Room(number, type, floor, true));
            refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Room number and floor must be numeric.");
        }
    }

    private void deleteSelectedRoom() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a room first.");
            return;
        }
        int roomNumber = (int) tableModel.getValueAt(row, 0);
        bookingService.getRoomDAO().deleteRoom(roomNumber);
        refresh();
    }
}
