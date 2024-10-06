package thebetweenlands.common.entity.movement.climb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ClimberPathFinder extends CustomPathFinder {

	private static final Direction[] DOWN = new Direction[]{Direction.DOWN};

	public ClimberPathFinder(NodeEvaluator processor, int maxExpansions) {
		super(processor, maxExpansions);
	}

	@Override
	protected Path createPath(Node node, BlockPos target, boolean isTargetReached) {
		List<Node> nodes = new ArrayList<>();

		//Backtrack path from target node back to entity
		this.backtrackPath(nodes, node);

		//Retrace path with valid side transitions
		NodeHolder end = this.retraceSidedPath(nodes, true);

		if (end == null) {
			return new Path(Collections.emptyList(), target, isTargetReached);
		}

		nodes.clear();

		//Backtrack retraced path
		this.backtrackPath(nodes, end);

		return new Path(nodes, target, isTargetReached);
	}

	private void backtrackPath(List<Node> nodes, Node start) {
		Node currentNode = start;
		nodes.add(start);

		while (currentNode.cameFrom != null) {
			currentNode = currentNode.cameFrom;
			nodes.add(currentNode);
		}
	}

	private void backtrackPath(List<Node> nodes, NodeHolder start) {
		NodeHolder currentNode = start;
		nodes.add(start.node);

		while (currentNode.previous != null) {
			currentNode = currentNode.previous;
			nodes.add(currentNode.node);
		}
	}

	private static Direction[] getPathableSidesWithFallback(DirectionalNode node) {
		if (node.getPathableSides().length == 0) {
			return DOWN;
		} else {
			return node.getPathableSides();
		}
	}

	@Nullable
	private ClimberPathFinder.NodeHolder retraceSidedPath(List<Node> nodes, boolean isReversed) {
		if (nodes.isEmpty()) {
			return null;
		}

		final Deque<NodeHolder> queue = new LinkedList<>();

		final DirectionalNode node = this.ensureDirectional(nodes.getFirst());

		for (Direction direction : getPathableSidesWithFallback(node)) {
			queue.add(new NodeHolder(null, node.assignPathSide(direction)));
		}

		NodeHolder end = null;

		final int maxExpansions = 200;
		final Set<NodeHolder> checkedSet = new HashSet<>();

		int expansions = 0;
		while (!queue.isEmpty()) {
			if (expansions++ > maxExpansions) {
				break;
			}

			NodeHolder current = queue.removeFirst();

			if (current.depth == nodes.size() - 1) {
				end = current;
				break;
			}

			Direction currentSide = current.side;

			DirectionalNode next = this.ensureDirectional(nodes.get(current.depth + 1));

			for (Direction nextSide : getPathableSidesWithFallback(next)) {
				NodeHolder nextNode = null;

				if ((isReversed && current.node.isDrop()) || (!isReversed && next.isDrop())) {

					//Side doesn't matter if node represents a drop
					nextNode = new NodeHolder(current, next.assignPathSide(nextSide));

				} else {
					int dx = (int) Math.signum(next.x - current.node.x);
					int dy = (int) Math.signum(next.y - current.node.y);
					int dz = (int) Math.signum(next.z - current.node.z);

					int adx = Math.abs(dx);
					int ady = Math.abs(dy);
					int adz = Math.abs(dz);

					int d = adx + ady + adz;

					if (d == 1) {
						//Path is straight line

						if (nextSide == currentSide) {

							//Allow movement on the same side
							nextNode = new NodeHolder(current, next.assignPathSide(nextSide));

						} else if (nextSide.getAxis() != currentSide.getAxis()) {

							//Allow movement around corners, but insert new node with transitional side in between
							NodeHolder intermediary;
							if (Math.abs(currentSide.getStepX()) == adx && Math.abs(currentSide.getStepY()) == ady && Math.abs(currentSide.getStepZ()) == adz) {
								intermediary = new NodeHolder(current, current.node.assignPathSide(nextSide));
							} else {
								intermediary = new NodeHolder(current, next.assignPathSide(currentSide));
							}

							nextNode = new NodeHolder(intermediary, intermediary.depth, next.assignPathSide(nextSide));

						}
					} else if (d == 2) {
						//Diagonal

						int currentSidePlaneMatch = (currentSide.getStepX() == -dx ? 1 : 0) + (currentSide.getStepY() == -dy ? 1 : 0) + (currentSide.getStepZ() == -dz ? 1 : 0);

						if (currentSide == nextSide && currentSidePlaneMatch == 0) {

							//Allow diagonal movement, no need to insert transitional side since the diagonal's plane's normal is the same as the path's side
							nextNode = new NodeHolder(current, next.assignPathSide(nextSide));

						} else {
							//Allow movement, but insert new node with transitional side inbetween

							NodeHolder intermediary = null;
							if (currentSidePlaneMatch == 2) {
								for (Direction intermediarySide : getPathableSidesWithFallback(current.node())) {
									if (intermediarySide != currentSide && (intermediarySide.getStepX() == dx ? 1 : 0) + (intermediarySide.getStepY() == dy ? 1 : 0) + (intermediarySide.getStepZ() == dz ? 1 : 0) == 2) {
										intermediary = new NodeHolder(current, current.node.assignPathSide(intermediarySide));
										break;
									}
								}
							} else {
								for (Direction intermediarySide : getPathableSidesWithFallback(next)) {
									if (intermediarySide != nextSide && (intermediarySide.getStepX() == -dx ? 1 : 0) + (intermediarySide.getStepY() == -dy ? 1 : 0) + (intermediarySide.getStepZ() == -dz ? 1 : 0) == 2) {
										intermediary = new NodeHolder(current, next.assignPathSide(intermediarySide));
										break;
									}
								}
							}

							if (intermediary != null) {
								nextNode = new NodeHolder(intermediary, intermediary.depth, next.assignPathSide(nextSide));
							} else {
								nextNode = new NodeHolder(current, next.assignPathSide(nextSide));
							}
						}
					}
				}

				if (nextNode != null && checkedSet.add(nextNode)) {
					queue.addLast(nextNode);
				}
			}
		}

		return end;
	}

	private DirectionalNode ensureDirectional(Node node) {
		if (node instanceof DirectionalNode dir) {
			return dir;
		} else {
			return new DirectionalNode(node);
		}
	}

	private record NodeHolder(@Nullable NodeHolder previous, DirectionalNode node, @Nullable Direction side, int depth) {

		private NodeHolder(@Nullable NodeHolder previous, DirectionalNode node) {
			this(previous, node, node.getPathSide(), previous != null ? previous.depth + 1 : 0);
		}

		private NodeHolder(NodeHolder previous, int depth, DirectionalNode node) {
			this(previous, node, node.getPathSide(), depth);
		}
	}
}
