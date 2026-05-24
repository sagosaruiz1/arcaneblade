package objects;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class LightningStrike {

	private float x, y;
	private BufferedImage[] frames;
	private int aniIndex = 0;
	private int aniTick = 0;
	private int aniSpeed = 15;
	private boolean active = true;
	private boolean damageDealt = false;
	
	private int flipW;

	public LightningStrike(float x, float y, BufferedImage[] frames, int flipW) {
	    this.x = x;
	    this.y = y;
	    this.frames = frames;
	    this.flipW = flipW;
	}

	public void update() {
		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= frames.length) {
				active = false;
				aniIndex = frames.length - 1;
			}
		}
	}

	public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
	    if (!active) return;
	    int drawX = (int)(x - xLvlOffset) - 64;
	    int drawY = (int)(y - yLvlOffset) - 250;
	    g.drawImage(frames[aniIndex], drawX, drawY, 128, 320, null);
	}

	public boolean isActive() {
		return active;
	}

	public boolean shouldDealDamage() {
		return !damageDealt && aniIndex >= 2;
	}

	public void setDamageDealt() {
		damageDealt = true;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
}
