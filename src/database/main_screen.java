package database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class main_screen extends JFrame {

    private JPanel mainPanel;
    private JButton refreshButton;
    private JButton profileButton;
    private JScrollPane postScrollPane;
    private JPanel postPanel;

    public main_screen() {
        setTitle("Instagram");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout());

        // 상단 패널에 새로고침 버튼과 프로필 버튼 추가
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                main_screen ms = new main_screen();
            }
        });

        profileButton = new JButton("Profile");
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = "sky";
                int userId = 16;
                User_Profile_Window window = new User_Profile_Window(nickname, userId);
            }
        });

        // Title
        JLabel title = new JLabel("Instagram Feed");
        title.setForeground(Color.ORANGE);
        Font titleFont = new Font("serif", Font.ITALIC, 40);
        title.setFont(titleFont);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        mainPanel.add(title, BorderLayout.NORTH);

        // 상단 패널에 새로고침 버튼과 프로필 버튼 추가
        JPanel topPanel = new JPanel();
        topPanel.add(refreshButton);
        topPanel.add(profileButton);

        mainPanel.add(topPanel, BorderLayout.SOUTH);

        // 중앙에 게시물을 담을 스크롤 패널 추가
        postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postScrollPane = new JScrollPane(postPanel);
        postScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤 비활성화

        mainPanel.add(postScrollPane, BorderLayout.CENTER);

        // 기본적인 게시물 몇 개 추가
        for (int i = 0; i < 10; i++) {
            addPost("Post " + (i + 1), i + 1); // 버튼을 추가하는 메서드로 변경
        }

        add(mainPanel);
        setVisible(true);
    }

    // 게시물을 추가하는 메서드
    private void addPost(String postText, final int postId) {
        JPanel post = new JPanel();
        post.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        post.setPreferredSize(new Dimension(600, 100));
        post.setMaximumSize(new Dimension(600, 100));
        post.setLayout(new BorderLayout());

        JButton postButton = new JButton(postText);
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 버튼이 클릭되었을 때 게시물로 이동하는 메서드 호출
                goToPost(postId);
            }
        });
        post.add(postButton, BorderLayout.CENTER);

        postPanel.add(post);
        revalidate();
        repaint();
    }

    // 게시물로 이동하는 메서드 (테스트 용도)
    private void goToPost(int postId) {
        JOptionPane.showMessageDialog(this, "Go to Post " + postId);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new main_screen();
            }
        });
    }
}