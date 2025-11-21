import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CartItemPanel extends JPanel {
    private CashierDashboard.CartItem cartItem;
    private CashierDashboard dashboard;
    private JLabel qtyLabel;
    private JLabel priceLabel;

    public CartItemPanel(CashierDashboard.CartItem item, CashierDashboard db) {
        this.cartItem = item;
        this.dashboard = db;

        // Use GridBagLayout for flexible, two-column layout
        setLayout(new GridBagLayout());
        // Reduced vertical padding (top, bottom) for a more compact look
        setBorder(new EmptyBorder(5, 10, 5, 10));
        setBackground(Color.WHITE);

        // Ensure this panel aligns LEFT when placed in the parent's vertical BoxLayout
        this.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 3, 0, 3); // Reduced internal padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure components fill their allocated space

        // --- 1. Left Section: Item Name and Price (INFO PANEL) ---
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(item.name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(CashierDashboard.TEXT_DARK);

        priceLabel = new JLabel(); // Initialize the price label
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceLabel.setForeground(CashierDashboard.ACCENT);

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        gbc.gridx = 0;
        gbc.weightx = 1.0; // Takes all available horizontal space
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.WEST; // Align to the left
        add(infoPanel, gbc);

        // --- 2. Right Section: +/- Controls (CONTROL PANEL) ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 0));
        controlPanel.setOpaque(false);

        JButton minusButton = new JButton("-");
        styleControlButton(minusButton, CashierDashboard.DANGER);
        minusButton.addActionListener(e -> {
            dashboard.changeItemQuantity(cartItem, -1);
            updatePriceLabel(); // Keep this call for instant visual update
        });

        qtyLabel = new JLabel(String.valueOf(item.qty), SwingConstants.CENTER);
        qtyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        qtyLabel.setPreferredSize(new Dimension(30, 25));
        qtyLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, new Color(220, 220, 220)));

        JButton plusButton = new JButton("+");
        styleControlButton(plusButton, CashierDashboard.SUCCESS);
        plusButton.addActionListener(e -> {
            dashboard.changeItemQuantity(cartItem, 1);
            updatePriceLabel(); // Keep this call for instant visual update
        });

        controlPanel.add(minusButton);
        controlPanel.add(qtyLabel);
        controlPanel.add(plusButton);

        gbc.gridx = 1;
        gbc.weightx = 0.0; // Don't grow horizontally
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE; // Don't fill
        gbc.anchor = GridBagConstraints.EAST; // Align to the right
        add(controlPanel, gbc);
        
        // Initial setup of labels
        updatePriceLabel(); 
    }

    private void styleControlButton(JButton btn, Color color) {
        btn.setPreferredSize(new Dimension(30, 25));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
    }

    public void updatePriceLabel() {
        // This method is now responsible for setting both qty and total price text
        qtyLabel.setText(String.valueOf(cartItem.qty));
        priceLabel.setText("₱" + String.format("%.2f", cartItem.price * cartItem.qty) +
                           " (@₱" + String.format("%.2f", cartItem.price) + ")");
    }
}