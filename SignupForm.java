import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Random;
import java.time.LocalDateTime;

public class SignupForm extends JFrame implements ActionListener {
    private JTextField nameField, ageField, emailField;
    private JPasswordField passwordField;
    private JButton signupButton, resetButton;

    private final String URL = "jdbc:mysql://localhost:3306/jdbcdemo";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    public SignupForm() {
        setTitle("Signup Form");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Age:"));
        ageField = new JTextField();
        add(ageField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        signupButton = new JButton("Sign Up");
        signupButton.addActionListener(this);
        add(signupButton);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        add(resetButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signupButton) {
            String name = nameField.getText();
            String ageText = ageField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || ageText.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            try {
                int age = Integer.parseInt(ageText);

                // Generate Account Number
                String accountNumber = generateAccountNumber();

                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                String query = "INSERT INTO student (name, age, email, password, account_number) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, name);
                ps.setInt(2, age);
                ps.setString(3, email);
                ps.setString(4, password);
                ps.setString(5, accountNumber);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Signup successful! Redirecting to login page.");
                    new LoginForm(); // Redirect to Login Form
                    dispose();       // Close Signup Form
                } else {
                    JOptionPane.showMessageDialog(this, "Signup failed.");
                }

                con.close();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } else if (e.getSource() == resetButton) {
            nameField.setText("");
            ageField.setText("");
            emailField.setText("");
            passwordField.setText("");
        }
    }

    private String generateAccountNumber() {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String time = String.valueOf(System.currentTimeMillis()).substring(8);
        int random = new Random().nextInt(1000); // Random number (0-999)

        return year + time + random;
    }

    public static void main(String[] args) {
        new SignupForm();
    }
}
