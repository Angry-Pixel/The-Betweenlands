package thebetweenlands.entities.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.entities.entityAI.EntityAIBLAvoidEntity;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.ManualManager;
import thebetweenlands.utils.AnimationMathHelper;

public class EntityLeech extends EntityMob implements IEntityBL {
	private static final int MAX_BLOOD_LEVEL = 5;

	private static final int TIME_TO_FLEE = 600;

	private EntityLivingBase target;

	public int attackCountDown = 60;

	public int hungerCoolDown;

	private int drainage;

	public float moveProgress;

	public boolean firstTickCheck;

	AnimationMathHelper mathSucking = new AnimationMathHelper();

	private final EntityAINearestAttackableTarget aiAttackTarget = new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true);

	private final EntityAIWander aiWander = new EntityAIWander(this, 0.5);

	private final EntityAIAttackOnCollide aiAttackOnCollide = new EntityAIAttackOnCollide(this, EntityLivingBase.class, 0.5, false);

	private final EntityAIBLAvoidEntity avoidHarmer = new EntityAIBLAvoidEntity(this, EntityPlayer.class, 6, 0.5, 0.6);

	public EntityLeech(World world) {
		super(world);
		setSize(0.5F, 0.25F);
		stepHeight = 0;
		moveProgress = 0;
		firstTickCheck = false;
		drainage = 0;
		setBloodConsumed(0);
		tasks.addTask(0, aiAttackOnCollide);
		tasks.addTask(1, aiWander);
		tasks.addTask(2, new EntityAILookIdle(this));
		targetTasks.addTask(0, aiAttackTarget);
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(15, (byte) 0);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0.5);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected String getLivingSound() {
		return "thebetweenlands:snailLiving1";
	}

	@Override
	protected String getHurtSound() {
		return "thebetweenlands:snailHurt";
	}

	@Override
	protected String getDeathSound() {
		return "thebetweenlands:snailDeath";
	}

	public void onCollideWithEntity(EntityLivingBase entity) {
		if (!worldObj.isRemote) {
			if (entity.riddenByEntity == null && getBloodConsumed() <= 0) {
				mountEntity(entity);
				targetTasks.removeTask(aiAttackTarget);
				tasks.removeTask(aiAttackOnCollide);
			}
		}
	}

	@Override
	public void onUpdate() {
		if (!worldObj.isRemote) {
			if (fleeingTick == 0 && getAttackTarget() != null && getDistanceToEntity(getAttackTarget()) < 2) {
				onCollideWithEntity(getAttackTarget());
			}
			if (ridingEntity != null) {
				setRotation(ridingEntity.rotationYaw, ridingEntity.rotationPitch);
				if (ridingEntity.isBurning() && ridingEntity instanceof EntityLivingBase) {
					PotionEffect stomachContents = new PotionEffect(Potion.poison.id, 120 + getBloodConsumed() * 200 / MAX_BLOOD_LEVEL, 0);
					((EntityLivingBase) ridingEntity).addPotionEffect(stomachContents);
					setBloodConsumed(0);
					dismountEntity(ridingEntity);
					flee();
					ridingEntity = null;
				}
			}
			if (getBloodConsumed() == MAX_BLOOD_LEVEL && ridingEntity != null) {
				dismountEntity(ridingEntity);
				ridingEntity = null;
			}
			if (fleeingTick > 0) {
				fleeingTick--;
				if (fleeingTick == 0) {
					stopFleeing();
				}
			}
		}

		if (!firstTickCheck) {
			mountEntity(null);
			ridingEntity = null;
			firstTickCheck = true;
		}
		if (--hungerCoolDown == 0) {
			if (getBloodConsumed() > 0) {
				setBloodConsumed(getBloodConsumed() - 1);
			}
		}
		if (ridingEntity != null) {
			moveProgress = 1 + mathSucking.swing(1, 0.15F, false);
			if (rand.nextInt(10) == 0) {
				for (int i = 0; i < 8; i++) {
					worldObj.spawnParticle("reddust", posX + (rand.nextFloat() - rand.nextFloat()), posY + rand.nextFloat(), posZ + (rand.nextFloat() - rand.nextFloat()), 0, 0, 0);
				}
			}
		} else if (!worldObj.isRemote) {
			moveProgress = 0F + mathSucking.swing(1, 0.15F, false);
		}

		if (ridingEntity != null && ridingEntity instanceof EntityLivingBase && getBloodConsumed() < MAX_BLOOD_LEVEL) {
			drainage++;
			if (drainage >= attackCountDown) {
				ridingEntity.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
				drainage = 0;
				setBloodConsumed(getBloodConsumed() + 1);
			}
		}
		super.onUpdate();
	}

	private void flee() {
		fleeingTick = TIME_TO_FLEE;
		tasks.addTask(0, avoidHarmer);
		avoidHarmer.setTargetEntityClass(ridingEntity.getClass());
		targetTasks.removeTask(aiAttackTarget);
		tasks.removeTask(aiAttackOnCollide);
	}

	private void stopFleeing() {
		tasks.removeTask(avoidHarmer);
		targetTasks.addTask(0, aiAttackTarget);
		tasks.addTask(0, aiAttackOnCollide);
	}

	@Override
	public double getYOffset() {
		if (ridingEntity != null && ridingEntity instanceof EntityPlayer) {
			return -2;
		}
		return yOffset;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.inWall) || source.equals(DamageSource.drown)) {
			return false;
		}
		return super.attackEntityFrom(source, damage);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return entity != EntityLeech.class && (entity == EntityPlayer.class || entity == EntityPlayerMP.class || entity == EntitySwampHag.class);
	}

	public int getBloodConsumed() {
		return dataWatcher.getWatchableObjectByte(15);
	}

	public void setBloodConsumed(int amount) {
		hungerCoolDown = 500;
		dataWatcher.updateObject(15, Byte.valueOf((byte) amount));
		if (amount == 0 && riddenByEntity == null) {
			targetTasks.addTask(0, aiAttackTarget);
			tasks.addTask(0, aiAttackOnCollide);
		}
	}

	@Override
	protected void dropFewItems(boolean hit, int amount) {
		int count = 1 + getBloodConsumed();
		dropItem(BLItemRegistry.sapBall, count);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("bloodLevel", getBloodConsumed());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setBloodConsumed(nbttagcompound.getInteger("bloodLevel"));
	}

	@Override
	public String pageName() {
		return "leech";
	}
}
