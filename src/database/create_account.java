package database;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class create_account extends Frame {
    Label nicknameLabel, passwordLabel, phoneLabel, emailLabel;
    TextField nicknameField, phoneField, emailField;
    JPasswordField passwordField;
    Button confirm, cancel;

    public create_account() {
        JPanel phrasePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel loginPhrase = new JLabel("CREATE ACCOUNT");
        Font font = new Font("Serif", Font.ITALIC, 40);
        loginPhrase.setFont(font);
        loginPhrase.setForeground(Color.orange);
        phrasePanel.add(loginPhrase);
        phrasePanel.setBackground(Color.white);

        setTitle("Create Account");
        setSize(600, 400);
        setLayout(new GridLayout(7, 2));

        nicknameLabel = new Label("Nickname");
        nicknameField = new TextField();
        passwordLabel = new Label("Password");
        passwordField = new JPasswordField();
        passwordField.setEchoChar('*');
        phoneLabel = new Label("Phone_number");
        phoneField = new TextField();
        emailLabel = new Label("Email");
        emailField = new TextField();

        confirm = new Button("Create account");
        cancel = new Button("Cancel");
        JPanel inputBox = new JPanel(new GridLayout(11, 2));
        inputBox.setBackground(Color.white);
        inputBox.add(nicknameLabel);
        inputBox.add(nicknameField);
        inputBox.add(passwordLabel);
        inputBox.add(passwordField);
        inputBox.add(phoneLabel);
        inputBox.add(phoneField);
        inputBox.add(emailLabel);
        inputBox.add(emailField);
        inputBox.add(confirm);
        inputBox.add(cancel);
        inputBox.add(new JLabel());
        setLayout(new BorderLayout());
        add(phrasePanel, BorderLayout.NORTH);
        add(inputBox, BorderLayout.SOUTH);
        
        confirm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void saveData() {
        String name = nicknameField.getText();
        String password = new String(passwordField.getPassword());
        String phone = phoneField.getText();
        String email = emailField.getText();

        if (isDuplicate(name, email, phone)) {
            JOptionPane.showMessageDialog(null, "Duplicate entry. Please re-enter.");
        } else {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root", "12345");
                String sql = "INSERT INTO userInfo (pW, nickname, email, phone_number) VALUES (?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, password);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, phone);
                    preparedStatement.executeUpdate();
                }

                connection.close();
                JOptionPane.showMessageDialog(null, "Registration complete");

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Registration failed");
            }
        }
    }

    private boolean isDuplicate(String name, String email, String phone) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root", "12345");
            String sql = "SELECT COUNT(*) FROM userInfo WHERE nickname = ? OR email = ? OR phone_number = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, phone);
                
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        create_account newaccount = new create_account();
    }
}
