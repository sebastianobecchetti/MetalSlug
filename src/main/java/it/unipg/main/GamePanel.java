package it.unipg.main;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Graphics;
import java.awt.Graphics2D;
import it.unipg.entity.*;
import it.unipg.texture.*;

public class GamePanel extends JPanel implements Runnable {
	// SCREEN SETTING
	// all of the value below can be changed to fit our projects
	public final int originalTileSize = 32; // 32x32 pixel is the dimension of a tile
	public final int tileScale = 3;// 3x the original tile size

	public final int tileSize = originalTileSize * tileScale;
	public final int maxScreenRow = 10; // MODIFICA L'ALTEZZA DELLO SCHERMO
	public final int maxScreenCol = 12; // MODIFICA LA LARGHEZZA DELLO SCHERMO
	public final int screenWidth = tileSize * maxScreenCol;
	public final int screenHeight = tileSize * maxScreenRow;

	// MAP SETTING
	public final int maxMapCol = 50;
	public final int maxMapRow = 50;
	public final int mapWidth = tileSize * maxMapCol;
	public final int mapHeight = tileSize * maxScreenRow;

	// FPS COUNTER
	int FPS = 60;

	TextureManager tm = new TextureManager(this);
	KeyHandler kh = new KeyHandler();
	// THREAD
	Thread gameThread;
	public CollisionChecker collisionChecker = new CollisionChecker(this);
	public Player player = new Player(this, kh);

	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.white);

		this.setDoubleBuffered(true); // It's a technique where drawings are not made directly on the screen, but
																	// first on a buffer area (off-screen memory). When the drawing is ready, the
																	// image is copied all at once to the screen.
		this.addKeyListener(kh);
		this.setFocusable(true);

	}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1_000_000_000 / FPS; // 0.01667 seconds
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0;
		int drawCount = 0;

		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval; // delta is the time passed since the last frame
			timer += currentTime - lastTime;
			lastTime = currentTime;
			if (delta >= 1) {
				update();
				repaint();
				delta--;
				drawCount++;
			}
			if (timer >= 1_000_000_000) { // every second
				System.out.println("FPS: " + drawCount);
				drawCount = 0;
				timer = 0;
			}
		}
	}

	public void update() {
		player.update();
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// draw player
		g2d.setColor(Color.black);
		g2d.drawString("FPS: " + FPS, 10, 20); // FPS counter
		tm.draw(g2d);
		player.draw(g2d);
		g2d.dispose(); // very similar to the action of get the heap free
	}
}
