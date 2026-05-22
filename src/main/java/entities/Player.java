package entities;

import static utilz.Constants.Directions.*;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.w3c.dom.css.RGBColor;

import gamestates.Playing;
import io.arcaneblade.Game;
import utilz.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 15;
	private int playerAction = IDLE;
	private boolean moving = false, attacking = false;
	private int comboTick = 0, attackType = ATTACK_1;
	private boolean left, right, jump;
	private float playerSpeed = 2.0f;
	private int[][] lvlData;

	// Hitbox
	private float xDrawOffset = 130 * Game.SCALE;
	private float yDrawOffset = 128 * Game.SCALE;

	// Gravity
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;


	// STATUS BAR UI
	private BufferedImage statusBarImg;

	private int statusBarWidth = (int) (192 * Game.SCALE);
	private int statusBarHeight = (int) (58 * Game.SCALE);
	private int statusBarX = (int) (10 * Game.SCALE);
	private int statusBarY = (int) (10 * Game.SCALE);

	private int healthBarWidth = (int) (150 * Game.SCALE);
	private int healthBarHeight = (int) (4 * Game.SCALE);
	private int healthBarXStart = (int) (34 * Game.SCALE);
	private int healthBarYStart = (int) (14 * Game.SCALE);

//	private int maxHealth = 100;
//	private int currentHealth = maxHealth;
	private int healthWidth = healthBarWidth;

	// ATTACK BOX
	private Rectangle2D.Float attackBox;

	private int flipX = 0;
	private int flipW = 1;

	private boolean attackChecked;

	Color healthColor = new Color(0, 255, 179);

	private Playing playing;

	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		this.maxHealth = 100;
		this.currentHealth = maxHealth;
		loadAnimations();
		initHitbox(x, y, 26 * Game.SCALE, 30 * Game.SCALE);
		initAttackBox();
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (50 * Game.SCALE), (int) (20 * Game.SCALE));
	}

	public void update() {
		updateHealthBar();
		if(currentHealth <= 0) {
			playing.setGameOver(true);
			return;
		}
		updateAttackBox();

		updatePos();
		if (attacking)
			checkAttack();
		updateAnimationTick();
		setAnimation();
	}

	private void checkAttack() {
		if (attackChecked || aniIndex != 1)
			return;
		attackChecked = true;
		playing.checkEnemyHit(attackBox);
	}

	private void updateAttackBox() {
		if (right) {
			attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 5);
		} else if (left){
			attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 25);
		} else {}
		attackBox.y = hitbox.y + (Game.SCALE * 10);
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
	}

	public void render(Graphics g, int lvlOffset) {

		g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset) - lvlOffset + flipX,
				(int) (hitbox.y - yDrawOffset), width * flipW, height, null);
//		drawHitbox(g, lvlOffset);
//		drawAttackBox(g, lvlOffset);
		drawUI(g);
	}

	private void drawAttackBox(Graphics g, int lvlOffsetX) {
		g.setColor(healthColor);
		g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
	}

	private void drawUI(Graphics g) {
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
		g.setColor(healthColor);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
	}

	private void updateAnimationTick() {

		aniTick++;

		int speed = utilz.Constants.PlayerConstants.GetAniSpeed(playerAction);

		if (aniTick >= speed) {
			aniTick = 0;
			aniIndex++;

			if (aniIndex >= GetSpriteAmount(playerAction)) {
				aniIndex = 0;

				if (playerAction == ATTACK_1) {

					if (attackType == ATTACK_2) {

						playerAction = ATTACK_2;

					} else {
						attacking = false;
						attackChecked = false;
					}
				} else if (playerAction == ATTACK_2) {

					attacking = false;
					attackChecked = false;
					attackType = ATTACK_1;

				} else {
					attacking = false;
					attackChecked = false;
				}
			}
		}

	}

	private void setAnimation() {
		int startAni = playerAction;

		if (moving)
			playerAction = RUNNING;
		else
			playerAction = IDLE;

		if (inAir) {
			if (airSpeed < 0)
				playerAction = JUMPING;
			else
				playerAction = FALLING;
		}

		if (attacking) {

			if (playerAction != ATTACK_1 && playerAction != ATTACK_2) {
				playerAction = attackType;
			}
		}

		if (startAni != playerAction) {
			resetAniTick();
		}

	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;

	}

	private void updatePos() {

		moving = false;

		if (jump)
			jump();
//		if (!left && !right && !inAir)
//			return;
		if (!inAir)
			if ((!left && !right) || (right && left))
				return;

		float xSpeed = 0;

		if (left) {
			xSpeed -= playerSpeed;
			flipX = width;
			flipW = -1;
		}
		if (right) {
			xSpeed += playerSpeed;
			flipX = 0;
			flipW = 1;
		}

		if (!inAir)
			if (!isEntityOnFloor(hitbox, lvlData))
				inAir = true;

		if (inAir) {
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
		moving = true;

	}

	private void jump() {
		if (inAir)
			return;
		inAir = true;
		airSpeed = jumpSpeed;

	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;

	}

	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
		}

	}

	public void changeHealth(int value) {
		currentHealth += value;

		if (currentHealth <= 0) {
			currentHealth = 0;
			// gameOver();
		} else if (currentHealth >= maxHealth)
			currentHealth = maxHealth;
	}

	private void loadAnimations() {
		animations = new BufferedImage[LoadSave.PLAYER_ATLAS.length][];

		for (int i = 0; i < animations.length; i++) {

			BufferedImage spriteSheet = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS[i]);

			animations[i] = loadAnimRow(spriteSheet, GetSpriteAmount(i));
		}

		statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
	}

	private BufferedImage[] loadAnimRow(BufferedImage atlas, int frameCount) {
		BufferedImage[] tempRow = new BufferedImage[frameCount];

		if (atlas != null) {
			for (int i = 0; i < frameCount; i++) {
				tempRow[i] = atlas.getSubimage(i * 144, 0, 144, 144);
			}

		}
		return tempRow;
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if (!isEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
	}

	public void setAttacking(boolean attacking) {

		if (attacking) {

			if (!this.attacking) {

				this.attacking = true;
				this.attackType = ATTACK_1;

			} else if (playerAction == ATTACK_1) {

				this.attackType = ATTACK_2;
			}
		}
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}


	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		playerAction = IDLE;
		currentHealth = maxHealth;
		
		hitbox.x = x;
		hitbox.y = y;
		
		if(!isEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}
}
