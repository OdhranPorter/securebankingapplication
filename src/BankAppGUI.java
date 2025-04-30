import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class BankAppGUI {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private BankService bankService = new BankService(new User());
    private int currentUserId = -1;

    // Authentication components
    private JTextField userField = new JTextField(20);
    private JPasswordField passField = new JPasswordField(20);
    private JLabel authErrorLabel = new JLabel();
    
    // Account components
    private JLabel balanceValueLabel = new JLabel("€0.00");
    private JTextField amountField = new JTextField(10);
    private JLabel accountErrorLabel = new JLabel();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankAppGUI().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("SecureBank with MFA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(createAuthPanel(), "auth");
        mainPanel.add(createAccountPanel(), "account");

        frame.add(mainPanel);
        showAuthScreen();
        frame.setVisible(true);
    }

    private JPanel createAuthPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Secure Banking");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(createLabel("Username:"), gbc);
        gbc.gridy++;
        panel.add(createLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(userField, gbc);
        gbc.gridy++;
        panel.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(createButton("Login", e -> performLogin()));
        buttonPanel.add(createButton("Register", e -> performRegistration()));
        panel.add(buttonPanel, gbc);

        authErrorLabel.setForeground(Color.RED);
        authErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        panel.add(authErrorLabel, gbc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(createButton("Logout", e -> logout()));
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        
        JLabel balanceLabel = createLabel("Account Balance:");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        balanceValueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        balanceValueLabel.setForeground(new Color(0, 100, 0));
        
        gbc.gridy = 0;
        centerPanel.add(balanceLabel, gbc);
        gbc.gridy++;
        centerPanel.add(balanceValueLabel, gbc);
        gbc.gridy++;
        
        JPanel transactionPanel = new JPanel();
        transactionPanel.add(new JLabel("Amount:"));
        transactionPanel.add(amountField);
        transactionPanel.add(createButton("Deposit", e -> performTransaction(true)));
        transactionPanel.add(createButton("Withdraw", e -> performTransaction(false)));
        
        centerPanel.add(transactionPanel, gbc);
        gbc.gridy++;
        
        accountErrorLabel.setForeground(Color.RED);
        accountErrorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(accountErrorLabel, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private void performLogin() {
        try {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            
            int userId = bankService.login(username, password);
            if(userId > 0) {
                String otp = JOptionPane.showInputDialog(
                    frame,
                    "Enter 6-digit OTP sent to your device:",
                    "MFA Verification",
                    JOptionPane.PLAIN_MESSAGE
                );

                if(otp != null && validateOTP(otp.trim())) {
                    currentUserId = userId;
                    showAccountScreen();
                    clearAuthFields();
                } else {
                    authErrorLabel.setForeground(Color.RED);
                    authErrorLabel.setText("Invalid OTP code");
                }
            } else {
                authErrorLabel.setText("Invalid username or password");
            }
        } catch (ValidationException | SQLException ex) {
            authErrorLabel.setText(ex.getMessage());
        }
    }

    private boolean validateOTP(String userOTP) {
        return "123456".equals(userOTP);
    }

    private void performRegistration() {
        try {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            
            if(bankService.createAccount(username, password)) {
                authErrorLabel.setForeground(new Color(0, 150, 0));
                authErrorLabel.setText("Account created successfully!");
                clearAuthFields();
            }
        } catch (ValidationException | SQLException ex) {
            authErrorLabel.setForeground(Color.RED);
            authErrorLabel.setText(ex.getMessage());
        }
    }

    private void performTransaction(boolean isDeposit) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            double newBalance = isDeposit ? 
                bankService.deposit(currentUserId, amount) :
                bankService.withdraw(currentUserId, amount);
            
            updateBalanceDisplay(newBalance);
            accountErrorLabel.setForeground(new Color(0, 150, 0));
            accountErrorLabel.setText("Transaction successful!");
            amountField.setText("");
        } catch (NumberFormatException ex) {
            accountErrorLabel.setForeground(Color.RED);
            accountErrorLabel.setText("Invalid amount format");
        } catch (ValidationException | SQLException ex) {
            accountErrorLabel.setForeground(Color.RED);
            accountErrorLabel.setText(ex.getMessage());
        }
    }

    private void updateBalanceDisplay(double balance) {
        balanceValueLabel.setText(String.format("€%.2f", balance));
    }

    private void logout() {
        currentUserId = -1;
        showAuthScreen();
        accountErrorLabel.setText("");
    }

    private void showAuthScreen() {
        cardLayout.show(mainPanel, "auth");
        authErrorLabel.setText("");
        clearAuthFields();
    }

    private void showAccountScreen() {
        try {
            double balance = bankService.getBalance(currentUserId);
            updateBalanceDisplay(balance);
            cardLayout.show(mainPanel, "account");
            accountErrorLabel.setText("");
        } catch (SQLException ex) {
            accountErrorLabel.setText("Error loading account information");
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void clearAuthFields() {
        userField.setText("");
        passField.setText("");
        authErrorLabel.setText("");
    }
}