package thebetweenlands.entities.rowboat;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.utils.CubicBezier;
import thebetweenlands.utils.EnumNBTTypes;
import thebetweenlands.utils.MathUtils;

/*
 * Useful links:
 * https://en.wikipedia.org/wiki/Glossary_of_rowing_terms
 * https://en.wikipedia.org/wiki/Glossary_of_nautical_terms
 * https://en.wikipedia.org/wiki/List_of_ship_directions
 * https://en.wikipedia.org/wiki/Anatomy_of_a_rowing_stroke
 */
public class EntityWeedwoodRowboat extends Entity {
	private static final CubicBezier DEVIATION_DRAG = new CubicBezier(0.9F, 0, 1, 0.6F); 

	private static final int TIME_SINCE_HIT_ID = 17;

	private static final int HIT_ROLL_DIRECTION_ID = 18;

	private static final int DAMAGE_TAKEN_ID = 19;

	private static final int[] OAR_ROTATION_IDS = { 20, 21 };

	public static final int LEFT_OAR = 0;

	public static final int RIGHT_OAR = 1;

	public static final float OAR_ROTATION_SCALE = -28;

	public static final float OAR_ROTATION_PERIOD = MathUtils.TAU / Math.abs(OAR_ROTATION_SCALE);

	private boolean hadPlayer;

	private final float[] rowForce = new float[2];

	private final int[] rowTime = new int[2];

	private final float[] oarRotationBuffer = new float[2];

	private final float[] prevOarRotation = new float[2];

	private float drag;

	private float submergeTicks;

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
		dataWatcher.addObject(HIT_ROLL_DIRECTION_ID, 1);
		dataWatcher.addObject(DAMAGE_TAKEN_ID, 0F);
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
		return 0;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@Override
	public float getShadowSize() {
		return 0;
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return new ItemStack(BLItemRegistry.weedwoodRowboat);
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
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isEntityInvulnerable()) {
			return false;
		} else if (!worldObj.isRemote && !isDead) {
			setHitRollDirection(-getHitRollDirection());
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
	public void performHurtAnimation() {
		setHitRollDirection(-getHitRollDirection());
		setTimeSinceHit(10);
		setDamageTaken(getDamageTaken() * 11);
	}

	public void updateControls(boolean oarStrokeLeft, boolean oarStrokeRight) {
		this.oarStrokeLeft = oarStrokeLeft;
		this.oarStrokeRight = oarStrokeRight;
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
			updateClientOarRotation(LEFT_OAR);
			updateClientOarRotation(RIGHT_OAR);
			boolean hasPlayer = riddenByEntity != null;
			if (!hadPlayer && hasPlayer) {
				TheBetweenlands.proxy.onPlayerEnterWeedwoodRowboat();
			}
			hadPlayer = hasPlayer;
			doSplash();
		} else {
			stroke(LEFT_OAR, oarStrokeLeft, prevOarStrokeLeft);
			stroke(RIGHT_OAR, oarStrokeRight, prevOarStrokeRight);
			prevOarStrokeLeft = oarStrokeLeft;
			prevOarStrokeRight = oarStrokeRight;
			// TODO: weedwood rowboat square control
			/*if (riddenByEntity instanceof EntityLivingBase) {
				EntityLivingBase pilot = (EntityLivingBase) riddenByEntity;
				if (pilot.moveForward < 0) {
					
				}
			}*/
		}
		super.onUpdate();
		if (worldObj.isRemote) {
			updatePosition();
		} else {
			boatPosRotationIncrements = 0;
		}
		if (!worldObj.isRemote) {
			applyForces();
			applyRowForce();
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
		rotationYaw = MathHelper.wrapAngleTo180_float(rotationYaw);
		prevRotationYaw = MathUtils.adjustAngleForInterpolation(rotationYaw, prevRotationYaw);
		adjustOarRotationForInterpolation(LEFT_OAR);
		adjustOarRotationForInterpolation(RIGHT_OAR);
	}

	private void adjustOarRotationForInterpolation(int side) {
		prevOarRotation[side] = MathUtils.adjustValueForInterpolation(getOarRotation(side), prevOarRotation[side], 0, OAR_ROTATION_PERIOD);
	}

	private void updateClientOarRotation(int side) {
		if (oarRotationBuffer[side] == getOarRotation(side)) {
			prevOarRotation[side] = oarRotationBuffer[side];
		} else {
			oarRotationBuffer[side] = getOarRotation(side);
		}
	}

	@Override
	public void updateRiderPosition() {
		if (riddenByEntity != null) {
			double dx = Math.cos(rotationYaw * MathUtils.DEG_TO_RAD) * (-0.2 - 0.0625F);
			double dz = Math.sin(rotationYaw * MathUtils.DEG_TO_RAD) * (-0.2 - 0.0625F);
			riddenByEntity.setPosition(posX + dx, posY + getMountedYOffset() + riddenByEntity.getYOffset(), posZ + dz);
			if (riddenByEntity instanceof EntityLivingBase) {
				EntityLivingBase rider = (EntityLivingBase) riddenByEntity;
				rider.renderYawOffset = rotationYaw - 90;
				rider.rotationYaw -= prevRotationYaw - rotationYaw;
				rider.rotationYawHead = rotationYaw - 90;
				TheBetweenlands.proxy.updateRiderYawInWeedwoodRowboat(this, rider);
			}
		}
	}

	private void updatePosition() {
		if (boatPosRotationIncrements > 0) {
			double x = posX + (boatX - posX) / boatPosRotationIncrements;
			double y = posY + (boatY - posY) / boatPosRotationIncrements;
			double z = posZ + (boatZ - posZ) / boatPosRotationIncrements;
			rotationYaw = rotationYaw + MathHelper.wrapAngleTo180_float(boatYaw - rotationYaw) / boatPosRotationIncrements;
			rotationPitch = rotationPitch + (boatPitch - rotationPitch) / boatPosRotationIncrements;
			boatPosRotationIncrements--;
			setPosition(x, y, z);
			setRotation(rotationYaw, rotationPitch);
		}
	}

	private void applyForces() {
		float bobBase = 0.1F;
		float buoyancy = 0;
		int blockX = MathHelper.floor_double(posX);
		int blockY = MathHelper.floor_double(posY);
		int blockZ = MathHelper.floor_double(posZ);
		Block blockAt = worldObj.getBlock(blockX, blockY, blockZ);
		Block blockAbove = worldObj.getBlock(blockX, blockY + 1, blockZ);
		if (blockAt.getMaterial() == Material.water && blockAbove.getMaterial() != Material.water) {
			buoyancy = (1 - ((float) posY - blockY - 1.1F)) * 0.9F + bobBase;
			drag = 0.9875F;
			submergeTicks = 0;
		} else if (blockAt.getMaterial() == Material.water && blockAbove.getMaterial() == Material.water) {
			buoyancy = 1.01F;
			drag = 0.975F;
			submergeTicks++;
		} else if (blockAt.getMaterial() == Material.air) {
			Block blockBellow = worldObj.getBlock(blockX, blockY - 1, blockZ);
			if (blockBellow.getMaterial() == Material.water) {
				drag = 0.95F;
			} else if (blockBellow.getMaterial().blocksMovement()) {
				drag = 0.35F;
			} else {
				drag = 1;
			}
		}
		float motionRawAngle = (float) Math.atan2(motionZ, motionX);
		float motionAngle = MathHelper.wrapAngleTo180_float(motionRawAngle * MathUtils.RAD_TO_DEG + 180);
		float deviation = Math.abs(MathHelper.wrapAngleTo180_float(rotationYaw - motionAngle)) / 180;
		drag *= MathUtils.linearTransformf(DEVIATION_DRAG.eval(deviation), 0, 1, 1, 0.25F);
		motionY -= 0.04;
		motionX *= drag;
		motionZ *= drag;
		rotationalVelocity *= drag * 0.95F;
		if (buoyancy != 0) {
			motionY *= 0.7;
			final float moveY = (buoyancy - bobBase) * 0.15F;
			motionY = Math.min(motionY + 0.05F, moveY);
		}
	}

	private void applyRowForce() {
		if (riddenByEntity != null) {
			if (submergeTicks < 25) {
				Vec3 rowForce = Vec3.createVectorHelper(1, 0, 0);
				Vec3 motion = Vec3.createVectorHelper(0, 0, 0);
				Vec3 rotation = Vec3.createVectorHelper(0, 0, 0);
				float leftOarForce = getRowForce(LEFT_OAR);
				float rightOarForce = getRowForce(RIGHT_OAR);
				float forceFactor = 0.5F;
				if (leftOarForce > 0) {
					updateOarRotation(LEFT_OAR, leftOarForce);
					if (canOarsApplyForce()) {
						leftOarForce *= getOarPeriodicForceApplyment(LEFT_OAR);
						Vec3 leftLever = Vec3.createVectorHelper(0, 0, leftOarForce);
						motion = motion.addVector(0, 0, leftOarForce * forceFactor);
						Vec3 cross = rowForce.crossProduct(leftLever);
						rotation = rotation.addVector(cross.xCoord, cross.yCoord, cross.zCoord);
					}
				}
				if (rightOarForce > 0) {
					updateOarRotation(RIGHT_OAR, rightOarForce);
					if (canOarsApplyForce()) {
						rightOarForce *= getOarPeriodicForceApplyment(RIGHT_OAR);
						Vec3 righerLever = Vec3.createVectorHelper(0, 0, rightOarForce);
						motion = motion.addVector(0, 0, rightOarForce * forceFactor);
						Vec3 cross = Vec3.createVectorHelper(-rowForce.xCoord, -rowForce.yCoord, -rowForce.zCoord).crossProduct(righerLever);
						rotation = rotation.addVector(cross.xCoord, cross.yCoord, cross.zCoord);
					}
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

	private float getOarPeriodicForceApplyment(int side) {
		return MathUtils.linearTransformf(MathHelper.cos(getOarRotation(side) * OAR_ROTATION_SCALE), -1, 1, 0, 2);
	}

	private void doSplash() {
		if (worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY), MathHelper.floor_double(posZ)).getMaterial() != Material.water) {
			return;
		}
		double motionX = boatX - posX;
		double motionY = boatY - posY;
		double motionZ = boatZ - posZ;
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		if (velocity > 0.2625) {
			double vecX = Math.cos(rotationYaw * MathUtils.DEG_TO_RAD);
			double vecZ = Math.sin(rotationYaw * MathUtils.DEG_TO_RAD);
			for (int p = 0; p < 1 + velocity * 60; p++) {
				double near = rand.nextFloat() * 2 - 1;
				double far = (rand.nextInt(2) * 2 - 1) * 0.7;
				if (rand.nextBoolean()) {
					double splashX = posX - vecX * near * 0.8 + vecZ * far;
					double splashZ = posZ - vecZ * near * 0.8 - vecX * far;
					worldObj.spawnParticle("splash", splashX, posY - 0.125, splashZ, motionX, motionY == 0 ? 1e-8 : motionY, motionZ);
				} else {
					double splashX = posX + vecX + vecZ * near * 0.7;
					double splashZ = posZ + vecZ - vecX * near * 0.7;
					worldObj.spawnParticle("splash", splashX, posY - 0.125, splashZ, motionX, motionY == 0 ? 1e-8 : motionY, motionZ);
				}
			}
		}
	}

	public boolean canOarsApplyForce() {
		return drag <= 1;
	}

	public float getRowForce(int side) {
		return 0.017F * rowForce[side];
	}

	public void updateOarRotation(int side, float value) {
		float rotation = getOarRotation(side) + value;
		if (rotation > OAR_ROTATION_PERIOD) {
			rotation -= OAR_ROTATION_PERIOD;
		}
		setOarRotation(side, rotation);
	}

	public boolean stroke(int side, boolean oarStroke, boolean prevOarStroke) {
		float force = rowForce[side];
		int time = rowTime[side];
		time++;
		boolean appliedForce = false;
		if (oarStroke || time < 10) {
			if (!prevOarStroke && oarStroke && time >= 10) {
				force = 1;
				time = 0;
//				dataWatcher.updateObject(OAR_ROTATION_IDS[side], 0F);
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

	public float getOarRotation(int side, float delta) {
		float prevRotation = prevOarRotation[side];
		float rotation = getOarRotation(side);
		return (float) MathHelper.denormalizeClamp(prevRotation, rotation, delta);
	}

	public void setOarRotation(int side, float value) {
		dataWatcher.updateObject(OAR_ROTATION_IDS[side], value);
	}

	public float getOarRotation(int side) {
		return dataWatcher.getWatchableObjectFloat(OAR_ROTATION_IDS[side]);
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

	public void setHitRollDirection(int direction) {
		dataWatcher.updateObject(HIT_ROLL_DIRECTION_ID, direction);
	}

	public int getHitRollDirection() {
		return dataWatcher.getWatchableObjectInt(HIT_ROLL_DIRECTION_ID);
	}

	@Override
	public void func_145781_i(int id) {
		if (id == OAR_ROTATION_IDS[LEFT_OAR]) {
			prevOarRotation[LEFT_OAR] = oarRotationBuffer[LEFT_OAR];
		} else if (id == OAR_ROTATION_IDS[RIGHT_OAR]) {
			prevOarRotation[RIGHT_OAR] = oarRotationBuffer[RIGHT_OAR];
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setFloat("oarRotationLeft", getOarRotation(LEFT_OAR));
		compound.setFloat("oarForceLeft", rowForce[LEFT_OAR]);
		compound.setInteger("oarTimeLeft", rowTime[LEFT_OAR]);
		compound.setFloat("oarRotationRight", getOarRotation(RIGHT_OAR));
		compound.setFloat("oarForceRight", rowForce[RIGHT_OAR]);
		compound.setInteger("oarTimeRight", rowTime[RIGHT_OAR]);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		setOarRotation(LEFT_OAR, compound.getFloat("oarRotationLeft"));
		rowForce[LEFT_OAR] = compound.getFloat("oarForceLeft");
		rowTime[LEFT_OAR] = compound.getInteger("oarTimeLeft");
		setOarRotation(RIGHT_OAR, compound.getFloat("oarRotationRight"));
		rowForce[RIGHT_OAR] = compound.getFloat("oarForceRight");
		rowTime[RIGHT_OAR] = compound.getInteger("oarTimeRight");
	}
}
