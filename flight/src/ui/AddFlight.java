package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddFlight extends JFrame {

    private RoundedTextField txtFlightNo, txtSource, txtDestination, txtDate, txtPrice;
    private JButton btnSave;

    public AddFlight() {
        setTitle("Add Flight");
        setSize(450, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(null);
        setContentPane(panel);

        /* ---------- Header ---------- */
        JPanel header = new JPanel(null);
        header.setBounds(0, 10, 450, 50);
        header.setBackground(new Color(0, 0, 0, 180));

        JLabel lblTitle = new JLabel("Add Flight");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBounds(120, 10, 250, 30);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lblTitle);
        panel.add(header);

        int labelX = 50, fieldX = 170, fieldWidth = 220, fieldHeight = 30;
        int startY = 80, spacingY = 60;

        /* ---------- Flight No ---------- */
        panel.add(labelBox("Flight No:", labelX, startY));
        txtFlightNo = new RoundedTextField(20);
        txtFlightNo.setBounds(fieldX, startY, fieldWidth, fieldHeight);
        panel.add(txtFlightNo);

        /* ---------- Source ---------- */
        panel.add(labelBox("Source:", labelX, startY + spacingY));
        txtSource = new RoundedTextField(20);
        txtSource.setBounds(fieldX, startY + spacingY, fieldWidth, fieldHeight);
        panel.add(txtSource);

        /* ---------- Destination ---------- */
        panel.add(labelBox("Destination:", labelX, startY + spacingY * 2));
        txtDestination = new RoundedTextField(20);
        txtDestination.setBounds(fieldX, startY + spacingY * 2, fieldWidth, fieldHeight);
        panel.add(txtDestination);

        /* ---------- Date ---------- */
        panel.add(labelBox("Date(YYYY-MM-DD):", labelX, startY + spacingY * 3));
        txtDate = new RoundedTextField(20);
        txtDate.setBounds(fieldX, startY + spacingY * 3, fieldWidth, fieldHeight);
        panel.add(txtDate);

        /* ---------- Price ---------- */
        panel.add(labelBox("Price:", labelX, startY + spacingY * 4));
        txtPrice = new RoundedTextField(20);
        txtPrice.setBounds(fieldX, startY + spacingY * 4, fieldWidth, fieldHeight);
        panel.add(txtPrice);

        /* ---------- Save Button ---------- */
        btnSave = createButton("Save Flight", 135, startY + spacingY * 5, 180, 40, new Color(0, 102, 204));
        panel.add(btnSave);
        btnSave.addActionListener(e -> saveFlight());
    }

    private JPanel labelBox(String text, int x, int y) {
        JPanel p = new JPanel(null);
        p.setBounds(x, y, 110, 30);
        p.setBackground(new Color(0, 0, 0, 180));
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setBounds(5, 4, 100, 22);
        p.add(l);
        return p;
    }

    private JButton createButton(String text, int x, int y, int w, int h, Color color) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(color.darker()); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    private void saveFlight() {
        String flightNo = txtFlightNo.getText().trim();
        String source = txtSource.getText().trim();
        String dest = txtDestination.getText().trim();
        String date = txtDate.getText().trim();
        String priceText = txtPrice.getText().trim();

        if(flightNo.isEmpty() || source.isEmpty() || dest.isEmpty() || date.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if(price < 0) throw new NumberFormatException();
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price value", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO flights(flight_no, source, destination, date, price) VALUES(?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, flightNo);
            ps.setString(2, source);
            ps.setString(3, dest);
            ps.setDate(4, Date.valueOf(date)); // convert string to SQL Date
            ps.setDouble(5, price);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Flight added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to add flight!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /* ---------- Background Panel ---------- */
    class BackgroundPanel extends JPanel {
        Image bg = new ImageIcon(AddFlight.class.getResource("/images/login_bg.png")).getImage();
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg,0,0,getWidth(),getHeight(),this);
        }
    }

    /* ---------- Rounded Text Field ---------- */
    class RoundedTextField extends JTextField {
        int r;
        RoundedTextField(int r) {
            this.r = r;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        }
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        new AddFlight().setVisible(true);
    }
}
