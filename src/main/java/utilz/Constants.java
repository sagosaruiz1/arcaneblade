package utilz;

public class Constants {
	
	public static class Directions {
		public static final int LEFT = 0;
		public static final int UP = 1;
		public static final int RIGHT = 2;
		public static final int DOWN = 3;
	}

	public static class PlayerConstants {
		public static final int RUNNING = 0;
		public static final int IDLE = 1;
		public static final int JUMPING = 2;
		public static final int FALLING = 3;
		public static final int DASHING = 4;
		public static final int DEATH = 5;
		public static final int HURT = 6;
		public static final int ATTACK_1 = 7;
		public static final int ATTACK_2 = 8;

		public static int GetSpriteAmount(int player_action) {

			switch (player_action) {

			case IDLE:
				return 7;
			case RUNNING:
				return 8;
			case JUMPING:
				return 4;
			case FALLING:
				return 4;
			case DASHING:
				return 12;
			case DEATH:
				return 18;
			case HURT:
				return 3;
			case ATTACK_1:
				return 10;
			case ATTACK_2:
				return 15;

			default:
				return 1;
			}

		}

	}

}
