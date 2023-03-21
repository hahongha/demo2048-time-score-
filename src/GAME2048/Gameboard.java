package GAME2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Random;
//chua sua duoc phan thoi gian
public class Gameboard {
	public static final int ROWS = 4;
	public static final int COLS = 4;

	private final int startingTiles = 2;// so o xuat hien
	private Tile[][] board;
	private boolean dead;
	private boolean won;
	private BufferedImage gameBoard;
	private BufferedImage finalBoard;
	private int x;
	private int y;

	private int score = 0;
	private int highScore = 0;
	private Font fontScore;
	private Font fontTime;
	
	private long elapsedMS;
	private long fastestMS;
	private long startTime;
	private String formatedTime="00:00:000";
	
	
	// saving
	private String saveDataPath;
	private String fileName = "SaveData";// khong ma hoa

	private boolean hasStarted;

	private static int SPACING = 10;
	public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH;
	public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT;
	
	
	public Gameboard(int x, int y) {
		try {
			saveDataPath = Gameboard.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
//			saveDataPath = System.getProperty("user.home")+"\\foldername";
		} catch (Exception e) {
			e.printStackTrace();
		}
		fontTime = Game.main.deriveFont(15f);
		fontScore = Game.main.deriveFont(36f);
		this.x = x;
		this.y = y;
		board = new Tile[ROWS][COLS];
		gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
		finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);

		startTime = System.nanoTime();
		
		loadHighScore();

		creatBoardImage();
		start();
	}

	private void creatSaveData() {
		try {
			File file = new File(saveDataPath, fileName);
			
			FileWriter output = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write("" + 0);
			// create fastest time
			writer.newLine();
			writer.write(""+Integer.MAX_VALUE);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadHighScore() {
		try {
			File f = new File(saveDataPath, fileName);
			if (!f.isFile()) {
				creatSaveData();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			highScore = Integer.parseInt(reader.readLine());
			// read fastest time
			fastestMS = Long.parseLong(reader.readLine());
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setHighScore() {
		FileWriter output = null;
		try {
			File f = new File(saveDataPath, fileName);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write(""+highScore);
			writer.newLine();
			if(elapsedMS<= fastestMS&& won) {
//				fastestMS= elapsedMS;
				writer.write(""+elapsedMS);
			}else {
				writer.write(""+fastestMS);
			}
			writer.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//tao bang
	private void creatBoardImage() {
		Graphics2D g = (Graphics2D) gameBoard.getGraphics();
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		g.setColor(Color.LIGHT_GRAY);

		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int x = SPACING + SPACING * col + Tile.WIDTH * col;
				int y = SPACING + SPACING * row + Tile.HEIGHT * row;
				g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
			}

		}

	}

	private void start() {
		for (int i = 0; i < startingTiles; i++) {
			spawnRandom();
		}
	}

	private void spawnRandom() {// xuat hien o bat ki
		Random random = new Random();
		boolean notValid = true;

		while (notValid) {// kiem tra o trong
			// chuyen doi tu 1 chieu sang 2 chieu
			int location = random.nextInt(ROWS * COLS);
			int row = location / ROWS;
			int col = location % COLS;
			Tile current = board[row][col];
			if (current == null) {// vi tri hien tai co trong khong
				int value = random.nextInt(10) < 8 ? 32 : 128;
				Tile tile = new Tile(value, getTileX(col), getTileY(row));
				board[row][col] = tile;
				notValid = false;
			}
		}
	}

	// set vi tri cho o
	private int getTileY(int row) {
		return SPACING + row * Tile.HEIGHT + row * SPACING;
	}

	private int getTileX(int col) {
		return SPACING + col * Tile.WIDTH + col * SPACING;
	}

	// bieu dien o
	public void render(Graphics2D g) {
		Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
		g2d.drawImage(gameBoard, 0, 0, null);// check

		// draw tiles
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.render(g2d);
			}
		}
		g.drawImage(finalBoard, x, y, null);
		g2d.dispose();
		
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(""+score, 30, 40);
		g.setColor(Color.RED);
		g.drawString("Best:"+highScore, Game.WIDTH-DrawUtils.getMessageWidth("Best:"+highScore, fontScore, g)-20, 40);
		
		
		g.setColor(Color.BLACK);
		g.drawString("Time:"+formatedTime, 30, 90);
		g.setColor(Color.RED);
		g.drawString("Fastest:" +formatTime(fastestMS), Game.WIDTH-DrawUtils.getMessageWidth("Fastest:" +formatTime(fastestMS), fontScore,g)-20, 160);
	}

	public void update() {
		
		if(!won && !dead) {
			if(hasStarted) {
				elapsedMS = (System.nanoTime()-startTime)/1000000;
				formatedTime = formatTime(elapsedMS);
			}else {
				startTime = System.nanoTime();
			}
		}
		
		checkKeys();
		
		if(score>=highScore) {
			highScore= score; 
		}
		
//			if(elapsedMS<= fastestMS&&won) {
//				fastestMS=elapsedMS;
//			}

		// kiem tra xem ban thang tro choi chua
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.update();
				// reset position
				resetPosition(current, row, col);
				if (current.getValue() == 2048) {
					won = true;
				}
			}
		}
	}
	
	private String formatTime(long millis) {
		String formatedTime;
		
		String hourFormat="";
		int hours = (int)(millis/3600000);
		if(hours>=1) {
			millis -= hours*3600000;
			if(hours<10) {
				hourFormat = "0"+hours;
			}
			else {
				hourFormat= ""+hours;
			}
		}else hourFormat = "00";
		hourFormat+=":";
		
		String minuteFormat="";
		int minutes = (int)(millis/60000);
		if(minutes>=1) {
			millis -= minutes*60000;
			if(minutes<10) {
				minuteFormat = "0"+minutes;
			}
			else {
				minuteFormat= ""+minutes;
			}
		}
		else minuteFormat="00";
//		minuteFormat+=":";
		
		String secondFormat="";
		int seconds = (int)(millis/1000);
		if(seconds>=1) {
			millis -= seconds*1000;
			if(seconds<10) {
				secondFormat = "0"+seconds;
			}
			else {
				secondFormat= ""+seconds;
			}
		}
		else secondFormat="00";
//		secondFormat+=":";
		
		String milliFormat ;
		if(millis>99) {
			milliFormat=""+millis;
		}else if(millis>9) {
			milliFormat="0"+millis;
		}else {
			milliFormat="00"+millis;
		}
		
		formatedTime= hourFormat+minuteFormat+":"+secondFormat+":"+milliFormat;
		return formatedTime;
		
	}
	
	
	
	// dich chuyen vi tri
	private void resetPosition(Tile current, int row, int col) {
		if (current == null)// neu o nay rong thi out
			return;
		// truy cap vi tri cua o can den
		int x = getTileX(col);
		int y = getTileY(row);

		// khoang cach giua o hien tai voi o can den
		int distX = current.getX() - x;
		int distY = current.getY() - y;

		// dich chuyen o
		if (Math.abs(distX) < Tile.SLIDE_SPEED) {
			current.setX(current.getX() - distX);
		}

		if (Math.abs(distY) < Tile.SLIDE_SPEED) {
			current.setY(current.getY() - distY);
		}

		if (distX < 0) {
			current.setX(current.getX() + Tile.SLIDE_SPEED);
		}

		if (distY < 0) {
			current.setY(current.getY() + Tile.SLIDE_SPEED);
		}

		if (distX > 0) {
			current.setX(current.getX() - Tile.SLIDE_SPEED);
		}

		if (distY > 0) {
			current.setY(current.getY() - Tile.SLIDE_SPEED);
		}
	}

//kiem tra xem bien nay co di chuyen duoc hay khong
	private boolean move(int row, int col, int horizontalDirection, int verticalDirection, Direction dir) {
		boolean canMove = false;
		// lay o hien tai
		Tile current = board[row][col];
		if (current == null)// neu o hien tai khoong thoat khoi ham
			return false;
		boolean move = true;
		int newCol = col;
		int newRow = row;
		while (move) {
			newCol += horizontalDirection;// cot se truot theo chieu ngang
			newRow += verticalDirection;
			if (checkOutOfBounds(dir, newRow, newCol))// kiem tra truot den vi tri hop le chua
				break;
			if (board[newRow][newCol] == null) {
				board[newRow][newCol] = current;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
				canMove = true;
			} else if (board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].CanCombine()) {
				board[newRow][newCol].setCanCombine(false);
				board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
				canMove = true;
				board[newRow - verticalDirection][newCol - horizontalDirection] = null;
				board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
				board[newRow][newCol].setCombineAnimation(true);
				// add to score
				score += board[newRow][newCol].getValue();
			} else {
				move = false;
			}
		}

		return canMove;
	}

	private boolean checkOutOfBounds(Direction dir, int row, int col) {
		if (dir == Direction.LEFT) {
			return col < 0;
		} else if (dir == Direction.RIGHT) {
			return col > COLS - 1;
		} else if (dir == Direction.UP) {
			return row < 0;
		} else if (dir == Direction.DOWN) {
			return row > ROWS - 1;
		}
		return false;
	}

	// di chuyen o
	private void moveTiles(Direction dir) {
		boolean canMove = false;// bien co the di chuyen
		int horizontalDirection = 0;// huong di chuyen theo chieu ngang
		int verticalDirection = 0;// huong di chuyen theo chieu doc

		if (dir == Direction.LEFT) {
			horizontalDirection = -1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, dir);
					else
						move(row, col, horizontalDirection, verticalDirection, dir);
				}
			}
		} else if (dir == Direction.RIGHT) {
			horizontalDirection = 1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = COLS - 1; col >= 0; col--) {
					if (!canMove) {
						canMove = move(row, col, horizontalDirection, verticalDirection, dir);
					} else
						move(row, col, horizontalDirection, verticalDirection, dir);
				}
			}
		} else if (dir == Direction.UP) {
			verticalDirection = -1;
			for (int row = 0; row < ROWS; row++) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, dir);
					else
						move(row, col, horizontalDirection, verticalDirection, dir);
				}
			}
		} else if (dir == Direction.DOWN) {
			verticalDirection = 1;
			for (int row = ROWS - 1; row >= 0; row--) {
				for (int col = 0; col < COLS; col++) {
					if (!canMove)
						canMove = move(row, col, horizontalDirection, verticalDirection, dir);
					else
						move(row, col, horizontalDirection, verticalDirection, dir);
				}
			}
		} else
			System.out.println(dir + "is not valid direction");
		// dich chuyen
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				Tile current = board[row][col];
				if (current == null)
					continue;
				current.setCanCombine(true);
			}
		}
		// neu con co the di chuyen thi tao ra o moi bat ki
		if (canMove) {
			spawnRandom();
			// check dead
			checkDead();
		}

	}

	private void checkDead() {
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				if (board[row][col] == null)
					return;
				if (checkSurroundingTiles(row, col, board[row][col])) {
					return;
				}
			}
		}
		dead = true;
		if(score>=highScore) {
			highScore= score; 
		}
		 setHighScore();
		
	}

	private boolean checkSurroundingTiles(int row, int col, Tile current) {
		if (row > 0) {
			Tile check = board[row - 1][col];
			if (check == null)
				return true;
			if (current.getValue() == check.getValue())
				return true;
		}

		if (row < ROWS - 1) {
			Tile check = board[row + 1][col];
			if (check == null)
				return true;
			if (current.getValue() == check.getValue())
				return true;
		}

		if (col > 0) {
			Tile check = board[row][col - 1];
			if (check == null)
				return true;
			if (current.getValue() == check.getValue())
				return true;
		}

		if (col < COLS - 1) {
			Tile check = board[row][col + 1];
			if (check == null)
				return true;
			if (current.getValue() == check.getValue())
				return true;
		}
		return false;
	}

	private void checkKeys() {
		if (Keyboard.typed(KeyEvent.VK_LEFT)) {
			// move tile left
			moveTiles(Direction.LEFT);
			if (!hasStarted)
				hasStarted = true;
		}

		if (Keyboard.typed(KeyEvent.VK_RIGHT)) {
			// move tile right
			moveTiles(Direction.RIGHT);
			if (!hasStarted)
				hasStarted = true;
		}

		if (Keyboard.typed(KeyEvent.VK_UP)) {
			// move tile up
			moveTiles(Direction.UP);
			if (!hasStarted)
				hasStarted = true;
		}

		if (Keyboard.typed(KeyEvent.VK_DOWN)) {
			// move tile down
			moveTiles(Direction.DOWN);
			if (!hasStarted)
				hasStarted = true;
		}
	}

}
