package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

//TODO Loot tables
public class EntityCryptCrawler extends EntityMob implements IEntityBL {
	protected static final int MUTEX_BLOCKING  = 0b01000;
	protected static final int MUTEX_ATTACKING = 0b10000;
	
	private static final byte EVENT_SHIELD_BLOCKED = 80;
	
	private static final DataParameter<Boolean> IS_BIPED = EntityDataManager.createKey(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_STANDING = EntityDataManager.createKey(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_CHIEF = EntityDataManager.createKey(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_BLOCKING = EntityDataManager.<Boolean>createKey(EntityCryptCrawler.class, DataSerializers.BOOLEAN);
	
	public float standingAngle, prevStandingAngle;

	protected boolean recentlyBlockedAttack;
	
	public EntityCryptCrawler(World world) {
		super(world);
		setSize(0.5F, 1.0F); //Width must be < 1 otherwise path finder will not path through one block wide gaps!!
		stepHeight = 1F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_STANDING, false);
		dataManager.register(IS_BIPED, false);
		dataManager.register(IS_CHIEF, false);
		dataManager.register(IS_BLOCKING, false);
	}

	public boolean isStanding() {
		return dataManager.get(IS_STANDING);
	}

	private void setIsStanding(boolean standing) {
		dataManager.set(IS_STANDING, standing);
	}

	public boolean isBiped() {
		return dataManager.get(IS_BIPED);
	}

	public void setIsBiped(boolean standing) {
		dataManager.set(IS_BIPED, standing);
	}

	public boolean isChief() {
		return dataManager.get(IS_CHIEF);
	}

	public void setIsChief(boolean chief) {
		dataManager.set(IS_CHIEF, chief);
	}

	public boolean isBlocking() {
		return dataManager.get(IS_BLOCKING);
	}

	private void setIsBlocking(boolean blocking) {
		dataManager.set(IS_BLOCKING, blocking);
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(1, new EntityAISwimming(this));
		tasks.addTask(2, new EntityCryptCrawler.AIShieldCharge(this)); //Shield charge AI interrupts shield block AI and attack AI
		tasks.addTask(3, new EntityCryptCrawler.AIShieldBlock(this));
		tasks.addTask(4, new EntityCryptCrawler.AICryptCrawlerAttack(this));
		tasks.addTask(5, new EntityAIWander(this, 1D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
		
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 3, true, true, null).setUnseenMemoryTicks(120));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		updateAttributes();
	}

	protected void updateAttributes() {
		if (getEntityWorld() != null && !getEntityWorld().isRemote) {
			if (isChief()) {
				setSize(0.98F, 1.9F);
				experienceValue = 20;
				getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
				getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100D);
				getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.25D);
				getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
				getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.75D);
			}
			if (!isChief() && isBiped()) {
				setSize(0.75F, 1.5F);
				experienceValue = 10;
				getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
				getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30D);
				getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.5D);
				getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
				getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
			}
			if (!isChief() && !isBiped()) {
				setSize(0.95F, 1F);
				experienceValue = 5;
				getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.31D);
				getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
				getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.75D);
				getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
				getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.25D);
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.CRYPT_CRAWLER_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.CRYPT_CRAWLER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.CRYPT_CRAWLER_DEATH;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.CRYPT_CRAWLER;
	}

	@Override
	protected float getSoundPitch() {
		if(isChief())
			return super.getSoundPitch() * 0.5F;
		return super.getSoundPitch();
	}
	
	@Override
	public void onLivingUpdate() {
		if(this.isBlocking() && !this.getHeldItemOffhand().isEmpty() && this.getHeldItemOffhand().getItem().isShield(this.getHeldItemOffhand(), this)) {
			this.setActiveHand(EnumHand.OFF_HAND);
			
			//"Fix" for janky item pose
			if(this.world.isRemote) {
				this.activeItemStack = this.getHeldItemOffhand();
			}
		} else if(this.isHandActive() && this.getActiveHand() == EnumHand.OFF_HAND) {
			this.stopActiveHand();
		}
		
		if (getEntityWorld().isRemote) {
			if (isChief())
				setSize(1.2F, 1.9F);
			if (!isChief() && isBiped())
				setSize(0.75F, 1.5F);
			if (!isChief() && !isBiped())
				setSize(0.95F - standingAngle * 0.2F, 1F + standingAngle * 0.75F);
		}

		if (!getEntityWorld().isRemote && !isBiped()) {
			if (getAttackTarget() != null) {
				double distance = getDistance(getAttackTarget().posX, getAttackTarget().getEntityBoundingBox().minY, getAttackTarget().posZ);

				setIsStanding(distance <= 3.0D);
			}
	
			if (getAttackTarget() == null) {
				setIsStanding(false);
			}
		}

		if (getEntityWorld().isRemote && !isBiped()) {
			prevStandingAngle = standingAngle;

			if (standingAngle > 0 && !isStanding()) {
				standingAngle -= 0.1F;
			}

			if (isStanding() && standingAngle <= 1F) {
				standingAngle += 0.1F;
			}
			
			if (standingAngle < 0 && !isStanding()) {
				standingAngle = 0F;
			}

			if (isStanding() && standingAngle > 1F) {
				standingAngle = 1F;
			}
			
			standingAngle = MathHelper.clamp(standingAngle, 0, 1);
		}

		super.onLivingUpdate();
	}

    @SideOnly(Side.CLIENT)
    public float smoothedStandingAngle(float partialTicks) {
        return prevStandingAngle + (standingAngle - prevStandingAngle) * partialTicks;
    }

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return EntityCryptCrawler.class != entity;
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
    public boolean isNotColliding() {
        return !getEntityWorld().containsAnyLiquid(getEntityBoundingBox()) && getEntityWorld().getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && getEntityWorld().checkNoEntityCollision(getEntityBoundingBox(), this);
    }

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if(!this.isBlocking() && this.canEntityBeSeen(entity)) {
			boolean hasHitTarget = entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float) ((int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

			if (hasHitTarget) {
				if(!getHeldItemMainhand().isEmpty())
					getHeldItemMainhand().getItem().hitEntity(getHeldItemMainhand(), (EntityLivingBase) entity, this);
				
				//entity.addVelocity(-MathHelper.sin(rotationYaw * 3.141593F / 180.0F) * 0.5F, 0.2D, MathHelper.cos(rotationYaw * 3.141593F / 180.0F) * 0.5F);
				
				if (!getEntityWorld().isRemote)
					getEntityWorld().playSound((EntityPlayer) null, posX, posY, posZ, SoundRegistry.CRYPT_CRAWLER_LIVING, SoundCategory.HOSTILE, 1F, 0.5F);
			}
			return hasHitTarget;
		}
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (ticksExisted < 40 && source == DamageSource.IN_WALL) {
			return false;
		}
		
		boolean wasAttackBlocked = super.attackEntityFrom(source, amount);
		
		if(this.isBlocking() && !wasAttackBlocked) {
			recentlyBlockedAttack = true;
			
			//Play shield block sound to all listeners. For some reason shield block sound from item doesn't seem to work.
			this.world.setEntityState(this, EVENT_SHIELD_BLOCKED);
		}
		
		return wasAttackBlocked;
	}
	
	@Override
	public void handleStatusUpdate(byte id) {
		super.handleStatusUpdate(id);
		
		if(id == EVENT_SHIELD_BLOCKED) {
			this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.NEUTRAL, 1.0F, 0.8F + this.world.rand.nextFloat() * 0.4F, false);
		}
	}
	
	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (rand.nextInt(3) == 0) { // TODO re-enable for both types
			setIsBiped(true);
			if (rand.nextInt(3) == 0)
				setIsChief(true);
		}

		if (isBiped()) {
			if (rand.nextFloat() < 0.05F)
				setLeftHanded(true);
			else
				setLeftHanded(false);
			setRandomEquipment();
		}
		updateAttributes();
		setHealth(getMaxHealth());
		return livingdata;
	}

	protected void setRandomEquipment() {
		if (!isChief()) {
			int randomWeapon = rand.nextInt(5);
			int randomShield = rand.nextInt(3);

			switch (randomWeapon) {
			case 0:
				setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.WIGHTS_BANE));
				break;
			case 1:
				setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.WEEDWOOD_SWORD));
				break;
			case 2:
				setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.BONE_SWORD));
				break;
			case 3:
				setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.BONE_AXE));
				break;
			case 4:
				setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.WEEDWOOD_AXE));
				break;
			}

			if (!getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).isEmpty()) {
				switch (randomShield) {
				case 0:
					setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
					break;
				case 1:
					setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ItemRegistry.WEEDWOOD_SHIELD));
					break;
				case 2:
					setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ItemRegistry.BONE_SHIELD));
					break;
				}
			}
		}
		else {
			setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ItemRegistry.SYRMORITE_SHIELD));
			setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.OCTINE_SWORD));
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("is_biped", isBiped());
		nbt.setBoolean("is_chief", isChief());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setIsBiped(nbt.getBoolean("is_biped"));
		setIsChief(nbt.getBoolean("is_chief"));
	}

	@Override
	public float getAIMoveSpeed() {
		//Half move speed when blocking
		return (this.isBlocking() ? 0.5F : 1.0F) * super.getAIMoveSpeed();
	}
	
	static class AICryptCrawlerAttack extends EntityAIAttackMelee {
		public AICryptCrawlerAttack(EntityCryptCrawler crypt_crawler) {
			super(crypt_crawler, 1.2D, false);
			this.setMutexBits(this.getMutexBits() | MUTEX_ATTACKING);
		}

		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget) {
			return (double) (3.0F + attackTarget.width);
		}
	}
	
	static class AIShieldCharge extends EntityAIBase {
		private EntityCryptCrawler crawler;
		
		private int chargeCooldown;
		private int chargeCooldownMax;
		
		private int chargeTimer = 0;
		
		public AIShieldCharge(EntityCryptCrawler crawler) {
			this.crawler = crawler;
			setMutexBits(3 | MUTEX_BLOCKING | MUTEX_ATTACKING);
		}
		
		protected boolean isHoldingShield() {
			if(crawler != null) {
				ItemStack heldItem = crawler.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
				if (heldItem.isEmpty()) {
					return false;
				} else if(!heldItem.getItem().isShield(heldItem, this.crawler)) {
					return false;
				}
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public boolean shouldExecute() {
			if(!crawler.isChief()) {
				return false;
			}
			
			ItemStack heldItem = crawler.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
			if (heldItem.isEmpty()) {
				return false;
			} else if(!heldItem.getItem().isShield(heldItem, this.crawler)) {
				return false;
			}
			
			if(crawler.getAttackTarget() != null) {
				if(chargeCooldownMax < 0) {
					chargeCooldownMax = 80 + crawler.rand.nextInt(90);
				}
				
				chargeCooldown++;
				
				return chargeCooldown > chargeCooldownMax;
			}
			
			return false;
		}
		
		@Override
		public void startExecuting() {
			this.chargeCooldown = 0;
			this.chargeCooldownMax = -1;
		}
		
		@Override
		public boolean shouldContinueExecuting() {
			return this.isHoldingShield() && this.chargeTimer < 60;
		}
		
		@Override
		public void resetTask() {
			crawler.setIsBlocking(false);
			this.chargeTimer = 0;
		}
		
		@Override
		public void updateTask() {
			this.chargeTimer++;
			
			if(!crawler.isBlocking()) {
				crawler.setIsBlocking(true);
			}
			
			if(this.chargeTimer < 20) {
				crawler.setSneaking(true);
			} else {
				crawler.setSneaking(false);
			}
			
			EntityLivingBase target = this.crawler.getAttackTarget();
			if(target != null) {
				this.crawler.faceEntity(target, 15, 15);
			}
		}
	}

	static class AIShieldBlock extends EntityAIBase {
		private EntityCryptCrawler crawler;
		private EntityLivingBase target;
		private int blockingCount;
		private int blockingCountMax;
		private int meleeBlockingCounter;
		private int meleeBlockingCounterMax;
		
		private int blockingCooldownCounter;
		private int blockingCooldownCounterMax = -1;
		
		public AIShieldBlock(EntityCryptCrawler crawler) {
			this.crawler = crawler;
			setMutexBits(MUTEX_BLOCKING);
		}

		protected boolean isInMeleeRange() {
			return crawler != null && target != null && crawler.getDistanceSq(target) < 9.0D;
		}
		
		protected boolean isHoldingShield() {
			if(crawler != null) {
				ItemStack heldItem = crawler.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
				if (heldItem.isEmpty()) {
					return false;
				} else if(!heldItem.getItem().isShield(heldItem, this.crawler)) {
					return false;
				}
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public boolean shouldExecute() {
			if(!this.isHoldingShield()) {
				return false;
			}

			target = crawler.getAttackTarget();

			if(target != null && !crawler.isSwingInProgress && crawler.hurtResistantTime <= Math.max(0, crawler.maxHurtResistantTime - 5)) {
				blockingCooldownCounter++;
				
				if(blockingCooldownCounterMax < 0) {
					blockingCooldownCounterMax = 4 + crawler.rand.nextInt(5);
				}
				
				return blockingCooldownCounter > blockingCooldownCounterMax;
			}
			
			return false;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.isHoldingShield() && !crawler.recentlyBlockedAttack && blockingCount != -1 && crawler.hurtResistantTime <= Math.max(0, crawler.maxHurtResistantTime - 5) && !crawler.isSwingInProgress &&
					!(blockingCount > blockingCountMax || (meleeBlockingCounterMax >= 0 && meleeBlockingCounter > meleeBlockingCounterMax));
		}

		@Override
		public void startExecuting() {
			blockingCount = 0;
			blockingCountMax = 40 + crawler.rand.nextInt(40);
			meleeBlockingCounterMax = -1;
			meleeBlockingCounter = 0;
			
			//Reset the recently blocked state
			crawler.recentlyBlockedAttack = false;
			
			blockingCooldownCounter = 0;
			blockingCooldownCounterMax = -1;
		}
		
		@Override
		public void resetTask() {
			crawler.setIsBlocking(false);
			blockingCount = -1;
			meleeBlockingCounterMax = -1;
			meleeBlockingCounter = 0;
		}

		@Override
	    public void updateTask() {
			if(!crawler.isBlocking()) {
				crawler.setIsBlocking(true);
			}
			
			this.blockingCount++;
			
			if(this.isInMeleeRange()) {
				if(meleeBlockingCounterMax < 0) {
					meleeBlockingCounterMax = 10 + crawler.rand.nextInt(20);
				}
				meleeBlockingCounter++;
			} else {
				meleeBlockingCounterMax = -1;
				meleeBlockingCounter = 0;
			}
	    }
	}
}