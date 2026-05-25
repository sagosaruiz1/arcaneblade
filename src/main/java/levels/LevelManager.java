package levels;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Gamestate;
import io.arcaneblade.Game;
import utilz.Gate;
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

	public void loadPreviousLevel() {
		lvlIndex--;
		if (lvlIndex < 0) {
			lvlIndex = 0;
			System.out.println("Already at first zone!");
			return;
		}

		Level prevLevel = levels.get(lvlIndex);
		game.getPlaying().getEnemyManager().loadEnemies(prevLevel);
		game.getPlaying().getPlayer().loadLvlData(prevLevel.getLevelData());
		game.getPlaying().setMaxLvlOffset(prevLevel.getLvlOffset());
		game.getPlaying().setMaxLvlOffsetY(prevLevel.getLvlOffsetY());
		game.getPlaying().getObjectManager().loadObjects(prevLevel);
	}

	// NEW EXPERIMENT
	private void buildAllLevels() {
		BufferedImage[] allLevels = LoadSave.GetAllLevels();
		for (BufferedImage img : allLevels)
			levels.add(new Level(img));

		setupGates();
	}

	// GATES TO ANOTHER ZONES
	private void setupGates() {
	    // --- ZONE 1 ---
		// Gate A
	    levels.get(0).addGate(new Gate(1,
	        new Point(29 * Game.TILES_SIZE, 29 * Game.TILES_SIZE),
	        null,
	        false));
	    // Gate B
	    levels.get(0).addGate(new Gate(2,
	        new Point(99 * Game.TILES_SIZE, 27 * Game.TILES_SIZE),
	        new Point(99 * Game.TILES_SIZE, 26 * Game.TILES_SIZE), true));

	    // --- ZONE 2 ---
	    levels.get(1).addGate(new Gate(1,
	        null,
	        new Point(29 * Game.TILES_SIZE, 0 * Game.TILES_SIZE), false));

	    levels.get(1).addGate(new Gate(2,
	        new Point(75 * Game.TILES_SIZE, 4 * Game.TILES_SIZE),
	        new Point(72 * Game.TILES_SIZE, 4 * Game.TILES_SIZE), true));
	    
	    levels.get(1).addGate(new Gate(3,
	    		new Point(70 * Game.TILES_SIZE, 29 * Game.TILES_SIZE),
	    		null,
	    		false));

	    // --- ZONE 3 ---
	    levels.get(2).addGate(new Gate(3,
		        new Point(69 * Game.TILES_SIZE, 0 * Game.TILES_SIZE),
		        new Point(69 * Game.TILES_SIZE, 1 * Game.TILES_SIZE), true));
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
				g.drawImage(levelSprite[index], Game.TILES_SIZE * i - xLvlOffset, Game.TILES_SIZE * j - yLvlOffset,
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
