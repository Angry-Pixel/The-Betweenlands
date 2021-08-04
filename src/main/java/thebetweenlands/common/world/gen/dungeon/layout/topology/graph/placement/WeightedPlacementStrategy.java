package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.ToIntFunction;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;

public class WeightedPlacementStrategy extends PlacementStrategy {

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
					if(rng.nextInt(3) == 0) {
						offset = Direction.NEG_Y;
					} else {
						switch(rng.nextInt(4)) {
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
						}
					}
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

		//TODO Make configurable
		Map<IGridNodeHandle, Integer> weights = this.evaluateWeights(grid, start, index, 100, n -> 1);

		List<BorderNode> attachPoints = new ArrayList<>();

		//TODO Make configurable
		int threshold = rng.nextInt(5);

		boolean useLowThreshold = rng.nextBoolean();

		int lowThreshold = threshold;
		int prevLowThreshold = Integer.MAX_VALUE;

		int highThreshold = threshold;
		int prevHighThreshold = Integer.MIN_VALUE;

		do {
			if(useLowThreshold) {
				prevLowThreshold = lowThreshold;
				lowThreshold = this.findBorderNodes(grid, weights, lowThreshold, context, attachPoints, true);
			} else {
				prevHighThreshold = highThreshold;
				highThreshold = this.findBorderNodes(grid, weights, highThreshold, context, attachPoints, false);
			}
			useLowThreshold = !useLowThreshold;
		} while(attachPoints.isEmpty() && (lowThreshold < prevLowThreshold || highThreshold > prevHighThreshold));

		if(!attachPoints.isEmpty()) {
			BorderNode attachPoint = attachPoints.get(rng.nextInt(attachPoints.size()));
			BorderNode.Connection connection = attachPoint.connections.get(rng.nextInt(attachPoint.connections.size()));

			IGridNodeHandle node = attachPoint.node;

			this.addConnection(node, connection.dir.opposite());

			return new Placement(node, connection.offset, connection.dir);
		}

		throw new IllegalStateException();
	}

	private static class BorderNode {
		private static class Connection {
			private final Direction offset;
			private final Direction dir;

			private Connection(Direction offset, Direction dir) {
				this.offset = offset;
				this.dir = dir;
			}
		}

		private final IGridNodeHandle node;
		private final List<Connection> connections;

		private BorderNode(IGridNodeHandle node, List<Connection> connections) {
			this.node = node;
			this.connections = connections;
		}
	}

	private int findBorderNodes(INodeGridHandle grid, Map<IGridNodeHandle, Integer> weights, int threshold, PlacementContext context, List<BorderNode> borderNodes, boolean decreaseThreshold) {
		int nextLowerThreshold = Integer.MIN_VALUE;
		int nextHigherThreshold = Integer.MAX_VALUE;

		for(Entry<IGridNodeHandle, Integer> weightsEntry : weights.entrySet()) {
			IGridNodeHandle node = weightsEntry.getKey();
			int weight = weightsEntry.getValue();

			if(weight < threshold) {
				nextLowerThreshold = Math.max(nextLowerThreshold, weight);
			} else if(weight > threshold) {
				nextHigherThreshold = Math.min(nextHigherThreshold, weight);
			}

			if(weight >= threshold) {
				Pos pos = node.getPos();
				Pos sourcePos = node.getSourcePos();

				boolean isAlone = true;
				boolean isBorder = false;
				List<BorderNode.Connection> generationSpaces = new ArrayList<>();

				for(Direction offset : Direction.values()) {
					Pos offsetPos = new Pos(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);

					IGridNodeHandle offsetNode = grid.get(offsetPos);

					if(offsetNode != null && (Objects.equals(pos, offsetNode.getSourcePos()) || Objects.equals(sourcePos, offsetPos))) {
						isAlone = false;

						if(weights.getOrDefault(offsetNode, Integer.MAX_VALUE) < threshold) {
							isBorder = true;
						}
					}

					if(context.isInGenerationSpace(node, offsetPos) && this.canConnect(node, offset)) {
						if(offset.y != 0) {
							if(offset.y < 0) {
								for(Direction horizontalOffset : Direction.values()) {
									if(horizontalOffset.y == 0 && this.canConnect(node, horizontalOffset)) {
										generationSpaces.add(new BorderNode.Connection(offset, horizontalOffset.opposite()));
										break;
									}
								}
							}
						} else {
							generationSpaces.add(new BorderNode.Connection(offset, offset.opposite()));
						}
					}
				}

				if((isAlone || isBorder) && !generationSpaces.isEmpty()) {
					borderNodes.add(new BorderNode(node, generationSpaces));
				}
			}
		}

		return decreaseThreshold ? (nextLowerThreshold == Integer.MIN_VALUE ? threshold : nextLowerThreshold) : (nextHigherThreshold == Integer.MAX_VALUE ? threshold : nextHigherThreshold);
	}

	private Map<IGridNodeHandle, Integer> evaluateWeights(INodeGridHandle grid, IGridNodeHandle start, int index, int maxPathWeight, ToIntFunction<IGridNodeHandle> weightFunction) {
		Map<IGridNodeHandle, Integer> weights = new HashMap<>();
		this.evaluateWeightsRecursive(grid, weights, start, index, 0, maxPathWeight, weightFunction);
		return weights;
	}

	private void evaluateWeightsRecursive(INodeGridHandle grid, Map<IGridNodeHandle, Integer> weights, IGridNodeHandle current, int index, int pathWeight, int maxPathWeight, ToIntFunction<IGridNodeHandle> weightFunction) {
		pathWeight += weightFunction.applyAsInt(current);

		int currentWeight = weights.getOrDefault(current, Integer.MAX_VALUE);

		if(pathWeight < currentWeight) {
			weights.put(current, pathWeight);

			if(pathWeight < maxPathWeight) {
				Pos pos = current.getPos();
				Pos sourcePos = current.getSourcePos();

				for(Direction offset : Direction.values()) {
					Pos offsetPos = new Pos(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);

					IGridNodeHandle offsetNode = grid.get(offsetPos);

					if(offsetNode != null && offsetNode.getIndex() <= index && (Objects.equals(pos, offsetNode.getSourcePos()) || Objects.equals(sourcePos, offsetPos))) {
						this.evaluateWeightsRecursive(grid, weights, offsetNode, index, pathWeight, maxPathWeight, weightFunction);
					}
				}
			}
		}
	}
}
