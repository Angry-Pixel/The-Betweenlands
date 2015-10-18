package thebetweenlands.entities.rowboat;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
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

	private static final int LEFT_OAR = 0;

	private static final int RIGHT_OAR = 1;

	/*private static final float CATCH_POINT_Z = 2;

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

	protected static final int STROKE_SQUARE_TIME = 20;*/

	private boolean hadPlayer;

	/*private Oar leftOar;

	private Oar rightOar;*/

	private final float[] f = new float[2];

	private final int[] g = new int[2];

	private final float[] h = new float[2];

	private float ao;

	private float ap;

	private float aq;

	private float ar;

	private double boatX;

	private double boatY;

	private double boatZ;

	private float boatYaw;

	private float boatPitch;

	private int boatPosRotationIncrements;

	public EntityWeedwoodRowboat(World world) {
		super(world);
		preventEntitySpawning = true;
		setSize(2, 0.9F);
		yOffset = height / 2;
//		leftOar = new Oar();
//		rightOar = new Oar();
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
		//leftOar.updateControls(oarStrokeLeft, oarSquareLeft);
		//rightOar.updateControls(oarStrokeRight, oarSquareRight);
	}

	private boolean bo() {
		return !(worldObj.isRemote ^ riddenByEntity instanceof EntityPlayer);
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
	}

	private void s() {
		// TODO
	}

	private void t() {
		// TODO
	}

	private void u() {
		if (riddenByEntity != null) {
			if (aq < 25) {
				Vec3 param_40 = Vec3.createVectorHelper(3, 0, 0);
				Vec3 param_41 = Vec3.createVectorHelper(0, 0, 0);
				Vec3 param_42 = Vec3.createVectorHelper(0, 0, 0);
				float param_43 = a(LEFT_OAR);
				float param_44 = a(LEFT_OAR);
				if (param_43 > 0) {
					
				}
			}
		}
	}

	public boolean k() {
		return ao > 1;
	}

	public float a(int side) {
		return 0.01375F * f[side]; // magic number
	}

	public void a(int side, float value) {
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

	public float joinOarRotation(int side, float value) {
		float rotationA = this.h[side];
		float rotationB = getOarRotation(side);
		return MathUtils.joinFloat(rotationA, rotationB, value);
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

	public boolean stroke(int side, boolean param_85, boolean param_86) {
		float param_87 = f[side];
		int param_88 = g[side];
		param_88++;
		boolean param_90 = false;
		if (param_85 || param_88 < 10) {
			if (!param_86 && param_85 && param_88 >= 10) {
				param_87 = 1;
				param_88 = 0;
				dataWatcher.updateObject(OAR_ROTATION_IDS[side], 0);
			} else {
				param_87 = Math.max(param_87 - 0.05F, 0.55F);
			}
		} else {
			param_87 = Math.max(param_87 - 0.1F, 0);
			param_90 = param_87 > 0;
		}
		g[side] = param_88;
		f[side] = param_87;
		return param_90;
	}
}
