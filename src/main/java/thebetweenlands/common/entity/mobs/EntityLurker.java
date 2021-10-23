package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.entity.ai.EntityAINearestAttackableSmellyTarget;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.MathUtils;

public class EntityLurker extends EntityCreature implements IEntityBL, IMob {
    private static final DataParameter<Boolean> IS_LEAPING = EntityDataManager.createKey(EntityLurker.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SHOULD_MOUTH_BE_OPEN = EntityDataManager.createKey(EntityLurker.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> MOUTH_MOVE_SPEED = EntityDataManager.createKey(EntityLurker.class, DataSerializers.FLOAT);
    private static final int MOUTH_OPEN_TICKS = 20;

    private int attackTime;

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
    public int huntingTimer;

    private boolean prevInWater;

    private int leapRiseTime;
    private int leapFallTime;

    private EntityMoveHelper moveHelperWater;
    private EntityMoveHelper moveHelperLand;

    private PathNavigateGround pathNavigatorGround;
    private PathNavigateSwimmer pathNavigatorWater;
    
    public EntityLurker(World world) {
        super(world);
        
        this.experienceValue = 5;

        this.setPathPriority(PathNodeType.WATER, 30);

        this.moveHelperWater = new EntityLurker.LurkerMoveHelper(this);
        this.moveHelperLand = new EntityMoveHelper(this);

        this.pathNavigatorGround = new PathNavigateGround(this, this.world);
        this.pathNavigatorGround.setCanSwim(true);
        
        this.pathNavigatorWater = new PathNavigateSwimmer(this, this.world);

        this.updateMovementAndPathfinding();
        
        setSize(1.6F, 0.9F);
    }

    @Override
    protected void initEntityAI() {
    	tasks.addTask(0, new EntityAIPanic(this, 1.5D) {
    		@Override
    		public boolean shouldExecute() {
    			return super.shouldExecute() && EntityLurker.this.world.getDifficulty() == EnumDifficulty.PEACEFUL;
    		}
    	});
        tasks.addTask(1, new EntityAIAttackMelee(this, 1.0D, false));
        tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 0.8D));
        tasks.addTask(3, new EntityAIWander(this, 0.7D, 80));
        tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(5, new EntityAILookIdle(this));

        targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(1, new EntityAINearestAttackableSmellyTarget<>(this, EntityPlayer.class, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityDragonFly.class, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityAngler.class, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityAnadia.class, true) {
    		@Override
    		public boolean shouldExecute() {
    			return super.shouldExecute() && huntingTimer <= 0;
    		}
        });

        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityJellyfish.class, true) {
    		@Override
    		public boolean shouldExecute() {
    			return super.shouldExecute() && huntingTimer <= 0;
    		}
        });

        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityFreshwaterUrchin.class, true) {
    		@Override
    		public boolean shouldExecute() {
    			return super.shouldExecute() && huntingTimer <= 0;
    		}
        });
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
        
        getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.5);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(55);
    }
    
    @Override
    protected boolean canDropLoot() {
        return true;
    }
    
    @Override
    public boolean isNotColliding() {
        return this.getEntityWorld().getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.getEntityWorld().checkNoEntityCollision(this.getEntityBoundingBox(), this);
    }

    @Override
    public boolean isInWater() {
        return getEntityWorld().handleMaterialAcceleration(getEntityBoundingBox(), Material.WATER, this);
    }

    private Block getRelativeBlock(int offsetY) {
        return getEntityWorld().getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(getEntityBoundingBox().minY) + offsetY, MathHelper.floor(posZ))).getBlock();
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (isInWater()) {
            if (!getEntityWorld().isRemote) {
                if (motionY < 0 && isLeaping()) {
                    setIsLeaping(false);
                }
            }
        } else {
            if (getEntityWorld().isRemote) {
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
            if (!getEntityWorld().isRemote) {
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
        float magnitude = MathHelper.sqrt(motionX * motionX + motionZ * motionZ) * (onGround ? 0 : 1);
        float motionPitch = MathHelper.clamp((float) Math.atan2(magnitude, motionY) / (float) Math.PI * 180 - 90, -10.0F, 10.0F);
        if (magnitude > 1) {
            magnitude = 1;
        }
        float newRotationPitch = isLeaping() ? 90 : leapFallTime > 0 ? -45 : MathHelper.clamp((rotationPitchBody - motionPitch) * magnitude * 4 * (inWater ? 1 : 0), -30.0F, 30.0F);
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
                BLParticles.SPLASH.spawn(this.getEntityWorld(), x, y, z, ParticleArgs.get().withMotion(motionX, motionY, motionZ).withColor(waterColorMultiplier));
            }
        }
    }

    private int getWaterColor() {
        int blockX = MathHelper.floor(posX), blockZ = MathHelper.floor(posZ);
        int y = 0;
        while (getRelativeBlock(y--) == Blocks.AIR && posY - y > 0) ;
        int blockY = MathHelper.floor(getEntityBoundingBox().minY + y);
        IBlockState blockState = getEntityWorld().getBlockState(new BlockPos(blockX, blockY, blockZ));
        if (blockState.getMaterial().isLiquid()) {
            int r = 255, g = 255, b = 255;
            // TODO: automatically build a map of all liquid blocks to the average color of there texture to get color from
            if (blockState.getBlock() == BlockRegistry.SWAMP_WATER) {
                r = 147;
                g = 132;
                b = 83;
            } else if (blockState.getBlock() == Blocks.WATER || blockState.getBlock() == Blocks.FLOWING_WATER) {
                r = 49;
                g = 70;
                b = 245;
            } else if (blockState.getBlock() == Blocks.LAVA || blockState.getBlock() == Blocks.FLOWING_LAVA) {
                r = 207;
                g = 85;
                b = 16;
            }
            int multiplier = blockState.getMapColor(getEntityWorld(), new BlockPos(blockX, blockY, blockZ)).getMapColor(1);
            return 0xFF000000 | (r * (multiplier >> 16 & 0xFF) / 255) << 16 | (g * (multiplier >> 8 & 0xFF) / 255) << 8 | (b * (multiplier & 0xFF) / 255);
        }
        return 0xFFFFFFFF;
    }

    protected void updateMovementAndPathfinding() {
    	if (this.isInWater()) {
            this.moveHelper = this.moveHelperWater;
        } else {
            this.moveHelper = this.moveHelperLand;
        }
        
        if (this.isInWater() && !this.world.isAirBlock(new BlockPos(this.posX, this.getEntityBoundingBox().maxY + 0.25D, this.posZ))) {
        	this.navigator = this.pathNavigatorWater;
        } else {
        	this.navigator = this.pathNavigatorGround;
        }
    }
    
    @Override
    public void setAttackTarget(EntityLivingBase entity) {
    	if(entity instanceof EntityPlayer && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
    		return;
    	}
    	super.setAttackTarget(entity);
    }
    
    @Override
    public void onUpdate() {
        this.updateMovementAndPathfinding();

        if (!this.getEntityWorld().isRemote) {
            Entity target = this.getAttackTarget();
            if (target instanceof EntityDragonFly && attackTime <= 0 && target.getDistance(this) < 3.2D && target.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && target.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY && ticksUntilBiteDamage == -1) {
                setShouldMouthBeOpen(true);
                setMouthMoveSpeed(10);
                ticksUntilBiteDamage = 10;
                attackTime = 20;
                entityBeingBit = target;

                if (isLeaping() && target instanceof EntityDragonFly) {
                    target.startRiding(this, true);
                    setAttackTarget((EntityDragonFly) target);
                }
            }
        }

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
                    	EntityAIAttackOnCollide.useStandardAttack(this, entityBeingBit);
                        if (getRidingEntity() == entityBeingBit) {
                            getRidingEntity().attackEntityFrom(DamageSource.causeMobDamage(this), ((EntityLivingBase) entityBeingBit).getMaxHealth());
                        }
                    }
                    entityBeingBit = null;
                }
            }
        }
        float movementSpeed = MathHelper.sqrt((prevPosX - posX) * (prevPosX - posX) + (prevPosY - posY) * (prevPosY - posY) + (prevPosZ - posZ) * (prevPosZ - posZ));
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
        
        if(!world.isRemote)
        	if(huntingTimer > 0)
        		huntingTimer--;
    }


    @Override
    public void travel(float strafe,float up, float forward) {
        if (isServerWorld()) {
            if (isInWater()) {
                moveRelative(strafe, up, forward, 0.1F);
                move(MoverType.SELF, motionX, motionY, motionZ);
                motionX *= 0.8999999761581421D;
                motionY *= 0.8999999761581421D;
                motionZ *= 0.8999999761581421D;

                if (getAttackTarget() == null) {
                    motionY -= 0.005D;
                }
            } else {
                super.travel(strafe, up, forward);
            }
        } else {
            super.travel(strafe, up, forward);
        }
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        attackTime--;
        if (anger > 0) {
            anger--;
            if (anger == 0) {
                setAttackTarget(null);
            }
        }
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        float distance = entityIn.getDistance(this);
        if (entityBeingBit != null || getRidingEntity() != null || entityIn.getRidingEntity() != null) {
            return false;
        }
        if (inWater && entityIn instanceof EntityDragonFly && !isLeaping() && distance < 5) {
            setIsLeaping(true);
            double distanceX = entityIn.posX - posX;
            double distanceZ = entityIn.posZ - posZ;
            float magnitude = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ);
            motionX += distanceX / magnitude * 0.8;
            motionY += 0.9;
            motionZ += distanceZ / magnitude * 0.8;
        }

        if (attackTime <= 0 && distance < 3.5D && entityIn.getEntityBoundingBox().maxY >= getEntityBoundingBox().minY && entityIn.getEntityBoundingBox().minY <= getEntityBoundingBox().maxY && ticksUntilBiteDamage == -1) {
            setShouldMouthBeOpen(true);
            setMouthMoveSpeed(10);
            ticksUntilBiteDamage = 10;
            attackTime = 10;
            entityBeingBit = entityIn;
        }
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (isEntityInvulnerable(source) || source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.DROWN)) {
            return false;
        }
        Entity attacker = source.getTrueSource();
        if (attacker instanceof EntityPlayer) {
            List<EntityLurker> nearLurkers = getEntityWorld().getEntitiesWithinAABB(EntityLurker.class, getEntityBoundingBox().grow(16, 16, 16));
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
    
    public void setHuntingTimer(int cooldownIn) {
        huntingTimer = cooldownIn;
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

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.LURKER_LIVING;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        setShouldMouthBeOpen(true);
        ticksUntilBiteDamage = 10;
        return SoundRegistry.LURKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.LURKER_DEATH;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.LURKER;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setShort("Anger", (short) anger);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        if (tagCompound.hasKey("Anger")) {
            anger = tagCompound.getShort("Anger");
        }
    }

    @Override
    public boolean isPushedByWater() {
        return false;
    }

    //AIs
    static class LurkerMoveHelper extends EntityMoveHelper {
        private final EntityLurker lurker;

        public LurkerMoveHelper(EntityLurker lurker) {
            super(lurker);
            this.lurker = lurker;
        }

        @Override
		public void onUpdateMoveHelper() {
            if (action == EntityMoveHelper.Action.MOVE_TO && !lurker.getNavigator().noPath()) {
                double d0 = posX - lurker.posX;
                double d1 = posY - lurker.posY;
                double d2 = posZ - lurker.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double) MathHelper.sqrt(d3);
                d1 = d1 / d3;
                float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                lurker.rotationYaw = limitAngle(lurker.rotationYaw, f, 90.0F);
                lurker.renderYawOffset = lurker.rotationYaw;
                float f1 = (float) (speed * lurker.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                lurker.setAIMoveSpeed(lurker.getAIMoveSpeed() + (f1 - lurker.getAIMoveSpeed()) * 0.125F);
                double d4 = Math.sin((double) (lurker.ticksExisted + lurker.getEntityId()) * 0.5D) * 0.05D;
                double d5 = Math.cos((double) (lurker.rotationYaw * 0.017453292F));
                double d6 = Math.sin((double) (lurker.rotationYaw * 0.017453292F));
                lurker.motionX += d4 * d5;
                lurker.motionZ += d4 * d6;
                d4 = Math.sin((double) (lurker.ticksExisted + lurker.getEntityId()) * 0.75D) * 0.05D;
                lurker.motionY += d4 * (d6 + d5) * 0.25D;
                if (Math.abs(lurker.motionY) < 0.35) {
                    lurker.motionY += (double) lurker.getAIMoveSpeed() * d1 * 0.1D * (2 + (d1 > 0 ? 0.4 : 0) + (lurker.collidedHorizontally ? 20 : 0));
                }
                EntityLookHelper entitylookhelper = lurker.getLookHelper();
                double d7 = lurker.posX + d0 / d3 * 2.0D;
                double d8 = (double) lurker.getEyeHeight() + lurker.posY + d1 / d3;
                double d9 = lurker.posZ + d2 / d3 * 2.0D;
                double d10 = entitylookhelper.getLookPosX();
                double d11 = entitylookhelper.getLookPosY();
                double d12 = entitylookhelper.getLookPosZ();

                if (!entitylookhelper.getIsLooking()) {
                    d10 = d7;
                    d11 = d8;
                    d12 = d9;
                }

                lurker.getLookHelper().setLookPosition(d10 + (d7 - d10) * 0.125D, d11 + (d8 - d11) * 0.125D, d12 + (d9 - d12) * 0.125D, 10.0F, 40.0F);
            } else {
                lurker.setAIMoveSpeed(0.0F);
            }
        }
    }
    
    @Override
    public float getBlockPathWeight(BlockPos pos) {
        return 0.5F;
    }
}
