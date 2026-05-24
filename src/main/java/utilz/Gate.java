package utilz;

import java.awt.Point;

public class Gate {
	public int id;
	public Point trigger;
	public Point spawn;
	public boolean vertical;
	public float glowOffsetX;
	public float glowOffsetY;
	public float glowW;
	public float glowH;
	public float glowRotation;

	public Gate(int id, Point trigger, Point spawn, boolean vertical, float glowOffsetX, float glowOffsetY, float glowW,
			float glowH, float glowRotation) {
		this.id = id;
		this.trigger = trigger;
		this.spawn = spawn;
		this.vertical = vertical;
		this.glowOffsetX = glowOffsetX;
		this.glowOffsetY = glowOffsetY;
		this.glowW = glowW;
		this.glowH = glowH;
		this.glowRotation = glowRotation;
	}
}