package thebetweenlands.entities;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;

public class EntityWeedwoodRowboat extends Entity {
	private static final int TIME_SINCE_HIT_ID = 17;

	private static final int FORWARD_DIRECTION_ID = 18;

	private static final int DAMAGE_TAKEN_ID = 19;

	private double speedMultiplier;

	private int boatPosRotationIncrements;

	private double boatX;

	private double boatY;

	private double boatZ;

	private double boatYaw;

	private double boatPitch;

	private double velocityX;

	private double velocityY;

	private double velocityZ;

	public EntityWeedwoodRowboat(World world) {
		super(world);
		speedMultiplier = 0.07;
		preventEntitySpawning = true;
		setSize(1.3F, 0.8F);
		yOffset = height / 2;
	}

	public EntityWeedwoodRowboat(World world, double x, double y, double z) {
		this(world);
		setPosition(x, y + yOffset, z);
		motionX = 0;
		motionY = 0;
		motionZ = 0;
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
	}

	@Override
	protected void entityInit() {
		dataWatcher.addObject(TIME_SINCE_HIT_ID, 0);
		dataWatcher.addObject(FORWARD_DIRECTION_ID, 1);
		dataWatcher.addObject(DAMAGE_TAKEN_ID, (float) 0);
	}

	public void setDamageTaken(float damage) {
		dataWatcher.updateObject(DAMAGE_TAKEN_ID, damage);
	}

	public float getDamageTaken() {
		return dataWatcher.getWatchableObjectFloat(DAMAGE_TAKEN_ID);
	}

	public void setTimeSinceHit(int time) {
		dataWatcher.updateObject(TIME_SINCE_HIT_ID, time);
	}

	public int getTimeSinceHit() {
		return dataWatcher.getWatchableObjectInt(TIME_SINCE_HIT_ID);
	}

	public void setForwardDirection(int direction) {
		dataWatcher.updateObject(FORWARD_DIRECTION_ID, direction);
	}

	public int getForwardDirection() {
		return dataWatcher.getWatchableObjectInt(FORWARD_DIRECTION_ID);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return entity.boundingBox;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return boundingBox;
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public double getMountedYOffset() {
		return 0;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isEntityInvulnerable()) {
			return false;
		} else if (!worldObj.isRemote && !isDead) {
			setForwardDirection(-getForwardDirection());
			setTimeSinceHit(10);
			setDamageTaken(getDamageTaken() + amount * 10);
			setBeenAttacked();
			boolean attackerIsCreativeMode = source.getEntity() instanceof EntityPlayer && ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode;

			if (attackerIsCreativeMode || getDamageTaken() > 40) {
				if (riddenByEntity != null) {
					riddenByEntity.mountEntity(this);
				}

				if (!attackerIsCreativeMode) {
					func_145778_a(BLItemRegistry.weedwoodRowboat, 1, 0);
				}

				setDead();
			}

			return true;
		} else {
			return true;
		}
	}

	@Override
	public void performHurtAnimation() {
		setForwardDirection(-getForwardDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() * 11);
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int three) {
		if (riddenByEntity == null) {
			boatPosRotationIncrements = three + 5;
		} else {
			double deltaX = x - posX;
			double deltaY = y - posY;
			double deltaZ = z - posZ;
			double velocity = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
			if (velocity <= 1) {
				return;
			}
			boatPosRotationIncrements = 3;
		}
		boatX = x;
		boatY = y;
		boatZ = z;
		boatYaw = yaw;
		boatPitch = pitch;
		motionX = velocityX;
		motionY = velocityY;
		motionZ = velocityZ;
	}

	@Override
	public void setVelocity(double x, double y, double z) {
		velocityX = motionX = x;
		velocityY = motionY = y;
		velocityZ = motionZ = z;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (getTimeSinceHit() > 0) {
			setTimeSinceHit(getTimeSinceHit() - 1);
		}
		if (getDamageTaken() > 0) {
			setDamageTaken(getDamageTaken() - 1);
		}
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		byte depth = 5;
		double buoyancy = 0;
		for (int i = 0; i < depth; ++i) {
			double minY = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * i / depth - 0.125;
			double maxY = boundingBox.minY + (boundingBox.maxY - boundingBox.minY) * (i + 1) / depth - 0.125;
			AxisAlignedBB box = AxisAlignedBB.getBoundingBox(boundingBox.minX, minY, boundingBox.minZ, boundingBox.maxX, maxY, boundingBox.maxZ);
			if (worldObj.isAABBInMaterial(box, Material.water)) {
				buoyancy += 0.6 / depth;
			}
		}
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		if (velocity > 0.2625) {
			double vecX = Math.cos(rotationYaw * Math.PI / 180);
			double vecZ = Math.sin(rotationYaw * Math.PI / 180);
			for (int p = 0; p < 1 + velocity * 60; p++) {
				double near = rand.nextFloat() * 2 - 1;
				double far = (rand.nextInt(2) * 2 - 1) * 0.7;
				if (rand.nextBoolean()) {
					double splashX = posX - vecX * near * 0.8 + vecZ * far;
					double splashZ = posZ - vecZ * near * 0.8 - vecX * far;
					worldObj.spawnParticle("splash", splashX, posY - 0.125, splashZ, motionX, motionY, motionZ);
				} else {
					double splashX = posX + vecX + vecZ * near * 0.7;
					double splashZ = posZ + vecZ - vecX * near * 0.7;
					worldObj.spawnParticle("splash", splashX, posY - 0.125, splashZ, motionX, motionY, motionZ);
				}
			}
		}
		if (worldObj.isRemote && riddenByEntity == null) {
			if (boatPosRotationIncrements > 0) {
				double x = posX + (boatX - posX) / boatPosRotationIncrements;
				double y = posY + (boatY - posY) / boatPosRotationIncrements;
				double z = posZ + (boatZ - posZ) / boatPosRotationIncrements;
				double yawDelta = MathHelper.wrapAngleTo180_double(boatYaw - rotationYaw);
				rotationYaw = (float) (rotationYaw + yawDelta / boatPosRotationIncrements);
				rotationPitch = (float) (rotationPitch + (boatPitch - rotationPitch) / boatPosRotationIncrements);
				boatPosRotationIncrements--;
				setPosition(x, y, z);
				setRotation(rotationYaw, rotationPitch);
			} else {
				double x = posX + motionX;
				double y = posY + motionY;
				double z = posZ + motionZ;
				setPosition(x, y, z);
				if (onGround) {
					motionX *= 0.5;
					motionY *= 0.5;
					motionZ *= 0.5;
				}
				motionX *= 0.99;
				motionY *= 0.94;
				motionZ *= 0.99;
			}
		} else {
			if (buoyancy < 1) {
				motionY += 0.04 * (buoyancy * 2 - 1);
			} else {
				if (motionY < 0) {
					motionY /= 2;
				}
				motionY += 0.007;
			}
			if (riddenByEntity != null && riddenByEntity instanceof EntityLivingBase) {
				EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
				float yaw = riddenByEntity.rotationYaw - rider.moveStrafing * 90;
				motionX += -Math.sin(yaw * (float) Math.PI / 180) * speedMultiplier * rider.moveForward * 0.05;
				motionZ += Math.cos(yaw * (float) Math.PI / 180) * speedMultiplier * rider.moveForward * 0.05;
			}
			double newVelocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
			if (newVelocity > 0.35) {
				double vecZ = 0.35 / newVelocity;
				motionX *= vecZ;
				motionZ *= vecZ;
				newVelocity = 0.35D;
			}
			if (newVelocity > velocity && speedMultiplier < 0.35) {
				speedMultiplier += (0.35 - speedMultiplier) / 35;
				if (speedMultiplier > 0.35) {
					speedMultiplier = 0.35;
				}
			} else {
				speedMultiplier -= (speedMultiplier - 0.07) / 35;
				if (speedMultiplier < 0.07) {
					speedMultiplier = 0.07;
				}
			}
			for (int xzIndex = 0; xzIndex < 4; xzIndex++) {
				int x = MathHelper.floor_double(posX + (xzIndex % 2 - 0.5) * 0.8);
				int z = MathHelper.floor_double(posZ + (xzIndex / 2 - 0.5) * 0.8);
				for (int dy = 0; dy < 2; dy++) {
					int y = MathHelper.floor_double(posY) + dy;
					Block block = worldObj.getBlock(x, y, z);
					if (block == Blocks.snow_layer) {
						worldObj.setBlockToAir(x, y, z);
						isCollidedHorizontally = false;
					} else if (block == Blocks.waterlily) {
						worldObj.func_147480_a(x, y, z, true);
						isCollidedHorizontally = false;
					}
				}
			}
			if (onGround) {
				motionX *= 0.5;
				motionY *= 0.5;
				motionZ *= 0.5;
			}
			moveEntity(motionX, motionY, motionZ);
			if (!isCollidedHorizontally || velocity < 0.2) {
				motionX *= 0.99;
				motionY *= 0.95;
				motionZ *= 0.99;
			}
			rotationPitch = 0;
			double yaw = rotationYaw;
			double deltaX = prevPosX - posX;
			double deltaZ = prevPosZ - posZ;
			if (deltaX * deltaX + deltaZ * deltaZ > 0.001) {
				yaw = (float) (Math.atan2(deltaZ, deltaX) * 180 / Math.PI);
			}
			double yawDelta = MathHelper.wrapAngleTo180_double(yaw - rotationYaw);
			if (yawDelta > 20) {
				yawDelta = 20;
			}
			if (yawDelta < -20) {
				yawDelta = -20;
			}
			rotationYaw = (float) (rotationYaw + yawDelta);
			setRotation(rotationYaw, rotationPitch);
			if (!worldObj.isRemote) {
				List<Entity> collidingEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
				if (!collidingEntities.isEmpty()) {
					for (int i = 0; i < collidingEntities.size(); ++i) {
						Entity entity = collidingEntities.get(i);
						if (entity != riddenByEntity && entity.canBePushed() && entity instanceof EntityWeedwoodRowboat) {
							entity.applyEntityCollision(this);
						}
					}
				}
				if (riddenByEntity != null && riddenByEntity.isDead) {
					riddenByEntity = null;
				}
			}
		}
	}

	@Override
	public void updateRiderPosition() {
		if (riddenByEntity != null) {
			double dx = Math.cos(rotationYaw * Math.PI / 180) * 0.4;
			double dz = Math.sin(rotationYaw * Math.PI / 180) * 0.4;
			riddenByEntity.setPosition(posX + dx, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ + dz);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {}

	@Override
	public float getShadowSize() {
		return 0;
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (riddenByEntity != null && riddenByEntity instanceof EntityPlayer && riddenByEntity != player) {
			return true;
		} else {
			if (!worldObj.isRemote) {
				player.mountEntity(this);
			}
			return true;
		}
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return new ItemStack(BLItemRegistry.weedwoodRowboat);
	}
}
