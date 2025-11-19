import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class CashierDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String username, role;

    // POS components
    private DefaultTableModel posTableModel;
    private JComboBox<String> itemsCombo;
    private JTextArea receiptArea;
    private double totalAmount = 0.0;

    class Item {
        String name;
        double price;
        int stock;

        Item(String n, double p, int s) {
            name = n;
            price = p;
            stock = s;
        }
    }

    private java.util.List<Item> itemList = new ArrayList<>();
    private java.util.List<String[]> salesHistory = new ArrayList<>(); 

    public CashierDashboard(String username, String role) {
        this.username = username;
        this.role = role;

        initializeItems();
        initComponents();
    }

    private void initializeItems() {
        itemList.add(new Item("Plywood 1/4", 450, 20));
        itemList.add(new Item("Plywood 1/2", 650, 15));
        itemList.add(new Item("Coco Lumber 2x2", 80, 50));
        itemList.add(new Item("Nails 1 inch", 50, 100));
        itemList.add(new Item("Marine Plywood", 950, 10));
    }

    private void initComponents() {
        setTitle("Dagamis Sash & Woodwork - Cashier Dashboard");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setVisible(true);

        // Material palette
        Color APP_BG = new Color(250, 250, 250);
        Color SIDEBAR = new Color(33, 33, 33);
        Color HEADER = new Color(48, 63, 159); 
        Color CARD = new Color(255, 255, 255);
        Color ACCENT = new Color(33, 150, 243); 
        Color DANGER = new Color(211, 47, 47);
        Color SUCCESS = new Color(56, 142, 60);

        getContentPane().setBackground(APP_BG);

        // ------------------ Sidebar ------------------
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
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

        JLabel headerTitle = new JLabel("  Cashier Dashboard");
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

        mainPanel.add(createPOSPanelMaterial(APP_BG, CARD, ACCENT, DANGER, SUCCESS), "POS");
        mainPanel.add(createTransactionsPanelMaterial(APP_BG, CARD), "TRANS");

        add(mainPanel, BorderLayout.CENTER);

        // ------------------ Button actions ------------------
        posBtn.addActionListener(e -> {
            refreshItems();
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
                new Login().setVisible(true);
            } catch (Exception ex) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

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

    // ========== POS Panel ==========
private JPanel createPOSPanelMaterial(Color APP_BG, Color CARD, Color ACCENT, Color DANGER, Color SUCCESS) {
    JPanel root = new JPanel(new GridBagLayout());
    root.setBackground(APP_BG);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(14, 14, 14, 14);
    gbc.fill = GridBagConstraints.BOTH;

    // ------------------ Left card ------------------
    JPanel leftCard = new JPanel(new GridBagLayout());
    leftCard.setBackground(CARD);
    leftCard.setBorder(new CompoundBorder(new EmptyBorder(16, 16, 16, 16),
            new LineBorder(new Color(230, 230, 230))));
    leftCard.setPreferredSize(new Dimension(760, 620));
    GridBagConstraints l = new GridBagConstraints();
    l.insets = new Insets(10, 10, 10, 10);
    l.fill = GridBagConstraints.HORIZONTAL;
    l.gridx = 0;
    l.gridy = 0;

    JLabel title = new JLabel("Point of Sale (POS)");
    title.setFont(new Font("Segoe UI", Font.BOLD, 22));
    leftCard.add(title, l);

    // Item selection
    l.gridy++;
    l.gridwidth = 1;
    l.weightx = 0.6;
    itemsCombo = new JComboBox<>();
    itemsCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    refreshItems();
    JPanel itemWrap = new JPanel(new BorderLayout(8, 8));
    itemWrap.setOpaque(false);
    itemWrap.add(itemsCombo, BorderLayout.CENTER);
    itemWrap.setBorder(new EmptyBorder(8, 0, 0, 0));
    leftCard.add(itemWrap, l);

    // Quantity spinner
    l.gridx = 1;
    l.weightx = 0.15;
    JSpinner qtySpin = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    qtySpin.setPreferredSize(new Dimension(80, 32));
    leftCard.add(qtySpin, l);

    // Add button
    l.gridx = 2;
    l.weightx = 0.25;
    JButton addBtn = createPrimaryButton("Add", ACCENT);
    addBtn.setPreferredSize(new Dimension(110, 34));
    leftCard.add(addBtn, l);

    // Table
    l.gridx = 0;
    l.gridy++;
    l.gridwidth = 3;
    l.weighty = 1.0; 
    l.fill = GridBagConstraints.BOTH;

    posTableModel = new DefaultTableModel(new Object[]{"Item", "Price", "Qty", "Total", "Remove"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    JTable table = new JTable(posTableModel);
    table.setRowHeight(36);
    table.setFillsViewportHeight(true);
    styleTableMaterial(table, ACCENT);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(new EmptyBorder(8, 8, 8, 8));
    leftCard.add(scroll, l);

    l.gridy++;
    l.weighty = 0.0; 
    l.fill = GridBagConstraints.NONE; 
    l.anchor = GridBagConstraints.CENTER;
    
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    buttonPanel.setOpaque(false);
    
    JButton clrBtn = createDangerButton("Clear Cart", DANGER);
    clrBtn.setPreferredSize(new Dimension(150, 44));
    
    JButton chkBtn = createAccentButton("Checkout", new Color(67, 160, 71)); 
    chkBtn.setPreferredSize(new Dimension(150, 44));
    
    buttonPanel.add(clrBtn);
    buttonPanel.add(chkBtn);
    
    leftCard.add(buttonPanel, l);


    // ------------------ Right card: Total & Receipt ------------------
    JPanel rightCard = new JPanel(new GridBagLayout());
    rightCard.setBackground(CARD);
    rightCard.setBorder(new CompoundBorder(new EmptyBorder(16, 16, 16, 16),
            new LineBorder(new Color(230, 230, 230))));
    rightCard.setPreferredSize(new Dimension(340, 620));
    GridBagConstraints r = new GridBagConstraints();
    r.insets = new Insets(12, 12, 12, 12);
    r.fill = GridBagConstraints.HORIZONTAL;
    r.gridx = 0;
    r.gridy = 0;

    // Total Label
    JLabel totalLabel = new JLabel("Total: ₱0.00");
    totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    rightCard.add(totalLabel, r);
    
    r.gridy++;
    r.fill = GridBagConstraints.HORIZONTAL;
    rightCard.add(Box.createVerticalStrut(8), r);

    // Receipt area 
    r.gridy++;
    JLabel receiptLbl = new JLabel("Receipt");
    receiptLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
    rightCard.add(receiptLbl, r);

    r.gridy++;
    r.weighty = 1.0; 
    r.fill = GridBagConstraints.BOTH;
    receiptArea = new JTextArea();
    receiptArea.setEditable(false);
    receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    JScrollPane receiptScroll = new JScrollPane(receiptArea);
    receiptScroll.setPreferredSize(new Dimension(300, 450));
    rightCard.add(receiptScroll, r);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.75;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    root.add(leftCard, gbc);

    gbc.gridx = 1;
    gbc.weightx = 0.25;
    root.add(rightCard, gbc);

    // ------------------ actions ------------------
    addBtn.addActionListener(e -> {
        String selectedItemName = (String) itemsCombo.getSelectedItem();
        if (selectedItemName == null) return;

        Item item = null;
        for (Item it : itemList) {
            if (it.name.equals(selectedItemName)) {
                item = it;
                break;
            }
        }

        if (item == null) return;

        int qty = (int) qtySpin.getValue();

        if (qty > item.stock) {
            JOptionPane.showMessageDialog(root, "Insufficient stock! Only " + item.stock + " available.");
            return;
        }

        double total = qty * item.price;
        posTableModel.addRow(new Object[]{
            item.name,
            "₱" + String.format("%.2f", item.price),
            qty,
            "₱" + String.format("%.2f", total),
            "✖"
        });

        item.stock -= qty;
        totalAmount += total;
        
        refreshItems(); 

        totalLabel.setText("Total: ₱" + String.format("%.2f", totalAmount));
        updateReceipt();
    });

    table.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (row >= 0 && col == 4) {
                String name = (String) posTableModel.getValueAt(row, 0);
                int qty = (int) posTableModel.getValueAt(row, 2);
                String rowTotalStr = posTableModel.getValueAt(row, 3).toString().replace("₱", "").replace(",", "");
                double rowTotal = 0.0;
                try {
                    rowTotal = Double.parseDouble(rowTotalStr);
                } catch (Exception ex) { /* ignore */ }

                // Return stock
                for (Item it : itemList) {
                    if (it.name.equals(name)) {
                        it.stock += qty;
                    }
                }

                posTableModel.removeRow(row);

                totalAmount -= rowTotal;
                if (totalAmount < 0) totalAmount = 0;
                totalLabel.setText("Total: ₱" + String.format("%.2f", totalAmount));
                updateReceipt();
                refreshItems();
            }
        }
    });

    chkBtn.addActionListener(e -> {
        if (posTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(root, "Cart is empty!"); 
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(root);

        PaymentDialog dialog = new PaymentDialog(parentFrame, totalAmount);
        dialog.setVisible(true);

        if (dialog.isPaymentConfirmed()) {
            String orderNumber = "ORD-" + System.currentTimeMillis();

            for (int i = 0; i < posTableModel.getRowCount(); i++) {
                salesHistory.add(new String[]{
                    orderNumber,
                    posTableModel.getValueAt(i, 0).toString(),
                    posTableModel.getValueAt(i, 2).toString(),
                    posTableModel.getValueAt(i, 3).toString(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())
                });
            }

            receiptArea.append("\n\n*** PAYMENT SUCCESSFUL ***\n");
            receiptArea.append("Order #: " + orderNumber + "\n");
            receiptArea.append("Payment: " + dialog.getPaymentMethod() + "\n");
            
            if (dialog.getPaymentMethod().startsWith("Cash")) {
                receiptArea.append("Tendered: ₱" + String.format("%.2f", totalAmount + dialog.getChange()) + "\n");
                receiptArea.append("Change: ₱" + String.format("%.2f", dialog.getChange()) + "\n");
            }
            
            JOptionPane.showMessageDialog(root, "Checkout successful!");

            posTableModel.setRowCount(0);
            totalAmount = 0;
            totalLabel.setText("Total: ₱0.00");
            refreshItems();
        }
    }); 

    return root;
}

    // ========== Transaction Panel ==========
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

        String[] cols = {"Order #", "Item", "Qty", "Total", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(36);
        styleTableMaterial(table, new Color(33, 150, 243));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new CompoundBorder(new EmptyBorder(8, 8, 8, 8),
                new LineBorder(new Color(230, 230, 230))));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
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
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
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
        JScrollPane scroll = (JScrollPane) panel.getComponent(1);
        JTable table = (JTable) scroll.getViewport().getView();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (String[] sale : salesHistory) {
            model.addRow(sale);
        }
    }

    private void refreshItems() {
        if (itemsCombo == null) return;
        itemsCombo.removeAllItems();
        for (Item item : itemList) {
            if (item.stock > 0) itemsCombo.addItem(item.name);
        }
    }

    private void updateReceipt() {
        if (posTableModel == null || receiptArea == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("DAGAMIS SASH & WOODWORK\n");
        sb.append("=============================\n");
        sb.append("Date: ").append(new Date()).append("\n");
        sb.append("Cashier: ").append(username).append("\n\n");

        for (int i = 0; i < posTableModel.getRowCount(); i++) {
            sb.append(posTableModel.getValueAt(i, 0)).append("  x")
                    .append(posTableModel.getValueAt(i, 2)).append("  = ")
                    .append(posTableModel.getValueAt(i, 3)).append("\n");
        }

        sb.append("\nTOTAL: ₱").append(String.format("%.2f", totalAmount)).append("\n");
        sb.append("=============================\n");

        receiptArea.setText(sb.toString());
    }


    // ----------------- main -----------------
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        SwingUtilities.invokeLater(() -> new CashierDashboard("cashier", "Staff"));
    }
}
