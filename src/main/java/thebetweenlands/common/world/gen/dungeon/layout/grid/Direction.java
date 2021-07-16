package thebetweenlands.common.world.gen.dungeon.layout.grid;

public enum Direction {
	POS_X(1, 0, 0),
	POS_Y(0, 1, 0),
	POS_Z(0, 0, 1),
	NEG_X(-1, 0, 0),
	NEG_Y(0, -1, 0),
	NEG_Z(0, 0, -1);

	public final int x, y, z;

	private Direction(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Direction opposite() {
		switch(this) {
		default:
		case POS_X:
			return NEG_X;
		case POS_Y:
			return NEG_Y;
		case POS_Z:
			return NEG_Z;
		case NEG_X:
			return POS_X;
		case NEG_Y:
			return POS_Y;
		case NEG_Z:
			return POS_Z;
		}
	}
}
