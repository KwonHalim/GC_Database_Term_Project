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

	public class find_account extends Frame {
	    private Label emailLabel;
	    private TextField emailField;
	    private Button confirmButton;
	    private Button cancelButton;

	    public find_account() {
	        setTitle("Finding your ID AND PASSWORD");
	        setSize(400, 300);

	        JPanel phrasePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        JLabel loginPhrase = new JLabel("CREATE ACCOUNT");
	        Font font = new Font("Serif", Font.ITALIC, 40);
	        loginPhrase.setFont(font);
	        loginPhrase.setForeground(Color.orange);
	        phrasePanel.add(loginPhrase);
	        phrasePanel.setBackground(Color.white);

	        emailLabel = new Label("Enter your email");
	        emailField = new TextField(20);
	        confirmButton = new Button("Confirm");
	        cancelButton = new Button("Cancel");
	        JPanel input_box = new JPanel(new GridLayout(4, 2));
	        input_box.add(emailLabel);
	        input_box.add(emailField);
	        input_box.add(confirmButton);
	        input_box.add(cancelButton);

	        setLayout(new BorderLayout());
	        add(input_box, BorderLayout.SOUTH);
	        add(phrasePanel, BorderLayout.NORTH);

	        confirmButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                findData();
	            }
	        });

	        cancelButton.addActionListener(new ActionListener() {
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

	private void findData() {
		String name = null;
		String password = null;
		String phone;
		String email = emailField.getText();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root",
					"12345");

			String sql = "SELECT * FROM userInfo WHERE email = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, email);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						name = resultSet.getString("Nickname");
						password = resultSet.getString("PW");
						phone = resultSet.getString("phone_number");
					}
				}
			}

			connection.close();
			if (name == null || password == null) {
				JOptionPane.showMessageDialog(null, "Your account does not exist");
			} else {
				JOptionPane.showMessageDialog(null, "Nickname is:" + name + "\n" + "Password is " + password);
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "회원 정보가 존재하지 않습니다.");
		}
	}

	public static void main(String[] args) {
		find_account findAccountFrame = new find_account();
	}
}
