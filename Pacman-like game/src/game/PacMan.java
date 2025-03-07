package game;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import javax.sound.sampled.*;

import javafx.scene.media.AudioClip;
import javax.sound.sampled.*; // For Clip and sound effects
import java.net.URL;
import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.net.URL;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import javax.swing.*;

import javafx.scene.media.AudioClip;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
public class PacMan extends JPanel implements ActionListener, KeyListener {
	public static int tileSize = 32;
	public static HashSet<Block> walls;
	private String[] tileMap;
	private int rowCount;
	private int columnCount;
	private int boardWidth;
	private int boardHeight;
	private Image wallImage;
	private Image blueGhostImage;
	private Image orangeGhostImage;
	private Image pinkGhostImage;
	private Image redGhostImage;
	private Image pacmanUpImage;
	private Image pacmanDownImage;
	private Image pacmanLeftImage;
	private Image pacmanRightImage;
	public static HashSet<Block> foods;
	public static HashSet<Block> ghosts;
	public static Block pacman;
	private Timer gameLoop;
	private char[] directions = { 'U', 'D', 'L', 'R' };
	private Random random = new Random();
	private int score = 0;
	private int lives = 3;
	private boolean gameOver = false;
	private GameState gameState = GameState.MENU_STATE;
	private JButton startButton;
	private JButton easyMapButton, mediumMapButton, hardMapButton;
	private boolean powerMode = false;
	private Timer powerModeTimer;
	private Image pacmanPowerRightImage;
	private Image pacmanPowerLeftImage;
	private Image pacmanPowerUpImage;
	private Image pacmanPowerDownImage;
	private Image powerPelletImage;
	private Image heartImage;
	private int powerModeTimeLeft = 0;
	private ArrayList<Image> fruitImages = new ArrayList<>();
	private int xPosition = 0;
	private int xPosition1 = 0;
	private ImageIcon backgroundImageIcon;
	private ImageIcon backgroundImageIcon_map;
	public Thread animationThread;

	private AudioClip backgroundMusic;
	private String currentMusic = "";
	private double musicVolume = 0.3; // Default volume (0.0 = mute, 1.0 = max)

	// Background Music
	public void playBackgroundMusic(String soundFile, double volume) {
	    try {
	        URL resource = ClassLoader.getSystemResource((soundFile));
	        String resourcePath = resource.toString();
	        
	        if (resourcePath == null) {
	            System.err.println("Sound file not found: sound/" + soundFile);
	            return;
	        }
	        if (resourcePath.equals(currentMusic)) {
	            return; // Prevent restarting the same music
	        }

	        stopBackgroundMusic();
//	        System.out.println(ClassLoader.getSystemResource(soundFile).toString());
	        backgroundMusic = new AudioClip(ClassLoader.getSystemResource(soundFile).toString());
	        backgroundMusic.setCycleCount(AudioClip.INDEFINITE);
	        backgroundMusic.setVolume(volume); // Set volume here
	        backgroundMusic.play();

	        musicVolume = volume;
	        currentMusic = ClassLoader.getSystemResource(soundFile).toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void setMusicVolume(double volume) {
	    musicVolume = Math.max(0.0, Math.min(1.0, volume)); // Limit volume
	    if (backgroundMusic != null) {
	        backgroundMusic.setVolume(musicVolume);
	    }
	}

	public void stopBackgroundMusic() {
	    if (backgroundMusic != null) {
	        backgroundMusic.stop();
	    }
	}

	// Sound Effects
	public void playSound(String soundFile, float volume) {
	    try {
	        URL resource = ClassLoader.getSystemResource((soundFile).toString());
	        if (resource == null) {
	            System.err.println("Sound file not found: " + soundFile);
	            return;
	        }

	        AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource);
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioStream);

	        // Volume Control
	        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        float dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), volume));
	        gainControl.setValue(dB);

	        clip.start();
	    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
	        e.printStackTrace();
	    }
	}
	
	public PacMan() {
		if (tileMap == null) {
			tileMap = Map.tileMap2;
		}
		rowCount = tileMap.length;
		columnCount = (rowCount > 0) ? tileMap[0].length() : 0;
		boardWidth = columnCount * tileSize;
		boardHeight = rowCount * tileSize;

		setPreferredSize(new Dimension(boardWidth, boardHeight));
		setBackground(Color.BLACK);
		addKeyListener(this);
		setFocusable(true);

		for (int i = 1; i <= 10; i++) {
			Image img = new ImageIcon(getClass().getResource("/fruit" + i + ".png")).getImage();
			if (img == null) {
				System.out.println("Failed to load: fruit" + i + ".png");
			} else {
				fruitImages.add(img);
			}
		}

		wallImage = new ImageIcon(getClass().getResource("/wall.png")).getImage();
		blueGhostImage = new ImageIcon(getClass().getResource("/blueGhost.png")).getImage();
		orangeGhostImage = new ImageIcon(getClass().getResource("/orangeGhost.png")).getImage();
		pinkGhostImage = new ImageIcon(getClass().getResource("/pinkGhost.png")).getImage();
		redGhostImage = new ImageIcon(getClass().getResource("/redGhost.png")).getImage();

		pacmanUpImage = new ImageIcon(getClass().getResource("/pacmanUp.png")).getImage();
		pacmanDownImage = new ImageIcon(getClass().getResource("/pacmanDown.png")).getImage();
		pacmanLeftImage = new ImageIcon(getClass().getResource("/pacmanLeft.png")).getImage();
		pacmanRightImage = new ImageIcon(getClass().getResource("/pacmanRight.png")).getImage();

		pacmanPowerRightImage = new ImageIcon(getClass().getResource("/pacmanSpecial_right.png")).getImage();
		pacmanPowerLeftImage = new ImageIcon(getClass().getResource("/pacmanSpecial_left.png")).getImage();
		pacmanPowerUpImage = new ImageIcon(getClass().getResource("/pacmanSpecial_up.png")).getImage();
		pacmanPowerDownImage = new ImageIcon(getClass().getResource("/pacmanSpecial_down.png")).getImage();

		powerPelletImage = new ImageIcon(getClass().getResource("/no_ghost.png")).getImage();
		heartImage = new ImageIcon(getClass().getResource("/heart.png")).getImage();
		loadMap();

		for (Block ghost : ghosts) {
			char newDirection = directions[random.nextInt(4)];
			ghost.updateDirection(newDirection);
		}

		gameLoop = new Timer(50, this); // 20fps (1000/50)
		gameLoop.start();
	}

	public void drawBackgroundMenu() {
		// Load GIF image
		try {
			backgroundImageIcon = new ImageIcon(getClass().getResource("/bg13.gif"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawMenu(Graphics g) {
		// Enable anti-aliasing for smoother text rendering
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawBackgroundMenu();
		if (backgroundImageIcon != null) {
			// Draw the first instance of the GIF
			g.drawImage(backgroundImageIcon.getImage(), xPosition1, 0, boardWidth, boardHeight, this);

			// Draw the second instance of the GIF when the first one goes off the screen
			if (xPosition1 < 0) {
				// Draw the second image (to the right of the first image)
				g.drawImage(backgroundImageIcon.getImage(), 0, 0, boardWidth, boardHeight, this);
			}
		} else {
			g.setColor(new Color(0, 100, 100)); // Fallback gray background
			g.fillRect(0, 0, boardWidth, boardHeight);
		}

		// Title (centered, larger font, gradient effect)
		try {
			InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
			Font arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(35f);
			g.setFont(arcadeFont);
		} catch (Exception e) {
			e.printStackTrace();
		}

		g.setColor(new Color(255, 223, 0)); // Yellow color
		String title = "Pac-Man Game";
		int titleWidth = g.getFontMetrics().stringWidth(title);
		g.drawString(title, (boardWidth - titleWidth) / 2, boardHeight / 3);
		try {
			InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
			Font arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(35f);
			g.setFont(arcadeFont);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Initialize and configure the Start button
		if (startButton == null) {
			startButton = new JButton("Start");
			startButton.setBounds(boardWidth / 2 - 60, boardHeight / 2 + 40, 120, 40); // Set position of the button
			// Title (centered, larger font, gradient effect)
			try {
				InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
				Font arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
				startButton.setFont(arcadeFont);
			} catch (Exception e) {
				e.printStackTrace();
			}
			startButton.setBackground(Color.GRAY);
			startButton.setForeground(Color.BLACK);
			startButton.setFocusPainted(false);
			startButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Switch to the difficulty selection screen
					gameState = GameState.MAPSELECTION_STATE;
					startButton.setVisible(false);
				}
			});
			add(startButton);
		}
		startButton.setBounds(boardWidth / 2 - 60, boardHeight / 2 + 40, 120, 40);
		startButton.setVisible(true);
	}

	public void drawBackgroundMap() {
		try {
			backgroundImageIcon_map = new ImageIcon(getClass().getResource("/bg12.gif"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawMap(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawBackgroundMap();
		if (backgroundImageIcon_map != null) {
			// Draw the first instance of the GIF
			g.drawImage(backgroundImageIcon_map.getImage(), xPosition + 0, 0, boardWidth, boardHeight, this);
		} else {
			g.setColor(new Color(0, 100, 100)); // Fallback gray background
			g.fillRect(0, 0, boardWidth, boardHeight);
		}

		// Title (centered, larger font, gradient effect)
		try {
			InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
			Font arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(35f);
			g.setFont(arcadeFont);
		} catch (Exception e) {
			e.printStackTrace();
		}

		g.setColor(new Color(255, 223, 0)); // Yellow color
		String title = "Map Selection";
		int titleWidth = g.getFontMetrics().stringWidth(title);
		g.drawString(title, (boardWidth - titleWidth) / 2, boardHeight / 3);

		// Instructions (subtle shadow effect for better readability)
		g.setFont(new Font("Arial", Font.PLAIN, 30));

		// Create and add buttons dynamically
		String[] mapNames = { "Easy Map", "Medium Map", "Hard Map" };
		GameState[] mapStates = { GameState.EASY_MAP, GameState.MEDIUM_MAP, GameState.HARD_MAP };
		int buttonYPosition = boardHeight / 2 + 0; // Initial Y position for buttons

		if (easyMapButton == null) {
			easyMapButton = createMapButton(mapNames[0], mapStates[0], buttonYPosition);
			add(easyMapButton);
		}
		buttonYPosition += 60;

		if (mediumMapButton == null) {
			mediumMapButton = createMapButton(mapNames[1], mapStates[1], buttonYPosition);
			add(mediumMapButton);
		}
		buttonYPosition += 60;

		if (hardMapButton == null) {
			hardMapButton = createMapButton(mapNames[2], mapStates[2], buttonYPosition);
			add(hardMapButton);
		}

		easyMapButton.setBounds(boardWidth / 2 - 100, boardHeight / 2, 200, 40);
		mediumMapButton.setBounds(boardWidth / 2 - 100, (boardHeight / 2) + 60, 200, 40);
		hardMapButton.setBounds(boardWidth / 2 - 100, (boardHeight / 2) + 120, 200, 40);
		easyMapButton.setVisible(true);
		mediumMapButton.setVisible(true);
		hardMapButton.setVisible(true);
	}

	// Helper method to create a map button
	private JButton createMapButton(String label, GameState state, int yPosition) {
		JButton button = new JButton(label);
		button.setBounds(boardWidth / 2 - 100, yPosition, 200, 40);
		try {
			InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
			Font arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
			button.setFont(arcadeFont);
		} catch (Exception e) {
			e.printStackTrace();
		}
		button.setBackground(Color.GRAY);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (label) {
				case "Easy Map":
					tileMap = Map.tileMap1;
					break;
				case "Medium Map":
					tileMap = Map.tileMap2;
					break;
				case "Hard Map":
					tileMap = Map.tileMap3;
					break;
				}
				rowCount = tileMap.length;
				loadMap();
				gameState = GameState.GAME_STATE;
				hideMapButtons();
				repaint();
			}
		});
		return button;
	}

	// Method to hide all map buttons after selection
	private void hideMapButtons() {
		if (easyMapButton != null)
			easyMapButton.setVisible(false);
		if (mediumMapButton != null)
			mediumMapButton.setVisible(false);
		if (hardMapButton != null)
			hardMapButton.setVisible(false);
	}

	public void loadMap() {
		walls = new HashSet<>();
		foods = new HashSet<>();
		ghosts = new HashSet<>();
		ArrayList<Block> emptySpaces = new ArrayList<>();

		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < columnCount; c++) {
				char tileMapChar = tileMap[r].charAt(c);

				int x = c * tileSize;
				int y = r * tileSize;
				Block block;

				switch (tileMapChar) {
				case 'X': // Wall
					block = new Block(wallImage, x, y, tileSize, tileSize);
					walls.add(block);
					break;
				case 'b': // Blue ghost
					block = new Block(blueGhostImage, x, y, tileSize, tileSize);
					ghosts.add(block);
					break;
				case 'o': // Orange ghost
					block = new Block(orangeGhostImage, x, y, tileSize, tileSize);
					ghosts.add(block);
					break;
				case 'p': // Pink ghost
					block = new Block(pinkGhostImage, x, y, tileSize, tileSize);
					ghosts.add(block);
					break;
				case 'r': // Red ghost
					block = new Block(redGhostImage, x, y, tileSize, tileSize);
					ghosts.add(block);
					break;
				case 'P': // Pac-Man
					pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
					break;
				case ' ': // Food
					block = new Block(null, x + 14, y + 14, 4, 4);
					foods.add(block);
					emptySpaces.add(block); // Store empty spaces for power pellet placement

					break;
				default:
					// O หรืออื่น ๆ ข้าม
					break;
				}
			}
		}

		for (Block ghost : ghosts) {
			char newDirection = directions[random.nextInt(4)];
			ghost.updateDirection(newDirection);
		}
		// Randomly place 5 Power Pellets
		Collections.shuffle(emptySpaces);
		for (int i = 0; i < Math.min(5, emptySpaces.size()); i++) {
			Block food = emptySpaces.get(i);
			Block powerPellet = new Block(powerPelletImage, food.getX() - 2, food.getY() - 2, 8, 8);
			powerPellet.setPowerPellet(true);

			foods.remove(food);
			foods.add(powerPellet);
		}

		// After creating `emptySpaces`
		Collections.shuffle(emptySpaces);

		// Place 5 special fruits
		for (int i = 0; i < Math.min(10, emptySpaces.size()); i++) {
			Block food = emptySpaces.get(i);
			Block fruit = new Block(fruitImages.get(i), food.getX() - 2, food.getY() - 2, tileSize, tileSize);
			fruit.setFruit(true);

			foods.remove(food);
			foods.add(fruit);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (gameState == GameState.MENU_STATE) {
			playBackgroundMusic("start_sound2.wav",0.3f);
			drawMenu(g);
		} else if (gameState == GameState.MAPSELECTION_STATE) {
			playBackgroundMusic("menu_sound.wav",0.3f);
			drawMap(g);
		} else if (gameState == GameState.GAME_STATE) {
			playBackgroundMusic("start_sound.wav",0.3f);
			draw(g);
		}
	}

	public void draw(Graphics g) {
		// วาด Pac-Man
		g.drawImage(pacman.getImage(), pacman.getX(), pacman.getY(), pacman.getWidth(), pacman.getHeight(), null);

		// วาด Ghosts
		for (Block ghost : ghosts) {
			g.drawImage(ghost.getImage(), ghost.getX(), ghost.getY(), ghost.getWidth(), ghost.getHeight(), null);
		}

		// วาด Walls
		for (Block wall : walls) {
			g.drawImage(wall.getImage(), wall.getX(), wall.getY(), wall.getWidth(), wall.getHeight(), null);
		}

		// วาด Food
		for (Block food : foods) {
			if (food.isPowerPellet()) {
				int powerPelletSize = (int) (tileSize * 1); // 60% of tile size
				int centerX = (food.getX() / tileSize) * tileSize + (tileSize - powerPelletSize) / 2;
				int centerY = (food.getY() / tileSize) * tileSize + (tileSize - powerPelletSize) / 2;

				g.drawImage(powerPelletImage, centerX, centerY, powerPelletSize, powerPelletSize, null);

			} else if (food.isFruit()) {
				int powerPelletSize = (int) (tileSize * 1); // 60% of tile size
				int centerX = (food.getX() / tileSize) * tileSize + (tileSize - powerPelletSize) / 2;
				int centerY = (food.getY() / tileSize) * tileSize + (tileSize - powerPelletSize) / 2;
				g.drawImage(food.getImage(), centerX, centerY, powerPelletSize, powerPelletSize, this);

			} else {
				g.setColor(Color.WHITE);
				g.fillRect(food.getX(), food.getY(), food.getWidth(), food.getHeight());
			}
		}

		try {
			InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
			Font arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
			g.setFont(arcadeFont);
		} catch (Exception e) {
			e.printStackTrace();
		}

		g.setColor(Color.YELLOW);
		if (gameOver) {
			g.drawString("Game Over: " + score, tileSize / 2, tileSize / 2);
		} else {
			// Draw lives as heart images
			int heartSize = tileSize / 2;
			int heartX = tileSize / 2;
			int heartY = tileSize / 2;

			for (int i = 0; i < lives; i++) {
				g.drawImage(heartImage, heartX + (i * (heartSize + 5)) + 5, heartY + 10, heartSize, heartSize, this);
			}
			g.drawString(" Score: " + score, tileSize / 2 - 7, tileSize / 2);
		}
		if (powerMode) {
			try {
				InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
				Font arcadeFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
				g.setFont(arcadeFont);
			} catch (Exception e) {
				e.printStackTrace();
			}
			g.setColor(Color.RED);
			g.drawString("Power: " + powerModeTimeLeft + "s", (tileSize * 5) + 30, tileSize / 2);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (gameState == GameState.GAME_STATE) {
			move(); // Update game state
			repaint(); // Redraw the game
			if (gameOver) {
				gameLoop.stop();
			}
		}
	}

	public void move() {
		if (gameState == GameState.GAME_STATE) {
			// ขยับ Pac-Man
			pacman.setX(pacman.getX() + pacman.getVelocityX());
			pacman.setY(pacman.getY() + pacman.getVelocityY());

			// Implement screen wrapping
			if (pacman.getX() < 0) {
				pacman.setX(boardWidth - pacman.getWidth()); // Wrap to right edge
			} else if (pacman.getX() + pacman.getWidth() > boardWidth) {
				pacman.setX(0); // Wrap to left edge
			}
			// check wall collisions
			for (Block wall : walls) {
				if (collision(pacman, wall)) {
					pacman.setX(pacman.getX() - pacman.getVelocityX());
					pacman.setY(pacman.getY() - pacman.getVelocityY());
					break;
				}
			}

			// check ghost collisions
			for (Block ghost : ghosts) {
				if (collision(ghost, pacman)) {
					if (powerMode) {
						// Ghost is eaten, reset its position and increase score
						score += 100;
						playSound("eat_sound.wav",-20.0f);
						ghost.reset();
					} else {
						lives -= 1;
						if (lives == 0) {
							gameOver = true;
							return;
						}
						resetPositions();
					}
				}

				// logic เดิม: ถ้า ghost อยู่แถว 9 และ direction != U,D -> บังคับให้เป็น 'U'
				if (ghost.getY() == tileSize * 9 && ghost.getDirection() != 'U' && ghost.getDirection() != 'D') {
					ghost.updateDirection('U');
				}

				// ขยับ Ghost
				ghost.setX(ghost.getX() + ghost.getVelocityX());
				ghost.setY(ghost.getY() + ghost.getVelocityY());

				// ชนกำแพงหรือออกขอบ -> revert และสุ่มทิศทาง
				for (Block wall : walls) {
					if (collision(ghost, wall) || ghost.getX() <= 0 || ghost.getX() + ghost.getWidth() >= boardWidth) {
						ghost.setX(ghost.getX() - ghost.getVelocityX());
						ghost.setY(ghost.getY() - ghost.getVelocityY());
						char newDirection = directions[random.nextInt(4)];
						ghost.updateDirection(newDirection);
						break;
					}
				}
			}

			// Check food collision
			Block foodEaten = null;
			for (Block food : foods) {
				if (collision(pacman, food)) {
					foodEaten = food;
					if (food.isPowerPellet()) {
						activatePowerMode();
						playSound("eat_sound.wav",-20.0f);
					} else if (foodEaten.isFruit()) {
						playSound("eat_sound.wav",-20.0f);
						score += 50;
					} else {
						playSound("eat_sound.wav",-20.0f);
						score += 10; // Normal food increases score
					}
				}
			}
			foods.remove(foodEaten);

			// ถ้าไม่มี Food เหลือ -> โหลดแมพใหม่ + reset
			if (foods.isEmpty()) {
				loadMap();
				resetPositions();
			}
		}

	}

	private void activatePowerMode() {
		powerMode = true;
		powerModeTimeLeft = 7; // Set power mode duration in seconds

		if (powerModeTimer != null) {
			powerModeTimer.stop();
		}

		powerModeTimer = new Timer(1000, e -> { // Update every second
			powerModeTimeLeft--;
			if (powerModeTimeLeft <= 0) {
				powerMode = false;
				powerModeTimer.stop();
			}
			repaint(); // Refresh screen
		});
		powerModeTimer.start();
	}

	public static boolean collision(GameObject a, GameObject b) {
		return a.getX() < b.getX() + b.getWidth() && a.getX() + a.getWidth() > b.getX()
				&& a.getY() < b.getY() + b.getHeight() && a.getY() + a.getHeight() > b.getY();
	}

	public void resetPositions() {
		pacman.reset();
		pacman.setVelocityX(0);
		pacman.setVelocityY(0);

		for (Block ghost : ghosts) {
			ghost.reset();
			char newDirection = directions[random.nextInt(4)];
			ghost.updateDirection(newDirection);
		}
	}

	// --------------------- KeyListener ---------------------

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (gameState == GameState.MENU_STATE) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				// Start the game when Enter is pressed
				gameState = GameState.MAPSELECTION_STATE;
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// Exit the game when ESC is pressed
				System.exit(0);
			}
		} else if (gameState == GameState.MAPSELECTION_STATE) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				// Exit the game when ESC is pressed
				System.exit(0);
			}
		} else if (gameState == GameState.GAME_STATE) {
			if (gameOver) {
				if (e.getKeyCode() != KeyEvent.VK_ESCAPE) {
					// When any key is pressed, return to the menu
					gameState = GameState.MENU_STATE;
					lives = 3; // Reset lives
					score = 0; // Reset score
					gameOver = false; // Reset game over flag
					loadMap(); // Reload the map
					repaint(); // Repaint the menu screen
					gameLoop.start();
				}
			} else {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					pacman.updateDirection('U');
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					pacman.updateDirection('D');
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					pacman.updateDirection('L');
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					pacman.updateDirection('R');
				}

				if (pacman.getDirection() == 'U') {
					pacman.setImage(pacmanUpImage);
				} else if (pacman.getDirection() == 'D') {
					pacman.setImage(pacmanDownImage);
				} else if (pacman.getDirection() == 'L') {
					pacman.setImage(pacmanLeftImage);
				} else if (pacman.getDirection() == 'R') {
					pacman.setImage(pacmanRightImage);
				}

				if (powerMode) {
					if (pacman.getDirection() == 'U') {
						pacman.setImage(pacmanPowerUpImage);
					} else if (pacman.getDirection() == 'D') {
						pacman.setImage(pacmanPowerDownImage);
					} else if (pacman.getDirection() == 'L') {
						pacman.setImage(pacmanPowerLeftImage);
					} else if (pacman.getDirection() == 'R') {
						pacman.setImage(pacmanPowerRightImage);
					}
				} else {
					if (pacman.getDirection() == 'U') {
						pacman.setImage(pacmanUpImage);
					} else if (pacman.getDirection() == 'D') {
						pacman.setImage(pacmanDownImage);
					} else if (pacman.getDirection() == 'L') {
						pacman.setImage(pacmanLeftImage);
					} else if (pacman.getDirection() == 'R') {
						pacman.setImage(pacmanRightImage);
					}
				}
			}

			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (gameOver) {
					gameLoop.start();
				}
				gameState = GameState.MENU_STATE;
				lives = 3; // Reset lives
				score = 0; // Reset score
				gameOver = false; // Reset game over flag
				loadMap(); // Reload the map
				repaint(); // Repaint the menu screen
			}
		}
	}
}
