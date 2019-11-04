package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIBLAvoidEntity;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class EntityLeech extends EntityMob implements IEntityBL {
	private static final int MAX_BLOOD_LEVEL = 5;

	private static final int TIME_TO_FLEE = 600;

	public int attackCountDown = 20;

	public int hungerCoolDown;

	private int drainage;

	public float moveProgress;

	public boolean firstTickCheck;

	public int fleeingTick;

	AnimationMathHelper mathSucking = new AnimationMathHelper();

	private EntityAINearestAttackableTarget<EntityLivingBase> aiAttackTarget;
	private EntityAIWander aiWander;
	private EntityAIAttackMelee aiAttackOnCollide;
	private EntityAIBLAvoidEntity aiAvoidHarmer;

	private static final DataParameter<Byte> BLOOD_CONSUMED = EntityDataManager.createKey(EntityLeech.class, DataSerializers.BYTE);

	public EntityLeech(World worldIn) {
		super(worldIn);
		setSize(0.7F, 0.3F);
		stepHeight = 0;
		moveProgress = 0;
		firstTickCheck = false;
		drainage = 0;
		setBloodConsumed(0);
	}


	@Override
	protected void initEntityAI() {
		this.aiAttackTarget = new EntityAINearestAttackableTarget<EntityLivingBase>(this, EntityLivingBase.class, true, true);
		this.aiWander = new EntityAIWander(this, 0.8D);
		this.aiAttackOnCollide = new EntityAIAttackMelee(this, 1.0D, false);
		this.aiAvoidHarmer = new EntityAIBLAvoidEntity(this, EntityPlayer.class, 6, 0.5D, 0.6D);

		tasks.addTask(0, aiAttackOnCollide);
		tasks.addTask(1, aiWander);
		tasks.addTask(2, new EntityAILookIdle(this));

		targetTasks.addTask(0, aiAttackTarget);
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(BLOOD_CONSUMED, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.LEECH_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundRegistry.LEECH_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.LEECH_DEATH;
	}

	public void onCollideWithEntity(EntityLivingBase entity) {
		if (!world.isRemote) {
			if (!entity.isBeingRidden() && getBloodConsumed() <= 0) {
				startRiding(entity);
				this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(entity));
				targetTasks.removeTask(aiAttackTarget);
				tasks.removeTask(aiAttackOnCollide);
			}
		}
	}

	@Override
	public void onUpdate() {
		if (!world.isRemote) {
			if (fleeingTick == 0 && getAttackTarget() != null && getDistance(getAttackTarget()) < 2) {
				onCollideWithEntity(getAttackTarget());
			}
			if (getRidingEntity() != null) {
				setRotation(getRidingEntity().rotationYaw, getRidingEntity().rotationPitch);
				if (getRidingEntity().isBurning() && getRidingEntity() instanceof EntityLivingBase) {
					PotionEffect stomachContents = new PotionEffect(MobEffects.POISON, 120 + getBloodConsumed() * 200 / MAX_BLOOD_LEVEL, 0);
					((EntityLivingBase) getRidingEntity()).addPotionEffect(stomachContents);
					setBloodConsumed(0);
					Entity mount = this.getRidingEntity();
					dismountEntity(mount);
					flee();
					dismountRidingEntity();
					this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(mount));
				}
			}
			if (getBloodConsumed() == MAX_BLOOD_LEVEL && getRidingEntity() != null) {
				Entity mount = this.getRidingEntity();
				dismountEntity(getRidingEntity());
				dismountRidingEntity();
				this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(mount));
			}
			if (fleeingTick > 0) {
				fleeingTick--;
				if (fleeingTick == 0) {
					stopFleeing();
				}
			}
		}

		if (!firstTickCheck) {
			dismountRidingEntity();
			firstTickCheck = true;
		}
		if (--hungerCoolDown == 0) {
			if (getBloodConsumed() > 0) {
				setBloodConsumed(getBloodConsumed() - 1);
			}
		}
		if (getRidingEntity() != null) {
			moveProgress = 1 + mathSucking.swing(1, 0.15F, false);
			if (rand.nextInt(10) == 0) {
				for (int i = 0; i < 8; i++) {
					world.spawnParticle(EnumParticleTypes.REDSTONE, posX + (rand.nextFloat() - rand.nextFloat()), posY + rand.nextFloat(), posZ + (rand.nextFloat() - rand.nextFloat()), 0, 0, 0);
				}
			}
		} else if (!world.isRemote) {
			moveProgress = 0F + mathSucking.swing(1, 0.15F, false);
		}

		if (getRidingEntity() != null && getRidingEntity() instanceof EntityLivingBase && getBloodConsumed() < MAX_BLOOD_LEVEL) {
			drainage++;
			if (drainage >= attackCountDown && this.deathTime == 0) {
				getRidingEntity().attackEntityFrom(DamageSource.causeMobDamage(this), (int)getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());
				drainage = 0;
				setBloodConsumed(getBloodConsumed() + 1);
			}
		}
		super.onUpdate();
	}

	private void flee() {
		fleeingTick = TIME_TO_FLEE;
		if(!this.world.isRemote) {
			aiAvoidHarmer.setTargetEntityClass(getRidingEntity().getClass());
			tasks.addTask(0, aiAvoidHarmer);
			targetTasks.removeTask(aiAttackTarget);
			tasks.removeTask(aiAttackOnCollide);
		}
	}

	private void stopFleeing() {
		if(!this.world.isRemote) {
			tasks.removeTask(aiAvoidHarmer);
			targetTasks.addTask(0, aiAttackTarget);
			tasks.addTask(0, aiAttackOnCollide);
		}
	}

	@Override
	public double getYOffset() {
		if (getRidingEntity() != null && getRidingEntity() instanceof EntityPlayer)
			return getRidingEntity().height -2.25F;
		else if (getRidingEntity() != null)
			return getRidingEntity().height * 0.5D - 1.25D;
		else
			return super.getYOffset();
	}

	@Override
    public boolean canRiderInteract() {
        return true;
    }

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return entity != EntityLeech.class && (entity == EntityPlayer.class || entity == EntityPlayerMP.class || entity == EntitySwampHag.class);
	}

	public int getBloodConsumed() {
		return dataManager.get(BLOOD_CONSUMED) & 0xFF;
	}

	public void setBloodConsumed(int amount) {
		hungerCoolDown = 500;
		dataManager.set(BLOOD_CONSUMED, Byte.valueOf((byte) amount));
		if (amount == 0 && getRidingEntity() == null && !this.world.isRemote) {
			targetTasks.addTask(0, aiAttackTarget);
			tasks.addTask(0, aiAttackOnCollide);
		}
	}

	@Override
	protected ResourceLocation getLootTable() {
		return LootTableRegistry.LEECH;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("bloodLevel", getBloodConsumed());
		nbttagcompound.setInteger("fleeingTick", fleeingTick);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		if(nbttagcompound.hasKey("bloodLevel")) {
			setBloodConsumed(nbttagcompound.getInteger("bloodLevel"));
		}
		if(nbttagcompound.hasKey("fleeingTick")) {
			fleeingTick = nbttagcompound.getInteger("fleeingTick");
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
}
