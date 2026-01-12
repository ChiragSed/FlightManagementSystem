package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AddCustomer extends JFrame {

    private RoundedTextField txtName, txtEmail, txtUsername, txtPassword;
    private JButton btnSave;

    public AddCustomer() {
        setTitle("Add Customer");
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

        JLabel lblTitle = new JLabel("Add Customer");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBounds(120, 10, 250, 30);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lblTitle);
        panel.add(header);

        int labelX = 50, fieldX = 170, fieldWidth = 220, fieldHeight = 30;
        int startY = 80, spacingY = 60;

        /* ---------- Name ---------- */
        panel.add(labelBox("Name:", labelX, startY));
        txtName = new RoundedTextField(20);
        txtName.setBounds(fieldX, startY, fieldWidth, fieldHeight);
        panel.add(txtName);

        /* ---------- Email ---------- */
        panel.add(labelBox("Email:", labelX, startY + spacingY));
        txtEmail = new RoundedTextField(20);
        txtEmail.setBounds(fieldX, startY + spacingY, fieldWidth, fieldHeight);
        panel.add(txtEmail);

        /* ---------- Username ---------- */
        panel.add(labelBox("Username:", labelX, startY + spacingY * 2));
        txtUsername = new RoundedTextField(20);
        txtUsername.setBounds(fieldX, startY + spacingY * 2, fieldWidth, fieldHeight);
        panel.add(txtUsername);

        /* ---------- Password ---------- */
        panel.add(labelBox("Password:", labelX, startY + spacingY * 3));
        txtPassword = new RoundedTextField(20);
        txtPassword.setBounds(fieldX, startY + spacingY * 3, fieldWidth, fieldHeight);
        panel.add(txtPassword);

        /* ---------- Save Button ---------- */
        btnSave = createButton("Save Customer", 135, startY + spacingY * 4, 180, 40, new Color(0, 102, 204));
        panel.add(btnSave);
        btnSave.addActionListener(e -> saveCustomer());
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
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(color.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    private void saveCustomer() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if(name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please fill all fields","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        try(Connection conn = DBConnection.getConnection()) {
            // Insert into customers
            String sqlCustomer = "INSERT INTO customers(name,email) VALUES(?,?)";
            PreparedStatement psCustomer = conn.prepareStatement(sqlCustomer, Statement.RETURN_GENERATED_KEYS);
            psCustomer.setString(1, name);
            psCustomer.setString(2, email);
            psCustomer.executeUpdate();

            ResultSet rs = psCustomer.getGeneratedKeys();
            int customerId = 0;
            if(rs.next()) customerId = rs.getInt(1);

            // Insert into users
            String sqlUser = "INSERT INTO users(username,password,role,customer_id) VALUES(?,?,?,?)";
            PreparedStatement psUser = conn.prepareStatement(sqlUser);
            psUser.setString(1, username);
            psUser.setString(2, password);
            psUser.setString(3, "customer");
            psUser.setInt(4, customerId);
            psUser.executeUpdate();

            JOptionPane.showMessageDialog(this,"Customer added successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
            this.dispose();

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Failed to add customer!","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ---------- Background Panel ---------- */
    class BackgroundPanel extends JPanel {
        Image bg = new ImageIcon(AddCustomer.class.getResource("/images/login_bg.png")).getImage();
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
        new AddCustomer().setVisible(true);
    }
}
