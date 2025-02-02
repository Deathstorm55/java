import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HomePage extends JFrame {
    private String name;
    private String accountNumber;
    private float accountBalance;
    private String transactionPin; // Login password as transaction PIN

    private final String URL = "jdbc:mysql://localhost:3306/jdbcdemo";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    public HomePage(String name, String accountNumber, String transactionPin) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.transactionPin = transactionPin;
        this.accountBalance = fetchAccountBalance(); // Fetch balance from DB

        setTitle("Home Page");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + name + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        // Account number and balance
        JLabel accountInfoLabel = new JLabel("<html>Account Number: " + accountNumber + "<br>Balance: $" + accountBalance + "</html>");
        accountInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        accountInfoLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(accountInfoLabel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel bottomPanel = new JPanel();
        JButton transferButton = new JButton("Transfer Money");
        transferButton.addActionListener(e -> openTransferDialog(accountInfoLabel));
        
        JButton addMoneyButton = new JButton("Add Money");
        addMoneyButton.addActionListener(e -> openAddMoneyDialog(accountInfoLabel));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginForm();
            dispose();
        });

        bottomPanel.add(transferButton);
        bottomPanel.add(addMoneyButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Fetch user's account balance from DB
    private float fetchAccountBalance() {
        try (Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT account_balance FROM student WHERE account_number = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getFloat("account_balance");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching balance: " + ex.getMessage());
        }
        return 0.0f;
    }

    // Add Money
    private void openAddMoneyDialog(JLabel accountInfoLabel) {
        JTextField amountField = new JTextField();
        JPasswordField pinField = new JPasswordField();

        Object[] message = {
            "Amount to Add:", amountField,
            "Transaction PIN:", pinField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Money", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String amountText = amountField.getText();
            String enteredPin = new String(pinField.getPassword());

            if (amountText.isEmpty() || enteredPin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.");
                return;
            }

            if (!enteredPin.equals(transactionPin)) {
                JOptionPane.showMessageDialog(this, "Incorrect transaction PIN.");
                return;
            }

            try {
                float addAmount = Float.parseFloat(amountText);
                if (addAmount < 1000 || addAmount > 1000000000) {
                    JOptionPane.showMessageDialog(this, "Amount must be between $1,000 and $1,000,000,000.");
                    return;
                }
                updateAccountBalance(addAmount);
                accountBalance += addAmount;
                accountInfoLabel.setText("<html>Account Number: " + accountNumber + "<br>Balance: $" + accountBalance + "</html>");
                JOptionPane.showMessageDialog(this, "Money added successfully.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        }
    }

    // Update account balance in DB
    private void updateAccountBalance(float amount) {
        try (Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "UPDATE student SET account_balance = account_balance + ? WHERE account_number = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setFloat(1, amount);
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating balance: " + ex.getMessage());
        }
    }

    // Transfer Money
    private void openTransferDialog(JLabel accountInfoLabel) {
        JTextField recipientField = new JTextField();
        JTextField amountField = new JTextField();
        JPasswordField pinField = new JPasswordField();

        Object[] message = {
            "Recipient Account Number:", recipientField,
            "Amount to Transfer:", amountField,
            "Transaction PIN:", pinField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String recipient = recipientField.getText();
            String amountText = amountField.getText();
            String enteredPin = new String(pinField.getPassword());

            if (recipient.isEmpty() || amountText.isEmpty() || enteredPin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled.");
                return;
            }

            if (!enteredPin.equals(transactionPin)) {
                JOptionPane.showMessageDialog(this, "Incorrect transaction PIN.");
                return;
            }

            try {
                float transferAmount = Float.parseFloat(amountText);
                if (transferAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "Transfer amount must be greater than zero.");
                    return;
                }
                if (transferAmount > accountBalance) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds.");
                    return;
                }
                
                if (performTransfer(recipient, transferAmount)) {
                    accountBalance -= transferAmount;
                    accountInfoLabel.setText("<html>Account Number: " + accountNumber + "<br>Balance: $" + accountBalance + "</html>");
                    JOptionPane.showMessageDialog(this, "Transfer successful.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        }
    }

    private boolean performTransfer(String recipientAccount, float amount) {
        try (Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            con.setAutoCommit(false);
            try {
                // Deduct from sender
                String deductQuery = "UPDATE student SET account_balance = account_balance - ? WHERE account_number = ?";
                PreparedStatement deductPs = con.prepareStatement(deductQuery);
                deductPs.setFloat(1, amount);
                deductPs.setString(2, accountNumber);
                deductPs.executeUpdate();

                // Add to recipient
                String addQuery = "UPDATE student SET account_balance = account_balance + ? WHERE account_number = ?";
                PreparedStatement addPs = con.prepareStatement(addQuery);
                addPs.setFloat(1, amount);
                addPs.setString(2, recipientAccount);
                int rowsAffected = addPs.executeUpdate();

                if (rowsAffected == 0) {
                    con.rollback();
                    JOptionPane.showMessageDialog(this, "Recipient account not found.");
                    return false;
                }

                con.commit();
                return true;
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error performing transfer: " + ex.getMessage());
            return false;
        }
    }
}
