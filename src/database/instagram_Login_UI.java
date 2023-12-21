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
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class instagram_Login_UI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;
    private JButton find_ID_PW;
    private JButton Change_PW;
    String main_nickname;
    int userId;
    public instagram_Login_UI() {
        super("instagram Demo");

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        createAccountButton = new JButton("Create Account");
        loginButton = new JButton("Login");
        find_ID_PW = new JButton("FIND ID/PW");
        Change_PW = new JButton("Change PW");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = usernameField.getText();
                main_nickname = nickname;
                
                String password = new String(passwordField.getPassword());
                if (validateLogin(password, nickname)) {
                    JOptionPane.showMessageDialog(instagram_Login_UI.this, "Login successful!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(instagram_Login_UI.this, "Invalid username or password.");
                }
            }
        });
        
        Change_PW.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String nickname = usernameField.getText();
        		change_pw change = new change_pw(nickname);
        	}
        });

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                create_account userCreateAccount = new create_account();
            }
        });
        
        find_ID_PW.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		find_account find_ID_PW = new find_account();
        	}
        });
        
        JPanel Phrase = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel login_Phrase = new JLabel("LOGIN");
        Font  font  = new Font("Serif", Font.ITALIC, 40);
        login_Phrase.setForeground(Color.orange);
        login_Phrase.setFont(font);
        Phrase.add(login_Phrase, BorderLayout.CENTER);
        Phrase.setBackground(Color.white);
        // Create panels for the layout
        JPanel loginPanel = new JPanel(new GridLayout(10, 3));
        loginPanel.add(new JLabel());
        loginPanel.add(new JLabel());
        loginPanel.add(new JLabel());
        loginPanel.add(new JLabel());
        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.setBackground(Color.white);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.add(createAccountButton);
        buttonPanel.add(find_ID_PW);
        buttonPanel.add(loginButton);
        buttonPanel.add(Change_PW);

        setLayout(new BorderLayout());
        add(Phrase, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void onLoginCompleted() {
        synchronized (this) {
            this.notify();  // 대기 중인 스레드 깨우기
        }
    }
    
    private boolean validateLogin(String password, String nickname) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root", "12345")) {
            String sql = "SELECT * FROM userInfo WHERE PW=? AND Nickname=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, password);
                preparedStatement.setString(2, nickname);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        userId = resultSet.getInt("user_id");
                        System.out.println(userId);
                        onLoginCompleted();  // 로그인 완료 시 메소드 호출
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
}