package entities;

import static utilz.Constants.Directions.*;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import io.arcaneblade.Game;
import utilz.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 15;
	private int playerAction = IDLE;
	private boolean moving = false, attacking = false;
	private int comboTick = 0, attackType = ATTACK_1;
	private boolean left, up, right, down, jump;
	private float playerSpeed = 2.0f;
	private int[][] lvlData;

	// Hitbox
	private float xDrawOffset = 130 * Game.SCALE;
	private float yDrawOffset = 128 * Game.SCALE;

	// Gravity
	private float airSpeed = 0f;
	private float gravity = 0.04f * Game.SCALE;
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
	private boolean inAir = false;

	public Player(float x, float y, int width, int height) {
		super(x, y, width, height);
		loadAnimations();
		initHitbox(x, y, 26 * Game.SCALE, 30 * Game.SCALE);

	}

	public void update() {
		updatePos();
		updateAnimationTick();
		setAnimation();

	}

	public void render(Graphics g, int lvlOffset) {

		g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset) - lvlOffset,
				(int) (hitbox.y - yDrawOffset), width, height, null);
//		drawHitbox(g);

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
					}
				} else if (playerAction == ATTACK_2) {

					attacking = false;
					attackType = ATTACK_1;

				} else {
					attacking = false;
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
		if(!inAir)
			if((!left && !right) || (right && left))
				return;

		float xSpeed = 0;

		if (left)
			xSpeed -= playerSpeed;
		if (right)
			xSpeed += playerSpeed;

		if (!inAir)
			if (!isEntityOnFloor(hitbox, lvlData))
				inAir = true;

		if (inAir) {
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += gravity;
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

	private void loadAnimations() {
		animations = new BufferedImage[LoadSave.PLAYER_ATLAS.length][];

		for (int i = 0; i < animations.length; i++) {

			BufferedImage spriteSheet = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS[i]);

			animations[i] = loadAnimRow(spriteSheet, GetSpriteAmount(i));

		}
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
		up = false;
		down = false;
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

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public void setJump(boolean jump) {
		this.jump = jump;
	}
}
