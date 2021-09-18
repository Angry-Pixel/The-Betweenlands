package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.EntityFishBait;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.TranslationHelper;

public class EntityAnadia extends EntityCreature implements IEntityBL {

	private static final DataParameter<Float> FISH_SIZE = EntityDataManager.<Float>createKey(EntityAnadia.class, DataSerializers.FLOAT);
	private static final DataParameter<Byte> HEAD_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> BODY_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> TAIL_TYPE = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.BOOLEAN);
	//private static final DataParameter<Integer> HUNGER_COOLDOWN = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> STAMINA_TICKS = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> FISH_COLOUR = EntityDataManager.<Byte>createKey(EntityAnadia.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> ESCAPE_TICKS = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ESCAPE_DELAY = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> OBSTRUCTION_TICKS_1 = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> OBSTRUCTION_TICKS_2 = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> OBSTRUCTION_TICKS_3 = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> OBSTRUCTION_TICKS_4 = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<ItemStack> HEAD_ITEM = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<ItemStack> BODY_ITEM = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<ItemStack> TAIL_ITEM = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Boolean> IS_TREASURE_FISH = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> TREASURE_TICKS = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> TREASURE_UNLOCKED = EntityDataManager.createKey(EntityAnadia.class, DataSerializers.BOOLEAN);


	public EntityAnadia.AIFindBait aiFindBait;
	public EntityAnadia.AIFindHook aiFindHook;

	public boolean playAnadiaWonSound = true;

	public int animationFrame = 0;
	public int animationFrameCrab = 0;
	public int netCheck = 0;

	private static final int MAX_NETTABLE_TIME = 20;
	private int nettableTimer = 0;

	public EntityAnadia(World world) {
		super(world);
		setSize(0.8F, 0.8F);
		experienceValue = 3;
		moveHelper = new EntityAnadia.AnadiaMoveHelper(this);
		setPathPriority(PathNodeType.WALKABLE, -8.0F);
		setPathPriority(PathNodeType.BLOCKED, -8.0F);
		setPathPriority(PathNodeType.WATER, 16.0F);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAIAttackMelee(this, 0.7D, true) {
			@Override
			protected double getAttackReachSqr(EntityLivingBase attackTarget) {
				return 0.75D + attackTarget.width;
			}
		});
		tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityLurker.class, 8F, 4D, 8D));
		tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 0.4D));
		tasks.addTask(3, new EntityAnadia.EntityAIAFishCalledWander(this, 0.5D, 20));
		tasks.addTask(4, new EntityAIPanicWhenUnhooked(this));
		tasks.addTask(5, new EntityAIPanicWhenHooked(this));
		aiFindBait = new EntityAnadia.AIFindBait(this, 2D);
		aiFindHook = new EntityAnadia.AIFindHook(this, 2D);
		tasks.addTask(6, aiFindBait);
		tasks.addTask(7, aiFindHook);
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(9, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(FISH_SIZE, 0.5F);
		dataManager.register(HEAD_TYPE, (byte) EnumAnadiaHeadParts.HEAD_1.ordinal());
		dataManager.register(BODY_TYPE, (byte) EnumAnadiaBodyParts.BODY_1.ordinal());
		dataManager.register(TAIL_TYPE, (byte) EnumAnadiaTailParts.TAIL_1.ordinal());
		dataManager.register(IS_LEAPING, false);
		//  dataManager.register(HUNGER_COOLDOWN, 0);
		dataManager.register(STAMINA_TICKS, 40);
		dataManager.register(ESCAPE_TICKS, 1024);
		dataManager.register(ESCAPE_DELAY, 80);
		dataManager.register(OBSTRUCTION_TICKS_1, 64);
		dataManager.register(OBSTRUCTION_TICKS_2, 128);
		dataManager.register(OBSTRUCTION_TICKS_3, 192);
		dataManager.register(OBSTRUCTION_TICKS_4, 256);
		dataManager.register(FISH_COLOUR, (byte) EnumAnadiaColor.BASE.ordinal());
		dataManager.register(HEAD_ITEM, ItemStack.EMPTY);
		dataManager.register(BODY_ITEM, ItemStack.EMPTY);
		dataManager.register(TAIL_ITEM, ItemStack.EMPTY);
		dataManager.register(IS_TREASURE_FISH, false);
		dataManager.register(TREASURE_TICKS, 256);
		dataManager.register(TREASURE_UNLOCKED, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if(!getEntityWorld().isRemote) {
			this.randomizeAnadiaProperties();
			randomiseObstructionOrder();
		}
		return super.onInitialSpawn(difficulty, livingdata);
	}

	public void randomizeAnadiaProperties() {
		setHeadType(EnumAnadiaHeadParts.random(this.rand));
		setBodyType(EnumAnadiaBodyParts.random(this.rand));
		setTailType(EnumAnadiaTailParts.random(this.rand));
		setFishSize(Math.round(Math.max(0.125F, rand.nextFloat()) * 16F) / 16F);
		if(getEntityWorld().getBiome(getPosition()) == BiomeRegistry.DEEP_WATERS)
			setFishColour(EnumAnadiaColor.SILVER);
		else if(getEntityWorld().getBiome(getPosition()) == BiomeRegistry.PATCHY_ISLANDS)
			setFishColour(EnumAnadiaColor.GREEN);
		else if(getEntityWorld().getBiome(getPosition()) == BiomeRegistry.COARSE_ISLANDS)
			setFishColour(EnumAnadiaColor.PURPLE);
		else
			setFishColour(EnumAnadiaColor.BASE);
		setHeadItem(getPartFromLootTable(LootTableRegistry.ANADIA_HEAD));
		setBodyItem(getPartFromLootTable(LootTableRegistry.ANADIA_BODY));
		setTailItem(getPartFromLootTable(LootTableRegistry.ANADIA_TAIL));

		if (getEntityWorld().getBiome(getPosition()) != BiomeRegistry.DEEP_WATERS) {
			if (getStaminaMods() >= 5F && getFishSize() >= 0.875F)
				setIsTreasureFish(true);
		} else if (getStaminaMods() >= 4.5F && getFishSize() >= 0.5F) // will happen on smaller && less stamina fish more often in deep waters
			setIsTreasureFish(true);

	}

	public float getFishSize() {
		return dataManager.get(FISH_SIZE);
	}

	public void setFishSize(float size) {
		dataManager.set(FISH_SIZE, size);
		setSize(getFishSize(), getFishSize() * 0.75F);
		setPosition(posX, posY, posZ);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D + getSpeedMods());
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D + getHealthMods());
		setHealth(getMaxHealth());
	}

	public void setAsLootFish(boolean lootFish) {
		if(lootFish) {
			setBodyItem(getPartFromLootTable(LootTableRegistry.ANADIA_TREASURE));
			setTreasureUnlocked(true);
		} else {
			setBodyItem(getPartFromLootTable(LootTableRegistry.ANADIA_BODY));
			setTreasureUnlocked(false);
		}
	}

	public void setHeadType(EnumAnadiaHeadParts type) {
		dataManager.set(HEAD_TYPE, (byte) type.ordinal());
	}

	public EnumAnadiaHeadParts getHeadType() {
		return EnumAnadiaHeadParts.get(dataManager.get(HEAD_TYPE));
	}

	public void setBodyType(EnumAnadiaBodyParts type) {
		dataManager.set(BODY_TYPE, (byte) type.ordinal());
	}

	public EnumAnadiaBodyParts getBodyType() {
		return EnumAnadiaBodyParts.get(dataManager.get(BODY_TYPE));
	}

	public void setTailType(EnumAnadiaTailParts type) {
		dataManager.set(TAIL_TYPE, (byte) type.ordinal());
	}

	public EnumAnadiaTailParts getTailType() {
		return EnumAnadiaTailParts.get(dataManager.get(TAIL_TYPE));
	}

	public void setFishColour(EnumAnadiaColor colour) {
		dataManager.set(FISH_COLOUR, (byte) colour.ordinal());
	}

	public EnumAnadiaColor getFishColour() {
		return EnumAnadiaColor.get(dataManager.get(FISH_COLOUR));
	}

	private void setIsLeaping(boolean leaping) {
		dataManager.set(IS_LEAPING, leaping);
	}

	public boolean isLeaping() {
		return dataManager.get(IS_LEAPING);
	}


	/*    
    public int getHungerCooldown() {
        return dataManager.get(HUNGER_COOLDOWN);
    }

    private void setHungerCooldown(int count) {
        dataManager.set(HUNGER_COOLDOWN, count);
    }
	 */

	public void setStaminaTicks(int count) {
		dataManager.set(STAMINA_TICKS, count);
	}

	public int getStaminaTicks() {
		return dataManager.get(STAMINA_TICKS);
	}

	public void setEscapeTicks(int count) {
		dataManager.set(ESCAPE_TICKS, count);
	}

	public int getEscapeTicks() {
		return dataManager.get(ESCAPE_TICKS);
	}

	public void setEscapeDelay(int count) {
		dataManager.set(ESCAPE_DELAY, count);
	}

	public int getEscapeDelay() {
		return dataManager.get(ESCAPE_DELAY);
	}

	public void setObstruction1Ticks(int count) {
		dataManager.set(OBSTRUCTION_TICKS_1, count);
	}

	public int getObstruction1Ticks() {
		return dataManager.get(OBSTRUCTION_TICKS_1);
	}

	public void setObstruction2Ticks(int count) {
		dataManager.set(OBSTRUCTION_TICKS_2, count);
	}

	public int getObstruction2Ticks() {
		return dataManager.get(OBSTRUCTION_TICKS_2);
	}

	public void setObstruction3Ticks(int count) {
		dataManager.set(OBSTRUCTION_TICKS_3, count);
	}

	public int getObstruction3Ticks() {
		return dataManager.get(OBSTRUCTION_TICKS_3);
	}

	public void setObstruction4Ticks(int count) {
		dataManager.set(OBSTRUCTION_TICKS_4, count);
	}

	public int getObstruction4Ticks() {
		return dataManager.get(OBSTRUCTION_TICKS_4);
	}

	public void setHeadItem(ItemStack itemStack) {
		dataManager.set(HEAD_ITEM, itemStack);
	}

	public ItemStack getHeadItem() {
		return dataManager.get(HEAD_ITEM);
	}

	public void setBodyItem(ItemStack itemStack) {
		dataManager.set(BODY_ITEM, itemStack);
	}

	public ItemStack getBodyItem() {
		return dataManager.get(BODY_ITEM);
	}

	public void setTailItem(ItemStack itemStack) {
		dataManager.set(TAIL_ITEM, itemStack);
	}

	public ItemStack getTailItem() {
		return dataManager.get(TAIL_ITEM);
	}

	public void setIsTreasureFish(boolean treasure) {
		dataManager.set(IS_TREASURE_FISH, treasure);
	}

	public boolean isTreasureFish() {
		return dataManager.get(IS_TREASURE_FISH);
	}

	public void setTreasureTicks(int count) {
		dataManager.set(TREASURE_TICKS, count);
	}

	public int getTreasureTicks() {
		return dataManager.get(TREASURE_TICKS);
	}

	public void setTreasureUnlocked(boolean unlocked) {
		dataManager.set(TREASURE_UNLOCKED, unlocked);
	}

	public boolean getTreasureUnlocked() {
		return dataManager.get(TREASURE_UNLOCKED);
	}

	public int getNettableTimer() {
		return this.nettableTimer;
	}

	public void randomiseObstructionOrder() {
		List<Integer> obstructionList = new ArrayList<Integer>();
		for(int i = 0; i < 4; i++)
			obstructionList.add(i, 64 + i * 64 + rand.nextInt(32) - rand.nextInt(32));
		Collections.shuffle(obstructionList);
		setObstruction1Ticks(obstructionList.get(0));
		setObstruction2Ticks(obstructionList.get(1));
		setObstruction3Ticks(obstructionList.get(2));
		setObstruction4Ticks(obstructionList.get(3) * 2);
		setEscapeTicks((int) (124 + getStaminaMods() * 100));
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (FISH_SIZE.equals(key)) {
			setSize(getFishSize(), getFishSize());
			rotationYaw = rotationYawHead;
			renderYawOffset = rotationYawHead;
		}
		if (FISH_COLOUR.equals(key)) {
			setFishColour(getFishColour());
		}
		super.notifyDataManagerChange(key);
	}

	@Override
	public String getName() {
		String body = TranslationHelper.translateToLocal("entity.thebetweenlands.anadia_body" + "_" + getBodyType().ordinal());
		String tail = TranslationHelper.translateToLocal("entity.thebetweenlands.anadia_tail" + "_" + getTailType().ordinal());
		String head = TranslationHelper.translateToLocal("entity.thebetweenlands.anadia_head" + "_" + getHeadType().ordinal());
		return body + " " + tail + " " + head;
	}

	public ItemStack getPartFromLootTable(ResourceLocation lootTableIn) {
		if(this.world instanceof WorldServer) {
			LootTable lootTable = this.world.getLootTableManager().getLootTableFromLocation(lootTableIn);
			if (lootTable != null) {
				LootContext.Builder lootBuilder = (new LootContext.Builder((WorldServer) this.world).withLootedEntity(this));
				List<ItemStack> loot = lootTable.generateLootForPools(this.world.rand, lootBuilder.build());
				if (!loot.isEmpty()) 
					return loot.get(0);
			}
		}
		return ItemStack.EMPTY; // to stop null;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("headType", (byte) getHeadType().ordinal());
		nbt.setByte("bodyType", (byte) getBodyType().ordinal());
		nbt.setByte("tailType", (byte) getTailType().ordinal());
		nbt.setFloat("fishSize", getFishSize());
		//		nbt.setInteger("hunger", getHungerCooldown());
		nbt.setByte("fishColour", (byte) getFishColour().ordinal());
		nbt.setBoolean("isTreasureFish", isTreasureFish());
		nbt.setBoolean("isTreasureUnlocked", getTreasureUnlocked());

		NBTTagCompound headItem = new NBTTagCompound();
		getHeadItem().writeToNBT(headItem);
		nbt.setTag("headItem", headItem);

		NBTTagCompound bodyItem = new NBTTagCompound();
		getBodyItem().writeToNBT(bodyItem);
		nbt.setTag("bodyItem", bodyItem);

		NBTTagCompound tailItem = new NBTTagCompound();
		getTailItem().writeToNBT(tailItem);
		nbt.setTag("tailItem", tailItem);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setHeadType(EnumAnadiaHeadParts.get(nbt.getByte("headType")));
		setBodyType(EnumAnadiaBodyParts.get(nbt.getByte("bodyType")));
		setTailType(EnumAnadiaTailParts.get(nbt.getByte("tailType")));
		setFishSize(nbt.getFloat("fishSize"));
		//		setHungerCooldown(nbt.getInteger("hunger"));
		setFishColour(EnumAnadiaColor.get(nbt.getByte("fishColour")));
		setIsTreasureFish(nbt.getBoolean("isTreasureFish"));
		setTreasureUnlocked(nbt.getBoolean("isTreasureUnlocked"));

		NBTBase headItem = nbt.getTag("headItem");
		ItemStack stackHead = ItemStack.EMPTY;
		if(headItem instanceof NBTTagCompound)
			stackHead = new ItemStack((NBTTagCompound) headItem);
		setHeadItem(stackHead);

		NBTBase bodyItem = nbt.getTag("bodyItem");
		ItemStack stackBody = ItemStack.EMPTY;
		if(bodyItem instanceof NBTTagCompound)
			stackBody = new ItemStack((NBTTagCompound) bodyItem);
		setBodyItem(stackBody);

		NBTBase tailItem = nbt.getTag("tailItem");
		ItemStack stackTail = ItemStack.EMPTY;
		if(tailItem instanceof NBTTagCompound)
			stackTail = new ItemStack((NBTTagCompound) tailItem);
		setTailItem(stackTail);
	}

	//cumulative speed, health, strength, & stamina modifiers
	public float getSpeedMods() {
		float head = getHeadType().getSpeedModifier();
		float body = getBodyType().getSpeedModifier();
		float tail = getTailType().getSpeedModifier();
		return Math.round((getFishSize() * 0.5F) * head + body + tail * 16F) / 16F;
	}

	public float getHealthMods() {
		float head = getHeadType().getHealthModifier();
		float body = getBodyType().getHealthModifier();
		float tail = getTailType().getHealthModifier();
		return Math.round(getFishSize() * head + body + tail * 2F) / 2F;
	}

	public float getStrengthMods() {
		float head = getHeadType().getStrengthModifier();
		float body = getBodyType().getStrengthModifier();
		float tail = getTailType().getStrengthModifier();
		return Math.round((getFishSize() * 0.5F) * head + body + tail * 2F) / 2F;
	}

	public float getStaminaMods() {
		float head = getHeadType().getStaminaModifier();
		float body = getBodyType().getStaminaModifier();
		float tail = getTailType().getStaminaModifier();
		return Math.max(Math.round(getFishSize() * head + body + tail * 2F) / 2F, 3.5F);
	}

	public void playTreasureCollectedSound(EntityPlayer player) {
		getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.ANADIA_TREASURE_COLLECTED, SoundCategory.PLAYERS, 0.25F, 1F);
	}

	public void playAnadiaLostSound(EntityPlayer player) {
		getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.ANADIA_LOST, SoundCategory.PLAYERS, 0.25F, 1F);
	}

	public void playAnadiaWonSound(EntityPlayer player) {
		getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.ANADIA_WON, SoundCategory.PLAYERS, 0.25F, 1F);
	}

	public void playAnadiaCrab(EntityPlayer player) {
		getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.FISHING_CRAB, SoundCategory.PLAYERS, 0.25F, 1F);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return super.getHurtSound(source);
	}
	/*
    @Override
    protected SoundEvent getDeathSound() {
       return SoundRegistry.ANADIA_DEATH;
    }
	 */
	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.ANADIA;
	}

	@Override
	public boolean getCanSpawnHere() {
		return world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getBlock() == BlockRegistry.SWAMP_WATER;
	}

	public boolean isGrounded() {
		return !isInWater() && world.isAirBlock(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY + 1), MathHelper.floor(posZ))) && world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY - 1), MathHelper.floor(posZ))).getBlock().isCollidable();
	}

	@Override
	protected PathNavigate createNavigator(World world){
		return new PathNavigateSwimmer(this, world);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return world.getBlockState(pos).getMaterial() == Material.WATER ? 10.0F + world.getLightBrightness(pos) - 0.5F : super.getBlockPathWeight(pos);
	}

	@Override
	public void setDead() {
		if (!getEntityWorld().isRemote) {
			if (getAttackingEntity() instanceof EntityLurker) {
				EntityLurker lurker = (EntityLurker) getAttackingEntity();
				lurker.huntingTimerAnadia = 1200; // 1 minute cooldown
			}
		}
		super.setDead();
	}

	@Override
	public void onLivingUpdate() {
		if (getEntityWorld().isRemote) {
			/*	if (isInWater()) {
				Vec3d vec3d = getLook(0.0F);
				for (int i = 0; i < 2; ++i)
					getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX + (rand.nextDouble() - 0.5D) * (double) width - vec3d.x , posY + rand.nextDouble() * (double) height - vec3d.y , posZ + (rand.nextDouble() - 0.5D) * (double) width - vec3d.z, 0.0D, 0.0D, 0.0D, new int[0]);
			}*/
		}

		if (inWater) {
			setAir(300);
		} else if (onGround) {
			motionY += 0.5D;
			motionX += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
			motionZ += (double) ((rand.nextFloat() * 2.0F - 1.0F) * 0.4F);
			rotationYaw = rand.nextFloat() * 360.0F;
			if(isLeaping())
				setIsLeaping(false);
			onGround = false;
			isAirBorne = true;
			if(getEntityWorld().getTotalWorldTime()%5==0)
				getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_GUARDIAN_FLOP, SoundCategory.HOSTILE, 1F, 1F);
			damageEntity(DamageSource.DROWN, 0.5F);
		}

		super.onLivingUpdate();
	}

	@Override
	public void onUpdate() {
		if(getStaminaTicks() <= 0 && isBeingRidden() && getPassengers().get(0) instanceof EntityBLFishHook) {
			this.nettableTimer = MAX_NETTABLE_TIME;
		} else if(this.nettableTimer > 0) {
			this.nettableTimer--;
		}

		if (getEntityWorld().isRemote) {
			setSize(getFishSize(), getFishSize() * 0.75F);

			if(getEntityWorld().getTotalWorldTime()%4 == 0)
				animationFrame += 16;
			if(animationFrame > 48)
				animationFrame = 0;

			animationFrameCrab += 16;
			if(animationFrameCrab > 48)
				animationFrameCrab = 0;
		}

		if(!getEntityWorld().isRemote) {
			if(getAttackTarget() != null && !getEntityWorld().containsAnyLiquid(getAttackTarget().getEntityBoundingBox())) {
				double distance = getPosition().getDistance((int) getAttackTarget().posX, (int) getAttackTarget().posY, (int) getAttackTarget().posZ);
				if (distance > 1.0F && distance < 6.0F)
					if (isInWater() && getEntityWorld().isAirBlock(new BlockPos((int) posX, (int) posY + 1, (int) posZ))) 
						leapAtTarget(getAttackTarget().posX, getAttackTarget().posY, getAttackTarget().posZ);
			}

			//			if(getHungerCooldown() >= 0)
			//				setHungerCooldown(getHungerCooldown() - 1);

			//regains stamina over time whilst not hooked
			if(!isBeingRidden() ) {
				playAnadiaWonSound = true;

				if(getEscapeDelay() < (int) getStaminaMods() * 30)
					setEscapeDelay((int) (getStaminaMods() * 30));

				if(getStaminaTicks() < (int) (getStaminaMods() * 20))
					setStaminaTicks(getStaminaTicks() + 1);
				//if(getEscapeTicks() < 1024)
				//	setEscapeTicks(1024);
				if(getObstruction1Ticks() < 256)
					setObstruction1Ticks(256);
				if(getObstruction2Ticks() < 256)
					setObstruction2Ticks(256);
				if(getObstruction3Ticks() < 256)
					setObstruction3Ticks(256);
				if(getObstruction4Ticks() < 512)
					setObstruction4Ticks(512);
				if(getTreasureTicks() < 1024)
					setTreasureTicks(1024);
			}

			if (isBeingRidden() && getPassengers().get(0) instanceof EntityBLFishHook) {
				EntityBLFishHook hook = (EntityBLFishHook) getPassengers().get(0);

				if (getStaminaTicks() <= 0 && playAnadiaWonSound) {
					if (hook != null && hook.getAngler() != null)
						playAnadiaWonSound(hook.getAngler());
					playAnadiaWonSound = false;
					this.getNavigator().clearPath();
				}

				if (getStaminaTicks() > 0 && getEscapeDelay() > 0) {
					setEscapeDelay(getEscapeDelay() - 1);

					if (getEscapeDelay() == 10)
						if (hook != null && hook.getAngler() != null)
							playAnadiaCrab(hook.getAngler());
				}

				if (getEscapeTicks() > 0 && getEscapeDelay() <= 0)
					setEscapeTicks(getEscapeTicks() - 3);

				if (getEscapeTicks() * 256 / 1024 < getStaminaTicks() * 256 / 180 && getEscapeDelay() <= 0) {
					if (hook != null && hook.getAngler() != null) {
						playAnadiaLostSound(hook.getAngler());
						getPassengers().get(0).dismountRidingEntity(); // this just  releases the fish atm
					}
				}

				if (getObstruction1Ticks() >= 0) {
					setObstruction1Ticks(getObstruction1Ticks() - 1);
					if (getObstruction1Ticks() <= 0)
						setObstruction1Ticks(256);
				}

				if (getObstruction2Ticks() >= 0) {
					setObstruction2Ticks(getObstruction2Ticks() - 2);
					if (getObstruction2Ticks() <= 0)
						setObstruction2Ticks(256);
				}

				if (getObstruction3Ticks() >= 0) {
					setObstruction3Ticks(getObstruction3Ticks() - 1);
					if (getObstruction3Ticks() <= 0)
						setObstruction3Ticks(256);
				}

				if (getObstruction4Ticks() >= 0) {
					setObstruction4Ticks(getObstruction4Ticks() - 1);
					if (getObstruction4Ticks() <= 0)
						setObstruction4Ticks(512);
				}

				if (isTreasureFish() && getTreasureTicks() >= 0) {
					setTreasureTicks(getTreasureTicks() - 1);
					if (getTreasureTicks() <= 0) {
						if (getTreasureUnlocked())
							setIsTreasureFish(false);
						setTreasureTicks(1024);
					}
				}
			}
		}
		super.onUpdate();
	}

	public boolean isObstructed() {
		if(256 - getObstruction1Ticks() <= getStaminaTicks() * 256 / 180 && 256 - getObstruction1Ticks() >= getStaminaTicks() * 256 / 180 - 16 )
			return true;
		else if(256 - getObstruction2Ticks() <= getStaminaTicks() * 256 / 180  && 256 - getObstruction2Ticks() >= getStaminaTicks() * 256 / 180 - 16 )
			return true;
		else if(256 - getObstruction3Ticks() <= getStaminaTicks() * 256 / 180  && 256 - getObstruction3Ticks() >= getStaminaTicks() * 256 / 180 - 16 )
			return true;
		else if(512 - getObstruction4Ticks() <= getStaminaTicks() * 512 / 180  && 512 - getObstruction4Ticks() >= getStaminaTicks() * 512 / 180 - 16 )
			return true;
		else
			return false;
	}

	public boolean isObstructedTreasure() {
		return (1024 - getTreasureTicks() <= getStaminaTicks() * 1024 / 180 + 8 && 1024 - getTreasureTicks() >= getStaminaTicks() * 1024 / 180 - 16 );
	}

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof EntityBLFishHook) {
			double a = Math.toRadians(rotationYaw);
			double offSetX = -Math.sin(a) * width * 0.5D;
			double offSetZ = Math.cos(a) * width * 0.5D;
			entity.setPosition(posX + offSetX, posY + height + 0.125f, posZ + offSetZ);
			this.rotationPitch = -20.0f;
		}
	}

	public void leapAtTarget(double targetX, double targetY, double targetZ) {
		if(!isLeaping()) {
			setIsLeaping(true);
			getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_HOSTILE_SWIM, SoundCategory.NEUTRAL, 1F, 2F);
		}
		double distanceX = targetX - posX;
		double distanceZ = targetZ - posZ;
		float distanceSqrRoot = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ);
		motionX = distanceX / distanceSqrRoot * 0.1D + motionX * getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
		motionZ = distanceZ / distanceSqrRoot * 0.1D + motionZ * getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
		motionY = 0.3F;

	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if (isServerWorld()) {
			if (isInWater()) {
				moveRelative(strafe, up,  forward, 0.1F);
				move(MoverType.SELF, motionX, motionY, motionZ);
				motionX *= 0.75D;
				motionY *= 0.75D;
				motionZ *= 0.75D;

				if (getAttackTarget() == null) {
					motionY -= 0.003D;
				}
			} else {
				super.travel(strafe, up, forward);
			}
		} else {
			super.travel(strafe, up, forward);
		}
	}

	@Override
	public boolean isNotColliding() {
		return getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty();
	}

	@Override
	public boolean isPushedByWater() {
		return false;
	}

	static class AnadiaMoveHelper extends EntityMoveHelper {
		private final EntityAnadia anadia;

		public AnadiaMoveHelper(EntityAnadia anadia) {
			super(anadia);
			this.anadia = anadia;
		}

		@Override
		public void onUpdateMoveHelper() {
			if (anadia.getStaminaTicks() <= 0 || anadia.getNettableTimer() > 0) {
				action = EntityMoveHelper.Action.WAIT;
				anadia.setAIMoveSpeed(0);
				return;
			}

			if (action == EntityMoveHelper.Action.MOVE_TO && !anadia.getNavigator().noPath()) {
				double targetX = posX - anadia.posX;
				double targetY = posY - anadia.posY;
				double targetZ = posZ - anadia.posZ;
				double targetDistance = targetX * targetX + targetY * targetY + targetZ * targetZ;
				targetDistance = (double) MathHelper.sqrt(targetDistance);
				targetY = targetY / targetDistance;
				float targetAngle = (float) (MathHelper.atan2(targetZ, targetX) * (180D / Math.PI)) - 90.0F;
				anadia.rotationYaw = limitAngle(anadia.rotationYaw, targetAngle, 90.0F);
				anadia.renderYawOffset = anadia.rotationYaw;
				float travelSpeed = (float) (speed * anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
				anadia.setAIMoveSpeed(anadia.getAIMoveSpeed() + (travelSpeed - anadia.getAIMoveSpeed()) * 0.125F);
				double wiggleSpeed = Math.sin((double) (anadia.ticksExisted + anadia.getEntityId()) * 0.5D) * anadia.getFishSize()* 0.05D;
				double wiggleOffsetX = Math.cos((double) (anadia.rotationYaw * anadia.getFishSize() * 0.01F));
				double wiggleOffsetZ = Math.sin((double) (anadia.rotationYaw * anadia.getFishSize() * 0.01F));
				anadia.motionX += wiggleSpeed * wiggleOffsetX;
				anadia.motionZ += wiggleSpeed * wiggleOffsetZ;
				wiggleSpeed = Math.sin((double) (anadia.ticksExisted + anadia.getEntityId()) * 0.75D) * 0.05D;
				anadia.motionY += wiggleSpeed * (wiggleOffsetZ + wiggleOffsetX) * 0.25D;
				anadia.motionY += (double) anadia.getAIMoveSpeed() * targetY * 0.1D;
				EntityLookHelper entitylookhelper = anadia.getLookHelper();
				double targetDirectionX = anadia.posX + targetX / targetDistance * 2.0D;
				double targetDirectionY = (double) anadia.getEyeHeight() + anadia.posY + targetY / targetDistance;
				double targetDirectionZ = anadia.posZ + targetZ / targetDistance * 2.0D;
				double lookX = entitylookhelper.getLookPosX();
				double lookY = entitylookhelper.getLookPosY();
				double lookZ = entitylookhelper.getLookPosZ();

				if (!entitylookhelper.getIsLooking()) {
					lookX = targetDirectionX;
					lookY = targetDirectionY;
					lookZ = targetDirectionZ;
				}

				anadia.getLookHelper().setLookPosition(lookX + (targetDirectionX - lookX) * 0.125D, lookY + (targetDirectionY - lookY) * 0.125D, lookZ + (targetDirectionZ - lookZ) * 0.125D, 10.0F, 40.0F);
			} else {
				anadia.setAIMoveSpeed(0.0F);
			}
		}

	}

	public class AIFindBait extends EntityAIBase {

		private final EntityAnadia anadia;
		private double searchRange;
		public EntityFishBait bait = null;

		public AIFindBait(EntityAnadia anadiaIn, double searchRangeIn) {
			anadia = anadiaIn;
			searchRange = searchRangeIn;
		}

		@Override
		public boolean shouldExecute() {
			return bait == null;
			//return anadia.getHungerCooldown() <= 0 && bait == null;
		}

		@Override
		public void startExecuting() {
			if(bait == null)
				bait = getClosestBait(searchRange);
		}

		@Override
		public boolean shouldContinueExecuting() {
			return bait != null && !bait.isDead;
			//	return anadia.getHungerCooldown() <= 0 && bait != null && !bait.isDead;
		}

		@Override
		public void updateTask() {
			if (!anadia.world.isRemote && shouldContinueExecuting()) {

				if (bait != null) {
					float distance = bait.getDistance(anadia);
					double x = bait.posX;
					double y = bait.posY;
					double z = bait.posZ;
					if (bait.cannotPickup()) {
						if (distance >= 1F) {
							anadia.getLookHelper().setLookPosition(x, y, z, 20.0F, 8.0F);
							moveToItem(bait);
						}

						if (distance <= 2F)
							if (anadia.isInWater() && anadia.getEntityWorld().isAirBlock(new BlockPos(x, y + 1D, z)) && anadia.canEntityBeSeen(bait))
								anadia.leapAtTarget(x, y, z);

						if (distance <= 1F) {
							anadia.getMoveHelper().setMoveTo(x, y, z, anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
							//anadia.setHungerCooldown(bait.getBaitSaturation());
							bait.getItem().shrink(1);
							if (bait.getItem().getCount() <= 0)
								bait.setDead();
							anadia.setIsLeaping(false);
							resetTask();
						}
					}
				}
			}
		}

		@Override
		public void resetTask() {
			bait = null;
		}

		public EntityFishBait getClosestBait(double distance) {
			List<EntityFishBait> list = anadia.getEntityWorld().getEntitiesWithinAABB(EntityFishBait.class, anadia.getEntityBoundingBox().grow(distance, distance, distance));
			for (Iterator<EntityFishBait> iterator = list.iterator(); iterator.hasNext();) {
				EntityFishBait bait = iterator.next();
				if (bait.getAge() >= bait.lifespan || !bait.isInWater())
					iterator.remove();
			}
			if (list.isEmpty())
				return null;
			if (!list.isEmpty())
				Collections.shuffle(list);
			return list.get(0);
		}

		public void moveToItem(EntityFishBait bait) {
			Path pathentity = anadia.getNavigator().getPath();
			if (pathentity != null) {
				//entity.getNavigator().setPath(pathentity, 0.5D);
				anadia.getNavigator().tryMoveToXYZ(bait.posX, bait.posY, bait.posZ, anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			}
		}
	}


	public class AIFindHook extends EntityAIBase {

		private final EntityAnadia anadia;
		private double searchRange;
		public EntityBLFishHook hook = null;

		public AIFindHook(EntityAnadia anadiaIn, double searchRangeIn) {
			anadia = anadiaIn;
			searchRange = searchRangeIn;
		}

		@Override
		public boolean shouldExecute() {
			return hook == null;
			//   		return anadia.getHungerCooldown() <= 0 && hook == null;
		}

		@Override
		public void startExecuting() {
			if(hook == null)
				hook = getClosestHook(searchRange);
		}

		@Override
		public boolean shouldContinueExecuting() {
			return   hook != null && !hook.isDead &&  hook.getBaited() ? true : hook != null && !hook.isDead && (anadia.getEntityWorld().rand.nextInt(50) == 0 && !hook.getBaited());
			//    		return  anadia.getHungerCooldown() <= 0 && hook != null && !hook.isDead &&  hook.getBaited() ? true : anadia.getHungerCooldown() <= 0 && hook != null && !hook.isDead && (anadia.getEntityWorld().rand.nextInt(50) == 0 && !hook.getBaited());
		}

		@Override
		public void updateTask() {
			if (!anadia.world.isRemote && shouldContinueExecuting()) {

				if (hook != null && hook.caughtEntity == null) {
					float distance = hook.getDistance(anadia);
					double x = hook.posX;
					double y = hook.posY;
					double z = hook.posZ;

					if (distance >= 1F) {
						anadia.getLookHelper().setLookPosition(x, y, z, 20.0F, 8.0F);
						moveToEntity(hook);
					}

					if (distance <= 2F)
						if (anadia.isInWater() && anadia.getEntityWorld().isAirBlock(new BlockPos(x, y + 1D, z)) && anadia.canEntityBeSeen(hook))
							anadia.leapAtTarget(x, y, z);

					if (distance <= 1F) {
						anadia.getMoveHelper().setMoveTo(x, y, z, anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
						//	anadia.setHungerCooldown(600);
						anadia.setIsLeaping(false);
						hook.caughtEntity = anadia;
						anadia.randomiseObstructionOrder();
						hook.startRiding(anadia, true);
						hook.setBaited(false);
						//resetTask();
					}
				}
			}
		}

		@Override
		public void resetTask() {
			hook = null;
		}

		public EntityBLFishHook getClosestHook(double distance) {
			List<EntityBLFishHook> list = anadia.getEntityWorld().getEntitiesWithinAABB(EntityBLFishHook.class, anadia.getEntityBoundingBox().grow(distance, distance, distance));
			for (Iterator<EntityBLFishHook> iterator = list.iterator(); iterator.hasNext();) {
				EntityBLFishHook hook = iterator.next();
				if (!hook.isInWater())
					iterator.remove();
			}
			if (list.isEmpty())
				return null;
			if (!list.isEmpty())
				Collections.shuffle(list);
			return list.get(0);
		}

		public void moveToEntity(EntityBLFishHook hook) {
			Path pathentity = anadia.getNavigator().getPath();
			if (pathentity != null) {
				//entity.getNavigator().setPath(pathentity, 0.5D);
				anadia.getNavigator().tryMoveToXYZ(hook.posX, hook.posY, hook.posZ, anadia.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
			}
		}
	}

	public class EntityAIAFishCalledWander extends EntityAIWander {

		public EntityAIAFishCalledWander(EntityCreature creatureIn, double speedIn, int chance) {
			super(creatureIn, speedIn, chance);
		}

		@Override
		public boolean shouldExecute() {
			return super.shouldExecute() && !isBeingRidden();
		}

		@Override
		public boolean shouldContinueExecuting(){
			return !entity.getNavigator().noPath() && !isBeingRidden();
		}
	}

	public class EntityAIPanicWhenHooked extends EntityAIPanic {
		private final EntityAnadia anadia;

		public EntityAIPanicWhenHooked(EntityAnadia entity) {
			super(entity, entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 2D);
			anadia = entity;
		}

		@Override
		public boolean shouldExecute() {
			return anadia.isBeingRidden() && anadia.getStaminaTicks() >= 1 && findRandomPosition();
		}

		@Override
		public boolean shouldContinueExecuting(){
			return !anadia.getNavigator().noPath() && anadia.isBeingRidden() && anadia.getStaminaTicks() >= 1;
		}

		@Override
		public void startExecuting() {
			anadia.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
		}
	}

	public class EntityAIPanicWhenUnhooked extends EntityAIPanic {
		private final EntityAnadia anadia;
		private int timeSinceUnhook = 0;

		public EntityAIPanicWhenUnhooked(EntityAnadia entity) {
			super(entity, 2.0D);
			anadia = entity;
		}

		@Override
		public boolean shouldExecute() {
			if(anadia.isBeingRidden() && anadia.getStaminaTicks() > 0) {
				this.timeSinceUnhook = 0;
			} else if(anadia.isBeingRidden() && anadia.getStaminaTicks() <= 0) {
				this.timeSinceUnhook = 1;
			} else if(this.timeSinceUnhook == 1 && this.anadia.getNettableTimer() <= 0) {
				this.timeSinceUnhook = 2;
			} else if(this.timeSinceUnhook >= 2) {
				this.timeSinceUnhook += 2;

				if(anadia.isBeingRidden() || this.timeSinceUnhook >= 60) {
					this.timeSinceUnhook = 0;
					return false;
				}

				if(findRandomPosition()) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean shouldContinueExecuting() {
			this.timeSinceUnhook++;

			if(anadia.isBeingRidden() || this.timeSinceUnhook >= 60) {
				this.timeSinceUnhook = 0;
				return false;
			}

			return !anadia.getNavigator().noPath() && !anadia.isBeingRidden();
		}

		@Override
		public void startExecuting() {
			anadia.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
		}
	}

	// Made separate methods so we can maintain ordering if new parts are added rather than ordinal juggling
	public static enum EnumAnadiaHeadParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		HEAD_1(0.125F, 1F, 1F, 1F),
		HEAD_2(0.25F, 2F, 2F, 2F),
		HEAD_3(0.5F, 3F, 3F, 3F),
		
		UNKNOWN(0.25F, 1F, 1F, 1F);

		private static final EnumAnadiaHeadParts[] VALUES = values();
		
		float speed; // added to movement speed
		float health; // added to health
		float strength; // added to attack if aggressive, and/or rod damage per catch
		float stamina; // possible use for how much it pulls or time taken to catch once hooked

		EnumAnadiaHeadParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			speed = speedModifier;
			health = healthModifier;
			strength = strengthModifier;
			stamina = staminaModifier;
		}

		EnumAnadiaHeadParts() {
			this(0.0F, 0.0F, 0.0F, 0.0F); // just in case no modifiers need to be added
		}

		public float getSpeedModifier() {
			return speed;
		}

		public float getHealthModifier() {
			return health;
		}

		public float getStrengthModifier() {
			return strength;
		}

		public float getStaminaModifier() {
			return stamina;
		}
		
		public static EnumAnadiaHeadParts random(Random rng) {
			return VALUES[rng.nextInt(VALUES.length - 1)];
		}
		
		public static EnumAnadiaHeadParts get(int id) {
			if(id >= 0 && id < VALUES.length) {
				return VALUES[id];
			}
			return EnumAnadiaHeadParts.UNKNOWN;
		}
	}

	public static enum EnumAnadiaBodyParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		BODY_1(0.125F, 1F, 1F, 1F),
		BODY_2(0.25F, 2F, 2F, 2F),
		BODY_3(0.5F, 3F, 3F, 3F),
		
		UNKNOWN(0.25F, 1F, 1F, 1F);

		private static final EnumAnadiaBodyParts[] VALUES = values();

		float speed; // added to movement speed
		float health; // added to health
		float strength; // added to attack if aggressive, and/or rod damage per catch
		float stamina; // possible use for how much it pulls or time taken to catch once hooked

		EnumAnadiaBodyParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			speed = speedModifier;
			health = healthModifier;
			strength = strengthModifier;
			stamina = staminaModifier;
		}

		EnumAnadiaBodyParts() {
			this(0.0F, 0.0F, 0.0F, 0.0F); // just in case no modifiers need to be added
		}

		public float getSpeedModifier() {
			return speed;
		}

		public float getHealthModifier() {
			return health;
		}

		public float getStrengthModifier() {
			return strength;
		}

		public float getStaminaModifier() {
			return stamina;
		}
		
		public static EnumAnadiaBodyParts random(Random rng) {
			return VALUES[rng.nextInt(VALUES.length - 1)];
		}
		
		public static EnumAnadiaBodyParts get(int id) {
			if(id >= 0 && id < VALUES.length) {
				return VALUES[id];
			}
			return EnumAnadiaBodyParts.UNKNOWN;
		}
	}

	public static enum EnumAnadiaTailParts {
		// part (speedModifier, healthModifier, strengthModifier, stamina)
		TAIL_1(0.125F, 1F, 1F, 1F),
		TAIL_2(0.25F, 2F, 2F, 2F),
		TAIL_3(0.5F, 3F, 3F, 3F),
		
		UNKNOWN(0.25F, 1F, 1F, 1F);

		private static final EnumAnadiaTailParts[] VALUES = values();

		private float speed; // added to movement speed
		private float health; // added to health
		private float strength; // added to attack if aggressive, and/or rod damage per catch
		private float stamina; // possible use for how much it pulls or time taken to catch once hooked

		EnumAnadiaTailParts(float speedModifier, float healthModifier, float strengthModifier, float staminaModifier) {
			speed = speedModifier;
			health = healthModifier;
			strength = strengthModifier;
			stamina = staminaModifier;
		}

		EnumAnadiaTailParts() {
			this(0.0F, 0.0F, 0.0F, 0.0F); // just in case no modifiers need to be added
		}

		public float getSpeedModifier() {
			return speed;
		}

		public float getHealthModifier() {
			return health;
		}

		public float getStrengthModifier() {
			return strength;
		}

		public float getStaminaModifier() {
			return stamina;
		}
		
		public static EnumAnadiaTailParts random(Random rng) {
			return VALUES[rng.nextInt(VALUES.length - 1)];
		}
		
		public static EnumAnadiaTailParts get(int id) {
			if(id >= 0 && id < VALUES.length) {
				return VALUES[id];
			}
			return EnumAnadiaTailParts.UNKNOWN;
		}
	}

	public static enum EnumAnadiaColor {
		SMOKED(false), ROTTEN(false), BASE(true), SILVER(true), PURPLE(true), GREEN(true),
		
		UNKNOWN(false);

		private static final EnumAnadiaColor[] VALUES = values();
		
		private final boolean isAlive;
		
		private EnumAnadiaColor(boolean isAlive) {
			this.isAlive = isAlive;
		}
		
		public boolean isAlive() {
			return this.isAlive;
		}
		
		public static EnumAnadiaColor get(int id) {
			if(id >= 0 && id < VALUES.length) {
				return VALUES[id];
			}
			return EnumAnadiaColor.UNKNOWN;
		}
	}
}
