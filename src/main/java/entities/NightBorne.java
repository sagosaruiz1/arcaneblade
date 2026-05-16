package entities;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;
import static utilz.HelpMethods.isEntityOnFloor;
import static utilz.HelpMethods.isFloor;

import io.arcaneblade.Game;

public class NightBorne extends Enemy {

	public NightBorne(float x, float y) {
		super(x, y, NIGHTBORNE_WIDTH, NIGHTBORNE_HEIGHT, NIGHTBORNE);
		initHitbox(x, y, (int)(20 * Game.SCALE), (int)(25 * Game.SCALE));
		
		
	}
	
	public void update(int[][] lvlData) {
		updateMove(lvlData);
		updateAnimationTick();
	}


	private void updateMove(int[][] lvlData) {
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
				move(lvlData);
				break;
			}
		}
	}
}
