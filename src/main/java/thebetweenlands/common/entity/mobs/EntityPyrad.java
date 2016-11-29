package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIFlyRandomly;
import thebetweenlands.common.entity.ai.EntityAIMoveToDirect;
import thebetweenlands.common.entity.movement.FlightMoveHelper;
import thebetweenlands.common.registries.SoundRegistry;

public class EntityPyrad extends EntityMob implements IEntityBL {
	private static final DataParameter<Boolean> CHARGING = EntityDataManager.<Boolean>createKey(EntityBlaze.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> ACTIVE = EntityDataManager.<Boolean>createKey(EntityBlaze.class, DataSerializers.BOOLEAN);

	private List<EntityAIBase> activeTasks;
	private List<EntityAIBase> activeTargetTasks;

	private int glowTicks = 0;
	private int prevGlowTicks = 0;

	private int activeTicks = 0;
	private int prevActiveTicks = 0;

	private int hitTicks = 0;
	private int prevHitTicks = 0;

	public EntityPyrad(World worldIn) {
		super(worldIn);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.LAVA, 8.0F);
		this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.isImmuneToFire = true;
		this.experienceValue = 10;
		this.moveHelper = new FlightMoveHelper(this);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(CHARGING, false);
		this.dataManager.register(ACTIVE, false);
	}

	@Override
	protected void initEntityAI() {
		this.activeTasks = new ArrayList<EntityAIBase>();
		this.activeTargetTasks = new ArrayList<EntityAIBase>();

		this.activeTasks.add(new EntityAIMoveToDirect<EntityPyrad>(this, 0.1D) {
			@Override
			protected Vec3d getTarget() {
				EntityLivingBase target = this.entity.getAttackTarget();
				if(target != null) {
					BlockPos pos = new BlockPos(this.entity);
					int groundHeight = FlightMoveHelper.getGroundHeight(this.entity.worldObj, pos, 16, pos).getY();
					Vec3d dir = new Vec3d(target.posX - this.entity.posX, target.posY + 1 - this.entity.rand.nextFloat() * 0.3 - this.entity.posY - 1, target.posZ - this.entity.posZ);
					double dst = dir.lengthVector();
					if(dst > 10) {
						dir = dir.normalize();
						this.setSpeed(0.08D);
						return new Vec3d(this.entity.posX + dir.xCoord * (dst - 10), Math.min(this.entity.posY + dir.yCoord * (dst - 10), Math.max(groundHeight + 2, target.posY + 2)), this.entity.posZ + dir.zCoord * (dst - 10));
					} else if(dst < 5) {
						dir = dir.normalize();
						this.setSpeed(0.1D);
						return new Vec3d(this.entity.posX - dir.xCoord * 2, Math.min(this.entity.posY - dir.yCoord * 2, Math.max(groundHeight + (this.entity.isCharging() ? 6 : 2), target.posY + 2)), this.entity.posZ - dir.zCoord * 2);
					}
				}
				return null;
			}
		});
		this.activeTasks.add(new EntityAIFlyRandomly<EntityPyrad>(this) {
			@Override
			protected double getTargetX(Random rand, double distanceMultiplier) {
				return this.entity.posX + (double)((rand.nextFloat() * 2.0F - 1.0F) * 10.0F * distanceMultiplier);
			}

			@Override
			protected double getTargetY(Random rand, double distanceMultiplier) {
				return this.entity.posY + (rand.nextFloat() * 1.45D - 1.0D) * 4.0D * distanceMultiplier;
			}

			@Override
			protected double getTargetZ(Random rand, double distanceMultiplier) {
				return this.entity.posZ + (double)((rand.nextFloat() * 2.0F - 1.0F) * 10.0F * distanceMultiplier);
			}

			@Override
			protected double getFlightSpeed() {
				return 0.04D;
			}
		});
		this.activeTasks.add(new EntityPyrad.AIPyradAttack(this));
		this.activeTasks.add(new EntityAIMoveTowardsRestriction(this, 0.04D));
		this.activeTasks.add(new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.activeTasks.add(new EntityAILookIdle(this));

		this.activeTargetTasks.add(new EntityAIHurtByTarget(this, true));
		this.activeTargetTasks.add(new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);;
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.04D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(28.0D);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if(!this.isActive()) {
			return null;
		}
		return SoundRegistry.PYRAD_LIVING;
	}

	@Override
	protected SoundEvent getHurtSound() {
		return SoundRegistry.PYRAD_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.PYRAD_DEATH;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("active", this.isActive());

		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		this.setActive(nbt.getBoolean("active"));

		super.readEntityFromNBT(nbt);
	}

	@Override
	public void onLivingUpdate() {
		if (!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.6D;
		}

		if(!this.worldObj.isRemote && this.isInWater() && !this.isActive()) {
			this.setActive(true);
		}

		this.prevGlowTicks = this.glowTicks;
		if(this.isCharging() && this.glowTicks < 10) {
			this.glowTicks++;
		} else if(!this.isCharging() && this.glowTicks > 0) {
			this.glowTicks--;
		}

		this.prevActiveTicks = this.activeTicks;
		if(this.isActive() && this.activeTicks < 60) {
			this.activeTicks++;
		} else if(!this.isActive() && this.activeTicks > 0 && this.onGround) {
			this.activeTicks--;
		}

		this.prevHitTicks = this.hitTicks;
		if(this.hitTicks > 0) {
			this.hitTicks--;
		}

		if(!this.worldObj.isRemote && this.isActive() && this.getAttackTarget() == null && this.rand.nextInt(800) == 0) {
			this.setActive(false);
		}

		if (this.worldObj.isRemote && this.isActive()) {
			if(this.rand.nextInt(4) == 0) {
				ParticleArgs<?> args = ParticleArgs.get().withDataBuilder().setData(2, this).buildData();
				if(this.isCharging()) {
					args.withColor(0.9F, 0.35F, 0.1F, 1);
				} else {
					args.withColor(1F, 0.65F, 0.25F, 1);
				}
				BLParticles.LEAF_SWIRL.spawn(this.worldObj, this.posX, this.posY, this.posZ, args);
			}
			if(this.isCharging() || this.rand.nextInt(10) == 0) {
				ParticleArgs<?> args = ParticleArgs.get().withMotion((this.rand.nextFloat() - 0.5F) / 4.0F, (this.rand.nextFloat() - 0.5F) / 4.0F, (this.rand.nextFloat() - 0.5F) / 4.0F);
				if(this.isCharging()) {
					args.withColor(0.9F, 0.35F, 0.1F, 1);
					args.withData(60);
				} else {
					args.withColor(1F, 0.65F, 0.25F, 1);
				}
				BLParticles.WEEDWOOD_LEAF.spawn(this.worldObj, this.posX, this.posY + this.getEyeHeight(), this.posZ, args);
			}
		}

		if(this.isActive()) {
			this.renderYawOffset = this.rotationYaw = -((float) Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float) Math.PI / 1.0F;
		}

		super.onLivingUpdate();
	}

	public float getGlowTicks(float partialTicks) {
		return this.prevGlowTicks + (this.glowTicks - this.prevGlowTicks) * partialTicks;
	}

	public float getActiveTicks(float partialTicks) {
		return this.prevActiveTicks + (this.activeTicks - this.prevActiveTicks) * partialTicks;
	}

	public float getHitTicks(float partialTicks) {
		return this.prevHitTicks + (this.hitTicks - this.prevHitTicks) * partialTicks;
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if(!this.isActive()) {
			this.motionX = 0;
			this.motionZ = 0;
			this.motionY -= 0.1D;
		}

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
			float friction = 0.91F;

			if (this.onGround) {
				friction = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
			}

			float f1 = 0.16277136F / (friction * friction * friction);
			this.moveRelative(strafe, forward, this.onGround ? 0.1F * f1 : 0.02F);
			friction = 0.91F;

			if (this.onGround) {
				friction = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91F;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)friction;
			this.motionY *= (double)friction;
			this.motionZ *= (double)friction;
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
	}

	@Override
	public boolean isOnLadder() {
		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(!this.isActive()) {
			if(this.hitTicks <= 0) {
				if(!this.worldObj.isRemote && (this.rand.nextInt(12) == 0 || amount > 3.0F)) {
					this.setActive(true);
					return super.attackEntityFrom(source, amount);
				}
				for(int i = 0; i < 10; i++) {
					if(this.worldObj.isRemote) {
						ParticleArgs<?> args = ParticleArgs.get().withMotion((this.rand.nextFloat() - 0.5F) / 2.0F, (this.rand.nextFloat() - 0.5F) / 2.0F, (this.rand.nextFloat() - 0.5F) / 2.0F).withColor(1F, 0.65F, 0.25F, 1);
						BLParticles.WEEDWOOD_LEAF.spawn(this.worldObj, this.posX, this.posY + 0.8D, this.posZ, args);
					}
					this.playSound(SoundRegistry.PYRAD_HURT, 0.1F, 0.3F + this.rand.nextFloat() * 0.3F);
				}
				this.hitTicks = 20;
			}
			return false;
		} else {
			return super.attackEntityFrom(source, amount);
		}
	}

	@Override
	public void knockBack(Entity entityIn, float strenght, double xRatio, double zRatio) {
		if(this.isActive()) {
			super.knockBack(entityIn, strenght, xRatio, zRatio);
		}
	}

	@Override
	public boolean canBePushed() {
		return super.canBePushed() && this.isActive();
	}

	public boolean isCharging() {
		return this.getDataManager().get(CHARGING);
	}

	public void setCharging(boolean charging) {
		this.getDataManager().set(CHARGING, charging);
	}

	public boolean isActive() {
		return this.getDataManager().get(ACTIVE);
	}

	public void setActive(boolean active) {
		this.getDataManager().set(ACTIVE, active);

		if(!this.worldObj.isRemote) {
			if(active) {
				for(int i = 0; i < this.activeTasks.size(); i++) {
					this.tasks.addTask(i, this.activeTasks.get(i));
				}
				for(int i = 0; i < this.activeTargetTasks.size(); i++) {
					this.targetTasks.addTask(i, this.activeTargetTasks.get(i));
				}
			} else {
				for(EntityAIBase ai : this.activeTasks) {
					this.tasks.removeTask(ai);
				}
				for(EntityAIBase ai : this.activeTargetTasks) {
					this.targetTasks.removeTask(ai);
				}
			}
		}
	}

	static class AIPyradAttack extends EntityAIBase {
		private final EntityPyrad pyrad;
		private int attackStep;
		private int attackTime;

		public AIPyradAttack(EntityPyrad pyrad) {
			this.pyrad = pyrad;
		}

		@Override
		public boolean shouldExecute() {
			EntityLivingBase target = this.pyrad.getAttackTarget();
			return target != null && target.isEntityAlive();
		}

		@Override
		public void startExecuting() {
			this.attackStep = 0;
		}

		@Override
		public void resetTask() {
			this.pyrad.setCharging(false);
		}

		@Override
		public void updateTask() {
			--this.attackTime;
			EntityLivingBase target = this.pyrad.getAttackTarget();
			double distSq = this.pyrad.getDistanceSqToEntity(target);

			if (distSq < 4.0D) {
				if (this.attackTime <= 0) {
					this.attackTime = 20;
					this.pyrad.attackEntityAsMob(target);
				}

				this.pyrad.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 1.0D);
			} else if (distSq < 256.0D) {
				double dx = target.posX - this.pyrad.posX;
				double dy = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F) - (this.pyrad.posY + (double)(this.pyrad.height / 2.0F));
				double dz = target.posZ - this.pyrad.posZ;

				if (this.attackTime <= 0) {
					++this.attackStep;

					if (this.attackStep == 1) {
						this.attackTime = 20 + this.pyrad.rand.nextInt(40);
						this.pyrad.setCharging(true);
					} else if (this.attackStep <= 4) {
						this.attackTime = 6;
					} else {
						this.attackTime = 60 + this.pyrad.rand.nextInt(40);
						this.attackStep = 0;
						this.pyrad.setCharging(false);
					}

					if (this.attackStep > 1) {
						float f = MathHelper.sqrt_float(MathHelper.sqrt_double(distSq)) * 0.8F;
						this.pyrad.worldObj.playEvent((EntityPlayer)null, 1018, new BlockPos((int)this.pyrad.posX, (int)this.pyrad.posY, (int)this.pyrad.posZ), 0);

						for (int i = 0; i < this.pyrad.rand.nextInt(6) + 1; ++i) {
							EntityPyradFlame flame = new EntityPyradFlame(this.pyrad.worldObj, this.pyrad, dx + this.pyrad.getRNG().nextGaussian() * (double)f, dy, dz + this.pyrad.getRNG().nextGaussian() * (double)f);
							flame.posY = this.pyrad.posY + (double)(this.pyrad.height / 2.0F) + 0.5D;
							this.pyrad.worldObj.spawnEntityInWorld(flame);
						}
					}
				}

				this.pyrad.getLookHelper().setLookPositionWithEntity(target, 10.0F, 10.0F);
			}

			super.updateTask();
		}
	}
}