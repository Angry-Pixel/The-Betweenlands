package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
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
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
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
	
	public float jawFloat;
	public float breatheFloat;
	AnimationMathHelper animationTalk = new AnimationMathHelper();
	AnimationMathHelper animationBreathe = new AnimationMathHelper();
	private int animationTick;

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
}
