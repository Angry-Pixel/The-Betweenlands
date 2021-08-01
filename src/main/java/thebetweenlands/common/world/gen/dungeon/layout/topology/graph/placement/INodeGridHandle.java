package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.Collection;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;

public interface INodeGridHandle {
	@Nullable
	public default IGridNodeHandle get(int x, int y, int z) {
		return this.get(new Pos(x, y, z));
	}

	@Nullable
	public IGridNodeHandle get(Pos pos);

	@Nullable
	public IGridNodeHandle get(Node graphNode);

	public Collection<? extends IGridNodeHandle> get();
}
