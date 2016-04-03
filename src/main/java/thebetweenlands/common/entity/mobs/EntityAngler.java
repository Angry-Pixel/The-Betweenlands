package thebetweenlands.common.entity.mobs;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.common.item.misc.ItemGeneric;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class EntityAngler extends EntityWaterMob implements IEntityBL, IMob {
    private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityAngler.class, DataSerializers.BOOLEAN);
    public float moveProgress;
    AnimationMathHelper animation = new AnimationMathHelper();
    private BlockPos currentSwimTarget;

    public EntityAngler(World world) {
        super(world);
        setSize(1F, 0.7F);
        setAir(80);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.register(IS_LEAPING, false);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(34.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
    }

    @Override
    protected SoundEvent getHurtSound() {
        return super.getHurtSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.entity_hostile_swim;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        int amount = rand.nextInt(3) + rand.nextInt(1 + looting);
        int count;
        if (recentlyHit) {
            for (count = 0; count < amount; ++count) {
                if (rand.nextBoolean())
                    entityDropItem(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.ANGLER_TOOTH, 3), 0.0F);
                else
                    entityDropItem(ItemGeneric.createStack(ItemRegistry.anglerMeatRaw, 1, 0), 0.0F);
            }
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        return worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY), MathHelper.floor_double(posZ))).getBlock() == BlockRegistry.swampWater;
    }

    @Override
    public boolean isInWater() {
        return worldObj.handleMaterialAcceleration(getEntityBoundingBox(), Material.water, this);
    }

    public boolean isGrounded() {
        return !isInWater() && worldObj.isAirBlock(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY + 1), MathHelper.floor_double(posZ))) && worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(posY - 1), MathHelper.floor_double(posZ))).getBlock().isCollidable();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.worldObj.isRemote) {
            EntityPlayer target = worldObj.func_184139_a(this.getPosition(), 16.0D, 16.0D);
            setAttackTarget(target);
            if (isInWater()) {
                if (!worldObj.isRemote) {
                    if (getAttackTarget() != null) {
                        currentSwimTarget = new BlockPos((int) getAttackTarget().posX, (int) ((int) getAttackTarget().posY + getAttackTarget().getEyeHeight()), (int) getAttackTarget().posZ);
                        swimToTarget();
                    } else
                        swimAbout();
                }
            } else {
                if (!worldObj.isRemote) {
                    if (!onGround) {
                        motionX = 0.0D;
                        motionY -= 0.08D;
                        motionY *= 0.9800000190734863D;
                        motionZ = 0.0D;
                    } else {
                        setIsLeaping(false);
                        motionY += 0.4F;
                        motionX += (rand.nextFloat() - rand.nextFloat()) * 0.3F;
                        motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.3F;
                    }
                }
            }
        } else {
            if (isInWater()) {
                moveProgress = animation.swing(1.2F, 0.4F, false);
                renderYawOffset += (-((float) Math.atan2(motionX, motionZ)) * 180.0F / (float) Math.PI - renderYawOffset) * 0.1F;
                rotationYaw = renderYawOffset;
            } else {
                moveProgress = animation.swing(2F, 0.4F, false);
            }
        }

    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.getDifficulty() == EnumDifficulty.PEACEFUL)
            setDead();
    }

    @Override
    public void onEntityUpdate() {
        int air = getAir();
        super.onEntityUpdate();

        if (isEntityAlive() && !isInWater()) {
            --air;
            setAir(air);

            if (getAir() == -20) {
                setAir(0);
                attackEntityFrom(DamageSource.drown, 2.0F);
            }
        } else
            setAir(80);
    }

    private void swimAbout() {
        if (currentSwimTarget != null && (worldObj.getBlockState(currentSwimTarget).getBlock() != BlockRegistry.swampWater && worldObj.getBlockState(currentSwimTarget).getBlock() != Blocks.water || currentSwimTarget.getY() < 1))
            currentSwimTarget = null;

        if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistance((int) posX, (int) posY, (int) posZ) < 10.0F)
            currentSwimTarget = new BlockPos((int) posX + rand.nextInt(10) - rand.nextInt(10), (int) posY - rand.nextInt(6) + 2, (int) posZ + rand.nextInt(10) - rand.nextInt(10));

        swimToTarget();
    }

    private void swimToTarget() {
        double targetX = currentSwimTarget.getX() + 0.5D - posX;
        double targetY = currentSwimTarget.getY() + 0.5D - posY;
        double targetZ = currentSwimTarget.getZ() + 0.5D - posZ;
        motionX += (Math.signum(targetX) * 0.3D - motionX) * 0.1D;
        motionY += (Math.signum(targetY) * 0.4D - motionY) * 0.08D;
        motionY -= 0.01D;
        motionZ += (Math.signum(targetZ) * 0.3D - motionZ) * 0.1D;
        moveForward = 0.5F;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        super.onCollideWithPlayer(player);
        if (!player.capabilities.isCreativeMode && !worldObj.isRemote && getEntitySenses().canSee(player)) {
            if (getDistanceToEntity(player) <= 1.5F)
                if (player.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && player.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY) {
                    player.attackEntityFrom(DamageSource.causeMobDamage(this), 2F);
                }
        }
    }


    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        Double distance = this.getPosition().getDistance((int) entityIn.posX, (int) entityIn.posY, (int) entityIn.posZ);
        if (distance > 2.0F && distance < 6.0F && entityIn.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && entityIn.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY && rand.nextInt(3) == 0)
            if (isInWater() && worldObj.isAirBlock(new BlockPos((int) posX, (int) posY + 1, (int) posZ))) {
                setIsLeaping(true);
                double distanceX = entityIn.posX - posX;
                double distanceZ = entityIn.posZ - posZ;
                float distanceSqrRoot = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
                motionX = distanceX / distanceSqrRoot * 1.5D * 0.900000011920929D + motionX * 2.70000000298023224D;
                motionZ = distanceZ / distanceSqrRoot * 1.5D * 0.900000011920929D + motionZ * 2.70000000298023224D;
                motionY = 0.8D;
                return true;
            }
        return false;
    }

    public boolean isLeaping() {
        return dataWatcher.get(IS_LEAPING);
    }

    private void setIsLeaping(boolean leaping) {
        dataWatcher.set(IS_LEAPING, leaping);
    }

}
