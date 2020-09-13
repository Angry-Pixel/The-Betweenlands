package thebetweenlands.common.entity.mobs;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.IPathObstructionAwareEntity;
import thebetweenlands.common.entity.ai.ObstructionAwarePathNavigateGround;
import thebetweenlands.common.entity.movement.ClimberMoveHelper;
import thebetweenlands.util.BoxSmoothingUtil;
import thebetweenlands.util.Matrix;

public abstract class EntityClimberBase extends EntityCreature implements IEntityBL, IPathObstructionAwareEntity {

	public double prevRenderOffsetX, prevRenderOffsetY, prevRenderOffsetZ;
	public double renderOffsetX, renderOffsetY, renderOffsetZ;

	public Vec3d orientationNormal = new Vec3d(0, 1, 0);
	public Vec3d prevOrientationNormal = new Vec3d(0, 1, 0);

	protected float stickingDistance = 2.0f;

	protected float collisionsInclusionRange = 2.0f;
	protected float collisionsSmoothingRange = 1.25f;

	protected int maxPathingTargetHeight = 0;

	public EntityClimberBase(World world) {
		super(world);
		this.setSize(0.9F, 0.9F);
		this.moveHelper = new ClimberMoveHelper(this);
	}

	@Override
	protected PathNavigate createNavigator(World worldIn) {
		ObstructionAwarePathNavigateGround<EntityClimberBase> navigate = new ObstructionAwarePathNavigateGround<EntityClimberBase>(this, worldIn, false, true, true, false) {
			@Override
			public Path getPathToEntityLiving(Entity entityIn) {
				BlockPos pos = new BlockPos(entityIn);

				//Path to ceiling above target if possible
				for(int i = 1; i <= EntityClimberBase.this.maxPathingTargetHeight; i++) {
					if(this.getNodeProcessor().getPathNodeType(this.world, pos.getX(), pos.getY() + i, pos.getZ()) == PathNodeType.WALKABLE) {
						pos = pos.up(i);
						break;
					}
				}

				return this.getPathToPos(pos);
			}
		};
		navigate.setCanSwim(true);
		return navigate;
	}

	@Override
	public float getBridgePathingMalus(EntityLiving entity, BlockPos pos, PathPoint fallPathPoint) {
		return -1.0f;
	}

	@Override
	public void onPathingObstructed(EnumFacing facing) {

	}

	@Override
	public int getMaxFallHeight() {
		return 1;
	}

	public Pair<EnumFacing, Vec3d> getWalkingSide() {
		EnumFacing avoidPathingFacing = EnumFacing.DOWN;

		Path path = this.getNavigator().getPath();
		if(path != null) {
			int index = path.getCurrentPathIndex();

			if(index < path.getCurrentPathLength()) {
				PathPoint point = path.getPathPointFromIndex(index);

				double maxDist = 0;

				for(EnumFacing facing : EnumFacing.VALUES) {
					double posEntity = Math.abs(facing.getXOffset()) * this.posX + Math.abs(facing.getYOffset()) * this.posY + Math.abs(facing.getZOffset()) * this.posZ;
					double posPath = Math.abs(facing.getXOffset()) * point.x + Math.abs(facing.getYOffset()) * point.y + Math.abs(facing.getZOffset()) * point.z;

					double distSigned = posPath + 0.5f - posEntity;
					if(distSigned * (facing.getXOffset() + facing.getYOffset() + facing.getZOffset()) > 0) {
						double dist = Math.abs(distSigned) - (facing.getAxis().isHorizontal() ? this.width / 2 : (facing == EnumFacing.DOWN ? 0 : this.height));

						if(dist > maxDist) {
							maxDist = dist;
							avoidPathingFacing = facing.getOpposite();
						}
					}
				}
			}
		}

		AxisAlignedBB entityBox = this.getEntityBoundingBox();

		double closestFacingDst = Double.MAX_VALUE;
		EnumFacing closestFacing = EnumFacing.DOWN;

		Vec3d weighting = new Vec3d(0, 0, 0);

		for(EnumFacing facing : EnumFacing.VALUES) {
			if(avoidPathingFacing == facing) {
				continue;
			}

			List<AxisAlignedBB> collisionBoxes = this.world.getCollisionBoxes(this, entityBox.grow(0.2f).expand(facing.getXOffset() * this.stickingDistance, facing.getYOffset() * this.stickingDistance, facing.getZOffset() * this.stickingDistance));

			double closestDst = Double.MAX_VALUE;

			for(AxisAlignedBB collisionBox : collisionBoxes) {
				switch(facing) {
				case EAST:
				case WEST:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateXOffset(collisionBox, -facing.getXOffset() * this.stickingDistance)));
					break;
				case UP:
				case DOWN:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateYOffset(collisionBox, -facing.getYOffset() * this.stickingDistance)));
					break;
				case NORTH:
				case SOUTH:
					closestDst = Math.min(closestDst, Math.abs(entityBox.calculateZOffset(collisionBox, -facing.getZOffset() * this.stickingDistance)));
					break;
				}
			}

			if(closestDst < closestFacingDst) {
				closestFacingDst = closestDst;
				closestFacing = facing;
			}

			if(closestDst < Double.MAX_VALUE) {
				weighting = weighting.add(new Vec3d(facing.getXOffset(), facing.getYOffset(), facing.getZOffset()).scale(1 - Math.min(closestDst, this.stickingDistance) / this.stickingDistance));
			}
		}

		return Pair.of(closestFacing, weighting.normalize().add(0, -0.001f, 0).normalize());
	}

	public static class Orientation {
		public final Vec3d normal, forward, up, right;
		public final float forwardComponent, upComponent, rightComponent, yaw, pitch;

		private Orientation(Vec3d normal, Vec3d forward, Vec3d up, Vec3d right, float forwardComponent, float upComponent, float rightComponent, float yaw, float pitch) {
			this.normal = normal;
			this.forward = forward;
			this.up = up;
			this.right = right;
			this.forwardComponent = forwardComponent;
			this.upComponent = upComponent;
			this.rightComponent = rightComponent;
			this.yaw = yaw;
			this.pitch = pitch;
		}
	}

	public Orientation getOrientation(float partialTicks) {
		//Big oof, please don't look at this

		Vec3d orientationNormal = this.prevOrientationNormal.add(this.orientationNormal.subtract(this.prevOrientationNormal).scale(partialTicks));

		Vec3d fwdAxis = new Vec3d(0, 0, 1);
		Vec3d upAxis = new Vec3d(0, 1, 0);
		Vec3d rightAxis = new Vec3d(1, 0, 0);

		float fwd = (float)fwdAxis.dotProduct(orientationNormal);
		float up = (float)upAxis.dotProduct(orientationNormal);
		float right = (float)rightAxis.dotProduct(orientationNormal);

		float yaw = (float)Math.toDegrees(Math.atan2(right, fwd));

		fwdAxis = new Vec3d(Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));
		upAxis = new Vec3d(0, 1, 0);
		rightAxis = new Vec3d(Math.sin(Math.toRadians(yaw - 90)), 0, Math.cos(Math.toRadians(yaw - 90)));

		fwd = (float)fwdAxis.dotProduct(orientationNormal);
		up = (float)upAxis.dotProduct(orientationNormal);
		right = (float)rightAxis.dotProduct(orientationNormal);

		float pitch = (float)Math.toDegrees(Math.atan2(fwd, up)) * (float)Math.signum(fwd);

		Matrix m = new Matrix();
		m.rotate(Math.toRadians(yaw), 0, 1, 0);
		m.rotate(Math.toRadians(pitch), 1, 0, 0);
		m.rotate(Math.toRadians((float)Math.signum(0.1f - up) * yaw), 0, 1, 0);

		Vec3d localFwd = m.transform(new Vec3d(0, 0, -1));
		Vec3d localUp = m.transform(new Vec3d(0, 1, 0));
		Vec3d localRight = m.transform(new Vec3d(1, 0, 0));

		return new Orientation(orientationNormal, localFwd, localUp, localRight, fwd, up, right, yaw, pitch);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.updateOffsetsAndOrientation();
	}

	protected void updateOffsetsAndOrientation() {
		Vec3d p = this.getPositionVector();

		Vec3d s = p.add(0, this.height / 2, 0);
		AxisAlignedBB inclusionBox = new AxisAlignedBB(s.x, s.y, s.z, s.x, s.y, s.z).grow(this.collisionsInclusionRange);

		List<AxisAlignedBB> boxes = this.world.getCollisionBoxes(this, inclusionBox);

		Pair<Vec3d, Vec3d> closestSmoothPoint = BoxSmoothingUtil.findClosestSmoothPoint(boxes, this.collisionsSmoothingRange, 1.0f, 0.005f, 20, 0.05f, s);

		this.prevRenderOffsetX = this.renderOffsetX;
		this.prevRenderOffsetY = this.renderOffsetY;
		this.prevRenderOffsetZ = this.renderOffsetZ;

		this.renderOffsetX = MathHelper.clamp(closestSmoothPoint.getLeft().x - p.x, -this.width / 2, this.width / 2);
		this.renderOffsetY = MathHelper.clamp(closestSmoothPoint.getLeft().y - p.y, 0, this.height);
		this.renderOffsetZ = MathHelper.clamp(closestSmoothPoint.getLeft().z - p.z, -this.width / 2, this.width / 2);

		this.prevOrientationNormal = this.orientationNormal;
		this.orientationNormal = closestSmoothPoint.getRight();
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		if(this.isServerWorld() || this.canPassengerSteer()) {
			Pair<EnumFacing, Vec3d> walkingSide = this.getWalkingSide();

			if(!this.hasNoGravity()) {
				//"Gravity"
				this.motionX += walkingSide.getRight().x * 0.04D;
				this.motionY += walkingSide.getRight().y * 0.04D;
				this.motionZ += walkingSide.getRight().z * 0.04D;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

			if(this.collidedHorizontally || this.collidedVertically) {
				this.onGround = true;
				this.fallDistance = 0;

				BlockPos offsetPos = new BlockPos(this).offset(walkingSide.getLeft());
				IBlockState offsetState = this.world.getBlockState(offsetPos);
				float blockSlipperiness = offsetState.getBlock().getSlipperiness(offsetState, this.world, offsetPos, this);

				float slipperiness = blockSlipperiness * 0.91F;

				switch(walkingSide.getLeft().getAxis()) {
				case X:
					this.motionZ *= slipperiness;
					this.motionY *= slipperiness;
					this.motionX = 0;
					break;
				case Y:
					this.motionX *= slipperiness;
					this.motionZ *= slipperiness;
					this.motionY = 0;
					break;
				case Z:
					this.motionX *= slipperiness;
					this.motionY *= slipperiness;
					this.motionZ = 0;
					break;
				}
			}
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double traveledX = this.posX - this.prevPosX;
		double traveledY = this.posY - this.prevPosY;
		double traveledZ = this.posZ - this.prevPosZ;
		float traveled = Math.min(MathHelper.sqrt(traveledX * traveledX + traveledY * traveledY + traveledZ * traveledZ) * 4.0f, 1.0f);

		this.limbSwingAmount += (traveled - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}
}