import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

public class CategoriesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField categoryField;
    private JButton addBtn, editBtn;

    private ItemsPanel itemsPanel;
    private ArrayList<String> categories = new ArrayList<>();

    public CategoriesPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        // -------------------------
        // TITLE
        // -------------------------
        JLabel title = new JLabel("Categories");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // -------------------------
        // INPUT PANEL
        // -------------------------
     JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        categoryField = new JTextField(25); // wider input field
        categoryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryField.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
        "Category Name",
        0, 0,
        new Font("Segoe UI", Font.PLAIN, 12),
        new Color(100, 100, 100)
));

        addBtn = createButton("Add Category", new Color(33, 150, 243), 140, 40);
        editBtn = createButton("Edit Category", new Color(102, 187, 106), 140, 40);

        inputPanel.add(categoryField);
        inputPanel.add(addBtn);
        inputPanel.add(editBtn);

        add(inputPanel, BorderLayout.SOUTH);

        

        // -------------------------
        // TABLE
        // -------------------------
        String[] columns = {"ID", "Name", "Action"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) {
                return col == 2; // only the delete button column
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(33, 33, 33));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 230, 230));
        table.setGridColor(new Color(200, 200, 200));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scroll, BorderLayout.CENTER);

        // -------------------------
        // ACTION LISTENERS
        // -------------------------
        addBtn.addActionListener(e -> addCategory());
        editBtn.addActionListener(e -> editCategory());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 2) { // Delete button
                    deleteCategory(row);
                } else if (col == 1) { // Click to edit
                    categoryField.setText((String) model.getValueAt(row, 1));
                }
            }
        });

        // Sample data
        categories.add("Doors");
        categories.add("Furniture");
        categories.add("Windows");
        loadCategories();
    }

    // -------------------------
    // Create button with modern style
    // -------------------------
    private JButton createButton(String text, Color color, int width, int height) {
    JButton btn = new JButton(text);
    btn.setPreferredSize(new Dimension(width, height)); // expanded size
    btn.setBackground(color);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btn.setBorder(new LineBorder(color.darker(), 1, true));
    btn.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) { btn.setBackground(color.darker()); }
        public void mouseExited(MouseEvent e) { btn.setBackground(color); }
    });
    return btn;
}

    // -------------------------
    // Set ItemsPanel reference (for updating dropdown)
    // -------------------------
    public void setItemsPanel(ItemsPanel itemsPanel) { // <--- The required method definition
    this.itemsPanel = itemsPanel;
   }

    // -------------------------
    // Load categories into table
    // -------------------------
    public void loadCategories() {
        model.setRowCount(0);
        for (int i = 0; i < categories.size(); i++) {
            model.addRow(new Object[]{i + 1, categories.get(i), "Delete"});
        }
        if (itemsPanel != null) {
            itemsPanel.loadCategories();
        }
    }

    // -------------------------
    // Add category
    // -------------------------
    private void addCategory() {
        String name = categoryField.getText().trim();
        if (!name.isEmpty()) {
            categories.add(name);
            categoryField.setText("");
            loadCategories();
        }
    }

    // -------------------------
    // Edit category
    // -------------------------
    private void editCategory() {
        int row = table.getSelectedRow();
        if (row != -1) {
            String newName = categoryField.getText().trim();
            if (!newName.isEmpty()) {
                categories.set(row, newName);
                categoryField.setText("");
                loadCategories();
            }
        }
    }

    // -------------------------
    // Delete category
    // -------------------------
    private void deleteCategory(int row) {
        if (row >= 0 && row < categories.size()) {
            categories.remove(row);
            loadCategories();
        }
    }

    // -------------------------
    // Getter for ItemsPanel
    // -------------------------
    public ArrayList<String> getCategories() {
        return new ArrayList<>(categories);
    }
}


