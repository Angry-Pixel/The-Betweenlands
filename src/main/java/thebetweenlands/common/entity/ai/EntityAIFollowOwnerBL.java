package thebetweenlands.common.entity.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Same as {@link EntityAIFollowOwner} but without caching the navigator
 */
public class EntityAIFollowOwnerBL extends EntityAIBase
{
	private final EntityTameable tameable;
	private EntityLivingBase owner;
	World world;
	private final double followSpeed;
	private int timeToRecalcPath;
	float maxDist;
	float minDist;
	private float oldWaterCost;

	public EntityAIFollowOwnerBL(EntityTameable tameableIn, double followSpeedIn, float minDistIn, float maxDistIn)
	{
		this.tameable = tameableIn;
		this.world = tameableIn.world;
		this.followSpeed = followSpeedIn;
		this.minDist = minDistIn;
		this.maxDist = maxDistIn;
		this.setMutexBits(3);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute()
	{
		EntityLivingBase entitylivingbase = this.tameable.getOwner();

		if (entitylivingbase == null)
		{
			return false;
		}
		else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).isSpectator())
		{
			return false;
		}
		else if (this.tameable.isSitting())
		{
			return false;
		}
		else if (this.tameable.getDistanceSq(entitylivingbase) < (double)(this.minDist * this.minDist))
		{
			return false;
		}
		else
		{
			this.owner = entitylivingbase;
			return true;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting()
	{
		return !this.tameable.getNavigator().noPath() && this.tameable.getDistanceSq(this.owner) > (double)(this.maxDist * this.maxDist) && !this.tameable.isSitting();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting()
	{
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.tameable.getPathPriority(PathNodeType.WATER);
		this.tameable.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	@Override
	public void resetTask()
	{
		this.owner = null;
		this.tameable.getNavigator().clearPath();
		this.tameable.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void updateTask()
	{
		this.tameable.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float)this.tameable.getVerticalFaceSpeed());

		if (!this.tameable.isSitting())
		{
			if (--this.timeToRecalcPath <= 0)
			{
				this.timeToRecalcPath = 10;

				if (!this.tameable.getNavigator().tryMoveToEntityLiving(this.owner, this.followSpeed))
				{
					if (!this.tameable.getLeashed() && !this.tameable.isRiding())
					{
						if (this.tameable.getDistanceSq(this.owner) >= 144.0D)
						{
							int i = MathHelper.floor(this.owner.posX) - 2;
							int j = MathHelper.floor(this.owner.posZ) - 2;
							int k = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

							for (int l = 0; l <= 4; ++l)
							{
								for (int i1 = 0; i1 <= 4; ++i1)
								{
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1))
									{
										this.tameable.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.tameable.rotationYaw, this.tameable.rotationPitch);
										this.tameable.getNavigator().clearPath();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset)
	{
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		IBlockState iblockstate = this.world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.tameable) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
	}
}