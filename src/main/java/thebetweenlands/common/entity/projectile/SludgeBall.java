package thebetweenlands.common.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.common.entity.boss.DreadfulPeatMummy;
import thebetweenlands.common.entity.monster.PeatMummy;
import thebetweenlands.common.registries.EntityRegistry;

public class SludgeBall extends ThrowableProjectile {
	private int bounces = 0;
	private boolean breakBlocks;

	public SludgeBall(EntityType<? extends ThrowableProjectile> type, Level level) {
		super(type, level);
		this.breakBlocks = false;
	}

	public SludgeBall(Level level, LivingEntity owner, boolean breakBlocks) {
		this(EntityRegistry.SLUDGE_BALL.get(), level);
		this.setOwner(owner);
		this.breakBlocks = breakBlocks;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	public void tick() {
		Vec3 prevMovement = this.getDeltaMovement();
		Vec3 prevPos = this.position();
		this.move(MoverType.SELF, this.getDeltaMovement());
		if (!this.level().isClientSide() && new Vec3(prevPos.x() - this.getX(), prevPos.y() - this.getY(), prevPos.z() - this.getZ()).lengthSqr() < 0.001D && this.tickCount > 10) {
			this.explode();
		}
		this.setDeltaMovement(prevMovement);
		this.setPos(prevPos);

		super.tick();
	}

	@Override
	public void move(MoverType type, Vec3 pos) {
		if (this.tickCount < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}

		super.move(type, pos);
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);

		if (!this.level().isClientSide() && EventHooks.canEntityGrief(this.level(), this) && this.bounces == 0) {
			Entity owner = this.getOwner();

			if (owner instanceof LivingEntity living && this.getY() > owner.getBoundingBox().maxY && this.getDeltaMovement().y() > 0.1D) {
				BlockPos pos = result.getBlockPos();
				BlockState hitState = this.level().getBlockState(pos);
				float hardness = hitState.getDestroySpeed(this.level(), pos);

				if (!hitState.isAir() && hardness >= 0 && hardness <= 2.5F
					&& hitState.getBlock().canEntityDestroy(hitState, this.level(), pos, owner)
					&& EventHooks.onEntityDestroyBlock(living, pos, hitState)) {
					this.level().destroyBlock(pos, false);

					this.explode();
				}
			}
		}

		if (Math.abs(this.getDeltaMovement().y()) <= 0.001) {
			if (this.level().isClientSide()) {
				this.setDeltaMovement(Vec3.ZERO);
			} else {
				this.explode();
			}
		}

		if (result.getDirection().getAxis() == Direction.Axis.Y) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
			this.hurtMarked = true;
			this.bounces++;
			if (this.bounces >= 3) {
				if (this.level().isClientSide()) {
					this.setDeltaMovement(Vec3.ZERO);
				} else {
					this.explode();
				}
			} else {
				this.level().playSound(null, this.blockPosition(), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1, 0.9f);
				this.spawnBounceParticles(8);
			}
		} else if (result.getDirection().getAxis() == Direction.Axis.Z) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 1.0D, -0.9D));
			this.hurtMarked = true;
			this.bounces++;
			if (this.bounces >= 3) {
				if (this.level().isClientSide()) {
					this.setDeltaMovement(Vec3.ZERO);
				} else {
					this.explode();
				}
			}
		} else if (result.getDirection().getAxis() == Direction.Axis.X) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(-0.9D, 1.0D, 1.0D));
			this.hurtMarked = true;
			this.bounces++;
			if (this.bounces >= 3) {
				if (this.level().isClientSide()) {
					this.setDeltaMovement(Vec3.ZERO);
				} else {
					this.explode();
				}
			}
		}
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		super.onHitEntity(result);
		if (result.getEntity() != this.getOwner() && !(result.getEntity() instanceof SludgeBall) && !(result.getEntity() instanceof PartEntity<?>) && !(result.getEntity() instanceof PeatMummy) && !(result.getEntity() instanceof DreadfulPeatMummy)) {
			if (this.attackEntity(result.getEntity())) {
				explode();
			} else {
				this.setDeltaMovement(this.getDeltaMovement().multiply(-0.1D, -0.1D, -0.1D));
				this.bounces++;
			}
		}
	}

	private void explode() {
		if (!this.level().isClientSide()) {
			float radius = 3;
			AABB region = new AABB(this.getX() - radius, this.getY() - radius, this.getZ() - radius, this.getX() + radius, this.getY() + radius, this.getZ() + radius);
			List<Entity> entities = this.level().getEntities(this, region);
			double radiusSq = radius * radius;
			for (Entity entity : entities) {
				if (entity instanceof LivingEntity && !(entity instanceof PeatMummy) && !(entity instanceof DreadfulPeatMummy) && this.distanceToSqr(entity) < radiusSq) {
					this.attackEntity(entity);
				}
			}
			this.level().playSound(null, this.blockPosition(), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1, 0.5f);
			this.level().playSound(null, this.blockPosition(), SoundEvents.SLIME_SQUISH_SMALL, SoundSource.HOSTILE, 1, 0.5f);
			this.discard();
		} else {
			//TODO Better explosion particle effects
			this.spawnBounceParticles(20);
		}
	}

	private boolean attackEntity(Entity entity) {
		boolean attacked;
		Entity owner = this.getOwner();
		if (owner instanceof LivingEntity living) {
			attacked = entity.hurt(this.damageSources().mobProjectile(this, living), 8);
		} else {
			attacked = entity.hurt(this.damageSources().mobProjectile(this, null), 8);
		}
		if (!this.level().isClientSide() && attacked && entity instanceof LivingEntity living) {
			living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3));
		}
		return attacked;
	}

	@Override
	protected double getDefaultGravity() {
		return 0.08F;
	}

	private void spawnBounceParticles(int amount) {
		for (int i = 0; i <= amount; i++) {
			this.level().addParticle(ParticleTypes.ITEM_SLIME, this.getX() + (amount / 8) * (this.getRandom().nextFloat() - 0.5), this.getY() + 0.3, this.getZ() + (amount / 8) * (this.getRandom().nextFloat() - 0.5), 0, 0, 0);
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("bounces", this.bounces);
		tag.putBoolean("break_blocks", this.breakBlocks);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.bounces = tag.getInt("bounces");
		this.breakBlocks = tag.getBoolean("break_blocks");
	}
}
