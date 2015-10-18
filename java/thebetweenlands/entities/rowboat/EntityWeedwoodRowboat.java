package thebetweenlands.entities.rowboat;

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

	private static final float CATCH_POINT_Z = 2;

	private static final float CATCH_POINT_X = 0.1875F;

	private static final float MAX_SPEED = 0.4F;

	private static final float ACCELERATION = 0.015F;

	protected static final float DRAG = 0.03F;

	protected static final float OAR_MAX_SPEED = MAX_SPEED / 2;

	protected static final float OAR_ACCELERATION = ACCELERATION / 2;

	protected static final float STROKE_RATIO = 1 / 3F;

	protected static final float STROKE_LENGTH = 2.2F;

	protected static final float STROKE_ANGLE_YAW = (float) Math.asin(STROKE_LENGTH / Math.sqrt(STROKE_LENGTH * STROKE_LENGTH + 4 * CATCH_POINT_Z * CATCH_POINT_Z)) * MathUtils.RAD_TO_DEG;

	protected static final float STROKE_ANGLE_PITCH = 30;

	protected static final float STROKE_MIN_SPEED = 0.05F;

	protected static final float STROKE_MAX_SPEED = 0.1F;

	protected static final float STROKE_PERIOD = MathUtils.TAU;

	protected static final float STROKE_DRIVE = STROKE_PERIOD * STROKE_RATIO;

	protected static final float STROKE_RECOVER = STROKE_PERIOD * (1 - STROKE_RATIO);

	protected static final float BLADE_RESISTANCE = 0.03F;

	protected static final float STROKE_DRIVE_CYCLE = 1 / (2 * STROKE_RATIO);

	protected static final float STROKE_DRIVE_STEEPNESS = 4;

	protected static final float STROKE_REST_POSITION = STROKE_DRIVE + STROKE_RECOVER / 2;

	protected static final float STROKE_REST_SPEED = 0.15F;

	protected static final float STROKE_SQUARE_POSITION = STROKE_DRIVE / 2;

	protected static final int STROKE_SQUARE_TIME = 20;

	private boolean hadPlayer;

	private Oar leftOar;

	private Oar rightOar;

	public EntityWeedwoodRowboat(World world) {
		super(world);
		preventEntitySpawning = true;
		setSize(2, 0.9F);
		yOffset = height / 2;
		leftOar = new Oar();
		rightOar = new Oar();
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

	public void updateControls(boolean oarStrokeLeft, boolean oarStrokeRight, boolean oarSquareLeft, boolean oarSquareRight) {
		leftOar.updateControls(oarStrokeLeft, oarSquareLeft);
		rightOar.updateControls(oarStrokeRight, oarSquareRight);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (worldObj.isRemote) {
			boolean hasPlayer = riddenByEntity instanceof EntityPlayer;
			if (!this.hadPlayer && hasPlayer) {
				TheBetweenlands.proxy.onPlayerEnterWeedwoodRowboat();
			} else if (this.hadPlayer && !hasPlayer) {
				TheBetweenlands.proxy.onPlayerLeaveWeedwoodRowboat();
			}
			this.hadPlayer = hasPlayer;
			doSplash();
		} else {
			if (getTimeSinceHit() > 0) {
				setTimeSinceHit(getTimeSinceHit() - 1);
			}
			if (getDamageTaken() > 0) {
				setDamageTaken(getDamageTaken() - 1);
			}
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
			doBlockBreak();
			leftOar.update();
			rightOar.update();
			float leftForce = leftOar.getForce();
			float rightForce = leftOar.getForce();
//			System.out.printf("%s %s\n", leftForce, rightForce);
			float a = (leftForce - rightForce) / (-2 * CATCH_POINT_Z);
			float c = (leftForce + rightForce) / 2 + CATCH_POINT_X;
			float vecX;
			float vecZ = 0;
			if (a == 0) {
				vecX = leftForce + rightForce;
			} else {
				float b = -1 / a;
				float d = CATCH_POINT_X;
				vecZ = (d - c) / (a - b);
				vecX = a * vecZ + c - CATCH_POINT_X;
			}
			float force = MathHelper.sqrt_float(vecX * vecX + vecZ * vecZ) * (vecX > 0 ? 1 : -1);
			float theta = (float) Math.atan2(vecX, vecZ) * MathUtils.RAD_TO_DEG - 90;
			if (buoyancy < 1) {
				motionY += 0.04 * (buoyancy * 2 - 1);
			} else {
				if (motionY < 0) {
					motionY /= 2;
				}
				motionY += 0.007;
			}
			if (onGround) {
				motionX *= 0.5;
				motionY *= 0.5;
				motionZ *= 0.5;
			}
			setRotation(rotationYaw, rotationPitch);
			List<Entity> collidingEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(0.2, 0, 0.2));
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
		moveEntity(motionX, motionY, motionZ);
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

	private void doBlockBreak() {
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

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return new ItemStack(BLItemRegistry.weedwoodRowboat);
	}
}
