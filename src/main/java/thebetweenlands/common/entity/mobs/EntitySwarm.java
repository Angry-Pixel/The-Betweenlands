package thebetweenlands.common.entity.mobs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.ISwarmedCapability;
import thebetweenlands.client.audio.EntitySound;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.common.entity.ai.EntityAIAttackOnCollide;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class EntitySwarm extends EntityClimberBase implements IMob {
	public static final DataParameter<Float> SWARM_SIZE = EntityDataManager.createKey(EntitySwarm.class, DataSerializers.FLOAT);

	@SideOnly(Side.CLIENT)
	private ISound idleSound;

	public EntitySwarm(World world) {
		this(world, 1);
	}

	public EntitySwarm(World world, float swarmSize) {
		super(world);
		this.setSwarmSize(swarmSize);
		this.experienceValue = 5;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(SWARM_SIZE, 1.0f);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new AIMerge(this, 50, 1.0D));
		this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false) {
			@Override
			protected double getAttackReachSqr(EntityLivingBase attackTarget) {
				return attackTarget.width + 0.15f;
			}
		});
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 1, false, false, null));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(24.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
	}

	public float getSwarmSize() {
		return this.dataManager.get(SWARM_SIZE);
	}

	public void setSwarmSize(float swarmSize) {
		this.dataManager.set(SWARM_SIZE, swarmSize);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		this.setSwarmSize(compound.getFloat("SwarmSize"));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		compound.setFloat("SwarmSize", this.getSwarmSize());
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		if(EntityAIAttackOnCollide.useStandardAttack(this, entityIn, new EntityDamageSource("bl.swarm", this))) {
			ISwarmedCapability cap = entityIn.getCapability(CapabilityRegistry.CAPABILITY_SWARMED, null);

			if(cap != null) {
				cap.setSwarmedStrength(cap.getSwarmedStrength() + 0.33f);

				cap.setDamage((float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());

				cap.setSwarmSource(this);
			}
			
			return true;
		}
		return false;
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.world.getDifficulty() != EnumDifficulty.PEACEFUL && super.getCanSpawnHere();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		if (entity instanceof EntityPlayerMP)
			AdvancementCriterionRegistry.INFESTED.trigger((EntityPlayerMP) entity);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(!this.world.isRemote) {
			if(world.getDifficulty() == EnumDifficulty.PEACEFUL)
				setDead();

			if(this.isBurning() || this.isInWater()) {
				if(this.getSwarmSize() > 0.1f) {
					this.setSwarmSize(Math.max(0.1f, this.getSwarmSize() - 0.005f));
				}

				if(this.isBurning() && this.rand.nextInt(10) == 0) {
					List<EntitySwarm> swarms = this.world.getEntitiesWithinAABB(EntitySwarm.class, this.getEntityBoundingBox().grow(1), s -> !s.isBurning());

					for(EntitySwarm swarm : swarms) {
						swarm.setFire(2);
					}
				}
			}

			float range = 3.25f;

			List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().grow(range));

			for(EntityPlayer player : players) {
				double dst = player.getDistance(this);

				if(dst < range && canEntityBeSeen(player)) {
					ISwarmedCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_SWARMED, null);

					if(cap != null) {
						cap.setSwarmedStrength(cap.getSwarmedStrength() + (1.0f - (float) dst / range) * 0.03f * MathHelper.clamp(this.getSwarmSize() * 1.75f, 0, 1));

						cap.setDamage((float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());

						cap.setSwarmSource(this);
					}
				}
			}
		} else {
			this.updateClient();
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundRegistry.CRUNCH;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if(source.isFireDamage()) {
			amount *= 2;
		}

		boolean attacked = super.attackEntityFrom(source, amount);

		if(this.isEntityAlive() && attacked && amount > 2 && (this.rand.nextFloat() * 16 < amount || this.getHealth() < this.getMaxHealth() * 0.25f)) {
			this.split();
		}

		return attacked;
	}

	protected boolean split() {
		float swarmSize = this.getSwarmSize();

		if(swarmSize > 0.3f) {
			float initialSwarmSize = swarmSize;

			float fraction = initialSwarmSize * 0.25f + initialSwarmSize * (this.rand.nextFloat() - 0.5f) * 0.05f;
			this.setSwarmSize(fraction);
			swarmSize -= fraction;

			for(int i = 0; i < 3; i++) {
				fraction = i == 2 ? swarmSize : (initialSwarmSize * 0.25f + initialSwarmSize * (this.rand.nextFloat() - 0.5f) * 0.05f);
				EntitySwarm swarm = new EntitySwarm(this.world, fraction);
				swarmSize -= fraction;

				swarm.setHealth(this.getHealth() * 0.66f);
				swarm.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);

				if(this.isBurning()) {
					swarm.setFire(40);
				}

				float mx = this.rand.nextFloat() - 0.5f;
				float mz = this.rand.nextFloat() - 0.5f;

				float len = MathHelper.sqrt(mx * mx + mz * mz);

				mx /= len;
				mz /= len;

				swarm.motionX = mx * 0.5f;
				swarm.motionY = 0.3f;
				swarm.motionZ = mz * 0.5f;

				this.world.spawnEntity(swarm);
			}

			this.setHealth(this.getHealth() * 0.5f);

			return true;
		}

		return false;
	}

	protected void mergeInto(EntitySwarm swarm) {
		swarm.setSwarmSize(swarm.getSwarmSize() + this.getSwarmSize());

		if(this.getHealth() < swarm.getHealth()) {
			swarm.setHealth((this.getHealth() / 0.66f + swarm.getHealth()) * 0.5f);
		}

		if(this.isBurning()) {
			swarm.setFire(2);
		}

		this.setDead();
	}

	@SideOnly(Side.CLIENT)
	protected void updateClient() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();

		if(view != null && view.getDistance(this) < 16 && !this.isInWater()) {
			SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
			if(this.idleSound == null || !handler.isSoundPlaying(this.idleSound)) {
				this.idleSound = new EntitySound<Entity>(SoundRegistry.SWARM_IDLE, SoundCategory.HOSTILE, this, e -> e.isEntityAlive(), 0.8f);
				handler.playSound(this.idleSound);
			}

			List<AxisAlignedBB> collisionBoxes = new ArrayList<>();

			for(BlockPos offsetPos : BlockPos.getAllInBoxMutable(
					new BlockPos(MathHelper.floor(this.posX - this.width - 2), MathHelper.floor(this.posY - 2), MathHelper.floor(this.posZ - this.width - 2)),
					new BlockPos(MathHelper.floor(this.posX + this.width + 2), MathHelper.floor(this.posY + this.height + 2), MathHelper.floor(this.posZ + this.width + 2)))) {
				IBlockState state = this.world.getBlockState(offsetPos);

				if(state.isFullCube()) {
					collisionBoxes.add(new AxisAlignedBB(offsetPos));
				}
			}

			float swarmSize = this.getSwarmSize();

			for(int i = 0; i < Math.max(2 - Minecraft.getMinecraft().gameSettings.particleSetting, 1) * swarmSize + 1; i++) {
				float rx = (this.world.rand.nextFloat() - 0.5f) * this.width;
				float ry = (this.world.rand.nextFloat() - 0.5f) * this.height;
				float rz = (this.world.rand.nextFloat() - 0.5f) * this.width;

				float len = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);

				rx /= len;
				ry /= len;
				rz /= len;

				len = 0.333f + this.world.rand.nextFloat() * 0.666f;

				double x = this.posX + this.motionX * 5 + rx * len * (this.width + 0.3f) * swarmSize * 0.5f;
				double y = this.posY + this.motionY * 5 - 0.15f * swarmSize + (this.height + 0.3f) * swarmSize * 0.5f + ry * len * (this.height + 0.3f) * swarmSize;
				double z = this.posZ + this.motionZ * 5 + rz * len * (this.width + 0.3f) * swarmSize * 0.5f;

				if(this.isBurning() && this.rand.nextInt(3) == 0) {
					this.world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, 0, 0, 0);
				}

				if(this.rand.nextInt(8) == 0) {
					if(this.rand.nextInt(3) == 0) {
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.FLYING_SWARM_EMISSIVE.create(this.world, x, y, z));
					} else {
						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.FLY.create(world, x, y, z, ParticleArgs.get().withScale(0.15F * world.rand.nextFloat() + 0.25F).withData(40, 0.01F, 0.0025F, false)));
					}
				} else {
					AxisAlignedBB particle = new AxisAlignedBB(x - 0.01f, y - 0.01f, z - 0.01f, x + 0.01f, y + 0.01f, z + 0.01f);

					double closestDst = 1;
					double closestDX = 0;
					double closestDY = 0;
					double closestDZ = 0;

					for(AxisAlignedBB box : collisionBoxes) {
						double dx1 = box.calculateXOffset(particle, -1);
						double dy1 = box.calculateYOffset(particle, -1);
						double dz1 = box.calculateZOffset(particle, -1);
						double dx2 = box.calculateXOffset(particle, 1);
						double dy2 = box.calculateYOffset(particle, 1);
						double dz2 = box.calculateZOffset(particle, 1);

						if(Math.abs(dx1) < closestDst) {
							closestDst = Math.abs(dx1);
							closestDX = dx1;
							closestDY = 0;
							closestDZ = 0;
						}

						if(Math.abs(dy1) < closestDst) {
							closestDst = Math.abs(dy1);
							closestDX = 0;
							closestDY = dy1;
							closestDZ = 0;
						}

						if(Math.abs(dz1) < closestDst) {
							closestDst = Math.abs(dz1);
							closestDX = 0;
							closestDY = 0;
							closestDZ = dz1;
						}

						if(Math.abs(dx2) < closestDst) {
							closestDst = Math.abs(dx2);
							closestDX = dx2;
							closestDY = 0;
							closestDZ = 0;
						}

						if(Math.abs(dy2) < closestDst) {
							closestDst = Math.abs(dy2);
							closestDX = 0;
							closestDY = dy2;
							closestDZ = 0;
						}

						if(Math.abs(dz2) < closestDst) {
							closestDst = Math.abs(dz2);
							closestDX = 0;
							closestDY = 0;
							closestDZ = dz2;
						}
					}

					if(closestDst < 1) {
						x += closestDX - Math.signum(closestDX) * 0.01f;
						y += closestDY - Math.signum(closestDY) * 0.01f;
						z += closestDZ - Math.signum(closestDZ) * 0.01f;

						double ox = 1 - Math.abs(Math.signum(closestDX));
						double oy = 1 - Math.abs(Math.signum(closestDY));
						double oz = 1 - Math.abs(Math.signum(closestDZ));

						BLParticles variant;
						if(this.rand.nextInt(6) == 0) {
							variant = BLParticles.SWARM_EMISSIVE;
						} else {
							variant = BLParticles.SWARM;
						}

						BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, variant.create(this.world, x, y, z, 
								ParticleArgs.get()
								.withMotion((this.world.rand.nextFloat() - 0.5f) * 0.05f * ox, (this.world.rand.nextFloat() - 0.5f) * 0.05f * oy, (this.world.rand.nextFloat() - 0.5f) * 0.05f * oz)
								.withScale(0.25f)
								.withData(EnumFacing.getFacingFromVector((float) -closestDX, (float) -closestDY, (float) -closestDZ), 40, this.getPositionVector(), (Supplier<Vec3d>) () -> this.getPositionVector())));
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	public static class AIMerge extends EntityAIBase {
		private final EntitySwarm entity;
		private int delay;
		private double speedTowardsTarget;

		private EntitySwarm leader;
		private int delayCounter;
		private int failedPathFindingPenalty = 0;

		public AIMerge(EntitySwarm entity, int delay, double speed) {
			this.entity = entity;
			this.delay = delay;
			this.speedTowardsTarget = speed;
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			if(this.entity.getSwarmSize() < 0.9f && this.delay-- <= 0) {
				EntitySwarm leader = this.findLeader();

				if(leader != null && leader != this.entity) {
					this.leader = leader;
					return true;
				}
			}
			return false;
		}

		@Nullable
		private EntitySwarm findLeader() {
			List<EntitySwarm> swarms = this.entity.world.getEntitiesWithinAABB(EntitySwarm.class, this.entity.getEntityBoundingBox().grow(8));

			int minId = Integer.MAX_VALUE;
			EntitySwarm leader = null;

			for(EntitySwarm swarm : swarms) {
				if(swarm.getEntityId() < minId && swarm.getSwarmSize() + this.entity.getSwarmSize() <= 1) {
					minId = swarm.getEntityId();
					leader = swarm;
				}
			}

			return leader;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return this.leader != null && this.leader.isEntityAlive() && this.leader.getSwarmSize() + this.entity.getSwarmSize() <= 1;
		}

		@Override
		public void resetTask() {
			this.leader = null;
			this.delayCounter = 0;
		}

		@Override
		public void updateTask() {
			if(this.leader != null) {
				if(this.leader.getDistance(this.entity) < 1) {
					this.entity.mergeInto(this.leader);
				} else if(--this.delayCounter <= 0) {
					this.delayCounter = 4 + this.entity.getRNG().nextInt(7);

					double dstSq = this.entity.getDistanceSq(this.leader.posX, this.leader.getEntityBoundingBox().minY, this.leader.posZ);

					if(dstSq > 1024.0D) {
						this.delayCounter += 10;
					} else if (dstSq > 256.0D) {
						this.delayCounter += 5;
					}

					if(!this.entity.getNavigator().tryMoveToEntityLiving(this.leader, this.speedTowardsTarget)) {
						this.delayCounter += 15;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onKnockback(LivingKnockBackEvent event) {
		if(event.getOriginalAttacker() instanceof EntitySwarm) {
			event.setCanceled(true);
		}
	}
}
