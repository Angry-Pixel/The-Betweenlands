package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.Collection;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.GridNode;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;

public interface INodeGrid extends INodeGridHandle {
	public default IGridNodeHandle set(int x, int y, int z, Node node, int index) {
		return this.set(new Pos(x, y, z), node, index);
	}

	public GridNode set(Pos pos, Node graphNode, int index);

	public default void remove(int x, int y, int z) {
		this.remove(new Pos(x, y, z));
	}

	public default void remove(IGridNodeHandle node) {
		this.remove(node.getPos());
	}

	public void remove(Pos pos);

	public void remove(Node graphNode);
}
