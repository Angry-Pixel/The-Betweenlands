package thebetweenlands.common.entity.mobs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.ai.EntityAIMoveToDirect;
import thebetweenlands.common.entity.ai.EntityAITargetNonSneaking;
import thebetweenlands.common.entity.ai.EntityAIWightAttack;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.ItemRegistry;

public class EntityWight extends EntityMob implements IEntityBL {

	public static final IAttribute VOLATILE_HEALTH_START_ATTRIB = (new RangedAttribute(null, "bl.volatileHealthStart", 1.0D, 0.0D, 1.0D)).setDescription("Volatile Health Percentage Start");
	public static final IAttribute VOLATILE_COOLDOWN_ATTRIB = (new RangedAttribute(null, "bl.volatileCooldown", 400.0D, 10.0D, Integer.MAX_VALUE)).setDescription("Volatile Cooldown");
	public static final IAttribute VOLATILE_FLIGHT_SPEED_ATTRIB = (new RangedAttribute(null, "bl.volatileFlightSpeed", 0.25D, 0.0D, 5.0D)).setDescription("Volatile Flight Speed");
	public static final IAttribute VOLATILE_LENGTH_ATTRIB = (new RangedAttribute(null, "bl.volatileLength", 600.0D, 0.0D, Integer.MAX_VALUE)).setDescription("Volatile Length");
	public static final IAttribute VOLATILE_MAX_DAMAGE_ATTRIB = (new RangedAttribute(null, "bl.volatileMaxDamage", 20.0D, 0.0D, Double.MAX_VALUE)).setDescription("Volatile Max Damage");

	protected static final DataParameter<Boolean> HIDING_STATE_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> VOLATILE_STATE_DW = EntityDataManager.createKey(EntityWight.class, DataSerializers.BOOLEAN);

	private int hidingAnimationTicks = 0;
	private int lastHidingAnimationTicks = 0;

	private int volatileCooldownTicks = (int) VOLATILE_COOLDOWN_ATTRIB.getDefaultValue() / 2 + 20;
	private int volatileTicks = 0;
	private float volatileReceivedDamage = 0.0F;

	private boolean canTurnVolatileOnTarget = false;

	public EntityWight(World world) {
		super(world);
		setSize(0.7F, 2.2F);
		this.setPathPriority(PathNodeType.WATER, 0.2F);
	}

	@Override
	protected void initEntityAI() {
		this.targetTasks.addTask(0, new EntityAITargetNonSneaking(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));

		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIWightAttack(this, 0.5D, false));
		this.tasks.addTask(2, new EntityAIMoveToDirect<EntityWight>(this, 0.1D) {
			@Override
			protected Vec3d getTarget() {
				if(this.entity.volatileTicks >= 20) {
					EntityLivingBase target = this.entity.getAttackTarget();
					if(target != null) {
						return new Vec3d(target.posX, target.posY + target.getEyeHeight() / 2.0D, target.posZ);
					}
				}
				return null;
			}
		});
		this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(5, new EntityAIWander(this, 0.3D));
		this.tasks.addTask(9, new EntityAIFlyRandomly<EntityWight>(this) {
			@Override
			public boolean shouldExecute() {
				return this.entity.isVolatile() && this.entity.volatileTicks >= 20 && this.entity.getAttackTarget() == null && super.shouldExecute();
			}

			@Override
			protected double getFlightSpeed() {
				return 0.04D;
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
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
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
		super.onUpdate();

		if(!this.worldObj.isRemote) {
			if(this.getAttackTarget() == null) {
				this.setHiding(true);

				this.canTurnVolatileOnTarget = false;
			} else {
				this.setHiding(false);

				if (!this.isVolatile() && this.canPossess(this.getAttackTarget()) && this.canTurnVolatileOnTarget) {
					if (this.volatileCooldownTicks > 0)
						this.volatileCooldownTicks--;

					if (this.getHealth() <= this.getMaxHealth() * this.getEntityAttribute(VOLATILE_HEALTH_START_ATTRIB).getAttributeValue() && this.volatileCooldownTicks <= 0) {
						this.setVolatile(true);
						this.volatileCooldownTicks = this.getMaxVolatileCooldown() + this.worldObj.rand.nextInt(this.getMaxVolatileCooldown()) + 20;
						this.volatileTicks = 0;
					}
				} else if (this.isVolatile() && !this.canPossess(this.getAttackTarget())) {
					this.setVolatile(false);
				}
			}
		}

		if(this.isVolatile()) {
			if (this.volatileTicks < this.getEntityAttribute(VOLATILE_LENGTH_ATTRIB).getAttributeValue()) {
				this.volatileTicks++;
			} else if (!this.worldObj.isRemote) {
				this.motionY -= 0.075D;

				this.fallDistance = 0;

				if (this.onGround) {
					this.setVolatile(false);
				}
			}

			if (this.volatileTicks < 20) {
				this.moveHelper.setMoveTo(this.posX, this.posY + 1.0D, this.posZ, 0.15D);
			}

			if(this.getAttackTarget() != null) {
				EntityLivingBase attackTarget = this.getAttackTarget();

				if(this.getDistanceToEntity(attackTarget) < 1.0D) {
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
					double dist = (double) MathHelper.sqrt_double(dx * dx + dz * dz);
					float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
					float pitch = (float) (-(Math.atan2(dy, dist) * 180.0D / Math.PI));
					this.setRotation(yaw, pitch);
					this.setRotationYawHead(yaw);
				} else {
					this.setRotation(0, 0);
					this.setRotationYawHead(0);
				}
			}

			this.setSize(0.7F, 0.7F);
		} else {
			this.setSize(0.7F, 2.2F);
		}

		this.lastHidingAnimationTicks = this.hidingAnimationTicks;
		if(this.isHiding()) {
			if(this.hidingAnimationTicks < 12)
				this.hidingAnimationTicks++;
		} else {
			if(this.hidingAnimationTicks > 0)
				this.hidingAnimationTicks--;
		}
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		if(!this.isVolatile()) {
			super.fall(distance, damageMultiplier);
		}
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
		if(!this.isVolatile()) {
			super.updateFallState(y, onGroundIn, state, pos);
		}
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if(this.isVolatile()) {
			//Use flight movement

			if (this.isInWater()) {
				this.moveRelative(strafe, forward, 0.02F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.800000011920929D;
				this.motionY *= 0.800000011920929D;
				this.motionZ *= 0.800000011920929D;
			} else if (this.isInLava()) {
				this.moveRelative(strafe, forward, 0.02F);
				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			} else {
				float f = 0.91F;

				if (this.onGround) {
					f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
				}

				float f1 = 0.16277136F / (f * f * f);
				this.moveRelative(strafe, forward, this.onGround ? 0.1F * f1 : 0.02F);
				f = 0.91F;

				if (this.onGround) {
					f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.motionX *= (double)f;
				this.motionY *= (double)f;
				this.motionZ *= (double)f;
			}

			this.prevLimbSwingAmount = this.limbSwingAmount;
			double d1 = this.posX - this.prevPosX;
			double d0 = this.posZ - this.prevPosZ;
			float f2 = MathHelper.sqrt_double(d1 * d1 + d0 * d0) * 4.0F;

			if (f2 > 1.0F) {
				f2 = 1.0F;
			}

			this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
			this.limbSwing += this.limbSwingAmount;
		} else {
			//Use normal movement

			super.moveEntityWithHeading(strafe, forward);
		}
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return this.isHiding();
	}

	@Override
	public boolean canBePushed() {
		return !this.isHiding();
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		entityDropItem(new ItemStack(ItemRegistry.WIGHT_HEART), 0F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (this.isVolatile() && source == DamageSource.inWall) {
			return false;
		}
		float prevHealth = this.getHealth();
		boolean ret = super.attackEntityFrom(source, damage);
		float dealtDamage = prevHealth - this.getHealth();
		if (this.isVolatile() && this.getRidingEntity() != null) {
			this.volatileReceivedDamage += dealtDamage;
			if (this.volatileReceivedDamage >= this.getEntityAttribute(VOLATILE_MAX_DAMAGE_ATTRIB).getAttributeValue()) {
				this.setVolatile(false);
			}
		}
		if (this.getAttackTarget() != null && source instanceof EntityDamageSource && source.getEntity() == this.getAttackTarget())
			this.canTurnVolatileOnTarget = true;
		return ret;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if (this.isVolatile()) {
			return false;
		}
		if (super.attackEntityAsMob(entity)) {
			if (entity == this.getAttackTarget())
				this.canTurnVolatileOnTarget = true;
			return true;
		}
		return false;
	}

	public void setHiding(boolean hiding) {
		this.getDataManager().set(HIDING_STATE_DW, hiding);
	}

	public boolean isHiding() {
		return this.getDataManager().get(HIDING_STATE_DW);
	}

	public float getHidingAnimation(float partialTicks) {
		return (this.lastHidingAnimationTicks + (this.hidingAnimationTicks - this.lastHidingAnimationTicks) * partialTicks) / 12.0F; 
	}

	public boolean isVolatile() {
		return this.getDataManager().get(VOLATILE_STATE_DW);
	}

	public void setVolatile(boolean isVolatile) {
		this.dataManager.set(VOLATILE_STATE_DW, isVolatile);
		this.volatileTicks = 0;
		this.volatileReceivedDamage = 0.0F;

		if(isVolatile) {
			this.moveHelper = new FlightMoveHelper(this) {
				@Override
				protected double getFlightSpeed() {
					return 0.1D;
				}
			};
		} else {
			Entity ridingEntity = this.getRidingEntity();
			if(ridingEntity != null) {
				this.dismountRidingEntity();
				this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(ridingEntity));
			}
			this.moveHelper = new EntityMoveHelper(this);
		}
	}

	public int getMaxVolatileCooldown() {
		return (int) this.getEntityAttribute(VOLATILE_COOLDOWN_ATTRIB).getAttributeValue();
	}

	public boolean canPossess(EntityLivingBase entity) {
		return entity instanceof EntityPlayer && (((EntityPlayer) entity).inventory.getStackInSlot(103) == null || ((EntityPlayer) entity).inventory.getStackInSlot(103).getItem() == ItemRegistry.SKULL_MASK);
	}
}
