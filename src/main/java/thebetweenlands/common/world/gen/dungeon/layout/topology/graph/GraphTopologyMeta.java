package thebetweenlands.common.world.gen.dungeon.layout.topology.graph;

import thebetweenlands.common.world.gen.dungeon.layout.grid.GridObject;
import thebetweenlands.common.world.gen.dungeon.layout.topology.TopologyMeta;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.GridNode;

public class GraphTopologyMeta extends TopologyMeta {
	public GridNode node;
	
	public GraphTopologyMeta(GridObject obj) {
		super(obj);
	}
}
