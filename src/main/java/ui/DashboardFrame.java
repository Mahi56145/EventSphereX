package ui;

import dao.AttendeeDAO;
import dao.EventDAO;
import dao.UserDAO;
import model.Attendee;
import model.Event;
import model.User;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final Color BG_TOP = new Color(5, 10, 35);
    private final Color BG_BOTTOM = new Color(5, 5, 20);
    private final Color CYAN = new Color(0, 220, 255);
    private final Color PURPLE = new Color(160, 80, 255);
    private final Color CARD_TOP = new Color(25, 35, 70, 230);
    private final Color CARD_BOTTOM = new Color(15, 20, 45, 240);
    private final Color SIDEBAR_BG = new Color(8, 12, 30, 240);
    private final Color TEXT_WHITE = Color.WHITE;

    private final User currentUser;

    private DefaultTableModel upcomingEventsModel;
    private DefaultTableModel manageEventsModel;

    private JLabel totalEventsLabel;
    private JLabel activeEventsLabel;
    private JLabel ticketsLabel;
    private JTextArea todayScheduleArea;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JButton btnDashboard;
    private JButton btnEvents;
    private JButton btnCalendar;
    private JButton btnSettings;

    private JTable manageEventsTable;

    private CalendarPanel calendarPanel;
    private java.util.List<Event> allEvents = new java.util.ArrayList<>();


    private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private String currentPageTitle = "Dashboard";

    public DashboardFrame(User user) {

        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/logo.png"));
        setIconImage(icon);

        this.currentUser = user;
        setTitle("EventSphereX - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setUndecorated(false);

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
                        new Point(getWidth() / 6, getHeight() / 3),
                        getWidth() / 3f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0, 220, 255, 150), new Color(0, 0, 0, 0)}
                ));
                g2.fillOval(getWidth() / 6 - getWidth() / 3, getHeight() / 3 - getWidth() / 3, getWidth() * 2 / 3, getWidth() * 2 / 3);

                g2.setPaint(new RadialGradientPaint(
                        new Point(getWidth() * 4 / 5, getHeight() / 4),
                        getWidth() / 3f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(170, 80, 255, 150), new Color(0, 0, 0, 0)}
                ));
                g2.fillOval(getWidth() * 4 / 5 - getWidth() / 3, getHeight() / 4 - getWidth() / 3, getWidth() * 2 / 3, getWidth() * 2 / 3);

                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        JPanel centerArea = new JPanel(new BorderLayout(20, 20));
        centerArea.setOpaque(false);
        mainPanel.add(centerArea, BorderLayout.CENTER);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        centerArea.add(cardPanel, BorderLayout.CENTER);

        JPanel dashboardCard = createDashboardCard();
        JPanel eventsCard = createEventsCard();
        JPanel calendarCard = createCalendarCard();
        JPanel settingsCard = createSettingsCard();

        cardPanel.add(dashboardCard, "dashboard");
        cardPanel.add(eventsCard, "events");
        cardPanel.add(calendarCard, "calendar");
        cardPanel.add(settingsCard, "settings");

        showCard("dashboard");
        refreshEventsData();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0,
                        SIDEBAR_BG,
                        0, getHeight(),
                        new Color(5, 8, 25, 230)
                );
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };

        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 20, 25, 20));

        // ---- Logo + Title centered ----
        JLabel logoLabel = new JLabel();
        ImageIcon icon = new ImageIcon(getClass().getResource("/logo.png"));
        Image scaled = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("EventSphereX");
        nameLabel.setForeground(TEXT_WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(Box.createVerticalStrut(10));
        logoPanel.add(logoLabel);
        logoPanel.add(Box.createVerticalStrut(10));
        logoPanel.add(nameLabel);
        logoPanel.add(Box.createVerticalStrut(25));

        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(30));

        // ---- Navigation buttons (center text) ----
        btnDashboard = createNavButton("Dashboard");
        btnEvents = createNavButton("Events");
        btnCalendar = createNavButton("Calendar");
        btnSettings = createNavButton("Settings");

        btnDashboard.addActionListener(e -> showCard("dashboard"));
        btnEvents.addActionListener(e -> showCard("events"));
        btnCalendar.addActionListener(e -> showCard("calendar"));
        btnSettings.addActionListener(e -> showCard("settings"));

        sidebar.add(btnDashboard);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnEvents);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnCalendar);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnSettings);

        sidebar.add(Box.createVerticalGlue());

        JButton logout = createLogoutButton("Log Out");
        sidebar.add(logout);

        return sidebar;
    }


    private JButton createNavButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getClientProperty("selected") == Boolean.TRUE || getModel().isRollover()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(
                            0, 0,
                            new Color(0, 200, 255, 180),
                            getWidth(), getHeight(),
                            new Color(120, 80, 255, 160)
                    );
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                    g2.dispose();
                }
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };

        btn.putClientProperty("selected", false);

        // 1) Center the button itself in the sidebar
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 2) Center the text inside the button
        btn.setHorizontalAlignment(SwingConstants.CENTER);

        // 3) Symmetric padding so it doesn’t look shifted
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 4) Limit width so pill doesn’t stretch full sidebar width
        btn.setMaximumSize(new Dimension(180, 44));
        btn.setPreferredSize(new Dimension(180, 44));

        btn.setForeground(new Color(210, 215, 230));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(TEXT_WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getClientProperty("selected") != Boolean.TRUE) {
                    btn.setForeground(new Color(210, 215, 230));
                }
            }
        });

        return btn;
    }


    private JButton createLogoutButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(190, 40, 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(180, 38));
        btn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        return btn;
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(18, 26, 60, 230),
                        0, getHeight(),
                        new Color(10, 16, 35, 230));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };

        bar.setLayout(new BorderLayout(15, 0));
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Left Title
        JLabel pageTitle = new JLabel(currentPageTitle);
        pageTitle.setForeground(Color.WHITE);
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        bar.add(pageTitle, BorderLayout.WEST);

        // Right Icons
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 0));

        JLabel userCircle = new JLabel("\uD83D\uDC64");
        userCircle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        userCircle.setForeground(new Color(185, 190, 255));

        JLabel gear = new JLabel("\u2699");
        gear.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        gear.setForeground(new Color(190, 195, 220));
        gear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        right.add(userCircle);
        right.add(gear);

        bar.add(right, BorderLayout.EAST);

        return bar;
    }


    private JPanel createDashboardCard() {
        JPanel cardsArea = new JPanel();
        cardsArea.setOpaque(false);
        cardsArea.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Upcoming Events now spans full width (no Quick Actions)
        JPanel upcomingEventsCard = createUpcomingEventsCard();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        cardsArea.add(upcomingEventsCard, gbc);

        gbc.gridwidth = 1;

        JPanel todayScheduleCard = createTodayScheduleCard();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 0.4;
        cardsArea.add(todayScheduleCard, gbc);

        JPanel analyticsCard = createAnalyticsCard();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.4;
        cardsArea.add(analyticsCard, gbc);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(cardsArea, BorderLayout.CENTER);
        return outer;
    }

    private JPanel createUpcomingEventsCard() {
        JPanel card = glassCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        // ========= TOP PANEL ==========
        JLabel title = new JLabel("Upcoming Events", SwingConstants.CENTER);
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton createBtn = createPrimaryMiniButton("Create Event");
        createBtn.setPreferredSize(new Dimension(130, 32));
        createBtn.addActionListener(e -> showCreateEventDialog());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.CENTER);
        top.add(createBtn, BorderLayout.EAST);  // stays right

        card.add(top, BorderLayout.NORTH);

        // ========= TABLE ==========
        String[] cols = {"Event Title", "Date & Time", "Location", "Status"};
        upcomingEventsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(upcomingEventsModel);
        table.setRowHeight(32);
        table.setBackground(new Color(18, 22, 45));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(50, 80, 140, 200));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Header
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 32));
        header.setBackground(new Color(10, 16, 35, 220));
        header.setForeground(new Color(190, 220, 255));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // CENTER ALIGN text in all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setOpaque(true);
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(new Color(18, 22, 45));
        sp.setBorder(BorderFactory.createEmptyBorder());

        card.add(sp, BorderLayout.CENTER);

        return card;
    }


    private JPanel createTodayScheduleCard() {
        JPanel card = glassCardPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Today’s Schedule");
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel date = new JLabel(LocalDate.now().format(DATE_FMT));
        date.setForeground(new Color(180, 190, 220));
        date.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(date, BorderLayout.EAST);

        todayScheduleArea = new JTextArea();
        todayScheduleArea.setEditable(false);
        todayScheduleArea.setOpaque(false);
        todayScheduleArea.setForeground(new Color(220, 225, 240));
        todayScheduleArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        todayScheduleArea.setBorder(BorderFactory.createEmptyBorder());
        todayScheduleArea.setLineWrap(true);
        todayScheduleArea.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(todayScheduleArea);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());

        card.add(top, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);

        return card;
    }

    private JPanel createAnalyticsCard() {
        JPanel card = glassCardPanel();
        card.setLayout(new BorderLayout(10, 15));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("Analytics Overview");
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel subtitle = new JLabel("Attendees Over Time");
        subtitle.setForeground(new Color(180, 190, 220));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(3));
        top.add(subtitle);

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                GradientPaint gp = new GradientPaint(0, h, new Color(0, 220, 255, 80),
                        w, 0, new Color(150, 80, 255, 40));
                g2.setPaint(gp);
                int[] xs = new int[]{0, w / 4, w / 2, w * 3 / 4, w};
                int[] ys = new int[]{h - 15, h - 40, h - 25, h - 60, h - 30};
                Polygon p = new Polygon();
                p.addPoint(xs[0], h);
                for (int i = 0; i < xs.length; i++) p.addPoint(xs[i], ys[i]);
                p.addPoint(xs[xs.length - 1], h);
                g2.fill(p);

                g2.setStroke(new BasicStroke(2.2f));
                g2.setPaint(new GradientPaint(0, h, CYAN, w, 0, PURPLE));
                for (int i = 0; i < xs.length - 1; i++) {
                    g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
                }

                g2.dispose();
            }
        };
        chart.setOpaque(false);

        JPanel statsRow = new JPanel();
        statsRow.setOpaque(false);
        statsRow.setLayout(new FlowLayout(FlowLayout.LEFT, 16, 0));

        totalEventsLabel = pillLabel("Total Events: 0");
        activeEventsLabel = pillLabel("Active Events: 0");
        ticketsLabel = pillLabel("Tickets: 0");

        statsRow.add(totalEventsLabel);
        statsRow.add(activeEventsLabel);
        statsRow.add(ticketsLabel);

        card.add(top, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        card.add(statsRow, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createEventsCard() {
        JPanel card = new JPanel(new BorderLayout(20, 20));
        card.setOpaque(false);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setOpaque(false);
        JLabel title = new JLabel("Events");
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        JPanel inner = glassCardPanel();
        inner.setLayout(new BorderLayout(10, 10));
        inner.setBorder(new EmptyBorder(18, 18, 18, 18));

        String[] cols = {"ID", "Title", "Date & Time", "Location"};
        manageEventsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        manageEventsTable = new JTable(manageEventsModel);
        manageEventsTable.setRowHeight(32);

        Color tableBg = new Color(18, 22, 45);
        manageEventsTable.setBackground(tableBg);
        manageEventsTable.setForeground(Color.WHITE);
        manageEventsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        manageEventsTable.setSelectionBackground(new Color(50, 80, 140, 200));
        manageEventsTable.setSelectionForeground(Color.WHITE);
        manageEventsTable.setShowGrid(false);
        manageEventsTable.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header2 = manageEventsTable.getTableHeader();
        header2.setPreferredSize(new Dimension(0, 32));
        header2.setBackground(new Color(10, 16, 35, 220));
        header2.setForeground(new Color(190, 220, 255));
        header2.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < manageEventsTable.getColumnCount(); i++) {
            manageEventsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane sp = new JScrollPane(manageEventsTable);
        sp.setOpaque(true);
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(tableBg);
        sp.setBorder(BorderFactory.createEmptyBorder());

        inner.add(sp, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton addEvent = createPrimaryMiniButton("Add Event");
        JButton addAttendees = createPrimaryMiniButton("Add Attendees");
        JButton editEvent = createPrimaryMiniButton("Edit Event");

        addEvent.addActionListener(e -> showCreateEventDialog());
        addAttendees.addActionListener(e -> showAttendeesDialog());
        editEvent.addActionListener(e -> showEditEventDialog());

        actions.add(addAttendees);
        actions.add(editEvent);
        actions.add(addEvent);

        inner.add(actions, BorderLayout.SOUTH);

        card.add(inner, BorderLayout.CENTER);

        return card;
    }

    private JPanel createCalendarCard() {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Calendar");
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        right.setOpaque(false);

        JButton prev = new JButton("<");
        JButton next = new JButton(">");

        prev.setFocusPainted(false);
        next.setFocusPainted(false);
        prev.setContentAreaFilled(false);
        next.setContentAreaFilled(false);
        prev.setForeground(TEXT_WHITE);
        next.setForeground(TEXT_WHITE);

        JLabel monthLabel = new JLabel();
        monthLabel.setForeground(TEXT_WHITE);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        right.add(prev);
        right.add(monthLabel);
        right.add(next);

        header.add(right, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        calendarPanel = new CalendarPanel(monthLabel);
        calendarPanel.setOpaque(false);
        card.add(calendarPanel, BorderLayout.CENTER);

        updateCalendarEvents();

        prev.addActionListener(e -> {
            calendarPanel.shiftMonth(-1);
            updateCalendarEvents();
        });

        next.addActionListener(e -> {
            calendarPanel.shiftMonth(1);
            updateCalendarEvents();
        });

        return card;
    }

    private JPanel createSettingsCard() {
        JPanel card = new JPanel(new BorderLayout(20, 20));
        card.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Settings");
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.add(title, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);

        JPanel inner = glassCardPanel();
        inner.setLayout(new GridBagLayout());
        inner.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(TEXT_WHITE);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField usernameField = createInputField();
        usernameField.setEditable(false);
        usernameField.setText(currentUser.getUsername());

        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setForeground(TEXT_WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField nameField = createInputField();
        nameField.setText(currentUser.getFullName());

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setForeground(TEXT_WHITE);
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField emailField = createInputField();
        emailField.setText(currentUser.getEmail());

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(TEXT_WHITE);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPasswordField passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(200, 35));
        passField.setBackground(new Color(35, 40, 65));
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(Color.WHITE);
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 110)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        gbc.gridy = 0;
        inner.add(usernameLabel, gbc);
        gbc.gridy++;
        inner.add(usernameField, gbc);
        gbc.gridy++;
        inner.add(nameLabel, gbc);
        gbc.gridy++;
        inner.add(nameField, gbc);
        gbc.gridy++;
        inner.add(emailLabel, gbc);
        gbc.gridy++;
        inner.add(emailField, gbc);
        gbc.gridy++;
        inner.add(passLabel, gbc);
        gbc.gridy++;
        inner.add(passField, gbc);
        gbc.gridy++;

        gbc.anchor = GridBagConstraints.CENTER;
        JButton saveBtn = createPrimaryMiniButton("Save Changes");
        inner.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();
            String newPass = new String(passField.getPassword()).trim();
            UserDAO dao = new UserDAO();
            boolean ok = dao.updateProfile(currentUser.getId(), fullName, email, newPass);
            if (ok) {
                currentUser.setFullName(fullName);
                currentUser.setEmail(email);
                JOptionPane.showMessageDialog(this, "Profile updated.");
                passField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.");
            }
        });

        card.add(inner, BorderLayout.CENTER);

        return card;
    }

    private JPanel glassCardPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, CARD_TOP, 0, getHeight(), CARD_BOTTOM);
                Shape rr = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30);
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
    }

    private JButton createPrimaryMiniButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, CYAN, getWidth(), getHeight(), PURPLE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JLabel pillLabel(String text) {
        JLabel l = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 30, 60, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        l.setForeground(new Color(200, 230, 255));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        return l;
    }

    private void refreshEventsData() {
        if (upcomingEventsModel != null) {
            upcomingEventsModel.setRowCount(0);
        }
        if (manageEventsModel != null) {
            manageEventsModel.setRowCount(0);
        }

        EventDAO dao = new EventDAO();
        List<Event> events = dao.getEventsByUser(currentUser.getId());

        allEvents = events;
        updateCalendarEvents();

        int active = 0;
        int total = events.size();
        StringBuilder todayBuilder = new StringBuilder();
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        for (Event e : events) {
            Timestamp ts = e.getEventDate();
            LocalDateTime eventTime = ts.toLocalDateTime();
            LocalDate eventDay = eventTime.toLocalDate();
            String formatted = eventTime.format(DateTimeFormatter.ofPattern("hh:mm a dd-MM-yyyy"));
            String status;
            if (eventTime.isAfter(now)) {
                status = "Upcoming";
                active++;
            } else {
                status = "Completed";
            }

            if (upcomingEventsModel != null) {
                upcomingEventsModel.addRow(new Object[]{
                        e.getTitle(),
                        formatted,
                        e.getLocation(),
                        status
                });
            }

            if (manageEventsModel != null) {
                manageEventsModel.addRow(new Object[]{
                        e.getId(),
                        e.getTitle(),
                        formatted,
                        e.getLocation()
                });
            }

            if (eventDay.equals(today)) {
                todayBuilder.append("• ")
                        .append(e.getTitle())
                        .append("  @ ")
                        .append(e.getLocation())
                        .append("  (")
                        .append(eventTime.toLocalTime().withSecond(0).withNano(0).toString())
                        .append(")\n");
            }
        }

        if (todayScheduleArea != null) {
            if (todayBuilder.length() == 0) {
                todayBuilder.append("No events scheduled for today.");
            }
            todayScheduleArea.setText(todayBuilder.toString());
        }

        if (totalEventsLabel != null) {
            totalEventsLabel.setText("Total Events: " + total);
        }
        if (activeEventsLabel != null) {
            activeEventsLabel.setText("Active Events: " + active);
        }
        if (ticketsLabel != null) {
            dao.AttendeeDAO aDao = new dao.AttendeeDAO();
            int totalAttendees = aDao.countAttendeesForUser(currentUser.getId());
            ticketsLabel.setText("Attendees: " + totalAttendees);
        }

    }

    private void showCreateEventDialog() {
        JDialog d = new JDialog(this, "New Event", true);
        d.setSize(520, 540);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(20, 25, 45));
        d.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField titleField = createInputField();
        JTextField locField = createInputField();
        JTextField dateField = createInputField();
        dateField.setToolTipText("DD-MM-YYYY");
        JTextArea descArea = new JTextArea(4, 20);
        styleTextArea(descArea);

        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(18, 0, 23, 1));
        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 5));
        styleTimeSpinner(hourSpinner);
        styleTimeSpinner(minuteSpinner);

        JLabel l1 = dialogLabel("Event Title");
        JLabel l2 = dialogLabel("Location");
        JLabel l3 = dialogLabel("Date (DD-MM-YYYY)");
        JLabel lTime = dialogLabel("Time (HH:MM)");
        JLabel l4 = dialogLabel("Description");

        d.add(l1, gbc);
        gbc.gridy++;
        d.add(titleField, gbc);
        gbc.gridy++;
        d.add(l2, gbc);
        gbc.gridy++;
        d.add(locField, gbc);
        gbc.gridy++;
        d.add(l3, gbc);
        gbc.gridy++;
        d.add(dateField, gbc);
        gbc.gridy++;

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        timePanel.setOpaque(false);
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteSpinner);

        d.add(lTime, gbc);
        gbc.gridy++;
        d.add(timePanel, gbc);
        gbc.gridy++;

        d.add(l4, gbc);
        gbc.gridy++;
        JScrollPane sp = new JScrollPane(descArea);
        sp.setBorder(BorderFactory.createLineBorder(new Color(70, 80, 110)));
        d.add(sp, gbc);
        gbc.gridy++;

        JButton saveBtn = createPrimaryMiniButton("Save Event");
        gbc.insets = new Insets(25, 20, 15, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        d.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText().trim(), DATE_FMT);
                int hour = (Integer) hourSpinner.getValue();
                int minute = (Integer) minuteSpinner.getValue();
                LocalDateTime dt = date.atTime(hour, minute);

                EventDAO dao = new EventDAO();
                Event event = new Event();
                event.setTitle(titleField.getText());
                event.setLocation(locField.getText());
                event.setDescription(descArea.getText());
                event.setEventDate(Timestamp.valueOf(dt));

                boolean ok = dao.addEvent(event, currentUser.getId());
                if (ok) {
                    JOptionPane.showMessageDialog(d, "Event Created!");
                    refreshEventsData();
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Could not save event.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: Check date/time format.");
            }
        });

        d.setVisible(true);
    }

    private void showEditEventDialog() {
        int row = manageEventsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an event to edit.");
            return;
        }

        int eventId = (int) manageEventsModel.getValueAt(row, 0);

        EventDAO dao = new EventDAO();
        Event existing = dao.getEventById(eventId, currentUser.getId());
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Could not load event from database.");
            return;
        }

        LocalDateTime existingDt = existing.getEventDate().toLocalDateTime();

        JDialog d = new JDialog(this, "Edit Event", true);
        d.setSize(520, 540);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(20, 25, 45));
        d.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField titleField = createInputField();
        titleField.setText(existing.getTitle());
        JTextField locField = createInputField();
        locField.setText(existing.getLocation());
        JTextField dateField = createInputField();
        dateField.setToolTipText("DD-MM-YYYY");
        dateField.setText(existingDt.toLocalDate().format(DATE_FMT));
        JTextArea descArea = new JTextArea(4, 20);
        styleTextArea(descArea);
        descArea.setText(existing.getDescription());

        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(existingDt.getHour(), 0, 23, 1));
        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(existingDt.getMinute(), 0, 59, 5));
        styleTimeSpinner(hourSpinner);
        styleTimeSpinner(minuteSpinner);

        JLabel l1 = dialogLabel("Event Title");
        JLabel l2 = dialogLabel("Location");
        JLabel l3 = dialogLabel("Date (DD-MM-YYYY)");
        JLabel lTime = dialogLabel("Time (HH:MM)");
        JLabel l4 = dialogLabel("Description");

        d.add(l1, gbc);
        gbc.gridy++;
        d.add(titleField, gbc);
        gbc.gridy++;
        d.add(l2, gbc);
        gbc.gridy++;
        d.add(locField, gbc);
        gbc.gridy++;
        d.add(l3, gbc);
        gbc.gridy++;
        d.add(dateField, gbc);
        gbc.gridy++;

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        timePanel.setOpaque(false);
        timePanel.add(hourSpinner);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteSpinner);

        d.add(lTime, gbc);
        gbc.gridy++;
        d.add(timePanel, gbc);
        gbc.gridy++;

        d.add(l4, gbc);
        gbc.gridy++;
        JScrollPane sp = new JScrollPane(descArea);
        sp.setBorder(BorderFactory.createLineBorder(new Color(70, 80, 110)));
        d.add(sp, gbc);
        gbc.gridy++;

        JButton saveBtn = createPrimaryMiniButton("Update Event");
        gbc.insets = new Insets(25, 20, 15, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        d.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            try {
                LocalDate date = LocalDate.parse(dateField.getText().trim(), DATE_FMT);
                int hour = (Integer) hourSpinner.getValue();
                int minute = (Integer) minuteSpinner.getValue();
                LocalDateTime dt = date.atTime(hour, minute);

                Event updated = new Event();
                updated.setId(eventId);
                updated.setTitle(titleField.getText());
                updated.setLocation(locField.getText());
                updated.setDescription(descArea.getText());
                updated.setEventDate(Timestamp.valueOf(dt));

                EventDAO daoInner = new EventDAO();
                boolean ok = daoInner.updateEvent(updated, currentUser.getId());
                if (ok) {
                    JOptionPane.showMessageDialog(d, "Event updated!");
                    refreshEventsData();
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Could not update event.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: Check date/time format.");
            }
        });

        d.setVisible(true);
    }

    private void showAttendeesDialog() {
        int row = manageEventsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an event first.");
            return;
        }

        int eventId = (int) manageEventsModel.getValueAt(row, 0);
        String eventTitle = (String) manageEventsModel.getValueAt(row, 1);

        JDialog d = new JDialog(this, "Attendees - " + eventTitle, true);
        d.setSize(650, 500);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(20, 25, 45));
        d.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Attendees for: " + eventTitle);
        title.setForeground(TEXT_WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        top.setBorder(new EmptyBorder(15, 20, 0, 20));
        top.add(title, BorderLayout.WEST);
        d.add(top, BorderLayout.NORTH);

        DefaultTableModel attendeesModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Email", "Phone"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(attendeesModel);
        table.setRowHeight(30);
        Color tableBg = new Color(18, 22, 45);
        table.setBackground(tableBg);
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(50, 80, 140, 200));
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 30));
        header.setBackground(new Color(10, 16, 35, 220));
        header.setForeground(new Color(190, 220, 255));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane sp = new JScrollPane(table);
        sp.setOpaque(true);
        sp.getViewport().setOpaque(true);
        sp.getViewport().setBackground(tableBg);
        sp.setBorder(new EmptyBorder(10, 20, 10, 20));
        d.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridBagLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(0, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        JTextField nameField = createInputField();
        JTextField emailField = createInputField();
        JTextField phoneField = createInputField();

        bottom.add(labelSmall("Name"), gbc);
        gbc.gridy++;
        bottom.add(nameField, gbc);
        gbc.gridy++;
        bottom.add(labelSmall("Email"), gbc);
        gbc.gridy++;
        bottom.add(emailField, gbc);
        gbc.gridy++;
        bottom.add(labelSmall("Phone"), gbc);
        gbc.gridy++;
        bottom.add(phoneField, gbc);

        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);
        JButton addBtn = createPrimaryMiniButton("Add");
        JButton deleteBtn = createPrimaryMiniButton("Delete Selected");
        btnRow.add(addBtn);
        btnRow.add(deleteBtn);

        bottom.add(btnRow, gbc);

        d.add(bottom, BorderLayout.SOUTH);

        AttendeeDAO dao = new AttendeeDAO();

        Runnable reload = () -> {
            attendeesModel.setRowCount(0);
            for (Attendee a : dao.getAttendeesByEvent(eventId)) {
                attendeesModel.addRow(new Object[]{
                        a.getId(),
                        a.getName(),
                        a.getEmail(),
                        a.getPhone()
                });
            }
        };

        reload.run();

        addBtn.addActionListener(e -> {
            String n = nameField.getText().trim();
            String em = emailField.getText().trim();
            String ph = phoneField.getText().trim();
            if (n.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Name is required.");
                return;
            }
            Attendee a = new Attendee();
            a.setEventId(eventId);
            a.setName(n);
            a.setEmail(em);
            a.setPhone(ph);
            if (dao.addAttendee(a)) {
                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                reload.run();
            } else {
                JOptionPane.showMessageDialog(d, "Failed to add attendee.");
            }
        });

        deleteBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel == -1) {
                JOptionPane.showMessageDialog(d, "Select an attendee to delete.");
                return;
            }
            int attendeeId = (int) attendeesModel.getValueAt(sel, 0);
            if (dao.deleteAttendee(eventId, attendeeId)) {
                reload.run();
            } else {
                JOptionPane.showMessageDialog(d, "Failed to delete attendee.");
            }
        });


        d.setVisible(true);
    }

    private void showDayDetails(java.time.LocalDate date, java.util.List<Event> eventsForDay) {
        JDialog d = new JDialog(this, "Events on " + date.toString(), true);
        d.setSize(600, 500);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(new Color(20, 25, 45));
        d.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Events on " + date.toString());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(15, 20, 5, 20));
        d.add(title, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setOpaque(false);
        area.setForeground(new Color(220, 225, 240));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 20, 10, 20));

        StringBuilder sb = new StringBuilder();
        if (eventsForDay == null || eventsForDay.isEmpty()) {
            sb.append("No events on this day.");
        } else {
            dao.AttendeeDAO attendeeDAO = new dao.AttendeeDAO();
            java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm");

            int idx = 1;
            for (Event e : eventsForDay) {
                java.time.LocalDateTime dt = e.getEventDate().toLocalDateTime();
                sb.append(idx++).append(". ").append(e.getTitle()).append("\n");
                sb.append("   Time: ").append(dt.toLocalTime().format(timeFmt)).append("\n");
                sb.append("   Location: ").append(e.getLocation() == null ? "-" : e.getLocation()).append("\n");
                sb.append("   Description: ").append(
                        e.getDescription() == null || e.getDescription().isEmpty() ? "-" : e.getDescription()
                ).append("\n");

                java.util.List<model.Attendee> attendees = attendeeDAO.getAttendeesByEvent(e.getId());
                if (attendees.isEmpty()) {
                    sb.append("   Attendees: none\n");
                } else {
                    sb.append("   Attendees:\n");
                    for (model.Attendee a : attendees) {
                        sb.append("      • ").append(a.getName());
                        if (a.getEmail() != null && !a.getEmail().isEmpty()) {
                            sb.append("  <").append(a.getEmail()).append(">");
                        }
                        if (a.getPhone() != null && !a.getPhone().isEmpty()) {
                            sb.append("  (").append(a.getPhone()).append(")");
                        }
                        sb.append("\n");
                    }
                }
                sb.append("\n");
            }
        }

        area.setText(sb.toString());

        JScrollPane sp = new JScrollPane(area);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        d.add(sp, BorderLayout.CENTER);

        d.setVisible(true);
    }

    private JLabel labelSmall(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JLabel dialogLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    private JTextField createInputField() {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(200, 35));
        f.setBackground(new Color(35, 40, 65));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 110)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private void styleTimeSpinner(JSpinner spinner) {
        spinner.setPreferredSize(new Dimension(60, 32));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setBackground(new Color(35, 40, 65));
            ((JSpinner.DefaultEditor) editor).getTextField().setForeground(Color.WHITE);
            ((JSpinner.DefaultEditor) editor).getTextField().setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(70, 80, 110)),
                            BorderFactory.createEmptyBorder(4, 6, 4, 6)
                    ));
        }
    }

    private void styleTextArea(JTextArea a) {
        a.setBackground(new Color(35, 40, 65));
        a.setForeground(Color.WHITE);
        a.setCaretColor(Color.WHITE);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
        switch (name) {
            case "dashboard": currentPageTitle = "Dashboard"; break;
            case "events": currentPageTitle = "Events"; break;
            case "calendar": currentPageTitle = "Calendar"; break;
            case "settings": currentPageTitle = "Settings"; break;
        }
        btnDashboard.putClientProperty("selected", name.equals("dashboard"));
        btnEvents.putClientProperty("selected", name.equals("events"));
        btnCalendar.putClientProperty("selected", name.equals("calendar"));
        btnSettings.putClientProperty("selected", name.equals("settings"));

        updateNavColor(btnDashboard);
        updateNavColor(btnEvents);
        updateNavColor(btnCalendar);
        updateNavColor(btnSettings);
        revalidate();
        repaint();
    }

    private void updateNavColor(JButton btn) {
        if (btn.getClientProperty("selected") == Boolean.TRUE) {
            btn.setForeground(TEXT_WHITE);
        } else {
            btn.setForeground(new Color(210, 215, 230));
        }
    }
    private void updateCalendarEvents() {
        if (calendarPanel != null && allEvents != null) {
            calendarPanel.setEvents(allEvents);
        }
    }
    private class CalendarPanel extends JPanel {

        private java.time.YearMonth month;
        private java.util.List<Event> events = new java.util.ArrayList<>();
        private JLabel monthLabel;

        CalendarPanel(JLabel monthLabel) {
            this.monthLabel = monthLabel;
            this.month = java.time.YearMonth.now();
            rebuild();
        }

        void setEvents(java.util.List<Event> events) {
            this.events = new java.util.ArrayList<>(events);
            rebuild();
        }

        void shiftMonth(int delta) {
            this.month = this.month.plusMonths(delta);
            rebuild();
        }

        java.time.YearMonth getMonth() {
            return month;
        }

        private void rebuild() {
            removeAll();
            setLayout(new BorderLayout(0, 8));

            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy");
            monthLabel.setText(month.format(fmt));

            JPanel daysHeader = new JPanel(new GridLayout(1, 7, 5, 5));
            daysHeader.setOpaque(false);
            String[] names = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String n : names) {
                JLabel l = new JLabel(n, SwingConstants.CENTER);
                l.setForeground(new Color(200, 210, 235));
                l.setFont(new Font("Segoe UI", Font.BOLD, 12));
                daysHeader.add(l);
            }

            JPanel grid = new JPanel(new GridLayout(0, 7, 5, 5)) {
                @Override
                public boolean isOpaque() {
                    return false;
                }
            };

            java.util.Map<java.time.LocalDate, java.util.List<Event>> map = new java.util.HashMap<>();
            for (Event e : allEvents) {
                java.time.LocalDate d = e.getEventDate().toLocalDateTime().toLocalDate();
                if (java.time.YearMonth.from(d).equals(month)) {
                    map.computeIfAbsent(d, k -> new java.util.ArrayList<>()).add(e);
                }
            }

            java.time.LocalDate first = month.atDay(1);
            int firstDow = first.getDayOfWeek().getValue();
            int daysInMonth = month.lengthOfMonth();

            int pad = firstDow == 7 ? 0 : firstDow;
            for (int i = 1; i < pad; i++) {
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                grid.add(empty);
            }

            java.time.LocalDate today = java.time.LocalDate.now();

            for (int day = 1; day <= daysInMonth; day++) {
                java.time.LocalDate date = month.atDay(day);
                java.util.List<Event> dayEvents = map.get(date);
                boolean hasEvents = dayEvents != null && !dayEvents.isEmpty();

                JPanel cell = new JPanel(new BorderLayout());
                cell.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

                boolean isToday = date.equals(today);

                if (hasEvents) {
                    cell.setBackground(new Color(25, 120, 190, 160));
                } else {
                    cell.setBackground(new Color(15, 25, 55, 150));
                }
                cell.setOpaque(true);

                if (isToday) {
                    cell.setBorder(BorderFactory.createLineBorder(new Color(0, 230, 255), 2));
                }

                JLabel dayLabel = new JLabel(String.valueOf(day));
                dayLabel.setForeground(Color.WHITE);
                dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                cell.add(dayLabel, BorderLayout.NORTH);

                if (hasEvents) {
                    int c = dayEvents.size();
                    JLabel evLabel = new JLabel(c + (c == 1 ? " event" : " events"));
                    evLabel.setForeground(new Color(220, 235, 255));
                    evLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    cell.add(evLabel, BorderLayout.SOUTH);
                }

                if (hasEvents) {
                    cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    cell.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            showDayDetails(date, dayEvents);
                        }
                    });
                }

                grid.add(cell);
            }

            add(daysHeader, BorderLayout.NORTH);
            add(grid, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }


}
