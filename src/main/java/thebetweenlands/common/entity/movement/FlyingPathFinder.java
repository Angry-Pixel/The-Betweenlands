package thebetweenlands.common.entity.movement;

import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class FlyingPathFinder extends PathFinder {
	/** The path being generated */
	private final PathHeap path = new PathHeap();
	private final Set<PathPoint> closedSet = Sets.<PathPoint>newHashSet();
	/** Selection of path points to add to the path */
	private final PathPoint[] pathOptions = new PathPoint[32];
	private final NodeProcessor nodeProcessor;

	public FlyingPathFinder(NodeProcessor processor) {
		super(processor);
		this.nodeProcessor = processor;
	}

	@Override
	@Nullable
	public Path findPath(IBlockAccess worldIn, EntityLiving entitylivingIn, Entity targetEntity, float maxDistance) {
		return this.findPath(worldIn, entitylivingIn, targetEntity.posX, targetEntity.getEntityBoundingBox().minY,
				targetEntity.posZ, maxDistance);
	}

	@Override
	@Nullable
	public Path findPath(IBlockAccess worldIn, EntityLiving entitylivingIn, BlockPos targetPos, float maxDistance) {
		return this.findPath(worldIn, entitylivingIn, (double) ((float) targetPos.getX() + 0.5F),
				(double) ((float) targetPos.getY() + 0.5F), (double) ((float) targetPos.getZ() + 0.5F), maxDistance);
	}

	@Nullable
	private Path findPath(IBlockAccess worldIn, EntityLiving entitylivingIn, double x, double y, double z,
			float maxDistance) {
		this.path.clearPath();
		this.nodeProcessor.init(worldIn, entitylivingIn);
		PathPoint pathpoint = this.nodeProcessor.getStart();
		PathPoint pathpoint1 = this.nodeProcessor.getPathPointToCoords(x, y, z);
		Path path = this.findPath(pathpoint, pathpoint1, maxDistance);
		this.nodeProcessor.postProcess();
		return path;
	}

	@Nullable
	private Path findPath(PathPoint pathFrom, PathPoint pathTo, float maxDistance) {
		pathFrom.totalPathDistance = 0.0F;
		pathFrom.distanceToNext = pathFrom.distanceManhattan(pathTo);
		pathFrom.distanceToTarget = pathFrom.distanceToNext;
		this.path.clearPath();
		this.closedSet.clear();
		this.path.addPoint(pathFrom);
		PathPoint pathpoint = pathFrom;
		int i = 0;

		while (!this.path.isPathEmpty()) {
			++i;

			if (i >= 1000) { // TODO: Increased this slightly because flying mobs have more tiles available that need to be checked. Maybe need to check performance
				break;
			}

			PathPoint pathpoint1 = this.path.dequeue();

			if (pathpoint1.equals(pathTo)) {
				pathpoint = pathTo;
				break;
			}

			if (pathpoint1.distanceManhattan(pathTo) < pathpoint.distanceManhattan(pathTo)) {
				pathpoint = pathpoint1;
			}

			pathpoint1.visited = true;
			int j = this.nodeProcessor.findPathOptions(this.pathOptions, pathpoint1, pathTo, maxDistance);

			for (int k = 0; k < j; ++k) {
				PathPoint pathpoint2 = this.pathOptions[k];
				float f = pathpoint1.distanceManhattan(pathpoint2);
				pathpoint2.distanceFromOrigin = pathpoint1.distanceFromOrigin + f;
				pathpoint2.cost = f + pathpoint2.costMalus;
				float f1 = pathpoint1.totalPathDistance + pathpoint2.cost;

				if (pathpoint2.distanceFromOrigin < maxDistance
						&& (!pathpoint2.isAssigned() || f1 < pathpoint2.totalPathDistance)) {
					pathpoint2.previous = pathpoint1;
					pathpoint2.totalPathDistance = f1;
					pathpoint2.distanceToNext = pathpoint2.distanceManhattan(pathTo) + pathpoint2.costMalus;

					if (pathpoint2.isAssigned()) {
						this.path.changeDistance(pathpoint2, pathpoint2.totalPathDistance + pathpoint2.distanceToNext);
					} else {
						pathpoint2.distanceToTarget = pathpoint2.totalPathDistance + pathpoint2.distanceToNext;
						this.path.addPoint(pathpoint2);
					}
				}
			}
		}

		if (pathpoint == pathFrom) {
			return null;
		} else {
			Path path = this.createPath(pathFrom, pathpoint);
			return path;
		}
	}

	/**
	 * Returns a new PathEntity for a given start and end point
	 */
	private Path createPath(PathPoint start, PathPoint end) {
		int i = 1;

		for (PathPoint pathpoint = end; pathpoint.previous != null; pathpoint = pathpoint.previous) {
			++i;
		}

		PathPoint[] apathpoint = new PathPoint[i];
		PathPoint pathpoint1 = end;
		--i;

		for (apathpoint[i] = end; pathpoint1.previous != null; apathpoint[i] = pathpoint1) {
			pathpoint1 = pathpoint1.previous;
			--i;
		}

		return new Path(apathpoint);
	}
}