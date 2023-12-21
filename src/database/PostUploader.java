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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PostUploader extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField captionField;
    private JButton uploadButton;

    private String nickname;
    private int userId;
    private User_Profile_Window userProfileWindow;

    public PostUploader(String nickname, int userId, User_Profile_Window userProfileWindow) {
        this.nickname = nickname;
        this.userId = userId;
        this.userProfileWindow = userProfileWindow;  // UserProfileWindow 참조 추가
        initComponents();
    }

    private void initComponents() {
        setTitle("Post Uploader");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new FlowLayout());

        JLabel captionLabel = new JLabel("Caption:");
        captionField = new JTextField(20);
        uploadButton = new JButton("Upload");

        inputPanel.add(captionLabel);
        inputPanel.add(captionField);
        inputPanel.add(uploadButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadPost();
            }
        });

        add(mainPanel);
        pack();
        setVisible(true);
    }

    private void uploadPost() {
        String caption = captionField.getText();
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String creationDate = dateFormat.format(currentDate);

        // 이미지 선택 다이얼로그를 열어 이미지 ID를 얻어오는 메서드 호출
        int imageId = openImageSelector();

        if (imageId == -1) {
            // 사용자가 이미지 선택을 취소한 경우
            return;
        }

        try (Connection connection = connectToDatabase()) {
            String sql = "INSERT INTO article (user_id, creation_date, image_id, caption) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, creationDate);
                preparedStatement.setInt(3, imageId);
                preparedStatement.setString(4, caption);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "게시물이 성공적으로 업로드되었습니다!");
                    dispose();  // 성공적으로 업로드된 후 PostUploader 창을 닫습니다.

                    // getGeneratedPostId() 메서드 대신에 실제로 사용할 메서드 또는 로직으로 변경합니다.
                    userProfileWindow.addPostToProfile(getGeneratedPostId(), caption);
                } else {
                    JOptionPane.showMessageDialog(this, "게시물 업로드에 실패했습니다. 다시 시도해 주세요.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error. Please try again later.");
        }
    }
    
    private int openImageSelector() {
        String userInput = JOptionPane.showInputDialog(this, "Enter Image ID:");
        
        if (userInput != null && !userInput.isEmpty()) {
            try {
                // 사용자가 입력한 값을 정수로 변환하여 반환
                return Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.");
            }
        }

        // 사용자가 취소하거나 유효하지 않은 입력을 한 경우 -1을 반환하거나 다른 적절한 값으로 설정
        return -1;
    }
    
    private int getGeneratedPostId() {
        int generatedPostId = -1; // 초기값으로 설정한 값

        try (Connection connection = connectToDatabase()) {
            String sql = "SELECT LAST_INSERT_ID() AS last_id";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        generatedPostId = resultSet.getInt("last_id");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // 예외 처리: 데이터베이스 오류 발생 시 적절한 조치를 취하거나 로그를 남길 수 있습니다.
        }

        return generatedPostId;
    }
    
    private Connection connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost/instagram";
        String user = "root";
        String password = "4654";  // Replace with your database password

        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        int userId = 1;  // 실제 사용자 ID로 교체
        String nickname = "example_user";  // 실제 닉네임으로 교체
        new PostUploader(nickname, userId, null); // userProfileWindow에 적절한 값을 전달
    }
}
