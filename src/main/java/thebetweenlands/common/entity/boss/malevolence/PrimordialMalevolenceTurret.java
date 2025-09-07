package thebetweenlands.common.entity.boss.malevolence;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PrimordialMalevolenceTurret extends Mob implements BLEntity, OwnableEntity {
	protected static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(PrimordialMalevolenceTurret.class, EntityDataSerializers.OPTIONAL_UUID);
	protected static final EntityDataAccessor<Optional<UUID>> TARGET = SynchedEntityData.defineId(PrimordialMalevolenceTurret.class, EntityDataSerializers.OPTIONAL_UUID);
	protected static final EntityDataAccessor<Boolean> DEFLECTION_STATE = SynchedEntityData.defineId(PrimordialMalevolenceTurret.class, EntityDataSerializers.BOOLEAN);

	private Entity cachedTarget;

	private boolean particlesSpawned = false;
	private int attackTicks = 0;
	private int attackDelay = 40;

	public PrimordialMalevolenceTurret(EntityType<? extends Mob> type, Level level) {
		super(type, level);
	}

	public PrimordialMalevolenceTurret(Level level, Entity source) {
		this(EntityRegistry.PRIMORDIAL_MALEVOLENCE_TURRET.get(), level);
		this.setOwner(source.getUUID());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(OWNER, Optional.empty());
		builder.define(TARGET, Optional.empty());
		builder.define(DEFLECTION_STATE, false);
	}

	public void setDeflectable(boolean deflectable) {
		this.getEntityData().set(DEFLECTION_STATE, deflectable);
	}

	public boolean isDeflectable() {
		return this.getEntityData().get(DEFLECTION_STATE);
	}

	public void setOwner(@Nullable UUID uuid) {
		this.getEntityData().set(OWNER, Optional.ofNullable(uuid));
	}

	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.getEntityData().get(OWNER).orElse(null);
	}

	public void setTurretTarget(@Nullable Entity entity) {
		this.getEntityData().set(TARGET, entity == null ? Optional.empty() : Optional.of(entity.getUUID()));
	}

	@Nullable
	public UUID getTurretTargetUUID() {
		return this.getEntityData().get(TARGET).orElse(null);
	}

	@Nullable
	public Entity getTurretTarget() {
		UUID uuid = this.getTurretTargetUUID();
		if (uuid == null) {
			this.cachedTarget = null;
		} else if (this.cachedTarget == null || !this.cachedTarget.isAlive() || !this.cachedTarget.getUUID().equals(uuid)) {
			this.cachedTarget = null;
			for (Entity entity : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(64.0D))) {
				if (entity.getUUID().equals(uuid)) {
					this.cachedTarget = entity;
					break;
				}
			}
		}
		return this.cachedTarget;
	}

	public int getAttackDelay() {
		return this.attackDelay;
	}

	public void setAttackDelay(int delay) {
		this.attackDelay = delay;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("attack_delay", this.attackDelay);
		compound.putBoolean("deflectable", this.isDeflectable());
		if (this.getOwnerUUID() != null) {
			compound.putUUID("owner", this.getOwnerUUID());
		}
		if (this.getTurretTargetUUID() != null) {
			compound.putUUID("target", this.getTurretTargetUUID());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.attackDelay = compound.getInt("attack_delay");
		this.setDeflectable(compound.getBoolean("deflectable"));
		if (compound.hasUUID("owner")) {
			this.getEntityData().set(OWNER, Optional.of(compound.getUUID("owner")));
		} else {
			this.getEntityData().set(OWNER, Optional.empty());
		}
		if (compound.hasUUID("target")) {
			this.getEntityData().set(TARGET, Optional.of(compound.getUUID("target")));
		} else {
			this.getEntityData().set(TARGET, Optional.empty());
		}
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide() && (this.level().getDifficulty() == Difficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isAlive()))) {
			this.discard();
			return;
		}
		this.setDeltaMovement(Vec3.ZERO);
		super.tick();

		if (this.level().isClientSide()) {
			if (!this.particlesSpawned) {
				this.particlesSpawned = true;
				for (int i = 0; i < 6; i++) {
					this.spawnVolatileParticles();
				}
			}
			if (this.level().getRandom().nextInt(6) == 0) {
				this.spawnFlameParticles();
			}
		}

		if (this.getTurretTarget() == null) {
			AABB searchBB = this.getBoundingBox().inflate(16.0D);
			List<Player> eligiblePlayers = this.level().getEntitiesOfClass(Player.class, searchBB);
			Player closest = null;
			for (Player player : eligiblePlayers) {
				if (closest == null || closest.distanceTo(this) > player.distanceTo(this))
					closest = player;
			}
			if (closest != null)
				this.setTurretTarget(closest);
		}

		if (this.getTurretTarget() != null) {
			this.lookAt(this.getTurretTarget(), 360.0F, 360.0F);
			this.attackTicks++;

			if (this.attackTicks > this.attackDelay) {
				if (!this.level().isClientSide()) {
					if (!this.isObstructedByBoss()) {
						Vec3 diff = this.position()
							.subtract(new Vec3(this.getTurretTarget().getBoundingBox().minX + (this.getTurretTarget().getBoundingBox().maxX - this.getTurretTarget().getBoundingBox().minX) / 2.0D,
								this.getTurretTarget().getBoundingBox().minY + (this.getTurretTarget().getBoundingBox().maxY - this.getTurretTarget().getBoundingBox().minY) / 2.0D,
								this.getTurretTarget().getBoundingBox().minZ + (this.getTurretTarget().getBoundingBox().maxZ - this.getTurretTarget().getBoundingBox().minZ) / 2.0D)).normalize();
						PrimordialMalevolenceProjectile bullet = new PrimordialMalevolenceProjectile(this.level(), this.getOwner());
						bullet.setDeflectable(this.isDeflectable());
						bullet.moveTo(this.getX(), this.getY(), this.getZ(), 0, 0);
						float speed = 0.5F;
						bullet.shoot(-diff.x, -diff.y, -diff.z, speed, 0.0F);
						this.level().addFreshEntity(bullet);
					}
				} else {
					for (int i = 0; i < 6; i++)
						this.spawnVolatileParticles();
				}
				this.discard();
			}
		} else {
			this.attackTicks = 0;
		}
	}

	public boolean isObstructedByBoss() {
		Vec3 ray = this.getLookAngle().normalize();
		Vec3 currentPos = this.position();
		Vec3 nextPos = currentPos.add(ray.x * 64.0D, ray.y * 64.0D, ray.z * 64.0D);
		Entity hitEntity = null;
		List<Entity> hitEntities = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(64, 64, 64), entity -> !(entity instanceof PrimordialMalevolenceTurret));
		double minDist = 0.0D;
		for (Entity entity : hitEntities) {
			if (entity.isPickable()) {
				float f = 0.65F / 2.0F + 0.1F + 0.1F;
				AABB entityBB = entity.getBoundingBox().inflate(f);
				Optional<Vec3> result = entityBB.clip(currentPos, nextPos);
				if (result.isPresent()) {
					double dst = currentPos.distanceTo(result.get());
					if (dst < minDist || minDist == 0.0D) {
						hitEntity = entity;
						minDist = dst;
					}
				}
			}
		}
		return hitEntity != null && (hitEntity instanceof PrimordialMalevolence || hitEntity == this.getOwner());
	}

	private void spawnFlameParticles() {
		//TheBetweenlands.createParticle(ParticleRegistry.GREEN_FLAME.get(), this.level(), this.getX(), this.getY() + 0.2F, this.getZ(), ParticleFactory.ParticleArgs.get().withMotion((this.level().getRandom().nextFloat() - 0.5F) / 5.0F, (this.level().getRandom().nextFloat() - 0.5F) / 5.0F, (this.level().getRandom().nextFloat() - 0.5F) / 5.0F));
	}

	private void spawnVolatileParticles() {
		final double radius = 0.3F;
		final double cx = this.getX();
		final double cy = this.getY() + 0.35D;
		final double cz = this.getZ();
		for (int i = 0; i < 8; i++) {
			double px = this.level().getRandom().nextFloat() * 0.7F;
			double py = this.level().getRandom().nextFloat() * 0.7F;
			double pz = this.level().getRandom().nextFloat() * 0.7F;
			Vec3 vec = new Vec3(px, py, pz).subtract(new Vec3(0.35F, 0.35F, 0.35F)).normalize();
			px = cx + vec.x * radius;
			py = cy + vec.y * radius;
			pz = cz + vec.z * radius;
			//TheBetweenlands.createParticle(ParticleRegistry.STEAM_PURIFIER.get(), this.level(), px, py, pz);
		}
	}

	@Override
	public void travel(Vec3 travelVector) {
		if (this.isInWater()) {
			this.moveRelative(0.02F, travelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().multiply(0.800000011920929D, 0.800000011920929D, 0.800000011920929D));
		} else {
			float friction = 0.91F;

			if (this.onGround()) {
				friction = this.level().getBlockState(BlockPos.containing(this.getX(), this.getBoundingBox().minY - 1, this.getZ())).getBlock().getFriction() * 0.91F;
			}

			float groundFriction = 0.16277136F / (friction * friction * friction);
			this.moveRelative(this.onGround() ? 0.1F * groundFriction : 0.02F, travelVector);
			friction = 0.91F;

			if (this.onGround()) {
				friction = this.level().getBlockState(BlockPos.containing(this.getX(), this.getBoundingBox().minY - 1, this.getZ())).getBlock().getFriction() * 0.91F;
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().multiply(friction, friction, friction));
		}
		this.calculateEntityAnimation(false);
	}
}
