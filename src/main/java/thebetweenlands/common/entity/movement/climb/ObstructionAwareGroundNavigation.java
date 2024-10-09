package thebetweenlands.common.entity.movement.climb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ObstructionAwareGroundNavigation<T extends PathfinderMob & PathObstructionAwareEntity> extends GroundPathNavigation {
	@Nullable
	protected CustomPathFinder pathFinder;
	protected long lastTimeUpdated;
	@Nullable
	protected BlockPos targetPos;

	protected final T advancedPathFindingEntity;
	protected final boolean checkObstructions;

	protected int stuckCheckTicks = 0;

	protected int checkpointRange;

	public ObstructionAwareGroundNavigation(T entity, Level level, boolean checkObstructions) {
		super(entity, level);
		this.advancedPathFindingEntity = entity;
		this.checkObstructions = checkObstructions;

		if (this.nodeEvaluator instanceof ObstructionAwareNodeEvaluator evaluator) {
			evaluator.setCheckObstructions(checkObstructions);
		}
	}

	@Override
	protected PathFinder createPathFinder(int maxExpansions) {
		this.pathFinder = this.createAdvancedPathFinder(maxExpansions);
		this.nodeEvaluator = this.pathFinder.getNodeProcessor();
		return this.pathFinder;
	}

	protected CustomPathFinder createAdvancedPathFinder(int maxExpansions) {
		ObstructionAwareNodeEvaluator nodeProcessor = new ObstructionAwareNodeEvaluator();
		nodeProcessor.setCanPassDoors(true);
		return new CustomPathFinder(nodeProcessor, maxExpansions);
	}

	@Nullable
	@Override
	protected Path createPath(Set<BlockPos> waypoints, int padding, boolean startAbove, int checkpointRange) {
		//Offset waypoints according to entity's size so that the lower AABB corner is at the offset waypoint and center is at the original waypoint
		Set<BlockPos> adjustedWaypoints = new HashSet<>();
		for (BlockPos pos : waypoints) {
			adjustedWaypoints.add(pos.offset(-Mth.ceil(this.mob.getBbWidth()) + 1, -Mth.ceil(this.mob.getBbHeight()) + 1, -Mth.ceil(this.mob.getBbWidth()) + 1));
		}

		Path path = super.createPath(adjustedWaypoints, padding, startAbove, checkpointRange);

		if (path != null) {
			this.checkpointRange = checkpointRange;
		}

		return path;
	}

	@Override
	public void recomputePath() {
		if (this.level.getGameTime() - this.lastTimeUpdated > 20L) {
			if (this.targetPos != null) {
				this.path = null;
				this.path = this.createPath(this.targetPos, this.checkpointRange);
				this.lastTimeUpdated = this.level.getGameTime();
				this.hasDelayedRecomputation = false;
			}
		} else {
			this.hasDelayedRecomputation = true;
		}
	}

	@Override
	protected void doStuckDetection(Vec3 entityPos) {
		super.doStuckDetection(entityPos);

		if (this.checkObstructions && this.path != null && !this.path.isDone()) {
			Vec3 target = this.path.getEntityPosAtNode(this.advancedPathFindingEntity, Math.min(this.path.getNodeCount() - 1, this.path.getNextNodeIndex()));
			Vec3 diff = target.subtract(entityPos);

			int axis = 0;
			double maxDiff = 0;
			for (int i = 0; i < 3; i++) {
				double d = switch (i) {
					case 1 -> Math.abs(diff.y);
					case 2 -> Math.abs(diff.z);
					default -> Math.abs(diff.x);
				};

				if (d > maxDiff) {
					axis = i;
					maxDiff = d;
				}
			}

			int height = Mth.floor(this.advancedPathFindingEntity.getBbHeight() + 1.0F);

			int ceilHalfWidth = Mth.ceil(this.advancedPathFindingEntity.getBbWidth() / 2.0f + 0.05F);

			Vec3 checkPos = switch (axis) {
				case 1 -> new Vec3(entityPos.x, entityPos.y + (diff.y > 0 ? (height + 1) : -1), target.z);
				case 2 -> new Vec3(target.x, entityPos.y, entityPos.z + Math.signum(diff.z) * ceilHalfWidth);
				default -> new Vec3(entityPos.x + Math.signum(diff.x) * ceilHalfWidth, entityPos.y, target.z);
			};

			Vec3 facingDiff = checkPos.subtract(entityPos.add(0, axis == 1 ? this.mob.getBbHeight() / 2 : 0, 0));
			Direction facing = Direction.getNearest((float) facingDiff.x, (float) facingDiff.y, (float) facingDiff.z);

			boolean blocked = false;

			AABB checkBox = this.advancedPathFindingEntity.getBoundingBox().expandTowards(Math.signum(diff.x) * 0.2D, Math.signum(diff.y) * 0.2D, Math.signum(diff.z) * 0.2D);

			loop:
			for (int yo = 0; yo < height; yo++) {
				for (int xzo = -ceilHalfWidth; xzo <= ceilHalfWidth; xzo++) {
					BlockPos pos = BlockPos.containing(checkPos.x + (axis != 0 ? xzo : 0), checkPos.y + (axis != 1 ? yo : 0), checkPos.z + (axis != 2 ? xzo : 0));

					BlockState state = this.advancedPathFindingEntity.level().getBlockState(pos);
					PathType nodeType = state.isPathfindable(PathComputationType.LAND) ? PathType.OPEN : PathType.BLOCKED;

					if (nodeType == PathType.BLOCKED) {
						VoxelShape collisionShape = state.getShape(this.advancedPathFindingEntity.level(), pos, CollisionContext.of(this.advancedPathFindingEntity)).move(pos.getX(), pos.getY(), pos.getZ());

						if (!collisionShape.isEmpty() && collisionShape.toAabbs().stream().anyMatch(aabb -> aabb.intersects(checkBox))) {
							blocked = true;
							break loop;
						}
					}
				}
			}

			if (blocked) {
				this.stuckCheckTicks++;

				if (this.stuckCheckTicks > this.advancedPathFindingEntity.getMaxStuckCheckTicks()) {
					this.advancedPathFindingEntity.onPathingObstructed(facing);
					this.stuckCheckTicks = 0;
				}
			} else {
				this.stuckCheckTicks = Math.max(this.stuckCheckTicks - 2, 0);
			}
		} else {
			this.stuckCheckTicks = Math.max(this.stuckCheckTicks - 4, 0);
		}
	}
}