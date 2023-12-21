package database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class User_Profile_Window extends JFrame {

	private static final long serialVersionUID = 1L;
	String nickname;
	ImageIcon UserImage = new ImageIcon(
			"C:\\Users\\ojh89\\eclipse-workspace\\Database_TeamPro\\src\\Image\\profileimage.jpg");
	ImageIcon FriendImage = new ImageIcon(
			"C:\\Users\\ojh89\\eclipse-workspace\\Database_TeamPro\\src\\Image\\FrImage.jpg");
	private Connection con = null;
	int article_num = 0;
	int userId;
	String intro;

	public User_Profile_Window(String nickname, int userId) {
		this.nickname = nickname;
		this.userId = userId;
		sqlConnect();
		article_num = getArticle_num();
		intro = getIntro();
		initUI();
	}

	// initUI() method
	private void initUI() {
	    setTitle("User Profile");
	    setSize(400, 600);
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setLocationRelativeTo(null);

	    JPanel mainPanel = new JPanel(new BorderLayout());
	    JPanel infoPanel = new JPanel(new BorderLayout());
	    JPanel sub_infoPanel1 = new JPanel(new GridLayout(1, 3));
	    JPanel sub_infoPanel2 = new JPanel(new BorderLayout());
	    JPanel articlePanel = new JPanel(new GridLayout(6, 3));
	    JPanel followerPanel = new JPanel(new GridLayout(10, 1));
	    JPanel followeePanel = new JPanel(new GridLayout(10, 1));
	    JScrollPane followerScroll = new JScrollPane(followerPanel);
	    JScrollPane followeeScroll = new JScrollPane(followeePanel);
	    JScrollPane articleScroll = new JScrollPane(articlePanel);

	    sub_infoPanel1.setBorder(BorderFactory.createEmptyBorder(12, 132, 12, 132));
	    sub_infoPanel2.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
	    articleScroll.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

	    followerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    followerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	    followeeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    followeeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	    articleScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    articleScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    articleScroll.setPreferredSize(new Dimension(400, 250));

	    JLabel title = new JLabel("My Profile");
	    title.setForeground(Color.ORANGE);
	    Font titleFont = new Font("serif", Font.ITALIC, 40);
	    title.setFont(titleFont);
	    title.setHorizontalAlignment(SwingConstants.CENTER);

	    JButton profImage = new JButton(UserImage);
	    profImage.setBackground(Color.WHITE);

	    JTextArea intro_user = new JTextArea(intro);
	    intro_user.setRows(1);
	    intro_user.setLineWrap(true);
	    intro_user.setSize(1, 30);

	    sub_infoPanel1.add(profImage);
	    sub_infoPanel2.add(intro_user);

	    int i = 1;
	    while (true) {
	        List<Integer> articleIds = getArticleIdsByIndexAndUserId(userId, i);
	        if (articleIds.isEmpty()) {
	            break;
	        }

	        for (int articleId : articleIds) {
	            System.out.println(articleId);

	            JButton articleButton = new JButton("article" + i);
	            articleButton.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    System.out.println("Selected Article ID: " + articleId);
	                    ArticleViewer view = new ArticleViewer(nickname, userId);
	                    view.setVisible(false);
	                    view.openArticleInfoWindow(articleId);
	                }
	            });
	            articleButton.setBackground(Color.WHITE);
	            articlePanel.add(articleButton);

	            i++;
	        }
	    }

	    JPanel followPanel = new JPanel(new GridLayout(1, 2));
	    JLabel followerLabel = new JLabel("Followers:");
	    JLabel followeeLabel = new JLabel("Followees:");

	    List<String> followers = getFollowers();
	    List<String> followees = getFollowees();

	    for (String follower : followers) {
	        JButton foButton = new JButton(follower);
	        foButton.setBackground(Color.WHITE);
	        foButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // Open User_Profile_Window for the selected follower
	                openUserProfileWindow(follower);
	            }
	        });
	        followerPanel.add(foButton);
	    }

	    for (String followee : followees) {
	        JButton foButton = new JButton(followee);
	        foButton.setBackground(Color.WHITE);
	        foButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                // Open User_Profile_Window for the selected followee
	                openUserProfileWindow(followee);
	            }
	        });
	        followeePanel.add(foButton);
	    }

	    followPanel.add(new JPanel(new BorderLayout()) {
	        {
	            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	            add(followerLabel, BorderLayout.NORTH);
	            add(followerScroll, BorderLayout.CENTER);
	        }
	    });
	    followPanel.add(new JPanel(new BorderLayout()) {
	        {
	            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
	            add(followeeLabel, BorderLayout.NORTH);
	            add(followeeScroll, BorderLayout.CENTER);
	        }
	    });

	    infoPanel.add(sub_infoPanel1, BorderLayout.NORTH);
	    infoPanel.add(followPanel, BorderLayout.CENTER);
	    infoPanel.add(sub_infoPanel2, BorderLayout.SOUTH);

	    mainPanel.add(title, BorderLayout.NORTH);
	    mainPanel.add(infoPanel, BorderLayout.CENTER);
	    mainPanel.add(articleScroll, BorderLayout.SOUTH);

	    add(mainPanel);
	    setVisible(true);

	    profImage.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            showImageInDialog(UserImage.getImage());
	        }
	    });
	}

	private void showImageInDialog(Image img) {
		JDialog dialog = new JDialog(this, "Profile Image", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.add(new JLabel(new ImageIcon(img)));
		dialog.setSize(200, 200);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	List<Integer> getArticleIdsByIndexAndUserId(int userId, int index) {
		List<Integer> articleIds = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root",
				"12345")) {
			String sql = "SELECT article_id FROM article WHERE user_id = ? ORDER BY user_id LIMIT ?, 1";
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setInt(1, userId);
				preparedStatement.setInt(2, index - 1); // 0          ϴ   ε          ϹǷ  index - 1
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						articleIds.add(resultSet.getInt("article_id"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return articleIds;
	}
	
	private void openUserProfileWindow(String selectedNickname) {
	    SwingUtilities.invokeLater(() -> new User_Profile_Window(selectedNickname, getUserId(selectedNickname)));
	}

	private int getUserId(String selectedNickname) {
	    int selectedUserId = -1;

	    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root", "12345")) {
	        String sql = "SELECT user_id FROM userinfo WHERE nickname = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setString(1, selectedNickname);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                    selectedUserId = resultSet.getInt("user_id");
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return selectedUserId;
	}

	private int getArticle_num() {
		try {
			String sql = "SELECT COUNT(*) FROM userinfo JOIN article ON userinfo.user_id = article.user_id WHERE nickname = ?";
			PreparedStatement preparedStatement = con.prepareStatement(sql);
			preparedStatement.setString(1, nickname);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				article_num = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return article_num;
	}

	private String getIntro() {
		try {
			String sql = "SELECT intro FROM introduce WHERE nickname = ?";
			PreparedStatement preparedStatement = con.prepareStatement(sql);
			preparedStatement.setString(1, nickname);
			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				intro = resultSet.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return intro;
	}

	private List<String> getFollowers() {
		List<String> followers = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root",
				"12345")) {
			String sql = "SELECT follower_id FROM follow WHERE followee_id = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setInt(1, userId);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						int followerId = resultSet.getInt("follower_id");
						String followerNickname = getNicknameById(followerId);
						followers.add(followerNickname);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return followers;
	}

	private List<String> getFollowees() {
		List<String> followees = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root",
				"12345")) {
			String sql = "SELECT followee_id FROM follow WHERE follower_id = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setInt(1, userId);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						int followeeId = resultSet.getInt("followee_id");
						String followeeNickname = getNicknameById(followeeId);
						followees.add(followeeNickname);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return followees;
	}

	private String getNicknameById(int userId) {
		String nickname = "";
		try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root",
				"12345")) {
			String sql = "SELECT nickname FROM userinfo WHERE user_id = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setInt(1, userId);
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						nickname = resultSet.getString("nickname");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nickname;
	}

	private void sqlConnect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/instagram", "root", "12345");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new User_Profile_Window("YourNickname", 1));
	}
}
