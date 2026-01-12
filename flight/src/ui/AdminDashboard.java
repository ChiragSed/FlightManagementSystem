package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Background Panel with image
        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(null);
        setContentPane(panel);

        /* ---------- Header ---------- */
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 900, 70);
        header.setBackground(new Color(0, 0, 0, 180));

        JLabel lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBounds(30, 20, 300, 30);
        header.add(lblTitle);

        panel.add(header);

        /* ---------- Buttons ---------- */
        panel.add(createCardButton(
                "Add Customer", "/images/add_customer.png",
                60, 100, e -> new AddCustomer().setVisible(true)));

        panel.add(createCardButton(
                "Add Flights", "/images/add_flight.png",
                60, 190, e -> new AddFlight().setVisible(true)));

        panel.add(createCardButton(
                "View / Edit Flights", "/images/view_flight.png",
                60, 280, e -> new ViewEditFlights().setVisible(true)));

        panel.add(createCardButton(
                "View Customers & Bookings", "/images/view_customers.jpg",
                60, 370, e -> new ViewCustomersWithBookings().setVisible(true)));

        /* ---------- Logout ---------- */
        JButton btnLogout = createCardButton(
                "Logout", "/images/logout.png",
                650, 420, e -> {
                    dispose();
                    new LoginPage().setVisible(true);
                });
        btnLogout.setBackground(new Color(200, 50, 50));
        panel.add(btnLogout);
    }

    /* ---------- Modern Card Button ---------- */
    private JButton createCardButton(
            String text, String iconPath, int x, int y,
            java.awt.event.ActionListener action) {

        JButton btn = new JButton(text);
        btn.setBounds(x, y, 300, 65);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 102, 204));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
        } catch (Exception ignored) {}

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(0, 140, 255));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(0, 102, 204));
            }
        });

        btn.addActionListener(action);
        return btn;
    }

    /* ---------- Background Panel ---------- */
    class BackgroundPanel extends JPanel {
        Image bg = new ImageIcon(AdminDashboard.class.getResource("/images/login_bg.png")).getImage();

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        new AdminDashboard().setVisible(true);
    }
}
