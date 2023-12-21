package database;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Search extends JFrame {
    private JTextField searchField;
    private JList<String> searchResultList;

    public Search() {
        setTitle("Search");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = searchField.getText();
                if (!nickname.isEmpty()) {
                    displaySearchResults(nickname);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a nickname.");
                }
            }
        });
        searchPanel.add(new JLabel("Nickname: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        DefaultListModel<String> searchResultListModel = new DefaultListModel<>();
        searchResultList = new JList<>(searchResultListModel);
        JScrollPane scrollPane = new JScrollPane(searchResultList);

        JPanel searchResultPanel = new JPanel(new BorderLayout());
        searchResultPanel.add(new JLabel("Search Results:"), BorderLayout.NORTH);
        searchResultPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(searchResultPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void displaySearchResults(String nickname) {
        String url = "jdbc:mysql://localhost/instagram";
        String user = "root";
        String password = "12345";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String searchQuery = "SELECT nickname FROM userinfo WHERE nickname LIKE ?";
            try (PreparedStatement searchStatement = connection.prepareStatement(searchQuery)) {
                searchStatement.setString(1, "%" + nickname + "%");
                ResultSet searchResult = searchStatement.executeQuery();
                DefaultListModel<String> searchResultListModel = new DefaultListModel<>();
                while (searchResult.next()) {
                    searchResultListModel.addElement(searchResult.getString("nickname"));
                }
                searchResultList.setModel(searchResultListModel);

                // Search result item click handling code
                searchResultList.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        JList<String> list = (JList<String>) evt.getSource();
                        if (evt.getClickCount() == 1) {
                            int index = list.locationToIndex(evt.getPoint());
                            String selectedNickname = list.getModel().getElementAt(index);
                            openUserProfile(selectedNickname);
                        }
                    }
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching data from the database.");
        }
    }

    private void openUserProfile(String selectedNickname) {
        int selectedUserId = getUserIdByNickname(selectedNickname);
        User_Profile_Window UPW = new User_Profile_Window(selectedNickname, selectedUserId);
    }

    public static int getUserIdByNickname(String nickname) {
        int userId = 0;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root", "12345")) {
            String query = "SELECT user_id FROM userinfo WHERE nickname = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nickname);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        userId = resultSet.getInt("user_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }

    public static void main(String[] args) {
        Search search = new Search();
    }
}
