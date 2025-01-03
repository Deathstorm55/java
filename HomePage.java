import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {
    public HomePage(String name, String accountNumber) {
        setTitle("Home Page");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Welcome message with user's name
        JLabel welcomeLabel = new JLabel("Welcome, " + name + "!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        // Display account number
        JLabel accountNumberLabel = new JLabel("Account Number: " + accountNumber);
        accountNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        accountNumberLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(accountNumberLabel, BorderLayout.CENTER);

        // Additional space for customization or buttons
        JPanel bottomPanel = new JPanel();
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginForm(); // Redirect to login form on logout
            dispose();       // Close the current home page
        });
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
