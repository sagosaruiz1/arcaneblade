package utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import io.arcaneblade.Game;

public class LoadSave {

	public static final String[] PLAYER_ATLAS = { "/entities/player/Run.png", "/entities/player/Idle.png",
			"/entities/player/Jump.png", "/entities/player/Fall.png", "/entities/player/Dash.png",
			"/entities/player/Death.png", "/entities/player/Hurt.png", "/entities/player/Attack 1.png",
			"/entities/player/Attack 2.png", };

	public static final String LEVEL_ATLAS = "/maps/tilemap.png";
	public static final String LEVEL_ONE_DATA = "/maps/level-one/level_one_data.png";

	public static BufferedImage GetSpriteAtlas(String fileName) {
		BufferedImage img = null;

		InputStream is = LoadSave.class.getResourceAsStream(fileName);

		try {
			if (is != null) {
				img = ImageIO.read(is);
			} else {
				System.err.println("File not found: " + fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return img;

	}

	public static int[][] GetLevelData() {
		int[][] lvlData = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);

		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				
				lvlData[j][i] = color.getRed();
			}
		return lvlData;
	}
}
