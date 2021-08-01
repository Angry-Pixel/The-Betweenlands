package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.Random;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.PlacementStrategy.Placement;

public abstract class SpaceType {
	public abstract class Instance {
		private String tag = null;

		private void setTag(String tag) {
			this.tag = tag;
		}

		@Nullable
		public final String getTag() {
			return this.tag;
		}

		public abstract boolean generate(INodeGridHandle grid, @Nullable Placement placement, ISpaceReservations reservations, Random rng);

		public void finish(INodeGrid grid) { }

		public boolean requiresRefresh(INodeGridHandle grid, @Nullable IGridNodeHandle placedNode) {
			return false;
		}
	}

	public final Instance init(String tag) {
		Instance instance = this.instantiate();
		instance.setTag(tag);
		return instance;
	}

	protected abstract Instance instantiate();
}
