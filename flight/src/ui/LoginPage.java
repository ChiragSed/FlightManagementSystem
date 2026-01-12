package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/* ==================== LOGIN PAGE ==================== */
public class LoginPage extends JFrame {

    private RoundedTextField txtUsername;
    private RoundedPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkShowPassword;

    public LoginPage() {
        setTitle("Airline Management System");
        setSize(460, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(null);
        setContentPane(panel);

        /* ---------- Circular Airline Logo ---------- */
        JLabel lblLogo = new JLabel(new CircularImageIcon("/images/airline_logo.jpg", 80));
        lblLogo.setBounds(190, 10, 80, 80);
        panel.add(lblLogo);

        /* ---------- Welcome Box ---------- */
        JPanel welcomeBox = new JPanel(null);
        welcomeBox.setBounds(65, 95, 330, 32);
        welcomeBox.setBackground(new Color(0, 0, 0, 180));
        JLabel lblWelcome = new JLabel("Welcome to Airline Management System");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBounds(5, 5, 320, 22);
        welcomeBox.add(lblWelcome);
        panel.add(welcomeBox);

        /* ---------- Username ---------- */
        panel.add(labelBox("Username:", 65, 140));
        txtUsername = new RoundedTextField(20);
        txtUsername.setBounds(170, 140, 220, 30);
        panel.add(txtUsername);

        /* ---------- Password ---------- */
        panel.add(labelBox("Password:", 65, 185));
        txtPassword = new RoundedPasswordField(20);
        txtPassword.setBounds(170, 185, 220, 30);
        panel.add(txtPassword);

        /* ---------- Show Password ---------- */
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setBounds(170, 220, 150, 20);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setForeground(Color.WHITE);
        chkShowPassword.addActionListener(e ->
                txtPassword.setEchoChar(
                        chkShowPassword.isSelected() ? (char) 0 : '•'));
        panel.add(chkShowPassword);

        /* ---------- Login Button ---------- */
        btnLogin = new JButton("Login");
        btnLogin.setBounds(185, 255, 100, 36);
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(0, 140, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(0, 102, 204));
            }
        });
        panel.add(btnLogin);
        btnLogin.addActionListener(e -> login());

        /* ---------- Signup Button ---------- */
        JButton btnSignup = new JButton("Sign Up");
        btnSignup.setBounds(185, 300, 100, 36);
        btnSignup.setBackground(new Color(0, 153, 76));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFocusPainted(false);
        btnSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSignup.setBackground(new Color(0, 180, 100));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnSignup.setBackground(new Color(0, 153, 76));
            }
        });
        panel.add(btnSignup);
        btnSignup.addActionListener(e -> new SignupPage().setVisible(true));
    }

    private void login() {
        String u = txtUsername.getText();
        String p = new String(txtPassword.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps =
                    conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, u);
            ps.setString(2, p);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this,
                        role.equalsIgnoreCase("admin")
                                ? "Welcome Admin 👨‍✈️"
                                : "Welcome Customer ✈️");

                if (role.equalsIgnoreCase("admin"))
                    new AdminDashboard().setVisible(true);
                else
                    new CustomerDashboard(rs.getInt("id")).setVisible(true);

                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JPanel labelBox(String text, int x, int y) {
        JPanel p = new JPanel(null);
        p.setBounds(x, y, 95, 28);
        p.setBackground(new Color(0, 0, 0, 180));
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setBounds(5, 4, 85, 20);
        p.add(l);
        return p;
    }

    public static void main(String[] args) {
        new LoginPage().setVisible(true);
    }

    /* ================= SUPPORT CLASSES ================= */
    class BackgroundPanel extends JPanel {
        Image bg = new ImageIcon(LoginPage.class.getResource("/images/login_bg.png")).getImage();
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    class RoundedTextField extends JTextField {
        int r;
        RoundedTextField(int r) { this.r = r; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); }
        @Override
        protected void paintComponent(Graphics g) { g.setColor(Color.WHITE); g.fillRoundRect(0, 0, getWidth(), getHeight(), r, r); super.paintComponent(g); }
    }

    class RoundedPasswordField extends JPasswordField {
        int r;
        RoundedPasswordField(int r) { this.r = r; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); }
        @Override
        protected void paintComponent(Graphics g) { g.setColor(Color.WHITE); g.fillRoundRect(0, 0, getWidth(), getHeight(), r, r); super.paintComponent(g); }
    }

    class CircularImageIcon extends ImageIcon {
        public CircularImageIcon(String path, int size) {
            super(new ImageIcon(LoginPage.class.getResource(path)).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }
        @Override
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setClip(new Ellipse2D.Float(x, y, getIconWidth(), getIconHeight()));
            super.paintIcon(c, g2, x, y);
        }
    }
}

/* ================= SIGNUP PAGE ================= */
class SignupPage extends JFrame {

    private RoundedTextField txtName, txtEmail, txtUsername;
    private RoundedPasswordField txtPassword;
    private JButton btnSignup;

    public SignupPage() {
        setTitle("Customer Sign Up");
        setSize(460, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel panel = new BackgroundPanel();
        panel.setLayout(null);
        setContentPane(panel);

        JLabel lblLogo = new JLabel(new CircularImageIcon("/images/airline_logo.jpg", 80));
        lblLogo.setBounds(190, 10, 80, 80);
        panel.add(lblLogo);

        JPanel welcomeBox = new JPanel(null);
        welcomeBox.setBounds(65, 95, 330, 32);
        welcomeBox.setBackground(new Color(0, 0, 0, 180));
        JLabel lblWelcome = new JLabel("Customer Sign Up");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBounds(5, 5, 320, 22);
        welcomeBox.add(lblWelcome);
        panel.add(welcomeBox);

        panel.add(labelBox("Name:", 65, 140));
        txtName = new RoundedTextField(20);
        txtName.setBounds(170, 140, 220, 30);
        panel.add(txtName);

        panel.add(labelBox("Email:", 65, 185));
        txtEmail = new RoundedTextField(20);
        txtEmail.setBounds(170, 185, 220, 30);
        panel.add(txtEmail);

        panel.add(labelBox("Username:", 65, 230));
        txtUsername = new RoundedTextField(20);
        txtUsername.setBounds(170, 230, 220, 30);
        panel.add(txtUsername);

        panel.add(labelBox("Password:", 65, 275));
        txtPassword = new RoundedPasswordField(20);
        txtPassword.setBounds(170, 275, 220, 30);
        panel.add(txtPassword);

        btnSignup = new JButton("Sign Up");
        btnSignup.setBounds(185, 320, 100, 36);
        btnSignup.setBackground(new Color(0, 102, 204));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFocusPainted(false);
        btnSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSignup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnSignup.setBackground(new Color(0,140,255)); }
            @Override
            public void mouseExited(MouseEvent e) { btnSignup.setBackground(new Color(0,102,204)); }
        });
        panel.add(btnSignup);
        btnSignup.addActionListener(e -> signup());
    }

    private void signup() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if(name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Please fill all fields","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String customerSQL = "INSERT INTO customers(name,email) VALUES(?,?)";
            PreparedStatement psCustomer = conn.prepareStatement(customerSQL);
            psCustomer.setString(1,name);
            psCustomer.setString(2,email);
            psCustomer.executeUpdate();

            ResultSet rs = conn.createStatement().executeQuery("SELECT LAST_INSERT_ID()");
            int customerId = 0;
            if(rs.next()) customerId = rs.getInt(1);

            String userSQL = "INSERT INTO users(username,password,role,customer_id) VALUES(?,?, 'customer', ?)";
            PreparedStatement psUser = conn.prepareStatement(userSQL);
            psUser.setString(1,username);
            psUser.setString(2,password);
            psUser.setInt(3,customerId);
            psUser.executeUpdate();

            JOptionPane.showMessageDialog(this,"Account created successfully!","Success",JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Failed to create account","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel labelBox(String text, int x, int y) {
        JPanel p = new JPanel(null);
        p.setBounds(x,y,95,28);
        p.setBackground(new Color(0,0,0,180));
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setBounds(5,4,85,20);
        p.add(l);
        return p;
    }

    /* ---------- Reuse UI Classes ---------- */
    class BackgroundPanel extends JPanel {
        Image bg = new ImageIcon(SignupPage.class.getResource("/images/login_bg.png")).getImage();
        @Override
        protected void paintComponent(Graphics g) { super.paintComponent(g); g.drawImage(bg,0,0,getWidth(),getHeight(),this); }
    }

    class RoundedTextField extends JTextField {
        int r;
        RoundedTextField(int r) { this.r=r; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(5,10,5,10)); }
        @Override
        protected void paintComponent(Graphics g) { g.setColor(Color.WHITE); g.fillRoundRect(0,0,getWidth(),getHeight(),r,r); super.paintComponent(g); }
    }

    class RoundedPasswordField extends JPasswordField {
        int r;
        RoundedPasswordField(int r) { this.r=r; setOpaque(false); setBorder(BorderFactory.createEmptyBorder(5,10,5,10)); }
        @Override
        protected void paintComponent(Graphics g) { g.setColor(Color.WHITE); g.fillRoundRect(0,0,getWidth(),getHeight(),r,r); super.paintComponent(g); }
    }

    class CircularImageIcon extends ImageIcon {
        public CircularImageIcon(String path,int size) {
            super(new ImageIcon(SignupPage.class.getResource(path)).getImage().getScaledInstance(size,size,Image.SCALE_SMOOTH));
        }
        
        @Override
        public synchronized void paintIcon(Component c,Graphics g,int x,int y) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setClip(new Ellipse2D.Float(x,y,getIconWidth(),getIconHeight()));
            super.paintIcon(c,g2,x,y);
        }
    }
}
