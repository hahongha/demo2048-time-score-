package GAME2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Tile {
	public static final int WIDTH = 100;//chiều dài
	public static final int HEIGHT = 100;//chiều rộng
	public static final int SLIDE_SPEED =10;
	public static final int ARC_WIDTH = 15;//đô bo góc
	public static final int ARC_HEIGHT = 15;

	private int value;
	private BufferedImage tileImage;
	private Color background;
	private Color text;
	private Font font;
	private Point slideTo;
	private int x;
	private int y;
	
	private boolean beginningAnimation= true;
	private double scaleFirst = 0.1;//kich thuoc o thay doi khi 2 o key hop
	private BufferedImage beginningImage;
//	private boolean canAnimation= false;
	
	private boolean combineAnimation=false;
	private double scaleCombine= 1.3;
	private BufferedImage combineImage;
	private boolean canCombine =true;

	/*hàm khởi tạo bởi giá trị ô và các vị trí ô*/
	public Tile(int value, int x, int y) {
		this.value = value;
		this.x = x;
		this.y = y;
		slideTo = new Point(x, y);
		tileImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);//hình ảnh đệm cho ô
		beginningImage= new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		combineImage= new BufferedImage(WIDTH*2, HEIGHT*2, BufferedImage.TYPE_INT_ARGB);
		drawImage();
	}
	// hàm vẽ ô
	private void drawImage() {
		Graphics2D g = (Graphics2D) tileImage.getGraphics();
		if (value == 2) {
			background = new Color(0xe9e9e9);
			text = new Color(0x000000);
		} else if (value == 4) {
			background = new Color(0xe6daab);
			text = new Color(0x000000);
		} else if (value == 8) {
			background = new Color(0xf79d3d);
			text = new Color(0xffffff);
		} else if (value == 16) {
			background = new Color(0xf28007);
			text = new Color(0xffffff);
		} else if (value == 32) {
			background = new Color(0xf55e3b);
			text = new Color(0xffffff);
		} else if (value == 64) {
			background = new Color(0xff0000);
			text = new Color(0xffffff);
		} else if (value == 128) {
			background = new Color(0xe9de84);
			text = new Color(0xffffff);
		} else if (value == 256) {
			background = new Color(0xf6e873);
			text = new Color(0xffffff);
		} else if (value == 512) {
			background = new Color(0xf5e455);
			text = new Color(0xffffff);
		} else if (value == 1024) {
			background = new Color(0xf7e12c);
			text = new Color(0xffffff);
		} else if (value == 2048) {
			background = new Color(0xffe400);
			text = new Color(0xffffff);
		} else {
			background = Color.black;
			text = Color.white;
		}
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setColor(background);
		g.fillRoundRect(0, 0, WIDTH, HEIGHT, ARC_WIDTH, ARC_HEIGHT);

		g.setColor(text);

		if (value <= 64) {
			font = Game.main.deriveFont(45f);
		} else {
			font = Game.main.deriveFont(40f);
		}
		g.setFont(font);
		//set vi tri cua text so voi o do java viết từ trái -  dưới
		int drawX = WIDTH/2 -DrawUtils.getMessageWidth(""+value, font, g)/2;
		//từ tâm của ô thì ta dịch chuyển sang bên trái 1 nửa giá trị
		int drawY = HEIGHT/2 + DrawUtils.getMessageHeight(""+value, font, g)/2;
		//từ tâm của ô thì ta dịch chuyển xuống dưới 1 nửa giá trị
		g.drawString(""+value, drawX, drawY);
		g.dispose();
	}
	//animation
	public void update() {
		if(beginningAnimation) {
			AffineTransform transform = new AffineTransform();
			transform.translate(WIDTH/2 - scaleFirst*WIDTH/2, HEIGHT/2-scaleFirst*HEIGHT/2);
			transform.scale(scaleFirst, scaleFirst);
			Graphics2D g2d = (Graphics2D) beginningImage.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setColor(new Color(0, 0, 0, 0));
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			g2d.drawImage(tileImage, transform, null);
			scaleFirst += 0.1;
			g2d.dispose();
			if(scaleFirst>= 1) {
				beginningAnimation=false;
			}
		}else if(combineAnimation) {
			AffineTransform transform = new AffineTransform();
			transform.translate(WIDTH/2 - scaleCombine*WIDTH/2, HEIGHT/2-scaleCombine*HEIGHT/2);
			transform.scale(scaleCombine, scaleCombine);
			Graphics2D g2d = (Graphics2D) combineImage.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setColor(new Color(0, 0, 0, 0));
			g2d.fillRect(0, 0, WIDTH, HEIGHT);
			g2d.drawImage(tileImage, transform, null);
			scaleCombine -= 0.025;
			g2d.dispose();
			if(scaleCombine<= 1) {
				combineAnimation=false;
			}
		}
		
	}
	//hiển thị
	public void render(Graphics2D g) {
		if(beginningAnimation) {
			g.drawImage(beginningImage, x, y, null);
		}else if(combineAnimation) {
			g.drawImage(combineImage, (int)(x+WIDTH/2-scaleCombine*WIDTH/2), 
					(int)(y+HEIGHT/2-scaleCombine*HEIGHT/2), null);
		}else {
			g.drawImage(tileImage, x, y, null);//hiển thị ô tại vị trí chỉ định
		}
		
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		drawImage();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	public boolean CanCombine() {
		return canCombine;
	}

	public Point getSlideTo() {
		return slideTo;
	}

	public void setSlideTo(Point slideTo) {
		this.slideTo = slideTo;
	}

	public void setCanCombine(boolean canCombine) {
		this.canCombine = canCombine;
	}

	public boolean isCombineAnimation() {
		return combineAnimation;
	}

	public void setCombineAnimation(boolean combineAnimation) {
		this.combineAnimation = combineAnimation;
		if(combineAnimation) scaleCombine = 1.3;
	}
}
