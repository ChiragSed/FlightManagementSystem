package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class ViewCustomersWithBookings extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ViewCustomersWithBookings() {
        setTitle("Customers & Bookings");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Layered Pane to hold background and content ---
        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);


        // --- Main Content Panel (transparent) ---
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false); // transparent to show background
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBounds(0, 0, 1000, 500);
        layeredPane.add(contentPanel, JLayeredPane.PALETTE_LAYER);

        // --- Header Panel ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 204, 200)); // semi-transparent
        headerPanel.setPreferredSize(new Dimension(1000, 70));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        JLabel titleLabel = new JLabel("Customers & Bookings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Optional icon
        ImageIcon icon = new ImageIcon("images/customer_icon.png"); // your path
        Image ic = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        titleLabel.setIcon(new ImageIcon(ic));
        titleLabel.setIconTextGap(15);

        headerPanel.add(titleLabel);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Table with ScrollPane ---
        model = new DefaultTableModel(new String[]{
                "Customer ID","Name","Email","Username","Password",
                "Booking ID","Flight No","Source","Destination","Date","Price"
        }, 0);

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Load Data
        loadCustomerBookings();
        styleTable();

        // --- Right-click menu to cancel booking ---
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem cancelItem = new JMenuItem("Cancel Booking");
        popupMenu.add(cancelItem);

        table.setComponentPopupMenu(popupMenu);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e){
                int row = table.rowAtPoint(e.getPoint());
                table.getSelectionModel().setSelectionInterval(row,row);
            }
        });

        cancelItem.addActionListener(e -> cancelSelectedBooking());
    }

    private void loadCustomerBookings() {
        try(Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.id AS CustomerID, c.name, c.email, u.username, u.password, " +
                    "b.id AS BookingID, f.flight_no, f.source, f.destination, f.date, f.price " +
                    "FROM customers c " +
                    "JOIN users u ON u.customer_id = c.id AND u.role='customer' " +
                    "LEFT JOIN bookings b ON b.customer_id = c.id " +
                    "LEFT JOIN flights f ON b.flight_id = f.id " +
                    "ORDER BY c.id";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            model.setRowCount(0); // clear table
            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("CustomerID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("BookingID") == 0 ? "" : rs.getInt("BookingID"),
                        rs.getString("flight_no"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDate("date"),
                        rs.getDouble("price")
                });
            }

        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Failed to load data","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelectedBooking() {
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this,"Please select a booking to cancel","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object bookingObj = model.getValueAt(row,5);
        if(bookingObj == null || bookingObj.toString().isEmpty()){
            JOptionPane.showMessageDialog(this,"No booking selected to cancel","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = Integer.parseInt(bookingObj.toString());

        int confirm = JOptionPane.showConfirmDialog(this,"Are you sure you want to cancel this booking?","Confirm",JOptionPane.YES_NO_OPTION);
        if(confirm != JOptionPane.YES_OPTION) return;

        try(Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM bookings WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookingId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Booking cancelled successfully","Success",JOptionPane.INFORMATION_MESSAGE);
            loadCustomerBookings(); // refresh table
        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Failed to cancel booking","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleTable() {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(0,153,255));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0,102,204));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ViewCustomersWithBookings().setVisible(true));
    }
}
