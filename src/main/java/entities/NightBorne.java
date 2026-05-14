package entities;

import static utilz.Constants.EnemyConstants.*;

import io.arcaneblade.Game;

public class NightBorne extends Enemy {

	public NightBorne(float x, float y) {
		super(x, y, NIGHTBORNE_WIDTH, NIGHTBORNE_HEIGHT, NIGHTBORNE);
		initHitbox(x, y, (int)(20 * Game.SCALE), (int)(25 * Game.SCALE));
		
		
	}

}
