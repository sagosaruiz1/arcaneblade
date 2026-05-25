package utilz;

import io.arcaneblade.Game;

public class Constants {

	public static final float GRAVITY = 0.04f * Game.SCALE;
	public static final int ANI_SPEED = 25;

	public static class ObjectConstants {
		public static final int HEALTH_POTION = 0;
		public static final int ENERGY_POTION = 1;
		public static final int BARREL = 2;
		public static final int BOX = 3;
		public static final int SPIKE = 4;

		public static final int HEALTH_POTION_VALUE = 15;
		public static final int ENERGY_POTION_VALUE = 30;

		public static final int CONTAINER_WIDTH_DEFAULT = 40;
		public static final int CONTAINER_HEIGHT_DEFAULT = 30;
		public static final int CONTAINER_WIDTH = (int) (Game.SCALE * CONTAINER_WIDTH_DEFAULT);
		public static final int CONTAINER_HEIGHT = (int) (Game.SCALE * CONTAINER_HEIGHT_DEFAULT);

		public static final int POTION_WIDTH_DEFAULT = 12;
		public static final int POTION_HEIGHT_DEFAULT = 16;
		public static final int POTION_WIDTH = (int) (Game.SCALE * POTION_WIDTH_DEFAULT);
		public static final int POTION_HEIGHT = (int) (Game.SCALE * POTION_HEIGHT_DEFAULT);

		public static final int SPIKE_WIDTH_DEFAULT = 32;
		public static final int SPIKE_HEIGHT_DEFAULT = 32;
		public static final int SPIKE_WIDTH = (int) (Game.SCALE * SPIKE_WIDTH_DEFAULT);
		public static final int SPIKE_HEIGHT = (int) (Game.SCALE * SPIKE_HEIGHT_DEFAULT);

		public static int GetSpriteAmount(int object_type) {
			switch (object_type) {
			case HEALTH_POTION, ENERGY_POTION:
				return 7;
			case BARREL, BOX:
				return 8;
			}
			return 1;
		}
	}

	public static class LightningConstants {
		public static final int LIGHTNING_WIDTH_DEFAULT = 64;
		public static final int LIGHTNING_HEIGHT_DEFAULT = 160;
		public static final int LIGHTNING_FRAMES = 8;
		public static final int LIGHTNING_RANGE = 8 * Game.TILES_SIZE;
		public static final int LIGHTNING_DAMAGE = 10;
		public static final int LIGHTNING_COOLDOWN = 180;
	}

	public static class EnemyConstants {
		public static final int NIGHTBORNE = 0;

		public static final int IDLE = 0;
		public static final int RUNNING = 1;
		public static final int ATTACK = 2;
		public static final int HURT = 3;
		public static final int DEAD = 4;

		public static final int NIGHTBORNE_WIDTH_DEFAULT = 80;
		public static final int NIGHTBORNE_HEIGHT_DEFAULT = 80;

		public static final int NIGHTBORNE_WIDTH = (int) (NIGHTBORNE_WIDTH_DEFAULT * Game.SCALE * 2);
		public static final int NIGHTBORNE_HEIGHT = (int) (NIGHTBORNE_HEIGHT_DEFAULT * Game.SCALE * 2);

//		public static final int NB_DRAWOFFSET_X = (int) (34 * Game.SCALE);
//		public static final int NB_DRAWOFFSET_Y = (int) (36 * Game.SCALE);
		public static final int NB_DRAWOFFSET_X = (int) (70 * Game.SCALE);
		public static final int NB_DRAWOFFSET_Y = (int) (100 * Game.SCALE);

		public static int GetSpriteAmount(int enemy_type, int enemy_state) {

			switch (enemy_type) {
			case NIGHTBORNE:
				switch (enemy_state) {
				case IDLE:
					return 9;
				case RUNNING:
					return 6;
				case ATTACK:
					return 12;
				case HURT:
					return 5;
				case DEAD:
					return 23;
				}
			}

			return 0;
		}

		public static int GetMaxHealth(int enemy_type) {
			switch (enemy_type) {
			case NIGHTBORNE:
				return 10;
			default:
				return 1;
			}
		}

		public static int GetEnemyDmg(int enemy_type) {
			switch (enemy_type) {
			case NIGHTBORNE:
				return 50;
			default:
				return 0;
			}
		}
	}

	public static class Environment {
		public static final int PILLARS_WIDTH_DEFAULT = 500;
		public static final int PILLARS_HEIGHT_DEFAULT = 110;

		public static final int PILLARS_WIDTH = (int) (PILLARS_WIDTH_DEFAULT * Game.SCALE);
		public static final int PILLARS_HEIGHT = (int) (PILLARS_HEIGHT_DEFAULT * Game.SCALE);
	}

	public static class UI {
		public static class Buttons {
			public static final int B_WIDTH_DEFAULT = 140;
			public static final int B_HEIGHT_DEFAULT = 56;
			public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
			public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
		}

		public static class PauseButtons {
			public static final int SOUND_SIZE_DEFAULT = 42;
			public static final int SOUND_SIZE = (int) (SOUND_SIZE_DEFAULT * Game.SCALE);
		}

		public static class URMButtons {
			public static final int URM_DEFAULT_SIZE = 56;
			public static final int URM_SIZE = (int) (URM_DEFAULT_SIZE * Game.SCALE);
		}

		public static class VolumeButtons {
			public static final int VOLUME_DEFAULT_WIDTH = 28;
			public static final int VOLUME_DEFAULT_HEIGHT = 44;
			public static final int SLIDER_DEFAULT_WIDTH = 215;

			public static final int VOLUME_WIDTH = (int) (VOLUME_DEFAULT_WIDTH * Game.SCALE);
			public static final int VOLUME_HEIGHT = (int) (VOLUME_DEFAULT_HEIGHT * Game.SCALE);
			public static final int SLIDER_WIDTH = (int) (SLIDER_DEFAULT_WIDTH * Game.SCALE);
		}
	}

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

		public static int GetAniSpeed(int player_action) {
			switch (player_action) {
			case IDLE:
			case RUNNING:
			case JUMPING:
			case FALLING:
			case DEATH:
				return 15;
			case DASHING:
			case ATTACK_1:
			case ATTACK_2:
				return 10;
			case HURT:
				return 20;

			default:
				return 1;
			}
		}

	}

}
