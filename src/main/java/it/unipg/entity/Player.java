package it.unipg.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import it.unipg.main.GamePanel;
import it.unipg.main.KeyHandler;

public class Player extends Entity {

	GamePanel gp;
	KeyHandler kh;
	public final int screenX;
	public final int screenY;

	private boolean isJumping = false;
	private boolean isShooting = false;

	private int velocityY = 0;
	private static final int GRAVITY = 1;
	private static final int JUMP_STRENGTH = -15;
	private static final int GROUND_LEVEL = 32 * 3 * 7 + 5;
	private static final int SPRITE_UPDATE_SPEED = 5;
	private static int ammo = 10;

	private BufferedImage[] walkingPlayerPistol, walkingLegs, reloadPistol, jumpingLegs, jumpingPlayerPistol,
			crouchPlayerPistol, runningPlayerPistol, runningLegs, shootingRightPistol, shootingUpPistol;

	private Animation walkingBodyAnim, walkingLegsAnim, jumpingLegsAnim, reloadingAnim,
			jumpingPlayerPistolAnim, crouchPlayerPistolAnim, runningPlayerPistolAnim,
			runningLegsAnim, shootingRightPistolAnim, shootingUpPistolAnim;

	private BodyState bodyState;
	private LegState legState; // Nuovo stato delle gambe

	public Player(GamePanel gp, KeyHandler kh, SpriteLoader loader) {
		this.gp = gp;
		this.kh = kh;
		screenX = gp.screenWidth / 2;
		screenY = GROUND_LEVEL;

		walkingPlayerPistol = loader.getWalkingPlayerPistol();
		walkingLegs = loader.getWalkingLegs();
		reloadPistol = loader.getReloadPistol();
		jumpingLegs = loader.getJumpingLegs();
		jumpingPlayerPistol = loader.getJumpingPlayerPistol();
		crouchPlayerPistol = loader.getCrouchPlayerPistol();
		runningPlayerPistol = loader.getRunningPlayerPistol();
		runningLegs = loader.getRunningLegs();
		shootingRightPistol = loader.getShootingRightPistol();
		shootingUpPistol = loader.getShootingUpPistol();

		setDefaultValues();

		walkingBodyAnim = new Animation(SPRITE_UPDATE_SPEED);
		walkingLegsAnim = new Animation(SPRITE_UPDATE_SPEED);
		jumpingLegsAnim = new Animation(SPRITE_UPDATE_SPEED);
		reloadingAnim = new Animation(SPRITE_UPDATE_SPEED);
		jumpingPlayerPistolAnim = new Animation(SPRITE_UPDATE_SPEED);
		crouchPlayerPistolAnim = new Animation(SPRITE_UPDATE_SPEED);
		runningPlayerPistolAnim = new Animation(SPRITE_UPDATE_SPEED);
		runningLegsAnim = new Animation(SPRITE_UPDATE_SPEED);
		shootingRightPistolAnim = new Animation(SPRITE_UPDATE_SPEED);
		shootingUpPistolAnim = new Animation(SPRITE_UPDATE_SPEED);

		solidArea = new Rectangle(10, 20, 20, 10);
	}

	private void setDefaultValues() {
		mapX = gp.tileSize * 10;
		mapY = GROUND_LEVEL;
		speed = 10;
		direction = Direction.RIGHT;
		bodyState = BodyState.STANDING;
		legState = LegState.STANDING;

	}

	private void updateFacingDirection() {
		if (kh.rightPressed)
			direction = Direction.RIGHT;
		if (kh.leftPressed)
			direction = Direction.LEFT;
	}

	public void update() {
		kh.update();

		updateFacingDirection();

		if (ammo == 0 && !kh.isReloading) {
			kh.isReloading = true;
			System.out.println("Ricaricando...");
		}

		if (kh.isReloading) {
			handleReload();
			return;
		}

		if (kh.spacePressed && !isJumping)
			handleJumpStart();
		handleCrouch();
		handleWalk();
		handleDirectionalAim();
		handleShooting();
		handleStanding();

		if (isJumping)
			handleJump();

		collisionOn = false;
		gp.collisionChecker.checkCollision(this);
		if (!collisionOn && bodyState != BodyState.CROUCHING) {
			handleHorizontalMovement();
		}

		walkingBodyAnim.update(walkingPlayerPistol.length);
		walkingLegsAnim.update(walkingLegs.length);
		runningPlayerPistolAnim.update(runningPlayerPistol.length);
		runningLegsAnim.update(runningLegs.length);
	}

	private void handleReload() {
		bodyState = BodyState.RELOADING;
		reloadingAnim.update(reloadPistol.length);
		if (reloadingAnim.getFrame() == reloadPistol.length - 1) {
			ammo = 10;
			kh.isReloading = false;
			reloadingAnim.reset();
			bodyState = BodyState.STANDING;
			System.out.println("Ricarica completata.");
		}
	}

	private void handleJumpStart() {
		isJumping = true;
		velocityY = JUMP_STRENGTH;
		legState = LegState.JUMPING;
		bodyState = BodyState.JUMPING;
	}

	private void handleJump() {
		mapY += velocityY;
		velocityY += GRAVITY;
		if (velocityY > 0)
			legState = LegState.JUMPING;
		if (mapY >= GROUND_LEVEL) {
			mapY = GROUND_LEVEL;
			isJumping = false;
			velocityY = 0;
		}
		jumpingLegsAnim.update(jumpingLegs.length);
		jumpingPlayerPistolAnim.update(jumpingPlayerPistol.length);
	}

	private void handleCrouch() {
		if (kh.shiftPressed && !isJumping) {
			bodyState = BodyState.CROUCHING;
			legState = LegState.CROUCHING;
		}
	}

	private void handleWalk() {
		if ((kh.rightPressed || kh.leftPressed) && !isJumping) {
			if (kh.controlPressed) {
				bodyState = BodyState.RUNNING;
				legState = LegState.RUNNING; // Cambia stato delle gambe a RUNNING
				speed = 16;
			} else {
				bodyState = BodyState.WALKING;
				legState = LegState.WALKING; // Cambia stato delle gambe a WALKING
				speed = 10;
			}
			if (kh.rightPressed) {
				direction = Direction.RIGHT;
			}
			if (kh.leftPressed) {
				direction = Direction.LEFT;
			}
		}
	}

	private void handleDirectionalAim() {
		if (kh.downPressed && !isJumping)
			direction = Direction.DOWN;
		if (kh.upPressed && !isJumping)
			direction = Direction.UP;
	}

	private void handleStanding() {
		if (!kh.rightPressed && !kh.leftPressed && !kh.isReloading && !kh.spacePressed &&
				!kh.shiftPressed && !kh.controlPressed && !isShooting) {
			bodyState = BodyState.STANDING;
			legState = LegState.STANDING; // Imposta le gambe su STANDING
			speed = 10;
		}
	}

	private void handleHorizontalMovement() {
		if (direction != Direction.DOWN) {
			if (kh.rightPressed)
				mapX += speed;
			if (kh.leftPressed)
				mapX -= speed;
		}
	}

	private void handleShooting() {
		if (kh.firePressed && ammo > 0 && !kh.isReloading && !isShooting) {
			isShooting = true;
			bodyState = BodyState.SHOOTING;

			if (direction == Direction.UP) {
				shootingUpPistolAnim.reset();
			} else {
				shootingRightPistolAnim.reset();
			}
		}

		if (isShooting) {
			if (direction == Direction.RIGHT || direction == Direction.LEFT) {
				shootingRightPistolAnim.update(shootingRightPistol.length);
				if (shootingRightPistolAnim.getFrame() == shootingRightPistol.length - 1) {
					ammo--;
					System.out.println("AMMO NUMBER: " + ammo);
					isShooting = false;
					kh.firePressed = false;
				}
			} else if (direction == Direction.UP) {
				shootingUpPistolAnim.update(shootingUpPistol.length);
				if (shootingUpPistolAnim.getFrame() == shootingUpPistol.length - 1) {
					ammo--;
					System.out.println("AMMO NUMBER: " + ammo);
					isShooting = false;
					kh.firePressed = false;
				}
			}
		}
	}

	public void draw(Graphics2D g2d) {
		BufferedImage body = null, legs = null;
		int bodyOffsetY = gp.tileSize * 4 / gp.tileScale;
		int legsOffsetY = gp.tileSize * 2 / gp.tileScale;
		float bodyScale = 1.0f;
		float legsScale = 1.0f;

		switch (bodyState) {
			case RELOADING -> {
				body = reloadPistol[reloadingAnim.getFrame()];
				bodyScale = 1.5f;
			}
			case WALKING -> {
				body = walkingPlayerPistol[walkingBodyAnim.getFrame()];
			}
			case RUNNING -> {
				bodyOffsetY = gp.tileSize * 5 / gp.tileScale;
				body = runningPlayerPistol[runningPlayerPistolAnim.getFrame()];
				bodyScale = 1.5f;
			}
			case CROUCHING -> {
				legsOffsetY = 144;
				legs = crouchPlayerPistol[crouchPlayerPistolAnim.getFrame()];
				bodyScale = 1.5f;
				legsScale = 1.5f;
			}
			case JUMPING -> {
				body = jumpingPlayerPistol[jumpingPlayerPistolAnim.getFrame()];
				bodyScale = 2.0f;
			}
			case SHOOTING -> {
				if (direction == Direction.RIGHT || direction == Direction.LEFT) {
					body = shootingRightPistol[shootingRightPistolAnim.getFrame()];
					bodyScale = 2.0f;
				} else if (direction == Direction.UP) {
					body = shootingUpPistol[shootingUpPistolAnim.getFrame()];
					bodyScale = 2.0f;
					bodyOffsetY = 250;
				}

			}
			case STANDING -> {
				body = walkingPlayerPistol[5];
			}
		}

		switch (legState) {
			case STANDING -> {
				legs = walkingLegs[5];
			}
			case WALKING -> {
				legs = walkingLegs[walkingLegsAnim.getFrame()];
			}
			case RUNNING -> {
				legs = runningLegs[runningLegsAnim.getFrame()];
			}
			case JUMPING -> {
				legs = jumpingLegs[jumpingLegsAnim.getFrame()];
				legsScale = 2.0f;
			}
			case CROUCHING -> {
				legsOffsetY = 144;
				legs = crouchPlayerPistol[crouchPlayerPistolAnim.getFrame()];
				bodyScale = 1.5f;
				legsScale = 1.5f;
			}
		}

		int x = screenX;
		int bodyScaledTileSize = (int) (gp.tileSize * bodyScale);
		int legsScaledTileSize = (int) (gp.tileSize * legsScale);

		if (direction == Direction.RIGHT || direction == Direction.UP || direction == Direction.DOWN) {
			g2d.drawImage(legs, x, screenY - legsOffsetY, legsScaledTileSize, legsScaledTileSize, null);
			g2d.drawImage(body, x, screenY - bodyOffsetY, bodyScaledTileSize, bodyScaledTileSize, null);
		} else if (direction == Direction.LEFT) {
			g2d.drawImage(legs, x + gp.tileSize, screenY - legsOffsetY, -legsScaledTileSize, legsScaledTileSize, null);
			g2d.drawImage(body, x + gp.tileSize, screenY - bodyOffsetY, -bodyScaledTileSize, bodyScaledTileSize, null);
		}
	}

	private enum BodyState {
		RELOADING, SHOOTING, JUMPING, WALKING, STANDING, CROUCHING, RUNNING
	}

	private enum LegState { // Nuovo enum per lo stato delle gambe
		JUMPING, WALKING, STANDING, RUNNING, CROUCHING
	}
}
