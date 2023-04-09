package GAME2048;

import javax.swing.JFrame;

public class Start {
	public static void main(String[] args) {
		Game game = new Game();
		
		JFrame window = new JFrame("2048 test");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//tắt chương trình khi nhấn X
		window.setResizable(false);//chặn không cho thay đổi kích thước
		window.add(game);
		window.pack();//điều chỉnh chiều rộng và chiều cao sao cho chứa được tất cả các thành phần hiển thị
		window.setLocationRelativeTo(null);//căn giữa
		
		game.start();// bắt đầu trò chơi
		window.setVisible(true);//hiển thị
	}
}
