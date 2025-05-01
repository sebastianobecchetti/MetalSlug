package it.unipg.entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {
	public int mapX, mapY;
	public int speed;

	public BufferedImage rightStanding, rightWalking, leftStanding, leftWalking, playerJump, playerUp,
			playerCrouch, playerLand; // VANNO TUTTE CAMBIATE ABBIAMO CAMBIATO I NOMI DI UN BOTTO DI ROBA DA FARE IL
																// PRIMA POSSIBILE PLS CHECCO

	public Direction direction;
	public FacingDirection facingDirection;

	public int spriteCounter = 0;
	public int spriteNum = 1;
	public Rectangle solidArea;
	public boolean collisionOn = false;

	public enum Direction {
		RIGHT, LEFT, UP, DOWN, JUMPING, LANDING, STANDING
	}

	public enum FacingDirection {
		RIGHT, LEFT
	}
}
