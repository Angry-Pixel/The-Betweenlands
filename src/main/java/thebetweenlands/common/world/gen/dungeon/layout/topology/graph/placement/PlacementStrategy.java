package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.Random;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;

public abstract class PlacementStrategy {
	public static class Placement {
		public static final Placement ORIGIN = new Placement(null, null) {
			@Override
			public Pos getPos() {
				return new Pos(0, 0, 0);
			}
		};

		@Nullable
		public final IGridNodeHandle source;

		@Nullable
		public final Direction offset;

		@Nullable
		public final Direction connection;

		protected Placement(IGridNodeHandle source, Direction offset) {
			this(source, offset, null);
		}

		protected Placement(IGridNodeHandle source, Direction offset, Direction connection) {
			this.source = source;
			this.offset = offset;
			this.connection = connection == null ? (offset == null ? null : offset.opposite()) : connection;
		}

		public Pos getPos() {
			Pos sourcePos = this.source.getPos();
			return new Pos(sourcePos.x + this.offset.x, sourcePos.y + this.offset.y, sourcePos.z + this.offset.z);
		}
	}

	public abstract Placement findPlacement(IGridNodeHandle start, Node graphNode, int index, INodeGridHandle grid, PlacementContext context, Random rng);
}
