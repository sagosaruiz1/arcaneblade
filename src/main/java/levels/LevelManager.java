package levels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Gamestate;
import io.arcaneblade.Game;
import utilz.LoadSave;

public class LevelManager {

	private Game game;
	private BufferedImage[] levelSprite;
	private ArrayList<Level> levels;
	private int lvlIndex = 0;

	public LevelManager(Game game) {
		this.game = game;
		importOutsideSprites();
		levels = new ArrayList<>();
		buildAllLevels();
	}

	public void loadNextLevel() {
		lvlIndex++;
		if (lvlIndex >= levels.size()) {
			lvlIndex = 0;
			System.out.println("No more levels! Game Completed!");
			Gamestate.state = Gamestate.MENU;
		}

		Level newLevel = levels.get(lvlIndex);
		game.getPlaying().getEnemyManager().loadEnemies(newLevel);
		game.getPlaying().getPlayer().loadLvlData(newLevel.getLevelData());
		game.getPlaying().setMaxLvlOffset(newLevel.getLvlOffset());
		game.getPlaying().setMaxLvlOffsetY(newLevel.getLvlOffsetY());
		game.getPlaying().getObjectManager().loadObjects(newLevel);

	}

	private void buildAllLevels() {
		BufferedImage[] allLevels = LoadSave.GetAllLevels();
		for (BufferedImage img : allLevels)
			levels.add(new Level(img));
	}

	private void importOutsideSprites() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprite = new BufferedImage[48];
		for (int j = 0; j < 4; j++)
			for (int i = 0; i < 12; i++) {
				int index = j * 12 + i;
				levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
			}

	}

	// DRAW THE GAME MAP
	public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
		int[][] lvlData = levels.get(lvlIndex).getLevelData();

		int startTileX = xLvlOffset / Game.TILES_SIZE;
		int startTileY = yLvlOffset / Game.TILES_SIZE;
		int endTileX = Math.min(startTileX + Game.TILES_IN_WIDTH + 1, lvlData[0].length);
		int endTileY = Math.min(startTileY + Game.TILES_IN_HEIGHT + 1, lvlData.length);

		for (int j = startTileY; j < endTileY; j++)
			for (int i = startTileX; i < endTileX; i++) {
				int index = levels.get(lvlIndex).getSpriteIndex(i, j);
				if (index < 0 || index >= levelSprite.length)
					continue;
				g.drawImage(levelSprite[index],
						Game.TILES_SIZE * i - xLvlOffset,
						Game.TILES_SIZE * j - yLvlOffset,
						Game.TILES_SIZE, Game.TILES_SIZE, null);
			}
	}

	public void update() {

	}

	public Level getCurrentLevel() {
		return levels.get(lvlIndex);
	}

	public int getAmountOfLevels() {
		return levels.size();
	}

	public int getLvlIndex() {
		return lvlIndex;
	}
}
