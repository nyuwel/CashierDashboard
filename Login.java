import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Login extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, exitButton;
    private JLabel forgotPasswordLabel;

    public Login() {
        setTitle("Dagamis Sash & Woodwork - Login");
        setSize(400, 500); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setLayout(new GridBagLayout());

        // ======================= CARD PANEL =======================
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(350, 450));
        card.setBackground(Color.WHITE);
        card.setLayout(null);
        card.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        add(card);

        // Title and Welcome Labels
        JLabel title = new JLabel("Dagamis Sash & Woodwork", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        title.setForeground(Color.GRAY);
        title.setBounds(0, 80, 350, 25);
        card.add(title);

        JLabel welcome = new JLabel("Welcome", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setBounds(0, 110, 350, 30);
        card.add(welcome);

        // ======================= USERNAME =======================
        usernameField = new JTextField("Username");
        usernameField.setBounds(40, 170, 270, 40);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setForeground(Color.GRAY);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(66, 133, 244), 2, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        card.add(usernameField);
        
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Username")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Username");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });

        // ======================= PASSWORD FIELD + TOGGLE =======================
        JPanel passwordInputPanel = new JPanel(null);
        passwordInputPanel.setBounds(40, 230, 270, 40);
        passwordInputPanel.setOpaque(false);
        card.add(passwordInputPanel);

        passwordField = new JPasswordField("Password");
        passwordField.setBounds(0, 0, 230, 40);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(Color.GRAY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(66, 133, 244), 2, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        passwordField.setEchoChar((char) 0);
        passwordInputPanel.add(passwordField);

        JButton toggleButton = new JButton("👁️");
        toggleButton.setBounds(230, 0, 40, 40);
        toggleButton.setFocusPainted(false);
        toggleButton.setMargin(new Insets(0, 0, 0, 0));
        toggleButton.setBackground(Color.WHITE);
        toggleButton.setForeground(new Color(66, 133, 244));
        toggleButton.setBorder(new LineBorder(new Color(66, 133, 244), 2, true));
        passwordInputPanel.add(toggleButton);

        toggleButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == 0) {
                passwordField.setEchoChar('•');
                toggleButton.setText("👁️");
                toggleButton.setToolTipText("Show Password");
            } else {
                passwordField.setEchoChar((char) 0);
                toggleButton.setText("🔒");
                toggleButton.setToolTipText("Hide Password");
            }
        });
        
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.equals("Password")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•'); // Start hiding when typing begins
                    passwordField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.isEmpty()) {
                    passwordField.setText("Password");
                    passwordField.setEchoChar((char) 0); // Show placeholder text
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });

        // ======================= FORGOT PASSWORD LINK =======================
        forgotPasswordLabel = new JLabel("Forgot Password?", SwingConstants.RIGHT);
        forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(new Color(66, 133, 244));
        forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.setBounds(180, 275, 130, 20);
        card.add(forgotPasswordLabel);

        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                forgotPassword();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                 forgotPasswordLabel.setForeground(new Color(40, 90, 180));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                 forgotPasswordLabel.setForeground(new Color(66, 133, 244));
            }
        });

        // ======================= BUTTONS =======================
        loginButton = new JButton("Log In");
        loginButton.setBounds(40, 300, 270, 45);
        loginButton.setBackground(new Color(66, 133, 244));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        card.add(loginButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(40, 360, 270, 35);
        exitButton.setBackground(Color.WHITE);
        exitButton.setForeground(Color.GRAY);
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder());
        card.add(exitButton);

        // ======================= ACTIONS =======================
        loginButton.addActionListener(e -> login());
        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void login() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || user.equals("Username") ||
            pass.isEmpty() || pass.equals("Password")) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Login Error", JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Use the DataStorage method to validate credentials
        User validatedUser = DataStorage.validateUser(user, pass);

        if (validatedUser != null) {
            // Login Successful
            if (validatedUser.getRole().equals("Admin")) {
                // Admin login (requires AdminDashboard.java)
                new AdminDashboard(validatedUser.getUsername(), validatedUser.getRole()).setVisible(true);
            } else if (validatedUser.getRole().equals("Cashier")) {
                // Cashier login (Implemented below)
                new CashierDashboard(validatedUser.getUsername(), validatedUser.getRole()).setVisible(true);
            }
            
            dispose();
        } else {
            // Login Failed
            JOptionPane.showMessageDialog(this,
                "Invalid username or password.",
                "Login Failed", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void forgotPassword() {
        String user = usernameField.getText().trim();
        
        // 1. Get Username
        if (user.isEmpty() || user.equals("Username")) {
             user = JOptionPane.showInputDialog(this,
                "Please enter your username to reset your password:",
                "Forgot Password", JOptionPane.QUESTION_MESSAGE
            );
            if (user == null || user.trim().isEmpty()) return;
            user = user.trim();
        }

        // 2. Security Question Check
        String securityAnswer = JOptionPane.showInputDialog(this,
            "To reset the password for user '" + user + "', please answer:\n" +
            "What is your mother's maiden name?",
            "Security Check", JOptionPane.QUESTION_MESSAGE
        );

        if (securityAnswer == null || securityAnswer.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password reset cancelled.", "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        User userToReset = DataStorage.checkSecurityAnswer(user, securityAnswer);

        if (userToReset != null) {
            // 3. Prompt for New Password
            JPasswordField newPasswordField = new JPasswordField();
            JPasswordField confirmPasswordField = new JPasswordField();

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Enter New Password:"));
            panel.add(newPasswordField);
            panel.add(new JLabel("Confirm New Password:"));
            panel.add(confirmPasswordField);

            int result = JOptionPane.showConfirmDialog(this, panel,
                "Enter New Password for: " + userToReset.getUsername(), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newPass = new String(newPasswordField.getPassword());
                String confirmPass = new String(confirmPasswordField.getPassword());

                if (newPass.isEmpty() || !newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match or are empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // 4. Update Password using DataStorage
                if (DataStorage.updateUserPassword(userToReset, newPass)) {
                     JOptionPane.showMessageDialog(this,
                        "Password successfully changed for " + userToReset.getUsername() + ".",
                        "Password Reset Complete", JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                     JOptionPane.showMessageDialog(this, "An error occurred during password update.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
             JOptionPane.showMessageDialog(this,
                "Username or security answer is incorrect.",
                "Security Check Failed", JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login();
        });
    }
}