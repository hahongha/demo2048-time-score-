package GAME2048;

import java.awt.event.KeyEvent;

public class Keyboard {
public static boolean[] pressed = new boolean[256];//so phim duoc nhan trong 1 tro choi
public static boolean[] prev = new boolean[256];
private Keyboard() {
	
}

public static void update() {
	for (int i = 0; i < 10; i++) {//cac phim lap lai
		if(i==0) prev[KeyEvent.VK_LEFT] = pressed[KeyEvent.VK_LEFT];
		else if(i==1) prev[KeyEvent.VK_RIGHT] = pressed[KeyEvent.VK_RIGHT];
		else if(i==2) prev[KeyEvent.VK_UP] = pressed[KeyEvent.VK_UP];
		else if(i==3) prev[KeyEvent.VK_DOWN] = pressed[KeyEvent.VK_DOWN];
		else if(i==4) prev[KeyEvent.VK_M] = pressed[KeyEvent.VK_M];
		else if(i==5) prev[KeyEvent.VK_SUBTRACT] = pressed[KeyEvent.VK_SUBTRACT];
		else if(i==6) prev[KeyEvent.VK_MULTIPLY] = pressed[KeyEvent.VK_MULTIPLY];
		else if(i==7) prev[KeyEvent.VK_ADD] = pressed[KeyEvent.VK_ADD];
		else if(i==8) prev[KeyEvent.VK_N] = pressed[KeyEvent.VK_N];
		else if(i==9) prev[KeyEvent.VK_P] = pressed[KeyEvent.VK_P];
	}
}

public static void keyPressed(KeyEvent e) {
	pressed[e.getKeyCode()] = true;//phim da duoc nhan
}

public static void keyReleased(KeyEvent e) {
	pressed[e.getKeyCode()]= false;//vo hieu hoa phim truoc
}
//phim se duoc thuc hien ngay sau khi nhan cho du 2 phim bam cach nhau co 1 tích tắc
public static boolean typed(int keyEvent) {
	return !pressed[keyEvent]&& prev[keyEvent];
}

}
