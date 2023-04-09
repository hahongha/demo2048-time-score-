package GAME2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class Game extends JPanel implements KeyListener, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; // thoi gian chay tuan tu hoa lien ket
	public static final int WIDTH = Gameboard.BOARD_WIDTH+ 2* Gameboard.SPACING;
	public static final int HEIGHT = Gameboard.BOARD_WIDTH + 250;
	public static final Font main = new Font("Bebas Neue Regular", Font.PLAIN, 28);
	private Thread game;
	private boolean running;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private Gameboard board;

	public Game() {
		setFocusable(true);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addKeyListener(this);
		board = new Gameboard(WIDTH / 2 - Gameboard.BOARD_WIDTH / 2, HEIGHT - Gameboard.BOARD_HEIGHT - 10);
	}

	private void update() {
		board.update();
		Keyboard.update();

	}

	private void render() {
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		board.render(g);
		g.dispose();

		Graphics2D g2d = (Graphics2D) getGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
	}

	@Override
	public void run() {
		int fps = 0, update = 0;//fps và số lần update
		long fpsTimer = System.currentTimeMillis();//bộ đếm thời gian fps tính bằng miliseconds
		double nsPerUpdate = 1000000000.0 / 60;//có bao nhiêu nanosenconds giữa các lần cập nhật với nhau

		// last update time in nanosecond
		double then = System.nanoTime();
		double unprocessed = 0;

		while (running) {
			boolean shouldRender = false;
			double now = System.nanoTime();
			unprocessed += (now - then) / nsPerUpdate;
			then = now;

			// update queue
			while (unprocessed >= 1) {
				update++;
				update();
				unprocessed--;
				shouldRender = true;
			}

			// render
			if (shouldRender) {
				fps++;
				render();
				shouldRender = false;
			} else {
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

//		 FPS Timer
		if (System.currentTimeMillis() - fpsTimer > 1000) {
			System.out.printf("%d fps %d updates", fps, update);
			System.out.println();
			fps = 0;
			update = 0;
			fpsTimer += 1000;
		}

	}

	public synchronized void start() {
		if (running)
			return;
		running = true;
		game = new Thread(this, "game");
		game.start();
	}

	public synchronized void stop() {
		if (!running)
			return;
		running = false;
		System.exit(0);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		Keyboard.keyPressed(e);
		if(e.getKeyCode()== KeyEvent.VK_X) {
			stop();
		}
		if (e.getKeyCode()==KeyEvent.VK_MULTIPLY) {
			board.play.volumeMute();
//			if (!playhasStarted)
//				hasStarted = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Keyboard.keyReleased(e);
	}
}