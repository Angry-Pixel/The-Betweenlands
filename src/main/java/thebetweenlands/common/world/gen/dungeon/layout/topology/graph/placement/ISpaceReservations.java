package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;

public interface ISpaceReservations {
	public default boolean canReserve(int x, int y, int z) {
		return this.canReserve(new Pos(x, y, z));
	}
	
	public boolean canReserve(Pos pos);
	
	public default boolean reserve(int x, int y, int z, boolean exclusive) {
		return this.reserve(new Pos(x, y, z), exclusive);
	}
	
	public boolean reserve(Pos pos, boolean exclusive);
}
