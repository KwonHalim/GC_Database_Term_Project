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

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class DBFollow extends JFrame {
    private JTextField searchField;
    private JList<String> followerList;
    private JList<String> followeeList;

    public DBFollow() {
        setTitle("DBFollow");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = searchField.getText();
                if (!nickname.isEmpty()) {
                    displayFollowersAndFollowees(nickname);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a nickname.");
                }
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Nickname: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        DefaultListModel<String> followerListModel = new DefaultListModel<>();
        followerList = new JList<>(followerListModel);

        DefaultListModel<String> followeeListModel = new DefaultListModel<>();
        followeeList = new JList<>(followeeListModel);

        mainPanel.add(searchPanel);

        // Follower list
        JPanel followerPanel = new JPanel(new BorderLayout());
        followerPanel.add(new JLabel("Follower:"), BorderLayout.NORTH);
        followerPanel.add(new JScrollPane(followerList), BorderLayout.CENTER);
        mainPanel.add(followerPanel);

        // Followee list
        JPanel followeePanel = new JPanel(new BorderLayout());
        followeePanel.add(new JLabel("Followee:"), BorderLayout.NORTH);
        followeePanel.add(new JScrollPane(followeeList), BorderLayout.CENTER);
        mainPanel.add(followeePanel);

        add(mainPanel);
        setVisible(true);
    }

    private void displayFollowersAndFollowees(String nickname) {
        String url = "jdbc:mysql://localhost/instagram";
        String user = "root";
        String password = "12345";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Query to get followers
            String followerQuery = "SELECT u.nickname FROM userinfo u JOIN follow f ON u.user_id = f.follower_id " +
                    "WHERE f.followee_id = (SELECT user_id FROM userinfo WHERE nickname = ?)";
            try (PreparedStatement followerStatement = connection.prepareStatement(followerQuery)) {
                followerStatement.setString(1, nickname);
                ResultSet followerResult = followerStatement.executeQuery();
                DefaultListModel<String> followerListModel = new DefaultListModel<>();
                while (followerResult.next()) {
                    followerListModel.addElement(followerResult.getString("nickname"));
                }
                followerList.setModel(followerListModel);

                // Code to handle follower list item click
                followerList.addMouseListener(new java.awt.event.MouseAdapter() {
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

            // Query to get followees
            String followeeQuery = "SELECT u.nickname FROM userinfo u JOIN follow f ON u.user_id = f.followee_id " +
                    "WHERE f.follower_id = (SELECT user_id FROM userinfo WHERE nickname = ?)";
            try (PreparedStatement followeeStatement = connection.prepareStatement(followeeQuery)) {
                followeeStatement.setString(1, nickname);
                ResultSet followeeResult = followeeStatement.executeQuery();
                DefaultListModel<String> followeeListModel = new DefaultListModel<>();
                while (followeeResult.next()) {
                    followeeListModel.addElement(followeeResult.getString("nickname"));
                }
                followeeList.setModel(followeeListModel);

                // Code to handle followee list item click
                followeeList.addMouseListener(new java.awt.event.MouseAdapter() {
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
        // Fetch user ID for the selected nickname (you may need to modify this based on your database schema)
        int selectedUserId = getUserIdByNickname(selectedNickname);

        // Open the user profile window for the selected follower/followee
        SwingUtilities.invokeLater(() -> new User_Profile_Window(selectedNickname, selectedUserId));
    }
    
    // Method to fetch user ID based on nickname
    public static int getUserIdByNickname(String nickname) {
        int userId = 0;

        // Example query (modify it based on your schema)
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
    	DBFollow Follow = new DBFollow();
    }
}
