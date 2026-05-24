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
import static utilz.Constants.LightningConstants.*;

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

			}
	}

	public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
		drawMobs(g, xLvlOffset, yLvlOffset);
	}

	private void drawMobs(Graphics g, int xLvlOffset, int yLvlOffset) {
		for (NightBorne nb : mobs)
			if (nb.isActive()) {
				g.drawImage(nbArr[nb.getEnemyState()][nb.getAniIndex()],
						(int) (nb.getHitbox().x - xLvlOffset) + nb.flipX() - NB_DRAWOFFSET_X,
						(int) (nb.getHitbox().y) - yLvlOffset - NB_DRAWOFFSET_Y, NIGHTBORNE_WIDTH * nb.flipW(),
						NIGHTBORNE_HEIGHT, null);
				nb.drawAttackBox(g, xLvlOffset, yLvlOffset);
			}
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
	    checkEnemyHit(attackBox, 10);
	}

	public void checkEnemyHit(Rectangle2D.Float attackBox, int damage) {
	    for (NightBorne nb : mobs)
	        if (nb.isActive())
	            if (attackBox.intersects(nb.getHitbox())) {
	                nb.hurt(damage);
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

	public NightBorne getClosestEnemy(float playerX, float playerY) {
		NightBorne closest = null;
		float closestDist = LIGHTNING_RANGE;
		
		for (NightBorne nb : mobs) {
			if(!nb.isActive())
				continue;
			float dist = (float)(Math.abs(nb.getHitbox().x - playerX));
			if(dist < closestDist) {
				closestDist = dist;
				closest = nb;
			}
		}
		return closest;
	}
	
	public void applyLightningDamage(float x, float y, int damage) {
		for (NightBorne nb : mobs) {
			if(!nb.isActive())
				continue;
			if(Math.abs(nb.getHitbox().x - x) < Game.TILES_SIZE &&
				Math.abs(nb.getHitbox().y - y) < Game.TILES_SIZE * 2) {
				nb.hurt(damage);
			}
		}
	}

	public void resetAllEnemies() {
		for (NightBorne nb : mobs)
			nb.resetEnemy();
	}
}
