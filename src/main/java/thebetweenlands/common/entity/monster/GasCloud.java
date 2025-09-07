package thebetweenlands.common.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.capabilities.Capabilities;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.EntityAIFlyRandomly;
import thebetweenlands.common.entity.movement.BLFlightMoveControl;
import thebetweenlands.common.registries.*;

import java.util.List;

public class GasCloud extends Monster implements BLEntity {

	private static final EntityDataAccessor<Integer> GAS_CLOUD_COLOR = SynchedEntityData.defineId(GasCloud.class, EntityDataSerializers.INT);

	protected double aboveLayer = 6.0D;
	protected int targetBlockedTicks = 0;

	public GasCloud(EntityType<? extends Monster> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = false;
		this.noCulling = true;
		this.moveControl = new BLFlightMoveControl(this) {
			@Override
			protected boolean isNotColliding(double x, double y, double z, double step) {
				double stepX = (x - this.mob.getX()) / step;
				double stepY = (y - this.mob.getY()) / step;
				double stepZ = (z - this.mob.getZ()) / step;

				double cx = this.mob.getX();
				double cy = this.mob.getY();
				double cz = this.mob.getZ();

				boolean canPassSolidBlocks = this.mob.getTarget() != null;

				BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

				for (int i = 1; (double) i < step; ++i) {
					cx += stepX;
					cy += stepY;
					cz += stepZ;

					checkPos.set(cx, cy, cz);

					if (this.mob.level().isLoaded(checkPos)) {
						BlockState state = this.mob.level().getBlockState(checkPos);

						if ((!canPassSolidBlocks && state.isSolidRender(this.mob.level(), checkPos)) || state.liquid()) {
							return false;
						}
					} else {
						return false;
					}
				}

				return true;
			}
		};
		this.setPathfindingMalus(PathType.WATER, -8.0F);
		this.setPathfindingMalus(PathType.BLOCKED, -8.0F);
		this.setPathfindingMalus(PathType.OPEN, 8.0F);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new EntityAIFlyRandomly<>(this) {
			@Override
			protected double getTargetY(RandomSource rand, double distanceMultiplier) {
				if (this.entity.getY() <= 0.0D) {
					return this.entity.getY() + 16.0F;
				}

				int worldHeight = 0;

				BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

				for (int yo = 0; yo < Mth.ceil(GasCloud.this.aboveLayer); yo++) {
					checkPos.set(this.entity.getX(), this.entity.getY() - yo, this.entity.getZ());

					if (!this.entity.level().isLoaded(checkPos))
						return this.entity.getY();

					if (!this.entity.level().isEmptyBlock(checkPos)) {
						worldHeight = checkPos.getY();
						break;
					}
				}

				if (this.entity.getY() > worldHeight + GasCloud.this.aboveLayer) {
					return this.entity.getY() + (-rand.nextFloat() * 2.0F) * 16.0F * distanceMultiplier;
				} else {
					float rndFloat = rand.nextFloat() * 2.0F - 1.0F;
					if (rndFloat > 0.0D) {
						double maxRange = worldHeight + GasCloud.this.aboveLayer - this.entity.getY();
						return this.entity.getY() + (-rand.nextFloat() * 2.0F) * maxRange * distanceMultiplier;
					} else {
						return this.entity.getY() + (rand.nextFloat() * 2.0F - 1.0F) * 16.0F * distanceMultiplier;
					}
				}
			}

			@Override
			protected double getFlightSpeed() {
				return 0.3D;
			}
		});
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MOVEMENT_SPEED, 0.065D)
			.add(Attributes.MAX_HEALTH, 16.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(GAS_CLOUD_COLOR, 0x68c4b3);
	}

	public int getGasColor() {
		return this.getEntityData().get(GAS_CLOUD_COLOR);
	}

	/**
	 * Porting: BatchedParticleRenderer currently not ported.
	 * TODO: evaluate performance of standard particle rendering and whether BatchedParticleRenderer should be added
	 *
	 * @param strongMotion
	 */
	private void spawnCloudParticle(boolean strongMotion) {
		if (strongMotion) {
			double x = this.xo + this.xxa + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double y = this.yo + this.getEyeHeight() / 2.0D + this.yya + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double z = this.zo + this.zza + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			int color = this.getGasColor();

			TheBetweenlands.createParticle(ParticleRegistry.GAS_CLOUD.get(), this.level(), x, y, z,
				ParticleFactory.ParticleArgs.get()
					.withMotion((this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F)
					.withColor(color));

			//TheBetweenlands.createParticle(ParticleRegistry.GAS_CLOUD_HAZE.get(), this.level(), x, y, z,
			//	ParticleFactory.ParticleArgs.get()
			//		.withMotion((this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F, (this.random.nextFloat() - 0.5F) * this.random.nextFloat() * 0.25F)
			//		.withColor(color));

			//BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
			//BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, particle);
		} else {
			double x = this.xo + this.xxa + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double y = this.yo + this.getEyeHeight() / 2.0D + this.yya + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double z = this.zo + this.zza + (this.level().random.nextFloat() - 0.5F) / 2.0F;
			double mx = this.xxa + (this.level().random.nextFloat() - 0.5F) / 16.0F;
			double my = this.yya + (this.level().random.nextFloat() - 0.5F) / 16.0F;
			double mz = this.zza + (this.level().random.nextFloat() - 0.5F) / 16.0F;
			int color = this.getGasColor();

			TheBetweenlands.createParticle(ParticleRegistry.GAS_CLOUD.get(), this.level(), x, y, z,
				ParticleFactory.ParticleArgs.get()
					.withMotion(mx, my, mz)
					.withColor(color));

			//TheBetweenlands.createParticle(ParticleRegistry.GAS_CLOUD_HAZE.get(), this.level(), x, y, z,
			//	ParticleFactory.ParticleArgs.get()
			//		.withMotion(mx, my, mz)
			//		.withColor(color));

			//BatchedParticleRenderer.INSTANCE.addParticle(this.particleBatch, particle);
			//BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.GAS_CLOUDS_HEAT_HAZE, particle);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			this.spawnCloudParticle(false);
			this.updateParticleBatch();
		}

		if (this.isInWater()) {
			this.moveControl.setWantedPosition(this.xo, this.yo + 1.0D, this.zo, 1.0D);
		} else {
			if (this.getTarget() != null) {
				this.moveControl.setWantedPosition(this.getTarget().xo, this.getTarget().yo + this.getTarget().getEyeHeight(), this.getTarget().zo, 1.0D);
			}
		}

		if (!this.level().isClientSide() && this.isAlive()) {
			List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.5D, 0.5D, 0.5D));
			for (LivingEntity target : targets) {
				if (!(target instanceof BLEntity)) {
					target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
					if (target.tickCount % 10 == 0) {
						target.hurt(this.damageSources().source(DamageTypeRegistry.SUFFOCATION), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
					}
				}
			}
		}
	}

	private void updateParticleBatch() {
		//BatchedParticleRenderer.INSTANCE.updateBatch(this.particleBatch);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (!source.is(DamageTypes.IN_WALL)) {
			if (source.getDirectEntity() instanceof Player player) {

				ItemStack held = player.getMainHandItem();

				if (!held.isEmpty() && held.getCapability(Capabilities.FluidHandler.ITEM) != null && held.getCapability(Capabilities.FluidHandler.ITEM).getFluidInTank(0).isEmpty()) {
					if (this.level().isClientSide()) {
						for (int i = 0; i < 10; i++) {
							this.level().addParticle(ParticleTypes.CRIT,
								this.getX() + this.getDeltaMovement().x(),
								this.getY() + this.getDeltaMovement().y() + this.getBbHeight() * 0.5F,
								this.getZ() + this.getDeltaMovement().z(),
								-this.getDeltaMovement().x() + (this.getRandom().nextFloat() - 0.5F),
								-this.getDeltaMovement().y() + 0.2D + (this.getRandom().nextFloat() - 0.5F),
								-this.getDeltaMovement().z() + (this.getRandom().nextFloat() - 0.5F));
						}
					}

					if (super.hurt(source, amount * 3.0f)) {
						//TODO add fluid vials
//						if (!this.level().isClientSide()) {
//							if (!this.isAlive()) {
//								held.shrink(1);
//								held.getCapability(Capabilities.FluidHandler.ITEM).fill(new FluidStack(FluidRegistry.SHALLOWBREATH_STILL, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE);
//								ItemHandlerHelper.giveItemToPlayer(player, ItemRegistry.DENTROTHYST_FLUID_VIAL.withFluid(held.getItemDamage() == 2 ? 1 : 0, FluidRegistry.SHALLOWBREATH_STILL));
//							}
//
//							this.level().playSound(null, player.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
//						}

						return true;
					}

					return false;
				}
			}
			return super.hurt(source, amount);
		}
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.GAS_CLOUD_LIVING.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.GAS_CLOUD_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.GAS_CLOUD_DEATH.get();
	}

	@Override
	protected void tickDeath() {
		this.deathTime++;

		if (this.level().isClientSide()) {
			for (int i = 0; i < 6; i++) {
				this.spawnCloudParticle(true);
			}
		}

		if (this.deathTime >= 80 && !this.level().isClientSide() && !this.isRemoved()) {
			this.level().broadcastEntityEvent(this, (byte) 60);
			this.remove(RemovalReason.KILLED);
		}
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader level) {
		return 0.5F;
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 1;
	}
}