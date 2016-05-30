package thebetweenlands.common.entity.mobs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
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
import thebetweenlands.util.MathUtils;

import java.util.List;

public class EntityLurker extends EntityMob implements IEntityBL {
    private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityLurker.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOULD_MOUTH_BE_OPEN = EntityDataManager.createKey(EntityLurker.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> MOUTH_MOVE_SPEED = EntityDataManager.createKey(EntityLurker.class, DataSerializers.FLOAT);
    private static final int MOUTH_OPEN_TICKS = 20;

    private BlockPos currentSwimTarget;

    private int attackTime;

    private Class<?>[] prey = {EntityAngler.class, EntityDragonFly.class};

    private float prevRotationPitchBody;
    private float rotationPitchBody;

    private float prevTailYaw;
    private float tailYaw;

    private float prevTailPitch;
    private float tailPitch;

    private float prevMouthOpenTicks;
    private float mouthOpenTicks;

    private int ticksUntilBiteDamage = -1;

    private Entity entityBeingBit;

    private int anger;

    private boolean prevInWater;

    private int leapRiseTime;
    private int leapFallTime;

    public EntityLurker(World world) {
        super(world);
        setSize(1.9F, 0.9F);
        tasks.addTask(1, new EntityAIAttackMelee(this, 0.5D, false));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(IS_LEAPING, false);
        dataManager.register(SHOULD_MOUTH_BE_OPEN, false);
        dataManager.register(MOUTH_MOVE_SPEED, 1.0f);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.5);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(55);
    }

    @Override
    public boolean getCanSpawnHere() {
        return worldObj.getDifficulty() != EnumDifficulty.PEACEFUL && getRelativeBlock(0) == BlockRegistry.swampWater;
    }

    @Override
    public boolean isInWater() {
        return worldObj.handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
    }

    private Block getRelativeBlock(int offsetY) {
        return worldObj.getBlockState(new BlockPos(MathHelper.floor_double(posX), MathHelper.floor_double(getEntityBoundingBox().minY) + offsetY, MathHelper.floor_double(posZ))).getBlock();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (isInWater()) {
            if (!worldObj.isRemote) {
                Entity entityToAttack = getAttackTarget();
                if (entityToAttack == null) {
                    swimAbout();
                } else {
                    currentSwimTarget = new BlockPos(MathHelper.floor_double(entityToAttack.posX), MathHelper.floor_double(entityToAttack.posY), MathHelper.floor_double(entityToAttack.posZ));
                    swimToTarget();
                }
                if (motionY < 0 && isLeaping()) {
                    setIsLeaping(false);
                }
            }
            renderYawOffset += (-((float) Math.atan2(motionX, motionZ)) * 180.0F / (float) Math.PI - renderYawOffset) * 0.1F;
            rotationYaw = renderYawOffset;
        } else {
            if (worldObj.isRemote) {
                if (prevInWater && isLeaping()) {
                    breachWater();
                }
            } else {
                if (onGround) {
                    setIsLeaping(false);
                } else {
                    motionX *= 0.4;
                    motionY *= 0.98;
                    motionZ *= 0.4;
                }
            }
        }
        if (isLeaping()) {
            leapRiseTime++;
            if (!worldObj.isRemote) {
                rotationYaw += 10F;
            }
        } else {
            if (leapRiseTime > 0 && leapFallTime == leapRiseTime) {
                leapFallTime = leapRiseTime = 0;
            }
            if (leapFallTime < leapRiseTime) {
                leapFallTime++;
            }
        }
        float magnitude = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ) * (onGround ? 0 : 1);
        float motionPitch = (float) Math.atan2(magnitude, motionY) / (float) Math.PI * 180 - 90;
        if (magnitude > 1) {
            magnitude = 1;
        }
        float newRotationPitch = isLeaping() ? 90 : leapFallTime > 0 ? -45 : (rotationPitchBody - motionPitch) * magnitude * 4 * (inWater ? 1 : 0);
        tailPitch += (rotationPitchBody - newRotationPitch) * 0.75F;
        rotationPitchBody += (newRotationPitch - rotationPitchBody) * 0.3F;
        if (Math.abs(rotationPitchBody) < 0.05F) {
            rotationPitchBody = 0;
        }
    }

    private void breachWater() {
        int ring = 2;
        int waterColorMultiplier = getWaterColor();
        while (ring-- > 0) {
            int particleCount = ring * 12 + 20 + rand.nextInt(10);
            for (int p = 0; p < particleCount; p++) {
                float theta = p / (float) particleCount * MathUtils.TAU;
                float dx = MathHelper.cos(theta);
                float dz = MathHelper.sin(theta);
                double x = posX + dx * ring * 1 * MathUtils.linearTransformd(rand.nextDouble(), 0, 1, 0.6, 1.2) + rand.nextDouble() * 0.3 - 0.15;
                double y = posY - rand.nextDouble() * 0.2;
                double z = posZ + dz * ring * 1 * MathUtils.linearTransformd(rand.nextDouble(), 0, 1, 0.6, 1.2) + rand.nextDouble() * 0.3 - 0.15;
                double motionX = dx * MathUtils.linearTransformf(rand.nextFloat(), 0, 1, 0.03F, 0.2F);
                double motionY = ring * 0.3F + rand.nextDouble() * 0.1;
                double motionZ = dz * MathUtils.linearTransformf(rand.nextFloat(), 0, 1, 0.03F, 0.2F);
                //TODO add splash particle
                //BLParticle.SPLASH.spawn(worldObj, x, y, z, motionX, motionY, motionZ, 1, waterColorMultiplier);
            }
        }
    }

    private int getWaterColor() {
        int blockX = MathHelper.floor_double(posX), blockZ = MathHelper.floor_double(posZ);
        int y = 0;
        while (getRelativeBlock(y--) == Blocks.AIR && posY - y > 0) ;
        int blockY = MathHelper.floor_double(getEntityBoundingBox().minY + y);
        Block block = worldObj.getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock();
        if (block.getMaterial(worldObj.getBlockState(new BlockPos(blockX, blockY, blockZ))).isLiquid()) {
            int r = 255, g = 255, b = 255;
            // TODO: automatically build a map of all liquid blocks to the average color of there texture to get color from
            if (block == BlockRegistry.swampWater) {
                r = 147;
                g = 132;
                b = 83;
            } else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                r = 49;
                g = 70;
                b = 245;
            } else if (block == Blocks.WATER || block == Blocks.FLOWING_LAVA) {
                r = 207;
                g = 85;
                b = 16;
            }
            int multiplier = block.getMapColor(worldObj.getBlockState(new BlockPos(blockX, blockY, blockZ))).getMapColor(1);
            return 0xFF000000 | (r * (multiplier >> 16 & 0xFF) / 255) << 16 | (g * (multiplier >> 8 & 0xFF) / 255) << 8 | (b * (multiplier & 0xFF) / 255);
        }
        return 0xFFFFFFFF;
    }

    @Override
    public void onUpdate() {
        prevRotationPitchBody = rotationPitchBody;
        prevTailPitch = tailPitch;
        prevTailYaw = tailYaw;
        while (rotationPitchBody - prevRotationPitchBody < -180) {
            prevRotationPitchBody -= 360;
        }
        while (rotationPitchBody - prevRotationPitchBody >= 180) {
            prevRotationPitchBody += 360;
        }
        while (tailPitch - prevTailPitch < -180) {
            prevTailPitch -= 360;
        }
        while (tailPitch - prevTailPitch >= 180) {
            prevTailPitch += 360;
        }
        while (tailYaw - prevTailYaw < -180) {
            prevTailYaw -= 360;
        }
        while (tailYaw - prevTailYaw >= 180) {
            prevTailYaw += 360;
        }
        prevMouthOpenTicks = mouthOpenTicks;
        prevInWater = inWater;
        super.onUpdate();
        if (shouldMouthBeOpen()) {
            if (mouthOpenTicks < MOUTH_OPEN_TICKS) {
                mouthOpenTicks += getMouthMoveSpeed();
            }
            if (mouthOpenTicks > MOUTH_OPEN_TICKS) {
                mouthOpenTicks = MOUTH_OPEN_TICKS;
            }
        } else {
            if (mouthOpenTicks > 0) {
                mouthOpenTicks -= getMouthMoveSpeed();
            }
            if (mouthOpenTicks < 0) {
                mouthOpenTicks = 0;
            }
        }
        if (ticksUntilBiteDamage > -1) {
            ticksUntilBiteDamage--;
            if (ticksUntilBiteDamage == -1) {
                setShouldMouthBeOpen(false);
                if (entityBeingBit != null) {
                    if (!entityBeingBit.isDead) {
                        super.attackEntityAsMob(entityBeingBit);
                        if (getRidingEntity() == entityBeingBit) {
                            getRidingEntity().attackEntityFrom(DamageSource.causeMobDamage(this), ((EntityLivingBase) entityBeingBit).getMaxHealth());
                        }
                    }
                    entityBeingBit = null;
                }
            }
        }
        float movementSpeed = MathHelper.sqrt_double((prevPosX - posX) * (prevPosX - posX) + (prevPosY - posY) * (prevPosY - posY) + (prevPosZ - posZ) * (prevPosZ - posZ));
        if (movementSpeed > 1) {
            movementSpeed = 1;
        } else if (movementSpeed < 0.08) {
            movementSpeed = 0;
        }
        if (Math.abs(tailYaw) < 90) {
            tailYaw += (prevRenderYawOffset - renderYawOffset);
        }
        if (Math.abs(tailPitch) < 90) {
            tailPitch += (prevRotationPitchBody - rotationPitchBody);
        }
        tailPitch *= 0.5F;
        tailYaw *= (1 - movementSpeed);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        attackTime--;
        if (getAttackTarget() == null) {
            if (entityBeingBit == null) {
                setAttackTarget(findEnemyToAttack());
            }
        } else {
            if (getAttackTarget().getDistanceSqToEntity(this) > 256) {
                setAttackTarget(null);
            }
        }
        if (anger > 0) {
            anger--;
            if (anger == 0) {
                setAttackTarget(null);
            }
        }
    }


    public void swimAbout() {
        if (currentSwimTarget != null && (worldObj.getBlockState(currentSwimTarget).getMaterial() != Material.WATER || currentSwimTarget.getY() < 1)) {
            currentSwimTarget = null;
        }
        int x = MathHelper.floor_double(posX);
        int y = MathHelper.floor_double(getEntityBoundingBox().minY);
        int z = MathHelper.floor_double(posZ);
        if (currentSwimTarget == null || rand.nextInt(30) == 0 || currentSwimTarget.getDistance(x, y, z) < 4) {
            currentSwimTarget = new BlockPos(x + rand.nextInt(10) - rand.nextInt(10), y - rand.nextInt(4) + 1, z + rand.nextInt(10) - rand.nextInt(10));
        }
        swimToTarget();
    }

    private void swimToTarget() {
        if (getRidingEntity() != null) {
            return;
        }
        double targetX = currentSwimTarget.getX() + 0.5 - posX;
        double targetY = currentSwimTarget.getY() - posY;
        double targetZ = currentSwimTarget.getZ() + 0.5 - posZ;
        motionX += (Math.signum(targetX) * 0.2 - motionX) * 0.2;
        motionY += (Math.signum(targetY) * 0.4 - motionY) * 0.01;
        motionY -= 0.01;
        motionZ += (Math.signum(targetZ) * 0.2 - motionZ) * 0.2;
        moveForward = 0.5F;
    }

    private EntityLivingBase findEnemyToAttack() {
        List<Entity> nearEntities = worldObj.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().expand(8, 10, 8));
        for (int i = 0; i < nearEntities.size(); i++) {
            Entity entity = nearEntities.get(i);
            for (int n = 0; n < prey.length; n++) {
                if (entity.getClass() == prey[n] && entity instanceof EntityLivingBase) {
                    return (EntityLivingBase) entity;
                }
            }
        }
        return null;
    }


    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        float distance = entityIn.getDistanceToEntity(this);
        if (entityBeingBit != null || getRidingEntity() != null || entityIn.getRidingEntity() != null) {
            return false;
        }
        if (inWater && entityIn instanceof EntityDragonFly && !isLeaping() && distance < 5) {
            setIsLeaping(true);
            double distanceX = entityIn.posX - posX;
            double distanceZ = entityIn.posZ - posZ;
            float magnitude = MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
            motionX += distanceX / magnitude * 0.8;
            motionY += 0.9;
            motionZ += distanceZ / magnitude * 0.8;
        }
        if (attackTime <= 0 && distance < 3 && entityIn.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && entityIn.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY && ticksUntilBiteDamage == -1) {
            setShouldMouthBeOpen(true);
            setMouthMoveSpeed(10);
            ticksUntilBiteDamage = 10;
            attackTime = 20;
            entityBeingBit = entityIn;

            //TODO // FIXME: 10/04/2016 doesn't seem to work atm
            if (isLeaping() && entityIn instanceof EntityDragonFly) {
                entityIn.startRiding(this, true);
                setAttackTarget(null);
            }
        }
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (isEntityInvulnerable(source) || source.equals(DamageSource.inWall) || source.equals(DamageSource.drown)) {
            return false;
        }
        Entity attacker = source.getEntity();
        if (attacker instanceof EntityPlayer) {
            List<EntityLurker> nearLurkers = worldObj.getEntitiesWithinAABB(EntityLurker.class, getEntityBoundingBox().expand(16, 16, 16));
            for (EntityLurker fellowLurker : nearLurkers) {
                // Thou shouldst joineth me! F'r thither is a great foe comest!
                // RE: lol
                fellowLurker.showDeadlyAffectionTowards(attacker);
            }
        }
        return super.attackEntityFrom(source, damage);
    }

    private void showDeadlyAffectionTowards(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            setAttackTarget((EntityLivingBase) entity);
            anger = 200 + rand.nextInt(100);
        }
    }

    public boolean isLeaping() {
        return dataManager.get(IS_LEAPING);
    }

    public void setIsLeaping(boolean isLeaping) {
        dataManager.set(IS_LEAPING, isLeaping);
    }

    public boolean shouldMouthBeOpen() {
        return dataManager.get(SHOULD_MOUTH_BE_OPEN);
    }

    public void setShouldMouthBeOpen(boolean shouldMouthBeOpen) {
        dataManager.set(SHOULD_MOUTH_BE_OPEN, shouldMouthBeOpen);
    }

    public float getMouthMoveSpeed() {
        return dataManager.get(MOUTH_MOVE_SPEED);
    }

    public void setMouthMoveSpeed(float mouthMoveSpeed) {
        dataManager.set(MOUTH_MOVE_SPEED, mouthMoveSpeed);
    }

    public float getRotationPitch(float partialRenderTicks) {
        return rotationPitchBody * partialRenderTicks + prevRotationPitchBody * (1 - partialRenderTicks);
    }

    public float getMouthOpen(float partialRenderTicks) {
        return (mouthOpenTicks * partialRenderTicks + prevMouthOpenTicks * (1 - partialRenderTicks)) / MOUTH_OPEN_TICKS;
    }

    public float getTailYaw(float partialRenderTicks) {
        return tailYaw * partialRenderTicks + prevTailYaw * (1 - partialRenderTicks);
    }

    public float getTailPitch(float partialRenderTicks) {
        return tailPitch * partialRenderTicks + prevTailPitch * (1 - partialRenderTicks);
    }

    //TODO add lurkerLiving sound
    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }


    //TODO add lurkerHurt sound
    @Override
    protected SoundEvent getHurtSound() {
        setShouldMouthBeOpen(true);
        ticksUntilBiteDamage = 10;
        return super.getHurtSound();
    }

    //TODO add lurkerDeath sound
    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    protected void dropFewItems(boolean recentlyHit, int looting) {
        entityDropItem(ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.LURKER_SKIN, 3), 0F);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setShort("Anger", (short) anger);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        anger = tagCompound.getShort("Anger");
    }
}
