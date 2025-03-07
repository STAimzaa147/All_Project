package game;

import java.awt.Image;

public class Block extends GameObject implements IMovable {
	private char direction = 'U'; // U D L R
	private int velocityX = 0;
	private int velocityY = 0;

	private boolean isPowerPellet = false;
	private boolean isFruit = false;

	public Block(Image image, int x, int y, int width, int height) {
		super(image, x, y, width, height);
	}

	// ----------- Getter/Setter -----------
	public char getDirection() {
		return direction;
	}

	public void setDirection(char direction) {
		this.direction = direction;
	}

	public int getVelocityX() {
		return velocityX;
	}

	public void setVelocityX(int velocityX) {
		this.velocityX = velocityX;
	}

	public int getVelocityY() {
		return velocityY;
	}

	public void setVelocityY(int velocityY) {
		this.velocityY = velocityY;
	}

	@Override
	public void updateDirection(char newDirection) {
		char prevDirection = this.direction;
		this.direction = newDirection;
		updateVelocity();

		int oldX = getX();
		int oldY = getY();

		setX(getX() + getVelocityX());
		setY(getY() + getVelocityY());

		for (Block wall : PacMan.walls) {
			if (PacMan.collision(this, wall)) {
				setX(oldX);
				setY(oldY);
				this.direction = prevDirection;
				updateVelocity();
			}
		}
	}

	@Override
	public void updateVelocity() {
		int tileSizeQuarter = PacMan.tileSize / 4;
		switch (direction) {
		case 'U':
			velocityX = 0;
			velocityY = -tileSizeQuarter;
			break;
		case 'D':
			velocityX = 0;
			velocityY = tileSizeQuarter;
			break;
		case 'L':
			velocityX = -tileSizeQuarter;
			velocityY = 0;
			break;
		case 'R':
			velocityX = tileSizeQuarter;
			velocityY = 0;
			break;
		}
	}

	@Override
	public void reset() {
		setX(getStartX());
		setY(getStartY());
	}

	public void setPowerPellet(boolean isPowerPellet) {
		this.isPowerPellet = isPowerPellet;
	}

	public boolean isPowerPellet() {
		return isPowerPellet;
	}

	public boolean isFruit() {
		return isFruit;
	}

	public void setFruit(boolean fruit) {
		isFruit = fruit;
	}

}
