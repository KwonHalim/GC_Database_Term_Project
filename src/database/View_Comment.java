package database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class View_Comment extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static JComboBox<String> articleComboBox;	// "Article"을 선택할 수 있는 콤보 박스
    private JTextArea chatTextArea;				// 코멘트를 표시할 텍스트 영역
    private Connection connection;				// 데이터베이스 연결을 위한 connection 객체

    public View_Comment() {
        initComponents();		// GUI 컴포넌트 초기화
        connectToDatabase();	// 데이터베이스 연결
        displayData();			// Article 정보 표시
    }

    private void initComponents() {
        articleComboBox = new JComboBox<>();	// "Article"을 선택할 수 있는 콤보 박스
        chatTextArea = new JTextArea(10, 40);	// 코멘트를 표시할 텍스트 영역
        chatTextArea.setEditable(false);
        
        // 텍스트 영역을 스크롤 가능하도록 함
        JScrollPane scrollPane = new JScrollPane(chatTextArea);

        JButton viewChatButton = new JButton("View Comment");	// 코멘트 보기 버튼
        viewChatButton.addActionListener(e -> viewChatForSelectedArticle());	// 선택한 "Article"에 대한 코멘트 표시

        JLabel articleLabel = new JLabel("Article #   ");	// Article 번호를 나타내는 레이블

        JPanel inputPanel = new JPanel(new BorderLayout());						// 전체 패널 (BorderLayout 사용)
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));	// padding(안쪽 여백) 생성
        inputPanel.add(articleLabel, BorderLayout.WEST);
        
        articleComboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 0, true),
                new EmptyBorder(0, 0, 0, 210) // 외부 여백 설정
        ));
        
        inputPanel.add(articleComboBox, BorderLayout.CENTER);
        inputPanel.add(viewChatButton, BorderLayout.EAST);
    

        viewChatButton.setBorder(new RoundedBorder(8));  // RoundedBorder class 정의

        // 타이틀 레이블 영역
        JLabel titleLabel = new JLabel("COMMENTS");
        titleLabel.setForeground(Color.ORANGE);  // 글자 색을 오렌지로 설정
        Font titleFont = new Font("serif", Font.ITALIC, 40);  // 폰트 스타일 설정
        titleLabel.setFont(titleFont);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        Font chatFont = new Font("nons-serif", Font.HANGING_BASELINE, 13);
        chatTextArea.setFont(chatFont);

        JPanel panel = new JPanel(new BorderLayout());                        // 전체 패널 (BorderLayout 사용)
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));    // padding(안쪽 여백) 생성
        panel.add(titleLabel, BorderLayout.NORTH);	 // 타이틀 레이블을 위쪽으로 설정
        panel.add(inputPanel, BorderLayout.CENTER);  // 입력 요소 패널을 중앙에 추가
        panel.add(scrollPane, BorderLayout.SOUTH);   // 텍스트 영역을 하단에 추가

        // 전체 패널을 프레임에 추가
        add(panel);

        setTitle("Comment Viewer");						// 프레임 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// 닫기 버튼 동작 설정
        pack();											// 최적의 크기로 프레임 조절
        setLocationRelativeTo(null);					// 프레임 화면 중앙에 위치
        setVisible(true);								// 프레임을 보이도록 설정
    }
    
    // 둥근 테두리에 대한 RoundedBorder class 정의
    public static class RoundedBorder extends LineBorder {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final int radius;

        public RoundedBorder(int radius) {
            super(Color.GRAY, 1, true);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            super.paintBorder(c, g, x, y, width, height);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
    }
    
    

    private void connectToDatabase() {
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
    }

    private void displayData() {
        try {
            String sql = "select * from article";	// Article 정보를 가져오는 SQL 쿼리

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int articleId = resultSet.getInt("article_id");
                articleComboBox.addItem(String.valueOf(articleId));	// 콤보 박스에 Article 번호 추가
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewChatForSelectedArticle() {
        chatTextArea.setText("");  // 이전 채팅 지우기

        try {
            int selectedArticleId = Integer.parseInt((String) articleComboBox.getSelectedItem());
            String sql = "select nickname, chat_text from userinfo natural join chat where image_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, selectedArticleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            boolean chatFound = false;	// 코멘트 발견 여부를 나타내는 변수

            while (resultSet.next()) {
                String nickName = resultSet.getString("nickname");
                String chatText = resultSet.getString("chat_text");
                chatTextArea.append("Nick name:   " + nickName + "\tChat:   " + chatText + "\n");
                chatFound = true;
            }
            
            if(!chatFound) {
            	chatTextArea.append("   Nothing...\n");		// 코멘트가 없을 경우 "Nothing..." 출력
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(View_Comment::new);
    }
}