package levels;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.NightBorne;
import io.arcaneblade.Game;
import objects.GameContainer;
import objects.Potion;
import utilz.HelpMethods;
import utilz.LoadSave;
import static utilz.HelpMethods.GetLevelData;
import static utilz.HelpMethods.GetMobs;

public class Level {

	private BufferedImage img;
	private int[][] lvlData;
	private ArrayList<NightBorne> mobs;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private int lvlTilesWide;
	private int maxTilesOffset;
	private int maxLvlOffsetX;

	public Level(BufferedImage img) {
		this.img = img;
		createLevelData();
		createEnemies();
		createPotions();
		createContainers();
		calcLvlOffsets();
	}

	private void createContainers() {
		containers = HelpMethods.GetContainers(img);
	}

	private void createPotions() {
		potions = HelpMethods.GetPotions(img);
	}

	private void calcLvlOffsets() {
		lvlTilesWide = img.getWidth();
		maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
		maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
	}

	private void createEnemies() {
		mobs = GetMobs(img);
	}

	private void createLevelData() {
		lvlData = GetLevelData(img);
	}

	public int getSpriteIndex(int x, int y) {
		return lvlData[y][x];
	}

	public int[][] getLevelData() {
		return lvlData;
	}

	public int getLvlOffset() {
		return maxLvlOffsetX;
	}

	public ArrayList<NightBorne> getMobs() {
		return mobs;
	}
	
	public ArrayList<Potion> getPotions() {
		return potions;
	}
	
	public ArrayList<GameContainer> getContainers() {
		return containers;
	}
}
