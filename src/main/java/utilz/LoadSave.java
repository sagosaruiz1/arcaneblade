package utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import entities.NightBorne;
import io.arcaneblade.Game;
import static utilz.Constants.EnemyConstants.NIGHTBORNE;

public class LoadSave {

	public static final String[] PLAYER_ATLAS = { "/entities/player/Run.png", "/entities/player/Idle.png",
			"/entities/player/Jump.png", "/entities/player/Fall.png", "/entities/player/Dash.png",
			"/entities/player/Death.png", "/entities/player/Hurt.png", "/entities/player/Attack 1.png",
			"/entities/player/Attack 2.png", };

	public static final String NIGHT_BORNE = "/entities/enemy/NightBorne.png";

	public static final String LEVEL_ATLAS = "/maps/tilemap.png";
//	public static final String LEVEL_ONE_DATA = "/maps/level-one/level_one_data.png";
	public static final String LEVEL_ONE_DATA_LONG = "/maps/level-one/level_one_data_long.png";
	public static final String MENU_BUTTONS = "/ui/button_atlas.png";
	public static final String MENU_BACKGROUND = "/ui/menu_background.png";
	public static final String MENU_BACKGROUND_IMG = "/ui/background_menu.png";
	public static final String PAUSE_BACKGROUND = "/ui/pause_menu.png";
	public static final String SOUND_BUTTONS = "/ui/sound_button.png";
	public static final String URM_BUTTONS = "/ui/urm_buttons.png";
	public static final String VOLUME_BUTTONS = "/ui/volume_buttons.png";
	public static final String PLAYING_BG_IMG = "/ui/playing_bg_img.png";
	public static final String PILLARS = "/ui/pillars.png";

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

	public static ArrayList<NightBorne> GetMobs() {
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA_LONG);
		ArrayList<NightBorne> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == NIGHTBORNE)
					list.add(new NightBorne(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
			}
		return list;
	}

	// GENERATE LEVEL ONE MAP
	public static int[][] GetLevelData() {
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA_LONG);
		int[][] lvlData = new int[img.getHeight()][img.getWidth()];

		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value >= 48)
					value = 0;
				lvlData[j][i] = value;
			}
		return lvlData;

	}
}
