package database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class ArticleViewer extends JFrame {
    private static final long serialVersionUID = 1L;

    JTextArea chatTextArea;
    private Connection connection;
    String nickname;
    int userId;
    JButton Follow_Button = new JButton("Search");
    JButton My_Profile = new JButton("My information");

    public ArticleViewer(String nickname, int userId) {
        this.nickname = nickname;
        this.userId = userId;

        initComponents();
    }

    private void initComponents() {
        setTitle("Article Viewer");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel articlePanel = new JPanel(new GridLayout(0, 1));
        JScrollPane articleScroll = new JScrollPane(articlePanel);

        articleScroll.setBorder(BorderFactory.createEmptyBorder(4, 20, 4, 20));

        articleScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        articleScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        articleScroll.setPreferredSize(new Dimension(250, 300));

        JLabel title = new JLabel(nickname + "'s articles");
        title.setForeground(Color.ORANGE);
        Font titleFont = new Font("serif", Font.ITALIC, 40);
        title.setFont(titleFont);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        Follow_Button.setPreferredSize(new Dimension(150, 40));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(Follow_Button);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        My_Profile.setPreferredSize(new Dimension(150, 40));
        JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel2.add(My_Profile);
        mainPanel.add(buttonPanel2, BorderLayout.WEST);

        int i = 1;
        while (true) {
        	List<ArticleInfo> articles = getArticleIdsByIndexAndFolloweeId(userId, i);
            if (articles.isEmpty()) {
                break; // No more articles
            }

            for (ArticleInfo article : articles) {
                System.out.println(article.getArticleId());

                JButton articleButton = new JButton("article" + article.getArticleId() + " by " + article.getNickname());
                articleButton.setPreferredSize(new Dimension(100, 60));

                articleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Selected Article ID: " + article.getArticleId());
                        openArticleInfoWindow(article.getArticleId());
                    }
                });
                articleButton.setBackground(Color.WHITE);
                articlePanel.add(articleButton);

                i++;
            }
        }

        Follow_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Search search = new Search();
            }
        });
        My_Profile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User_Profile_Window profile = new User_Profile_Window(nickname, userId);
            }
        });

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(articleScroll, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
        setSize(400, 450);
    }

    void openArticleInfoWindow(int articleId) {
        JFrame articleFrame = new JFrame("article ID: " + articleId);
        articleFrame.setLocationRelativeTo(null);
        articleFrame.setResizable(false);

        List<String> articleInfos = getArticleInfos(articleId);

        String creation_date = articleInfos.get(0);
        int image_id = Integer.parseInt(articleInfos.get(1));

        // 이미지 표시 패널
        // 더미 이미지를 생성 (실제로는 getImageById 메서드를 사용해야 함)
        BufferedImage dummyImage = createDummyImage(image_id);

        // 이미지 표시 패널
        JPanel imagePanel = new JPanel();
        JLabel imageLabel = new JLabel(new ImageIcon(dummyImage));
        imagePanel.add(imageLabel);

        // creation_date 표시 레이블
        JLabel dateLabel = new JLabel("Creation Date: " + creation_date);

        // "View Comment" 버튼
        JButton viewCommentButton = new JButton("View Comment");
        viewCommentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // "View Comment" 버튼이 클릭되었을 때의 동작 추가
                openCommentWindow(articleId);
            }
        });

        // "Chat" 버튼
        JButton chatButton = new JButton("Chat");
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // "Chat" 버튼이 클릭되었을 때의 동작 추가
                openChatWindow(articleId);
            }
        });

        // 레이아웃 매니저를 GridBagLayout으로 설정하여 컴포넌트들을 배치
        articleFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // creation_date 레이블을 왼쪽 위에 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(8, 5, 0, 0); // 여백 추가
        articleFrame.add(dateLabel, gbc);

        // 이미지 표시 패널을 중앙에 배치
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10); // 여백 추가
        articleFrame.add(imagePanel, gbc);

        // "View Comment" 버튼을 오른쪽 아래에 배치
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.insets = new Insets(0, 0, 10, 13); // 여백 추가
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        articleFrame.add(viewCommentButton, gbc);

        // "Chat" 버튼을 왼쪽 아래에 배치
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(0, 13, 10, 0); // 여백 추가
        articleFrame.add(chatButton, gbc);

        articleFrame.setSize(400, 300);
        articleFrame.setVisible(true);
    }

    
    private void openCommentWindow(int articleId) {
    	JFrame commentFrame = new JFrame("article_id# " + articleId);
        commentFrame.setLocationRelativeTo(null);
        commentFrame.setResizable(false);
        
        chatTextArea = new JTextArea(10, 40);	// 코멘트를 표시할 텍스트 영역
        chatTextArea.setEditable(false);
        
        // 타이틀 레이블 영역
        JLabel titleLabel = new JLabel("COMMENTS");
        titleLabel.setForeground(Color.ORANGE);  // 글자 색을 오렌지로 설정
        Font titleFont = new Font("serif", Font.ITALIC, 40);  // 타이틀 폰트 스타일 설정
        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        Font chatFont = new Font("nons-serif", Font.HANGING_BASELINE, 13);	// 댓굴 폰트 스타일 설정
        chatTextArea.setFont(chatFont);
        
        // 텍스트 영역을 스크롤 가능하도록 함
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        
        JPanel panel = new JPanel(new BorderLayout());                        // 전체 패널 (BorderLayout 사용)
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));    // padding(안쪽 여백) 생성
        panel.add(titleLabel, BorderLayout.NORTH);	 // 타이틀 레이블을 위쪽으로 설정
        panel.add(scrollPane, BorderLayout.SOUTH);   // 텍스트 영역을 하단에 추가

        // 전체 패널을 프레임에 추가
        commentFrame.add(panel);
        
        commentFrame.setTitle("Comment Viewer");						// 프레임 제목 설정
        commentFrame.pack();											// 최적의 크기로 프레임 조절
        commentFrame.setLocationRelativeTo(null);					// 프레임 화면 중앙에 위치
        commentFrame.setVisible(true);								// 프레임을 보이도록 설정
       
    	
    	try (Connection connection = connectToDatabase()){
    		String sql = "select nickname, chat_text from userinfo natural join chat where image_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, articleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            boolean chatFound = false;	// 코멘트 발견 여부를 나타내는 변수

            while (resultSet.next()) {
                String nickName = resultSet.getString("nickname");
                String chatText = resultSet.getString("chat_text");
                chatTextArea.append("Nick name:   " + nickName + "\tChat:   " + chatText + "\n");
                chatFound = true;
            }
            
            if(!chatFound)
            	chatTextArea.append("   Nothing...\n");		// 코멘트가 없을 경우 "Nothing..." 출력
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    }
    
    // 더미 이미지 생성 메서드 (예시)
    private BufferedImage createDummyImage(int imageId) {
        BufferedImage image = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        
        // 배경색 설정
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 400, 200);
        
        // 폰트 설정
        Font font = new Font("Arial", Font.CENTER_BASELINE, 18);
        g.setFont(font);
        g.setColor(Color.lightGray);
        
        // 폰트 크기와 위치에 맞게 텍스트 그리기
        FontMetrics fontMetrics = g.getFontMetrics();
        int x = (400 - fontMetrics.stringWidth("image_num# " + imageId)) / 2; // 가운데 정렬
        int y = (180 - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
        g.drawString("image_num# " + imageId, x, y);

        return image;
    }
    
    List<ArticleInfo> getArticleIdsByIndexAndFolloweeId(int followeeId, int index) {
        List<ArticleInfo> articles = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {
            // 'follow' 테이블이 팔로워와 팔로우 대상 간의 관계를 포함한다고 가정합니다.
            String sql = "SELECT a.article_id, u.nickname FROM article a " +
                         "JOIN userinfo u ON a.user_id = u.user_id " +
                         "JOIN follow f ON a.user_id = f.followee_id " +
                         "WHERE f.follower_id = ? " +
                         "ORDER BY a.article_id LIMIT ?, 1";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, followeeId);
                preparedStatement.setInt(2, index - 1);  // 0부터 시작하는 인덱스

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int articleId = resultSet.getInt("article_id");
                        String nickname = resultSet.getString("nickname");
                        articles.add(new ArticleInfo(articleId, nickname));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return articles;
    }


    public class ArticleInfo {
        private int articleId;
        private String nickname;

        public ArticleInfo(int articleId, String nickname) {
            this.articleId = articleId;
            this.nickname = nickname;
        }

        public int getArticleId() {
            return articleId;
        }

        public String getNickname() {
            return nickname;
        }
    }
    
    List<String> getArticleInfos(int articleId) {
    	List<String> articleInfos = new ArrayList<>();
    	
    	try(Connection connection = connectToDatabase()){
    		String sql = "SELECT creation_date, image_id from article where article_id = ?";
    		
    		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, articleId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // 결과 집합에서 데이터 추출
                        String creationDate = resultSet.getString("creation_date");
                        int imageId = resultSet.getInt("image_id");

                        // 추출한 데이터를 리스트에 추가
                        articleInfos.add(creationDate);
                        articleInfos.add(Integer.toString(imageId));
                        // 또는 필요한 형식으로 데이터를 가공하여 저장
                    }
                }
            }
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    	return articleInfos;
    }

	private Connection connectToDatabase() {
        try {
            // MySQL JDBC 드라이버 클래스를 동적으로 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 데이터베이스 연결 정보 설정
            String url = "jdbc:mysql://localhost/instagram";
            String user = "root", passwd = "12345";

            // DriverManager를 사용하여 데이터베이스에 연결
            connection = DriverManager.getConnection(url, user, passwd);

            // 연결 성공 메시지 출력
            System.out.println(connection);
            
        } catch (ClassNotFoundException e) {
            // JDBC 드라이버 로드 실패 시 예외 처리
            e.printStackTrace();
        } catch (SQLException e) {
            // 데이터베이스 연결 실패 시 예외 처리
            e.printStackTrace();
        }
		return connection;
    }
	
	private void openChatWindow(int articleId) {
        JFrame chatFrame = new JFrame("Chat for Article ID: " + articleId);
        chatFrame.setLocationRelativeTo(null);
        chatFrame.setResizable(false);

        JTextArea chatInput = new JTextArea(5, 30);
        JButton finishButton = new JButton("Finish");

        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Insert chat data into the database
                insertChatData(articleId, chatInput.getText());

                // Update Comment Viewer to display the new chat
                openCommentWindow(articleId);

                // Close the chat window
                chatFrame.dispose();
            }
        });

        chatFrame.setLayout(new BorderLayout());
        chatFrame.add(chatInput, BorderLayout.CENTER);
        chatFrame.add(finishButton, BorderLayout.SOUTH);

        chatFrame.pack();
        chatFrame.setVisible(true);
    }

	private void insertChatData(int articleId, String chatText) {
	    try (Connection connection = connectToDatabase()) {
	        String sql = "INSERT INTO chat (create_date, user_id, image_id, chat_text) VALUES (NOW(), ?, ?, ?)";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setInt(1, userId);  // Assuming userId is accessible here
	            preparedStatement.setInt(2, articleId); // Assuming imageId is mapped to articleId
	            preparedStatement.setString(3, chatText);
	            preparedStatement.executeUpdate();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
    

    public static void main(String[] args) throws SQLException {
    	int userId = 3;
    	String nickname = "sky";
    	new ArticleViewer(nickname, userId);
    }
}