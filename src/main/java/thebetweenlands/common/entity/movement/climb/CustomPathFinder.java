package thebetweenlands.common.entity.movement.climb;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomPathFinder extends PathFinder {

	private final BinaryHeap path = new BinaryHeap();
	private final Node[] pathOptions = new Node[32];
	private final NodeEvaluator nodeProcessor;

	private int maxExpansions;

	public interface Heuristic {
		float compute(Node start, Node end, boolean isTargetHeuristic);
	}

	public static final Heuristic DEFAULT_HEURISTIC = (start, end, isTargetHeuristic) -> start.distanceManhattan(end);

	private Heuristic heuristic = DEFAULT_HEURISTIC;

	public CustomPathFinder(NodeEvaluator processor, int maxExpansions) {
		super(processor, maxExpansions);
		this.nodeProcessor = processor;
		this.maxExpansions = maxExpansions;
	}

	public NodeEvaluator getNodeProcessor() {
		return this.nodeProcessor;
	}

	public CustomPathFinder setMaxExpansions(int expansions) {
		this.maxExpansions = expansions;
		return this;
	}

	public CustomPathFinder setHeuristic(Heuristic heuristic) {
		this.heuristic = heuristic;
		return this;
	}

	@Nullable
	@Override
	public Path findPath(PathNavigationRegion region, Mob entity, Set<BlockPos> checkpoints, float maxDistance, int checkpointRange, float maxExpansionsMultiplier) {
		this.path.clear();

		this.nodeProcessor.prepare(region, entity);

		Node pathpoint = this.nodeProcessor.getStart();

		//Create a checkpoint for each block pos in the checkpoints set
		Map<Target, BlockPos> checkpointsMap = checkpoints.stream().collect(Collectors.toMap((pos) -> this.nodeProcessor.getTarget(pos.getX(), pos.getY(), pos.getZ()), Function.identity()));

		Path path = this.findPath(pathpoint, checkpointsMap, maxDistance, checkpointRange, maxExpansionsMultiplier);
		this.nodeProcessor.done();

		return path;
	}

	@Nullable
	private Path findPath(Node start, Map<Target, BlockPos> checkpointsMap, float maxDistance, int checkpointRange, float maxExpansionsMultiplier) {
		Set<Target> checkpoints = checkpointsMap.keySet();

		start.g = 0.0F;
		start.h = this.computeHeuristic(start, checkpoints);
		start.f = start.h;

		this.path.clear();
		this.path.insert(start);

		Set<Target> reachedCheckpoints = Sets.newHashSetWithExpectedSize(checkpoints.size());

		int expansions = 0;
		int maxExpansions = (int) (this.maxExpansions * maxExpansionsMultiplier);

		while(!this.path.isEmpty() && ++expansions < maxExpansions) {
			Node openPathPoint = this.path.pop();
			openPathPoint.closed = true;

			for(Target checkpoint : checkpoints) {
				if(openPathPoint.distanceManhattan(checkpoint) <= checkpointRange) {
					checkpoint.setReached();
					reachedCheckpoints.add(checkpoint);
				}
			}

			if(!reachedCheckpoints.isEmpty()) {
				break;
			}

			if(openPathPoint.distanceTo(start) < maxDistance) {
				int numOptions = this.nodeProcessor.getNeighbors(this.pathOptions, openPathPoint);

				for(int i = 0; i < numOptions; ++i) {
					Node successorPathPoint = this.pathOptions[i];

					float costHeuristic = this.heuristic.compute(openPathPoint, successorPathPoint, false);

					//walkedDistance corresponds to the total path cost of the evaluation function
					successorPathPoint.walkedDistance = openPathPoint.walkedDistance + costHeuristic;

					float totalSuccessorPathCost = openPathPoint.g + costHeuristic + successorPathPoint.costMalus;

					if(successorPathPoint.walkedDistance < maxDistance && (!successorPathPoint.inOpenSet() || totalSuccessorPathCost < successorPathPoint.g)) {
						successorPathPoint.cameFrom = openPathPoint;
						successorPathPoint.g = totalSuccessorPathCost;

						//distanceToNext corresponds to the heuristic part of the evaluation function
						successorPathPoint.h = this.computeHeuristic(successorPathPoint, checkpoints);

						if(successorPathPoint.inOpenSet()) {
							this.path.changeCost(successorPathPoint, successorPathPoint.g + successorPathPoint.h);
						} else {
							//distanceToTarget corresponds to the evaluation function, i.e. total path cost + heuristic
							successorPathPoint.f = successorPathPoint.g + successorPathPoint.h;
							this.path.insert(successorPathPoint);
						}
					}
				}
			}
		}

		Optional<Path> path;

		if(!reachedCheckpoints.isEmpty()) {
			//Use shortest path towards next reached checkpoint
			path = reachedCheckpoints.stream().map((checkpoint) -> this.createPath(checkpoint.getBestNode(), checkpointsMap.get(checkpoint), true)).min(Comparator.comparingInt(Path::getNodeCount));
		} else {
			//Use lowest cost path towards any checkpoint
			path = checkpoints.stream().map((checkpoint) -> this.createPath(checkpoint.getBestNode(), checkpointsMap.get(checkpoint), false)).min(Comparator.comparingDouble(Path::getDistToTarget /*TODO Replace calculation with cost heuristic*/).thenComparingInt(Path::getNodeCount));
		}

		return path.orElse(null);
	}

	private float computeHeuristic(Node pathPoint, Set<Target> checkpoints) {
		float minDst = Float.MAX_VALUE;

		for(Target checkpoint : checkpoints) {
			float dst = this.heuristic.compute(pathPoint, checkpoint, true);
			checkpoint.updateBest(dst, pathPoint);
			minDst = Math.min(dst, minDst);
		}

		return minDst;
	}

	protected Path createPath(Node start, BlockPos target, boolean isTargetReached) {
		List<Node> points = Lists.newArrayList();

		Node currentPathPoint = start;
		points.addFirst(start);

		while(currentPathPoint.cameFrom != null) {
			currentPathPoint = currentPathPoint.cameFrom;
			points.addFirst(currentPathPoint);
		}

		return new Path(points, target, isTargetReached);
	}
}
