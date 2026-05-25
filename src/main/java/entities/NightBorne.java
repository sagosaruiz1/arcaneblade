package entities;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.Directions.*;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static utilz.HelpMethods.isEntityOnFloor;
import static utilz.HelpMethods.isFloor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import io.arcaneblade.Game;

public class NightBorne extends Enemy {

	// ATTACK BOX
	private Rectangle2D.Float attackBox;
	private int attackBoxOffsetX;

	public NightBorne(float x, float y) {
		super(x, y, NIGHTBORNE_WIDTH, NIGHTBORNE_HEIGHT, NIGHTBORNE);
		initHitbox(x, y, (int) (30 * Game.SCALE), (int) (22 * Game.SCALE));
		initAttackBox();
		
	}

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (60 * Game.SCALE), (int) (25 * Game.SCALE));
		attackBoxOffsetX = (int) (Game.SCALE * 40);
	}

	public void update(int[][] lvlData, Player player) {
		updateBehaviour(lvlData, player);
		updateAnimationTick(player);
		updateAttackBox();
	}

	private void updateAttackBox() {
//		if (walkDir == LEFT)
//			attackBox.x = hitbox.x - attackBox.width + (int)(60*Game.SCALE);
//		else
//			attackBox.x = hitbox.x - (int)(-100 * Game.SCALE);
//
//		attackBox.y = hitbox.y;
		
		// AFTER — clean simple logic:
		if (walkDir == LEFT)
		    attackBox.x = hitbox.x - attackBox.width;
		else
		    attackBox.x = hitbox.x + hitbox.width;

		attackBox.y = hitbox.y + (hitbox.height / 2) - (attackBox.height / 2);
	}

	private void updateBehaviour(int[][] lvlData, Player player) {
		if (firstUpdate)
			firstUpdateCheck(lvlData);

		if (inAir)
			updateInAir(lvlData);
		else {
			switch (enemyState) {
			case IDLE:
				newState(RUNNING);
				break;
			case RUNNING:
				if (canSeePlayer(lvlData, player))
					turnTowardsPlayer(player);
				if (isPlayerCloseForAttack(player))
					newState(ATTACK);
				move(lvlData);
				break;
			case ATTACK:
				if (aniIndex == 0)
					attackChecked = false;

				if (aniIndex == 10 && !attackChecked)
					checkEnemyHit(attackBox, player);
				break;
			case HURT:
				break;
			}
		}
	}

	
	// DRAW ATTACKBOX
//	public void drawAttackBox(Graphics g, int xLvlOffset, int yLvlOffset) {
//		g.setColor(Color.red);
//		g.drawRect((int) (attackBox.x - xLvlOffset),
//				(int) attackBox.y - yLvlOffset,
//				(int) attackBox.width,
//				(int) attackBox.height);
//		
//		g.setColor(Color.black);
//	    g.drawRect((int) (hitbox.x - xLvlOffset),
//	    		(int) hitbox.y - yLvlOffset,
//	    		(int) hitbox.width, (int) hitbox.height);
//	}

	public int flipX() {
		if (walkDir == LEFT)
			return NIGHTBORNE_WIDTH;
		else
			return 0;
	}

	public int flipW() {
		if (walkDir == LEFT)
			return -1;
		else
			return 1;
	}

	@Override
	protected boolean isPlayerCloseForAttack(Player player) {
		return attackBox.intersects(player.getHitbox());
	}

	public void resetEnemy() {
		hitbox.x = x;
		hitbox.y = y;
		currentHealth = maxHealth;
		active = true;
		newState(IDLE);
		firstUpdate = true;
		airSpeed = 0;
	}
}
