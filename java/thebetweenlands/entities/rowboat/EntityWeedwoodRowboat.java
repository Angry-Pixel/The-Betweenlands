package thebetweenlands.entities.rowboat;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import thebetweenlands.utils.CubicBezier;
import thebetweenlands.utils.MathUtils;

/**
 * <pre>
 * Useful links:
 * https://en.wikipedia.org/wiki/Glossary_of_rowing_terms
 * https://en.wikipedia.org/wiki/Glossary_of_nautical_terms
 * https://en.wikipedia.org/wiki/List_of_ship_directions
 * https://en.wikipedia.org/wiki/Anatomy_of_a_rowing_stroke
 * </pre>
 * 
 * Left and right are used in the perspective of the pilot
 */
public class EntityWeedwoodRowboat extends Entity {
	private static final CubicBezier DEVIATION_DRAG = new CubicBezier(0.9F, 0, 1, 0.6F);

	private static final int TIME_SINCE_HIT_ID = 17;

	private static final int HIT_ROLL_DIRECTION_ID = 18;

	private static final int DAMAGE_TAKEN_ID = 19;

	private static final EnumMap<ShipSide, Integer> OAR_ROTATION_IDS = ShipSide.newEnumMap(int.class, 20, 21);

	public static final float OAR_ROTATION_SCALE = -28;

	public static final float OAR_ROTATION_PERIOD = MathUtils.TAU / Math.abs(OAR_ROTATION_SCALE);

	private boolean hadPlayer;

	private final EnumMap<ShipSide, Float> rowForce = ShipSide.newEnumMap(float.class);

	private final EnumMap<ShipSide, Integer> rowTime = ShipSide.newEnumMap(int.class);

	private final EnumMap<ShipSide, Float> oarRotationBuffer = ShipSide.newEnumMap(float.class);

	private final EnumMap<ShipSide, Float> prevOarRotation = ShipSide.newEnumMap(float.class);

	private float drag;

	private float submergeTicks;

	private float rotationalVelocity;

	private double serverX;

	private double serverY;

	private double serverZ;

	private float boatYaw;

	private float boatPitch;

	private int serverT = -1;

	private boolean prevOarStrokeLeft;

	private boolean oarStrokeLeft;

	private boolean prevOarStrokeRight;

	private boolean oarStrokeRight;

	private ShipSide synchronizer = ShipSide.STARBOARD;

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
		dataWatcher.addObject(OAR_ROTATION_IDS.get(ShipSide.STARBOARD), 0F);
		dataWatcher.addObject(OAR_ROTATION_IDS.get(ShipSide.PORT), 0F);
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
		serverX = x;
		serverY = y;
		serverZ = z;
		boatYaw = yaw;
		boatPitch = pitch;
		serverT = rotationIncrements;
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
			updateClientOarRotation(ShipSide.STARBOARD);
			updateClientOarRotation(ShipSide.PORT);
			boolean hasPlayer = riddenByEntity != null;
			if (!hadPlayer && hasPlayer) {
				TheBetweenlands.proxy.onPlayerEnterWeedwoodRowboat();
			}
			hadPlayer = hasPlayer;
			doSplash();
		} else {
			stroke(ShipSide.STARBOARD, oarStrokeLeft, prevOarStrokeLeft);
			stroke(ShipSide.PORT, oarStrokeRight, prevOarStrokeRight);
			prevOarStrokeLeft = oarStrokeLeft;
			prevOarStrokeRight = oarStrokeRight;
		}
		super.onUpdate();
		if (worldObj.isRemote) {
			updatePosition();
		} else {
			float rotationLeft = getOarRotation(ShipSide.STARBOARD);
			float rotationRight = getOarRotation(ShipSide.PORT);
			applyForces();
			applyRowForce();
			returnOarToResting(ShipSide.STARBOARD, rotationLeft);
			returnOarToResting(ShipSide.PORT, rotationRight);
			synchronizeOars();
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
		adjustOarRotationForInterpolation(ShipSide.STARBOARD);
		adjustOarRotationForInterpolation(ShipSide.PORT);
	}

	private void returnOarToResting(ShipSide side, float preApplyValue) {
		if (getRowForce(side) == 0) {
			final float target = OAR_ROTATION_PERIOD * 0.05F;
			float value = getOarRotation(side);
			if (value != target) {
				float dist = target - value;
				if (dist < 0) {
					dist += OAR_ROTATION_PERIOD;
				}
				if (dist < 1e-4 && preApplyValue < target) {
					value = target;
				} else {
					float increment = dist * 0.085F;
					if (increment > 0.005F) {
						increment = 0.005F;
					}
					value += increment;
				}
			}
			setOarRotation(side, value);
		}
	}

	private void synchronizeOars() {
		if (getRowForce(synchronizer) == 0) {
			return;
		}
		ShipSide desynced = synchronizer.getOpposite();
		if (getRowForce(desynced) == 0) {
			return;
		}
		float target = getOarRotation(synchronizer);
		float value = getOarRotation(desynced);
		if (Math.abs(target - value) < 1e-6F) {
			return;
		}
		if (target < value) {
			synchronizer = desynced;
			return;
		}
		value += 0.0045F;
		if (value > target) {
			value = target;
		}
		setOarRotation(desynced, value);
	}

	private void adjustOarRotationForInterpolation(ShipSide side) {
		float value = MathUtils.adjustValueForInterpolation(getOarRotation(side), prevOarRotation.get(side), 0, OAR_ROTATION_PERIOD);
		prevOarRotation.put(side, value);
	}

	private void updateClientOarRotation(ShipSide side) {
		if (oarRotationBuffer.get(side) == getOarRotation(side)) {
			prevOarRotation.put(side, oarRotationBuffer.get(side));
		} else {
			oarRotationBuffer.put(side, getOarRotation(side));
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
		if (serverT > 0) {
			double x = posX + (serverX - posX) / serverT;
			double y = posY + (serverY - posY) / serverT;
			double z = posZ + (serverZ - posZ) / serverT;
			rotationYaw = rotationYaw + MathHelper.wrapAngleTo180_float(boatYaw - rotationYaw) / serverT;
			rotationPitch = rotationPitch + (boatPitch - rotationPitch) / serverT;
			serverT--;
			setPosition(x, y, z);
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
				float leftOarForce = getRowForce(ShipSide.STARBOARD);
				float rightOarForce = getRowForce(ShipSide.PORT);
				float forceFactor = 0.5F;
				if (leftOarForce > 0) {
					updateOarRotation(ShipSide.STARBOARD, leftOarForce * getOarWaterResistance(ShipSide.STARBOARD));
					if (canOarsApplyForce()) {
						leftOarForce *= getOarPeriodicForceApplyment(ShipSide.STARBOARD);
						Vec3 leftLever = Vec3.createVectorHelper(0, 0, leftOarForce);
						motion = motion.addVector(0, 0, leftOarForce * forceFactor);
						Vec3 cross = rowForce.crossProduct(leftLever);
						rotation = rotation.addVector(cross.xCoord, cross.yCoord, cross.zCoord);
					}
				}
				if (rightOarForce > 0) {
					updateOarRotation(ShipSide.PORT, rightOarForce * getOarWaterResistance(ShipSide.PORT));
					if (canOarsApplyForce()) {
						rightOarForce *= getOarPeriodicForceApplyment(ShipSide.PORT);
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

	private float getOarPeriodicForceApplyment(ShipSide side) {
		return MathUtils.linearTransformf(getOarPitch(side), -1, 1, 0, 2);
	}

	private float getOarWaterResistance(ShipSide side) {
		float weight = MathUtils.linearTransformf(getOarPitch(side), -1, 1, 1, 0.25F);
		float velocity = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
		final float max = 0.5F;
		if (velocity > max) {
			velocity = max;
		}
		float t = velocity / max;
		return weight + (1 - weight) * t;
	}

	private float getOarPitch(ShipSide side) {
		return MathHelper.cos(getOarRotation(side) * OAR_ROTATION_SCALE); 
	}

	private void doSplash() {
		if (worldObj.getBlock(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY), MathHelper.floor_double(posZ)).getMaterial() != Material.water) {
			return;
		}
		if (serverT == -1) {
			return;
		}
		double motionX = serverX - posX;
		double motionY = serverY - posY;
		double motionZ = serverZ - posZ;
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

	public float getRowForce(ShipSide side) {
		return 0.017F * rowForce.get(side);
	}

	public void updateOarRotation(ShipSide side, float value) {
		setOarRotation(side, getOarRotation(side) + value);
	}

	public boolean stroke(ShipSide side, boolean oarStroke, boolean prevOarStroke) {
		float force = rowForce.get(side);
		int time = rowTime.get(side);
		time++;
		boolean appliedForce = false;
		if (oarStroke || time < 10) {
			if (!prevOarStroke && oarStroke && time >= 10) {
				force = 1;
				time = 0;
			} else {
				force = Math.max(force - 0.05F, 0.55F);
			}
		} else {
			force = Math.max(force - 0.1F, 0);
			appliedForce = force > 0;
		}
		rowTime.put(side, time);
		rowForce.put(side, force);
		return appliedForce;
	}

	@Override
	public boolean handleWaterMovement() {
		if (worldObj.handleMaterialAcceleration(boundingBox.contract(0.001, 0.399, 0.001), Material.water, this)) {
			if (!inWater) {
				float volume = MathHelper.sqrt_double(motionX * motionX * 0.2 + motionY * motionY + motionZ * motionZ * 0.2) * 0.2F;
				if (volume > 0.15) {
					if (volume > 1) {
						volume = 1;
					}
					playSound(getSplashSound(), volume, 1 + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
					float min = (float) MathHelper.floor_double(boundingBox.minY);
					for (int i = 0; i < (1 + width * 20); i++) {
						float x = (rand.nextFloat() * 2 - 1) * width;
						float z = (rand.nextFloat() * 2 - 1) * width;
						worldObj.spawnParticle("bubble", posX + x, min + 1, posZ + z, motionX, motionY - rand.nextFloat() * 0.2F, motionZ);
					}
					for (int i = 0; i < (1 + width * 20); i++) {
						float x = (rand.nextFloat() * 2 - 1) * width;
						float z = (rand.nextFloat() * 2 - 1) * width;
						worldObj.spawnParticle("splash", posX + x, min + 1, posZ + z, motionX, motionY, motionZ);
					}
				}
			}
			fallDistance = 0;
			inWater = true;
			extinguish();
		} else {
			inWater = false;
		}
		return inWater;
	}

	public void setOarRotation(ShipSide side, float value) {
		while (value > OAR_ROTATION_PERIOD) {
			value -= OAR_ROTATION_PERIOD;
		}
		while (value < 0) {
			value += OAR_ROTATION_PERIOD;
		}
		dataWatcher.updateObject(OAR_ROTATION_IDS.get(side), value);
	}

	public float getOarRotation(ShipSide side, float delta) {
		float prevRotation = prevOarRotation.get(side);
		float rotation = getOarRotation(side);
		return (float) MathHelper.denormalizeClamp(prevRotation, rotation, delta);
	}

	public float getOarRotation(ShipSide side) {
		return dataWatcher.getWatchableObjectFloat(OAR_ROTATION_IDS.get(side));
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
		if (id == OAR_ROTATION_IDS.get(ShipSide.STARBOARD)) {
			prevOarRotation.put(ShipSide.STARBOARD, oarRotationBuffer.get(ShipSide.STARBOARD));
		} else if (id == OAR_ROTATION_IDS.get(ShipSide.PORT)) {
			prevOarRotation.put(ShipSide.PORT, oarRotationBuffer.get(ShipSide.PORT));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setFloat("oarRotationLeft", getOarRotation(ShipSide.STARBOARD));
		compound.setFloat("oarForceLeft", rowForce.get(ShipSide.STARBOARD));
		compound.setInteger("oarTimeLeft", rowTime.get(ShipSide.STARBOARD));
		compound.setFloat("oarRotationRight", getOarRotation(ShipSide.PORT));
		compound.setFloat("oarForceRight", rowForce.get(ShipSide.PORT));
		compound.setInteger("oarTimeRight", rowTime.get(ShipSide.PORT));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		setOarRotation(ShipSide.STARBOARD, compound.getFloat("oarRotationLeft"));
		rowForce.put(ShipSide.STARBOARD, compound.getFloat("oarForceLeft"));
		rowTime.put(ShipSide.STARBOARD, compound.getInteger("oarTimeLeft"));
		setOarRotation(ShipSide.PORT, compound.getFloat("oarRotationRight"));
		rowForce.put(ShipSide.PORT, compound.getFloat("oarForceRight"));
		rowTime.put(ShipSide.PORT, compound.getInteger("oarTimeRight"));
	}
}
