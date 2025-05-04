package it.unipg.main;

import it.unipg.entity.*;

public class CollisionChecker {
	GamePanel gp;

	public CollisionChecker(GamePanel gp) {
		this.gp = gp;
	}

	public void checkCollision(Entity entity) {
		// Calcola i bordi dell'entità
		int entityLeftMapX = entity.mapX + entity.solidArea.x;
		int entityRightMapX = entity.mapX + entity.solidArea.x + entity.solidArea.width;
		int entityTopMapY = entity.mapY + entity.solidArea.y;
		int entityBottomMapY = entity.mapY + entity.solidArea.y + entity.solidArea.height;

		// Colonne e righe dell'entità nella mappa
		int entityLeftCol = entityLeftMapX / gp.tileSize;
		int entityRightCol = entityRightMapX / gp.tileSize;
		int entityTopRow = entityTopMapY / gp.tileSize - 1; // da risolvere!
		int entityBottomRow = entityBottomMapY / gp.tileSize - 1;

		int textureNum1, textureNum2;

		entity.collisionOn = false; // reset collisione

		switch (entity.direction) {
			case RIGHT: // le collisioni vengono erratamente messe a true anche se il giocatore si trova
									// sopra
				entityRightCol = (entityRightMapX + entity.speed) / gp.tileSize;
				textureNum1 = gp.tm.mapTextureNum[entityTopRow][entityRightCol];
				textureNum2 = gp.tm.mapTextureNum[entityBottomRow][entityRightCol];
				System.out.println(entityRightCol + "\n" + entityTopRow + "\n" + entityBottomRow + "\n" + entityLeftCol);
				if (gp.tm.textures[textureNum1].collision || gp.tm.textures[textureNum2].collision) {
					entity.collisionOn = true;
					System.out.println("COLLISIONE");
				}
				break;

			case LEFT:
				entityLeftCol = (entityLeftMapX - entity.speed) / gp.tileSize;
				textureNum1 = gp.tm.mapTextureNum[entityTopRow][entityLeftCol];
				textureNum2 = gp.tm.mapTextureNum[entityBottomRow][entityLeftCol];
				if (gp.tm.textures[textureNum1].collision || gp.tm.textures[textureNum2].collision) {
					entity.collisionOn = true;
				}
				break;

			default:
				break;
		}
	}
}
