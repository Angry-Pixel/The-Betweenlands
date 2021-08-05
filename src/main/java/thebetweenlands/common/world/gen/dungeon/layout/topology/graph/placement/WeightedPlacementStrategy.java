package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

import thebetweenlands.api.entity.spawning.IWeightProvider;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;
import thebetweenlands.util.WeightedList;

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
		//TODO Make configurable
		ToIntFunction<IGridNodeHandle> nodeWeightFunc = n -> 1;
		ToIntBiFunction<Pos, Direction> offsetWeightFunc = (p, d) -> d.y > 0 ? 0 : (d.y < 0 ? 1 : 10);

		//TODO Make configurable
		int minPathWeight = 0;
		int maxPathWeight = 100;
		ToIntFunction<Random> pathWeightThresholdDistribution = r -> r.nextInt(5);

		if(context.isStructured()) {
			WeightedList<Connection> weightedAttachConnections = new WeightedList<>();

			Pos startPos = start.getPos();

			for(Direction offset : Direction.ALL) {
				Pos offsetPos = new Pos(startPos.x + offset.x, startPos.y + offset.y, startPos.z + offset.z);

				if(context.isInGenerationSpace(start, offsetPos)) {
					if(offset.y != 0) {
						for(Direction horizontalOffset : Direction.HORIZONTAL) {
							if(this.canConnect(start, horizontalOffset)) {
								Connection attachConnection = new Connection(offset, horizontalOffset.opposite(), (short)offsetWeightFunc.applyAsInt(offsetPos, offset));
								if(attachConnection.weight > 0) {
									weightedAttachConnections.add(attachConnection);
								}
							}
						}
					} else if(this.canConnect(start, offset)) {
						Connection attachConnection = new Connection(offset, offset.opposite(), (short)offsetWeightFunc.applyAsInt(offsetPos, offset));
						if(attachConnection.weight > 0) {
							weightedAttachConnections.add(attachConnection);
						}
					}
				}
			}

			if(!weightedAttachConnections.isEmpty()) {
				Connection attachConnection = weightedAttachConnections.getRandomItem(rng);

				this.addConnection(start, attachConnection.dir.opposite());

				return new Placement(start, attachConnection.offset, attachConnection.dir);
			}
		}

		Map<IGridNodeHandle, Integer> weights = this.evaluateWeights(grid, start, index, maxPathWeight, nodeWeightFunc);

		List<AttachPoint> attachPoints = new ArrayList<>();

		boolean useLowThreshold = rng.nextBoolean();

		int lowThreshold;
		int highThreshold;

		lowThreshold = highThreshold = pathWeightThresholdDistribution.applyAsInt(rng);

		int prevLowThreshold = Integer.MAX_VALUE;
		int prevHighThreshold = Integer.MIN_VALUE;

		do {
			if(useLowThreshold) {
				prevLowThreshold = lowThreshold;
				lowThreshold = this.findAttachPoints(grid, weights, offsetWeightFunc, lowThreshold, context, attachPoints, true);
			} else {
				prevHighThreshold = highThreshold;
				highThreshold = this.findAttachPoints(grid, weights, offsetWeightFunc, highThreshold, context, attachPoints, false);
			}
			useLowThreshold = !useLowThreshold;
		} while(attachPoints.isEmpty() && ((lowThreshold < prevLowThreshold && lowThreshold >= minPathWeight) || (highThreshold > prevHighThreshold && highThreshold <= maxPathWeight)));

		if(!attachPoints.isEmpty()) {
			WeightedList<AttachPoint> weightedAttachPoints = new WeightedList<>();
			for(AttachPoint attachPoint : attachPoints) {
				attachPoint.calculateWeight(nodeWeightFunc, offsetWeightFunc);
				if(attachPoint.weight > 0) {
					weightedAttachPoints.add(attachPoint);
				}
			}

			AttachPoint attachPoint = weightedAttachPoints.getRandomItem(rng);

			WeightedList<Connection> weightedAttachConnections = new WeightedList<>();
			for(Connection connection : attachPoint.connections) {
				if(connection.weight > 0) {
					weightedAttachConnections.add(connection);
				}
			}

			Connection attachConnection = weightedAttachConnections.getRandomItem(rng);

			IGridNodeHandle node = attachPoint.node;

			this.addConnection(node, attachConnection.dir.opposite());

			return new Placement(node, attachConnection.offset, attachConnection.dir);
		}

		throw new IllegalStateException();
	}

	private static class Connection implements IWeightProvider {
		private final Direction offset;
		private final Direction dir;

		private short weight;

		private Connection(Direction offset, Direction dir) {
			this.offset = offset;
			this.dir = dir;
		}

		private Connection(Direction offset, Direction dir, short weight) {
			this.offset = offset;
			this.dir = dir;
			this.weight = weight;
		}

		@Override
		public short getWeight() {
			return this.weight;
		}
	}

	private static class AttachPoint implements IWeightProvider {
		private final IGridNodeHandle node;
		private final List<Connection> connections;

		private short weight;

		private AttachPoint(IGridNodeHandle node, List<Connection> connections) {
			this.node = node;
			this.connections = connections;
		}

		private int calculateWeight(ToIntFunction<IGridNodeHandle> nodeWeightFunc, ToIntBiFunction<Pos, Direction> offsetWeightFunc) {
			this.weight = 0;
			this.weight += nodeWeightFunc.applyAsInt(this.node);
			int connectionsWeight = 0;
			Pos pos = this.node.getPos();
			for(Connection connection : this.connections) {
				connection.weight = (short)offsetWeightFunc.applyAsInt(new Pos(pos.x + connection.offset.x, pos.y + connection.offset.y, pos.z + connection.offset.z), connection.offset);
				connectionsWeight += connection.weight;
			}
			this.weight += connectionsWeight / this.connections.size();
			return this.weight;
		}

		@Override
		public short getWeight() {
			return this.weight;
		}
	}

	private int findAttachPoints(INodeGridHandle grid, Map<IGridNodeHandle, Integer> weights, ToIntBiFunction<Pos, Direction> offsetWeightFunc, int threshold, PlacementContext context, List<AttachPoint> borderNodes, boolean decreaseThreshold) {
		int nextLowerThreshold = Integer.MIN_VALUE;
		int nextHigherThreshold = Integer.MAX_VALUE;

		for(IGridNodeHandle node : grid.get()) {
			int weight = weights.getOrDefault(node, Integer.MIN_VALUE);

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
				List<Connection> generationSpaces = new ArrayList<>();

				for(Direction offset : Direction.ALL) {
					Pos offsetPos = new Pos(pos.x + offset.x, pos.y + offset.y, pos.z + offset.z);

					IGridNodeHandle offsetNode = grid.get(offsetPos);

					if(offsetNode != null && (Objects.equals(pos, offsetNode.getSourcePos()) || Objects.equals(sourcePos, offsetPos))) {
						isAlone = false;

						if(weights.getOrDefault(offsetNode, Integer.MAX_VALUE) < threshold) {
							isBorder = true;
						}
					}

					if(context.isInGenerationSpace(node, offsetPos) && this.canConnect(node, offset) && offsetWeightFunc.applyAsInt(offsetPos, offset) > 0) {
						if(offset.y != 0) {
							for(Direction horizontalOffset : Direction.HORIZONTAL) {
								if(this.canConnect(node, horizontalOffset)) {
									generationSpaces.add(new Connection(offset, horizontalOffset.opposite()));
								}
							}
						} else {
							generationSpaces.add(new Connection(offset, offset.opposite()));
						}
					}
				}

				if((isAlone || isBorder) && !generationSpaces.isEmpty()) {
					borderNodes.add(new AttachPoint(node, generationSpaces));
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

				for(Direction offset : Direction.ALL) {
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
