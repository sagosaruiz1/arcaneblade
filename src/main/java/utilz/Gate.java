package utilz;

import java.awt.Point;

public class Gate {
	public int id;
	public Point trigger;
	public Point spawn;
	public boolean vertical;

	public Gate(int id, Point trigger, Point spawn, boolean vertical) {
		this.id = id;
		this.trigger = trigger;
		this.spawn = spawn;
		this.vertical = vertical;
	}
}