package thebetweenlands.common.entity.mobs;

import javax.annotation.Nullable;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySporeMinion extends EntityMob implements IEntityBL {
	private float jumpHeightOverride = -1;
	public int animation_1 = 0, prev_animation_1 = 0;
	public int animation_2 = 0, prev_animation_2 = 0;
	protected static final DataParameter<Boolean> IS_FALLING = EntityDataManager.<Boolean>createKey(EntitySporeMinion.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Integer> TYPE = EntityDataManager.<Integer>createKey(EntitySporeMinion.class, DataSerializers.VARINT);

	protected float prevFloatingRotationTicks = 0;
	protected float floatingRotationTicks = 0;

	public EntitySporeMinion(World world) {
		super(world);
		setSize(0.75F, 0.75F);
		stepHeight = 1.0F;
		this.experienceValue = 1;
		
		this.setPathPriority(PathNodeType.WATER, -1.0F);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_FALLING, false);
		dataManager.register(TYPE, rand.nextInt(3));
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 0.6D, false));;
		tasks.addTask(2, new EntityAIWander(this, 0.6D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(4, new EntityAILookIdle(this));
		targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		double attackBuff = 0D;
		double moveSpeedBuff = 0D;
		double healthBuff = 0D;
		switch (getType()) {
		case 0:
			attackBuff = 1.5D;
			moveSpeedBuff = 0.2D;
			break;
		case 1:
			healthBuff = 15D;
			break;
		case 2:
			moveSpeedBuff = -0.2D;
			healthBuff = 25D;
			break;
		}
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.5D + attackBuff);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.49D + moveSpeedBuff);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D + healthBuff);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
	}

	@Override
	public void onUpdate() {
		prev_animation_1 = animation_1;
		prev_animation_2 = animation_2;
		if (!getEntityWorld().isRemote) {
			if (!this.isInWater()) {
				boolean canSpin = (this.getRidingEntity() != null ? !this.getRidingEntity().onGround : !onGround) && !this.isInWeb && !this.isInWater() && !this.isInLava() && world.isAirBlock(getPosition().down());
				if (canSpin && (motionY < 0D || (this.getRidingEntity() != null && this.getRidingEntity().motionY < 0))) {
					if (!getIsFalling())
						setIsFalling(true);
					motionY *= 0.7D;
					if(this.getRidingEntity() != null) {
						this.getRidingEntity().motionY *= 0.7D;
						this.getRidingEntity().fallDistance = 0;
					}
				} else {
					if (!canSpin && getIsFalling()) {
						setIsFalling(false);
					}
				}
			}
		}

		this.prevFloatingRotationTicks = this.floatingRotationTicks;
		if(this.getIsFalling()) {
			this.floatingRotationTicks += 30;
			float wrap = MathHelper.wrapDegrees(this.floatingRotationTicks) - this.floatingRotationTicks;
			this.floatingRotationTicks +=wrap;
			this.prevFloatingRotationTicks += wrap;
		} else {
			this.floatingRotationTicks = 0;
		}

		if(ticksExisted >= 20) {
			if (animation_1 <= 10)
				animation_1++;
			if (animation_1 > 10) {
				prev_animation_1 = animation_1 = 10;
				if (animation_2 <= 10)
					animation_2++;
				if (animation_2 > 10)
					prev_animation_2 = animation_2 = 10;
			}
		}
		super.onUpdate();
	}

    public float smoothedAngle(float partialTicks) {
        return this.prevFloatingRotationTicks + (this.floatingRotationTicks - this.prevFloatingRotationTicks) * partialTicks;
    }

    private float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);

        if (f > maxIncrease)
        {
            f = maxIncrease;
        }

        if (f < -maxIncrease)
        {
            f = -maxIncrease;
        }

        return angle + f;
    }
    
	private void setIsFalling(boolean state) {
		dataManager.set(IS_FALLING, state);
	}

	public boolean getIsFalling() {
		return dataManager.get(IS_FALLING);
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 1;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SPORELING_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.SPORELING_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SPORELING_DEATH;
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return null;//LootTableRegistry.SPORELING;
	}
	
	public void setJumpHeightOverride(float jumpHeightOverride) {
		this.jumpHeightOverride = jumpHeightOverride;
	}
	
	@Override
	protected float getJumpUpwardsMotion() {
		if(this.jumpHeightOverride > 0) {
			float height = this.jumpHeightOverride;
			this.jumpHeightOverride = -1;
			return height;
		}
		return super.getJumpUpwardsMotion();
	}

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		if (!getEntityWorld().isRemote) {
		}
		return livingdata;
	}

	public void setType(int skinType) {
		dataManager.set(TYPE, skinType);
	}

	public int getType() {
		return dataManager.get(TYPE);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("type", getType());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setType(nbt.getInteger("type"));
	}
}
