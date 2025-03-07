package game;

import java.awt.Image;

public class GameObject {
	private int x;
	private int y;
	private int width;
	private int height;
	private Image image;
	private int startX;
	private int startY;

	public GameObject(Image image, int x, int y, int width, int height) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.startX = x;
		this.startY = y;
	}

	// ----------- Getter/Setter -----------
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}
}
