import java.awt.*; 
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ItemsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch; // Used for searching items
    private JComboBox<String> categoryDropdown; // Used for filtering
    private JButton addBtn;
    private CategoriesPanel categoriesPanel;

    // Use a proper data structure for items
    private ArrayList<Item> items = new ArrayList<>();
    private int nextId = 1; // Simple ID counter

    // Inner class to represent an Item
    public static class Item {
        int id;
        String name;
        String category;
        double price;
        int stock; // Stock is managed/viewed here but added/edited in modal

        public Item(int id, String name, String category, double price, int stock) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }
    }

    public ItemsPanel(CategoriesPanel categoriesPanel) {
        this.categoriesPanel = categoriesPanel;
        setLayout(new BorderLayout(20, 20)); // Use BorderLayout for professional design
        setBackground(new Color(242, 245, 249));

        // === TITLE BAR (Similar to InventoryPanel) ===
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(242, 245, 249));
        titleBar.setBorder(new EmptyBorder(10, 20, 0, 20));

        JLabel title = new JLabel("Items Management");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        title.setForeground(new Color(55, 71, 79));

        titleBar.add(title, BorderLayout.WEST);
        
        // === TOOLBAR/SEARCH PANEL ===
        JPanel toolBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolBarPanel.setBackground(Color.WHITE);
        
        // Search Field
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                "Search Item Name", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 12), new Color(100, 100, 100)));
        toolBarPanel.add(txtSearch);

        // Category Filter
        JLabel lblCategory = new JLabel("Category Filter:");
        lblCategory.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toolBarPanel.add(lblCategory);
        
        categoryDropdown = new JComboBox<>();
        categoryDropdown.setPreferredSize(new Dimension(150, 30));
        toolBarPanel.add(categoryDropdown);

        // Add Item Button (Primary Action)
        addBtn = new JButton("Add New Item");
        addBtn.setPreferredSize(new Dimension(150, 40));
        addBtn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        addBtn.setBackground(new Color(76, 175, 80)); // Green color for adding
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toolBarPanel.add(addBtn);

        // Styling the toolbar panel border
        toolBarPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(10, 20, 10, 20)
        ));
        
        // Add components to the main panel
        add(titleBar, BorderLayout.NORTH);
        add(toolBarPanel, BorderLayout.NORTH); // This placement will override titleBar, let's fix the layout

        // Re-arranging layout: titleBar goes on top, searchPanel below it (using a wrapper)
        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.add(titleBar, BorderLayout.NORTH);
        topWrapper.add(toolBarPanel, BorderLayout.SOUTH);
        add(topWrapper, BorderLayout.NORTH);

        // === TABLE ===
        String[] columns = {"ID", "Name", "Category", "Price", "Stock", "Edit", "Delete"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { 
                // Only the 'Edit' and 'Delete' action cells are editable/clickable
                return col == 5 || col == 6; 
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        // Setup custom button renderer and editor for the action columns
        table.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit", new Color(33, 150, 243))); // Blue
        table.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete", new Color(244, 67, 54))); // Red

        // The button editor must invoke the method using the row index
        table.getColumn("Edit").setCellEditor(new ButtonEditor(new JTextField(), "Edit", e -> editItem(table.getSelectedRow()), new Color(33, 150, 243)));
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JTextField(), "Delete", e -> deleteItem(table.getSelectedRow()), new Color(244, 67, 54)));


        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        table.getColumnModel().getColumn(5).setPreferredWidth(80); // Edit
        table.getColumnModel().getColumn(6).setPreferredWidth(80); // Delete

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 20, 20));

        add(scroll, BorderLayout.CENTER);

        // --- Initial Data and Actions ---
        loadCategories();
        
        // Sample data
        items.add(new Item(nextId++, "Plywood 1/4", "Plywoods", 450.00, 20));
        items.add(new Item(nextId++, "Coco Lumber 2x2", "Lumbers", 80.00, 50));
        items.add(new Item(nextId++, "Nails 1 inch", "Hardware", 50.00, 100));
        
        loadItems(); 
        
        addBtn.addActionListener(e -> showAddItemModal());
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchItems(txtSearch.getText());
            }
        });
        categoryDropdown.addActionListener(e -> filterItems());

    }

    // --- Core Item Logic ---
    
    public void loadCategories() {
        categoryDropdown.removeAllItems();
        categoryDropdown.addItem("All Categories"); // Default filter option
        for (String cat : categoriesPanel.getCategories()) {
            categoryDropdown.addItem(cat);
        }
    }

    private void loadItems() {
        model.setRowCount(0);
        for (Item item : items) {
            model.addRow(new Object[]{
                item.id, 
                item.name, 
                item.category, 
                String.format("₱%.2f", item.price), 
                item.stock,
                "Edit", // Action button text
                "Delete" // Action button text
            });
        }
    }
    
    private void filterItems() {
        searchItems(txtSearch.getText()); // Apply current search query after category filter
    }
    
    private void searchItems(String query) {
        query = query.trim().toLowerCase();
        String selectedCategory = (String) categoryDropdown.getSelectedItem();
        boolean filterByCategory = selectedCategory != null && !selectedCategory.equals("All Categories");
        
        model.setRowCount(0);
        
        for (Item item : items) {
            boolean matchesName = item.name.toLowerCase().contains(query);
            boolean matchesCategory = !filterByCategory || item.category.equals(selectedCategory);
            
            if (matchesName && matchesCategory) {
                model.addRow(new Object[]{
                    item.id, 
                    item.name, 
                    item.category, 
                    String.format("₱%.2f", item.price), 
                    item.stock,
                    "Edit",
                    "Delete"
                });
            }
        }
    }


    // --- MODAL & Action Handlers ---

    private void showAddItemModal() {
        // Implement the Add Item Modal (JDialog) here
        // The modal must take inputs for Name, Price, Stock, Category
        String newItemName = JOptionPane.showInputDialog(this, "Enter New Item Name:");
        if (newItemName != null && !newItemName.trim().isEmpty()) {
            // For now, add a placeholder item
            items.add(new Item(nextId++, newItemName, "New Category", 100.00, 5));
            loadItems();
            JOptionPane.showMessageDialog(this, "Item added (Placeholder). ID: " + (nextId-1), "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editItem(int row) {
        // Implement the Edit Item Modal (JDialog) here
        if (row >= 0 && row < items.size()) {
            Item itemToEdit = items.get(row);
            // In a real application, you pass itemToEdit to the EditModal constructor
            String newPrice = JOptionPane.showInputDialog(this, "Edit Price for " + itemToEdit.name + ":", itemToEdit.price);
            
            if (newPrice != null) {
                try {
                    itemToEdit.price = Double.parseDouble(newPrice);
                    loadItems();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteItem(int row) {
        if (row >= 0 && row < items.size()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete '" + items.get(row).name + "'?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                items.remove(row);
                loadItems();
            }
        }
    }
    
    // --- Custom Table Button Renderer and Editor Classes (Necessary for buttons in JTable) ---

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text, Color background) {
            setOpaque(true);
            setText(text);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.WHITE);
            setBackground(background);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private ActionListener actionListener;

        public ButtonEditor(JTextField textField, String label, ActionListener listener, Color background) {
            super(textField);
            this.actionListener = listener;
            button = new JButton(label);
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setBackground(background);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            button.addActionListener(e -> {
                fireEditingStopped(); // Stop editing immediately
                actionListener.actionPerformed(e);
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            // This ensures the button is drawn when being clicked
            return button;
        }
    }
}