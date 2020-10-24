package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.common.entity.ai.EntityAIApproachItem;
import thebetweenlands.common.entity.ai.EntityAIPeatMummyCharge;
import thebetweenlands.common.entity.attributes.BooleanAttribute;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityPeatMummy extends EntityMob implements IEntityBL, IEntityScreenShake {
	public static final IAttribute SPAWN_LENGTH_ATTRIB = (new RangedAttribute(null, "bl.spawnLength", 100.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Spawning Length");
	public static final IAttribute SPAWN_OFFSET_ATTRIB = (new RangedAttribute(null, "bl.spawnOffset", 2.0D, -Integer.MAX_VALUE, Integer.MAX_VALUE)).setDescription("Spawning Y Offset");
	public static final IAttribute SPAWN_RANGE_ATTRIB = (new RangedAttribute(null, "bl.spawnRange", 8.0D, 0, Double.MAX_VALUE)).setDescription("Spawning Range");

	public static final IAttribute CHARGING_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.chargingCooldown", 160.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Cooldown");
	public static final IAttribute CHARGING_PREPARATION_SPEED_ATTRIB = (new RangedAttribute(null, "bl.chargingPreparationSpeed", 60.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Preparation Speed");
	public static final IAttribute CHARGING_TIME_ATTRIB = (new RangedAttribute(null, "bl.chargingTime", 320.0D, 0, Integer.MAX_VALUE)).setDescription("Charging Time");
	public static final IAttribute CHARGING_SPEED_ATTRIB = (new RangedAttribute(null, "bl.chargingSpeed", 0.55D, 0, Double.MAX_VALUE)).setDescription("Charging Movement Speed");
	public static final IAttribute CHARGING_DAMAGE_MULTIPLIER_ATTRIB = (new RangedAttribute(null, "bl.chargingDamageMultiplier", 1.65D, 0, Double.MAX_VALUE)).setDescription("Charging Damage Multiplier");

	public static final IAttribute CARRY_SHIMMERSTONE = (new BooleanAttribute(null, "bl.carryShimmerstone", false)).setDescription("Whether this Peat Mummy carries a Shimmerstone");
	public static final IAttribute IS_BOSS = (new BooleanAttribute(null, "bl.isDreadfulPeatMummyBoss", false)).setDescription("Whether this Peat Mummy was spawned by a Dreadful Peat Mummy");
	
	private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

	private static final int BREAK_COUNT = 5;

	public static final float BASE_SPEED = 0.2F;
	public static final float BASE_DAMAGE = 6.0F;

	private static final DataParameter<Integer> SPAWNING_TICKS = EntityDataManager.<Integer>createKey(EntityPeatMummy.class, DataSerializers.VARINT);

	private int chargingPreparation;
	private static final DataParameter<Byte> CHARGING_STATE = EntityDataManager.<Byte>createKey(EntityPeatMummy.class, DataSerializers.BYTE);

	//Scream timer is only used for the screen shake and is client side only.
	private int prevScreamTimer;
	private int screamTimer;
	private boolean screaming = false;

	//Adjust to length of screaming sound
	private static final int SCREAMING_TIMER_MAX = 50;

	private float prevSpawningOffset = 0.0F;
	private float prevSpawningProgress = 0.0F;

	private static final List<Block> SPAWN_BLOCKS = new ArrayList<Block>();
	static {
		SPAWN_BLOCKS.add(BlockRegistry.MUD);
		SPAWN_BLOCKS.add(BlockRegistry.PEAT);
	}

	private List<EntityAIBase> activeTargetTasks;
	private List<EntityAIBase> inactiveTargetTasks;

	public EntityPeatMummy(World world) {
		super(world);
		this.setSize(1.0F, 1.2F);
		this.setSpawningTicks(0);
	}

	@Override
	public void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIApproachItem(this, ItemRegistry.SHIMMER_STONE, 80, 64, 1.9F, 1.5F) {
			@Override
			protected double getNearSpeed() {
				if(EntityPeatMummy.this.isCharging()) {
					return 1.0F;
				} else {
					return super.getNearSpeed();
				}
			}
			@Override
			protected double getFarSpeed() {
				if(EntityPeatMummy.this.isCharging()) {
					return 1.0F;
				} else {
					return super.getFarSpeed();
				}
			}
			@Override
			protected void onPickup() {
				EntityPeatMummy entity = EntityPeatMummy.this;
				if(entity.isCharging()) {
					entity.stopCharging();
				}
				entity.setCarryShimmerstone(true);
			}
		});
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, true) {
			@Override
			protected double getAttackReachSqr(EntityLivingBase attackTarget) {
				return 0.8D + attackTarget.width;
			}
		});
		this.tasks.addTask(3, new EntityAIPeatMummyCharge(this));
		this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		this.tasks.addTask(6, new EntityAIWander(this, 1D));
		this.tasks.addTask(7, new EntityAILookIdle(this));

		this.activeTargetTasks = new ArrayList<EntityAIBase>();
		this.activeTargetTasks.add(new EntityAIHurtByTarget(this, false));
		this.activeTargetTasks.add(new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));

		this.inactiveTargetTasks = new ArrayList<EntityAIBase>();
		this.inactiveTargetTasks.add(new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false) {
			@Override
			protected double getTargetDistance() {
				return EntityPeatMummy.this.getEntityAttribute(SPAWN_RANGE_ATTRIB).getAttributeValue();
			}
		});
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(110.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

		this.getAttributeMap().registerAttribute(CARRY_SHIMMERSTONE);
		this.getAttributeMap().registerAttribute(IS_BOSS);
		this.getAttributeMap().registerAttribute(SPAWN_LENGTH_ATTRIB);
		this.getAttributeMap().registerAttribute(SPAWN_OFFSET_ATTRIB);
		this.getAttributeMap().registerAttribute(SPAWN_RANGE_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_COOLDOWN_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_PREPARATION_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_TIME_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_SPEED_ATTRIB);
		this.getAttributeMap().registerAttribute(CHARGING_DAMAGE_MULTIPLIER_ATTRIB);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		this.getDataManager().register(SPAWNING_TICKS, 0);
		this.getDataManager().register(CHARGING_STATE, (byte) 0);
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || this.getChargingState() == 1;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("spawningTicks", this.getSpawningTicks());
		nbt.setInteger("chargingPreparation", this.chargingPreparation);
		nbt.setByte("chargingState", this.getDataManager().get(CHARGING_STATE));

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("spawningTicks")) {
			this.setSpawningTicks(nbt.getInteger("spawningTicks"));
		}
		if(nbt.hasKey("chargingPreparation")) {
			this.chargingPreparation = nbt.getInteger("chargingPreparation");
		}
		if(nbt.hasKey("chargingState")) {
			this.getDataManager().set(CHARGING_STATE, nbt.getByte("chargingState"));
		}

		super.readEntityFromNBT(nbt);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onUpdate() {
		super.onUpdate();

		this.prevSpawningOffset = this.getSpawningOffset();
		this.prevSpawningProgress = this.getSpawningProgress();

		if(!this.world.isRemote) {
			if(this.shouldUpdateSpawningAnimation()) {
				if(this.getSpawningTicks() == 0) {
					this.playSound(SoundRegistry.PEAT_MUMMY_EMERGE, 1.2F, 1.0F);
				}
				this.updateSpawningTicks();
			} else if(this.getSpawningTicks() > 0) {
				this.setSpawningFinished();
			}

			if(this.isInValidSpawn() && !this.isSpawningFinished()) {
				this.motionY = 0;
				this.motionX = 0;
				this.motionZ = 0;
				this.velocityChanged = true;

				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningTicks() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = new BlockPos(this.posX, this.posY - 1, this.posZ);
					IBlockState blockState = this.world.getBlockState(pos);
					this.playSound(blockState.getBlock().getSoundType().getBreakSound(), this.rand.nextFloat() * 0.3F + 0.3F, this.rand.nextFloat() * 0.15F + 0.7F);
				}

				if(this.getAttackTarget() != null) {
					this.faceEntity(this.getAttackTarget(), 360, 360);
				}

				if(this.getSpawningTicks() == this.getSpawningLength() - 1) {
					this.setPosition(this.posX, this.posY, this.posZ);
				}
			} else {
				this.setSpawningFinished();
			}
		} else {
			if(this.getSpawningProgress() != 0.0F && this.getSpawningProgress() != 1.0F) {
				int breakPoint = getSpawningLength() / BREAK_COUNT;
				if ((getSpawningTicks() - breakPoint / 2 - 1) % breakPoint == 0) {
					BlockPos pos = new BlockPos(this.posX, this.posY - 1, this.posZ);
					IBlockState blockState = this.world.getBlockState(pos);
					double px = this.posX + this.rand.nextDouble() - 0.5F;
					double py = this.posY + this.rand.nextDouble() * 0.2 + 0.075;
					double pz = this.posZ + this.rand.nextDouble() - 0.5F;
					for (int i = 0, amount = this.rand.nextInt(20) + 15; i < amount; i++) {
						double ox = this.rand.nextDouble() * 0.1F - 0.05F;
						double oz = this.rand.nextDouble() * 0.1F - 0.05F;
						double motionX = this.rand.nextDouble() * 0.2 - 0.1;
						double motionY = this.rand.nextDouble() * 0.25 + 0.1;
						double motionZ = this.rand.nextDouble() * 0.2 - 0.1;
						this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, px + ox, py, pz + oz, motionX, motionY, motionZ, Block.getStateId(blockState));
					}
				}
			}
		}

		if(this.isSpawningFinished()) {
			this.prevScreamTimer = this.screamTimer;
			if(this.isPreparing() && this.screamTimer == 0) {
				this.screaming = true;
				this.screamTimer = 1;
			}
			if(this.screamTimer > 0) {
				this.screamTimer++;
			}
			if(this.screamTimer >= SCREAMING_TIMER_MAX || !this.isPreparing()) {
				this.screaming = false;
			} else {
				this.screaming = true;
			}
			if(!this.isPreparing()) {
				this.screamTimer = 0;
			}

			if(!this.world.isRemote) {
				if(this.isPreparing()){
					this.chargingPreparation++;
					if(this.getPreparationProgress() == 1.0F) {
						this.chargingPreparation = 0;
						this.setChargingState(2);
					}
				}

				if(this.isCharging()) {
					this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE * this.getEntityAttribute(CHARGING_DAMAGE_MULTIPLIER_ATTRIB).getAttributeValue());
					this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.getEntityAttribute(CHARGING_SPEED_ATTRIB).getAttributeValue());
				} else {
					this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE);
					this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BASE_SPEED);
				}
			}
		}
	}

	@Override
	public void updatePassenger(Entity entity) {
		super.updatePassenger(entity);
		if (entity instanceof EntitySwampHag)
			entity.setPosition(posX, posY + 0.5D + entity.getYOffset(), posZ);
	}

	/**
	 * Returns whether the spawning animation should be started
	 * @return
	 */
	public boolean shouldUpdateSpawningAnimation() {
		return this.getAttackTarget() != null;
	}

	/**
	 * Returns whether the ground below the peat mummy is suitable for spawning
	 * @return
	 */
	public boolean isInValidSpawn() {
		int ebx = MathHelper.floor(this.posX);
		int eby = MathHelper.floor(this.posY);
		int ebz = MathHelper.floor(this.posZ);
		return inMud(ebx, eby, ebz);
	}

	@SuppressWarnings("deprecation")
	private boolean inMud(int ebx, int eby, int ebz) {
		MutableBlockPos pos = new MutableBlockPos();
		for(int y = -MathHelper.ceil(this.getMaxSpawnOffset()); y < 0; y++) {
			for(int x = -1; x <= 1; x++) {
				for(int z = -1; z <= 1; z++) {
					IBlockState blockState = this.world.getBlockState(pos.setPos(ebx + x, eby + y, ebz + z));
					Block cb = blockState.getBlock();
					if(!(y == -1 ? (SPAWN_BLOCKS.contains(cb)) : (cb.isOpaqueCube(blockState) || SPAWN_BLOCKS.contains(cb)))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if(this.isSpawningFinished()) {
			super.onCollideWithPlayer(player);
		}
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed() && this.isSpawningFinished() && this.getChargingState() == 0;
	}


	@Override
	public int getMaxSpawnedInChunk() {
		return 2;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		return !source.equals(DamageSource.IN_WALL) && super.attackEntityFrom(source, damage);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere() && this.isInValidSpawn();
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if(this.isCharging()) {
			this.stopCharging();
		}
		if(this.isSpawningFinished()) {
			return super.attackEntityAsMob(entity);
		}
		return false;
	}

	@Override
	public void playLivingSound() {
		if(this.isSpawningFinished()) {
			super.playLivingSound();
		}
	}

	/**
	 * Starts the charging progress
	 */
	public void startCharging() {
		this.playSound(SoundRegistry.PEAT_MUMMY_CHARGE, 1.75F, (this.rand.nextFloat() * 0.4F + 0.8F) * 0.8F);
		this.setChargingState(1);
	}

	/**
	 * Stops the charging progress
	 */
	public void stopCharging() {
		this.setChargingState(0);
		this.chargingPreparation = 0;
	}

	/**
	 * Returns the spawning offset
	 * @return
	 */
	public float getSpawningOffset() {
		return (float) ((-this.getMaxSpawnOffset() + this.getSpawningProgress() * this.getMaxSpawnOffset()));
	}

	/**
	 * Returns the interpolated spawning offset
	 * @param partialTicks
	 * @return
	 */
	public float getInterpolatedSpawningOffset(float partialTicks) {
		return this.prevSpawningOffset + (this.getSpawningOffset() - this.prevSpawningOffset) * partialTicks;
	}

	/**
	 * Returns the maximum spawning offset
	 * @return
	 */
	public double getMaxSpawnOffset() {
		return this.getEntityAttribute(SPAWN_OFFSET_ATTRIB).getAttributeValue();
	}

	/**
	 * Sets the spawning ticks
	 * @param ticks
	 */
	public void setSpawningTicks(int ticks) {
		this.getDataManager().set(SPAWNING_TICKS, ticks);

		if(!this.world.isRemote) {
			if(this.isSpawningFinished()) {
				for(EntityAIBase task : this.inactiveTargetTasks) {
					this.targetTasks.removeTask(task);
				}
				for(int i = 0; i < this.activeTargetTasks.size(); i++) {
					this.targetTasks.addTask(i, this.activeTargetTasks.get(i));
				}
			} else {
				for(EntityAIBase task : this.activeTargetTasks) {
					this.targetTasks.removeTask(task);
				}
				for(int i = 0; i < this.inactiveTargetTasks.size(); i++) {
					this.targetTasks.addTask(i, this.inactiveTargetTasks.get(i));
				}
			}
		}
	}

	/**
	 * Returns the spawning ticks
	 * @return
	 */
	public int getSpawningTicks() {
		return this.getDataManager().get(SPAWNING_TICKS);
	}

	/**
	 * Returns the spawning animation duration
	 * @return
	 */
	public int getSpawningLength() {
		return (int) this.getEntityAttribute(SPAWN_LENGTH_ATTRIB).getAttributeValue();
	}

	/**
	 * Returns the range at which a buried peat mummy detects a player
	 * @return
	 */
	public double getSpawningRange() {
		return this.getEntityAttribute(SPAWN_RANGE_ATTRIB).getAttributeValue();
	}

	/**
	 * Returns the relative spawning progress
	 * @return
	 */
	public float getSpawningProgress() {
		if(this.getSpawningLength() == 0) {
			return 1.0F;
		}
		return 1.0F / this.getSpawningLength() * this.getSpawningTicks();
	}

	/**
	 * Returns the interpolated relative spawning progress
	 * @param partialTicks
	 * @return
	 */
	public float getInterpolatedSpawningProgress(float partialTicks) {
		return this.prevSpawningProgress + (this.getSpawningProgress() - this.prevSpawningProgress) * partialTicks;
	}

	/**
	 * Increments the spawning ticks
	 */
	public void updateSpawningTicks() {
		int spawningTicks = this.getSpawningTicks();
		if(spawningTicks < this.getSpawningLength()) {
			this.setSpawningTicks(spawningTicks + 1);
		}
	}

	/**
	 * Resets the spawning ticks
	 */
	public void resetSpawningState() {
		this.setSpawningTicks(0);
	}

	/**
	 * Sets the spawning ticks to finished
	 */
	public void setSpawningFinished() {
		if(this.isSpawningFinished()) 
			return;

		this.setSpawningTicks(this.getSpawningLength());
	}

	/**
	 * Returns whether the spawning progress has finished
	 * @return
	 */
	public boolean isSpawningFinished() {
		return this.getSpawningTicks() == this.getSpawningLength();
	}

	/**
	 * Returns the maximum charge cooldown in ticks
	 * @return
	 */
	public int getMaxChargingCooldown() {
		return (int) this.getEntityAttribute(CHARGING_COOLDOWN_ATTRIB).getAttributeValue();
	}

	/**
	 * Sets the charging state:
	 * - 0: Not charging
	 * - 1: Preparing
	 * - 2: Charging
	 * @param state
	 */
	public void setChargingState(int state) {
		this.getDataManager().set(CHARGING_STATE, (byte) state);
	}

	/**
	 * Returns the charging state
	 * @return
	 */
	public byte getChargingState() {
		return this.getDataManager().get(CHARGING_STATE);
	}

	/**
	 * Returns whether this entity is preparing for a charge attack
	 * @return
	 */
	public boolean isPreparing() {
		return this.getChargingState() == 1;
	}

	/**
	 * Returns whether this entity is charging
	 * @return
	 */
	public boolean isCharging() {
		return this.getChargingState() == 2;
	}

	/**
	 * Returns the relative charging prepartion progress
	 * @return
	 */
	public float getPreparationProgress() {
		return 1.0F / (int)this.getEntityAttribute(CHARGING_PREPARATION_SPEED_ATTRIB).getAttributeValue() * this.chargingPreparation;
	}

	/**
	 * Returns whether the mummy is screaming
	 * @return
	 */
	public boolean isScreaming() {
		return this.screaming;
	}

	/**
	 * Returns the relative screaming progress
	 * @param delta
	 * @return
	 */
	public float getScreamingProgress(float delta) {
		return 1.0F / SCREAMING_TIMER_MAX * (this.prevScreamTimer + (this.screamTimer - this.prevScreamTimer) * delta);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.PEAT_MUMMY_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.PEAT_MUMMY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.PEAT_MUMMY_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.PEAT_MUMMY;
	}

	/**
	 * Sets whether the peat mummy is carrying a shimmer stone
	 * @param shimmerStone
	 */
	public void setCarryShimmerstone(boolean shimmerStone) {
		this.getEntityAttribute(CARRY_SHIMMERSTONE).setBaseValue(shimmerStone ? 1 : 0);
	}
	
	/**
	 * Returns whether the Peat Mummy is holding a Shimmerstone
	 * @return
	 */
	public boolean doesCarryShimmerstone() {
		return this.getEntityAttribute(CARRY_SHIMMERSTONE).getBaseValue() > 0;
	}
	
	/**
	 * Sets whether the peat mummy was spawned by a Dreadful Peat Mummy
	 * @param shimmerStone
	 */
	public void setBossMummy(boolean boss) {
		this.getEntityAttribute(IS_BOSS).setBaseValue(boss ? 1 : 0);
	}
	
	/**
	 * Returns whether the Peat Mummy was spawned by a Dreadful Peat Mummy
	 * @return
	 */
	public boolean isBossMummy() {
		return this.getEntityAttribute(IS_BOSS).getBaseValue() > 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return Minecraft.getMinecraft().player.capabilities.isCreativeMode || this.getSpawningTicks() > 0 ? this.getEntityBoundingBox() : ZERO_AABB;
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(this.isScreaming()) {
			double dist = this.getDistance(viewer);
			float screamMult = (float) (1.0F - dist / 30.0F);
			if(dist >= 30.0F) {
				return 0.0F;
			}
			return (float) ((Math.sin(this.getScreamingProgress(partialTicks) * Math.PI) + 0.1F) * 0.15F * screamMult);
		} else {
			return 0.0F;
		}
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        if (!world.isRemote && this.world.rand.nextInt(20) == 0) {
            EntitySwampHag hag = new EntitySwampHag(this.world);
            hag.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            hag.onInitialSpawn(difficulty, (IEntityLivingData)null);
            this.world.spawnEntity(hag);
            hag.startRiding(this);
        }
		return livingdata;
    }
}