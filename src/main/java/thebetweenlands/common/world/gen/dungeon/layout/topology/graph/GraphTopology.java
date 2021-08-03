package thebetweenlands.common.world.gen.dungeon.layout.topology.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Cell;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Connector;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.topology.Topology;
import thebetweenlands.common.world.gen.dungeon.layout.topology.TopologyMeta;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.GridNode;

public class GraphTopology extends Topology<GraphTopologyMeta> {

	/*private static class Node {
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

	}*/

	private final GraphNodeGrid graphGrid;

	public GraphTopology(GraphNodeGrid graphGrid) {
		this.graphGrid = graphGrid;
	}


	@Override
	public void init(Grid grid, Random rng, TagSupplier tagSupplier) {
		super.init(grid, rng, tagSupplier);

		this.setMeta(GraphTopologyMeta.class, GraphTopologyMeta::new, false);
	}

	@Override
	public boolean create() {

		Map<Cell, Direction> connectionDirs = new HashMap<>();

		for(GridNode node : this.graphGrid.get()) {
			//Cell cell = this.grid.setCell(node.getPos().x, node.getPos().y, node.getPos().z, 2 + this.rng.nextInt(4), 2 + this.rng.nextInt(2), 2 + this.rng.nextInt(4)).getTile().updateOrCreateMeta(this, meta -> meta.node = node).getCell();
			Cell cell = this.grid.setCell(node.getPos().x, node.getPos().y, node.getPos().z, 4, 2, 4).getTile().updateOrCreateMeta(this, meta -> meta.node = node).getCell();
			if(node.getSourceConnection() != null) {
				connectionDirs.put(cell, node.getSourceConnection());
			}
		}

		for(GridNode node : this.graphGrid.get()) {
			if(node.isReservedSpace()) {
				continue;
			}
			if(node.getSourcePos() != null) {
				Cell cell = this.grid.getCell(node.getPos().x, node.getPos().y, node.getPos().z);
				Cell source = this.grid.getCell(node.getSourcePos().x, node.getSourcePos().y, node.getSourcePos().z);

				int dir = 0;
				if(cell.getX() > source.getX()) {
					dir = 0;
				} else if(cell.getY() > source.getY()) {
					dir = 1;
				} else if(cell.getZ() > source.getZ()) {
					dir = 2;
				} else if(cell.getX() < source.getX()) {
					dir = 3;
				} else if(cell.getY() < source.getY()) {
					dir = 4;
				} else if(cell.getZ() < source.getZ()) {
					dir = 5;
				}

				Connector c1 = null;
				Connector c2 = null;

				/*switch(dir) {
				default:
				case 0:
					c1 = source.addTileConnector(source.getTileSizeX() - 1, this.rng.nextInt(source.getTileSizeY()), this.rng.nextInt(source.getTileSizeZ()), Direction.POS_X);
					c2 = cell.addTileConnector(0, this.rng.nextInt(cell.getTileSizeY()), this.rng.nextInt(cell.getTileSizeZ()), Direction.NEG_X);
					break;
				case 1:
					if(connectionDirs.containsKey(cell)) {
						switch(connectionDirs.get(cell)) {
						case POS_X:
							c1 = source.addTileConnector(0, this.rng.nextInt(source.getTileSizeY()), this.rng.nextInt(source.getTileSizeZ()), Direction.NEG_X);
							c2 = cell.addTileConnector(cell.getTileSizeX() - 1, this.rng.nextInt(cell.getTileSizeY()), this.rng.nextInt(cell.getTileSizeZ()), Direction.POS_X);
							break;
						case POS_Z:
							c1 = source.addTileConnector(this.rng.nextInt(source.getTileSizeX()), this.rng.nextInt(source.getTileSizeY()), 0, Direction.NEG_Z);
							c2 = cell.addTileConnector(this.rng.nextInt(cell.getTileSizeX()), this.rng.nextInt(cell.getTileSizeY()), cell.getTileSizeZ() - 1, Direction.POS_Z);
							break;
						case NEG_X:
							c1 = source.addTileConnector(source.getTileSizeX() - 1, this.rng.nextInt(source.getTileSizeY()), this.rng.nextInt(source.getTileSizeZ()), Direction.POS_X);
							c2 = cell.addTileConnector(0, this.rng.nextInt(cell.getTileSizeY()), this.rng.nextInt(cell.getTileSizeZ()), Direction.NEG_X);
							break;
						case NEG_Z:
							c1 = source.addTileConnector(this.rng.nextInt(source.getTileSizeX()), this.rng.nextInt(source.getTileSizeY()), source.getTileSizeZ() - 1, Direction.POS_Z);
							c2 = cell.addTileConnector(this.rng.nextInt(cell.getTileSizeX()), this.rng.nextInt(cell.getTileSizeY()), 0, Direction.NEG_Z);
							break;
						}
					}
					break;
				case 2:
					c1 = source.addTileConnector(this.rng.nextInt(source.getTileSizeX()), this.rng.nextInt(source.getTileSizeY()), source.getTileSizeZ() - 1, Direction.POS_Z);
					c2 = cell.addTileConnector(this.rng.nextInt(cell.getTileSizeX()), this.rng.nextInt(cell.getTileSizeY()), 0, Direction.NEG_Z);
					break;
				case 3:
					c1 = source.addTileConnector(0, this.rng.nextInt(source.getTileSizeY()), this.rng.nextInt(source.getTileSizeZ()), Direction.NEG_X);
					c2 = cell.addTileConnector(cell.getTileSizeX() - 1, this.rng.nextInt(cell.getTileSizeY()), this.rng.nextInt(cell.getTileSizeZ()), Direction.POS_X);
					break;
				case 4:
					if(connectionDirs.containsKey(cell)) {
						switch(connectionDirs.get(cell)) {
						case POS_X:
							c1 = source.addTileConnector(0, this.rng.nextInt(source.getTileSizeY()), this.rng.nextInt(source.getTileSizeZ()), Direction.NEG_X);
							c2 = cell.addTileConnector(cell.getTileSizeX() - 1, this.rng.nextInt(cell.getTileSizeY()), this.rng.nextInt(cell.getTileSizeZ()), Direction.POS_X);
							break;
						case POS_Z:
							c1 = source.addTileConnector(this.rng.nextInt(source.getTileSizeX()), this.rng.nextInt(source.getTileSizeY()), 0, Direction.NEG_Z);
							c2 = cell.addTileConnector(this.rng.nextInt(cell.getTileSizeX()), this.rng.nextInt(cell.getTileSizeY()), cell.getTileSizeZ() - 1, Direction.POS_Z);
							break;
						case NEG_X:
							c1 = source.addTileConnector(source.getTileSizeX() - 1, this.rng.nextInt(source.getTileSizeY()), this.rng.nextInt(source.getTileSizeZ()), Direction.POS_X);
							c2 = cell.addTileConnector(0, this.rng.nextInt(cell.getTileSizeY()), this.rng.nextInt(cell.getTileSizeZ()), Direction.NEG_X);
							break;
						case NEG_Z:
							c1 = source.addTileConnector(this.rng.nextInt(source.getTileSizeX()), this.rng.nextInt(source.getTileSizeY()), source.getTileSizeZ() - 1, Direction.POS_Z);
							c2 = cell.addTileConnector(this.rng.nextInt(cell.getTileSizeX()), this.rng.nextInt(cell.getTileSizeY()), 0, Direction.NEG_Z);
							break;
						}
					}
					break;
				case 5:
					c1 = source.addTileConnector(this.rng.nextInt(source.getTileSizeX()), this.rng.nextInt(source.getTileSizeY()), 0, Direction.NEG_Z);
					c2 = cell.addTileConnector(this.rng.nextInt(cell.getTileSizeX()), this.rng.nextInt(cell.getTileSizeY()), cell.getTileSizeZ() - 1, Direction.POS_Z);
					break;
				}*/
				
				switch(dir) {
				default:
				case 0:
					c1 = source.addTileConnector(source.getTileSizeX() - 1, source.getTileSizeY() / 2, source.getTileSizeZ() / 2, Direction.POS_X);
					c2 = cell.addTileConnector(0, cell.getTileSizeY() / 2, cell.getTileSizeZ() / 2, Direction.NEG_X);
					break;
				case 1:
					if(connectionDirs.containsKey(cell)) {
						switch(connectionDirs.get(cell)) {
						case POS_X:
							c1 = source.addTileConnector(0, source.getTileSizeY() / 2, source.getTileSizeZ() / 2, Direction.NEG_X);
							c2 = cell.addTileConnector(cell.getTileSizeX() - 1, cell.getTileSizeY() / 2, cell.getTileSizeZ() / 2, Direction.POS_X);
							break;
						case POS_Z:
							c1 = source.addTileConnector(source.getTileSizeX() / 2, source.getTileSizeY() / 2, 0, Direction.NEG_Z);
							c2 = cell.addTileConnector(cell.getTileSizeX() / 2, cell.getTileSizeY() / 2, cell.getTileSizeZ() - 1, Direction.POS_Z);
							break;
						case NEG_X:
							c1 = source.addTileConnector(source.getTileSizeX() - 1, source.getTileSizeY() / 2, source.getTileSizeZ() / 2, Direction.POS_X);
							c2 = cell.addTileConnector(0, cell.getTileSizeY() / 2, cell.getTileSizeZ() / 2, Direction.NEG_X);
							break;
						case NEG_Z:
							c1 = source.addTileConnector(source.getTileSizeX() / 2, source.getTileSizeY() / 2, source.getTileSizeZ() - 1, Direction.POS_Z);
							c2 = cell.addTileConnector(cell.getTileSizeX() / 2, cell.getTileSizeY() / 2, 0, Direction.NEG_Z);
							break;
						}
					}
					break;
				case 2:
					c1 = source.addTileConnector(source.getTileSizeX() / 2, source.getTileSizeY() / 2, source.getTileSizeZ() - 1, Direction.POS_Z);
					c2 = cell.addTileConnector(cell.getTileSizeX() / 2, cell.getTileSizeY() / 2, 0, Direction.NEG_Z);
					break;
				case 3:
					c1 = source.addTileConnector(0, source.getTileSizeY() / 2, source.getTileSizeZ() / 2, Direction.NEG_X);
					c2 = cell.addTileConnector(cell.getTileSizeX() - 1, cell.getTileSizeY() / 2, cell.getTileSizeZ() / 2, Direction.POS_X);
					break;
				case 4:
					if(connectionDirs.containsKey(cell)) {
						switch(connectionDirs.get(cell)) {
						case POS_X:
							c1 = source.addTileConnector(0, source.getTileSizeY() / 2, source.getTileSizeZ() / 2, Direction.NEG_X);
							c2 = cell.addTileConnector(cell.getTileSizeX() - 1, cell.getTileSizeY() / 2, cell.getTileSizeZ() / 2, Direction.POS_X);
							break;
						case POS_Z:
							c1 = source.addTileConnector(source.getTileSizeX() / 2, source.getTileSizeY() / 2, 0, Direction.NEG_Z);
							c2 = cell.addTileConnector(cell.getTileSizeX() / 2, cell.getTileSizeY() / 2, cell.getTileSizeZ() - 1, Direction.POS_Z);
							break;
						case NEG_X:
							c1 = source.addTileConnector(source.getTileSizeX() - 1, source.getTileSizeY() / 2, source.getTileSizeZ() / 2, Direction.POS_X);
							c2 = cell.addTileConnector(0, cell.getTileSizeY() / 2, cell.getTileSizeZ() / 2, Direction.NEG_X);
							break;
						case NEG_Z:
							c1 = source.addTileConnector(source.getTileSizeX() / 2, source.getTileSizeY() / 2, source.getTileSizeZ() - 1, Direction.POS_Z);
							c2 = cell.addTileConnector(cell.getTileSizeX() / 2, cell.getTileSizeY() / 2, 0, Direction.NEG_Z);
							break;
						}
					}
					break;
				case 5:
					c1 = source.addTileConnector(source.getTileSizeX() / 2, source.getTileSizeY() / 2, 0, Direction.NEG_Z);
					c2 = cell.addTileConnector(cell.getTileSizeX() / 2, cell.getTileSizeY() / 2, cell.getTileSizeZ() - 1, Direction.POS_Z);
					break;
				}

				if(c1 != null && c2 != null) {
					this.grid.connect(c1, c2, 2);
				}
			}
		}

		return true;
	}

}
