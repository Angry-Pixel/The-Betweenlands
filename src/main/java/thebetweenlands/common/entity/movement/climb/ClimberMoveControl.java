package thebetweenlands.common.entity.movement.climb;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import thebetweenlands.common.entity.ClimbingMob;
import thebetweenlands.common.entity.Orientation;

import javax.annotation.Nullable;

public class ClimberMoveControl extends MoveControl {

	protected final ClimbingMob climber;

	@Nullable
	protected BlockPos block;

	@Nullable
	protected Direction side;

	public ClimberMoveControl(ClimbingMob entity) {
		super(entity);
		this.climber = entity;
	}

	@Override
	public void setWantedPosition(double x, double y, double z, double speed) {
		this.setMoveTo(x, y, z, null, null, speed);
	}

	public void setMoveTo(double x, double y, double z, @Nullable BlockPos block, @Nullable Direction side, double speed) {
		super.setWantedPosition(x, y, z, speed);
		this.block = block;
		this.side = side;
	}

	@Override
	public void tick() {
		double speed = this.climber.getMovementSpeed() * this.speedModifier;

		if (this.operation == Operation.STRAFE) {
			this.operation = Operation.WAIT;

			float forward = this.strafeForwards;
			float strafe = this.strafeRight;
			float moveSpeed = Mth.sqrt(forward * forward + strafe * strafe);
			if (moveSpeed < 1.0F) {
				moveSpeed = 1.0F;
			}

			moveSpeed = (float) speed / moveSpeed;
			forward = forward * moveSpeed;
			strafe = strafe * moveSpeed;

			Orientation orientation = this.climber.getOrientation();

			Vec3 forwardVector = orientation.getGlobal(this.mob.getYRot(), 0);
			Vec3 strafeVector = orientation.getGlobal(this.mob.getYRot() + 90.0F, 0);

			if (!this.isWalkableAtOffset(forwardVector.x * forward + strafeVector.x * strafe, forwardVector.y * forward + strafeVector.y * strafe, forwardVector.z * forward + strafeVector.z * strafe)) {
				this.strafeForwards = 1.0F;
				this.strafeRight = 0.0F;
			}

			this.mob.setSpeed((float) speed);
			this.mob.setZza(this.strafeForwards);
			this.mob.setXxa(this.strafeRight);
		} else if (this.operation == Operation.MOVE_TO) {
			this.operation = Operation.WAIT;

			double dx = this.wantedX - this.mob.getX();
			double dy = this.wantedY - this.mob.getY();
			double dz = this.wantedZ - this.mob.getZ();

			if (this.side != null && this.block != null) {
				VoxelShape shape = this.mob.level().getBlockState(this.block).getCollisionShape(this.mob.level(), this.block);

				AABB aabb = this.mob.getBoundingBox();

				double ox = 0;
				double oy = 0;
				double oz = 0;

				//Use offset towards pathing side if mob is above that pathing side
				switch (this.side) {
					case DOWN:
						if (aabb.minY >= this.block.getY() + shape.max(Direction.Axis.Y) - 0.01D) {
							ox -= 0.1D;
						}
						break;
					case UP:
						if (aabb.maxY <= this.block.getY() + shape.min(Direction.Axis.Y) + 0.01D) {
							oy += 0.1D;
						}
						break;
					case WEST:
						if (aabb.minX >= this.block.getX() + shape.max(Direction.Axis.X) - 0.01D) {
							ox -= 0.1D;
						}
						break;
					case EAST:
						if (aabb.maxX <= this.block.getX() + shape.min(Direction.Axis.X) + 0.01D) {
							ox += 0.1D;
						}
						break;
					case NORTH:
						if (aabb.minZ >= this.block.getZ() + shape.max(Direction.Axis.Z) - 0.01D) {
							oz -= 0.1D;
						}
						break;
					case SOUTH:
						if (aabb.maxZ <= this.block.getZ() + shape.min(Direction.Axis.Z) + 0.01D) {
							oz += 0.1D;
						}
						break;
				}

				AABB blockAabb = new AABB(this.block.relative(this.side.getOpposite()));

				//If mob is on the pathing side block then only apply the offsets if the block is above the according side of the voxel shape
				if (aabb.intersects(blockAabb)) {
					Direction.Axis offsetAxis = this.side.getAxis();
					double offset = switch (offsetAxis) {
						case Y -> this.side.getStepY() * 0.5f;
						case Z -> this.side.getStepZ() * 0.5f;
						default -> this.side.getStepX() * 0.5f;
					};

					double allowedOffset = shape.collide(offsetAxis, aabb.move(-this.block.getX(), -this.block.getY(), -this.block.getZ()), offset);

					switch (this.side) {
						case DOWN:
							if (aabb.minY + allowedOffset < this.block.getY() + shape.max(Direction.Axis.Y) - 0.01D) {
								oy = 0;
							}
							break;
						case UP:
							if (aabb.maxY + allowedOffset > this.block.getY() + shape.min(Direction.Axis.Y) + 0.01D) {
								oy = 0;
							}
							break;
						case WEST:
							if (aabb.minX + allowedOffset < this.block.getX() + shape.max(Direction.Axis.X) - 0.01D) {
								ox = 0;
							}
							break;
						case EAST:
							if (aabb.maxX + allowedOffset > this.block.getX() + shape.min(Direction.Axis.X) + 0.01D) {
								ox = 0;
							}
							break;
						case NORTH:
							if (aabb.minZ + allowedOffset < this.block.getZ() + shape.max(Direction.Axis.Z) - 0.01D) {
								oz = 0;
							}
							break;
						case SOUTH:
							if (aabb.maxZ + allowedOffset > this.block.getZ() + shape.min(Direction.Axis.Z) + 0.01D) {
								oz = 0;
							}
							break;
					}
				}

				dx += ox;
				dy += oy;
				dz += oz;
			}

			Direction mainOffsetDir = Direction.getNearest(dx, dy, dz);

			float reach = switch (mainOffsetDir) {
				case DOWN -> -0.5F;
				case UP -> this.mob.getBbHeight();
				default -> this.mob.getBbWidth() * 0.5f;
			};

			double verticalOffset = Math.abs(mainOffsetDir.getStepX() * dx) + Math.abs(mainOffsetDir.getStepY() * dy) + Math.abs(mainOffsetDir.getStepZ() * dz);

			Direction groundDir = this.climber.getGroundSide();

			if (this.side != null && verticalOffset > reach - 0.05f && groundDir != this.side && groundDir.getAxis() != this.side.getAxis()) {
				double hdx = (1 - Math.abs(mainOffsetDir.getStepX())) * dx;
				double hdy = (1 - Math.abs(mainOffsetDir.getStepY())) * dy;
				double hdz = (1 - Math.abs(mainOffsetDir.getStepZ())) * dz;

				double hdsq = hdx * hdx + hdy * hdy + hdz * hdz;
				if (hdsq < 0.707f) {
					dx -= this.side.getStepX() * 0.2f;
					dy -= this.side.getStepY() * 0.2f;
					dz -= this.side.getStepZ() * 0.2f;
				}
			}

			Orientation orientation = this.climber.getOrientation();

			Vec3 up = orientation.getGlobal(this.mob.getYRot(), -90);

			Vec3 offset = new Vec3(dx, dy, dz);

			Vec3 targetDir = offset.subtract(up.scale(offset.dot(up)));
			double targetDist = targetDir.length();
			targetDir = targetDir.normalize();

			if (targetDist < 0.0001D) {
				this.mob.setZza(0);
			} else {
				float rx = (float) orientation.localZ().dot(targetDir);
				float ry = (float) orientation.localX().dot(targetDir);

				this.mob.setYRot(this.rotlerp(this.mob.getYRot(), 270.0f - (float) Math.toDegrees(Mth.atan2(rx, ry)), 90.0f));

				this.mob.setSpeed((float) speed);
			}
		} else if (this.operation == Operation.JUMPING) {
			this.mob.setSpeed((float) speed);

			if (this.mob.onGround()) {
				this.operation = Operation.WAIT;
			}
		} else {
			this.mob.setZza(0);
		}
	}

	private boolean isWalkableAtOffset(double x, double y, double z) {
		NodeEvaluator evaluator = this.mob.getNavigation().getNodeEvaluator();

		return evaluator.getPathType(new PathfindingContext(this.mob.level(), this.mob), Mth.floor(this.mob.getX() + x), Mth.floor(this.mob.getY() + this.mob.getBbHeight() * 0.5f + y), Mth.floor(this.mob.getZ() + z)) == PathType.WALKABLE;
	}
}