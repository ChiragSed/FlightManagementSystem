package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerDashboard extends JFrame {
    private int userId;
    private JTable flightsTable, bookingsTable;
    private DefaultTableModel flightsModel, bookingsModel;
    private JButton btnBook, btnCancel;
    private JLabel lblPrice, lblWelcome;

    public CustomerDashboard(int userId) {
        this.userId = userId;

        setTitle("Customer Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(null);

        // Header panel
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 900, 50);
        header.setBackground(new Color(0, 102, 204));
        add(header);

        lblWelcome = new JLabel();
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWelcome.setBounds(20, 10, 850, 30);
        header.add(lblWelcome);

        setWelcomeMessage();

        // Tabs
        JTabbedPane tabPane = new JTabbedPane();
        tabPane.setBounds(20, 60, 850, 500);
        add(tabPane);

        // Available Flights Tab
        JPanel flightsPanel = new JPanel(null);
        tabPane.addTab("Available Flights", flightsPanel);

        flightsModel = new DefaultTableModel(
                new String[]{"Flight ID", "Flight No", "Source", "Destination", "Date", "Price"}, 0);
        flightsTable = new JTable(flightsModel);
        JScrollPane spFlights = new JScrollPane(flightsTable);
        spFlights.setBounds(0, 0, 830, 350);
        flightsPanel.add(spFlights);

        lblPrice = new JLabel("Selected Flight Price: NPR0.00");
        lblPrice.setBounds(10, 360, 300, 25);
        flightsPanel.add(lblPrice);

        btnBook = new JButton("Book Selected Flight");
        btnBook.setBounds(320, 360, 200, 35);
        btnBook.setBackground(new Color(0, 102, 204));
        btnBook.setForeground(Color.WHITE);
        btnBook.setFocusPainted(false);
        flightsPanel.add(btnBook);
        btnBook.addActionListener(e -> bookSelectedFlight());

        flightsTable.getSelectionModel().addListSelectionListener(e -> updatePrice());

        // My Bookings Tab
        JPanel bookingsPanel = new JPanel(null);
        tabPane.addTab("My Bookings", bookingsPanel);

        bookingsModel = new DefaultTableModel(
                new String[]{"Booking ID", "Flight No", "Source", "Destination", "Date", "Price"}, 0);
        bookingsTable = new JTable(bookingsModel);
        JScrollPane spBookings = new JScrollPane(bookingsTable);
        spBookings.setBounds(0, 0, 830, 400);
        bookingsPanel.add(spBookings);

        btnCancel = new JButton("Cancel Selected Booking");
        btnCancel.setBounds(320, 410, 200, 35);
        btnCancel.setBackground(new Color(204, 0, 0));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        bookingsPanel.add(btnCancel);
        btnCancel.addActionListener(e -> cancelSelectedBooking());

        loadAvailableFlights();
        loadMyBookings();
    }

    private void setWelcomeMessage() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.username, c.name FROM users u " +
                         "JOIN customers c ON u.customer_id = c.id " +
                         "WHERE u.id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String username = rs.getString("username");
                lblWelcome.setText("Welcome " + name + " (" + username + ")");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadAvailableFlights() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM flights WHERE id NOT IN " +
                    "(SELECT flight_id FROM bookings WHERE customer_id=(SELECT customer_id FROM users WHERE id=?))";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            flightsModel.setRowCount(0);
            while (rs.next()) {
                flightsModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("flight_no"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("date"),
                        rs.getDouble("price")
                });
            }
            lblPrice.setText("Selected Flight Price: $0.00");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadMyBookings() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT b.id AS booking_id, f.flight_no, f.source, f.destination, f.date, f.price " +
                    "FROM bookings b JOIN flights f ON b.flight_id=f.id " +
                    "WHERE b.customer_id=(SELECT customer_id FROM users WHERE id=?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            bookingsModel.setRowCount(0);
            while (rs.next()) {
                bookingsModel.addRow(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("flight_no"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("date"),
                        rs.getDouble("price")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updatePrice() {
        int row = flightsTable.getSelectedRow();
        if (row != -1) {
            double price = (double) flightsModel.getValueAt(row, 5);
            lblPrice.setText("Selected Flight Price: $" + price);
        } else {
            lblPrice.setText("Selected Flight Price: $0.00");
        }
    }

    private void bookSelectedFlight() {
        int row = flightsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight to book", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int flightId = (int) flightsModel.getValueAt(row, 0);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO bookings(customer_id, flight_id) VALUES ((SELECT customer_id FROM users WHERE id=?), ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, flightId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Flight booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAvailableFlights();
            loadMyBookings();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cancelSelectedBooking() {
        int row = bookingsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int bookingId = (int) bookingsModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM bookings WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Booking cancelled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAvailableFlights();
            loadMyBookings();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        new CustomerDashboard(2).setVisible(true); // Replace 2 with actual userId
    }
}
