package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;

public class RandomPlacementStrategy extends PlacementStrategy {

	private Map<IGridNodeHandle, Set<Direction>> connections = new HashMap<>();

	private void addConnection(IGridNodeHandle node, Direction dir) {
		Set<Direction> set = this.connections.get(node);
		if(set == null) {
			this.connections.put(node, set = new HashSet<>());
		}
		set.add(dir);
	}

	private boolean canConnect(IGridNodeHandle node, Direction dir) {
		if(node.getSourceConnection() == dir) {
			return false;
		}
		Set<Direction> set = this.connections.get(node);
		return set == null || !set.contains(dir);
	}

	@Override
	public Placement findPlacement(IGridNodeHandle start, Node graphNode, int index, INodeGridHandle grid, PlacementContext context, Random rng) {
		if(context.isStructured()) {
			Pos startPos = start.getPos();

			int startDir = rng.nextInt(5);
			for(int dir = startDir; dir < startDir + 5; dir++) {
				Direction offset;
				switch(dir % 5) {
				default:
				case 0:
					offset = Direction.POS_X;
					break;
				case 1:
					offset = Direction.POS_Z;
					break;
				case 2:
					offset = Direction.NEG_X;
					break;
				case 3:
					offset = Direction.NEG_Z;
					break;
				case 4:
					offset = Direction.NEG_Y;
					break;
				}

				Pos offsetPos = new Pos(startPos.x + offset.x, startPos.y + offset.y, startPos.z + offset.z);
				if(context.isInGenerationSpace(start, offsetPos)) {
					if(offset == Direction.NEG_Y) {
						int startDir2 = rng.nextInt(4);
						for(int dir2 = startDir2; dir2 < startDir2 + 4; dir2++) {
							Direction offset2;
							switch(dir2 % 4) {
							default:
							case 0:
								offset2 = Direction.POS_X;
								break;
							case 1:
								offset2 = Direction.POS_Z;
								break;
							case 2:
								offset2 = Direction.NEG_X;
								break;
							case 3:
								offset2 = Direction.NEG_Z;
								break;
							}

							if(this.canConnect(start, offset2)) {
								this.addConnection(start, offset2);
								return new Placement(start, offset, offset2.opposite());
							}
						}
					} else if(this.canConnect(start, offset)) {
						this.addConnection(start, offset);
						return new Placement(start, offset);
					}
				}
			}
		}

		List<IGridNodeHandle> attachPoints = new ArrayList<>();
		for(IGridNodeHandle node : grid.get()) {
			if(node.getIndex() < graphNode.getID()) {
				attachPoints.add(node);
			}
		}
		Collections.shuffle(attachPoints, rng);
		for(IGridNodeHandle node : attachPoints) {
			Pos startPos = node.getPos();

			int startDir = rng.nextInt(5);
			for(int dir = startDir; dir < startDir + 5; dir++) {
				Direction offset;
				switch(dir % 5) {
				default:
				case 0:
					offset = Direction.POS_X;
					break;
				case 1:
					offset = Direction.POS_Z;
					break;
				case 2:
					offset = Direction.NEG_X;
					break;
				case 3:
					offset = Direction.NEG_Z;
					break;
				case 4:
					offset = Direction.NEG_Y;
					break;
				}

				Pos offsetPos = new Pos(startPos.x + offset.x, startPos.y + offset.y, startPos.z + offset.z);
				if(context.isInGenerationSpace(node, offsetPos)) {
					if(offset == Direction.NEG_Y) {
						int startDir2 = rng.nextInt(4);
						for(int dir2 = startDir2; dir2 < startDir2 + 4; dir2++) {
							Direction offset2;
							switch(dir2 % 4) {
							default:
							case 0:
								offset2 = Direction.POS_X;
								break;
							case 1:
								offset2 = Direction.POS_Z;
								break;
							case 2:
								offset2 = Direction.NEG_X;
								break;
							case 3:
								offset2 = Direction.NEG_Z;
								break;
							}

							if(this.canConnect(node, offset2)) {
								this.addConnection(node, offset2);
								return new Placement(node, offset, offset2.opposite());
							}
						}
					} else if(this.canConnect(node, offset)) {
						this.addConnection(node, offset);
						return new Placement(node, offset);
					}
				}
			}
		}

		throw new IllegalStateException();
	}

}
