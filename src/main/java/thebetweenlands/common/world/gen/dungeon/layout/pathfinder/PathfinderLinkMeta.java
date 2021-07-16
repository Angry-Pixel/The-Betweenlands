package thebetweenlands.common.world.gen.dungeon.layout.pathfinder;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;

import thebetweenlands.common.world.gen.dungeon.layout.grid.GridObject;

public class PathfinderLinkMeta {
	public static enum Shape {
		/**
		 * Straight corridor
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
		STRAIGHT,

		/**
		 * Corner corridor
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
		 * Ascending stairs corridor
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

		/**
		 * Stairs top segment
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
		STAIRS_TOP,

		/**
		 * Stairs bottom segment
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
		STAIRS_BOTTOM
	}

	public static class Piece {
		public final int x, y, z;
		public final int indx, indy, indz;
		public final int outdx, outdy, outdz;
		public final int rotation;
		public final int length;
		public final Shape shape;

		public Piece(int x, int y, int z, int indx, int indy, int indz, int outdx, int outdy, int outdz, int rotation, int length, Shape shape) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.indx = indx;
			this.indy = indy;
			this.indz = indz;
			this.outdx = outdx;
			this.outdy = outdy;
			this.outdz = outdz;
			this.rotation = rotation;
			this.length = length;
			this.shape = shape;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("x", this.x).add("y", this.y).add("z", this.z)
					.add("indx", this.indx).add("indy", this.indy).add("indz", this.indz)
					.add("outdx", this.outdx).add("outdy", this.outdy).add("outdz", this.outdz)
					.add("r", this.rotation).add("l", this.length).add("s", this.shape.name()).toString();
		}
	}

	public final List<Piece> pieces = new ArrayList<>();

	public PathfinderLinkMeta(GridObject obj) { }
}
