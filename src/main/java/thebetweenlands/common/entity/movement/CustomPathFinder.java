package thebetweenlands.common.entity.movement;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class CustomPathFinder extends PathFinder {
	private final PathHeap path = new PathHeap();
	private final PathPoint[] pathOptions = new PathPoint[32];
	private final NodeProcessor nodeProcessor;

	private int maxExpansions = 200;

	public static interface Heuristic {
		public float compute(PathPoint start, PathPoint end, boolean isTargetHeuristic);
	}

	public static final Heuristic DEFAULT_HEURISTIC = (start, end, isTargetHeuristic) -> start.distanceManhattan(end);

	private Heuristic heuristic = DEFAULT_HEURISTIC;

	public CustomPathFinder(NodeProcessor processor) {
		super(processor);
		this.nodeProcessor = processor;
	}

	public NodeProcessor getNodeProcessor() {
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

	@Override
	@Nullable
	public Path findPath(IBlockAccess worldIn, EntityLiving entitylivingIn, Entity targetEntity, float maxDistance) {
		return this.findPath(worldIn, entitylivingIn, targetEntity.posX, targetEntity.getEntityBoundingBox().minY, targetEntity.posZ, maxDistance);
	}

	@Override
	@Nullable
	public Path findPath(IBlockAccess worldIn, EntityLiving entitylivingIn, BlockPos targetPos, float maxDistance) {
		return this.findPath(worldIn, entitylivingIn, (double)((float)targetPos.getX() + 0.5F), (double)((float)targetPos.getY() + 0.5F), (double)((float)targetPos.getZ() + 0.5F), maxDistance);
	}

	@Nullable
	private Path findPath(IBlockAccess worldIn, EntityLiving entitylivingIn, double x, double y, double z, float maxDistance) {
		this.path.clearPath();
		this.nodeProcessor.init(worldIn, entitylivingIn);
		PathPoint startPathPoint = this.nodeProcessor.getStart();
		PathPoint targetPathPoint = this.nodeProcessor.getPathPointToCoords(x, y, z);
		Path path = this.findPath(startPathPoint, targetPathPoint, maxDistance);
		this.nodeProcessor.postProcess();
		return path;
	}

	@Nullable
	private Path findPath(PathPoint startPathPoint, PathPoint targetPathPoint, float maxDistance) {
		startPathPoint.totalPathDistance = 0.0F;
		startPathPoint.distanceToNext = this.heuristic.compute(startPathPoint, targetPathPoint, true);
		startPathPoint.distanceToTarget = startPathPoint.distanceToNext;
		this.path.clearPath();
		this.path.addPoint(startPathPoint);
		PathPoint finalPathPoint = startPathPoint;

		int expansions = 0;

		while(!this.path.isPathEmpty()) {
			++expansions;

			if(expansions >= this.maxExpansions) {
				break;
			}

			PathPoint openPathPoint = this.path.dequeue();

			if(openPathPoint.equals(targetPathPoint)) {
				finalPathPoint = targetPathPoint;
				break;
			}

			if(this.heuristic.compute(openPathPoint, targetPathPoint, true) < this.heuristic.compute(finalPathPoint, targetPathPoint, true)) {
				finalPathPoint = openPathPoint;
			}

			openPathPoint.visited = true;
			int numOptions = this.nodeProcessor.findPathOptions(this.pathOptions, openPathPoint, targetPathPoint, maxDistance);

			for(int i = 0; i < numOptions; ++i) {
				PathPoint successorPathPoint = this.pathOptions[i];

				float costHeuristic = this.heuristic.compute(openPathPoint, successorPathPoint, false);

				//distanceFromOrigin corresponds to the total path cost of the evaluation function
				successorPathPoint.distanceFromOrigin = openPathPoint.distanceFromOrigin + costHeuristic;
				successorPathPoint.cost = costHeuristic + successorPathPoint.costMalus;

				float totalSuccessorPathCost = openPathPoint.totalPathDistance + successorPathPoint.cost;

				if(successorPathPoint.distanceFromOrigin < maxDistance && (!successorPathPoint.isAssigned() || totalSuccessorPathCost < successorPathPoint.totalPathDistance)) {
					successorPathPoint.previous = openPathPoint;
					successorPathPoint.totalPathDistance = totalSuccessorPathCost;

					//distanceToNext corresponds to the heuristic part of the evaluation function
					successorPathPoint.distanceToNext = this.heuristic.compute(successorPathPoint, targetPathPoint, true) + successorPathPoint.costMalus;

					if(successorPathPoint.isAssigned()) {
						this.path.changeDistance(successorPathPoint, successorPathPoint.totalPathDistance + successorPathPoint.distanceToNext);
					} else {
						//distanceToTarget corresponds to the evaluation function, i.e. total path cost + heuristic
						successorPathPoint.distanceToTarget = successorPathPoint.totalPathDistance + successorPathPoint.distanceToNext;
						this.path.addPoint(successorPathPoint);
					}
				}
			}
		}

		if(finalPathPoint == startPathPoint) {
			return null;
		} else {
			Path path = this.createPath(startPathPoint, finalPathPoint);
			return path;
		}
	}

	private Path createPath(PathPoint start, PathPoint end) {
		int pathLength = 1;

		for(PathPoint pathpoint = end; pathpoint.previous != null; pathpoint = pathpoint.previous) {
			++pathLength;
		}

		PathPoint[] path = new PathPoint[pathLength];
		PathPoint currentPathPoint = end;
		--pathLength;

		for(path[pathLength] = end; currentPathPoint.previous != null; path[pathLength] = currentPathPoint) {
			currentPathPoint = currentPathPoint.previous;
			--pathLength;
		}

		return new Path(path);
	}
}
