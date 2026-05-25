package levels;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.NightBorne;
import io.arcaneblade.Game;
import objects.GameContainer;
import objects.Potion;
import objects.Spike;
import utilz.Gate;
import utilz.HelpMethods;
import static utilz.HelpMethods.GetLevelData;
import static utilz.HelpMethods.GetMobs;

public class Level {

	private BufferedImage img;
	private int[][] lvlData;
	private ArrayList<NightBorne> mobs;
	private ArrayList<Potion> potions;
	private ArrayList<Spike> spikes;
	private ArrayList<GameContainer> containers;
	private ArrayList<Gate> gates= new ArrayList<>();
	
	private Point playerSpawn;
	
	private int lvlTilesWide;
	private int lvlTilesTall;
	private int maxTilesOffset;
	private int maxTilesOffsetY;
	private int maxLvlOffsetX;
	private int maxLvlOffsetY;

	public Level(BufferedImage img) {
		this.img = img;
		createLevelData();
		createEnemies();
		createPotions();
		createContainers();
		createSpikes();
		calcLvlOffsets();
		createPlayerSpawn();
	}
	
	private void createSpikes() {
		spikes = HelpMethods.GetSpikes(img);
	}

	public void addGate(Gate gate) {
		gates.add(gate);
	}

	private void createContainers() {
		containers = HelpMethods.GetContainers(img);
	}

	private void createPotions() {
		potions = HelpMethods.GetPotions(img);
	}

	private void calcLvlOffsets() {
		lvlTilesWide = img.getWidth();
		lvlTilesTall = img.getHeight();
		maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
		maxTilesOffsetY = lvlTilesTall - Game.TILES_IN_HEIGHT;
		maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
		maxLvlOffsetY = Game.TILES_SIZE * maxTilesOffsetY;
	}

	private void createEnemies() {
		mobs = GetMobs(img);
	}

	private void createLevelData() {
		lvlData = GetLevelData(img);
	}
	
	// NEW
	private void createPlayerSpawn() {
	    playerSpawn = HelpMethods.GetPlayerSpawn(img);
	}
	
	// NEW
	public Point getPlayerSpawn() {
	    return playerSpawn;
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
	
	public int getLvlOffsetY() {
		return maxLvlOffsetY;
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
	
	public ArrayList<Gate> getGates() {
		return gates;
	}
	
	public ArrayList<Spike> getSpikes() {
		return spikes;
	}
}
