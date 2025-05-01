package it.unipg.entity;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class SpriteLoader {
	private BufferedImage playerPistolSpriteSheet, legsSpriteSheet, reloadPistolSheet, legsJumpingSpriteSheet,
			jumpingPlayerPistolSheet;
	private BufferedImage[] walkingPlayerPistol, walkingLegs, reloadPistol, jumpingLegs, jumpingPlayerPistol;
	private static final int WALK_FRAMES = 12;
	private static final int RELOAD_FRAMES = 19;
	private static final int JUMP_FRAMES = 6;

	public SpriteLoader() {
		loadPlayerImages();
	}

	private void loadPlayerImages() {
		try {
			playerPistolSpriteSheet = ImageIO.read(getClass().getResourceAsStream("/res/player_pistol/walking.png"));
			walkingPlayerPistol = extractFrames(playerPistolSpriteSheet, WALK_FRAMES);

			legsSpriteSheet = ImageIO.read(getClass().getResourceAsStream("/res/legs/walking.png"));
			walkingLegs = extractFrames(legsSpriteSheet, WALK_FRAMES);

			reloadPistolSheet = ImageIO.read(getClass().getResourceAsStream("/res/player_pistol/reloading.png"));
			reloadPistol = extractFrames(reloadPistolSheet, RELOAD_FRAMES);

			legsJumpingSpriteSheet = ImageIO.read(getClass().getResource("/res/legs/jumping.png"));
			jumpingLegs = extractFrames(legsJumpingSpriteSheet, JUMP_FRAMES);

			jumpingPlayerPistolSheet = ImageIO.read(getClass().getResource("/res/player_pistol/jumping.png"));
			jumpingPlayerPistol = extractFrames(jumpingPlayerPistolSheet, JUMP_FRAMES);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedImage[] extractFrames(BufferedImage sheet, int numFrames) {
		BufferedImage[] frames = new BufferedImage[numFrames];
		int frameWidth = sheet.getWidth() / numFrames;
		int frameHeight = sheet.getHeight();
		for (int i = 0; i < numFrames; i++) {
			frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
		}
		return frames;
	}

	public BufferedImage[] getWalkingPlayerPistol() {
		return walkingPlayerPistol;
	}

	public BufferedImage[] getWalkingLegs() {
		return walkingLegs;
	}

	public BufferedImage[] getReloadPistol() {
		return reloadPistol;
	}

	public BufferedImage[] getJumpingLegs() {
		return jumpingLegs;
	}

	public BufferedImage[] getJumpingPlayerPistol() {
		return jumpingPlayerPistol;
	}
}
