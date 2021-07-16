package thebetweenlands.common.world.gen.dungeon.layout.topology;

import thebetweenlands.common.world.gen.dungeon.layout.grid.GridObject;
import thebetweenlands.common.world.gen.dungeon.structure.TileInfo;

public class TopologyMeta {
	//TODO Store which specific tile/room instance the GridObject represents,
	//needed for generation later
	public TileInfo info;
	
	public TopologyMeta(GridObject obj) { }
}
