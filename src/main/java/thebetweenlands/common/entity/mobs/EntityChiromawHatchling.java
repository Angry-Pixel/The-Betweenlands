package thebetweenlands.common.entity.mobs;


import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.block.misc.BlockOctine;
import thebetweenlands.common.entity.EntityProximitySpawner;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityChiromawHatchling extends EntityProximitySpawner implements IEntityAdditionalSpawnData {
	private static final byte EVENT_HATCH_PARTICLES = 100;
	private static final byte EVENT_FLOAT_UP_PARTICLES = 101;
	private static final byte EVENT_NEW_SPAWN = 102;
	
	public static final int MAX_EATING_COOLDOWN = 3000; // set to whatever time between hunger cycles 3000 = 2.5 minutes
	public static final int MIN_EATING_COOLDOWN = 0;
	public static final int MAX_RISE = 40;
	public static final int MIN_RISE = 0; 
	public static final int MAX_FOOD_NEEDED = 8; // amount of times needs to be fed
	NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
	public float feederRotation, prevFeederRotation, headPitch, prevHeadPitch;
	public int prevHatchAnimation, hatchAnimation, riseCount, prevRise, prevTransformTick, flapArmsCount, blinkCount;
	public boolean flapArms = false;
	private EnumFacing facing = EnumFacing.NORTH;
	
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityChiromawHatchling.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<Boolean> HATCHED = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_RISING = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_HUNGRY = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> EATING_COOLDOWN = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> FOOD_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_CHEWING = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> TRANSFORM = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> TRANSFORM_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> HATCH_COUNT = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.VARINT);
	private static final DataParameter<ItemStack> FOOD_CRAVED = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Boolean> ELECTRIC = EntityDataManager.createKey(EntityChiromawHatchling.class, DataSerializers.BOOLEAN);

	public EntityChiromawHatchling(World world) {
		super(world);
		setSize(0.75F, 1F);
		this.facing = EnumFacing.HORIZONTALS[world.rand.nextInt(4)];
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		dataManager.register(HATCHED, false);
		dataManager.register(IS_RISING, false);
		dataManager.register(IS_HUNGRY, false);
		dataManager.register(EATING_COOLDOWN, 0);
		dataManager.register(FOOD_COUNT, 0);
		dataManager.register(IS_CHEWING, false);
		dataManager.register(TRANSFORM, false);
		dataManager.register(TRANSFORM_COUNT, 0);
		dataManager.register(HATCH_COUNT, 0);
		dataManager.register(FOOD_CRAVED, ItemStack.EMPTY);
		dataManager.register(ELECTRIC, false);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		// STAGE 1
		if (!getHasHatched()) {
			if (!getEntityWorld().isRemote) {
				if (ticksExisted %200 == 0) { // 200 = 10 seconds (no need to count this every second)
					if (getEntityWorld().getBlockState(getPosition().down()).getBlock() instanceof BlockOctine)
						setHatchTick(getHatchTick() + 1); // increment whilst on an octine block.
				}
				if (getHatchTick() >= 60) { // how many increments before hatching 60 = 10 minutes
					getEntityWorld().setEntityState(this, EVENT_HATCH_PARTICLES);
					setIsHungry(true);
					setHasHatched(true);
					getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCH, SoundCategory.BLOCKS, 1F, 1F);
				}
			}

			if (getEntityWorld().isRemote) {
				if (getHatchTick() >= 1) { // animation
					prevHatchAnimation = hatchAnimation;
					hatchAnimation++;
				}
				if(getElectricBoogaloo())
					spawnLightningArcs();
			}
		}

		// STAGE 2
		if (getHasHatched()) {
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
					; // TODO maybe else something to show this is ready to transform/transforming
				
				if (getElectricBoogaloo())
					spawnLightningArcs();

				if (getIsChewing())
					if (getTransformCount() < 60)
						spawnEatingParticles();

				if (!getIsHungry() && getRiseCount() >= MAX_RISE && !getIsChewing()) {
					if(!flapArms && flapArmsCount <= 0) {
						if(rand.nextInt(200) == 0) {
							flapArms = true;
							flapArmsCount = 30;
						}
					}
					if(blinkCount <= 0)
						if(rand.nextInt(200) == 0)
							blinkCount =  rand.nextBoolean() ? 10 : 5;
				}

				if (flapArmsCount >= 0)
					flapArmsCount--;

				if(flapArms && flapArmsCount <= 0)
					flapArms = false;
				
				if (blinkCount >= 0)
					blinkCount--;

			}

			prevRise = getRiseCount();
			if (!getRising() && getRiseCount() > MIN_RISE) {
				setRiseCount(getRiseCount() - 4);
			} else if (getRising() && getRiseCount() < MAX_RISE) {
				setRiseCount(getRiseCount() + 4);
			}
			
			if (!getEntityWorld().isRemote) {
				checkArea();
					
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
					if (getTransformCount() == 1)
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCHLING_TRANSFORM, SoundCategory.NEUTRAL, 1F, 1F);
					if (getTransformCount() <= 60) {
						setTransformCount(getTransformCount() + 1);
						getEntityWorld().setEntityState(this, EVENT_FLOAT_UP_PARTICLES); // TODO - no idea O.o
						}
					if(getOwner() != null)
						lookAtFeeder(getOwner(), 30F);
				}

				if (!isDead && getRiseCount() >= MAX_RISE) {
					if(getIsHungry() && ticksExisted %20 == 0) {
						getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_LONG, SoundCategory.NEUTRAL, 1F, 1F + rand.nextFloat() * 0.125F - rand.nextFloat() * 0.125F);
					}
					if (getAmountEaten() >= MAX_FOOD_NEEDED && getEatingCooldown() <= 0) {
						if (!getIsTransforming())
							setIsTransforming(true);
						if (!isDead && getTransformCount() >= 60) {
							Entity spawn = getEntitySpawned();
							if (spawn != null) {
								if (!spawn.isDead) { // just in case
									getEntityWorld().setEntityState(this, EVENT_NEW_SPAWN);
									getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_MATRIARCH_BARB_FIRE, SoundCategory.NEUTRAL, 1F, 1F + (getEntityWorld().rand.nextFloat() - getEntityWorld().rand.nextFloat()) * 0.8F);
									getEntityWorld().spawnEntity(spawn);
								}
								setDead();
							}
						}
					}
				}
			}
		}
		
		this.rotationYaw = this.facing.getHorizontalAngle();
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		if (getIsHungry() && getRiseCount() < MAX_RISE)
			return SoundRegistry.CHIROMAW_HATCHLING_HUNGRY_SHORT;
		if (!getHasHatched())
			return SoundRegistry.CHIROMAW_HATCHLING_INSIDE_EGG;
		return SoundRegistry.CHIROMAW_HATCHLING_LIVING;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_HATCH_PARTICLES) {
			for (int count = 0; count <= 100; ++count) {
				BLParticles.ITEM_BREAKING.spawn(world, this.posX + (world.rand.nextDouble() - 0.5D), this.posY + 2D + world.rand.nextDouble(), this.posZ + (world.rand.nextDouble() - 0.5D), ParticleArgs.get().withData(new ItemStack(ItemRegistry.CHIROMAW_EGG)));
			}
		}
		
		if(id == EVENT_FLOAT_UP_PARTICLES) {
			// TODO NOT THIS! // no idea how this works it's on a weird timer etc also; not leaves but barbs/feather particles maybe?
			ParticleArgs<?> args = ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
			args.withColor(1F, 0.65F, 0.25F, 0.75F);
			args.withScale(0.5F + rand.nextFloat() * 6);
			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.LEAF_SWIRL.create(this.world, this.posX, this.posY + 2.6D, this.posZ, args));
		}
		
		if(id == EVENT_NEW_SPAWN) {
			// TODO not leaves but barbs/feather particles maybe
			int leafCount = 40;
			float x = (float) (posX);
			float y = (float) (posY + 1.5F);
			float z = (float) (posZ);
			while (leafCount-- > 0) {
				float dx = getEntityWorld().rand.nextFloat() * 1 - 0.5f;
				float dy = getEntityWorld().rand.nextFloat() * 1f - 0.1F;
				float dz = getEntityWorld().rand.nextFloat() * 1 - 0.5f;
				float mag = 0.08F + getEntityWorld().rand.nextFloat() * 0.07F;
				BLParticles.CHIROMAW_TRANSFORM.spawn(getEntityWorld(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(dx * mag, dy * mag, dz * mag));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnEatingParticles() {
		double angle = Math.toRadians(feederRotation + rotationYaw);
		double offSetX = -Math.sin(angle) * 0.35D;
		double offSetZ = Math.cos(angle) * 0.35D;
		BLParticles.ITEM_BREAKING.spawn(world, this.posX + (float) offSetX + (world.rand.nextDouble() * 0.25D - 0.125D) , this.posY + 0.75F, this.posZ + (float) offSetZ + (world.rand.nextDouble() * 0.25D - 0.125D), ParticleArgs.get().withData(this.getFoodCraved()));
	}

	@SideOnly(Side.CLIENT)
	private void spawnLightningArcs() {
		if(getEntityWorld().rand.nextInt(2) == 0) {
			float ox = getEntityWorld().rand.nextFloat() - 0.5f;
			float oy = getEntityWorld().rand.nextFloat() - 0.5f + ((float)this.motionY + getTransformCount() * 0.02F);
			float oz = getEntityWorld().rand.nextFloat() - 0.5f;
			
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
		feederRotation = updateFeederRotation(feederRotation, angle - this.rotationYaw, maxYawIncrease);
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
		player.swingArm(hand);
		if(!getIsTransforming() && getHasHatched()) {
			if (!stack.isEmpty() && !checkFoodEqual(stack, getFoodCraved())) {
				getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCHLING_NO, SoundCategory.NEUTRAL, 1F, 1F);
					return false;
			}
			if (!stack.isEmpty() && getIsHungry()) {
				
				if (checkFoodEqual(stack, getFoodCraved())) {
					if (!player.capabilities.isCreativeMode) {
						stack.shrink(1);
						if (stack.getCount() <= 0)
							player.setHeldItem(hand, ItemStack.EMPTY);
					}
					setEatingCooldown(MAX_EATING_COOLDOWN);
					setAmountEaten(getAmountEaten() + 1);
					getEntityWorld().playSound(null, getPosition(), SoundRegistry.CHIROMAW_HATCHLING_EAT, SoundCategory.NEUTRAL, 1F, 1F);
					setIsHungry(false);
					return true;
				}
			}
		}
		return super.processInteract(player, hand);
	}

	private boolean checkFoodEqual(ItemStack stack, ItemStack foodCraved) {
		if(stack.getItem() == foodCraved.getItem() && stack.getItemDamage() == foodCraved.getItemDamage()) {

			if(stack.getItem() instanceof ItemMob) {
				ResourceLocation cravedEntity = ((ItemMob)foodCraved.getItem()).getCapturedEntityId(foodCraved);
				ResourceLocation stackEntity = ((ItemMob)stack.getItem()).getCapturedEntityId(stack);
				
				if(!Objects.equals(cravedEntity, stackEntity)) {
					return false;
				}
			}
			
			return true;
		}
		return false;
	}

	protected ResourceLocation getFoodCravingLootTable() {
		return LootTableRegistry.CHIROMAW_HATCHLING;
	}

	public ItemStack chooseNewFoodFromLootTable() {
		LootTable lootTable = getEntityWorld().getLootTableManager().getLootTableFromLocation(getFoodCravingLootTable());
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

	@Override
    public void onStruckByLightning(EntityLightningBolt lightningBolt) {
		if(!getHasHatched() && getHatchTick() < 1)
			setElectricBoogaloo(true);
		else
			super.onStruckByLightning(lightningBolt);
    }

	private void setElectricBoogaloo(boolean electric) {
		dataManager.set(ELECTRIC, electric);
	}

    public boolean getElectricBoogaloo() {
        return dataManager.get(ELECTRIC);
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
		this.riseCount = riseCountIn;
	}

	public int getRiseCount() {
		return this.riseCount;
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
		if(getIsTransforming())
			return false;
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
		if(hasCustomName())
			entity.setCustomNameTag(getCustomNameTag());
		entity.setLocationAndAngles(posX, posY + 1F, posZ, feederRotation + rotationYaw, 0.0F);
		entity.rotationYawHead = entity.rotationYaw;
		entity.renderYawOffset = entity.rotationYaw;
		entity.setMoveForward(0.1F);
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
		nbt.setInteger("Facing", this.facing.ordinal());
		nbt.setBoolean("Electric", getElectricBoogaloo());

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
		this.facing = EnumFacing.VALUES[nbt.getInteger("Facing")];
		setElectricBoogaloo(nbt.getBoolean("Electric"));

		NBTTagCompound nbtFood = (NBTTagCompound) nbt.getTag("Items");
		ItemStack stack = new ItemStack(ItemRegistry.SNAIL_FLESH_RAW);
		if(nbtFood != null)
			stack = new ItemStack(nbtFood);
		setFoodCraved(stack);
	}

	@Nullable
	public UUID getOwnerId() {
		return dataManager.get(OWNER_UNIQUE_ID).orNull();
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

	@Override
	public void writeSpawnData(ByteBuf buf) {
		buf.writeInt(this.facing.ordinal());
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.facing = EnumFacing.VALUES[buf.readInt()];
	}
}