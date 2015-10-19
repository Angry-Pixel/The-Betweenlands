package thebetweenlands.entities.rowboat;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.utils.MathUtils;

/*
 * Useful links:
 * 	https://en.wikipedia.org/wiki/Glossary_of_rowing_terms
 * 	https://en.wikipedia.org/wiki/Glossary_of_nautical_terms
 * 	https://en.wikipedia.org/wiki/List_of_ship_directions
 * 	https://en.wikipedia.org/wiki/Anatomy_of_a_rowing_stroke
 */
public class EntityWeedwoodRowboat extends Entity {
	private static final int TIME_SINCE_HIT_ID = 17;

	private static final int FORWARD_DIRECTION_ID = 18;

	private static final int DAMAGE_TAKEN_ID = 19;

	private static final int[] OAR_ROTATION_IDS = { 20, 21 };

	public static final int LEFT_OAR = 0;

	public static final int RIGHT_OAR = 1;

	private boolean hadPlayer;

	private final float[] rowForce = new float[2];

	private final int[] rowTime = new int[2];

	private final float[] prevOarRotation = new float[2];

	private float drag;

	private float bobTime;

	private float aq;

	private float rotationalVelocity;

	private double boatX;

	private double boatY;

	private double boatZ;

	private float boatYaw;

	private float boatPitch;

	private int boatPosRotationIncrements;

	private boolean prevOarStrokeLeft;

	private boolean oarStrokeLeft;

	private boolean prevOarStrokeRight;

	private boolean oarStrokeRight;

	public EntityWeedwoodRowboat(World world) {
		super(world);
		preventEntitySpawning = true;
		setSize(2, 0.9F);
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
		dataWatcher.addObject(DAMAGE_TAKEN_ID,  0F);
		for (int i = 0; i < OAR_ROTATION_IDS.length; i++) {
			dataWatcher.addObject(OAR_ROTATION_IDS[i], 0F);
		}
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
		return -0.05;
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
		}
		return true;
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return new ItemStack(BLItemRegistry.weedwoodRowboat);
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
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int rotationIncrements) {
		boatX = x;
		boatY = y;
		boatZ = z;
		boatYaw = yaw;
		boatPitch = pitch;
		boatPosRotationIncrements = rotationIncrements;
	}

	@Override
	public void setVelocity(double x, double y, double z) {}

	public void updateControls(boolean oarStrokeLeft, boolean oarStrokeRight, boolean oarSquareLeft, boolean oarSquareRight) {
		this.oarStrokeLeft = oarStrokeLeft;
		this.oarStrokeRight = oarStrokeRight;
	}

	private void doSplash() {
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
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player) {
			return true;
		} else {
			if (!this.worldObj.isRemote) {
				player.mountEntity(this);
			}
			return true;
		}
	}

	@Override
	public void onUpdate() {
		if (getTimeSinceHit() > 0) {
			setTimeSinceHit(getTimeSinceHit() - 1);
		}
		if (getDamageTaken() > 0) {
			setDamageTaken(getDamageTaken() - 1);
		}
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		if (worldObj.isRemote) {
			boolean hasPlayer = riddenByEntity != null;
			if (!hadPlayer && hasPlayer) {
				TheBetweenlands.proxy.onPlayerEnterWeedwoodRowboat();
			} else if (hadPlayer && !hasPlayer) {
				TheBetweenlands.proxy.onPlayerLeaveWeedwoodRowboat();
			}
			hadPlayer = hasPlayer;
		} else {
			stroke(LEFT_OAR, oarStrokeLeft, prevOarStrokeLeft);
			stroke(RIGHT_OAR, oarStrokeRight, prevOarStrokeRight);
			prevOarStrokeLeft = oarStrokeLeft;
			prevOarStrokeRight = oarStrokeRight;
		}
		prevOarRotation[LEFT_OAR] = getOarRotation(LEFT_OAR);
		prevOarRotation[RIGHT_OAR] = getOarRotation(RIGHT_OAR);
		super.onUpdate();
		if (worldObj.isRemote) {
			updatePosition();
		} else {
			boatPosRotationIncrements = 0;
		}
		applyForces();
		applyRowForce();
		if (!worldObj.isRemote) {
			moveEntity(motionX, motionY, motionZ);
		}
		func_145775_I();
		if (!worldObj.isRemote) {
			List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, getBoundingBox().expand(0.2, 0.05, 0.2));
			if (entities != null && !entities.isEmpty()) {
				double motionCutoff = motionX * motionX + motionZ * motionZ + 5;
				boolean shouldMount = !worldObj.isRemote && motionCutoff > 0.01;
				for (Entity entity : entities) {
					if (shouldMount && entity instanceof EntityLiving && entity.ridingEntity == null && riddenByEntity == null) {
						entity.mountEntity(this);
					} else {
						applyEntityCollision(entity);
					}
				}
			}
			if (riddenByEntity != null && riddenByEntity.isDead) {
				riddenByEntity = null;
			}
		}
	}

	private void updatePosition() {
		if (boatPosRotationIncrements > 0) {
			double x = posX + (boatX - posX) / boatPosRotationIncrements;
			double y = posY + (boatY - posY) / boatPosRotationIncrements;
			double z = posZ + (boatZ - posZ) / boatPosRotationIncrements;
			rotationYaw = ((float)(rotationYaw + MathHelper.wrapAngleTo180_float(boatYaw - rotationYaw) / boatPosRotationIncrements));
			rotationPitch = ((float)(rotationPitch + (boatPitch - rotationPitch) / boatPosRotationIncrements));
			boatPosRotationIncrements--;
			setPosition(x, y, z);
			setRotation(rotationYaw, rotationPitch);
		}
	}

	private void applyForces() {
		Vec3 currentMotion = Vec3.createVectorHelper(motionX, 0, motionZ);
		float bobAmount = 1 + (float) currentMotion.lengthVector() * 30;
		if (worldObj.rand.nextInt(30) == 0) {
			bobAmount *= 10;
		}
		bobTime += 0.05 * bobAmount;
		float bobBase = 0.1F;
		float bob = 0;
		float buoyancy = 0;
		int blockX = MathHelper.floor_double(posX);
		int blockY = MathHelper.floor_double(posY);
		int blockZ = MathHelper.floor_double(posZ);
		Block blockAt = worldObj.getBlock(blockX, blockY, blockZ);
		Block blockAbove = worldObj.getBlock(blockX, blockY + 1, blockZ);
		if (blockAt.getMaterial() == Material.water && blockAbove.getMaterial() != Material.water) {
			buoyancy = (1 - ((float) this.posY - blockY - 1)) * 0.9F + bobBase;
			bob = 1 + MathHelper.sin(bobTime) * 0.035F;
			drag = 0.9F;
			aq = 0;
		} else if (blockAt.getMaterial() == Material.water && blockAbove.getMaterial() == Material.water) {
			buoyancy = 1.01F;
			drag = 0.9F;
			aq++;
		} else if (blockAt.getMaterial() == Material.air) {
			Block blockBellow = worldObj.getBlock(blockX, blockY - 1, blockZ);
			if (blockBellow.getMaterial() == Material.water) {
				drag = 0.9F;
			} else if (blockBellow.getMaterial().blocksMovement()) {
				drag = 0.35F;
			} else {
				drag = 1;
			}
		}
		motionY -= 0.04;
		motionX *= drag;
		motionZ *= drag;
		rotationalVelocity *= drag;
		if (buoyancy != 0) {
			motionY *= 0.7;
			final float param_39 = (buoyancy - bob - bobBase) * 0.15F;
			motionY = Math.min(motionY + 0.05F, param_39);
		}
	}

	private void applyRowForce() {
		if (riddenByEntity != null) {
			if (aq < 25) {
				Vec3 rowForce = Vec3.createVectorHelper(3, 0, 0);
				Vec3 motion = Vec3.createVectorHelper(0, 0, 0);
				Vec3 rotation = Vec3.createVectorHelper(0, 0, 0);
				float leftOarForce = getRowForce(LEFT_OAR);
				float rightOarForce = getRowForce(RIGHT_OAR);
				if (leftOarForce > 0) {
					if (!isDragHeavy()) {
						Vec3 param_45 = Vec3.createVectorHelper(0, 0, leftOarForce);
						motion = motion.addVector(0, 0, leftOarForce);
						Vec3 cross = rowForce.crossProduct(param_45); 
						rotation = rotation.addVector(cross.xCoord, cross.yCoord, cross.zCoord);
					}
					updateOarRotation(LEFT_OAR, leftOarForce);
				}
				if (rightOarForce > 0) {
					if (!isDragHeavy()) {
						Vec3 param_46 = Vec3.createVectorHelper(0, 0, rightOarForce);
						motion = motion.addVector(0, 0, rightOarForce);
						Vec3 cross = Vec3.createVectorHelper(-rowForce.xCoord, -rowForce.yCoord, -rowForce.zCoord).crossProduct(param_46); 
						rotation = rotation.addVector(cross.xCoord, cross.yCoord, cross.zCoord);
					}
					updateOarRotation(RIGHT_OAR, rightOarForce);
				}
				Vec3 currentMotion = Vec3.createVectorHelper(motionX, 0, motionZ);
				if (currentMotion.lengthVector() < 0.1 && rotation.xCoord * rotation.xCoord + rotation.yCoord * rotation.yCoord + rotation.zCoord + rotation.zCoord > 0) {
					motion.xCoord *= 0.35;
					motion.yCoord *= 0.35;
					motion.zCoord *= 0.35;
					rotation.xCoord *= 1.6;
					rotation.yCoord *= 1.6;
					rotation.zCoord *= 1.6;
				}
				rotationalVelocity += rotation.yCoord * 10;
				rotationYaw += rotationalVelocity;
				motion.rotateAroundY((270 - rotationYaw) * MathUtils.DEG_TO_RAD);
				motionX += motion.xCoord;
				motionY += motion.yCoord;
				motionZ += motion.zCoord;
			}
		}
	}

	public boolean isDragHeavy() {
		return drag > 1;
	}

	public float getRowForce(int side) {
		return 0.017F * rowForce[side];
	}

	public void updateOarRotation(int side, float value) {
		float rotation = getOarRotation(side) + value;
		final float max = 1000;
		if (rotation > max) {
			rotation -= max;
		}
		dataWatcher.updateObject(OAR_ROTATION_IDS[side], rotation);
	}

	@Override
	public void updateRiderPosition() {
		if (riddenByEntity != null) {
			double dx = Math.cos(rotationYaw * Math.PI / 180) * -0.2;
			double dz = Math.sin(rotationYaw * Math.PI / 180) * -0.2;
			riddenByEntity.setPosition(posX + dx, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ + dz);
			if (riddenByEntity instanceof EntityLivingBase) {
				EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
				rider.renderYawOffset = rotationYaw - 90;
				rider.rotationYaw -= (prevRotationYaw - rotationYaw) * 0.2F;
				TheBetweenlands.proxy.updateRiderYawInWeedwoodRowboat(this, rider);
			}
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

	public float getOarRotation(int side) {
		return dataWatcher.getWatchableObjectFloat(OAR_ROTATION_IDS[side]);
	}

	public float getOarRotation(int side, float swing) {
		float prevRotation = this.prevOarRotation[side];
		float rotation = getOarRotation(side);
		return (float) MathHelper.denormalizeClamp(prevRotation, rotation, swing);
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

	public void setOarRotation(float left, float right) {
		dataWatcher.updateObject(OAR_ROTATION_IDS[LEFT_OAR], left);
		dataWatcher.updateObject(OAR_ROTATION_IDS[RIGHT_OAR], right);
	}

	public boolean stroke(int side, boolean prevOarStroke, boolean oarStroke) {
		float force = rowForce[side];
		int time = rowTime[side];
		time++;
		boolean appliedForce = false;
		if (prevOarStroke || time < 10) {
			if (!oarStroke && prevOarStroke && time >= 10) {
				force = 1;
				time = 0;
				dataWatcher.updateObject(OAR_ROTATION_IDS[side], 0F);
			} else {
				force = Math.max(force - 0.05F, 0.55F);
			}
		} else {
			force = Math.max(force - 0.1F, 0);
			appliedForce = force > 0;
		}
		rowTime[side] = time;
		rowForce[side] = force;
		return appliedForce;
	}
}
