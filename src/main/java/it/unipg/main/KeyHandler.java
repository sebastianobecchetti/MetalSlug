package it.unipg.main;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeyHandler implements KeyListener {
	public boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

	public boolean isReloading;
	private long reloadStartTime;
	private final long RELOAD_DURATION_MS = 2000; // 2 secondi di "hold simulato"

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_W)
			upPressed = true;
		if (code == KeyEvent.VK_S)
			downPressed = true;
		if (code == KeyEvent.VK_A)
			leftPressed = true;
		if (code == KeyEvent.VK_D)
			rightPressed = true;
		if (code == KeyEvent.VK_SPACE)
			spacePressed = true;

		if (code == KeyEvent.VK_R) {
			isReloading = true;
			reloadStartTime = System.currentTimeMillis();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_W)
			upPressed = false;
		if (code == KeyEvent.VK_S)
			downPressed = false;
		if (code == KeyEvent.VK_A)
			leftPressed = false;
		if (code == KeyEvent.VK_D)
			rightPressed = false;
		if (code == KeyEvent.VK_SPACE)
			spacePressed = false;
		// Non disattiviamo subito isReloading per R
	}

	public void update() {
		if (isReloading) {
			long elapsed = System.currentTimeMillis() - reloadStartTime;
			if (elapsed >= RELOAD_DURATION_MS) {
				isReloading = false;
			}
		}
	}
}
