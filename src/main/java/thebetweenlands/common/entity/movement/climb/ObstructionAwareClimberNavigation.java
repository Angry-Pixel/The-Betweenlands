package thebetweenlands.common.entity.movement.climb;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.entity.ClimbingMob;
import thebetweenlands.common.entity.Orientation;

import javax.annotation.Nullable;

public class ObstructionAwareClimberNavigation<T extends ClimbingMob> extends ObstructionAwareGroundNavigation<T> {
	protected final ClimbingMob climber;

	protected Direction verticalFacing = Direction.DOWN;

	protected boolean findDirectPathPoints = false;

	public ObstructionAwareClimberNavigation(T entity, Level level, boolean checkObstructions, boolean canPathWalls, boolean canPathCeiling) {
		super(entity, level, checkObstructions);

		this.climber = entity;

		if (this.nodeEvaluator instanceof ObstructionAwareNodeEvaluator evaluator) {
			evaluator.setStartPathOnGround(false);
			evaluator.setCanPathWalls(canPathWalls);
			evaluator.setCanPathCeiling(canPathCeiling);
		}
	}

	@Override
	protected Vec3 getTempMobPos() {
		return this.mob.position().add(0, this.mob.getBbHeight() / 2.0f, 0);
	}

	@Override
	@Nullable
	public Path createPath(BlockPos pos, int checkpointRange) {
		return this.createPath(ImmutableSet.of(pos), 8, false, checkpointRange);
	}

	@Override
	@Nullable
	public Path createPath(Entity entityIn, int checkpointRange) {
		return this.createPath(ImmutableSet.of(entityIn.blockPosition()), 16, true, checkpointRange);
	}

	@Override
	public void tick() {
		++this.tick;

		if (this.hasDelayedRecomputation) {
			this.recomputePath();
		}

		if (!this.isDone()) {
			if (this.canUpdatePath()) {
				this.followThePath();
			} else if (this.path != null && !this.path.isDone()) {
				Vec3 pos = this.getTempMobPos();
				Vec3 targetPos = this.path.getNextEntityPos(this.mob);

				if (pos.y > targetPos.y && !this.mob.onGround() && Mth.floor(pos.x) == Mth.floor(targetPos.x) && Mth.floor(pos.z) == Mth.floor(targetPos.z)) {
					this.path.advance();
				}
			}

			DebugPackets.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);

			if (!this.isDone()) {
				Node targetPoint = this.path.getNode(this.path.getNextNodeIndex());

				Direction dir = null;

				if (targetPoint instanceof DirectionalNode node) {
					dir = node.getPathSide();
				}

				if (dir == null) {
					dir = Direction.DOWN;
				}

				Vec3 targetPos = this.getExactPathingTarget(this.level, targetPoint.asBlockPos(), dir);

				MoveControl moveController = this.mob.getMoveControl();

				if (moveController instanceof ClimberMoveControl mover && targetPoint instanceof DirectionalNode node && node.getPathSide() != null) {
					mover.setMoveTo(targetPos.x, targetPos.y, targetPos.z, targetPoint.asBlockPos().relative(dir), node.getPathSide(), this.speedModifier);
				} else {
					moveController.setWantedPosition(targetPos.x, targetPos.y, targetPos.z, this.speedModifier);
				}
			}
		}
	}

	public Vec3 getExactPathingTarget(BlockGetter blockaccess, BlockPos pos, Direction dir) {
		BlockPos offsetPos = pos.relative(dir);

		VoxelShape shape = blockaccess.getBlockState(offsetPos).getCollisionShape(blockaccess, offsetPos);

		Direction.Axis axis = dir.getAxis();

		int sign = dir.getStepX() + dir.getStepY() + dir.getStepZ();
		double offset = shape.isEmpty() ? sign /*undo offset if no collider*/ : (sign > 0 ? shape.min(axis) - 1 : shape.max(axis));

		double marginXZ = 1 - (this.mob.getBbWidth() % 1);
		double marginY = 1 - (this.mob.getBbHeight() % 1);

		double pathingOffsetXZ = (int) (this.mob.getBbWidth() + 1.0F) * 0.5D;
		double pathingOffsetY = (int) (this.mob.getBbHeight() + 1.0F) * 0.5D - this.mob.getBbHeight() * 0.5f;

		double x = offsetPos.getX() + pathingOffsetXZ + dir.getStepX() * marginXZ;
		double y = offsetPos.getY() + pathingOffsetY + (dir == Direction.DOWN ? -pathingOffsetY : 0.0D) + (dir == Direction.UP ? -pathingOffsetY + marginY : 0.0D);
		double z = offsetPos.getZ() + pathingOffsetXZ + dir.getStepZ() * marginXZ;

		return switch (axis) {
			case Y -> new Vec3(x, y + offset, z);
			case Z -> new Vec3(x, y, z + offset);
			default -> new Vec3(x + offset, y, z);
		};
	}

	@Override
	protected void followThePath() {
		Vec3 pos = this.getTempMobPos();

		this.maxDistanceToWaypoint = this.mob.getBbWidth() > 0.75F ? this.mob.getBbWidth() / 2.0F : 0.75F - this.mob.getBbWidth() / 2.0F;
		float maxDistanceToWaypointY = Math.max(1 /*required for e.g. slabs*/, this.mob.getBbHeight() > 0.75F ? this.mob.getBbHeight() / 2.0F : 0.75F - this.mob.getBbHeight() / 2.0F);

		int sizeX = Mth.ceil(this.mob.getBbWidth());
		int sizeY = Mth.ceil(this.mob.getBbHeight());
		int sizeZ = sizeX;

		Orientation orientation = this.climber.getOrientation();
		Vec3 upVector = orientation.getGlobal(this.mob.getYRot(), -90);

		this.verticalFacing = Direction.getNearest((float) upVector.x, (float) upVector.y, (float) upVector.z);

		//Look up to 4 nodes ahead so it doesn't backtrack on positions with multiple path sides when changing/updating path
		for (int i = 4; i >= 0; i--) {
			if (this.path.getNextNodeIndex() + i < this.path.getNodeCount()) {
				Node currentTarget = this.path.getNode(this.path.getNextNodeIndex() + i);

				double dx = Math.abs(currentTarget.x + (int) (this.mob.getBbWidth() + 1.0f) * 0.5f - this.mob.getX());
				double dy = Math.abs(currentTarget.y - this.mob.getY());
				double dz = Math.abs(currentTarget.z + (int) (this.mob.getBbWidth() + 1.0f) * 0.5f - this.mob.getZ());

				boolean isWaypointInReach = dx < this.maxDistanceToWaypoint && dy < maxDistanceToWaypointY && dz < this.maxDistanceToWaypoint;

				boolean isOnSameSideAsTarget = false;
				if (this.canFloat() && (currentTarget.type == PathType.WATER || currentTarget.type == PathType.WATER_BORDER || currentTarget.type == PathType.LAVA)) {
					isOnSameSideAsTarget = true;
				} else if (currentTarget instanceof DirectionalNode node) {
					Direction targetSide = node.getPathSide();
					isOnSameSideAsTarget = targetSide == null || this.climber.getGroundSide() == targetSide;
				} else {
					isOnSameSideAsTarget = true;
				}

				if (isOnSameSideAsTarget && (isWaypointInReach || (i == 0 && this.mob.getNavigation().canCutCorner(this.path.getNextNode().type) && this.isNextTargetInLine(pos, sizeX, sizeY, sizeZ, 1 + i)))) {
					this.path.setNextNodeIndex(this.path.getNextNodeIndex() + 1 + i);
					break;
				}
			}
		}

		if (this.findDirectPathPoints) {
			Direction.Axis verticalAxis = this.verticalFacing.getAxis();

			int firstDifferentHeightPoint = this.path.getNodeCount();

			switch (verticalAxis) {
				case X:
					for (int i = this.path.getNextNodeIndex(); i < this.path.getNodeCount(); ++i) {
						if (this.path.getNode(i).x != Math.floor(pos.x)) {
							firstDifferentHeightPoint = i;
							break;
						}
					}
					break;
				case Y:
					for (int i = this.path.getNextNodeIndex(); i < this.path.getNodeCount(); ++i) {
						if (this.path.getNode(i).y != Math.floor(pos.y)) {
							firstDifferentHeightPoint = i;
							break;
						}
					}
					break;
				case Z:
					for (int i = this.path.getNextNodeIndex(); i < this.path.getNodeCount(); ++i) {
						if (this.path.getNode(i).z != Math.floor(pos.z)) {
							firstDifferentHeightPoint = i;
							break;
						}
					}
					break;
			}

			for (int i = firstDifferentHeightPoint - 1; i >= this.path.getNextNodeIndex(); --i) {
				if (this.canMoveDirectly(pos, this.path.getEntityPosAtNode(this.mob, i)/*, sizeX, sizeY, sizeZ*/)) {
					this.path.setNextNodeIndex(i);
					break;
				}
			}
		}

		this.doStuckDetection(pos);
	}

	private boolean isNextTargetInLine(Vec3 pos, int sizeX, int sizeY, int sizeZ, int offset) {
		if (this.path.getNextNodeIndex() + offset >= this.path.getNodeCount()) {
			return false;
		} else {
			Vec3 currentTarget = Vec3.atBottomCenterOf(this.path.getNextNodePos());

			if (!pos.closerThan(currentTarget, 2.0D)) {
				return false;
			} else {
				Vec3 nextTarget = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + offset));
				Vec3 targetDir = nextTarget.subtract(currentTarget);
				Vec3 currentDir = pos.subtract(currentTarget);

				if (targetDir.dot(currentDir) > 0.0D) {
					Direction.Axis ax, ay, az;
					boolean invertY;

					switch (this.verticalFacing.getAxis()) {
						case X:
							ax = Direction.Axis.Z;
							ay = Direction.Axis.X;
							az = Direction.Axis.Y;
							invertY = this.verticalFacing.getStepX() < 0;
							break;
						default:
						case Y:
							ax = Direction.Axis.X;
							ay = Direction.Axis.Y;
							az = Direction.Axis.Z;
							invertY = this.verticalFacing.getStepY() < 0;
							break;
						case Z:
							ax = Direction.Axis.Y;
							ay = Direction.Axis.Z;
							az = Direction.Axis.X;
							invertY = this.verticalFacing.getStepZ() < 0;
							break;
					}

					//Make sure that the mob can stand at the next point in the same orientation it currently has
					return this.isSafeToStandAt(Mth.floor(nextTarget.x), Mth.floor(nextTarget.y), Mth.floor(nextTarget.z), sizeX, sizeY, sizeZ, currentTarget, 0, 0, -1, ax, ay, az, invertY);
				}

				return false;
			}
		}
	}

	//todo: fix this?
	@Override
	protected boolean canMoveDirectly(Vec3 start, Vec3 end/*, int sizeX, int sizeY, int sizeZ*/) {
		int sizeX = 0;//(int) this.mob.getBbWidth();
		int sizeY = 0;//(int) this.mob.getBbHeight();
		int sizeZ = 0;//(int) this.mob.getBbWidth();
		return switch (this.verticalFacing.getAxis()) {
			case X -> this.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ, Direction.Axis.Z, Direction.Axis.X, Direction.Axis.Y, 0.0D, this.verticalFacing.getStepX() < 0);
			case Y -> this.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ, Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z, 0.0D, this.verticalFacing.getStepY() < 0);
			case Z -> this.isDirectPathBetweenPoints(start, end, sizeX, sizeY, sizeZ, Direction.Axis.Y, Direction.Axis.Z, Direction.Axis.X, 0.0D, this.verticalFacing.getStepZ() < 0);
		};
	}

	protected static double swizzle(Vec3 vec, Direction.Axis axis) {
		return switch (axis) {
			case X -> vec.x;
			case Y -> vec.y;
			case Z -> vec.z;
		};
	}

	protected static int swizzle(int x, int y, int z, Direction.Axis axis) {
		switch (axis) {
			case X:
				return x;
			case Y:
				return y;
			case Z:
				return z;
		}
		return 0;
	}

	protected static int unswizzle(int x, int y, int z, Direction.Axis ax, Direction.Axis ay, Direction.Axis az, Direction.Axis axis) {
		Direction.Axis unswizzle;
		if (axis == ax) {
			unswizzle = Direction.Axis.X;
		} else if (axis == ay) {
			unswizzle = Direction.Axis.Y;
		} else {
			unswizzle = Direction.Axis.Z;
		}
		return swizzle(x, y, z, unswizzle);
	}

	protected boolean isDirectPathBetweenPoints(Vec3 start, Vec3 end, int sizeX, int sizeY, int sizeZ, Direction.Axis ax, Direction.Axis ay, Direction.Axis az, double minDotProduct, boolean invertY) {
		int bx = Mth.floor(swizzle(start, ax));
		int bz = Mth.floor(swizzle(start, az));
		double dx = swizzle(end, ax) - swizzle(start, ax);
		double dz = swizzle(end, az) - swizzle(start, az);
		double dSq = dx * dx + dz * dz;

		int by = (int) swizzle(start, ay);

		int sizeX2 = swizzle(sizeX, sizeY, sizeZ, ax);
		int sizeY2 = swizzle(sizeX, sizeY, sizeZ, ay);
		int sizeZ2 = swizzle(sizeX, sizeY, sizeZ, az);

		if (dSq < 1.0E-8D) {
			return false;
		} else {
			double d3 = 1.0D / Math.sqrt(dSq);
			dx = dx * d3;
			dz = dz * d3;
			sizeX2 = sizeX2 + 2;
			sizeZ2 = sizeZ2 + 2;

			if (!this.isSafeToStandAt(
				unswizzle(bx, by, bz, ax, ay, az, Direction.Axis.X), unswizzle(bx, by, bz, ax, ay, az, Direction.Axis.Y), unswizzle(bx, by, bz, ax, ay, az, Direction.Axis.Z),
				unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, Direction.Axis.X), unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, Direction.Axis.Y), unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, Direction.Axis.Z),
				start, dx, dz, minDotProduct, ax, ay, az, invertY)) {
				return false;
			} else {
				sizeX2 = sizeX2 - 2;
				sizeZ2 = sizeZ2 - 2;
				double stepX = 1.0D / Math.abs(dx);
				double stepZ = 1.0D / Math.abs(dz);
				double relX = (double) bx - swizzle(start, ax);
				double relZ = (double) bz - swizzle(start, az);

				if (dx >= 0.0D) {
					++relX;
				}

				if (dz >= 0.0D) {
					++relZ;
				}

				relX = relX / dx;
				relZ = relZ / dz;
				int dirX = dx < 0.0D ? -1 : 1;
				int dirZ = dz < 0.0D ? -1 : 1;
				int ex = Mth.floor(swizzle(end, ax));
				int ez = Mth.floor(swizzle(end, az));
				int offsetX = ex - bx;
				int offsetZ = ez - bz;

				while (offsetX * dirX > 0 || offsetZ * dirZ > 0) {
					if (relX < relZ) {
						relX += stepX;
						bx += dirX;
						offsetX = ex - bx;
					} else {
						relZ += stepZ;
						bz += dirZ;
						offsetZ = ez - bz;
					}

					if (!this.isSafeToStandAt(
						unswizzle(bx, by, bz, ax, ay, az, Direction.Axis.X), unswizzle(bx, by, bz, ax, ay, az, Direction.Axis.Y), unswizzle(bx, by, bz, ax, ay, az, Direction.Axis.Z),
						unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, Direction.Axis.X), unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, Direction.Axis.Y), unswizzle(sizeX2, sizeY2, sizeZ2, ax, ay, az, Direction.Axis.Z),
						start, dx, dz, minDotProduct, ax, ay, az, invertY)) {
						return false;
					}
				}

				return true;
			}
		}
	}

	protected boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 start, double dx, double dz, double minDotProduct, Direction.Axis ax, Direction.Axis ay, Direction.Axis az, boolean invertY) {
		int sizeX2 = swizzle(sizeX, sizeY, sizeZ, ax);
		int sizeZ2 = swizzle(sizeX, sizeY, sizeZ, az);

		int bx = swizzle(x, y, z, ax) - sizeX2 / 2;
		int bz = swizzle(x, y, z, az) - sizeZ2 / 2;

		int by = swizzle(x, y, z, ay);

		if (!this.isPositionClear(
			unswizzle(bx, y, bz, ax, ay, az, Direction.Axis.X), unswizzle(bx, y, bz, ax, ay, az, Direction.Axis.Y), unswizzle(bx, y, bz, ax, ay, az, Direction.Axis.Z),
			sizeX, sizeY, sizeZ, start, dx, dz, minDotProduct, ax, ay, az)) {
			return false;
		} else {
			for (int obx = bx; obx < bx + sizeX2; ++obx) {
				for (int obz = bz; obz < bz + sizeZ2; ++obz) {
					double offsetX = (double) obx + 0.5D - swizzle(start, ax);
					double offsetZ = (double) obz + 0.5D - swizzle(start, az);

					if (offsetX * dx + offsetZ * dz >= minDotProduct) {
						PathType nodeTypeBelow = this.nodeEvaluator.getPathType(
							new PathfindingContext(this.level,this.mob),
							unswizzle(obx, by + (invertY ? 1 : -1), obz, ax, ay, az, Direction.Axis.X), unswizzle(obx, by + (invertY ? 1 : -1), obz, ax, ay, az, Direction.Axis.Y), unswizzle(obx, by + (invertY ? 1 : -1), obz, ax, ay, az, Direction.Axis.Z));

						if (nodeTypeBelow == PathType.WATER) {
							return false;
						}

						if (nodeTypeBelow == PathType.LAVA) {
							return false;
						}

						if (nodeTypeBelow == PathType.OPEN) {
							return false;
						}

						PathType nodeType = this.nodeEvaluator.getPathType(
							new PathfindingContext(this.level,this.mob),
							unswizzle(obx, by, obz, ax, ay, az, Direction.Axis.X), unswizzle(obx, by, obz, ax, ay, az, Direction.Axis.Y), unswizzle(obx, by, obz, ax, ay, az, Direction.Axis.Z)
						);
						float f = this.mob.getPathfindingMalus(nodeType);

						if (f < 0.0F || f >= 8.0F) {
							return false;
						}

						if (nodeType == PathType.DAMAGE_FIRE || nodeType == PathType.DANGER_FIRE || nodeType == PathType.DAMAGE_OTHER) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	protected boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 start, double dx, double dz, double minDotProduct, Direction.Axis ax, Direction.Axis ay, Direction.Axis az) {
		for (BlockPos pos : BlockPos.betweenClosed(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
			if (level.isLoaded(pos)) continue;
			double offsetX = swizzle(pos.getX(), pos.getY(), pos.getZ(), ax) + 0.5D - swizzle(start, ax);
			double pffsetZ = swizzle(pos.getX(), pos.getY(), pos.getZ(), az) + 0.5D - swizzle(start, az);

			if (offsetX * dx + pffsetZ * dz >= minDotProduct) {
				BlockState state = this.level.getBlockState(pos);

				if (!state.isPathfindable(PathComputationType.LAND)) {
					return false;
				}
			}
		}

		return true;
	}
}
