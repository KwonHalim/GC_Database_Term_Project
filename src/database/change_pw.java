package database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class change_pw extends JFrame {

    private JTextField usernameField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JButton changePasswordButton;

    public change_pw(String nickname) {
        super("Change Password");

        usernameField = new JTextField(nickname);
        currentPasswordField = new JPasswordField(20);
        newPasswordField = new JPasswordField(20);
        changePasswordButton = new JButton("Change Password");

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentPassword = new String(currentPasswordField.getPassword());
                System.out.println("current:" + currentPassword);
                String newPassword = new String(newPasswordField.getPassword());
                System.out.println("new" + newPassword);
                if (changePassword(usernameField.getText(), currentPassword, newPassword)) {
                    JOptionPane.showMessageDialog(change_pw.this, "Password changed successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(change_pw.this, "Failed to change password. Please check your current password.");
                }
            }
        });

        JPanel phrasePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel phraseLabel = new JLabel("Change Password");
        Font font = new Font("Serif", Font.ITALIC, 30);
        phraseLabel.setForeground(Color.ORANGE);
        phraseLabel.setFont(font);
        phrasePanel.add(phraseLabel);

        JPanel changePasswordPanel = new JPanel(new GridLayout(4, 2));
        changePasswordPanel.add(new JLabel("Username:"));
        changePasswordPanel.add(usernameField);
        changePasswordPanel.add(new JLabel("Current Password:"));
        changePasswordPanel.add(currentPasswordField);
        changePasswordPanel.add(new JLabel("New Password:"));
        changePasswordPanel.add(newPasswordField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
        buttonPanel.add(changePasswordButton);

        setLayout(new BorderLayout());
        add(phrasePanel, BorderLayout.NORTH);
        add(changePasswordPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private boolean changePassword(String nickname, String currentPassword, String newPassword) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root", "12345")) {
            String sql = "UPDATE userInfo SET PW=? WHERE Nickname=? AND PW=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, nickname);
                preparedStatement.setString(3, currentPassword);
                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
