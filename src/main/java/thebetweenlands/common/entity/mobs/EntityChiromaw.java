package thebetweenlands.common.entity.mobs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.ai.EntityAIMoveToDirect;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityChiromaw extends EntityFlying implements IMob, IEntityBL {
    private static final DataParameter<Boolean> IS_HANGING = EntityDataManager.createKey(EntityChiromaw.class, DataSerializers.BOOLEAN);

    public EntityChiromaw(World world) {
        super(world);
        setSize(0.8F, 0.9F);
        setIsHanging(false);

        this.moveHelper = new FlightMoveHelper(this);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackOnCollide(this));
        this.tasks.addTask(2, new EntityAIMoveToDirect<EntityChiromaw>(this, 0.1D) {
            @Override
            protected Vec3d getTarget() {
                if (!this.entity.getIsHanging()) {
                    EntityLivingBase target = this.entity.getAttackTarget();
                    if (target != null) {
                        return new Vec3d(target.posX, target.posY + 1 - this.entity.rand.nextFloat() * 0.3, target.posZ);
                    }
                }
                return null;
            }
        });
        this.tasks.addTask(3, new EntityAIFlyRandomly<EntityChiromaw>(this) {
            @Override
            protected double getTargetX(Random rand, double distanceMultiplier) {
                return this.entity.posX + (double) ((rand.nextFloat() * 2.0F - 1.0F) * 10.0F * distanceMultiplier);
            }

            @Override
            protected double getTargetY(Random rand, double distanceMultiplier) {
                return this.entity.posY + (rand.nextFloat() * 1.75D - 1.0D) * 10.0D * distanceMultiplier;
            }

            @Override
            protected double getTargetZ(Random rand, double distanceMultiplier) {
                return this.entity.posZ + (double) ((rand.nextFloat() * 2.0F - 1.0F) * 10.0F * distanceMultiplier);
            }

            @Override
            protected double getFlightSpeed() {
                return 0.08D;
            }
        });

        this.targetTasks.addTask(0, new EntityAIFindEntityNearestPlayer(this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        this.dataManager.register(IS_HANGING, false);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote && world.getDifficulty() == EnumDifficulty.PEACEFUL)
            setDead();

        EntityLivingBase attackTarget = this.getAttackTarget();

        if (attackTarget != null) {
            if (attackTarget instanceof EntityPlayer && ((EntityPlayer) attackTarget).capabilities.isCreativeMode || !getEntitySenses().canSee(attackTarget)) {
                this.setAttackTarget(null);
            }
        }

        if (this.isJumping && this.isInWater()) {
            //Moving out of water
            this.getMoveHelper().setMoveTo(this.posX, this.posY + 1, this.posZ, 0.32D);
        }

        if (this.getIsHanging()) {
            this.motionX = this.motionY = this.motionZ = 0.0D;
            this.posY = (double) MathHelper.floor(this.posY) + 1.0D - (double) this.height;
        } else {
            this.motionY *= 0.6D;
        }
    }


    @Override
    protected void updateAITasks() {
        super.updateAITasks();

        if (getIsHanging()) {
            if (!this.world.isRemote) {
                this.moveHelper.setMoveTo(this.posX, this.posY + 0.5D, this.posZ, 0);

                if (this.rand.nextInt(250) == 0 || !this.world.getBlockState(new BlockPos(this.posX, this.posY + 1, this.posZ)).isNormalCube()) {
                    setIsHanging(false);
                    this.world.playEvent(null, 1025, this.getPosition(), 0);
                } else if (this.getAttackTarget() != null) {
                    setIsHanging(false);
                    this.world.playEvent(null, 1025, this.getPosition(), 0);
                }
            }
        } else {
            if (this.getAttackTarget() != null) {
                double distanceX = this.getAttackTarget().posX - this.posX;
                double distanceZ = this.getAttackTarget().posZ - this.posZ;
                this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(distanceX, distanceZ)) * 180.0F / (float) Math.PI;
            } else {
                this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI;

                if (!this.world.isRemote && this.rand.nextInt(20) == 0 && world.getBlockState(new BlockPos(this.posX, this.posY + 1, this.posZ)).isNormalCube()) {
                    setIsHanging(true);
                }
            }
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
    }

    public boolean getIsHanging() {
        return dataManager.get(IS_HANGING);
    }

    public void setIsHanging(boolean hanging) {
        dataManager.set(IS_HANGING, hanging);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.CHIROMAW;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.FLYING_FIEND_LIVING;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return SoundRegistry.FLYING_FIEND_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.FLYING_FIEND_DEATH;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return EntityAIAttackOnCollide.useStandardAttack(this, entityIn);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 3;
    }
}
