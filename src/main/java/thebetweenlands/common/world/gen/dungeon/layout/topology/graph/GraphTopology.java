package thebetweenlands.common.world.gen.dungeon.layout.topology.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.topology.Topology;
import thebetweenlands.common.world.gen.dungeon.layout.topology.TopologyMeta;
import thebetweenlands.common.world.gen.dungeon.structure.Tileset;

public class GraphTopology extends Topology<TopologyMeta> {

	private static class Node {
		private final Set<Node> outEdges = new HashSet<>();
		private final Set<Node> inEdges = new HashSet<>();

		public void connect(Node other) {
			this.outEdges.add(other);
			other.inEdges.add(this);
		}

		public Set<Node> getOutgoing() {
			return Collections.unmodifiableSet(this.outEdges);
		}

		public Set<Node> getIncoming() {
			return Collections.unmodifiableSet(this.inEdges);
		}
	}

	private static class TilesetNode extends Node {
		private final Tileset tileset;

		public TilesetNode(Tileset tileset) {
			this.tileset = tileset;
		}

		public Tileset getTileset() {
			return this.tileset;
		}
	}

	private static class Path extends TilesetNode {

		//TODO Information about length, floors, start/end range (on start/end paths), etc.

		public Path(Tileset tileset) {
			super(tileset);
		}

	}

	private static class Room extends TilesetNode {

		public Room(Tileset tileset) {
			super(tileset);
		}

	}

	@Override
	public void init(Grid grid, Random rng, TagSupplier tagSupplier) {
		super.init(grid, rng, tagSupplier);

		this.setMeta(TopologyMeta.class, TopologyMeta::new, false);
	}

	@Override
	public boolean create() {
		return false;
	}

}
