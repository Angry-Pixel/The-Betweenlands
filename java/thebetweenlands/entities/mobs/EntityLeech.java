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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.utils.AnimationMathHelper;

public class EntityLeech extends EntityMob implements IEntityBL {

	private final int MAX_BLOOD_LEVEL = 5;
	private EntityLivingBase target;
	public int attackCountDown = 60;
	public int hungerCoolDown = 0;
	private int drainage;
	public float moveProgress;
	public boolean firstTickCheck;
	AnimationMathHelper mathSucking = new AnimationMathHelper();
	private final EntityAINearestAttackableTarget AI_ATTACK_TARGET = new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true);
	private final EntityAIWander AI_WANDER = new EntityAIWander(this, 0.5D);
	private final EntityAIAttackOnCollide AI_ATTACK_ON_COLLIDE = new EntityAIAttackOnCollide(this, EntityLivingBase.class, 0.5D, false);

	public EntityLeech(World world) {
		super(world);
		setSize(0.5F, 0.25F);
		stepHeight = 0.0F;
		moveProgress = 0.0F;
		firstTickCheck = false;
		drainage = 0;
		setBloodConsumed(0);
		tasks.addTask(0, AI_ATTACK_ON_COLLIDE);
		tasks.addTask(1, AI_WANDER);
		tasks.addTask(2, new EntityAILookIdle(this));
		targetTasks.addTask(0, AI_ATTACK_TARGET);
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(15, Byte.valueOf((byte) 0));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(0.5D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(16.0D);
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
		if (!worldObj.isRemote)
			if (entity.riddenByEntity == null && getBloodConsumed() <= 0) {
				mountEntity(entity);
				targetTasks.removeTask(AI_ATTACK_TARGET);
				tasks.removeTask(AI_ATTACK_ON_COLLIDE);
			}
	}

	@Override
	public void onUpdate() {
		if (!worldObj.isRemote) {
			if(getAttackTarget() != null && getDistanceToEntity(getAttackTarget() ) < 2.0D)
				onCollideWithEntity(getAttackTarget());
			if (ridingEntity != null)
				setRotation(ridingEntity.rotationYaw, ridingEntity.rotationPitch);
			if (getBloodConsumed() == MAX_BLOOD_LEVEL && ridingEntity != null) {
				dismountEntity(ridingEntity);
				ridingEntity = null;
			}
		}

		if (!firstTickCheck) {
			mountEntity(null);
			ridingEntity = null;
			firstTickCheck = true;
		}
		if (--hungerCoolDown == 0)
			if (getBloodConsumed() > 0)
				setBloodConsumed(getBloodConsumed() - 1);
		if (ridingEntity != null) {
			moveProgress = 1.0F + mathSucking.swing(1.F, 0.15F, false);
			if (rand.nextInt(10) == 0)
				for (int i = 0; i < 8; i++)
					worldObj.spawnParticle("reddust", posX + (rand.nextFloat() - rand.nextFloat()), posY + rand.nextFloat(), posZ + (rand.nextFloat() - rand.nextFloat()), 0, 0, 0);
		} else
			if(!worldObj.isRemote)
				moveProgress = 0F + mathSucking.swing(1.F, 0.15F, false);

		if (ridingEntity != null && ridingEntity instanceof EntityLivingBase && getBloodConsumed() < MAX_BLOOD_LEVEL) {
			drainage++;
			if (drainage >= attackCountDown) {
				ridingEntity.attackEntityFrom(DamageSource.causeMobDamage(this), 1F);
				drainage = 0;
				setBloodConsumed(getBloodConsumed() + 1);
			}
		}
		super.onUpdate();
	}

	@Override
	public double getYOffset() {
		if (ridingEntity != null && ridingEntity instanceof EntityPlayer)
			return -2D;
		else
			return yOffset;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.inWall) || source.equals(DamageSource.drown))
			return false;
		return super.attackEntityFrom(source, damage);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canAttackClass(Class entity) {
		return EntityLeech.class != entity;
	}

	public int getBloodConsumed() {
		return dataWatcher.getWatchableObjectByte(15);
	}

	public void setBloodConsumed(int amount) {
		hungerCoolDown = 500;
		dataWatcher.updateObject(15, Byte.valueOf((byte) amount));
		if(amount == 0 && riddenByEntity == null) {
			targetTasks.addTask(0, AI_ATTACK_TARGET);
			tasks.addTask(0, AI_ATTACK_ON_COLLIDE);
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
}
