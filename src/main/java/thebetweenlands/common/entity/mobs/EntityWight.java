package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.ai.EntityAIMoveToDirect;
import thebetweenlands.common.entity.ai.EntityAITargetNonSneaking;
import thebetweenlands.common.entity.ai.EntityAIWightAttack;
import thebetweenlands.common.entity.ai.EntityAIWightBuffSwampHag;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.network.clientbound.MessageWightVolatileParticles;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityWight extends EntityMob implements IEntityBL {

    public static final IAttribute VOLATILE_HEALTH_START_ATTRIB = (new RangedAttribute(null, "bl.volatileHealthStart", 1.0D, 0.0D, 1.0D)).setDescription("Volatile Health Percentage Start");
    public static final IAttribute VOLATILE_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.volatileCooldown", 400.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Volatile Cooldown");
    public static final IAttribute VOLATILE_FLIGHT_SPEED_ATTRIB = (new RangedAttribute(null, "bl.volatileFlightSpeed", 0.32D, 0.0D, 5.0D)).setDescription("Volatile Flight Speed");
    public static final IAttribute VOLATILE_LENGTH_ATTRIB = (new RangedAttribute(null, "bl.volatileLength", 600.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Volatile Length");
    public static final IAttribute VOLATILE_MAX_DAMAGE_ATTRIB = (new RangedAttribute(null, "bl.volatileMaxDamage", 20.0D, 0.0D, Double.MAX_VALUE)).setDescription("Volatile Max Damage");

    protected static final DataParameter<Boolean> HIDING_STATE_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> VOLATILE_STATE_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.BOOLEAN);
    protected final EntityMoveHelper flightMoveHelper;
    protected final EntityMoveHelper groundMoveHelper;
    private int hidingAnimationTicks = 0;
    private int lastHidingAnimationTicks = 0;
    private int volatileCooldownTicks = (int) VOLATILE_COOLDOWN_ATTRIB.getDefaultValue() / 2 + 20;
    private int volatileTicks = 0;
    private float volatileReceivedDamage = 0.0F;
    private boolean canTurnVolatile = true;
    private boolean canTurnVolatileOnTarget = false;
    private boolean didTurnVolatileOnPlayer = false;
    private static final DataParameter<Integer> GROW_TIMER = EntityDataManager.createKey(EntityWight.class, DataSerializers.VARINT);
    public int growCount, prevGrowCount;

    public EntityWight(World world) {
        super(world);
        this.experienceValue = 10;
        setSize(0.7F, 2.2F);
        this.setPathPriority(PathNodeType.WATER, 0.2F);
        this.flightMoveHelper = new FlightMoveHelper(this) {
            @Override
            protected double getFlightSpeed() {
                return this.entity.getAttributeMap().getAttributeInstance(VOLATILE_FLIGHT_SPEED_ATTRIB).getAttributeValue();
            }
        };
        this.moveHelper = this.groundMoveHelper = new EntityMoveHelper(this);
    }

    @Override
    protected void initEntityAI() {
        this.targetTasks.addTask(0, new EntityAITargetNonSneaking(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIWightAttack(this, 1.0D, false));
        this.tasks.addTask(2, new EntityAIWightBuffSwampHag(this));
        this.tasks.addTask(3, new EntityAIMoveToDirect<EntityWight>(this, this.getAttributeMap().getAttributeInstance(VOLATILE_FLIGHT_SPEED_ATTRIB).getAttributeValue()) {
            @Override
            protected Vec3d getTarget() {
                if (this.entity.volatileTicks >= 20) {
                    EntityLivingBase target = this.entity.getAttackTarget();
                    if (target != null) {
                        return new Vec3d(target.posX, target.posY + target.getEyeHeight() / 2.0D, target.posZ);
                    }
                }
                return null;
            }
        });
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 0.4D));
        this.tasks.addTask(5, new EntityAIWander(this, 0.3D));
        this.tasks.addTask(9, new EntityAIFlyRandomly<EntityWight>(this) {
            @Override
            public boolean shouldExecute() {
                return this.entity.isVolatile() && this.entity.volatileTicks >= 20 && this.entity.getAttackTarget() == null && super.shouldExecute();
            }

            @Override
            protected double getFlightSpeed() {
                return 0.1D;
            }
        });
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(VOLATILE_STATE_DW, false);
        this.dataManager.register(HIDING_STATE_DW, false);
        dataManager.register(GROW_TIMER, 40);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(76.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80.0D);

        this.getAttributeMap().registerAttribute(VOLATILE_HEALTH_START_ATTRIB);
        this.getAttributeMap().registerAttribute(VOLATILE_COOLDOWN_ATTRIB);
        this.getAttributeMap().registerAttribute(VOLATILE_FLIGHT_SPEED_ATTRIB);
        this.getAttributeMap().registerAttribute(VOLATILE_LENGTH_ATTRIB);
        this.getAttributeMap().registerAttribute(VOLATILE_MAX_DAMAGE_ATTRIB);
    }

    @Override
	public void onUpdate() {
		if (this.world.isRemote) {
			prevGrowCount = growCount;
			growCount = getGrowTimer();
			if (getGrowTimer() > 0 && getGrowTimer() < 40)
				if (this.ticksExisted % 4 == 0)
					this.spawnVolatileParticles(true);
		}

        if (!this.world.isRemote) {
			if (isInTar()) {
				if (getGrowTimer() > 0)
					setGrowTimer(Math.max(0, getGrowTimer() - 1));
				if (getGrowTimer() <= 0) {
					EntityTarBeast tar_beast = new EntityTarBeast(getEntityWorld());
					tar_beast.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
					tar_beast.setGrowTimer(0);
					getEntityWorld().spawnEntity(tar_beast);
					setDead();
				}
			}

			if (!isInTar() && getGrowTimer() < 40)
				setGrowTimer(Math.min(40, getGrowTimer() + 1));

            if (this.getAttackTarget() == null) {
                this.setHiding(true);

                this.canTurnVolatileOnTarget = false;
            } else {
                this.setHiding(false);

                if (this.canTurnVolatile && !this.isVolatile() && !this.isRiding() && this.canPossess(this.getAttackTarget()) && this.canTurnVolatileOnTarget) {
                    if (this.volatileCooldownTicks > 0) {
                        this.volatileCooldownTicks--;
                    }

                    if (this.getHealth() <= this.getMaxHealth() * this.getEntityAttribute(VOLATILE_HEALTH_START_ATTRIB).getAttributeValue() && this.volatileCooldownTicks <= 0) {
                        this.setVolatile(true);
                        this.didTurnVolatileOnPlayer = true;
                        this.volatileReceivedDamage = 0.0F;
                        this.volatileCooldownTicks = this.getMaxVolatileCooldown() + this.world.rand.nextInt(this.getMaxVolatileCooldown()) + 20;
                        this.volatileTicks = 0;

                        TheBetweenlands.networkWrapper.sendToAllAround(new MessageWightVolatileParticles(this), new TargetPoint(this.dimension, this.posX, this.posY, this.posZ, 32));
                        this.world.playSound(null, this.posX, this.posY, this.posZ, SoundRegistry.WIGHT_ATTACK, SoundCategory.HOSTILE, 1.6F, 1.0F);
                    }
                } else if (this.didTurnVolatileOnPlayer && this.isVolatile() && !this.canPossess(this.getAttackTarget())) {
                    this.setVolatile(false);
                    this.didTurnVolatileOnPlayer = false;
                }
            }
        }

        if (this.isVolatile()) {
        	if(!this.world.isRemote) {
	        	if (this.volatileTicks < this.getEntityAttribute(VOLATILE_LENGTH_ATTRIB).getAttributeValue()) {
	            	this.volatileTicks++;
	
	                if (this.volatileTicks >= 20) {
	                    this.noClip = true;
	                }
	            } else {
	                if (!this.world.isRemote) {
	                    this.motionY -= 0.075D;
	
	                    this.fallDistance = 0;
	
	                    if (this.didTurnVolatileOnPlayer && this.onGround) {
	                        this.setVolatile(false);
	                        this.didTurnVolatileOnPlayer = false;
	                    }
	                }
	
	                this.noClip = false;
	            }
	
	            if (this.volatileTicks < 20) {
	                this.moveHelper.setMoveTo(this.posX, this.posY + 1.0D, this.posZ, 0.15D);
	            }
	
	            if (this.getAttackTarget() != null) {
	                EntityLivingBase attackTarget = this.getAttackTarget();
	
	                if (this.getRidingEntity() == null && this.getDistance(attackTarget) < 1.75D && this.canPossess(attackTarget)) {
	                    this.startRiding(attackTarget, true);
	                    this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(attackTarget));
	                }
	
	                if (this.getRidingEntity() == null) {
	                    double dx = attackTarget.posX - this.posX;
	                    double dz = attackTarget.posZ - this.posZ;
	                    double dy;
	                    if (attackTarget instanceof EntityLivingBase) {
	                        EntityLivingBase entitylivingbase = attackTarget;
	                        dy = entitylivingbase.posY + (double) entitylivingbase.getEyeHeight() - (this.posY + (double) this.getEyeHeight());
	                    } else {
	                        dy = (attackTarget.getEntityBoundingBox().minY + attackTarget.getEntityBoundingBox().maxY) / 2.0D - (this.posY + (double) this.getEyeHeight());
	                    }
	                    double dist = (double) MathHelper.sqrt(dx * dx + dz * dz);
	                    float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
	                    float pitch = (float) (-(Math.atan2(dy, dist) * 180.0D / Math.PI));
	                    this.setRotation(yaw, pitch);
	                    this.setRotationYawHead(yaw);
	                } else {
	                    this.setRotation(0, 0);
	                    this.setRotationYawHead(0);
	
	                    if (this.ticksExisted % 5 == 0 && this.canEntityBeSeen(this.getAttackTarget()) && !this.isWearingSkullMask(this.getAttackTarget())) {
	                        List<EntityVolatileSoul> existingSouls = this.world.getEntitiesWithinAABB(EntityVolatileSoul.class, this.getEntityBoundingBox().grow(16.0D, 16.0D, 16.0D));
	                        if (existingSouls.size() < 16) {
	                            EntityVolatileSoul soul = new EntityVolatileSoul(this.world);
	                            float mx = this.world.rand.nextFloat() - 0.5F;
	                            float my = this.world.rand.nextFloat() / 2.0F;
	                            float mz = this.world.rand.nextFloat() - 0.5F;
	                            Vec3d dir = new Vec3d(mx, my, mz).normalize();
	                            soul.setOwner(this.getUniqueID());
	                            soul.setLocationAndAngles(this.posX + dir.x * 0.5D, this.posY + dir.y * 1.5D, this.posZ + dir.z * 0.5D, 0, 0);
	                            soul.shoot(mx * 2.0D, my * 2.0D, mz * 2.0D, 1.0F, 1.0F);
	                            this.world.spawnEntity(soul);
	                        }
	                    }
	                }
	            }
	            
	            this.moveHelper = this.flightMoveHelper;
        	}

            if (this.world.isRemote && (this.getRidingEntity() == null || this.ticksExisted % 4 == 0)) {
                this.spawnVolatileParticles(false);
            }

            this.setSize(0.7F, 0.7F);
        } else {
        	if(!this.world.isRemote) {
	            this.noClip = false;
	            this.moveHelper = this.groundMoveHelper;
        	}
            
            this.setSize(0.7F, 2.2F);
        }

        this.lastHidingAnimationTicks = this.hidingAnimationTicks;
        if (this.isHiding()) {
            if (this.hidingAnimationTicks < 12)
                this.hidingAnimationTicks++;
        } else {
            if (this.hidingAnimationTicks > 0)
                this.hidingAnimationTicks--;
        }

        super.onUpdate();
    }

	public boolean isInTar() {
		//System.out.println("IN TAR!");
		return this.world.isMaterialInBB(this.getEntityBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), BLMaterialRegistry.TAR);
	}

	@Override
	protected boolean isMovementBlocked() {
		return super.isMovementBlocked() || isInTar() || getGrowTimer() < 40;
	}

	@Override
    public void fall(float distance, float damageMultiplier) {
        if (!this.isVolatile()) {
            super.fall(distance, damageMultiplier);
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        if (!this.isVolatile()) {
            super.updateFallState(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void travel(float strafe, float up, float forward) {
        if (this.isVolatile()) {
            //Use flight movement

            if (this.isInWater()) {
                this.moveRelative(strafe, up, forward, 0.02F);
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.800000011920929D;
                this.motionY *= 0.800000011920929D;
                this.motionZ *= 0.800000011920929D;
            } else if (this.isInLava()) {
                this.moveRelative(strafe,up, forward, 0.02F);
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            } else {
                float f = 0.91F;

                if (this.onGround) {
                    f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
                }

                float f1 = 0.16277136F / (f * f * f);
                this.moveRelative(strafe, up,  forward, this.onGround ? 0.1F * f1 : 0.02F);
                f = 0.91F;

                if (this.onGround) {
                    f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
                }

                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= (double) f;
                this.motionY *= (double) f;
                this.motionZ *= (double) f;
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.posX - this.prevPosX;
            double d0 = this.posZ - this.prevPosZ;
            float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

            if (f2 > 1.0F) {
                f2 = 1.0F;
            }

            this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        } else {
            //Use normal movement

            super.travel(strafe,up, forward);
        }
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        boolean isCreative = source instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect) source).getTrueSource() instanceof EntityPlayer && ((EntityPlayer) ((EntityDamageSourceIndirect) source).getTrueSource()).isCreative();
        return this.isHiding() && isCreative;
    }

    @Override
    public boolean canBePushed() {
        return !this.isHiding();
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LootTableRegistry.WIGHT;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (this.isVolatile() && source == DamageSource.IN_WALL) {
            return false;
        }
        float prevHealth = this.getHealth();
        boolean ret = super.attackEntityFrom(source, damage);
        float dealtDamage = prevHealth - this.getHealth();
        if (this.didTurnVolatileOnPlayer && this.isVolatile() && this.getRidingEntity() != null) {
            this.volatileReceivedDamage += dealtDamage;
            if (this.volatileReceivedDamage >= this.getEntityAttribute(VOLATILE_MAX_DAMAGE_ATTRIB).getAttributeValue()) {
                this.setVolatile(false);
                this.didTurnVolatileOnPlayer = false;
            }
        }
        if (this.getAttackTarget() != null && source instanceof EntityDamageSource && source.getTrueSource() == this.getAttackTarget()) {
            this.canTurnVolatileOnTarget = true;
        }
        return ret;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (this.isVolatile()) {
            return false;
        }
        if (super.attackEntityAsMob(entity)) {
            if (entity == this.getAttackTarget()) {
                this.canTurnVolatileOnTarget = true;
            }
            return true;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.WIGHT_MOAN;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundRegistry.WIGHT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.WIGHT_DEATH;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("volatileState", this.isVolatile());
        nbt.setInteger("volatileCooldown", this.volatileCooldownTicks);
        nbt.setInteger("volatileTicks", this.volatileTicks);
        nbt.setFloat("volatileReceivedDamage", this.volatileReceivedDamage);
        nbt.setBoolean("canTurnVolatileOnTarget", this.canTurnVolatileOnTarget);
        nbt.setBoolean("canTurnVolatile", this.canTurnVolatile);
        nbt.setBoolean("turnVolatileOnPlayer", this.didTurnVolatileOnPlayer);
        nbt.setInteger("grow_timer", getGrowTimer());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);

        if (nbt.hasKey("volatileState")) {
            this.setVolatile(nbt.getBoolean("volatileState"));
        }
        if (nbt.hasKey("turnVolatileOnPlayer")) {
            this.didTurnVolatileOnPlayer = nbt.getBoolean("turnVolatileOnPlayer");
        }
        if (nbt.hasKey("volatileCooldown")) {
            this.volatileCooldownTicks = nbt.getInteger("volatileCooldown");
        }
        if (nbt.hasKey("volatileTicks")) {
            this.volatileTicks = nbt.getInteger("volatileTicks");
        }
        if (nbt.hasKey("volatileReceivedDamage")) {
            this.volatileReceivedDamage = nbt.getFloat("volatileReceivedDamage");
        }
        if (nbt.hasKey("canTurnVolatileOnTarget")) {
            this.canTurnVolatileOnTarget = nbt.getBoolean("canTurnVolatileOnTarget");
        }
        if (nbt.hasKey("canTurnVolatile")) {
            this.canTurnVolatile = nbt.getBoolean("canTurnVolatile");
        }

		if(nbt.hasKey("grow_timer"))
			setGrowTimer(nbt.getInteger("grow_timer")); 
    }

    @SideOnly(Side.CLIENT)
    private void spawnVolatileParticles(boolean tarred) {
        final double radius = 0.3F;

        final double cx = this.posX;
        final double cy = this.posY + 0.35D;
        final double cz = this.posZ;

        for (int i = 0; i < 8; i++) {
            double px = this.world.rand.nextFloat() * 0.7F;
            double py = this.world.rand.nextFloat() * 0.7F;
            double pz = this.world.rand.nextFloat() * 0.7F;
            Vec3d vec = new Vec3d(px, py, pz).subtract(new Vec3d(0.35F, 0.35F, 0.35F)).normalize();
            px = cx + vec.x * radius;
            py = cy + vec.y * radius;
            pz = cz + vec.z * radius;
			if (tarred) {
				float tintChange = 1F / 40F * growCount;
				BLParticles.STEAM_PURIFIER.spawn(this.world, px, py + 0.25D, pz).setRBGColorF(tintChange, tintChange,
						tintChange);
			} else
				BLParticles.STEAM_PURIFIER.spawn(this.world, px, py, pz);
		}
    }

    public boolean isHiding() {
        return this.getDataManager().get(HIDING_STATE_DW);
    }

    public void setHiding(boolean hiding) {
        this.getDataManager().set(HIDING_STATE_DW, hiding);
    }

	public int getGrowTimer() {
		return dataManager.get(GROW_TIMER);
	}

	public void setGrowTimer(int timer) {
		dataManager.set(GROW_TIMER, timer);
	}

    public float getHidingAnimation(float partialTicks) {
        return (this.lastHidingAnimationTicks + (this.hidingAnimationTicks - this.lastHidingAnimationTicks) * partialTicks) / 12.0F;
    }

	public float getGrowthFactor(float partialTicks) {
		return prevGrowCount + (growCount - prevGrowCount) * partialTicks;
	}

    public boolean isVolatile() {
        return this.getDataManager().get(VOLATILE_STATE_DW);
    }

    public void setVolatile(boolean isVolatile) {
        this.dataManager.set(VOLATILE_STATE_DW, isVolatile);

        if (!isVolatile) {
            Entity ridingEntity = this.getRidingEntity();
            if (ridingEntity != null) {
                this.dismountRidingEntity();
                if(!this.world.isRemote) {
                	this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(ridingEntity));
                }
            }
        }
    }

    public int getMaxVolatileCooldown() {
        return (int) this.getEntityAttribute(VOLATILE_COOLDOWN_ATTRIB).getAttributeValue();
    }

    public boolean canPossess(EntityLivingBase entity) {
        if(entity instanceof EntitySwampHag) {
        	return true;
        }
        if(entity instanceof EntityPlayer) {
        	return !this.isWearingSkullMask(entity);
        }
        return false;
    }
    
    public boolean isWearingSkullMask(EntityLivingBase entity) {
    	if(entity instanceof EntityPlayer) {
        	ItemStack helmet = ((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        	if(!helmet.isEmpty() && helmet.getItem() == ItemRegistry.SKULL_MASK) {
        		return true;
        	}
        }
    	return false;
    }

    public void setCanTurnVolatile(boolean canTurnVolatile) {
        this.canTurnVolatile = canTurnVolatile;
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
