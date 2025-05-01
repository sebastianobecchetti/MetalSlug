package it.unipg.texture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import it.unipg.main.GamePanel;

public class TextureManager {
	private static final String FILE_PATH = "/res/map/map_01_prova.txt";
	GamePanel gp;
	public Texture[] textures;
	public int mapTextureNum[][];

	public TextureManager(GamePanel gp) {
		this.gp = gp;
		textures = new Texture[10]; // per esempio 10
		mapTextureNum = new int[gp.maxMapRow][gp.maxMapCol];

		loadTextures();
		loadMap();

	}

	public void loadTextures() {
		try {
			textures[0] = new Texture();
			textures[0].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/earth.png"));
			textures[0].collision = true;

			textures[1] = new Texture();
			textures[1].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/wall.png"));

			textures[2] = new Texture();
			textures[2].image = ImageIO.read(getClass().getResourceAsStream("/res/tiles/trasparente.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadMap() {
		try (InputStream is = getClass().getResourceAsStream(FILE_PATH);
				BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

			for (int row = 0; row < gp.maxMapRow; row++) {
				String line = br.readLine();
				if (line == null || line.trim().isEmpty()) {
					// Riga mancante o vuota → riempiamo con 0
					for (int col = 0; col < gp.maxMapCol; col++) {
						mapTextureNum[row][col] = 0;
					}
					continue;
				}

				String[] numbers = line.trim().split("\\s+");

				for (int col = 0; col < gp.maxMapCol; col++) {
					if (col < numbers.length) {
						try {
							int num = Integer.parseInt(numbers[col]);
							mapTextureNum[row][col] = num;
						} catch (NumberFormatException e) {
							mapTextureNum[row][col] = 0; // valore non valido → default
						}
					} else {
						mapTextureNum[row][col] = 0; // Colonna mancante
					}
				}
			}

		} catch (IOException e) {
			System.err.println("Errore nel caricamento della mappa da: " + FILE_PATH);
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g2d) {

		for (int row = 0; row < gp.maxMapRow; row++) {
			for (int col = 0; col < gp.maxMapCol; col++) {
				int textureNum = mapTextureNum[row][col];

				int mapX = col * gp.tileSize;
				int mapY = row * gp.tileSize;
				int screenX = mapX - gp.player.mapX + gp.player.screenX;
				int screenY = mapY - gp.player.mapY + gp.player.screenY;

				if (mapX + gp.tileSize > gp.player.mapX - gp.player.screenX &&
						mapX - gp.tileSize < gp.player.mapX + gp.player.screenX &&
						mapY + gp.tileSize > gp.player.mapY - gp.player.screenY &&
						mapY - gp.tileSize < gp.player.mapY + gp.player.screenY)
					g2d.drawImage(textures[textureNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
			}
		}

	}
}
