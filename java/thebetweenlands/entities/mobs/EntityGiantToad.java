package thebetweenlands.entities.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.model.ControlledAnimation;

/**
 * Created by jnad325 on 7/14/15.
 */
public class EntityGiantToad extends EntityCreature implements IEntityBL {
	private int ticksOnGround = 0;
	private int strokeTicks = 0;
	private boolean prevOnGround;
	private ControlledAnimation leapingAnim = new ControlledAnimation(4);
	private ControlledAnimation swimmingAnim = new ControlledAnimation(8);
	
	public EntityGiantToad(World worldObj) {
		super(worldObj);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(5, new EntityAIWander(this, 0));
		this.setSize(2F, 1.5F);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onUpdate() {
		prevOnGround = onGround;
		super.onUpdate();
		if(this.onGround) {
			this.ticksOnGround++;
		} else {
			this.ticksOnGround = 0;
		}
		//TODO: RIP TPS, 2016 - 2016
		//Keeps trying to pathfind causing huge stress on TPS
		if(this.strokeTicks > 0) {
			this.strokeTicks--;
		}
		if (!worldObj.isRemote) {
			this.setAir(20);
			/*if (getAttackTarget() != null) {
				getNavigator().tryMoveToEntityLiving(getAttackTarget(), 0);
			}*/
			PathEntity path = getNavigator().getPath();
			if (path != null && !path.isFinished() && onGround && !this.isMovementBlocked()) {
				if (this.ticksOnGround > 20) {
					int index = path.getCurrentPathIndex();
					if (index < path.getCurrentPathLength()) {
						PathPoint nextHopSpot = path.getPathPointFromIndex(index);
						float x = (float) (nextHopSpot.xCoord - posX);
						float z = (float) (nextHopSpot.zCoord - posZ);
						float angle = (float) (Math.atan2(z, x));
						float distance = (float) Math.sqrt(x * x + z * z);
						if (distance > 1) {
							motionY += 0.6;
							motionX += 0.3 * MathHelper.cos(angle);
							motionZ += 0.3 * MathHelper.sin(angle);
						}/* else {
							getNavigator().clearPathEntity();
						}*/
					}
				}
			}
		} else {
			leapingAnim.updateTimer();
			// allow 1 tick lag time to prevent single tick onGround == false
			if (this.inWater || onGround || prevOnGround) {
				leapingAnim.decreaseTimer();
			} else {
				leapingAnim.increaseTimer();
			}
			
			this.swimmingAnim.updateTimer();
			if(this.inWater && Math.sqrt(this.motionX*this.motionX+this.motionZ*this.motionZ) > 0.04D) {
				this.swimmingAnim.increaseTimer();
			} else {
				this.swimmingAnim.decreaseTimer();
			}
		}
	}

	public float getLeapProgress(float partialRenderTicks) {
		return easeInOut(leapingAnim.getAnimationProgressSinSqrt(partialRenderTicks));
	}
	
	public float getSwimProgress(float partialRenderTicks) {
		return easeInOut(this.swimmingAnim.getAnimationProgressSinToTenWithoutReturn(partialRenderTicks));
	}

	private static float easeInOut(float t) {
		return t;
		//		final float d = 1, c = 1, b = 0;
		//		float s = 1.70158f;
		//		if ((t /= d / 2) < 1)
		//			return c / 2 * (t * t * (((s *= (1.525f)) + 1) * t - s)) + b;
		//		return c / 2 * ((t -= 2) * t * (((s *= (1.525f)) + 1) * t + s) + 2) + b;
	}

	@Override
	public String pageName() {
		return "giantToad";
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:giantToadLiving";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:giantToadHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:giantToadDeath";
	}

	@Override
	public boolean interact(EntityPlayer player) {
		if(this.riddenByEntity == null) {
			player.mountEntity(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean canBePushed() {
		return this.riddenByEntity == null;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		Entity entity = source.getEntity();
		return this.riddenByEntity != null && this.riddenByEntity.equals(entity) ? false : super.attackEntityFrom(source, damage);
	}

	@Override
	protected boolean isMovementBlocked() {
		return this.riddenByEntity != null ? true : super.isMovementBlocked();
	}

	@Override
	public void moveEntityWithHeading(float strafing, float forward) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
			this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
			this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
			strafing = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
			forward = ((EntityLivingBase)this.riddenByEntity).moveForward;

			if (forward <= 0.0F) {
				forward *= 0.25F;
			}

			boolean onWaterSurface = !this.onGround && this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 1), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid();
			
			if(!onWaterSurface) {
				if (this.onGround && forward != 0.0F && this.ticksOnGround > 4) {
					if(!this.worldObj.isRemote) {
						motionY += 0.6;
						motionX += forward * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
						motionZ += forward * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
						motionX += strafing / 2.0F * MathHelper.cos((float) Math.toRadians(this.rotationYaw));
						motionZ += strafing / 2.0F * MathHelper.sin((float) Math.toRadians(this.rotationYaw));
					}
				}
			} else {
				if(this.motionY < 0.0F)
					this.motionY *= 0.25F;
				this.motionY += 0.01F;

				if(!this.worldObj.isRemote) {
					if(forward != 0.0F && this.strokeTicks == 0) {
						motionX += forward / 1.25F * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
						motionZ += forward / 1.25F * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
						this.strokeTicks = 20;
					}
				}
			}

			super.moveEntityWithHeading(0, 0);
		} else {
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.moveEntityWithHeading(strafing, forward);
		}
	}
}
