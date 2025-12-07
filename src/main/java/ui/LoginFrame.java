package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    private final Color BG_TOP = new Color(5, 10, 35);
    private final Color BG_BOTTOM = new Color(5, 5, 20);
    private final Color CYAN = new Color(0, 220, 255);
    private final Color PURPLE = new Color(160, 80, 255);
    private final Color CARD_TOP = new Color(25, 35, 70, 230);
    private final Color CARD_BOTTOM = new Color(15, 20, 45, 240);
    private final Color TEXT_WHITE = Color.WHITE;

    public LoginFrame() {
        setTitle("EventSphereX");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(false);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Image logo = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo.png"));
        setIconImage(logo);


        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint bg = new GradientPaint(0, 0, BG_TOP, getWidth(), getHeight(), BG_BOTTOM);
                g2.setPaint(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setPaint(new RadialGradientPaint(
                        new Point(getWidth() / 4, getHeight() / 3),
                        getWidth() / 3f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0, 220, 255, 160), new Color(0, 0, 0, 0)}
                ));
                g2.fillOval(getWidth() / 4 - getWidth() / 3, getHeight() / 3 - getWidth() / 3, getWidth() * 2 / 3, getWidth() * 2 / 3);

                g2.setPaint(new RadialGradientPaint(
                        new Point(getWidth() * 3 / 4, getHeight() / 4),
                        getWidth() / 3f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(170, 80, 255, 160), new Color(0, 0, 0, 0)}
                ));
                g2.fillOval(getWidth() * 3 / 4 - getWidth() / 3, getHeight() / 4 - getWidth() / 3, getWidth() * 2 / 3, getWidth() * 2 / 3);

                g2.dispose();
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel);

        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, CARD_TOP, 0, getHeight(), CARD_BOTTOM);
                Shape rr = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setPaint(gp);
                g2.fill(rr);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.draw(rr);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        loginCard.setOpaque(false);
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setPreferredSize(new Dimension(420, 560));
        loginCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));
        Image scaled = icon.getImage().getScaledInstance(95, 95, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));

        JLabel titleLabel = new JLabel("EventSphereX");
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage your events with ease");
        subtitleLabel.setForeground(new Color(200, 200, 220));
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = createStyledField();
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 110)),
                "UserName",
                0,
                0,
                new Font("Segoe UI", Font.PLAIN, 12),
                new Color(180, 185, 210)
        ));

        JPasswordField passField = createStyledPasswordField();
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 110)),
                "Password",
                0,
                0,
                new Font("Segoe UI", Font.PLAIN, 12),
                new Color(180, 185, 210)
        ));

        JButton loginBtn = createPrimaryButton("Sign In");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerBtn = createSecondaryButton("Create Account");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(logoLabel);
        loginCard.add(Box.createVerticalStrut(20));
        loginCard.add(titleLabel);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(subtitleLabel);
        loginCard.add(Box.createVerticalStrut(35));
        loginCard.add(usernameField);
        loginCard.add(Box.createVerticalStrut(18));
        loginCard.add(passField);
        loginCard.add(Box.createVerticalStrut(30));
        loginCard.add(loginBtn);
        loginCard.add(Box.createVerticalStrut(25));
        loginCard.add(registerBtn);

        mainPanel.add(loginCard);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passField.getPassword());
            UserDAO dao = new UserDAO();
            User user = dao.login(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user.getFullName());
                new DashboardFrame(user).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Email or Password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> showRegisterDialog());

        MouseAdapter ma = new MouseAdapter() {
            int lastX, lastY;
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getXOnScreen();
                lastY = e.getYOnScreen();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(getLocation().x + (x - lastX), getLocation().y + (y - lastY));
                lastX = x;
                lastY = y;
            }
        };
        mainPanel.addMouseListener(ma);
        mainPanel.addMouseMotionListener(ma);
    }

    private void showRegisterDialog() {
        JDialog d = new JDialog(this, "SignUP", true);
        d.setSize(480, 530);
        d.setLocationRelativeTo(this);
        d.setLayout(new GridBagLayout());
        d.getContentPane().setBackground(new Color(15, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField regUser = createStyledField();
        JTextField regFull = createStyledField();
        JTextField regEmail = createStyledField();
        JPasswordField regPass = createStyledPasswordField();

        addLabel(d, "Create a Username:", gbc, gbc.gridy);
        gbc.gridy++;
        d.add(regUser, gbc);
        gbc.gridy++;

        addLabel(d, "Full Name:", gbc, gbc.gridy);
        gbc.gridy++;
        d.add(regFull, gbc);
        gbc.gridy++;

        addLabel(d, "Email Address:", gbc, gbc.gridy);
        gbc.gridy++;
        d.add(regEmail, gbc);
        gbc.gridy++;

        addLabel(d, "Password:", gbc, gbc.gridy);
        gbc.gridy++;
        d.add(regPass, gbc);
        gbc.gridy++;

        JButton submit = createPrimaryButton("Complete Registration");
        submit.setPreferredSize(new Dimension(320, 48));
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        d.add(submit, gbc);

        submit.addActionListener(ev -> {
            if (regUser.getText().isEmpty()
                    || regFull.getText().isEmpty()
                    || regEmail.getText().isEmpty()
                    || String.valueOf(regPass.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(d, "Please fill all fields");
                return;
            }

            UserDAO dao = new UserDAO();
            boolean success = dao.register(
                    regUser.getText(),
                    regFull.getText(),
                    regEmail.getText(),
                    String.valueOf(regPass.getPassword())
            );

            if (success) {
                JOptionPane.showMessageDialog(d, "Account Created! You can now Sign In.");
                d.dispose();
            } else {
                JOptionPane.showMessageDialog(d, "Username or Email already exists.");
            }
        });

        d.setVisible(true);
    }

    private void addLabel(JDialog d, String text, GridBagConstraints gbc, int y) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        GridBagConstraints g = (GridBagConstraints) gbc.clone();
        g.gridy = y;
        d.add(l, g);
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(320, 44));
        field.setMaximumSize(new Dimension(320, 44));
        field.setBackground(new Color(35, 40, 65));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 110)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(320, 44));
        field.setMaximumSize(new Dimension(320, 44));
        field.setBackground(new Color(35, 40, 65));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 110)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, CYAN, getWidth(), getHeight(), PURPLE);
                Shape rr = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setPaint(gp);
                g2.fill(rr);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(320, 46));
        btn.setMaximumSize(new Dimension(320, 46));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        return btn;
    }

    private JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Shape rr = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(0, 0, 0, 0));
                g2.fill(rr);
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(new Color(230, 230, 240));
                g2.draw(rr);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(320, 48));
        btn.setMaximumSize(new Dimension(320, 48));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
