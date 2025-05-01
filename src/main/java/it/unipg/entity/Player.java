package it.unipg.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import it.unipg.main.GamePanel;
import it.unipg.main.KeyHandler;

public class Player extends Entity {

	GamePanel gp;
	KeyHandler kh;
	public final int screenX;
	public final int screenY;

	private boolean isJumping = false;
	private int velocityY = 0;
	private static final int GRAVITY = 1;
	private static final int JUMP_STRENGTH = -15;
	private static final int GROUND_LEVEL = 32 * 3 * 7;

	private static final int SPRITE_UPDATE_SPEED = 5;

	private BufferedImage playerPistolSpriteSheet, legsSpriteSheet, reloadPistolSheet, legsJumpingSriteSheet;
	private BufferedImage[] walkingPlayerPistol, walkingLegs, reloadPistol, jumpingLegs;

	private Animation walkingBodyAnim;
	private Animation walkingLegsAnim;
	private Animation jumpingLegsAnim;
	private Animation reloadingAnim;
	private BodyState bodyState;

	public Player(GamePanel gp, KeyHandler kh) {
		this.gp = gp;
		this.kh = kh;
		screenX = gp.screenWidth / 2;
		screenY = GROUND_LEVEL;
		setDefaultValues();
		loadPlayerImages();
		walkingBodyAnim = new Animation(SPRITE_UPDATE_SPEED);
		walkingLegsAnim = new Animation(SPRITE_UPDATE_SPEED);
		jumpingLegsAnim = new Animation(SPRITE_UPDATE_SPEED);
		reloadingAnim = new Animation(SPRITE_UPDATE_SPEED);
		solidArea = new Rectangle(4, 4, 50, 50);
	}

	private void setDefaultValues() {
		mapX = gp.tileSize * 10;
		mapY = GROUND_LEVEL;
		speed = 10;
		direction = Direction.RIGHT;
		bodyState = BodyState.WALKING;
		facingDirection = FacingDirection.RIGHT;
	}

	private void loadPlayerImages() {
		try {
			int NUM_FRAMES = 12;
			playerPistolSpriteSheet = ImageIO.read(getClass().getResourceAsStream("/res/player_pistol/walking.png"));
			walkingPlayerPistol = new BufferedImage[NUM_FRAMES];
			for (int i = 0; i < NUM_FRAMES; i++) {
				int frameWidth = playerPistolSpriteSheet.getWidth() / NUM_FRAMES;
				int frameHeight = playerPistolSpriteSheet.getHeight();
				walkingPlayerPistol[i] = playerPistolSpriteSheet.getSubimage(frameWidth * i, 0, frameWidth, frameHeight);
			}

			legsSpriteSheet = ImageIO.read(getClass().getResourceAsStream("/res/legs/walking.png"));
			walkingLegs = new BufferedImage[NUM_FRAMES];
			for (int i = 0; i < NUM_FRAMES; i++) {
				int frameWidth = legsSpriteSheet.getWidth() / NUM_FRAMES;
				int frameHeight = legsSpriteSheet.getHeight();
				walkingLegs[i] = legsSpriteSheet.getSubimage(frameWidth * i, 0, frameWidth, frameHeight);
			}

			NUM_FRAMES = 19;
			reloadPistolSheet = ImageIO.read(getClass().getResourceAsStream("/res/player_pistol/reloading.png"));
			reloadPistol = new BufferedImage[NUM_FRAMES];
			for (int i = 0; i < NUM_FRAMES; i++) {
				int frameWidth = reloadPistolSheet.getWidth() / NUM_FRAMES;
				int frameHeight = reloadPistolSheet.getHeight();
				reloadPistol[i] = reloadPistolSheet.getSubimage(frameWidth * i, 0, frameWidth, frameHeight);
			}
			NUM_FRAMES = 6;
			legsJumpingSriteSheet = ImageIO.read(getClass().getResource("/res/legs/jumping.png"));
			jumpingLegs = new BufferedImage[NUM_FRAMES];
			for (int i = 0; i < NUM_FRAMES; i++) {
				int frameWidth = legsJumpingSriteSheet.getWidth() / NUM_FRAMES;
				int frameHeight = legsJumpingSriteSheet.getHeight();
				jumpingLegs[i] = legsJumpingSriteSheet.getSubimage(frameWidth * i, 0, frameWidth, frameHeight);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		kh.update();

		if (kh.isReloading) {
			handleReload();
		} else {
			bodyState = BodyState.WALKING;

			if (kh.spacePressed && !isJumping) {
				handleJumpStart();
				direction = Direction.JUMPING;
			}
			handleWalk();
			handleDirectionalAim();
			handleStanding();
		}

		if (isJumping)
			handleJump();

		collisionOn = false;
		gp.collisionChecker.checkCollision(this);
		if (!collisionOn)
			handleHorizontalMovement();

		// Animazioni aggiornate sempre
		walkingBodyAnim.update(walkingPlayerPistol.length);
		walkingLegsAnim.update(walkingLegs.length);
	}

	private void handleReload() {
		bodyState = BodyState.RELOADING;
		reloadingAnim.update(reloadPistol.length);
		if (reloadingAnim.getFrame() == reloadPistol.length - 1) {
			kh.isReloading = false;
			reloadingAnim.reset();
			bodyState = BodyState.WALKING;
		}
	}

	private void handleJumpStart() {
		isJumping = true;
		velocityY = JUMP_STRENGTH;
		direction = Direction.JUMPING;
		bodyState = BodyState.JUMPING;
		jumpingLegsAnim.update(jumpingLegs.length);
	}

	private void handleJump() {
		mapY += velocityY;
		velocityY += GRAVITY;
		if (velocityY > 0)
			direction = Direction.LANDING;
		if (mapY >= GROUND_LEVEL) {
			mapY = GROUND_LEVEL;
			isJumping = false;
			velocityY = 0;
		}
		jumpingLegsAnim.update(jumpingLegs.length);
	}

	private void handleWalk() {
		if (kh.rightPressed) {
			facingDirection = FacingDirection.RIGHT;
			if (!isJumping)
				direction = Direction.RIGHT;
		}
		if (kh.leftPressed) {
			facingDirection = FacingDirection.LEFT;
			if (!isJumping)
				direction = Direction.LEFT;
		}
	}

	private void handleDirectionalAim() {
		if (kh.downPressed && !isJumping)
			direction = Direction.DOWN;
		if (kh.upPressed && !isJumping)
			direction = Direction.UP;
	}

	private void handleStanding() {
		if (!kh.rightPressed && !kh.leftPressed && !kh.isReloading && !kh.spacePressed)
			direction = Direction.STANDING;
	}

	private void handleHorizontalMovement() {
		if (kh.rightPressed)
			mapX += speed;
		if (kh.leftPressed)
			mapX -= speed;
	}

	public void draw(Graphics2D g2d) {
		BufferedImage body = null, legs = null;
		int bodyOffsetY = gp.tileSize * 4 / gp.tileScale;
		int legsOffsetY = gp.tileSize * 2 / gp.tileScale;

		// Gambe camminano sempre quando in movimento orizzontale
		if (direction == Direction.STANDING)
			legs = walkingLegs[5]; // per adesso ho disegnato questa come standing
		else {
			legs = walkingLegs[walkingLegsAnim.getFrame()];
		}
		// Corpo in base allo stato
		switch (bodyState) {
			case RELOADING:
				body = reloadPistol[reloadingAnim.getFrame()];
				break;
			case WALKING:
			default:
				body = walkingPlayerPistol[walkingBodyAnim.getFrame()];
				break;
		}
		if (isJumping) {
			legs = jumpingLegs[jumpingLegsAnim.getFrame()];
		}

		if (body != null && legs != null) {
			if (facingDirection == FacingDirection.RIGHT) {
				g2d.drawImage(legs, screenX, screenY - legsOffsetY, gp.tileSize, gp.tileSize, null);
				g2d.drawImage(body, screenX, screenY - bodyOffsetY, gp.tileSize, gp.tileSize, null);
			} else {
				g2d.drawImage(legs, screenX + gp.tileSize, screenY - legsOffsetY, -gp.tileSize, gp.tileSize, null);
				g2d.drawImage(body, screenX + gp.tileSize, screenY - bodyOffsetY, -gp.tileSize, gp.tileSize, null);
			}
		}
		g2d.drawImage(legs, 0, 0, gp.tileSize, gp.tileSize, null);
	}

	private enum BodyState {
		RELOADING, SHOOTING, JUMPING, WALKING
	}
}
