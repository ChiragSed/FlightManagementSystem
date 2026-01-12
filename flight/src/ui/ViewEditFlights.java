package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewEditFlights extends JFrame {
    private JTable flightsTable;
    private DefaultTableModel model;
    private JButton btnUpdatePrice;

    public ViewEditFlights() {
        setTitle("View & Edit Flights");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(null);

        // Background Panel
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(245, 245, 245));
        setContentPane(panel);

        // Header box
        JPanel headerBox = new JPanel(null);
        headerBox.setBounds(0, 0, 900, 70);
        headerBox.setBackground(new Color(0, 102, 204));
        panel.add(headerBox);

        JLabel lblTitle = new JLabel("View & Edit Flights");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(30, 20, 400, 30);
        headerBox.add(lblTitle);

        // Table
        model = new DefaultTableModel(new String[]{"Flight ID", "Flight No", "Source", "Destination", "Date", "Price"}, 0);
        flightsTable = new JTable(model);
        flightsTable.setRowHeight(25);
        flightsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBounds(20, 90, 850, 300);
        panel.add(scrollPane);

        // Update Button
        btnUpdatePrice = new JButton("Update Selected Flight Price");
        btnUpdatePrice.setBounds(300, 410, 300, 40);
        btnUpdatePrice.setBackground(new Color(0, 102, 204));
        btnUpdatePrice.setForeground(Color.WHITE);
        btnUpdatePrice.setFocusPainted(false);
        btnUpdatePrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnUpdatePrice.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(btnUpdatePrice);

        // Hover effect
        btnUpdatePrice.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnUpdatePrice.setBackground(new Color(0, 140, 255)); }
            public void mouseExited(java.awt.event.MouseEvent e) { btnUpdatePrice.setBackground(new Color(0, 102, 204)); }
        });

        btnUpdatePrice.addActionListener(e -> updateSelectedFlightPrice());

        loadFlights();
    }

    private void loadFlights() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM flights";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("flight_no"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("date"),
                        rs.getDouble("price")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateSelectedFlightPrice() {
        int row = flightsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a flight", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int flightId = (int) model.getValueAt(row, 0);
        String flightNo = (String) model.getValueAt(row, 1);

        String newPriceStr = JOptionPane.showInputDialog(this, "Enter new price for flight " + flightNo + ":");
        if (newPriceStr == null) return; // cancelled
        double newPrice;
        try {
            newPrice = Double.parseDouble(newPriceStr);
            if (newPrice < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid price", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE flights SET price=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, newPrice);
            ps.setInt(2, flightId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Price updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadFlights();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        new ViewEditFlights().setVisible(true);
    }
}
