package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.EnemyManager;
import entities.Player;
import io.arcaneblade.Game;
import levels.LevelManager;
import objects.LightningStrike;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utilz.Gate;
import utilz.LoadSave;

public class Playing extends State implements Statemethods {
	private Player player;
	private LevelManager levelManager;
	private EnemyManager enemyManager;
	private ObjectManager objectManager;
	private PauseOverlay pauseOverlay;
	private GameOverOverlay gameOverOverlay;
	private LevelCompletedOverlay levelCompletedOverlay;
	private boolean paused = false;

	private int xLvlOffset;
	private int leftBorder = (int) (0.5 * Game.GAME_WIDTH);
	private int rightBorder = (int) (0.5 * Game.GAME_WIDTH);
	private int maxLvlOffsetX;
	private int yLvlOffset;
	private int topBorder = (int) (0.5 * Game.GAME_HEIGHT);
	private int bottomBorder = (int) (0.5 * Game.GAME_HEIGHT);
	private int maxLvlOffsetY;

	private BufferedImage[] zoneBackgrounds, lightningFrames;
	private BufferedImage backgroundImg;

	private boolean gameOver;
	private boolean lvlCompleted;
	private boolean playerDying;


	private ArrayList<LightningStrike> lightningStrikes = new ArrayList<>();

	public Playing(Game game) {
		super(game);
		initClasses();

		zoneBackgrounds = new BufferedImage[] { LoadSave.GetSpriteAtlas(LoadSave.ZONE_1_BG),
				LoadSave.GetSpriteAtlas(LoadSave.ZONE_2_BG), LoadSave.GetSpriteAtlas(LoadSave.ZONE_3_BG), };
		// Set first zone background
		backgroundImg = zoneBackgrounds[0];

//		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BACKGROUND_IMG);
//		pillars = LoadSave.GetSpriteAtlas(LoadSave.PILLARS);

		calcLvlOffset();
		loadStartLevel();
	}

	public void loadNextLevel() {
		gameOver = false;
		paused = false;
		playerDying = false;
		xLvlOffset = 0;
		yLvlOffset = 0;
		levelManager.loadNextLevel();

		// NEW: switch background to match new zone
		int lvlIdx = levelManager.getLvlIndex();
		if (lvlIdx < zoneBackgrounds.length)
			backgroundImg = zoneBackgrounds[lvlIdx];

		Point spawn = levelManager.getCurrentLevel().getPlayerSpawn();
		player.setSpawn(spawn);
		player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		enemyManager.loadEnemies(levelManager.getCurrentLevel());
		objectManager.loadObjects(levelManager.getCurrentLevel());
		calcLvlOffset();
	}
	private void loadStartLevel() {
		enemyManager.loadEnemies(levelManager.getCurrentLevel());
		objectManager.loadObjects(levelManager.getCurrentLevel());
	}

	private void loadLightningFrames() {
		BufferedImage sheet = LoadSave.GetSpriteAtlas(LoadSave.LIGHTNING_STRIKE);
		lightningFrames = new BufferedImage[8];
		for (int i = 0; i < 8; i++)
			lightningFrames[i] = sheet.getSubimage(i * 64, 0, 64, 160);
	}

	private void checkLightningCast() {
	    if (!player.isCastingLightning()) return;

	    float strikeX;
	    if (player.flipW == 1) // facing right
	        strikeX = player.getHitbox().x + player.getLightningBox().width / 2;
	    else // facing left
	        strikeX = player.getHitbox().x - player.getLightningBox().width / 2;

	    float strikeY = player.getHitbox().y;
	    lightningStrikes.add(new LightningStrike(strikeX, strikeY, lightningFrames, player.flipW));
	}

	private void updateLightning() {
		for (LightningStrike ls : lightningStrikes) {
			if (!ls.isActive())
				continue;
			ls.update();
			if (ls.shouldDealDamage()) {
				enemyManager.checkEnemyHit(player.getLightningBox(), 10);
				ls.setDamageDealt();
			}
		}
		lightningStrikes.removeIf(ls -> !ls.isActive());
	}

	private void drawLightning(Graphics g) {
		for (LightningStrike ls : lightningStrikes)
			ls.draw(g, xLvlOffset, yLvlOffset);
	}

	private void calcLvlOffset() {
		maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
		maxLvlOffsetY = levelManager.getCurrentLevel().getLvlOffsetY();
	}

	private void initClasses() {
		levelManager = new LevelManager(game);
		enemyManager = new EnemyManager(this);
		objectManager = new ObjectManager(this);

		Point spawn = levelManager.getCurrentLevel().getPlayerSpawn();
		player = new Player(spawn.x, spawn.y, (int) (288 * Game.SCALE), (int) (288 * Game.SCALE), this);
		player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		pauseOverlay = new PauseOverlay(this);
		gameOverOverlay = new GameOverOverlay(this);
		levelCompletedOverlay = new LevelCompletedOverlay(this);

		loadLightningFrames();
	}

	@Override
	public void update() {
		if (paused) {
			pauseOverlay.update();
		} else if (lvlCompleted) {
			levelCompletedOverlay.update();
		} else if (gameOver) {
			gameOverOverlay.update();
		} else if (playerDying) {
			player.update();
		} else {
			levelManager.update();
			objectManager.update();
			player.update();
			enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
			checkCloseToBorder();
			checkGates(); // CHECK GATES
			checkLightningCast();
			updateLightning();
		}
	}

	private void checkGates() {
		ArrayList<Gate> gates = levelManager.getCurrentLevel().getGates();
		Rectangle2D.Float playerHitbox = player.getHitbox();

		for (Gate gate : gates) {
			if (gate.trigger == null)
				continue;

			Point trigger = gate.trigger;
			if (playerHitbox.x < trigger.x + Game.TILES_SIZE && playerHitbox.x + playerHitbox.width > trigger.x
					&& playerHitbox.y < trigger.y + Game.TILES_SIZE
					&& playerHitbox.y + playerHitbox.height > trigger.y) {
				loadLevelViaGate(gate.id);
				return;
			}

			// COORDINATE DEBUGGER
//	        System.out.println("Gate ID: " + gate.id + 
//	                " trigger tile: (" + (trigger.x / Game.TILES_SIZE) + ", " + (trigger.y / Game.TILES_SIZE) + ")" +
//	                " | player tile: (" + (int)(playerHitbox.x / Game.TILES_SIZE) + ", " + (int)(playerHitbox.y / Game.TILES_SIZE) + ")");
//	            
//            if (playerHitbox.x < trigger.x + Game.TILES_SIZE &&
//                playerHitbox.x + playerHitbox.width > trigger.x &&
//                playerHitbox.y < trigger.y + Game.TILES_SIZE &&
//                playerHitbox.y + playerHitbox.height > trigger.y) {
//                loadLevelViaGate(gate.id);
//                return;
//            }
		}
	}

	private void loadLevelViaGate(int gateId) {
		int currentIdx = levelManager.getLvlIndex();
		boolean goingForward = isForwardGate(gateId, currentIdx);

		gameOver = false;
		paused = false;
		playerDying = false;
		xLvlOffset = 0;
		yLvlOffset = 0;

		if (goingForward) {
			if (currentIdx >= levelManager.getAmountOfLevels() - 1) {
				setLevelCompleted(true);
				return;
			}
			levelManager.loadNextLevel();
		} else {
			levelManager.loadPreviousLevel();
		}

		int lvlIdx = levelManager.getLvlIndex();
		if (lvlIdx < zoneBackgrounds.length)
			backgroundImg = zoneBackgrounds[lvlIdx];

		Point spawn = findGateSpawn(gateId);
		player.setSpawn(spawn);
		player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		enemyManager.loadEnemies(levelManager.getCurrentLevel());
		objectManager.loadObjects(levelManager.getCurrentLevel());
		calcLvlOffset();
	}

	private boolean isForwardGate(int gateId, int currentLvlIdx) {
		if (currentLvlIdx == 0)
			return true; // Zone 1: always forward to Zone 2
		if (currentLvlIdx == 2)
			return false; // Zone 3: always back to Zone 2
		return gateId == 3; // Zone 2: Gate C(3) forward, Gate A(1) & B(2) back
	}

	private Point findGateSpawn(int gateId) {
		ArrayList<Gate> gates = levelManager.getCurrentLevel().getGates();
		for (Gate gate : gates)
			if (gate.id == gateId && gate.spawn != null)
				return gate.spawn;
		System.out.println("Gate " + gateId + " spawn not found! Using default.");
		return levelManager.getCurrentLevel().getPlayerSpawn();
	}

	// PLAYER VIEW
	private void checkCloseToBorder() {
		int playerX = (int) player.getHitbox().x;
		int diffX = playerX - xLvlOffset;
		if (diffX > rightBorder)
			xLvlOffset += diffX - rightBorder;
		else if (diffX < leftBorder)
			xLvlOffset += diffX - leftBorder;
		xLvlOffset = Math.max(0, Math.min(xLvlOffset, maxLvlOffsetX));

		int playerY = (int) player.getHitbox().y;
		int diffY = playerY - yLvlOffset;
		if (diffY > bottomBorder)
			yLvlOffset += diffY - bottomBorder;
		else if (diffY < topBorder)
			yLvlOffset += diffY - topBorder;
		yLvlOffset = Math.max(0, Math.min(yLvlOffset, maxLvlOffsetY));

//		checkNextZone();
//		checkReturnZone();
//		checkGates();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

//		drawPillars(g);

		levelManager.draw(g, xLvlOffset, yLvlOffset);
		player.render(g, xLvlOffset, yLvlOffset);
		enemyManager.draw(g, xLvlOffset, yLvlOffset);
		drawLightning(g);
		objectManager.draw(g, xLvlOffset, yLvlOffset);

		if (paused) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pauseOverlay.draw(g);
		} else if (gameOver) {
			gameOverOverlay.draw(g);
		} else if (lvlCompleted) {
			levelCompletedOverlay.draw(g);
		}
	}
	
	public void resetAll() {
		// TODO: reset playing, enemy, lvl, etc.
		gameOver = false;
		paused = false;
		lvlCompleted = false;
		playerDying = false;
		xLvlOffset = 0;
		yLvlOffset = 0;
		player.resetAll();
		enemyManager.resetAllEnemies();
		objectManager.resetAllObjects();
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public void checkObjectHit(Rectangle2D.Float attackBox) {
		objectManager.checkObjectHit(attackBox);
	}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		enemyManager.checkEnemyHit(attackBox);
	}

	public void checkPotionTouched(Rectangle2D.Float hitbox) {
		objectManager.checkObjectTouched(hitbox);
	}

	public void checkSpikesTouched(Player p) {
		objectManager.checkSpikesTouched(p);
	}

	public void mouseDragged(MouseEvent e) {
		if (!gameOver)
			if (paused)
				pauseOverlay.mouseDragged(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!gameOver)
			if (e.getButton() == MouseEvent.BUTTON1)
				player.setAttacking(true);

		// DEBUG - print tile coordinate of mouse click
		int tileX = (e.getX() + xLvlOffset) / Game.TILES_SIZE;
		int tileY = (e.getY() + yLvlOffset) / Game.TILES_SIZE;
		System.out.println("Clicked tile: (" + tileX + ", " + tileY + ")");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mousePressed(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mousePressed(e);
		} else
			gameOverOverlay.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mouseReleased(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mouseReleased(e);
		} else
			gameOverOverlay.mouseReleased(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!gameOver) {
			if (paused)
				pauseOverlay.mouseMoved(e);
			else if (lvlCompleted)
				levelCompletedOverlay.mouseMoved(e);
		} else
			gameOverOverlay.mouseMoved(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (gameOver)
			gameOverOverlay.keyPressed(e);
		else
			switch (e.getKeyCode()) {

			case KeyEvent.VK_W:
				player.setJump(true);
				break;
			case KeyEvent.VK_A:
				player.setLeft(true);
				break;
			case KeyEvent.VK_D:
				player.setRight(true);
				break;
			case KeyEvent.VK_K:
				player.setAttacking(true);
				break;
			case KeyEvent.VK_L:
				player.castLightning();
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(true);
				break;
			case KeyEvent.VK_SHIFT:
				player.dash();
				break;
			case KeyEvent.VK_ESCAPE:
				paused = !paused;
			}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!gameOver)
			switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				player.setJump(false);
				break;
			case KeyEvent.VK_A:
				player.setLeft(false);
				break;
			case KeyEvent.VK_D:
				player.setRight(false);
				break;
			case KeyEvent.VK_K:
				player.setAttacking(false);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(false);
				break;
			case KeyEvent.VK_SHIFT:
				break;

			}
	}

	public void setLevelCompleted(boolean levelCompleted) {
		this.lvlCompleted = levelCompleted;
	}

	public void setMaxLvlOffset(int lvlOffset) {
		this.maxLvlOffsetX = lvlOffset;
	}

	public void setMaxLvlOffsetY(int lvlOffsetY) {
		this.maxLvlOffsetY = lvlOffsetY;
	}

	public void unpauseGame() {
		paused = false;
	}

	public void windowFocusLost() {
		player.resetDirBooleans();
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public Player getPlayer() {
		return player;
	}

	public EnemyManager getEnemyManager() {
		return enemyManager;
	}

	public ObjectManager getObjectManager() {
		return objectManager;
	}

	public void setPlayerDying(boolean playerDying) {
		this.playerDying = playerDying;
	}

}
