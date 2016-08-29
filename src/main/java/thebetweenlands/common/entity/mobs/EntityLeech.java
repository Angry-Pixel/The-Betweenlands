package thebetweenlands.common.entity.mobs;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ai.EntityAIBLAvoidEntity;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class EntityLeech extends EntityMob implements IEntityBL {
    private static final int MAX_BLOOD_LEVEL = 5;

    private static final int TIME_TO_FLEE = 600;

    private EntityLivingBase target;

    public int attackCountDown = 20;

    public int hungerCoolDown;

    private int drainage;

    public float moveProgress;

    public boolean firstTickCheck;

    public int fleeingTick;

    AnimationMathHelper mathSucking = new AnimationMathHelper();

    private final EntityAINearestAttackableTarget aiAttackTarget = new EntityAINearestAttackableTarget(this, EntityLivingBase.class, false, true);

    private final EntityAIWander aiWander = new EntityAIWander(this, 0.5);

    private final EntityAIAttackMelee aiAttackOnCollide = new EntityAIAttackMelee(this, 0.5, false);

    private final EntityAIBLAvoidEntity avoidHarmer = new EntityAIBLAvoidEntity(this, EntityPlayer.class, 6, 0.5, 0.6);

    private static final DataParameter<Byte> BLOOD_CONSUMED = EntityDataManager.createKey(EntityAngler.class, DataSerializers.BYTE);

    public EntityLeech(World worldIn) {
        super(worldIn);
        setSize(0.7F, 0.3F);
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
        dataManager.register(BLOOD_CONSUMED, (byte) 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0);
    }

    @Override
    public boolean isAIDisabled() {
        return false;
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.SNAIL_LIVING;
    }

    @Override
    protected SoundEvent getHurtSound() {
        return SoundRegistry.SNAIL_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.SNAIL_DEATH;
    }

    public void onCollideWithEntity(EntityLivingBase entity) {
        if (!worldObj.isRemote) {
            if (!entity.isBeingRidden() && getBloodConsumed() <= 0) {
                startRiding(entity);
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
            if (getRidingEntity() != null) {
                setRotation(getRidingEntity().rotationYaw, getRidingEntity().rotationPitch);
                if (getRidingEntity().isBurning() && getRidingEntity() instanceof EntityLivingBase) {
                    PotionEffect stomachContents = new PotionEffect(MobEffects.POISON, 120 + getBloodConsumed() * 200 / MAX_BLOOD_LEVEL, 0);
                    ((EntityLivingBase) getRidingEntity()).addPotionEffect(stomachContents);
                    setBloodConsumed(0);
                    dismountEntity(getRidingEntity());
                    flee();
                    dismountRidingEntity();
                }
            }
            if (getBloodConsumed() == MAX_BLOOD_LEVEL && getRidingEntity() != null) {
                dismountEntity(getRidingEntity());
                dismountRidingEntity();
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
                    worldObj.spawnParticle(EnumParticleTypes.REDSTONE, posX + (rand.nextFloat() - rand.nextFloat()), posY + rand.nextFloat(), posZ + (rand.nextFloat() - rand.nextFloat()), 0, 0, 0);
                }
            }
        } else if (!worldObj.isRemote) {
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
        tasks.addTask(0, avoidHarmer);
        avoidHarmer.setTargetEntityClass(getRidingEntity().getClass());
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
        if (getRidingEntity() != null && getRidingEntity() instanceof EntityPlayer && this.getRidingEntity() == TheBetweenlands.proxy.getClientPlayer()) {
            return -2;
        }
        return 0.0;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean canAttackClass(Class entity) {
        return entity != EntityLeech.class && (entity == EntityPlayer.class || entity == EntityPlayerMP.class || entity == EntitySwampHag.class);
    }

    public int getBloodConsumed() {
        return dataManager.get(BLOOD_CONSUMED);
    }

    public void setBloodConsumed(int amount) {
        hungerCoolDown = 500;
        dataManager.set(BLOOD_CONSUMED, Byte.valueOf((byte) amount));
        if (amount == 0 && getRidingEntity() == null) {
            targetTasks.addTask(0, aiAttackTarget);
            tasks.addTask(0, aiAttackOnCollide);
        }
    }

    @Override
    protected void dropFewItems(boolean hit, int amount) {
        int count = 1 + getBloodConsumed();
        dropItem(ItemRegistry.SAP_BALL, count);
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
        setBloodConsumed(nbttagcompound.getInteger("bloodLevel"));
        fleeingTick = nbttagcompound.getInteger("fleeingTick");
    }

}
