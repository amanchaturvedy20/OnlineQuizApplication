import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame implements ActionListener {
    JLabel titleLabel, userLabel, passLabel;
    JTextField userField;
    JPasswordField passField;
    JButton loginButton;

    public static String currentUser;

    public Login() {
        setTitle("Login Page");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        titleLabel = new JLabel("Welcome to Quiz App");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBounds(70, 30, 300, 30);
        add(titleLabel);

        userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setBounds(50, 90, 100, 25);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(160, 90, 180, 25);
        add(userField);

        passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passLabel.setBounds(50, 130, 100, 25);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(160, 130, 180, 25);
        add(passField);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBounds(140, 190, 120, 35);
        loginButton.addActionListener(this);
        add(loginButton);

        setLocationRelativeTo(null); // center window
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String username = userField.getText();
        String password = String.valueOf(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Username and Password", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            if (authenticate(username, password)) {
                currentUser = username;
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose(); // close login window
                new Quiz(); // ✅ Launch the quiz
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    boolean authenticate(String username, String password) {
        // Simple hardcoded login for testing
        return username.equals("admin") && password.equals("12345");
    }

    public static void main(String[] args) {
        new Login();
    }
}
