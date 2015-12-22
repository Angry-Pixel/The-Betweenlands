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
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
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

	private static final EnumMap<ShipSide, Integer> ROW_PROGRESS_IDS = ShipSide.newEnumMap(int.class, 20, 21);

	private static final float OAR_ROTATION_SCALE = -28;

	private static final float ROW_PROGRESS_PERIOD = MathUtils.TAU / Math.abs(OAR_ROTATION_SCALE);

	private static final float OAR_LENGTH = 40F / 16;
	
	private static final float BLADE_LENGTH = 12F / 16;

	private static final float LOOM_LENGTH = OAR_LENGTH - BLADE_LENGTH;

	private boolean hadPlayer;

	private EnumMap<ShipSide, Float> rowForce = ShipSide.newEnumMap(float.class);

	private EnumMap<ShipSide, Integer> rowTime = ShipSide.newEnumMap(int.class);

	private EnumMap<ShipSide, Float> rowProgressBuffer = ShipSide.newEnumMap(float.class);

	private EnumMap<ShipSide, Float> prevRowProgress = ShipSide.newEnumMap(float.class);

	private EnumMap<ShipSide, Boolean> oarInAir = ShipSide.newEnumMap(boolean.class);

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
		dataWatcher.addObject(ROW_PROGRESS_IDS.get(ShipSide.STARBOARD), 0F);
		dataWatcher.addObject(ROW_PROGRESS_IDS.get(ShipSide.PORT), 0F);
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
			animateHullWaterInteraction();
			animateOars();
		} else {
			updateRowForce(ShipSide.STARBOARD, oarStrokeLeft, prevOarStrokeLeft);
			updateRowForce(ShipSide.PORT, oarStrokeRight, prevOarStrokeRight);
			prevOarStrokeLeft = oarStrokeLeft;
			prevOarStrokeRight = oarStrokeRight;
			hitTheQuan();
		}
		super.onUpdate();
		if (worldObj.isRemote) {
			updatePosition();
		} else {
			float rotationLeft = getRowProgress(ShipSide.STARBOARD);
			float rotationRight = getRowProgress(ShipSide.PORT);
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
		adjustStrokeProgressForInterpolation(ShipSide.STARBOARD);
		adjustStrokeProgressForInterpolation(ShipSide.PORT);
	}

	private void returnOarToResting(ShipSide side, float preApplyValue) {
		if (getRowForce(side) == 0) {
			final float target = ROW_PROGRESS_PERIOD * 0.05F;
			float value = getRowProgress(side);
			if (value != target) {
				float dist = target - value;
				if (dist < 0) {
					dist += ROW_PROGRESS_PERIOD;
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
			setRowProgress(side, value);
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
		float target = getRowProgress(synchronizer);
		float value = getRowProgress(desynced);
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
		setRowProgress(desynced, value);
	}

	private void adjustStrokeProgressForInterpolation(ShipSide side) {
		float value = MathUtils.adjustValueForInterpolation(getRowProgress(side), prevRowProgress.get(side), 0, ROW_PROGRESS_PERIOD);
		prevRowProgress.put(side, value);
	}

	private void updateClientOarRotation(ShipSide side) {
		if (rowProgressBuffer.get(side) == getRowProgress(side)) {
			prevRowProgress.put(side, rowProgressBuffer.get(side));
		} else {
			rowProgressBuffer.put(side, getRowProgress(side));
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
					updateRowProgress(ShipSide.STARBOARD, leftOarForce * getOarWaterResistance(ShipSide.STARBOARD));
					if (canOarsApplyForce()) {
						leftOarForce *= getOarPeriodicForceApplyment(ShipSide.STARBOARD);
						Vec3 leftLever = Vec3.createVectorHelper(0, 0, leftOarForce);
						motion = motion.addVector(0, 0, leftOarForce * forceFactor);
						Vec3 cross = rowForce.crossProduct(leftLever);
						rotation = rotation.addVector(cross.xCoord, cross.yCoord, cross.zCoord);
					}
				}
				if (rightOarForce > 0) {
					updateRowProgress(ShipSide.PORT, rightOarForce * getOarWaterResistance(ShipSide.PORT));
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
		return MathUtils.linearTransformf(getOarElevation(side), -1, 1, 0, 2);
	}

	private float getOarWaterResistance(ShipSide side) {
		float weight = MathUtils.linearTransformf(getOarElevation(side), -1, 1, 1, 0.25F);
		float velocity = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
		final float max = 0.5F;
		if (velocity > max) {
			velocity = max;
		}
		float t = velocity / max;
		return weight + (1 - weight) * t;
	}

	private float getOarElevation(ShipSide side) {
		return MathHelper.cos(getRowProgress(side) * OAR_ROTATION_SCALE); 
	}

	private void animateHullWaterInteraction() {
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

	private void animateOars() {
		if (serverT == -1) {
			return;
		}
		double motionX = serverX - posX;
		double motionY = serverY - posY;
		double motionZ = serverZ - posZ;
		if (motionY == 0) {
			motionY = 1e-8;
		}
		animateOar(ShipSide.STARBOARD, motionX, motionY, motionZ);
		animateOar(ShipSide.PORT, motionX, motionY, motionZ);
	}

	private void animateOar(ShipSide side, double motionX, double motionY, double motionZ) {
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		Vec3 oarlock = getOarlockPosition(side);
		Vec3 oarVector = getOarVector(side);
		Vec3 blade = oarlock.addVector(oarVector.xCoord * OAR_LENGTH, oarVector.yCoord * OAR_LENGTH, oarVector.zCoord * OAR_LENGTH);
		MovingObjectPosition raytrace = worldObj.rayTraceBlocks(Vec3.createVectorHelper(oarlock.xCoord, oarlock.yCoord, oarlock.zCoord), blade, true);
		boolean bladeInAir = true;
		float amountOfBladeInAir = BLADE_LENGTH;
		if (raytrace != null && raytrace.typeOfHit == MovingObjectType.BLOCK) {
			if (velocity > 0.175) {
				for (int p = 0; p < velocity * 4; p++) {
					float x = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
					float y = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
					float z = MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.2F, 0.2F);
					worldObj.spawnParticle("splash", raytrace.hitVec.xCoord + x, raytrace.hitVec.yCoord + y, raytrace.hitVec.zCoord + z, motionX, motionY, motionZ);
				}
			}
			float amountInAir = (float) oarlock.distanceTo(raytrace.hitVec);
			if (amountInAir < LOOM_LENGTH) {
				bladeInAir = false;
			} else {
				amountOfBladeInAir = OAR_LENGTH - amountInAir;
			}
		}
		if (bladeInAir) {
			if (rand.nextFloat() < 0.4F) {
				for (int p = 0, count = (int) (1 + velocity * 3); p < count; p++) {
					float point = (LOOM_LENGTH + rand.nextFloat() * amountOfBladeInAir);
					float x = (float) (oarVector.xCoord * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.1F, 0.1F));
					float y = (float) (oarVector.yCoord * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.4F, -0.2F));
					float z = (float) (oarVector.zCoord * point + MathUtils.linearTransformf(rand.nextFloat(), 0, 1, -0.1F, 0.1F));
					worldObj.spawnParticle("splash", oarlock.xCoord + x, oarlock.yCoord + y, oarlock.zCoord + z, 0, 1e-8, 0);
				}
			}
		}
		oarInAir.put(side, bladeInAir);
	}

	private Vec3 getOarlockPosition(ShipSide side) {
		float dir = side == ShipSide.PORT ? -1 : 1;
		float sideX = MathHelper.cos((rotationYaw - 90 * dir) * MathUtils.DEG_TO_RAD) * 0.6F;
		float sideZ = MathHelper.sin((rotationYaw - 90 * dir) * MathUtils.DEG_TO_RAD) * 0.6F;
		float forwardX = MathHelper.cos(rotationYaw * MathUtils.DEG_TO_RAD) * 0.2F;
		float forwardZ = MathHelper.sin(rotationYaw * MathUtils.DEG_TO_RAD) * 0.2F;
		return Vec3.createVectorHelper(posX + sideX + forwardX, posY + 1 - yOffset + 0.15F, posZ + sideZ + forwardZ);
	}

	private Vec3 getOarVector(ShipSide side) {
		float dir = side == ShipSide.PORT ? -1 : 1;
		float progress = getRowProgress(side);
		float yaw = getOarRotationX(progress) * dir - rotationYaw * MathUtils.DEG_TO_RAD;
		float pitch = getOarRotationZ(side, progress) - MathUtils.PI / 2;
		float cosYaw = MathHelper.cos(-yaw);
		float sinYaw = MathHelper.sin(-yaw);
		float cosPitch = MathHelper.cos(-pitch);
		return Vec3.createVectorHelper(-sinYaw * cosPitch, MathHelper.sin(pitch), cosYaw * cosPitch);
	}

	private void hitTheQuan() {
		createOarSoundFX(ShipSide.STARBOARD);
		createOarSoundFX(ShipSide.PORT);
	}

	private void createOarSoundFX(ShipSide side) {
		double velocity = Math.sqrt(motionX * motionX + motionZ * motionZ);
		Vec3 oarlock = getOarlockPosition(side);
		Vec3 oarVector = getOarVector(side);
		Vec3 blade = oarlock.addVector(oarVector.xCoord * OAR_LENGTH, oarVector.yCoord * OAR_LENGTH, oarVector.zCoord * OAR_LENGTH);
		MovingObjectPosition raytrace = worldObj.rayTraceBlocks(Vec3.createVectorHelper(oarlock.xCoord, oarlock.yCoord, oarlock.zCoord), blade, true);
		boolean bladeInAir = true;
		if (raytrace != null && raytrace.typeOfHit == MovingObjectType.BLOCK) {
			float amountInAir = (float) oarlock.distanceTo(raytrace.hitVec);
			if (amountInAir < LOOM_LENGTH) {
				bladeInAir = false;
				float force = rowForce.get(side);
				boolean start = force == 1;
				if (oarInAir.get(side) || start) {
					float volume = force * 0.8F + 0.2F;
					String sound = start ? ModInfo.ID + ":rowboat.row.start" : ModInfo.ID + ":rowboat.row";
					worldObj.playSoundEffect(raytrace.hitVec.xCoord, raytrace.hitVec.yCoord, raytrace.hitVec.zCoord, sound, volume, 0.8F + rand.nextFloat() * 0.3F);	
				}
			}
		}
		oarInAir.put(side, bladeInAir);
	}

	public boolean canOarsApplyForce() {
		return drag <= 1;
	}

	public float getRowForce(ShipSide side) {
		return 0.017F * rowForce.get(side);
	}

	public void updateRowProgress(ShipSide side, float value) {
		setRowProgress(side, getRowProgress(side) + value);
	}

	public boolean updateRowForce(ShipSide side, boolean oarStroke, boolean prevOarStroke) {
		float force = rowForce.get(side);
		int time = rowTime.get(side);
		time++;
		boolean appliedForce = false;
		if (oarStroke || time < 10) {
			if (!prevOarStroke && oarStroke && time >= 10) {
				force = 1;
				time = 0;
				worldObj.playSound(posX, posY, posZ, ModInfo.ID + ":rowboat.row", 1, 0.8F + rand.nextFloat() * 0.3F, true);	
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

	public void setRowProgress(ShipSide side, float progress) {
		while (progress > ROW_PROGRESS_PERIOD) {
			progress -= ROW_PROGRESS_PERIOD;
		}
		while (progress < 0) {
			progress += ROW_PROGRESS_PERIOD;
		}
		dataWatcher.updateObject(ROW_PROGRESS_IDS.get(side), progress);
	}

	public float getRowProgress(ShipSide side, float delta) {
		float prevProgress = prevRowProgress.get(side);
		float progress = getRowProgress(side);
		return (float) MathHelper.denormalizeClamp(prevProgress, progress, delta);
	}

	public float getRowProgress(ShipSide side) {
		return dataWatcher.getWatchableObjectFloat(ROW_PROGRESS_IDS.get(side));
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
		if (id == ROW_PROGRESS_IDS.get(ShipSide.STARBOARD)) {
			prevRowProgress.put(ShipSide.STARBOARD, rowProgressBuffer.get(ShipSide.STARBOARD));
		} else if (id == ROW_PROGRESS_IDS.get(ShipSide.PORT)) {
			prevRowProgress.put(ShipSide.PORT, rowProgressBuffer.get(ShipSide.PORT));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setFloat("rowProgressLeft", getRowProgress(ShipSide.STARBOARD));
		compound.setFloat("rowForceLeft", rowForce.get(ShipSide.STARBOARD));
		compound.setInteger("rowTimeLeft", rowTime.get(ShipSide.STARBOARD));
		compound.setFloat("rowProgressRight", getRowProgress(ShipSide.PORT));
		compound.setFloat("rowForceRight", rowForce.get(ShipSide.PORT));
		compound.setInteger("rowTimeRight", rowTime.get(ShipSide.PORT));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		setRowProgress(ShipSide.STARBOARD, compound.getFloat("rowProgressLeft"));
		rowForce.put(ShipSide.STARBOARD, compound.getFloat("rowForceLeft"));
		rowTime.put(ShipSide.STARBOARD, compound.getInteger("rowTimeLeft"));
		setRowProgress(ShipSide.PORT, compound.getFloat("rowProgressRight"));
		rowForce.put(ShipSide.PORT, compound.getFloat("rowForceRight"));
		rowTime.put(ShipSide.PORT, compound.getInteger("rowTimeRight"));
	}

	public static float getOarRotationX(float theta) {
		return MathHelper.sin(theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE) * 0.6F;
	}

	public static float getOarRotationY(ShipSide side, float theta) {
		float angle = MathUtils.linearTransformf(MathHelper.sin((theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE + MathUtils.PI / 2)), -1, 1, MathUtils.PI / 2, 0);
		if (side == ShipSide.PORT) {
			angle = MathUtils.PI - angle;
		}
		return angle;
	}

	public static float getOarRotationZ(ShipSide side, float theta) {
		float angle = MathHelper.cos(theta * EntityWeedwoodRowboat.OAR_ROTATION_SCALE) * 0.45F - MathUtils.PI / 2.5F;
		if (side == ShipSide.PORT) {
			angle = -angle;
		}
		return angle;
	}
}
