package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import io.arcaneblade.Game;
import levels.Level;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

	private Playing playing;
	private BufferedImage[][] nbArr;
	private ArrayList<NightBorne> mobs = new ArrayList<>();

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
	}

	public void loadEnemies(Level level) {
		mobs = level.getMobs();
		System.out.println("size of mobs: " + mobs.size());
	}

	public void update(int[][] lvlData, Player player) {
		boolean isAnyActive = false;
		for (NightBorne nb : mobs)
			if (nb.isActive()) {
				nb.update(lvlData, player);
				isAnyActive = true;
			}
		if(!isAnyActive)
			playing.setLevelCompleted(true);
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawMobs(g, xLvlOffset);
	}

	private void drawMobs(Graphics g, int xLvlOffset) {
		for (NightBorne nb : mobs)
			if (nb.isActive()) {
				int yOffset = (int) (NIGHTBORNE_HEIGHT - Game.TILES_SIZE * 2);
				
				int extraX = 10;
				int extraY = 10;
				
				
				g.drawImage(nbArr[nb.getEnemyState()][nb.getAniIndex()],
						(int) (nb.getHitbox().x - xLvlOffset) + nb.flipX() + extraX,
						(int) (nb.getHitbox().y - yOffset) - extraY,
						NIGHTBORNE_WIDTH * nb.flipW(),
						NIGHTBORNE_HEIGHT, null);
				nb.drawAttackBox(g, xLvlOffset);
			}
	}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for (NightBorne nb : mobs)
			if (nb.isActive())
				if (attackBox.intersects(nb.getHitbox())) {
					nb.hurt(10);
					return;
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
	
	public void resetAllEnemies() {
		for (NightBorne nb : mobs)
			nb.resetEnemy();
	}
}
