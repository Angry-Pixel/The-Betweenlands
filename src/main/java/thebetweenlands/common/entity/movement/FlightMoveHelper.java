package thebetweenlands.common.entity.movement;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FlightMoveHelper extends EntityMoveHelper {

    public EntityCreature entity;
    private int courseChangeCooldown;
    protected boolean blocked = false;

    public FlightMoveHelper(EntityCreature entity) {
        super(entity);
        this.entity = entity;
    }

    public void onUpdateMoveHelper() {
        if (this.action == EntityMoveHelper.Action.MOVE_TO) {
            double x = this.posX - this.entity.posX;
            double y = this.posY + 0.5D - this.entity.posY;
            double z = this.posZ - this.entity.posZ;
            float distance = (float) Math.sqrt(x * x + y * y + z * z);

            if (this.courseChangeCooldown-- <= 0) {
                this.courseChangeCooldown += this.entity.getRNG().nextInt(5) + 2;
                if(distance >= 1D) {
                	this.entity.motionX += (Math.signum(x) * 0.5D - entity.motionX) * entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 0.5D;
                	this.entity.motionY += (Math.signum(y) * 0.5D - entity.motionY) * 0.2D;
                    this.entity.motionZ += (Math.signum(z) * 0.5D - entity.motionZ) * entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() * 0.5D;
					float angle = (float) (Math.atan2(entity.motionZ, entity.motionX) * 180.0D / Math.PI) - 90.0F;
					float rotation = MathHelper.wrapDegrees(angle - entity.rotationYaw);
					entity.rotationYaw += rotation;
					this.blocked = false;
                }
                else {
                    this.action = EntityMoveHelper.Action.WAIT;
                    this.blocked = true;
                }
            }
        }

        if (this.entity.getAttackTarget() != null) {
            EntityLivingBase entitylivingbase = this.entity.getAttackTarget();
            double distanceX = entitylivingbase.posX - this.entity.posX;
            double distanceZ = entitylivingbase.posZ - this.entity.posZ;
            this.entity.renderYawOffset = this.entity.rotationYaw = -((float)MathHelper.atan2(distanceX, distanceZ)) * (180F / (float)Math.PI);
        }
        else if(this.action == EntityMoveHelper.Action.MOVE_TO) {
            this.entity.renderYawOffset = this.entity.rotationYaw = -((float)MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * (180F / (float)Math.PI);
        }
    }

	/**
	 * Returns whether the path is currently blocked
	 * @return
	 */
	public boolean isBlocked() {
		return this.blocked;
	}


	/**
	 * Returns whether the entity will collide on the current path
	 * @param x
	 * @param y
	 * @param z
	 * @param step
	 * @return
	 */
	protected boolean isNotColliding(double x, double y, double z, double step) {
		if(this.entity.noClip)
			return true;

		double stepX = (x - this.entity.posX) / step;
		double stepY = (y - this.entity.posY) / step;
		double stepZ = (z - this.entity.posZ) / step;
		AxisAlignedBB aabb = this.entity.getEntityBoundingBox();

		for(int i = 1; (double)i < step; ++i) {
			aabb = aabb.offset(stepX, stepY, stepZ);

			if(this.isBlocked(aabb)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns whether the entities path is blocked at the specified AABB
	 * @param aabb
	 * @return
	 */
	protected boolean isBlocked(AxisAlignedBB aabb) {
		return !this.entity.world.getCollisionBoxes(this.entity, aabb).isEmpty();
	}

	/**
	 * Returns the flight speed
	 * @return
	 */
	protected double getFlightSpeed() {
		return this.speed;
	}

	/**
	 * Returns the ground height at the specified block position
	 * @param world
	 * @param pos
	 * @param maxIter
	 * @param fallback
	 * @return
	 */
	public static BlockPos getGroundHeight(World world, BlockPos pos, int maxIter, BlockPos fallback) {
		if(world.canSeeSky(pos)) {
			return world.getHeight(pos);
		}
		MutableBlockPos mutablePos = new MutableBlockPos();
		int i = 0;
		for(; i < maxIter; i++) {
			mutablePos.setPos(pos.getX(), pos.getY() - i, pos.getZ());
			if(!world.isAirBlock(mutablePos))
				break;
		}
		if(i < maxIter) {
			return new BlockPos(pos.getX(), pos.getY() - i, pos.getZ());
		}
		return fallback;
	}
}