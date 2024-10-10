package thebetweenlands.common.entity.movement;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RowingNodeEvaluator extends WalkNodeEvaluator {

	@Override
	public Node getStart() {
		int startY = Mth.floor(this.mob.getBoundingBox().minY + 0.5D);
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		BlockPos blockpos = this.mob.blockPosition();
		PathType startNodeType = this.isFree(new PathfindingContext(this.mob.level(), this.mob), blockpos.getX(), startY, blockpos.getZ());
		if (this.mob.getPathfindingMalus(startNodeType) < 0.0F) {
			AABB aabb = this.mob.getBoundingBox();
			if (this.canStartAt(mutable.set(aabb.minX, startY, aabb.minZ))
				|| this.canStartAt(mutable.set(aabb.minX, startY, aabb.maxZ))
				|| this.canStartAt(mutable.set(aabb.maxX, startY, aabb.minZ))
				|| this.canStartAt(mutable.set(aabb.maxX, startY, aabb.maxZ))) {
				return this.getStartNode(mutable);
			}
		}

		return this.getNode(blockpos.getX(), startY, blockpos.getZ());
	}

	protected boolean canStartAt(BlockPos pos) {
		PathType pathtype = this.isFree(new PathfindingContext(this.mob.level(), this.mob), pos.getX(), pos.getY(), pos.getZ());
		return pathtype != PathType.BLOCKED;
	}

	@Override
	public Target getTarget(double x, double y, double z) {
		return new Target(Mth.floor(x - (double) (this.mob.getBbWidth() / 2.0F)), Mth.floor(y + 0.5D), Mth.floor(z - (double) (this.mob.getBbWidth() / 2.0F)));
	}

	@Override
	public int getNeighbors(Node[] outputArray, Node currentPoint) {
		int i = 0;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			Node node = this.getAirNode(new PathfindingContext(this.mob.level(), this.mob), currentPoint.x + direction.getStepX(), currentPoint.y, currentPoint.z + direction.getStepZ());
			this.reusableNeighbors[direction.get2DDataValue()] = node;
			if (this.isNeighborValid(node, currentPoint)) {
				outputArray[i++] = node;
			}
		}
		return i;
	}

	@Override
	public PathType getPathType(PathfindingContext context, int x, int y, int z) {
		return this.isFree(context, x, y, z);
	}

	@Override
	public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob mob) {
		return this.isFree(context, x, y, z);
	}

	@Nullable
	private Node getAirNode(PathfindingContext context, int x, int y, int z) {
		PathType pathnodetype = this.isFree(context, x, y, z);
		if (pathnodetype == PathType.OPEN) {
			Node pathPoint = this.getNode(x, y, z);
			PathType cached = this.getCachedPathType(x, y - 1, z);
			if (cached == PathType.BLOCKED) {
				return null;
			} else if (this.isFree(context, x, y - 1, z) == PathType.BLOCKED) {
				return null;
			}
			return pathPoint;
		}
		return null;
	}

	private PathType isFree(PathfindingContext context, int x, int y, int z) {
		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos checkPosBelow = new BlockPos.MutableBlockPos();
		for (int i = x; i < x + this.entityWidth; ++i) {
			for (int j = y; j < y + this.entityHeight; ++j) {
				for (int k = z; k < z + this.entityDepth; ++k) {
					checkPos.set(i, j, k);
					checkPosBelow.set(i, j - 1, k);

					BlockState state = context.getBlockState(checkPos);
					BlockState stateBelow = context.getBlockState(checkPosBelow);

					List<AABB> collidingAABBs = new ArrayList<>();
					if (!stateBelow.liquid() && !stateBelow.isSolid())
						this.addBoxToList(state, context.level(), checkPos, new AABB(x, y, z, x + this.entityWidth, y + this.entityHeight, z + this.entityDepth), collidingAABBs, this.mob);
					this.addBoxToList(state, context.level(), checkPos, new AABB(x, y, z, x + this.entityWidth, y + this.entityHeight, z + this.entityDepth), collidingAABBs, this.mob);
					if (!collidingAABBs.isEmpty()) {
						return PathType.BLOCKED;
					}
				}
			}
		}
		return PathType.OPEN;
	}

	private void addBoxToList(BlockState state, CollisionGetter level, BlockPos pos, AABB detectionShape, List<AABB> shapes, Entity entity) {
		VoxelShape shape = state.getCollisionShape(level, pos);
		if (!shape.isEmpty()) {
			AABB entitybb = entity.getBoundingBox().move(pos);
			if (detectionShape.intersects(entitybb)) {
				shapes.add(entitybb);
			}
		}
	}
}