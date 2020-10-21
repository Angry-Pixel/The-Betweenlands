package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIHurtByTargetImproved;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class EntitySwampHag extends EntityMob implements IEntityBL {
	private static final DataParameter<Byte> TALK_SOUND = EntityDataManager.createKey(EntitySwampHag.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> SHOULD_JAW_MOVE = EntityDataManager.createKey(EntitySwampHag.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> LIVING_SOUND_TIMER = EntityDataManager.createKey(EntitySwampHag.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> IS_THROWING = EntityDataManager.createKey(EntitySwampHag.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Integer> THROW_TIMER = EntityDataManager.createKey(EntitySwampHag.class, DataSerializers.VARINT);
	
	public float jawFloat;
	public float breatheFloat;
	AnimationMathHelper animationTalk = new AnimationMathHelper();
	AnimationMathHelper animationBreathe = new AnimationMathHelper();
	private int animationTick;
	public boolean playPullSound;
	private int pathingCooldown = 0;
	
	public EntitySwampHag(World world) {
		super(world);
		this.setSize(0.6F, 1.8F);
	}

	@Override
	protected void initEntityAI() {
		((PathNavigateGround) this.getNavigator()).setBreakDoors(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		//Works as long as the material is wood
		this.tasks.addTask(1, new EntityAIBreakDoor(this));
		tasks.addTask(2, new EntitySwampHag.EntityAIThrowWorm(this));
		// this.tasks.addTask(2, new EntityAIBLBreakDoor(this, Blocks.iron_door, 20));
		this.tasks.addTask(3, new EntityAIAttackMelee(this, 1D, true));
		this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(5, new EntityAIWander(this, 0.75D));
		this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(7, new EntityAILookIdle(this));

		this.targetTasks.addTask(0, new EntityAIHurtByTargetImproved(this, true));
		this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityVillager>(this, EntityVillager.class, false));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(TALK_SOUND, (byte) 0);
		dataManager.register(SHOULD_JAW_MOVE, false);
		dataManager.register(LIVING_SOUND_TIMER, 0);
		dataManager.register(IS_THROWING, false);
		dataManager.register(THROW_TIMER, 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
	}

	@Override
	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		int randomSound = rand.nextInt(4) + 1;
		setTalkSound((byte) randomSound);
		switch (getTalkSound()) {
		case 1:
			return SoundRegistry.SWAMP_HAG_LIVING_1;
		case 2:
			return SoundRegistry.SWAMP_HAG_LIVING_2;
		case 3:
			return SoundRegistry.SWAMP_HAG_LIVING_3;
		default:
			return SoundRegistry.SWAMP_HAG_LIVING_4;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		setTalkSound(4);
		setShouldJawMove(true);
		return SoundRegistry.SWAMP_HAG_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		setTalkSound(4);
		setShouldJawMove(true);
		return SoundRegistry.SWAMP_HAG_DEATH;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		// WIP Temp
		if (!getEntityWorld().isRemote && isRidingMummy()) {
			if (getAttackTarget() != null) {
				if (getThrowTimer() < 90 && !getIsThrowing()) {
					setThrowTimer(Math.min(90, getThrowTimer() + 1));
				}
				if (getThrowTimer() >= 90 && getIsThrowing() && getThrowTimer() < 101) {
					setThrowTimer(Math.min(102, getThrowTimer() + 2));
				}
			} else {
				if (getThrowTimer() > 0 && !getIsThrowing()) {
					setThrowTimer(Math.max(0, getThrowTimer() - 1));
				}
			}

			if (getThrowTimer() <= 0)
				playPullSound = true;

			if (isRaisingArm())
				if (playPullSound) {
					getEntityWorld().playSound(null, getPosition(), SoundRegistry.SLINGSHOT_CHARGE, SoundCategory.HOSTILE, 1F, 1F);
					playPullSound = false;
				}
		}
	}

	public boolean isRidingMummy() {
		return isRiding() && getRidingEntity() instanceof EntityPeatMummy;
	}

	@Override
	public void onLivingUpdate() {
		breatheFloat = animationBreathe.swing(0.2F, 0.5F, false);
		
		if(this.getAttackTarget() != null && !this.getRecursivePassengersByType(EntityWight.class).isEmpty()) {
			if(this.pathingCooldown <= 0) {
				//No idea why the swamp hag doesn't want to move when possessed, this seems to work as hackish solution
				if(!this.navigator.tryMoveToEntityLiving(this.getAttackTarget(), 1)) {
					this.pathingCooldown = 20;
				}
			} else {
				this.pathingCooldown--;
			}
		}
		
		if (!world.isRemote) {
			updateLivingSoundTime();
		}

		if (animationTick > 0) {
			animationTick--;
		}

		if (animationTick == 0) {
			setShouldJawMove(false);
			jawFloat = animationTalk.swing(0F, 0F, true);
		}

		if (getLivingSoundTime() == -getTalkInterval())
			setShouldJawMove(true);

		if (!shouldJawMove())
			jawFloat = animationTalk.swing(0F, 0F, true);
		else if (shouldJawMove() && getTalkSound() != 3 && getTalkSound() != 4)
			jawFloat = animationTalk.swing(2.0F, 0.1F, false);
		else if (shouldJawMove() && getTalkSound() == 3 || shouldJawMove() && getTalkSound() == 4)
			jawFloat = animationTalk.swing(0.4F, 1.2F, false);
		super.onLivingUpdate();
	}

	private byte getTalkSound() {
		return dataManager.get(TALK_SOUND);
	}

	private void setTalkSound(int soundIndex) {
		dataManager.set(TALK_SOUND, (byte) soundIndex);
	}

	public void setShouldJawMove(boolean jawState) {
		dataManager.set(SHOULD_JAW_MOVE, jawState);
		if (jawState)
			animationTick = 20;
	}

	public boolean shouldJawMove() {
		return dataManager.get(SHOULD_JAW_MOVE);
	}

	private void updateLivingSoundTime() {
		dataManager.set(LIVING_SOUND_TIMER, livingSoundTime);
	}

	private int getLivingSoundTime() {
		return dataManager.get(LIVING_SOUND_TIMER);
	}

	public boolean getIsThrowing() {
		return dataManager.get(IS_THROWING);
	}

	public void setIsThrowing(boolean shooting) {
		dataManager.set(IS_THROWING, shooting);
	}
	
	public int getThrowTimer() {
		return dataManager.get(THROW_TIMER);
	}

	public void setThrowTimer(int timer) {
		dataManager.set(THROW_TIMER, timer);
	}

	public boolean isRaisingArm() {
		return dataManager.get(THROW_TIMER) > 0 && dataManager.get(THROW_TIMER) < 90 ;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.SWAMP_HAG;
	}
	
	@Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }

    @Override
    protected boolean isValidLightLevel() {
    	return true;
    }
    
    static class EntityAIThrowWorm extends EntityAIBase {
    	EntitySwampHag hag;
		EntityLivingBase target;
		
		public EntityAIThrowWorm(EntitySwampHag hag) {
			this.hag = hag;
			this.setMutexBits(5);
		}

		@Override
		public boolean shouldExecute() {
			target = hag.getAttackTarget();
			
			if (!hag.isRidingMummy())
				return false;
			if (target == null)
				return false;
			else {
				double distance = hag.getDistanceSq(target);
				if (distance >= 16.0D && distance <= 1024.0D && hag.getThrowTimer() >= 90 && hag.canEntityBeSeen(target)) {
						return true;
				} else
					return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting() {
			return target != null && hag.recentlyHit <= 40 && hag.getThrowTimer() >= 90 && hag.isRidingMummy() && hag.canEntityBeSeen(target);
		}

		@Override
		public void startExecuting() {
		}

		@Override
		public void updateTask() {
			if(!hag.getIsThrowing())
				hag.setIsThrowing(true);
			if(target != null) {
				hag.faceEntity(target, 30F, 30F);
				hag.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
				if (hag.getThrowTimer() == 90) {
					double targetX = target.posX - hag.posX;
					double targetY = target.getEntityBoundingBox().minY + (double) (target.height / 2.0F) - (hag.posY + (double) (hag.height / 2.0F));
					double targetZ = target.posZ - hag.posZ;
					double targetDistance = (double) MathHelper.sqrt(targetX * targetX + targetZ * targetZ);
					EntityTinySludgeWorm worm = new EntityTinySludgeWorm(hag.getEntityWorld());
					worm.setPosition(hag.posX, hag.posY + (double) hag.getEyeHeight() - 0.10000000149011612D, hag.posZ);
					throwWorm(worm, targetX, targetY + targetDistance * 0.2D, targetZ, 1.6F, 0F);
					hag.getEntityWorld().spawnEntity(worm);
					hag.getEntityWorld().playSound(null, hag.getPosition(), SoundRegistry.SLINGSHOT_SHOOT, SoundCategory.HOSTILE, 1F, 1F + (hag.getEntityWorld().rand.nextFloat() - hag.getEntityWorld().rand.nextFloat()) * 0.8F);
					hag.playPullSound = true;
				}
			}
			if (hag.getThrowTimer() == 102) {
				if (hag.getIsThrowing()) {
					hag.setIsThrowing(false);
					hag.setThrowTimer(0);
					hag.playPullSound = true;
				}
				resetTask();
			}
		}

		@Override
	    public void resetTask() {
	        target = null;
	    }

		public void throwWorm(EntityTinySludgeWorm entity, double x, double y, double z, float velocity, float inaccuracy) {
			float f = MathHelper.sqrt(x * x + y * y + z * z);
			x = x / (double) f;
			y = y / (double) f;
			z = z / (double) f;
			x = x + entity.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
			y = y + entity.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
			z = z + entity.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
			x = x * (double) velocity;
			y = y * (double) velocity;
			z = z * (double) velocity;
			entity.motionX = x;
			entity.motionY = y;
			entity.motionZ = z;
			float f1 = MathHelper.sqrt(x * x + z * z);
			entity.rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
			entity.rotationPitch = (float) (MathHelper.atan2(y, (double) f1) * (180D / Math.PI));
			entity.prevRotationYaw = entity.rotationYaw;
			entity.prevRotationPitch = entity.rotationPitch;
		}
	}
}
