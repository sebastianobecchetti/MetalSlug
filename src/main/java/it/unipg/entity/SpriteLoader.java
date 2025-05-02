package it.unipg.entity;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class SpriteLoader {
	private BufferedImage playerPistolSpriteSheet, legsSpriteSheet, reloadPistolSheet, legsJumpingSpriteSheet,
			jumpingPlayerPistolSheet, crouchPlayerPistolSheet, runningPlayerPistolSheet, runningLegsSheet;
	private BufferedImage[] walkingPlayerPistol, walkingLegs, reloadPistol, jumpingLegs, jumpingPlayerPistol,
			crouchPlayerPistol, runningPlayerPistol, runningLegs;
	private static final int WALK_FRAMES = 12;
	private static final int RELOAD_FRAMES = 19;
	private static final int JUMP_FRAMES = 6;
	private static final int CROUCH_FRAMES = 7;
	private static final int RUNNING_FRAMES = 6;
	private static final int RUNNING_LEGS_FRAME = 5;

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

			jumpingPlayerPistolSheet = ImageIO.read(getClass().getResourceAsStream("/res/player_pistol/jumping.png"));
			jumpingPlayerPistol = extractFrames(jumpingPlayerPistolSheet, JUMP_FRAMES);

			crouchPlayerPistolSheet = ImageIO.read(getClass().getResourceAsStream("/res/player_pistol/crouch.png"));
			crouchPlayerPistol = extractFrames(crouchPlayerPistolSheet, CROUCH_FRAMES);

			runningPlayerPistolSheet = ImageIO.read(getClass().getResourceAsStream("/res/player_pistol/running.png"));
			runningPlayerPistol = extractFrames(runningPlayerPistolSheet, RUNNING_FRAMES);

			runningLegsSheet = ImageIO.read(getClass().getResourceAsStream("/res/legs/running.png"));
			runningLegs = extractFrames(runningLegsSheet, RUNNING_LEGS_FRAME);
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

	public BufferedImage[] getCrouchPlayerPistol() {
		return crouchPlayerPistol;
	}

	public BufferedImage[] getRunningPlayerPistol() {
		return runningPlayerPistol;
	}

	public BufferedImage[] getRunningLegs() {
		return runningLegs;
	}
}
