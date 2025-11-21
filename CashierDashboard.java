import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class CashierDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String username, role;

    private JLabel totalLabel;
    private JPanel productGridPanel;
    private JTextField searchField;
    private JPanel cartItemsPanel;
    private JButton checkoutButton;
    private JButton clearCartButton;

    private double totalAmount = 0.0;
    private static Set<String> usedReferenceCodes = new HashSet<>();
    private static int orderCounter = 0;

    public static final Color APP_BG = new Color(250, 250, 250);
    public static final Color SIDEBAR = new Color(33, 33, 33);
    public static final Color HEADER = new Color(48, 63, 159);
    public static final Color CARD = new Color(255, 255, 255);
    public static final Color ACCENT = new Color(33, 150, 243);
    public static final Color DANGER = new Color(211, 47, 47);
    public static final Color SUCCESS = new Color(56, 142, 60);
    public static final Color TEXT_DARK = new Color(44, 62, 80);
    public static final Color ITEM_CARD_BG = new Color(245, 245, 245);

    private enum ProductCategory {
        PLYWOOD("Plywoods", new Color(255, 179, 0)), // Amber/Orange
        LUMBER("Lumbers", new Color(67, 160, 71)),   // Green
        BOARDS("Boards", new Color(94, 53, 177)),   // Deep Purple
        HARDWARE("Hardware", new Color(244, 81, 30)),// Deep Orange
        OTHER("Other", new Color(120, 144, 156));    // Blue Gray

        public final String name;
        public final Color color;

        ProductCategory(String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }


    // Item Inventory
    class Item {
        String name;
        double price;
        int stock;
        ProductCategory category; // <-- NEW CATEGORY FIELD
        JLabel stockLabelRef;

        Item(String n, double p, int s, ProductCategory c) { // <-- UPDATED CONSTRUCTOR
            name = n;
            price = p;
            stock = s;
            category = c;
        }

        void setStockLabelRef(JLabel label) {
            this.stockLabelRef = label;
            updateStockLabel();
        }

        void updateStockLabel() {
            if (stockLabelRef != null) {
                stockLabelRef.setText("Stock: " + stock);
                stockLabelRef.setForeground(stock > 10 ? SUCCESS : DANGER);
            }
        }
    }

    // Cart Item Class to store necessary data (not just display text)
    class CartItem {
        String name;
        double price;
        int qty;

        CartItem(String n, double p, int q) {
            name = n;
            price = p;
            qty = q;
        }

        // Returns the item from the inventory
        Item getInventoryItem() {
            for (Item item : itemList) {
                if (item.name.equals(this.name)) {
                    return item;
                }
            }
            return null;
        }
    }

    private List<CartItem> cartItems = new ArrayList<>();
    private List<Item> itemList = new ArrayList<>();
    private List<String[]> salesHistory = new ArrayList<>();

    public CashierDashboard(String username, String role) {
        this.username = username;
        this.role = role;

        initializeItems();
        initComponents();
    }

    private void initializeItems() {
        itemList.add(new Item("Plywood 1/4", 450, 20, ProductCategory.PLYWOOD));
        itemList.add(new Item("Plywood 1/2", 650, 15, ProductCategory.PLYWOOD));
        itemList.add(new Item("Coco Lumber 2x2", 80, 50, ProductCategory.LUMBER));
        itemList.add(new Item("Nails 1 inch", 50, 100, ProductCategory.HARDWARE));
        itemList.add(new Item("Gmelina 2x3 (8ft)", 120, 40, ProductCategory.LUMBER));
        itemList.add(new Item("Yakal 2x4 (10ft)", 450, 100, ProductCategory.LUMBER));
        itemList.add(new Item("Apitong 2x6 (12ft)", 200, 90, ProductCategory.LUMBER));
        itemList.add(new Item("Treated Lumber 4x4 (8ft)", 100, 15, ProductCategory.LUMBER));
        itemList.add(new Item("S4S Pine 1x2 (6ft)", 10, 1000, ProductCategory.LUMBER));
        itemList.add(new Item("Marine Plywood", 120, 5, ProductCategory.PLYWOOD));
        itemList.add(new Item("OSB Board 1/2\" (4x8)", 200, 500, ProductCategory.BOARDS));
        itemList.add(new Item("MDF Board 3/4\" (4x8)", 285, 300, ProductCategory.BOARDS));
        itemList.add(new Item("Particle Board 5/8\" (4x8)", 360, 213, ProductCategory.BOARDS));
        itemList.add(new Item("Hardboard (Masonite) 1/8\"", 560, 617, ProductCategory.BOARDS));
        itemList.add(new Item("Finger Joint Pine 1x4 (8ft)", 420, 67, ProductCategory.LUMBER));
    }

    private void initComponents() {
        // [Existing initComponents code for setting up JFrame, Sidebar, and Header remains the same]
        setTitle("Dagamis Sash & Woodwork - Cashier Dashboard");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getContentPane().setBackground(APP_BG);

        // ------------------ Sidebar ------------------
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(180, getHeight()));
        sidebar.setLayout(new BorderLayout());

        // top of sidebar
        JLabel logo = new JLabel("  CASHIER PANEL");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));
        sidebar.add(logo, BorderLayout.NORTH);

        // buttons area
        JPanel sbButtons = new JPanel();
        sbButtons.setBackground(SIDEBAR);
        sbButtons.setLayout(new BoxLayout(sbButtons, BoxLayout.Y_AXIS));
        sbButtons.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        JButton posBtn = createMaterialSidebarButton("POS", SIDEBAR, ACCENT);
        JButton transBtn = createMaterialSidebarButton("Transactions", SIDEBAR, ACCENT);
        JButton logoutBtn = createMaterialSidebarButton("Logout", SIDEBAR, ACCENT);

        sbButtons.add(posBtn);
        sbButtons.add(Box.createVerticalStrut(12));
        sbButtons.add(transBtn);
        sbButtons.add(Box.createVerticalGlue());
        sbButtons.add(logoutBtn);
        sbButtons.add(Box.createVerticalStrut(16));

        sidebar.add(sbButtons, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);

        // ------------------ Header ------------------
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(getWidth(), 64));
        header.setBackground(HEADER);

        JLabel headerTitle = new JLabel("  Cashier Dashboard - POS");
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        header.add(headerTitle, BorderLayout.WEST);

        JLabel userInfo = new JLabel(username + " | " + role + "  ");
        userInfo.setForeground(Color.WHITE);
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        header.add(userInfo, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ------------------ Main Panel Cards ------------------
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.setBackground(APP_BG);

        mainPanel.add(createPOSPanelNewLayout(), "POS");
        mainPanel.add(createTransactionsPanelMaterial(APP_BG, CARD), "TRANS");

        add(mainPanel, BorderLayout.CENTER);

        // ------------------ Button actions ------------------
        posBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "POS");
            headerTitle.setText("  Cashier Dashboard - POS");
        });

        transBtn.addActionListener(e -> {
            loadTransactions();
            cardLayout.show(mainPanel, "TRANS");
            headerTitle.setText("  Cashier Dashboard - Transactions");
        });

        logoutBtn.addActionListener(e -> {
            dispose();

        try {
            Login loginFrame = new Login();
            loginFrame.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error returning to Login screen: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
});

        setVisible(true);
    }

    // [createMaterialSidebarButton, createPrimaryButton, createAccentButton, createDangerButton remain the same]
    private JButton createMaterialSidebarButton(String text, Color bg, Color accent) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setBackground(new Color(40, 40, 40));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(55, 55, 55));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(40, 40, 40));
            }
        });
        return btn;
    }

    private JButton createPrimaryButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setBackground(accent);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createAccentButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createDangerButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ========== NEW POS PANEL - Product Grid on Left, Cart on Right ==========
    private JPanel createPOSPanelNewLayout() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(APP_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0); // Remove root insets

        // ------------------ 1. Category Filter Panel (Far Left) ------------------
        gbc.gridx = 0;
        gbc.weightx = 0.0; // Fixed width
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;

        JPanel categoryPanel = createCategoryFilterPanel();
        root.add(categoryPanel, gbc);

        // ------------------ 2. Product Selection Panel (Middle) ------------------
        gbc.gridx = 1;
        gbc.weightx = 0.70; // 70% width
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10); // Re-add padding here

        JPanel productPanel = createProductSelectionPanel();
        root.add(productPanel, gbc);

        // ------------------ 3. Right Panel (Current Order/Cart) ------------------
        gbc.gridx = 2; // Moved to column 2
        gbc.weightx = 0.30; // 30% width
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 10); // Reduced left inset

        JPanel cartPanel = createCurrentOrderCartPanel();
        root.add(cartPanel, gbc);

        // Final Setup and Actions
        setupCartActions();

        return root;
    }

    // --- Product Selection (Left) ---
    private JPanel createProductSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Product Selection");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        // Search Bar (Removed Qty Spinner)
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setOpaque(false);

        searchField = new JTextField("Search products by name...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topBar.add(searchField, BorderLayout.CENTER);

        // Product Grid (Using a panel inside a JScrollPane)
        productGridPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // Changed to 4 columns for smaller cards
        productGridPanel.setBackground(CARD);
        productGridPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Initial population of the grid
        for (Item item : itemList) {
            productGridPanel.add(createProductCard(item));
        }

        JScrollPane scroll = new JScrollPane(productGridPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(50);

        // Re-arranging for better layout: use a wrapper for the search bar and grid
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);
        content.add(topBar, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);

        panel.add(content, BorderLayout.CENTER);

        // Add search listener
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterProductGrid(searchField.getText());
            }
        });

        // Clear default text on focus
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search products by name...")) {
                    searchField.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search products by name...");
                    filterProductGrid("");
                }
            }
        });

        return panel;
    }

    // ========== NEW: Category Filter Panel (Left Side of Product Area) ==========
    private JPanel createCategoryFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(180, 0)); // Set fixed width
        panel.setBackground(SIDEBAR); // Use a contrasting color (like SIDEBAR)

        // Title Area
        JLabel title = new JLabel("CATEGORIES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        title.setBackground(new Color(50, 50, 50)); // Darker shade
        title.setOpaque(true);
        panel.add(title, BorderLayout.NORTH);

        // Buttons Container
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(SIDEBAR);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 1. "ALL ITEMS" Button
        JButton allItemsBtn = createCategoryButton("ALL ITEMS", null);
        buttonsPanel.add(allItemsBtn);
        buttonsPanel.add(Box.createVerticalStrut(5));

        // Action for ALL ITEMS button (Shows all products)
        allItemsBtn.addActionListener(e -> filterProductGrid(""));


        // 2. Category Buttons (from the enum)
        for (ProductCategory category : ProductCategory.values()) {
            JButton btn = createCategoryButton(category.name, category);
            buttonsPanel.add(btn);
            buttonsPanel.add(Box.createVerticalStrut(5));
        }

        // Wrap buttons in a scroll pane just in case there are many categories
        JScrollPane scroll = new JScrollPane(buttonsPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(10);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JButton createCategoryButton(String text, ProductCategory category) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(170, 40));
        btn.setBackground(new Color(50, 50, 50)); // Dark button background
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Use the category color for hover/active state
        Color activeColor = (category != null) ? category.color : ACCENT;

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(activeColor);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(new Color(50, 50, 50));
            }
        });

        if (category != null) {
            btn.addActionListener(e -> filterProductGrid(category.name));
        }

        return btn;

    }

   // Filters the product grid based on search text OR Category Name
    private void filterProductGrid(String filterTextOrCategoryName) {
        productGridPanel.removeAll();
        String filter = filterTextOrCategoryName.toLowerCase().trim();

        // If the filter matches a known category name, we filter by category.
        // Otherwise, we assume it's a generic product name search.
        boolean isCategoryFilter = false;
        ProductCategory targetCategory = null;
        if (!filter.isEmpty() && !filter.equals("all items")) {
              for (ProductCategory category : ProductCategory.values()) {
                  if (category.name.toLowerCase().equals(filter)) {
                      targetCategory = category;
                      isCategoryFilter = true;
                      break;
                  }
              }
        }


        for (Item item : itemList) {
            boolean shouldShow = false;

            if (filter.isEmpty() || filter.equals("all items")) {
                // Show all items if filter is empty or 'all items'
                shouldShow = true;
            } else if (isCategoryFilter) {
                // Filter by category name
                if (item.category.equals(targetCategory)) {
                    shouldShow = true;
                }
            } else {
                // Filter by product name search text
                if (item.name.toLowerCase().contains(filter)) {
                    shouldShow = true;
                }
            }

            if (shouldShow) {
                productGridPanel.add(createProductCard(item));
            }
        }

        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    private JPanel createProductCard(Item item) {
        // Use item.category.color for styling
        Color categoryColor = item.category.color;

        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setPreferredSize(new Dimension(150, 150)); // Adjusted height

        // --- CHANGE: Make the entire card background the category color ---
        card.setBackground(categoryColor);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Use a semi-transparent overlay panel for content to ensure text readability
        JPanel contentOverlayPanel = new JPanel(new BorderLayout(5, 5));
        contentOverlayPanel.setOpaque(true);
        // A semi-transparent black or white can work, let's try a light gray with some transparency
        // This creates a slightly muted background for text on the colored card
        contentOverlayPanel.setBackground(new Color(255, 255, 255, 200)); // White with 80% opacity
        contentOverlayPanel.setBorder(new EmptyBorder(8, 8, 8, 8)); // Padding for content

        // Item Name & Price (Center)
        JPanel namePricePanel = new JPanel(new GridLayout(2, 1, 0, 0));
        namePricePanel.setOpaque(false); // Keep this transparent

        JLabel nameLabel = new JLabel(item.name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_DARK); // Dark text for contrast against the light overlay
        nameLabel.setToolTipText(item.name);

        JLabel priceLabel = new JLabel("₱" + String.format("%.2f", item.price), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        priceLabel.setForeground(ACCENT); // Can keep ACCENT or use TEXT_DARK for consistency

        namePricePanel.add(nameLabel);
        namePricePanel.add(priceLabel);

        // 3. BOTTOM PANEL FOR STOCK
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false); // Keep this transparent

        JLabel stockLabel = new JLabel("Stock: " + item.stock, SwingConstants.CENTER);
        stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        // Stock label color should also contrast well with the overlay
        stockLabel.setForeground(item.stock > 10 ? SUCCESS : DANGER); // Existing logic is fine

        bottomPanel.add(stockLabel, BorderLayout.CENTER);

        // Assemble contentOverlayPanel
        contentOverlayPanel.add(namePricePanel, BorderLayout.CENTER);
        contentOverlayPanel.add(bottomPanel, BorderLayout.SOUTH); // Stock label at bottom of overlay

        // Add the overlay panel to the main card
        card.add(contentOverlayPanel, BorderLayout.CENTER);

        // Keep your RoundedBorder for the overall shape
        card.setBorder(new RoundedBorder(8));

        item.setStockLabelRef(stockLabel);

        // Action to add item when card is clicked
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addProductToCart(item, 1);
            }
        });

        return card;
    }

    // --- Current Order Cart (Right) ---
    private JPanel createCurrentOrderCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header with Clear Button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Current Order");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);

        clearCartButton = createDangerButton("Clear", DANGER); // Renamed and resized
        clearCartButton.setPreferredSize(new Dimension(80, 30));
        headerPanel.add(clearCartButton, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Cart Items List (Custom JPanel instead of JList)
        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(new Color(250, 250, 250)); // Slightly off-white background
        JScrollPane scroll = new JScrollPane(cartItemsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(30);

        // Total and Buttons Area (Bottom)
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomPanel.setOpaque(false);

        // Total Label
        totalLabel = new JLabel("TOTAL: ₱0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        totalLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        bottomPanel.add(totalLabel, BorderLayout.NORTH);

        // Checkout Button
        checkoutButton = createAccentButton("Complete Transaction", SUCCESS);
        checkoutButton.setPreferredSize(new Dimension(280, 50));

        // Payment/Change/Reference Field Area (New)
        JPanel paymentDetailsPanel = new JPanel(new BorderLayout());
        paymentDetailsPanel.setOpaque(false);

        // We'll put the payment dialog logic into a JDialog, as planned.
        paymentDetailsPanel.add(checkoutButton, BorderLayout.CENTER);

        bottomPanel.add(paymentDetailsPanel, BorderLayout.SOUTH);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // --- Helper for Adding Items ---
    private void addProductToCart(Item item, int qty) {
        if (qty <= 0) return;

        if (item.stock < qty) {
            JOptionPane.showMessageDialog(this, "Insufficient stock! Only " + item.stock + " available for " + item.name + ".");
            return;
        }

        CartItem existingCartItem = null;
        for (CartItem cartItem : cartItems) {
            if (cartItem.name.equals(item.name)) {
                existingCartItem = cartItem;
                break;
            }
        }

        if (existingCartItem != null) {
            existingCartItem.qty += qty;
        } else {
            cartItems.add(new CartItem(item.name, item.price, qty));
        }

        item.stock -= qty;
        item.updateStockLabel();

        updateCartDisplay();
    }

    // --- Helper for Updating Cart Display and Total (Refactored) ---
    private void updateCartDisplay() {
        totalAmount = 0.0;
        cartItemsPanel.removeAll(); // Clear existing panels

        for (CartItem item : cartItems) {
            CartItemPanel itemPanel = new CartItemPanel(item, this);
            itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            cartItemsPanel.add(itemPanel);
            cartItemsPanel.add(Box.createVerticalStrut(5)); // Add space between items
            totalAmount += item.price * item.qty;
        }

        cartItemsPanel.add(Box.createVerticalGlue());

        totalLabel.setText("TOTAL: ₱" + String.format("%.2f", totalAmount));

        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    // --- Item quantity change handlers (called from CartItemPanel) ---
    public void changeItemQuantity(CartItem cartItem, int delta) {
        Item inventoryItem = cartItem.getInventoryItem();
        if (inventoryItem == null) return;

        int newQty = cartItem.qty + delta;

        if (delta > 0) {
            // Adding quantity (check inventory)
            if (inventoryItem.stock < delta) {
                JOptionPane.showMessageDialog(this, "Insufficient stock! Only " + inventoryItem.stock + " available for " + cartItem.name + ".");
                return;
            }
            inventoryItem.stock -= delta;
            cartItem.qty += delta;

        } else if (delta < 0) {
            // Removing quantity
            if (newQty <= 0) {
                // If quantity goes to zero or less, remove item from cart
                removeItemFromCart(cartItem);
                return;
            }
            inventoryItem.stock -= delta; // delta is negative, so this adds stock back
            cartItem.qty += delta;
        }

        inventoryItem.updateStockLabel();
        updateCartDisplay();
    }

    public void removeItemFromCart(CartItem cartItem) {
        Item inventoryItem = cartItem.getInventoryItem();
        if (inventoryItem != null) {
            inventoryItem.stock += cartItem.qty; // Return stock
            inventoryItem.updateStockLabel();
        }

        cartItems.remove(cartItem);
        updateCartDisplay();
    }

    // --- Setup all button listeners for the cart ---
    private void setupCartActions() {
        clearCartButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the entire order?", "Confirm Clear Cart", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Return stock to inventory
                for (CartItem cartItem : cartItems) {
                    Item item = cartItem.getInventoryItem();
                    if (item != null) {
                        item.stock += cartItem.qty;
                        item.updateStockLabel();
                    }
                }
                cartItems.clear();
                updateCartDisplay();
            }
        });

        checkoutButton.addActionListener(e -> {
            if (cartItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "The order is empty. Please add items first.");
                return;
            }

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
            PaymentDialog dialog = new PaymentDialog(parentFrame, totalAmount);
            dialog.setVisible(true);

            if (dialog.isPaymentConfirmed()) {
                // 1. Check/Reserve Reference Code if Online
                if (dialog.getPaymentMethod().startsWith("Online")) {
                    String refCode = dialog.getReferenceCode();
                    if (usedReferenceCodes.contains(refCode)) {
                        JOptionPane.showMessageDialog(this, "Error: The provided reference code is already used. Transaction canceled.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                        // Do not clear cart, allow user to correct payment info
                        return;
                    }
                    usedReferenceCodes.add(refCode); // Reserve code for this transaction
                }

                // 2. Process Successful Transaction
                processTransaction(dialog);

                // 3. Clear Cart for next transaction
                cartItems.clear();
                updateCartDisplay();
            }
        });
    }

    // --- Process Successful Transaction ---
    private void processTransaction(PaymentDialog dialog) {
        orderCounter++;
        String orderNumber = "ORD-" + String.format("%04d", orderCounter);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String transactionDate = sdf.format(new Date());

        // Construct the list of items for the history record (optional detail for full record)
        StringBuilder itemsDetail = new StringBuilder();
        double totalRevenue = 0.0;
        for (CartItem cartItem : cartItems) {
            itemsDetail.append(cartItem.qty).append("x ").append(cartItem.name).append(", ");
            totalRevenue += cartItem.price * cartItem.qty;
        }
        String details = itemsDetail.length() > 0 ? itemsDetail.substring(0, itemsDetail.length() - 2) : "No Items";

        // Create the sales record array:
        // {Order Number, Date/Time, Total Amount, Payment Method, Reference Code/Cash, Cashier}
        String[] salesRecord = {
            orderNumber,
            transactionDate,
            String.format("%.2f", totalRevenue), // Use the calculated totalRevenue
            dialog.getPaymentMethod(),
            dialog.getPaymentMethod().startsWith("Online") ? dialog.getReferenceCode() : "Cash: " + String.format("%.2f", dialog.getCashReceived()),
            this.username
            // You can add 'details' if your salesHistory table supports it
        };

        salesHistory.add(salesRecord); // THIS IS THE KEY LINE TO POPULATE HISTORY

        // 3. Display the Receipt/Summary in a Pop-up (long receipt style)
        String receipt = generateReceipt(orderNumber, dialog);
        JTextArea receiptDisplay = new JTextArea(receipt);
        receiptDisplay.setFont(new Font("Monospaced", Font.PLAIN, 13));
        receiptDisplay.setEditable(false);

        // Use a JScrollPane with preferred size for the long receipt effect
        JScrollPane receiptScroll = new JScrollPane(receiptDisplay);
        receiptScroll.setPreferredSize(new Dimension(350, 500)); // Fixed width, long height

        JOptionPane.showMessageDialog(this, receiptScroll, "Transaction Complete - Receipt", JOptionPane.INFORMATION_MESSAGE);
    } // <--- CORRECTED: ADDED MISSING CLOSING BRACE

    // --- Generates the Receipt Text ---
    private String generateReceipt(String orderNumber, PaymentDialog dialog) {
        StringBuilder sb = new StringBuilder();
        sb.append("         DAGAMIS SASH & WOODWORK\n");
        sb.append("            OFFICIAL RECEIPT\n");
        sb.append("========================================\n");
        sb.append(String.format("Order #: %s\n", orderNumber));
        sb.append(String.format("Date: %s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        sb.append(String.format("Cashier: %s\n", username));
        sb.append("----------------------------------------\n");
        sb.append("ITEM               QTY   PRICE     AMOUNT\n");
        sb.append("----------------------------------------\n");

        for (CartItem item : cartItems) {
            double lineTotal = item.price * item.qty;
            String itemName = item.name.substring(0, Math.min(item.name.length(), 18));
            sb.append(String.format("%-18s %3d %7.2f %9.2f\n",
                    itemName,
                    item.qty,
                    item.price,
                    lineTotal));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("%-30s %9s\n", "TOTAL:", "₱" + String.format("%.2f", totalAmount)));

        if (dialog.getPaymentMethod().startsWith("Cash")) {
            double tendered = dialog.getCashReceived();
            sb.append(String.format("%-30s %9s\n", "RECEIVED:", "₱" + String.format("%.2f", tendered)));
            sb.append(String.format("%-30s %9s\n", "CHANGE:", "₱" + String.format("%.2f", dialog.getChange())));
        } else {
            sb.append(String.format("%-30s %9s\n", "PAID VIA:", dialog.getPaymentMethod()));
            sb.append(String.format("REF. CODE: %s\n", dialog.getReferenceCode()));
        }

        sb.append("========================================\n");
        sb.append("           *** THANK YOU ***\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    // [createTransactionsPanelMaterial, styleTableMaterial, RoundedBorder, loadTransactions remain the same]
    private JPanel createTransactionsPanelMaterial(Color APP_BG, Color CARD) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(APP_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Transaction History", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        top.add(title, BorderLayout.WEST);
        panel.add(top, BorderLayout.NORTH);

        // NOTE: The columns here MUST match the data added in salesRecord:
        // {Order #, Date/Time, Total Amount, Payment Method, Reference Code/Cash, Cashier}
        String[] cols = {"Order #", "Date/Time", "Total Amount", "Payment Method", "Ref/Cash", "Cashier"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(36);
        styleTableMaterial(table, ACCENT);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new CompoundBorder(new EmptyBorder(8, 8, 8, 8),
                new LineBorder(new Color(230, 230, 230))));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void styleTableMaterial(JTable table, Color accent) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 245));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(221, 235, 255));
        table.setSelectionForeground(Color.BLACK);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
    }

    class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() { return false; }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    private void loadTransactions() {
        JPanel panel = (JPanel) mainPanel.getComponent(1);
        JScrollPane scroll = (JScrollPane) ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JTable table = (JTable) scroll.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (String[] sale : salesHistory) {
            model.addRow(sale);
        }
    }

    // --- Payment Dialog Class (Refactored) ---
    private class PaymentDialog extends JDialog {
        private double totalAmount;
        private double change = 0.0;
        private boolean confirmed = false;
        private String paymentMethod = "Cash Payment";
        private String referenceCode = "";
        private double cashReceived = 0.0; // Added for receipt info

        private JTextField cashReceivedField;
        private JTextField refCodeField;
        private JLabel remainingLabel;
        private JRadioButton cashRadio;
        private JRadioButton onlineRadio;
        private JButton confirmBtn;


        public PaymentDialog(JFrame parent, double total) {
            super(parent, "Process Payment", true);
            this.totalAmount = total;
            initComponents();
            setSize(400, 500);
            setLocationRelativeTo(parent);
        }

        private void initComponents() {
            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(Color.WHITE);

            // Header (Remains largely the same)
            JPanel header = new JPanel();
            header.setBackground(new Color(240, 240, 240));
            header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            JLabel totalLbl = new JLabel("TOTAL: ₱" + String.format("%.2f", totalAmount));
            totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
            totalLbl.setForeground(HEADER);
            header.add(totalLbl);
            root.add(header, BorderLayout.NORTH);

            // Center Content
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            content.setBackground(Color.WHITE);

            // Payment Method
            content.add(new JLabel("Payment Method:"));
            ButtonGroup group = new ButtonGroup();
            cashRadio = new JRadioButton("Cash Payment");
            onlineRadio = new JRadioButton("Online Payment (Gcash)");
            group.add(cashRadio);
            group.add(onlineRadio);
            cashRadio.setSelected(true);
            cashRadio.setBackground(Color.WHITE);
            onlineRadio.setBackground(Color.WHITE);

            JPanel radioPanel = new JPanel(new GridLayout(2, 1));
            radioPanel.setBorder(new LineBorder(new Color(220, 220, 220)));
            radioPanel.add(cashRadio);
            radioPanel.add(onlineRadio);
            radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(radioPanel);

            content.add(Box.createVerticalStrut(20));

            // Cash Received
            content.add(new JLabel("Cash Received:"));
            cashReceivedField = new JTextField("0.00");
            cashReceivedField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            cashReceivedField.setMaximumSize(new Dimension(300, 36));
            cashReceivedField.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(cashReceivedField);

            content.add(Box.createVerticalStrut(20));

            // Reference Code
            content.add(new JLabel("Reference Code:"));
            refCodeField = new JTextField();
            refCodeField.setMaximumSize(new Dimension(300, 36));
            refCodeField.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(refCodeField);
            // Validation indicator label (Placeholder for the "red line" effect)
            JLabel refErrorLabel = new JLabel("");
            refErrorLabel.setForeground(DANGER);
            refErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            content.add(refErrorLabel);

            root.add(content, BorderLayout.CENTER);

            // Footer
            JPanel footer = new JPanel(new BorderLayout(0, 10));
            footer.setBackground(Color.WHITE);
            footer.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

            remainingLabel = new JLabel("Remaining: ₱" + String.format("%.2f", totalAmount), SwingConstants.CENTER);
            remainingLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            remainingLabel.setForeground(DANGER);
            footer.add(remainingLabel, BorderLayout.NORTH);

            confirmBtn = createAccentButton("CONFIRM PAYMENT", new Color(67, 160, 71));
            confirmBtn.setPreferredSize(new Dimension(300, 50));
            footer.add(confirmBtn, BorderLayout.SOUTH);

            root.add(footer, BorderLayout.SOUTH);
            setContentPane(root);

            // Listeners
            cashRadio.addActionListener(e -> togglePaymentFields(true, refErrorLabel));
            onlineRadio.addActionListener(e -> togglePaymentFields(false, refErrorLabel));

            // Cash Calculation Listener
            KeyAdapter cashCalcListener = new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    calculateChange(confirmBtn);
                }
            };
            cashReceivedField.addKeyListener(cashCalcListener);

            // Reference Code Validation Listener
            refCodeField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    validateReferenceCode(refCodeField.getText(), refErrorLabel, confirmBtn);
                }
            });

            confirmBtn.addActionListener(e -> confirmPayment());

            togglePaymentFields(true, refErrorLabel); // Ensures initial state is correct (Cash)
        }

        private void togglePaymentFields(boolean isCash, JLabel errorLabel) {
            cashReceivedField.setEnabled(isCash);
            refCodeField.setEnabled(!isCash);
            errorLabel.setText(""); // Clear error on switch

            if (isCash) {
                paymentMethod = "Cash Payment";
                refCodeField.setText("");
                calculateChange(confirmBtn); // Check for zero change
            } else {
                paymentMethod = "Online Payment";
                cashReceivedField.setText(String.format("%.2f", totalAmount)); // Pre-fill with total (paid in full)
                cashReceived = totalAmount; // Update internal variable
                change = 0.0;
                remainingLabel.setText("Payment Full");
                remainingLabel.setForeground(SUCCESS);
                refCodeField.setText("");
                validateReferenceCode("", errorLabel, confirmBtn); // Initial check (empty field)
            }
        }

        private void calculateChange(JButton confirmBtn) {
            if (cashRadio.isSelected()) {
                try {
                    cashReceived = Double.parseDouble(cashReceivedField.getText().trim());
                    change = cashReceived - totalAmount;

                    if (change >= 0) {
                        remainingLabel.setText("Change: ₱" + String.format("%.2f", change));
                        remainingLabel.setForeground(SUCCESS);
                        confirmBtn.setEnabled(true);
                    } else {
                        remainingLabel.setText("Remaining: ₱" + String.format("%.2f", Math.abs(change)));
                        remainingLabel.setForeground(DANGER);
                        confirmBtn.setEnabled(false);
                    }
                } catch (NumberFormatException ex) {
                    remainingLabel.setText("Invalid Cash Amount");
                    remainingLabel.setForeground(DANGER);
                    confirmBtn.setEnabled(false);
                    change = -1.0;
                    cashReceived = 0.0;
                }
            }
        }

        private void validateReferenceCode(String code, JLabel errorLabel, JButton confirmBtn) {
            if (onlineRadio.isSelected()) {
                referenceCode = code.trim();
                if (referenceCode.isEmpty()) {
                    errorLabel.setText("Reference code cannot be empty.");
                    confirmBtn.setEnabled(false);
                } else if (usedReferenceCodes.contains(referenceCode)) {
                    errorLabel.setText("Reference code is already used.");
                    confirmBtn.setEnabled(false);
                } else {
                    errorLabel.setText("");
                    confirmBtn.setEnabled(true);
                }
            }
        }

        private void confirmPayment() {
            if (cashRadio.isSelected()) {
                calculateChange(confirmBtn);
                if (change >= 0) {
                    confirmed = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Tendered amount is insufficient.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (onlineRadio.isSelected()) {
                // IMPORTANT: Use the actual error label when validating before confirming
                // Creating a new JLabel() here is wrong as it bypasses the visual check.
                // However, since we re-enabled the button based on the listener, a simple re-check works.
                if (refCodeField.getText().trim().isEmpty() || usedReferenceCodes.contains(refCodeField.getText().trim())) {
                     JOptionPane.showMessageDialog(this, "Please provide a unique reference code.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                     return;
                }
                referenceCode = refCodeField.getText().trim();
                confirmed = true;
                dispose();

            }
        }


        public boolean isPaymentConfirmed() { return confirmed; }
        public double getChange() { return change; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getReferenceCode() { return referenceCode; }
        public double getCashReceived() { return cashReceived; } // Getter for the receipt
    }
}
// Note: You must also have the Login class and the CartItemPanel class defined in separate files or as nested public static classes for this code to compile fully.