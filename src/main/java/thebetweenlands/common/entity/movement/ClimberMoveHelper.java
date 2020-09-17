package thebetweenlands.common.entity.movement;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityClimberBase;

public class ClimberMoveHelper extends EntityMoveHelper {
	protected int courseChangeCooldown;
	protected boolean blocked = false;

	protected final EntityClimberBase climber;

	public ClimberMoveHelper(EntityClimberBase entity) {
		super(entity);
		this.climber = entity;
	}

	@Override
	public void onUpdateMoveHelper() {
		double speed = this.climber.getMovementSpeed() * this.speed;

		if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			Pair<EnumFacing, Vec3d> walkingSide = this.climber.getWalkingSide();
			Vec3d normal = /*walkingSide.getRight();*/new Vec3d(walkingSide.getLeft().getXOffset(), walkingSide.getLeft().getYOffset(), walkingSide.getLeft().getZOffset());

			double dx = this.posX - this.entity.posX;
			double dy = this.posY + 0.5f - (this.entity.posY + this.entity.height / 2.0f);
			double dz = this.posZ - this.entity.posZ;

			Vec3d dir = new Vec3d(dx, dy, dz);

			Vec3d targetDir = dir.subtract(normal.scale(dir.dotProduct(normal)));

			double targetDist = targetDir.length();

			if(targetDist < 0.1D) {
				this.entity.setMoveForward(0);
				this.action = EntityMoveHelper.Action.WAIT;
			} else {
				double moveDx = this.posX + (this.posX < this.entity.posX ? -this.entity.width / 2 : this.entity.width / 2) - this.entity.posX;
				double moveDy = this.posY + 0.5f - (this.posY < this.entity.posY ? this.entity.height : 0) - this.entity.posY;
				double moveDz = this.posZ + (this.posZ < this.entity.posZ ? -this.entity.width / 2 : this.entity.width / 2) - this.entity.posZ;

				Vec3d moveDir = new Vec3d(moveDx, moveDy, moveDz);

				Vec3d walkingDir = moveDir.subtract(normal.scale(moveDir.dotProduct(normal)));

				double walkingDist = walkingDir.length();

				BlockPos offsetPos = new BlockPos(this.entity).offset(walkingSide.getLeft());
				IBlockState offsetState = this.entity.world.getBlockState(offsetPos);
				float blockSlipperiness = offsetState.getBlock().getSlipperiness(offsetState, this.entity.world, offsetPos, this.entity);

				float slipperiness = blockSlipperiness * 0.91F;

				float friction = (float)speed * 0.16277136F / (slipperiness * slipperiness * slipperiness);

				float f = (float)(speed * speed);
				if(f >= 1.0E-4F) {
					f = Math.max(MathHelper.sqrt(f), 1.0f);
					f = friction / f;

					//When moving around edges redirect some of the previous motion
					if(this.entity.onGround) {
						Vec3d redirectedMotion = new Vec3d(this.entity.motionX, this.entity.motionY, this.entity.motionZ);
						double motionSpeed = redirectedMotion.length();
						redirectedMotion = redirectedMotion.subtract(normal.scale(redirectedMotion.dotProduct(normal))).normalize().scale(motionSpeed);

						this.entity.motionX = (redirectedMotion.x + this.entity.motionX) * 0.5f;
						this.entity.motionY = (redirectedMotion.y + this.entity.motionY) * 0.5f;
						this.entity.motionZ = (redirectedMotion.z + this.entity.motionZ) * 0.5f;
					}

					//Nullify sticking force along movement vector
					Vec3d stickingForce = this.climber.getStickingForce(walkingSide);
					Vec3d counterStickingForce = stickingForce.subtract(normal.scale(stickingForce.dotProduct(normal))).scale(-1);

					this.entity.motionX += walkingDir.x / walkingDist * speed * f + counterStickingForce.x;
					this.entity.motionY += walkingDir.y / walkingDist * speed * f + counterStickingForce.y;
					this.entity.motionZ += walkingDir.z / walkingDist * speed * f + counterStickingForce.z;

					EntityClimberBase.Orientation orientation = this.climber.getOrientation(1);

					float rx = (float)orientation.forward.dotProduct(walkingDir.normalize());
					float ry = (float)orientation.right.dotProduct(walkingDir.normalize());

					this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, 270.0f - (float)Math.toDegrees(Math.atan2(rx, ry)), 90.0f);

					this.entity.setAIMoveSpeed((float)speed);
				}
			}
		}
	}
}