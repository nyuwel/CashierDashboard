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

        JPanel dashboardScrollContent = createDashboardUI();

        JScrollPane dashboardScrollPane = new JScrollPane(dashboardScrollContent);

        dashboardScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default border
        dashboardScrollPane.getVerticalScrollBar().setUnitIncrement(20); // Make the scroll bar 'faster'

// 3. Add the scrollable panel to the card layout
        mainPanel.add(dashboardScrollPane, "dashboard");

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
        JPanel panel = new JPanel();
        panel.setBackground(new Color(242, 245, 249));
        // Use BoxLayout for vertical stacking: Cards on top, detailed sections below
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. Cards Panel (Now 4 key metrics in the requested order)
        // CHANGE: 1 row, 4 columns
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        cardsPanel.setOpaque(false);
        
        // --- Core Metrics in New Order ---
        
        // 1. Placeholder for "Top Selling Products" (using Total Active Products instead)
        cardsPanel.add(createDashboardCard("Total Sales (Today)", "₱85,300", new Color(3, 169, 244)));
        
        // 2. Low Stock
        cardsPanel.add(createDashboardCard("Low Stock", "42", new Color(255, 111, 0))); // Orange
        
        // 3. Out of Stock Items
        cardsPanel.add(createDashboardCard("Out of Stock Items", "5", new Color(244, 67, 54))); // Red
        
        // 4. Employees
        cardsPanel.add(createDashboardCard("Employees", "12", new Color(102, 187, 106))); // Green
        
        // --- Cards Removed: Total Sales (Today) and Open Orders ---

        // Wrap cardsPanel to align it horizontally and give it a max height
        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setOpaque(false);
        cardsWrapper.add(cardsPanel, BorderLayout.NORTH);
        
        panel.add(cardsWrapper);
        panel.add(Box.createRigidArea(new Dimension(0, 25))); // Vertical spacing
        
        // 2. Detailed Content Section (Lists and Alerts) - Remains the same
        panel.add(createLowerContentSection());
        
        return panel;
    }
    // ... (All other methods remain the same) ...

    private JPanel createLowerContentSection() {
        JPanel lowerPanel = new JPanel();
        lowerPanel.setOpaque(false);
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));

        // 1. Top Selling Products (Full Width)
        lowerPanel.add(createTopSellingPanel());
        lowerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Vertical spacing

        // 2. Recent Activity & Stock Alerts (Side-by-Side)
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 row, 2 columns, 20px horizontal gap
        splitPanel.setOpaque(false);
        
        splitPanel.add(createRecentActivityPanel());
        splitPanel.add(createStockAlertsPanel());
        
        lowerPanel.add(splitPanel);

        return lowerPanel;
    }

    private JPanel createTopSellingPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), 15); 
        panel.setBackground(Color.WHITE);
        
        // Remove the old LineBorder since RoundedPanel draws a new one
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));;
        
        JLabel title = new JLabel("Top Selling Woodwork Items 📈");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        // This list will contain our modern styled product rows
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);
        list.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // --- Populating with Items and Simulated Sales ---
        // Note: Sold count and stock status are based on your provided list/simulated.
        
        list.add(createProductItem("S4S Pine 1x2 (6ft)", "950 sold", 1000, 1)); // Highest stock = most popular?
        list.add(createSeparator());
        list.add(createProductItem("Plywood 1/4", "75 sold", 20, 2));
        list.add(createSeparator());
        list.add(createProductItem("Marine Plywood", "55 sold", 5, 3)); // Critical stock item
        list.add(createSeparator());
        list.add(createProductItem("Yakal 2x4 (10ft)", "45 sold", 100, 4));
        list.add(createSeparator());
        list.add(createProductItem("Gmelina 2x3 (8ft)", "38 sold", 40, 5));


        panel.add(list, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProductItem(String name, String sold, int stock, int rank) {
        JPanel item = new JPanel(new BorderLayout(15, 0));
        item.setOpaque(false);
        item.setBorder(new EmptyBorder(10, 0, 10, 0));

        // --- LEFT SIDE: Name, Sold, and Stock Badge ---
        
        // Stock Badge (Pill Style)
        Color stockColor = new Color(76, 175, 80); // Default: Green
        String stockText = "In Stock";
        if (stock <= 20 && stock > 5) {
            stockColor = new Color(255, 152, 0); // Warning: Orange
            stockText = "Low Stock";
        } else if (stock <= 5) {
            stockColor = new Color(244, 67, 54); // Critical: Red
            stockText = "CRITICAL STOCK";
        }

        JLabel lblStockBadge = new JLabel(" " + stockText + " ");
        lblStockBadge.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 10));
        lblStockBadge.setForeground(Color.WHITE);
        lblStockBadge.setBackground(stockColor);
        lblStockBadge.setOpaque(true);
        lblStockBadge.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        // Note: For true rounded corners, you'd need a custom border/drawing logic.

        // Details Panel (Sold count and Stock Badge)
        JLabel lblSold = new JLabel(sold + " | " + stock + " in stock");
        lblSold.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSold.setForeground(new Color(120, 120, 120));
        
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        detailsPanel.setOpaque(false);
        detailsPanel.add(lblSold);
        detailsPanel.add(lblStockBadge);


        // Main Product Text
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.add(lblName, BorderLayout.NORTH);
        left.add(detailsPanel, BorderLayout.CENTER);
        item.add(left, BorderLayout.CENTER);
        
        // --- RIGHT SIDE: Rank Badge ---
        
        JLabel lblRank = new JLabel(" #" + rank + " ");
        lblRank.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        lblRank.setForeground(Color.WHITE);
        lblRank.setBackground(new Color(158, 158, 158)); // Dark Gray Background
        lblRank.setOpaque(true);
        lblRank.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Padding for a "badge" look
        
        item.add(lblRank, BorderLayout.EAST);
        
        return item;
    }
    
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(240, 240, 240));
        return sep;
    }

    private JPanel createRecentActivityPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), 15); 
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
          
        JLabel title = new JLabel("Recent Activity 🕒");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setOpaque(false);
        activityList.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Activity items (using HTML for color/icon)
        activityList.add(createActivityItem("<html><span style='color:green;'>• New Order </span>#1005 processed</html>", "5 min ago"));
        activityList.add(Box.createRigidArea(new Dimension(0, 10)));
        activityList.add(createActivityItem("<html><span style='color:blue;'>• Inventory Update:</span> Pine 2x4s added</html>", "12 min ago"));
        activityList.add(Box.createRigidArea(new Dimension(0, 10)));
        activityList.add(createActivityItem("<html><span style='color:red;'>• Employee Login:</span> J. Dela Cruz</html>", "25 min ago"));
        activityList.add(Box.createRigidArea(new Dimension(0, 10)));
        activityList.add(createActivityItem("<html><span style='color:orange;'>• Report Generated:</span> End of Day Sales</html>", "1 hour ago"));
        
        panel.add(activityList, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createActivityItem(String activity, String time) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        
        JLabel lblActivity = new JLabel(activity);
        lblActivity.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel lblTime = new JLabel(time);
        lblTime.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblTime.setForeground(Color.GRAY);
        lblTime.setHorizontalAlignment(SwingConstants.RIGHT);

        item.add(lblActivity, BorderLayout.WEST);
        item.add(lblTime, BorderLayout.EAST);
        return item;
    }

    private JPanel createStockAlertsPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), 15); 
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Use a FlowLayout or GridBagLayout for the title to place the icon
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Stock Alerts ⚠️");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        titlePanel.add(title, BorderLayout.WEST);
        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel alertsContent = new JPanel();
        alertsContent.setLayout(new BoxLayout(alertsContent, BoxLayout.Y_AXIS));
        alertsContent.setOpaque(false);
        alertsContent.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Low Stock Alert Card
        JPanel lowStockCard = createAlertCard("Low Stock Items", "Need restocking soon", "42", new Color(255, 152, 0));
        
        // Total Products Card
        JPanel totalProductsCard = createAlertCard("Total Products", "Active in inventory", "1,240", new Color(76, 175, 80));
        
        alertsContent.add(lowStockCard);
        alertsContent.add(Box.createRigidArea(new Dimension(0, 10)));
        alertsContent.add(totalProductsCard);
        
        panel.add(alertsContent, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAlertCard(String title, String subtitle, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(250, 250, 250)); // Slightly off-white for contrast
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(240, 240, 240), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        lblTitle.setForeground(new Color(50, 50, 50));
        
        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(Color.GRAY);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(lblTitle, BorderLayout.NORTH);
        textPanel.add(lblSubtitle, BorderLayout.SOUTH);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        lblValue.setForeground(color);
        
        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblValue, BorderLayout.EAST);
        
        return card;
    }

    // ===== Dashboard Card =====
    // ===== Dashboard Card - UPDATED TO USE RoundedPanel =====
    private JPanel createDashboardCard(String title, String value, Color color) {
        // CHANGE: Use RoundedPanel(Layout, Radius)
        RoundedPanel card = new RoundedPanel(new BorderLayout(), 15);
        
        card.setPreferredSize(new Dimension(200, 130));
        card.setBackground(Color.WHITE);
        
        // Remove old borders since RoundedPanel draws its own and we added padding in the class
        // card.setBorder(new CompoundBorder(...)); 
        
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
