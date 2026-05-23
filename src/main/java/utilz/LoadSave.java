package utilz;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
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
	
	public static final String MENU_BUTTONS = "/ui/button_atlas.png";
	public static final String MENU_BACKGROUND = "/ui/menu_background.png";
	public static final String MENU_BACKGROUND_IMG = "/ui/background_menu.png";
	public static final String PAUSE_BACKGROUND = "/ui/pause_menu.png";
	public static final String SOUND_BUTTONS = "/ui/sound_button.png";
	public static final String URM_BUTTONS = "/ui/urm_buttons.png";
	public static final String VOLUME_BUTTONS = "/ui/volume_buttons.png";
	public static final String PLAYING_BG_IMG = "/ui/playing_bg_img.png";
	public static final String PILLARS = "/ui/pillars.png";
	public static final String STATUS_BAR = "/ui/health_power_bar.png";
	public static final String COMPLETED_IMG = "/ui/completed_sprite.png";
	public static final String POTION_ATLAS = "/ui/potions_sprites.png";
	public static final String CONTAINER_ATLAS = "/ui/objects_sprites.png";
	public static final String DEATH_SCREEN = "/ui/death_screen.png";
	public static final String OPTIONS_MENU = "/ui/options_background.png";
	public static final String PLAYING_BACKGROUND_IMG ="/ui/playing_background_img.png";
	
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



	public static BufferedImage[] GetAllLevels() {
		URL url = LoadSave.class.getResource("/maps/lvls");
		File file = null;

		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		File[] files = file.listFiles();
		File[] filesSorted = new File[files.length];

		for (int i = 0; i < filesSorted.length; i++)
			for (int j = 0; j < files.length; j++) {
				if (files[j].getName().equals("" + (i + 1) + ".png"))
					filesSorted[i] = files[j];
			}

		BufferedImage[] imgs = new BufferedImage[filesSorted.length];

		for (int i = 0; i < imgs.length; i++)
			try {
				imgs[i] = ImageIO.read(filesSorted[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}

		return imgs;

	}

}
