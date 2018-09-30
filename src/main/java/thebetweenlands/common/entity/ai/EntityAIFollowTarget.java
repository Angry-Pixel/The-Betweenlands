package thebetweenlands.common.entity.ai;

import java.util.function.Supplier;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowTarget extends EntityAIBase {
	protected final EntityLiving taskOwner;
	protected final Supplier<EntityLivingBase> target;
	protected World theWorld;
	protected final double speed;
	protected final PathNavigate navigator;
	protected int timeToRecalcPath;
	protected float maxDist;
	protected float minDist;
	protected float oldWaterCost;

	public EntityAIFollowTarget(EntityLiving taskOwner, Supplier<EntityLivingBase> target, double speed, float minDist, float maxDist) {
		this.taskOwner = taskOwner;
		this.theWorld = taskOwner.world;
		this.target = target;
		this.speed = speed;
		this.navigator = taskOwner.getNavigator();
		this.minDist = minDist;
		this.maxDist = maxDist;
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase target = this.target.get();

		if (target == null) {
			return false;
		} else if (target instanceof EntityPlayer && ((EntityPlayer)target).isSpectator()) {
			return false;
		} else if (this.taskOwner.getDistanceSq(target) < (double)(this.minDist * this.minDist)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean shouldContinueExecuting() {
		EntityLivingBase target = this.target.get();
		return target != null && !this.navigator.noPath() && this.taskOwner.getDistanceSq(target) > (double)(this.maxDist * this.maxDist);
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.taskOwner.getPathPriority(PathNodeType.WATER);
		this.taskOwner.setPathPriority(PathNodeType.WATER, 0.0F);
	}

	@Override
	public void resetTask() {
		this.navigator.clearPath();
		this.taskOwner.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	private boolean isEmptyBlock(BlockPos pos) {
		IBlockState blockState = this.theWorld.getBlockState(pos);
		return blockState.getMaterial() == Material.AIR ? true : !blockState.isFullCube();
	}

	@Override
	public void updateTask() {
		EntityLivingBase target = this.target.get();

		if(target != null) {
			this.taskOwner.getLookHelper().setLookPositionWithEntity(target, 10.0F, (float)this.taskOwner.getVerticalFaceSpeed());

			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10;

				if (!this.navigator.tryMoveToEntityLiving(target, this.speed)) {
					if (!this.taskOwner.getLeashed()) {
						if (this.taskOwner.getDistanceSq(target) >= 144.0D) {
							int i = MathHelper.floor(target.posX) - 2;
							int j = MathHelper.floor(target.posZ) - 2;
							int k = MathHelper.floor(target.getEntityBoundingBox().minY);

							for (int l = 0; l <= 4; ++l) {
								for (int i1 = 0; i1 <= 4; ++i1) {
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i1)).isBlockNormalCube() && this.isEmptyBlock(new BlockPos(i + l, k, j + i1)) && this.isEmptyBlock(new BlockPos(i + l, k + 1, j + i1))) {
										this.taskOwner.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.taskOwner.rotationYaw, this.taskOwner.rotationPitch);
										this.navigator.clearPath();
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
}