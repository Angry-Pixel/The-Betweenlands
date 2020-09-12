package thebetweenlands.common.entity.movement;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.common.entity.mobs.EntityClimber;

public class ClimbMoveHelper extends EntityMoveHelper {
	protected int courseChangeCooldown;
	protected boolean blocked = false;

	protected final EntityClimber climber;

	public ClimbMoveHelper(EntityClimber entity) {
		super(entity);
		this.climber = entity;
	}

	@Override
	public void onUpdateMoveHelper() {
		IAttributeInstance entityMoveSpeedAttribute = this.entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
		double speed = entityMoveSpeedAttribute != null ? this.speed * entityMoveSpeedAttribute.getAttributeValue() : this.speed;

		if(this.action == EntityMoveHelper.Action.MOVE_TO) {
			double dx = this.posX + (this.posX < this.entity.posX ? -this.entity.width / 2 : this.entity.width / 2) - this.entity.posX;
			double dy = this.posY + 0.5f - (this.posY < this.entity.posY ? this.entity.height : 0) - this.entity.posY;
			double dz = this.posZ + (this.posZ < this.entity.posZ ? -this.entity.width / 2 : this.entity.width / 2) - this.entity.posZ;

			Vec3d dir = new Vec3d(dx, dy, dz);

			Pair<EnumFacing, Vec3d> walkingSide = this.climber.getWalkingSide();
			Vec3d normal = new Vec3d(walkingSide.getLeft().getXOffset(), walkingSide.getLeft().getYOffset(), walkingSide.getLeft().getZOffset());

			Vec3d walkingDir = dir.subtract(normal.scale(dir.dotProduct(normal)));

			double walkingDist = walkingDir.length();

			if(walkingDist < 0.1D) {
				this.entity.setMoveForward(0);
				this.action = EntityMoveHelper.Action.WAIT;
			} else {
				BlockPos offsetPos = new BlockPos(this.entity).offset(walkingSide.getLeft());
				IBlockState offsetState = this.entity.world.getBlockState(offsetPos);
				float blockSlipperiness = offsetState.getBlock().getSlipperiness(offsetState, this.entity.world, offsetPos, this.entity);

				float slipperiness = blockSlipperiness * 0.91F;

				float friction = (float)speed * 0.16277136F / (slipperiness * slipperiness * slipperiness);

				float f = (float)(speed * speed);
				if(f >= 1.0E-4F) {
					f = MathHelper.sqrt(f);
					if (f < 1.0F) f = 1.0F;
					f = friction / f;

					this.entity.motionX += walkingDir.x / walkingDist * speed * f;
					this.entity.motionY += walkingDir.y / walkingDist * speed * f;
					this.entity.motionZ += walkingDir.z / walkingDist * speed * f;

					this.entity.setAIMoveSpeed((float)speed);
				}
			}
		}
	}
}