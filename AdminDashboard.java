import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class AdminDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel, sidebar, topNavbar;
    private JButton btnDashboard, btnReports, btnInventory, btnItems, btnCategories, btnEmployees, btnLogout;
    private JLabel titleLabel;

    private String username, role;

    private InventoryPanel inventoryPanel;
    private ReportsPanel reportsPanel;
    private CategoriesPanel categoriesPanel;
    private ItemsPanel itemsPanel;
    private EmployeePanel employeesPanel;

    public AdminDashboard(String username, String role) {
        this.username = username;
        this.role = role;

        setTitle("Dagamis Sash and Woodwork - Admin Dashboard");
        setSize(1450, 850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== TOP NAVBAR =====
        topNavbar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(33, 150, 243), getWidth(), 0, new Color(30, 136, 229));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topNavbar.setPreferredSize(new Dimension(1400, 70));
        topNavbar.setLayout(new BorderLayout());
        topNavbar.setBorder(new MatteBorder(0, 0, 3, 0, new Color(21, 101, 192)));

        titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        topNavbar.add(titleLabel, BorderLayout.WEST);

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        profilePanel.setOpaque(false);
        JLabel userInfo = new JLabel(username + " | " + role + "  ");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userInfo.setForeground(Color.WHITE);
        profilePanel.add(userInfo);

        topNavbar.add(profilePanel, BorderLayout.EAST);
        add(topNavbar, BorderLayout.NORTH);

        // ===== SIDEBAR - MODERN DARK GLASS =====
        sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(260, 740));
        sidebar.setBackground(new Color(28, 37, 48));
        sidebar.setLayout(new GridLayout(12, 1, 0, 2));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 2, new Color(45, 57, 69)));

        btnDashboard = createSidebarButton("Dashboard");
        btnReports = createSidebarButton("Reports"); 
        btnInventory = createSidebarButton("Inventory");
        btnItems = createSidebarButton("Items");
        btnCategories = createSidebarButton("Categories");
        btnEmployees = createSidebarButton("Employees"); 
        btnLogout = createSidebarButton("Logout");


        sidebar.add(btnDashboard);
        sidebar.add(btnReports);
        sidebar.add(btnInventory);
        sidebar.add(btnItems);
        sidebar.add(btnCategories);
        sidebar.add(btnEmployees);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ===== MAIN CONTENT =====
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(242, 245, 249));

        categoriesPanel = new CategoriesPanel();
        itemsPanel = new ItemsPanel(categoriesPanel);
        categoriesPanel.setItemsPanel(itemsPanel);

        inventoryPanel = new InventoryPanel();
        reportsPanel = new ReportsPanel();
        employeesPanel = new EmployeePanel();

        mainPanel.add(createDashboardUI(), "dashboard");
        mainPanel.add(reportsPanel, "reports");
        mainPanel.add(inventoryPanel, "inventory");
        mainPanel.add(itemsPanel, "items");
        mainPanel.add(categoriesPanel, "categories");
        mainPanel.add(employeesPanel, "employees");

        add(mainPanel, BorderLayout.CENTER);

        // Action Listeners
        btnDashboard.addActionListener(e -> switchPanel("dashboard", "Dashboard"));
        btnReports.addActionListener(e -> switchPanel("reports", "Reports"));
        btnInventory.addActionListener(e -> switchPanel("inventory", "Inventory"));
        btnItems.addActionListener(e -> switchPanel("items", "Items"));
        btnCategories.addActionListener(e -> switchPanel("categories", "Categories"));
        btnEmployees.addActionListener(e -> switchPanel("employees", "Employees"));

        btnLogout.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }

    // ===== Modern Sidebar Button =====
    private JButton createSidebarButton(String text) {
    JButton btn = new JButton("   " + text); // Add small padding
    btn.setFont(new Font("Segoe UI", Font.PLAIN, 17));
    btn.setForeground(Color.WHITE);
    btn.setBackground(new Color(28, 37, 48));
    btn.setFocusPainted(false);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setHorizontalAlignment(SwingConstants.LEFT);

    btn.setBorder(new EmptyBorder(12, 25, 12, 10));

    addHoverEffect(btn, new Color(38, 50, 56), new Color(55, 71, 79));
    return btn;
}


    private void addHoverEffect(JButton btn, Color normal, Color hover) {
        btn.setBackground(normal);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(normal);
            }
        });
    }

    // ===== Switch Panel =====
    private void switchPanel(String panelName, String title) {
        cardLayout.show(mainPanel, panelName);
        titleLabel.setText(title);
    }

    private JPanel createDashboardUI() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(242, 245, 249));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Cards layout
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);

        cardsPanel.add(createDashboardCard("Total Items", "1,240", new Color(33, 150, 243)));
        cardsPanel.add(createDashboardCard("Low Stock", "42", new Color(255, 111, 0)));
        cardsPanel.add(createDashboardCard("Employees", "12", new Color(102, 187, 106)));
        cardsPanel.add(createDashboardCard("Categories", "18", new Color(171, 71, 188)));

        panel.add(cardsPanel, BorderLayout.NORTH);
        return panel;
    }

    // ===== Dashboard Card =====
    private JPanel createDashboardCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(200, 130));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(new Color(90, 90, 90));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI Semibold", Font.BOLD, 30));
        lblValue.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard("admin", "Admin"));
    }
}
