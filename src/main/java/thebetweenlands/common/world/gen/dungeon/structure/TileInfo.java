package thebetweenlands.common.world.gen.dungeon.structure;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;

public class TileInfo {
	public static enum Shape {
		/**
		 * <pre>
		 * rotation 0:
		 * +X
		 * |
		 * |   ∧
		 * |_______ +Z
		 * 
		 * rotation 1:
		 * +X
		 * |
		 * |   <
		 * |_______ +Z
		 * 
		 * rotation 2:
		 * +X
		 * |
		 * |   ∨
		 * |_______ +Z
		 * 
		 * rotation 3:
		 * +X
		 * |
		 * |   >
		 * |_______ +Z
		 * </pre>
		 */
		DEADEND,

		/**
		 * <pre>
		 * rotation 0:
		 * +X
		 * |
		 * |   │
		 * |_______ +Z
		 * 
		 * rotation 1:
		 * +X
		 * |
		 * |   ─
		 * |_______ +Z
		 * 
		 * rotation 2:
		 * +X
		 * |
		 * |   │
		 * |_______ +Z
		 * 
		 * rotation 3:
		 * +X
		 * |
		 * |   ─
		 * |_______ +Z
		 * </pre>
		 */
		CORRIDOR,

		/**
		 * <pre>
		 * rotation 0:
		 * +X
		 * |
		 * |   └
		 * |_______ +Z
		 * 
		 * rotation 1:
		 * +X
		 * |
		 * |   ┘
		 * |_______ +Z
		 * 
		 * rotation 2:
		 * +X
		 * |
		 * |   ┐
		 * |_______ +Z
		 * 
		 * rotation 3:
		 * +X
		 * |
		 * |   ┌
		 * |_______ +Z
		 * </pre>
		 */
		CORNER,

		/**
		 * <pre>
		 * rotation 0:
		 * +X
		 * |
		 * |   ├
		 * |_______ +Z
		 * 
		 * rotation 1:
		 * +X
		 * |
		 * |   ┴
		 * |_______ +Z
		 * 
		 * rotation 2:
		 * +X
		 * |
		 * |   ┤
		 * |_______ +Z
		 * 
		 * rotation 3:
		 * +X
		 * |
		 * |   ┬
		 * |_______ +Z
		 * </pre>
		 */
		JUNCTION,

		/**
		 * <pre>
		 * rotaton 0:
		 * +X
		 * |
		 * |   ┼
		 * |_______ +Z
		 * 
		 * rotaton 1:
		 * +X
		 * |
		 * |   ┼
		 * |_______ +Z
		 * 
		 * rotaton 2:
		 * +X
		 * |
		 * |   ┼
		 * |_______ +Z
		 * 
		 * rotaton 3:
		 * +X
		 * |
		 * |   ┼
		 * |_______ +Z
		 * </pre>
		 */
		CROSS,

		/**
		 * <pre>
		 * rotation 0:
		 * +X
		 * |
		 * |   │
		 * |_______ +Z
		 * 
		 * rotation 1:
		 * +X
		 * |
		 * |   ─
		 * |_______ +Z
		 * 
		 * rotation 2:
		 * +X
		 * |
		 * |   │
		 * |_______ +Z
		 * 
		 * rotation 3:
		 * +X
		 * |
		 * |   ─
		 * |_______ +Z
		 * </pre>
		 */
		STAIRS,

		INVALID;

		public static final List<TileInfo.Shape> CORRIDOR_SHAPES = ImmutableList.of(CORRIDOR, CORNER, STAIRS);
	}

	public static class Door {
		public final int x, y, z;
		public final Direction dir;

		private Door(int x, int y, int z, Direction dir) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.dir = dir;
		}
	}

	private final boolean isStairs;
	private final int sizeX, sizeY, sizeZ;
	private final int length;
	private final Map<Direction, Door> doors = new HashMap<>();

	private Shape shape = Shape.INVALID;
	private Set<Integer> rotations = new HashSet<>();

	private TileInfo(int sizeX, int sizeY, int sizeZ) {
		this.isStairs = false;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		this.length = 1;
	}

	private TileInfo(int length, Direction dir) {
		this.isStairs = true;
		this.sizeX = Math.abs(dir.x) * length;
		this.sizeY = 2;
		this.sizeZ = Math.abs(dir.z) * length;
		this.length = length;
		switch(dir) {
		case NEG_X:
			this.doors.put(Direction.POS_X, new Door(this.sizeX - 1, 0, 0, Direction.POS_X));
			this.doors.put(Direction.NEG_X, new Door(0, 1, 0, Direction.NEG_X));
			break;
		case POS_X:
			this.doors.put(Direction.NEG_X, new Door(0, 0, 0, Direction.NEG_X));
			this.doors.put(Direction.POS_X, new Door(this.sizeX - 1, 1, 0, Direction.POS_X));
			break;
		case NEG_Z:
			this.doors.put(Direction.POS_Z, new Door(0, 0, this.sizeZ - 1, Direction.POS_Z));
			this.doors.put(Direction.NEG_Z, new Door(0, 1, 0, Direction.NEG_Z));
			break;
		case POS_Z:
			this.doors.put(Direction.NEG_Z, new Door(0, 0, 0, Direction.NEG_Z));
			this.doors.put(Direction.POS_Z, new Door(0, 1, this.sizeZ - 1, Direction.POS_Z));
			break;
		default:
			throw new IllegalStateException("Only POS_X, NEG_X, POS_Z and NEG_Z directions are valid for stairs");
		}
		this.categorize();
	}

	public static TileInfo room(int sizeX, int sizeY, int sizeZ) {
		return new TileInfo(sizeX, sizeY, sizeZ);
	}

	public static TileInfo stairs(int length, Direction dir) {
		return new TileInfo(length, dir);
	}

	public Shape getShape() {
		return this.shape;
	}

	public Set<Integer> getRotations() {
		return Collections.unmodifiableSet(this.rotations);
	}

	public TileInfo addDoor(int x, int y, int z, Direction dir) {
		if(this.isStairs) {
			throw new IllegalStateException("Cannot add doors to stairs");
		}
		this.doors.put(dir, new Door(x, y, z, dir));
		this.categorize();
		return this;
	}

	@Nullable
	public Door getDoor(Direction dir) {
		return this.doors.get(dir);
	}

	private void categorize() {
		this.shape = Shape.INVALID;
		this.rotations = new HashSet<>();

		Set<Direction> dirs = this.doors.keySet();

		if(dirs.contains(Direction.POS_Y) || dirs.contains(Direction.NEG_Y)) {
			return;
		}

		if(this.isStairs) {
			this.shape = Shape.STAIRS;
			Door doorpx = this.getDoor(Direction.POS_X);
			if(doorpx != null) {
				if(doorpx.y == 1) {
					this.rotations.add(0);
				} else {
					this.rotations.add(2);
				}
			} else {
				Door doorpz = this.getDoor(Direction.POS_Z);
				if(doorpz.y == 1) {
					this.rotations.add(3);
				} else {
					this.rotations.add(1);
				}
			}
		} else {
			switch(dirs.size()) {
			case 0:
				this.shape = Shape.INVALID;
				return;
			case 1:
				this.shape = Shape.DEADEND;
				if(dirs.contains(Direction.POS_X)) {
					this.rotations.add(0);
				} else if(dirs.contains(Direction.NEG_Z)) {
					this.rotations.add(1);
				} else if(dirs.contains(Direction.NEG_X)) {
					this.rotations.add(2);
				} else {
					this.rotations.add(3);
				}
				return;
			case 2:
				if(dirs.contains(Direction.POS_X) && dirs.contains(Direction.NEG_X)) {
					this.shape = Shape.CORRIDOR;
					this.rotations.add(0);
					this.rotations.add(2);
				} else if(dirs.contains(Direction.POS_Z) && dirs.contains(Direction.NEG_Z)) {
					this.shape = Shape.CORRIDOR;
					this.rotations.add(1);
					this.rotations.add(3);
				} else {
					this.shape = Shape.CORNER;
					if(dirs.contains(Direction.POS_X)) {
						if(dirs.contains(Direction.POS_Z)) {
							this.rotations.add(0);
						} else {
							this.rotations.add(1);
						}
					} else if(dirs.contains(Direction.NEG_X)) {
						if(dirs.contains(Direction.POS_Z)) {
							this.rotations.add(3);
						} else {
							this.rotations.add(2);
						}
					} else if(dirs.contains(Direction.POS_Z)) {
						if(dirs.contains(Direction.POS_X)) {
							this.rotations.add(0);
						} else {
							this.rotations.add(3);
						}
					} else {
						if(dirs.contains(Direction.POS_X)) {
							this.rotations.add(1);
						} else {
							this.rotations.add(2);
						}
					}
				}
				return;
			case 3:
				this.shape = Shape.JUNCTION;
				if(dirs.contains(Direction.POS_X) && dirs.contains(Direction.NEG_X)) {
					if(dirs.contains(Direction.POS_Z)) {
						this.rotations.add(0);
					} else {
						this.rotations.add(2);
					}
				} else {
					if(dirs.contains(Direction.POS_X)) {
						this.rotations.add(1);
					} else {
						this.rotations.add(3);
					}
				}
				return;
			case 4:
				this.shape = Shape.CROSS;
				this.rotations.add(0);
				this.rotations.add(1);
				this.rotations.add(2);
				this.rotations.add(3);
				return;
			}
		}
	}
}
