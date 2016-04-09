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

	public static final int DW_SWIM_STROKE = 20;

	public EntityGiantToad(World worldObj) {
		super(worldObj);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(5, new EntityAIWander(this, 0));
		this.setSize(2F, 1.5F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(DW_SWIM_STROKE, (byte) 0);
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
		if(this.strokeTicks > 0) {
			this.strokeTicks--;
			if(!this.worldObj.isRemote)
				this.dataWatcher.updateObject(DW_SWIM_STROKE, (byte) 1);
		} else if(!this.worldObj.isRemote) {
			this.dataWatcher.updateObject(DW_SWIM_STROKE, (byte) 0);
		}
		if (!worldObj.isRemote) {
			this.setAir(20);
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
							motionX += 0.5 * MathHelper.cos(angle);
							motionZ += 0.5 * MathHelper.sin(angle);
						}
					}
				}
			}
		}

		leapingAnim.updateTimer();
		if (this.inWater || onGround || prevOnGround) {
			leapingAnim.decreaseTimer();
		} else {
			leapingAnim.increaseTimer();
		}

		this.swimmingAnim.updateTimer();
		if(this.inWater && this.dataWatcher.getWatchableObjectByte(DW_SWIM_STROKE) == 1 && Math.sqrt(this.motionX*this.motionX+this.motionZ*this.motionZ) > 0.25D) {
			this.swimmingAnim.increaseTimer();
		} else {
			this.swimmingAnim.decreaseTimer();
		}
	}

	public float getLeapProgress(float partialRenderTicks) {
		return leapingAnim.getAnimationProgressSinSqrt(partialRenderTicks);
	}

	public float getSwimProgress(float partialRenderTicks) {
		return Math.min((1F - (float)Math.pow(1-this.swimmingAnim.getAnimationFraction(partialRenderTicks), 2)) * 2 * this.swimmingAnim.getAnimationFraction(partialRenderTicks), 1.0F);
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
			this.stepHeight = 1F;
			this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
			this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
			this.setRotation(this.rotationYaw, this.rotationPitch);
			this.rotationYawHead = this.renderYawOffset = this.rotationYaw;
			strafing = ((EntityLivingBase)this.riddenByEntity).moveStrafing * 0.5F;
			forward = ((EntityLivingBase)this.riddenByEntity).moveForward;

			if (forward <= 0.0F) {
				forward *= 0.25F;
			}

			boolean onWaterSurface = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 1), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid();

			if(!this.inWater || !onWaterSurface) {
				if (this.onGround && forward != 0.0F) {
					if(!this.worldObj.isRemote) {
						if(this.ticksOnGround > 4) {
							motionY += 0.5;
							motionX += forward / 1.5F * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
							motionZ += forward / 1.5F * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
							motionX += strafing / 2.0F * MathHelper.cos((float) Math.toRadians(this.rotationYaw));
							motionZ += strafing / 2.0F * MathHelper.sin((float) Math.toRadians(this.rotationYaw));
						}
					}
				}
			} else {
				if(this.motionY < 0.0F) {
					if(this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 0.9D), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid()) {
						this.motionY *= 0.05F;
					}
				}
				if(this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 1.1D), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid()) {
					this.motionY += 0.02F;
				}

				if(!this.worldObj.isRemote) {
					if(this.isCollidedHorizontally) {
						this.motionY += 0.2D;
						this.strokeTicks = 0;
					}
				}

				if(!this.worldObj.isRemote && forward > 0.0F) {
					if(forward != 0.0F && this.strokeTicks == 0) {
						motionX += forward * 1F * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
						motionZ += forward * 1F * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
						motionX += strafing / 1.25F * MathHelper.cos((float) Math.toRadians(this.rotationYaw));
						motionZ += strafing / 1.25F * MathHelper.sin((float) Math.toRadians(this.rotationYaw));
						this.strokeTicks = 20;
					}
				}
			}
			if(!this.worldObj.isRemote && forward > 0.0F) {
				motionX += 0.05D * MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
				motionZ += 0.05D * MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
			}

			super.moveEntityWithHeading(0, 0);
		} else {
			this.stepHeight = 0.5F;
			this.jumpMovementFactor = 0.02F;
			super.moveEntityWithHeading(strafing, forward);
		}
	}

	@Override
	public double getMountedYOffset() {
		boolean onWaterSurface = this.inWater || this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + 1), MathHelper.floor_double(this.posZ)).getMaterial().isLiquid();
		return super.getMountedYOffset() + (onWaterSurface ? -0.6D + (this.swimmingAnim.getAnimationFraction(1) > 0 ? -0.2D : 0) : 0.0D) + 0.2D + (this.leapingAnim.getAnimationFraction(1) > 0 ? -this.getLeapProgress(1) * 0.4D : 0.0D);
	}
}
