import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class InventoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private ArrayList<InventoryItem> items = new ArrayList<>();

    public InventoryPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(242, 245, 249));

        // === TITLE BAR ===
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(242, 245, 249));
        titleBar.setBorder(new EmptyBorder(10, 20, 0, 20));

        JLabel title = new JLabel("Inventory");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        title.setForeground(new Color(55, 71, 79));

        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // === SEARCH BAR PANEL ===
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(Color.WHITE);

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                "Search Item or Category",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 12),
                new Color(100, 100, 100)
        ));

        JButton btnSearch = new JButton("Search");
        btnSearch.setPreferredSize(new Dimension(120, 45));
        btnSearch.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        btnSearch.setBackground(new Color(33, 150, 243));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setBorder(new LineBorder(new Color(21, 101, 192), 1, true));

        btnSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSearch.setBackground(new Color(30, 136, 229));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSearch.setBackground(new Color(33, 150, 243));
            }
        });

        searchPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(10, 20, 10, 20)
        ));

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.SOUTH);

        // === TABLE DESIGN ===
        String[] columns = {"ID", "Item Name", "Category", "Quantity"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(new Color(60, 60, 60));

        table.setSelectionBackground(new Color(200, 220, 250));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(225, 225, 225));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 20, 20));

        add(scroll, BorderLayout.CENTER);

        // === SAMPLE DATA ===
        items.add(new InventoryItem(1, "Wood Door", "Doors", 15));
        items.add(new InventoryItem(2, "Office Chair", "Furniture", 20));
        items.add(new InventoryItem(3, "Window Glass", "Windows", 50));

        loadItems();

        // SEARCH ACTION
        btnSearch.addActionListener(e -> search());
    }

    private void loadItems() {
        model.setRowCount(0);
        for (InventoryItem item : items) {
            model.addRow(new Object[]{item.id, item.name, item.category, item.quantity});
        }
    }

    private void search() {
        String query = txtSearch.getText().trim().toLowerCase();
        model.setRowCount(0);

        for (InventoryItem item : items) {
            if (item.name.toLowerCase().contains(query) ||
                item.category.toLowerCase().contains(query)) {

                model.addRow(new Object[]{
                    item.id,
                    item.name,
                    item.category,
                    item.quantity
                });
            }
        }
    }

    // === MODEL CLASS ===
    static class InventoryItem {
        int id;
        String name, category;
        int quantity;

        public InventoryItem(int id, String name, String category, int quantity) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.quantity = quantity;
        }
    }
}
