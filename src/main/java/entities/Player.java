package entities;

import static utilz.Constants.Directions.*;
import static utilz.Constants.PlayerConstants.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 15;
	private int playerAction = IDLE;
	private boolean moving = false, attacking = false;
	private int comboTick = 0, attackType = ATTACK_1;
	private boolean left, up, right, down;
	private float playerSpeed = 2.0f;

	public Player(float x, float y) {
		super(x, y);
		loadAnimations();
	}

	public void update() {

		updatePos();
		updateAnimationTick();
		setAnimation();

	}

	public void render(Graphics g) {

		g.drawImage(animations[playerAction][aniIndex], (int) x, (int) y, 432, 432, null);

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

		if (left && !right) {
			x -= playerSpeed;
			moving = true;
		} else if (right && !left) {
			x += playerSpeed;
			moving = true;
		}

		if (up && !down) {
			y -= playerSpeed;
			moving = true;
		} else if (down && !up) {
			y += playerSpeed;
			moving = true;
		}

	}

	private void loadAnimations() {
		animations = new BufferedImage[9][];

		animations[IDLE] = loadAnimRow("/entities/player/Idle.png", GetSpriteAmount(IDLE));
		animations[RUNNING] = loadAnimRow("/entities/player/Run.png", GetSpriteAmount(RUNNING));
		animations[JUMPING] = loadAnimRow("/entities/player/Jump.png", GetSpriteAmount(JUMPING));
		animations[FALLING] = loadAnimRow("/entities/player/Fall.png", GetSpriteAmount(FALLING));
		animations[DASHING] = loadAnimRow("/entities/player/Dash.png", GetSpriteAmount(DASHING));
		animations[DEATH] = loadAnimRow("/entities/player/Death.png", GetSpriteAmount(DEATH));
		animations[HURT] = loadAnimRow("/entities/player/Hurt.png", GetSpriteAmount(HURT));
		animations[ATTACK_1] = loadAnimRow("/entities/player/Attack 1.png", GetSpriteAmount(ATTACK_1));
		animations[ATTACK_2] = loadAnimRow("/entities/player/Attack 2.png", GetSpriteAmount(ATTACK_2));
	}

	private BufferedImage[] loadAnimRow(String path, int frameCount) {
		BufferedImage[] tempRow = new BufferedImage[frameCount];
		try (InputStream is = getClass().getResourceAsStream(path)) {
			BufferedImage fullSheet = ImageIO.read(is);

			for (int i = 0; i < frameCount; i++) {
				tempRow[i] = fullSheet.getSubimage(i * 144, 0, 144, 144);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempRow;
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

}
