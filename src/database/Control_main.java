package database;

public class Control_main {
    public static void main(String[] args) {
        // instagram_Login_UI 클래스의 인스턴스 생성
        instagram_Login_UI login = new instagram_Login_UI();

        // 로그인 UI를 표시하고 사용자가 닫을 때까지 대기
        login.setVisible(true);

        // 로그인 UI가 종료될 때까지 대기
        synchronized (login) {
            try {
                login.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        

        // 로그인 UI가 종료된 후, main_nickname에 접근
        String nickname = login.main_nickname;
        int userId = login.userId;
        System.out.println(userId);
        // main_nickname 출력
        System.out.println(nickname);
        new ArticleViewer(nickname, userId);
        // User_Profile_Window 창 열기
//        User_Profile_Window profile = new User_Profile_Window(nickname,userID);
        // User_Profile_Window 창을 표시
//        profile.setVisible(true);
    }
}
//	16	bnvc54	computer		