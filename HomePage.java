import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

public class HomePage extends JFrame {
    public HomePage(String accountNumber) {
        setTitle("Home Page");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to your account!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.NORTH);

        JLabel accountNumberLabel = new JLabel("Account Number: " + accountNumber);
        accountNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(accountNumberLabel, BorderLayout.CENTER);

        setVisible(true);
    }
}
