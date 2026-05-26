package entities;


import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;
import static utilz.Constants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gamestates.Playing;
import io.arcaneblade.Game;
import utilz.LoadSave;

public class Player extends Entity {

	private BufferedImage[][] animations;
	private int aniTick, aniIndex;
	private int playerAction = IDLE;
	private boolean moving = false, attacking = false;
	private int attackType = ATTACK_1;
	private boolean left, right, jump;
	private float playerSpeed = 2.0f;
	private int[][] lvlData;

	// Lightning skill
	private boolean castLightning = false;
	private int lightningCooldown = 0;
	private int lightningCooldownMax = 180;

	// Hitbox
	private float xDrawOffset = 130 * Game.SCALE;
	private float yDrawOffset = 128 * Game.SCALE;

	// Gravity
	private float jumpSpeed = -1.75f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

	// Variable jump
	private boolean jumpHeld = false;
	private int jumpHeldTicks = 0;
	private int jumpsLeft = 2;
	private int maxJumpHoldTicks = 15;
	private boolean jumpPressed = false;

	// Coyote time
	private int coyoteTimer = 0;
	private int coyoteTimerMax = 6;

	// Dash
	private boolean dashing = false;
	private float dashDistance = 250f * Game.SCALE;
	private float dashProgress = 20f;
	private float dashSpeed = 0.015f;
	private int dashCooldown = 3;
	private int dashCooldownMax = 40;

	// Wall slide
	private boolean onWall = false;
	private float wallSlideSpeed = 0.5f * Game.SCALE;

	// Acceleration
	private float currentSpeedX = 0;
	private float maxSpeed = 1.2f * Game.SCALE;
	private float acceleration = 0.1f * Game.SCALE;
	private float deceleration = 2.0f * Game.SCALE;

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

	private int maxPower = 100;
	private int currentPower = maxPower;
	private int lightningManaCost = 10;

	// MANA BAR UI
	private int powerBarWidth = (int) (105 * Game.SCALE);
	private int powerBarHeight = (int) (3.4f * Game.SCALE);
	private int powerBarXStart = (int) (45 * Game.SCALE);
	private int powerBarYStart = (int) (33 * Game.SCALE);
	private int powerWidth = powerBarWidth;
	Color powerColor = new Color(50, 254, 227);

//	private int maxHealth = 100;
//	private int currentHealth = maxHealth;
	private int healthWidth = healthBarWidth;

	// ATTACK BOX
	private Rectangle2D.Float attackBox, lightningBox;

	private int flipX = 0;
	public int flipW = 1;

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

	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = spawn.x;
		hitbox.y = spawn.y;
	}

	// lightning skill
	public void castLightning() {
		if (lightningCooldown > 0)
			return;
		if (currentPower < lightningManaCost) {
			System.out.println("Not enough mana!");
			return;
		}
		castLightning = true;
		currentPower -= lightningManaCost;
		playing.getGame().getAudioPlayer().playLightningSound();
	}

	public boolean isCastingLightning() {
		if (castLightning) {
			castLightning = false;
			lightningCooldown = lightningCooldownMax;
			return true;
		}
		return false;
	}

	private void updatePowerBar() {
		powerWidth = (int) ((currentPower / (float) maxPower) * powerBarWidth);
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (50 * Game.SCALE), (int) (20 * Game.SCALE));

		lightningBox = new Rectangle2D.Float(x, y, (int) (8 * Game.TILES_SIZE), // adjust width
				(int) (2 * Game.TILES_SIZE)); // adjust height
	}

	public void update() {
		updateHealthBar();
		updatePowerBar();

		if (lightningCooldown > 0)
			lightningCooldown--;

		if (currentHealth <= 0) {
			if (playerAction != DEATH) {
				playerAction = DEATH;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
			} else {

				int speed = utilz.Constants.PlayerConstants.GetAniSpeed(playerAction);

				if (aniIndex == GetSpriteAmount(DEATH) - 1 && aniTick >= speed - 1) {
					playing.setGameOver(true);
				} else
					updateAnimationTick();
			}
			return;
		}
		updateAttackBox();

		updatePos();
		if (moving)
			checkPotionTouched();
			checkSpikesTouched();
		if (attacking)
			checkAttack();
		updateAnimationTick();
		setAnimation();
	}

	private void checkSpikesTouched() {
		playing.checkSpikesTouched(this);
	}

	private void checkPotionTouched() {
		playing.checkPotionTouched(hitbox);
	}

	private void checkAttack() {
		if (attackChecked || aniIndex != 1)
			return;
		attackChecked = true;
		playing.checkEnemyHit(attackBox);
		playing.checkObjectHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();
	}

	private void updateAttackBox() {
		if (right) {
			attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 5);
		} else if (left) {
			attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 25);
		}
		attackBox.y = hitbox.y + (Game.SCALE * 10);

		// lightning box
		if (flipW == 1)
			lightningBox.x = hitbox.x;
		else
			lightningBox.x = hitbox.x - (8 * Game.TILES_SIZE);
		lightningBox.y = hitbox.y - Game.TILES_SIZE;
	}

	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
	}

	public void render(Graphics g, int xLvlOffset, int yLvlOffset) {

		g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset) - xLvlOffset + flipX,
				(int) (hitbox.y - yDrawOffset) - yLvlOffset, width * flipW, height, null);

//		drawHitbox(g, lvlOffset);
//		drawAttackBox(g, lvlOffset);
		drawUI(g);
	}

//	private void drawAttackBox(Graphics g, int lvlOffsetX) {
//		g.setColor(healthColor);
//		g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
//	}

	private void drawUI(Graphics g) {
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);

		// Health bar
		g.setColor(healthColor);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);

		// Power bar
		g.setColor(powerColor);
		g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
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

		if (dashing)
			playerAction = DASHING;
		
		if(playerAction == HURT) {
			if(aniIndex >= GetSpriteAmount(HURT) - 1)
				playerAction = IDLE;
			return;
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

		// Dash cooldown tick
		if (dashCooldown > 0)
			dashCooldown--;

		// Handle dash movement
		if (dashing) {
			dashProgress += dashSpeed;

			float t = dashProgress;
			float easeOut = t * t * (3f - 2f * t);
			easeOut = 1f - easeOut; // invert so it goes fast-to-slow
			float frameMove = dashDistance * dashSpeed * easeOut;

			float dSpeed = (flipW == 1) ? frameMove : -frameMove;

			if (CanMoveHere(hitbox.x + dSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
				hitbox.x += dSpeed;
			else
				dashing = false;
			if (dashProgress >= 1f)
				dashing = false;
			moving = true;
//			return;
		}

		if (jumpPressed) {
			jump();
			jumpPressed = false;
		}

		// Acceleration / deceleration
		if (left) {
			currentSpeedX = Math.max(currentSpeedX - acceleration, -maxSpeed);
			flipX = width;
			flipW = -1;
		} else if (right) {
			currentSpeedX = Math.min(currentSpeedX + acceleration, maxSpeed);
			flipX = 0;
			flipW = 1;
		} else {
			if (currentSpeedX > 0)
				currentSpeedX = Math.min(0, currentSpeedX - deceleration);
			else if (currentSpeedX < 0)
				currentSpeedX = Math.min(0, currentSpeedX + deceleration);
		}

		// Coyote time
		if (!inAir) {
			if (!isEntityOnFloor(hitbox, lvlData)) {
				coyoteTimer++;
				if (coyoteTimer > coyoteTimerMax)
					inAir = true;
			} else {
				coyoteTimer = 0;
			}
		}

		// Wall detection
		if (inAir) {
			boolean hitWallRight = !CanMoveHere(hitbox.x + playerSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData);
			boolean hitWallLeft = !CanMoveHere(hitbox.x - playerSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData);
			onWall = (right && hitWallRight) || (left && hitWallLeft);
		} else {
			onWall = false;
		}

		// Variable jump hold
		if (jumpHeld) {
			if (jump && jumpHeldTicks < maxJumpHoldTicks) {
				airSpeed -= 0.08f * Game.SCALE;
				jumpHeldTicks++;
			} else {
				jumpHeld = false;
			}
		}

		// Wall slide - slow fall when pressing into wall
		if (inAir) {
			if (onWall && airSpeed > 0)
				airSpeed = wallSlideSpeed;

			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(currentSpeedX);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(currentSpeedX);
			}
		} else {
			updateXPos(currentSpeedX);
		}
		if (currentSpeedX != 0)
			moving = true;
	}

	private void jump() {
		if (inAir) {
			// Wall jump
			if (onWall) {
				airSpeed = jumpSpeed;
				jumpHeld = true;
				jumpHeldTicks = 0;
				if (right) {
					currentSpeedX = -maxSpeed; // push left
					flipX = width;
					flipW = -1;
				} else if (left) {
					currentSpeedX = maxSpeed; // push right
					flipX = 0;
					flipW = 1;
				}
				return;
			}
			// Double jump
			if (jumpsLeft > 0) {
				airSpeed = jumpSpeed;
				jumpHeld = true;
				jumpHeldTicks = 0;
				jumpsLeft--;
			}
			return;
		}
		// Normal jump from ground
		inAir = true;
		coyoteTimer = coyoteTimerMax + 1;
		airSpeed = jumpSpeed;
		jumpHeld = true;
		jumpHeldTicks = 0;
		jumpsLeft = 1;
	}

	public void dash() {
		if (dashCooldown > 0 || dashing)
			return;
		dashing = true;
		dashProgress = 0f;
		dashCooldown = dashCooldownMax;
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
		jumpsLeft = 2;

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
		} else if (currentHealth >= maxHealth) {
			currentHealth = maxHealth;
		} else if (value < 0) {
			playerAction = HURT;
			aniTick = 0;
			aniIndex = 0;
		}
	}
	
	public void kill() {
		currentHealth = 0;
	}

	public void changePower(int value) {
		currentPower += value;
		if (currentPower <= 0)
			currentPower = 0;
		else if (currentPower >= maxPower)
			currentPower = maxPower;
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
		jumpHeld = false;
		dashing = false;
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

	public Rectangle2D.Float getLightningBox() {
		return lightningBox;
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
		if (jump)
			jumpPressed = true;
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

		if (!isEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}


}
