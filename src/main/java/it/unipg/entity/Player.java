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
	private int velocityY = 0;
	private static final int GRAVITY = 1;
	private static final int JUMP_STRENGTH = -15;
	private static final int GROUND_LEVEL = 32 * 3 * 7;
	private static final int SPRITE_UPDATE_SPEED = 5;

	private BufferedImage[] walkingPlayerPistol, walkingLegs, reloadPistol, jumpingLegs, jumpingPlayerPistol,
			crouchPlayerPistol, runningPlayerPistol, runningLegs;

	private Animation walkingBodyAnim;
	private Animation walkingLegsAnim;
	private Animation jumpingLegsAnim;
	private Animation reloadingAnim;
	private Animation jumpingPlayerPistolAnim;
	private Animation crouchPlayerPistolAnim;
	private Animation runningPlayerPistolAnim;
	private Animation runningLegsAnim;

	private BodyState bodyState;

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

		setDefaultValues();

		walkingBodyAnim = new Animation(SPRITE_UPDATE_SPEED);
		walkingLegsAnim = new Animation(SPRITE_UPDATE_SPEED);
		jumpingLegsAnim = new Animation(SPRITE_UPDATE_SPEED);
		reloadingAnim = new Animation(SPRITE_UPDATE_SPEED);
		jumpingPlayerPistolAnim = new Animation(SPRITE_UPDATE_SPEED);
		crouchPlayerPistolAnim = new Animation(SPRITE_UPDATE_SPEED);
		runningPlayerPistolAnim = new Animation(SPRITE_UPDATE_SPEED);
		runningLegsAnim = new Animation(SPRITE_UPDATE_SPEED);

		solidArea = new Rectangle(4, 4, 50, 50);
	}

	private void setDefaultValues() {
		mapX = gp.tileSize * 10;
		mapY = GROUND_LEVEL;
		speed = 10;
		direction = Direction.RIGHT;
		bodyState = BodyState.STANDING;
		facingDirection = FacingDirection.RIGHT;
	}

	private void updateFacingDirection() {
		if (kh.rightPressed) {
			facingDirection = FacingDirection.RIGHT;
		}
		if (kh.leftPressed) {
			facingDirection = FacingDirection.LEFT;
		}
	}

	public void update() {
		kh.update();

		updateFacingDirection(); // <-- AGGIUNTA QUI

		if (kh.isReloading) {
			handleReload();
		} else {
			if (kh.spacePressed && !isJumping) {
				handleJumpStart();
			}

			handleCrouch();
			handleWalk();
			handleDirectionalAim();
			handleStanding();
		}

		if (isJumping) {
			handleJump();
		}

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
			kh.isReloading = false;
			reloadingAnim.reset();
			bodyState = BodyState.STANDING;
		}
	}

	private void handleJumpStart() {
		isJumping = true;
		velocityY = JUMP_STRENGTH;
		direction = Direction.JUMPING;
		bodyState = BodyState.JUMPING;
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
		jumpingPlayerPistolAnim.update(jumpingPlayerPistol.length);
	}

	private void handleCrouch() {
		if (kh.shiftPressed && !isJumping) {
			bodyState = BodyState.CROUCHING;
		}
	}

	private void handleWalk() {
		if ((kh.rightPressed || kh.leftPressed) && !isJumping) {
			if (kh.controlPressed) {
				bodyState = BodyState.RUNNING;
				speed = 16; // maggiore velocità durante la corsa
			} else {
				bodyState = BodyState.WALKING;
				speed = 10; // normale velocità a piedi
			}

			if (kh.rightPressed) {
				facingDirection = FacingDirection.RIGHT;
				direction = Direction.RIGHT;
			}
			if (kh.leftPressed) {
				facingDirection = FacingDirection.LEFT;
				direction = Direction.LEFT;
			}
		}
	}

	private void handleDirectionalAim() {
		if (kh.downPressed && !isJumping) {
			direction = Direction.DOWN;
		}
		if (kh.upPressed && !isJumping) {
			direction = Direction.UP;
		}
	}

	private void handleStanding() {
		if (!kh.rightPressed && !kh.leftPressed && !kh.isReloading && !kh.spacePressed && !kh.shiftPressed
				&& !kh.controlPressed) {
			bodyState = BodyState.STANDING;
			direction = Direction.STANDING;
			speed = 10; // reset velocità normale
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

	public void draw(Graphics2D g2d) {
		BufferedImage body = null, legs = null;
		int bodyOffsetY = gp.tileSize * 4 / gp.tileScale;
		int legsOffsetY = gp.tileSize * 2 / gp.tileScale;

		if (isJumping) {
			legs = jumpingLegs[jumpingLegsAnim.getFrame()];
			body = jumpingPlayerPistol[jumpingPlayerPistolAnim.getFrame()];
		} else {
			switch (bodyState) {
				case RELOADING -> {
					body = reloadPistol[reloadingAnim.getFrame()];
					legs = walkingLegs[walkingLegsAnim.getFrame()];
				}
				case WALKING -> {
					body = walkingPlayerPistol[walkingBodyAnim.getFrame()];
					legs = walkingLegs[walkingLegsAnim.getFrame()];
				}
				case RUNNING -> {
					body = runningPlayerPistol[runningPlayerPistolAnim.getFrame()];
					legs = runningLegs[runningLegsAnim.getFrame()]; // riusa gambe da walk se non hai sprite appositi
				}
				case CROUCHING -> {
					body = crouchPlayerPistol[crouchPlayerPistolAnim.getFrame()];
				}
				case STANDING -> {
					body = walkingPlayerPistol[5];
					legs = walkingLegs[5];
				}
				default -> {
					body = walkingPlayerPistol[walkingBodyAnim.getFrame()];
					legs = walkingLegs[walkingLegsAnim.getFrame()];
				}
			}
		}

		int x = screenX;

		if (facingDirection == FacingDirection.RIGHT) {
			g2d.drawImage(legs, x, screenY - legsOffsetY, gp.tileSize, gp.tileSize, null);
			g2d.drawImage(body, x, screenY - bodyOffsetY, gp.tileSize, gp.tileSize, null);
		} else {
			g2d.drawImage(legs, x + gp.tileSize, screenY - legsOffsetY, -gp.tileSize, gp.tileSize, null);
			g2d.drawImage(body, x + gp.tileSize, screenY - bodyOffsetY, -gp.tileSize, gp.tileSize, null);
		}
	}

	private enum BodyState {
		RELOADING, SHOOTING, JUMPING, WALKING, STANDING, CROUCHING, RUNNING
	}

}
