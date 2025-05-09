package it.unipg.main;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import it.unipg.entity.*;
import it.unipg.texture.*;

public class GamePanel extends JPanel {

	// SCREEN SETTING
	public final int originalTileSize = 32;
	public final int tileScale = 3;
	public final int tileSize = originalTileSize * tileScale;
	public final int maxScreenRow = 10;
	public final int maxScreenCol = 12;
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;

	// MAP SETTING
	public final int maxMapCol = 50;
	public final int maxMapRow = 50;
	public final int mapWidth = tileSize * maxMapCol;
	public final int mapHeight = tileSize * maxScreenRow;

	// FPS
	private final int FPS = 60;
	// GAMESTATE
	private boolean paused = false;

	// COMPONENTS
	TextureManager tm = new TextureManager(this);
	KeyHandler kh = new KeyHandler(this);
	public CollisionChecker collisionChecker = new CollisionChecker(this);
	public SpriteLoader spriteLoader = new SpriteLoader();
	public Player player = new Player(this, kh, spriteLoader);

	// TIMER
	private Timer timer;

	public GamePanel() {
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setBackground(Color.white);
		setDoubleBuffered(true);
		addKeyListener(kh);
		setFocusable(true);
	}

	public void startGameLoop() {
		int delay = 1000 / FPS;
		timer = new Timer(delay, e -> {
			update();
			repaint();
		});
		timer.start();
	}

	public void update() {
		player.update();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.black);
		g2d.drawString("FPS: " + FPS, 10, 20); // statico

		if (!paused) {
			// TILE
			tm.draw(g2d);
			// PLAYER
			player.draw(g2d);
		}

		if (paused) { // provvisorio
			g2d.setColor(Color.RED);
			g2d.setFont(new Font("SansSerif", Font.BOLD, 48));
			g2d.drawString("PAUSED", screenWidth / 2 - 100, screenHeight / 2);
		}

		g2d.dispose();
	}

	public void pauseGame() {
		paused = true;
	}

	public void resumeGame() {
		paused = false;
	}

	public void togglePause() {
		paused = !paused;
	}

}
