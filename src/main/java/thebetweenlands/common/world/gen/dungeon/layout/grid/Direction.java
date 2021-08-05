package thebetweenlands.common.world.gen.dungeon.layout.grid;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

public enum Direction {
	POS_X(1, 0, 0),
	POS_Y(0, 1, 0),
	POS_Z(0, 0, 1),
	NEG_X(-1, 0, 0),
	NEG_Y(0, -1, 0),
	NEG_Z(0, 0, -1);

	public static final List<Direction> ALL = ImmutableList.of(POS_X, POS_Y, POS_Z, NEG_X, NEG_Y, NEG_Z);
	public static final List<Direction> HORIZONTAL = ImmutableList.of(POS_X, POS_Z, NEG_X, NEG_Z);
	public static final List<Direction> X = ImmutableList.of(POS_X, NEG_X);
	public static final List<Direction> Y = ImmutableList.of(POS_Y, NEG_Y);
	public static final List<Direction> Z = ImmutableList.of(POS_Z, NEG_Z);

	public final int x, y, z;

	private Direction(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Nullable
	public static Direction of(int x, int y, int z) {
		if(x > 0) {
			return Direction.POS_X;
		} else if(x < 0) {
			return Direction.NEG_X;
		} else if(y > 0) {
			return Direction.POS_Y;
		} else if(y < 0) {
			return Direction.NEG_Y;
		} else if(z > 0) {
			return Direction.POS_Z;
		} else if(z < 0) {
			return Direction.NEG_Z;
		}
		return null;
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
