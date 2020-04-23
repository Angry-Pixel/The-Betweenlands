package thebetweenlands.common.entity.mobs;


import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.misc.BlockOctine;
import thebetweenlands.common.entity.EntityProximitySpawner;
import thebetweenlands.common.item.misc.ItemCritters;
import thebetweenlands.common.network.clientbound.PacketParticle;
import thebetweenlands.common.network.clientbound.PacketParticle.ParticleType;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

public class EntityChiromawHatchling extends EntityProximitySpawner {

	public final int MAX_EATING_COOLDOWN = 240; // set to whatever time between hunger cycles
	public final int MIN_EATING_COOLDOWN = 0;
	public final int MAX_RISE = 40;
	public final int MIN_RISE = 0; 
	public final int MAX_FOOD_NEEDED = 5; // amount of times needs to be fed
	NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
	public float feederRotation, prevFeederRotation, headPitch, prevHeadPitch;
	public int prevHatchAnimation, hatchAnimation, prevRise, prevTransformTick;

	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityChiromawHatchling.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Boolean> HATCHED = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_RISING = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> RISE_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_HUNGRY = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> EATING_COOLDOWN = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FOOD_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_CHEWING = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TRANSFORM = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> TRANSFORM_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> HATCH_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<ItemStack> FOOD_CRAVED = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.ITEM_STACK);

	public EntityChiromawHatchling(World world) {
		super(world);
		setSize(1F, 1F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		dataManager.register(HATCHED, false);
		dataManager.register(IS_RISING, false);
		dataManager.register(RISE_COUNT, 0);
		dataManager.register(IS_HUNGRY, false);
		dataManager.register(EATING_COOLDOWN, 0);
		dataManager.register(FOOD_COUNT, 0);
		dataManager.register(IS_CHEWING, false);
		dataManager.register(TRANSFORM, false);
		dataManager.register(TRANSFORM_COUNT, 0);
		dataManager.register(HATCH_COUNT, 0);
		dataManager.register(FOOD_CRAVED, ItemStack.EMPTY);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		// STAGE 1
		if (!getHasHatched()) {
			if (!getEntityWorld().isRemote) {
				if (ticksExisted % 120 == 0) {
					if (getEntityWorld().getBlockState(getPosition().down()).getBlock() instanceof BlockOctine)
						setHatchTick(getHatchTick() + 1); // increment whilst on an octine block.
				}
				if (getHatchTick() >= 10) { // how many increments before hatching
					spawnHatchingParticles();
					setIsHungry(true);
					setHasHatched(true);
				}
			}

			if (getEntityWorld().isRemote) {
				if (getHatchTick() >= 1) { // after the 1st hatch increment
					prevHatchAnimation = hatchAnimation;
					hatchAnimation++;
				}
			}
		}

		// STAGE 2
		if (getHasHatched()) {
			prevRise = getRiseCount();
			prevFeederRotation = feederRotation;
			prevHeadPitch = headPitch;
			prevTransformTick = getTransformCount();

			if (getEntityWorld().isRemote) {
				if(!getIsTransforming())
					checkFeeder();
				else {
					if(getOwner() != null)
						lookAtFeeder(getOwner(), 30F);
					}

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

				if (getAmountEaten() >= MAX_FOOD_NEEDED && !getIsChewing())
					spawnLightningArcs(); // TODO maybe else something to show this is ready to transform/transforming

				if (getIsChewing())
					if (getTransformCount() < 60)
						spawnEatingParticles();

			}

			if (!getEntityWorld().isRemote) {
				checkArea();

				if (!getRising() && getRiseCount() > MIN_RISE)
					setRiseCount(getRiseCount() - 4);

				if (getRising() && getRiseCount() < MAX_RISE)
					setRiseCount(getRiseCount() + 4);

				if (!getIsHungry()) {
					setEatingCooldown(getEatingCooldown() - 1);
					if (getEatingCooldown() <= MAX_EATING_COOLDOWN && getEatingCooldown() > MAX_EATING_COOLDOWN - 60 && !getIsChewing())
						setIsChewing(true);
					if (getEatingCooldown() < MAX_EATING_COOLDOWN - 60 && getIsChewing())
						setIsChewing(false);
					if (getEatingCooldown() <= MIN_EATING_COOLDOWN && getAmountEaten() < MAX_FOOD_NEEDED) {
						setIsHungry(true);
						setFoodCraved(chooseNewFoodFromLootTable());
					}
				}

				if (getIsTransforming()) {
					if (getTransformCount() <= 60)
						setTransformCount(getTransformCount() + 1);
					if(getOwner() != null)
						lookAtFeeder(getOwner(), 30F);
				}

				if (!isDead && getRiseCount() >= MAX_RISE) {
					if (getAmountEaten() >= MAX_FOOD_NEEDED && getEatingCooldown() <= 0) {
						if (!getIsTransforming())
							setIsTransforming(true);
						if (!isDead && getTransformCount() >= 60) {
							Entity spawn = getEntitySpawned();
							if (spawn != null) {
								if (!spawn.isDead) { // just in case
									getEntityWorld().spawnEntity(spawn);
								}
								setDead();
							}
						}
					}
				}
			}
		}
	}

	private void spawnHatchingParticles() {
		TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.CHIROMAW_HATCH, (float) posX, (float) posY + 1F, (float) posZ, 0F, new ItemStack(ItemRegistry.CHIROMAW_EGG)));
	}

	private void spawnEatingParticles() {
		// TODO temp unless item particles chane for critters
		ItemStack tempThing = ItemStack.EMPTY;
		if(getFoodCraved().getItem() instanceof ItemCritters)
			tempThing = new ItemStack(ItemRegistry.SAP_SPIT);
		else
			tempThing = getFoodCraved();
		TheBetweenlands.networkWrapper.sendToAll(new PacketParticle(ParticleType.CHIROMAW_HATCHLING_EAT, (float) posX, (float) posY + 0.75F, (float) posZ, 0F, tempThing));
	}

	@SideOnly(Side.CLIENT)
	private void spawnLightningArcs() {
		if(this.world.rand.nextInt(2) == 0) {
			float ox = this.world.rand.nextFloat() - 0.5f;
			float oy = this.world.rand.nextFloat() - 0.5f + ((float)this.motionY + getTransformCount() * 0.02F);
			float oz = this.world.rand.nextFloat() - 0.5f;
			
			Particle particle = BLParticles.LIGHTNING_ARC.create(this.world, this.posX, this.posY + 0.5F + getTransformCount() * 0.02F, this.posZ, 
					ParticleArgs.get()
					.withMotion(this.motionX, this.motionY, this.motionZ)
					.withColor(0.3f, 0.5f, 1.0f, 0.9f)
					.withData(new Vec3d(this.posX + ox, this.posY + oy, this.posZ + oz)));
			
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);
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

					}
				}
			}
			if (entity == null && getRiseCount() > MIN_RISE && !getIsTransforming()) {
				if (getRising())
					setRising(false);
			}
		}
		return entity;
	}

	@Override
	protected void performPreSpawnaction(@Nullable Entity targetEntity, Entity entitySpawned) {}

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
		if (!stack.isEmpty() && getHasHatched()) {
			if (stack.getItem() == ItemRegistry.NET)
				return false;
		}
		if (!stack.isEmpty() && getIsHungry()) {
			if (stack.getItem() == getFoodCraved().getItem() && stack.getItemDamage() == getFoodCraved().getItemDamage()) {
				if (checkforNBT(stack, getFoodCraved())) {
					if (!player.capabilities.isCreativeMode) {
						stack.shrink(1);
						if (stack.getCount() <= 0)
							player.setHeldItem(hand, ItemStack.EMPTY);
					}
					setEatingCooldown(MAX_EATING_COOLDOWN);
					setAmountEaten(getAmountEaten() + 1);
					setIsHungry(false);
					return true;
				}
			}
		}
		return super.processInteract(player, hand);
	}

	private boolean checkforNBT(ItemStack stack, ItemStack foodCraved) {
		if (stack.hasTagCompound() && foodCraved.hasTagCompound())
			return stack.getTagCompound().equals(foodCraved.getTagCompound());
		return true;
	}

	protected ResourceLocation getFoodCravingLootTable() {
		return LootTableRegistry.CHIROMAW_HATCHLING;
	}

	public ItemStack chooseNewFoodFromLootTable() {
		LootTable lootTable = this.world.getLootTableManager().getLootTableFromLocation(getFoodCravingLootTable());
		if (lootTable != null) {
			LootContext.Builder lootBuilder = (new LootContext.Builder((WorldServer) this.world)).withLootedEntity(this);
			List<ItemStack> loot = lootTable.generateLootForPools(world.rand, lootBuilder.build());
			if (!loot.isEmpty()) {
				Collections.shuffle(loot); // mix it up a bit
				return loot.get(0);
			}
		}
		return new ItemStack(ItemRegistry.SNAIL_FLESH_RAW); // to stop null;
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
			setLocationAndAngles(posX, posY, posZ, 0F, 0.0F); // stahp random rotating on spawn with an egg mojang pls
			setFoodCraved(chooseNewFoodFromLootTable());
		if(checkArea() != null && checkArea() instanceof EntityPlayer)
			setOwnerId(checkArea().getUniqueID());
		}
		return livingdata;
	}

	private void setHasHatched(boolean hatched) {
		dataManager.set(HATCHED, hatched);
	}

    public boolean getHasHatched() {
        return dataManager.get(HATCHED);
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

	private void setEatingCooldown(int cooldown) {
		dataManager.set(EATING_COOLDOWN, cooldown);
	}

	public int getEatingCooldown() {
		return dataManager.get(EATING_COOLDOWN);
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

	private void setIsTransforming(boolean transform) {
		dataManager.set(TRANSFORM, transform);
	}

	public boolean getIsTransforming() {
		return dataManager.get(TRANSFORM);
	}
	
	private void setTransformCount(int transformCountIn) {
		dataManager.set(TRANSFORM_COUNT, transformCountIn);
	}

	public int getTransformCount() {
		return dataManager.get(TRANSFORM_COUNT);
	}

	private void setHatchTick(int hatchCount) {
		dataManager.set(HATCH_COUNT, hatchCount);
		
	}

	public int getHatchTick() {
		return dataManager.get(HATCH_COUNT);
	}

	public void setFoodCraved(ItemStack itemStack) {
		dataManager.set(FOOD_CRAVED, itemStack);
		
	}

	public ItemStack getFoodCraved() {
		return dataManager.get(FOOD_CRAVED);
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
        return true;
    }

	@Override
    public boolean canBeCollidedWith() {
        return true;
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
		entity.setOwnerId(getOwnerId());
		//System.out.println("FEEDER ROTATION: " + feederRotation);
		entity.setLocationAndAngles(posX, posY + 1F, posZ, feederRotation, 0.0F);
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		entity.setMoveForward(0.1F);
        // mojang pls - why wont it spawn rotated?

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

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (getOwnerId() == null)
			nbt.setString("OwnerUUID", "");
		else
			nbt.setString("OwnerUUID", getOwnerId().toString());

		nbt.setBoolean("Hatched", getHasHatched());
		nbt.setInteger("HatchTick", getHatchTick());
		nbt.setBoolean("Rising", getRising());
		nbt.setInteger("RisingCount", getRiseCount());
		nbt.setBoolean("Hungry", getIsHungry());
		nbt.setInteger("FoodEaten", getAmountEaten());
		nbt.setInteger("EatingCooldown", getEatingCooldown());
		nbt.setBoolean("Transforming", getIsTransforming());
		nbt.setInteger("TransformCount", getTransformCount());

		NBTTagCompound nbtFood = new NBTTagCompound();
		getFoodCraved().writeToNBT(nbtFood);
		nbt.setTag("Items", nbtFood);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		String s;
		if (nbt.hasKey("OwnerUUID", 8))
			s = nbt.getString("OwnerUUID");
		else {
			String s1 = nbt.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(getServer(), s1);
		}
		if (!s.isEmpty()) {
			try {
				setOwnerId(UUID.fromString(s));
			} catch (Throwable e) {}
		}

		setHasHatched(nbt.getBoolean("Hatched"));
		setHatchTick(nbt.getInteger("HatchTick"));
		setRising(nbt.getBoolean("Rising"));
		setRiseCount(nbt.getInteger("RisingCount"));
		setIsHungry(nbt.getBoolean("Hungry"));
		setAmountEaten(nbt.getInteger("FoodEaten"));
		setEatingCooldown(nbt.getInteger("EatingCooldown"));
		setIsTransforming(nbt.getBoolean("Transforming"));
		setTransformCount(nbt.getInteger("TransformCount"));

		NBTTagCompound nbtFood = (NBTTagCompound) nbt.getTag("Items");
		ItemStack stack = new ItemStack(ItemRegistry.SNAIL_FLESH_RAW);
		if(nbtFood != null)
			stack = new ItemStack(nbtFood);
		setFoodCraved(stack);
	}

	@Nullable
	public UUID getOwnerId() {
		return (UUID) ((Optional) dataManager.get(OWNER_UNIQUE_ID)).orNull();
	}

	public void setOwnerId(@Nullable UUID uuid) {
		dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(uuid));
	}

	@Nullable
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = getOwnerId();
			return uuid == null ? null : getEntityWorld().getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public boolean isOwner(EntityLivingBase entityIn) {
		return entityIn == getOwner();
	}
}