package thebetweenlands.common.entity.mobs;


import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thebetweenlands.common.entity.EntityProximitySpawner;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityChiromawHatchling extends EntityProximitySpawner {

	public int prevRise;
	public float feederRotation, prevFeederRotation;
	public float headPitch, prevHeadPitch;
	public int eatingCooldown;
	public final int MAX_EATING_COOLDOWN = 240; // set to whatever time between hunger cycles
	public final int MIN_EATING_COOLDOWN = 0;
	public final int MAX_RISE = 40;
	public final int MIN_RISE = 0; 
	public final int MAX_FOOD_NEEDED = 2; // amount of times needs to be fed

	private static final DataParameter<Boolean> IS_RISING = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> RISE_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_HUNGRY = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> FOOD_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_CHEWING = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);

	public EntityChiromawHatchling(World world) {
		super(world);
		setSize(1F, 1F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_RISING, false);
		dataManager.register(RISE_COUNT, 0);
		dataManager.register(IS_HUNGRY, true);
		dataManager.register(FOOD_COUNT, 0);
		dataManager.register(IS_CHEWING, false);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		prevRise = getRiseCount();
		prevFeederRotation = feederRotation;
		prevHeadPitch = headPitch;
		if(!getEntityWorld().isRemote && getEntityWorld().getTotalWorldTime()%20 == 0)
			checkArea();

		if (getEntityWorld().isRemote) {
			checkFeeder();

			if (getRising() && getRiseCount() >= MAX_RISE) {
				if (!getIsHungry())
					if (headPitch < 40)
						headPitch += 8;
				if (getIsHungry())
					if (headPitch > 0)
						headPitch -= 8;
			}

			if (!getRising() && getRiseCount() < MAX_RISE)
				headPitch = getRiseCount();
		}

		if (!getEntityWorld().isRemote) {
			if (!getRising() && getRiseCount() > MIN_RISE)
				setRiseCount(getRiseCount() - 4);

			if (getRising() && getRiseCount() < MAX_RISE)
				setRiseCount(getRiseCount() + 4);

			if (!getIsHungry()) {
				eatingCooldown--;
				if (eatingCooldown <= MAX_EATING_COOLDOWN && eatingCooldown > MAX_EATING_COOLDOWN - 60 && !getIsChewing())
					setIsChewing(true);
				if (eatingCooldown < MAX_EATING_COOLDOWN - 60 && getIsChewing())
					setIsChewing(false);
				if (eatingCooldown <= MIN_EATING_COOLDOWN)
					setIsHungry(true);
			}
		}
	}

	protected Entity checkFeeder() {
		Entity entity = null;
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);
				if (entity != null)
					if (entity instanceof EntityPlayer)
						if (!isDead && getRiseCount() >= MAX_RISE)
							lookAtFeeder(entity, 30F);
			}

			if (entity == null && getRiseCount() > MIN_RISE)
				feederRotation = updateFeederRotation(feederRotation, 0F, 30F);

		return entity;
	}

	@Override
	protected Entity checkArea() {
		Entity entity = null;
		if (!getEntityWorld().isRemote) {// && getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL) {
			List<EntityPlayer> list = getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, proximityBox());
			for (int entityCount = 0; entityCount < list.size(); entityCount++) {
				entity = list.get(entityCount);
				if (entity != null) {
					if (entity instanceof EntityPlayer) {// && !((EntityPlayer) entity).isSpectator() && !((EntityPlayer) entity).isCreative()) {
						if (canSneakPast() && entity.isSneaking())
							return null;
						else if (checkSight() && !canEntityBeSeen(entity))
							return null;
						else {
							if(!getRising())
								setRising(true);
						}
						if (!isDead && getRiseCount() >= MAX_RISE) {
							if (getAmountEaten() >= MAX_FOOD_NEEDED && eatingCooldown <= 0) {
								Entity spawn = getEntitySpawned();
								if (spawn != null) {
									performPreSpawnaction(entity, spawn);
									if (!spawn.isDead) { // just in case of pre-emptive removal
										((EntityChiromawTame) spawn).setOwnerId(entity.getUniqueID()); // just for now
										getEntityWorld().spawnEntity(spawn);
									}
									performPostSpawnaction(entity, spawn);
									setDead();
								}
							}
						}
					}
				}
			}
			if (entity == null && getRiseCount() > MIN_RISE) {
				if (getRising())
					setRising(false);
			}
		}
		return entity;
	}

	public void lookAtFeeder(Entity entity, float maxYawIncrease) {
		double distanceX = entity.posX - posX;
		double distanceZ = entity.posZ - posZ;
		float angle = (float) (MathHelper.atan2(distanceZ, distanceX) * (180D / Math.PI)) - 90.0F;
		feederRotation = updateFeederRotation(feederRotation, angle, maxYawIncrease);
	}

	private float updateFeederRotation(float angle, float targetAngle, float maxIncrease) {
		float f = MathHelper.wrapDegrees(targetAngle - angle);
		if (f > maxIncrease)
			f = maxIncrease;
		if (f < -maxIncrease)
			f = -maxIncrease;
		return angle + f;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && getIsHungry()) {
			if (stack.getItem() == ItemRegistry.SNAIL_FLESH_RAW) {
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
					if (stack.getCount() <= 0)
						player.setHeldItem(hand, ItemStack.EMPTY);
				}
				eatingCooldown = MAX_EATING_COOLDOWN;
				setAmountEaten(getAmountEaten() + 1);
				setIsHungry(false);
				return true;
			}
		}
		return super.processInteract(player, hand);
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote)
			setLocationAndAngles(posX, posY, posZ, 0F, 0.0F); // stahp random rotating on spawn with an egg mojang pls
		return livingdata;
	}

	private void setRising(boolean rise) {
		dataManager.set(IS_RISING, rise);
	}

    public boolean getRising() {
        return dataManager.get(IS_RISING);
    }

	private void setRiseCount(int riseCountIn) {
		dataManager.set(RISE_COUNT, riseCountIn);
	}

	public int getRiseCount() {
		return dataManager.get(RISE_COUNT);
	}

	private void setAmountEaten(int foodIn) {
		dataManager.set(FOOD_COUNT, foodIn);
	}

	private int getAmountEaten() {
		return dataManager.get(FOOD_COUNT);
	}

	private void setIsHungry(boolean hungry) {
		dataManager.set(IS_HUNGRY, hungry);
	}

	public boolean getIsHungry() {
		return dataManager.get(IS_HUNGRY);
	}

	private void setIsChewing(boolean chewing) {
		dataManager.set(IS_CHEWING, chewing);
	}

	public boolean getIsChewing() {
		return dataManager.get(IS_CHEWING);
	}

	@Override
	public void onKillCommand() {
		setDead();
	}

	@Override
	protected boolean isMovementBlocked() {
		return true;
	}

	@Override
    public boolean canBePushed() {
        return false;
    }

	@Override
    public boolean canBeCollidedWith() {
        return true;
    }

	@Override
	public void addVelocity(double x, double y, double z) {
	}

	@Override
	public boolean getIsInvulnerable() {
		return false;
	}

	@Override
	protected float getProximityHorizontal() {
		return 5F;
	}

	@Override
	protected float getProximityVertical() {
		return 1F;
	}

	@Override
	protected AxisAlignedBB proximityBox() {
		return new AxisAlignedBB(getPosition()).grow(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal());
	}

	@Override
	protected boolean canSneakPast() {
		return true;
	}

	@Override
	protected boolean checkSight() {
		return true;
	}

	@Override
	protected Entity getEntitySpawned() {
		EntityChiromawTame entity = new EntityChiromawTame(getEntityWorld());
		//entity.setOwnerId(player.getUniqueID());
		return entity;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	protected boolean isSingleUse() {
		return true;
	}

	@Override
	protected int maxUseCount() {
		return 1;
	}
}