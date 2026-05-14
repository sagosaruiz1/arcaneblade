package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import io.arcaneblade.Game;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

	private Playing playing;
	private BufferedImage[][] nbArr;
	private ArrayList<NightBorne> mobs = new ArrayList<>();

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
		addEnemies();
	}

	private void addEnemies() {
		mobs = LoadSave.GetMobs();
		System.out.println("size of crab: " + mobs.size());
	}

	public void update(int[][] lvlData) {
		for (NightBorne nb : mobs)
			nb.update(lvlData);
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawMobs(g, xLvlOffset);
	}

	private void drawMobs(Graphics g, int xLvlOffset) {
//		for (NightBorne nb : mobs)
//			g.drawImage(nbArr[nb.getEnemyState()][nb.getAniIndex()], (int) nb.getHitbox().x - xLvlOffset,
//					(int) nb.getHitbox().y, NIGHTBORNE_WIDTH, NIGHTBORNE_HEIGHT, null);

		for (NightBorne nb : mobs) {
			int yOffset = (int) (NIGHTBORNE_HEIGHT - Game.TILES_SIZE * 2);

			g.drawImage(nbArr[nb.getEnemyState()][nb.getAniIndex()], (int) nb.getHitbox().x - xLvlOffset,
					(int) nb.getHitbox().y - yOffset, NIGHTBORNE_WIDTH, NIGHTBORNE_HEIGHT, null);
		}
	}

	private void loadEnemyImgs() {
		nbArr = new BufferedImage[5][23];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.NIGHT_BORNE);
		for (int j = 0; j < nbArr.length; j++)
			for (int i = 0; i < nbArr[j].length; i++)
				nbArr[j][i] = temp.getSubimage(i * NIGHTBORNE_WIDTH_DEFAULT, j * NIGHTBORNE_HEIGHT_DEFAULT,
						NIGHTBORNE_WIDTH_DEFAULT, NIGHTBORNE_HEIGHT_DEFAULT);

	}
}
