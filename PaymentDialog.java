import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.DecimalFormat;

public class PaymentDialog extends JDialog {
    
    private double total;
    private JRadioButton cashRadio, onlineRadio;
    private JTextField cashTenderedField, refCodeField;
    private JLabel changeLabel, totalDisplay;
    private JButton confirmButton;
    private boolean paymentConfirmed = false;
    private String paymentMethod = "";

    private final DecimalFormat currencyFormat = new DecimalFormat("₱#,##0.00");

    private final Color PRIMARY_ACCENT = new Color(0, 123, 255); 
    private final Color SUCCESS_GREEN = new Color(40, 167, 69); 
    private final Color DANGER_RED = new Color(220, 53, 69);
    private final Color BG_LIGHT = new Color(248, 249, 250); 
    private final Color CARD_BG = Color.WHITE;
    private final Color TEXT_DARK = new Color(52, 58, 64);

    public PaymentDialog(JFrame parent, double totalAmount) {
        super(parent, "Process Payment", true);
        this.total = totalAmount;

        setSize(700, 500); 
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // --- Main Content Panel ---
        JPanel mainContentPanel = new JPanel(new GridLayout(1, 2));
        mainContentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // --- Left Panel ---
        JPanel controlsPanel = createControlsPanel();
        mainContentPanel.add(controlsPanel);

        // --- Right Panel ---
        JPanel summaryPanel = createSummaryPanel();
        mainContentPanel.add(summaryPanel);

        add(mainContentPanel, BorderLayout.CENTER);
        
        // --- Listeners ---
        setupListeners();

        cashTenderedField.setEditable(true);
        refCodeField.setEditable(false);
        updateChange();
    }
 
    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        gbc.gridy = 0;
        panel.add(createLabel("Select Payment Method:"), gbc);

        cashRadio = createRadioButton("Cash Payment");
        onlineRadio = createRadioButton("Online Payment (Gcash/Bank Transfer)");
        
        ButtonGroup group = new ButtonGroup();
        group.add(cashRadio);
        group.add(onlineRadio);
        cashRadio.setSelected(true);
        
        JPanel radioPanel = new JPanel(new GridLayout(2, 1));
        radioPanel.setOpaque(false);
        radioPanel.add(cashRadio);
        radioPanel.add(onlineRadio);
        radioPanel.setBorder(new LineBorder(BG_LIGHT.darker(), 1, true)); 

        gbc.gridy = 1;
        panel.add(radioPanel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 5, 0); 
        panel.add(createLabel("Cash Received:"), gbc);
        
        gbc.gridy = 3;
        cashTenderedField = createStyledTextField("0.00");
        cashTenderedField.setFont(new Font("Segoe UI", Font.BOLD, 26)); 
        cashTenderedField.setForeground(Color.BLACK); 
        panel.add(cashTenderedField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 5, 0);
        panel.add(createLabel("Reference Code:"), gbc);
        
        gbc.gridy = 5;
        refCodeField = createStyledTextField("");
        refCodeField.setEditable(false);
        panel.add(refCodeField, gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalStrut(20), gbc);
        
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_ACCENT);
        headerPanel.setBorder(new EmptyBorder(30, 20, 30, 20));
        headerPanel.putClientProperty("JComponent.roundRect", Boolean.TRUE); 
        
        JLabel totalTitle = new JLabel("TOTAL DUE", SwingConstants.CENTER);
        totalTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        totalTitle.setForeground(Color.WHITE);
        headerPanel.add(totalTitle, BorderLayout.NORTH);
        
        totalDisplay = new JLabel(currencyFormat.format(total), SwingConstants.CENTER);
        totalDisplay.setFont(new Font("Segoe UI", Font.BOLD, 48));
        totalDisplay.setForeground(Color.WHITE);
        headerPanel.add(totalDisplay, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        changeLabel = new JLabel("Remaining: " + currencyFormat.format(total), SwingConstants.CENTER);
        changeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        changeLabel.setForeground(DANGER_RED);
        changeLabel.setBorder(new EmptyBorder(30, 0, 30, 0));
        
        gbc.gridy = 0;
        centerPanel.add(changeLabel, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        confirmButton = createActionButton("CONFIRM PAYMENT", SUCCESS_GREEN);
        
        panel.add(confirmButton, BorderLayout.SOUTH);
        
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_DARK);
        return label;
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton btn = new JRadioButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor
        btn.setOpaque(false);
        btn.setForeground(TEXT_DARK);
        return btn;
    }
    
    private JTextField createStyledTextField(String defaultText) {
        JTextField field = new JTextField(defaultText);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        Border focusBorder = BorderFactory.createLineBorder(PRIMARY_ACCENT, 2);
        Border unfocusBorder = BorderFactory.createLineBorder(new Color(200, 200, 200), 1);
        
        field.setBorder(BorderFactory.createCompoundBorder(
            unfocusBorder,
            new EmptyBorder(10, 10, 10, 10)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.isEditable()) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        focusBorder,
                        new EmptyBorder(10, 10, 10, 10)
                    ));
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    unfocusBorder,
                    new EmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        
        // Hand cursor listener for fields
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (field.isEditable()) {
                    field.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                } else {
                    field.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                field.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return field;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 60));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private void setupListeners() {
        ActionListener radioListener = e -> {
            boolean isCash = cashRadio.isSelected();
            cashTenderedField.setEditable(isCash);
            refCodeField.setEditable(!isCash);

            cashTenderedField.setCursor(isCash ? Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR) : Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            refCodeField.setCursor(!isCash ? Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR) : Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            if (isCash) {
                refCodeField.setText("");
                updateChange();
            } else {
                cashTenderedField.setText("0.00");
                changeLabel.setText("Payment Required");
                changeLabel.setForeground(TEXT_DARK);
                confirmButton.setEnabled(true); 
            }
        };
        cashRadio.addActionListener(radioListener);
        onlineRadio.addActionListener(radioListener);

        cashTenderedField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateChange();
            }
        });

        confirmButton.addActionListener(e -> {
            if (cashRadio.isSelected()) {
                handleCashPayment();
            } else {
                handleOnlinePayment();
            }
        });

        cashRadio.doClick(); 
    }

    private void updateChange() {
        if (!cashRadio.isSelected()) return;
        
        try {

            String text = cashTenderedField.getText().trim().replace("₱", "").replace(",", "");
            double tendered = Double.parseDouble(text);
            double change = tendered - total;
            
            if (change < 0) {
                changeLabel.setText("Remaining: " + currencyFormat.format(Math.abs(change)));
                changeLabel.setForeground(DANGER_RED); 
                confirmButton.setEnabled(false);
            } else {
                changeLabel.setText("Change: " + currencyFormat.format(change));
                changeLabel.setForeground(SUCCESS_GREEN);
                confirmButton.setEnabled(true);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Invalid Amount");
            changeLabel.setForeground(DANGER_RED);
            confirmButton.setEnabled(false);
        }
    }

    private void handleCashPayment() {
        try {
            String text = cashTenderedField.getText().trim().replace("₱", "").replace(",", "");
            double tendered = Double.parseDouble(text);
            if (tendered < total) {
                JOptionPane.showMessageDialog(this, "Tendered amount is less than the total.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            paymentMethod = "Cash";
            paymentConfirmed = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid cash amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleOnlinePayment() {
        String refCode = refCodeField.getText().trim();
        if (refCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid reference code.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        paymentMethod = "Online (Ref: " + refCode + ")";
        paymentConfirmed = true;
        dispose();
    }

    public boolean isPaymentConfirmed() {
        return paymentConfirmed;
    }
    
    public double getChange() {
        try {
            String text = cashTenderedField.getText().trim().replace("₱", "").replace(",", "");
            double tendered = Double.parseDouble(text);
            return Math.max(0.0, tendered - total);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}