package thebetweenlands.common.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.registries.DamageTypeRegistry;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.UUID;

public class FlameJet extends Entity implements OwnableEntity {

	@Nullable
	private UUID owner;

	public FlameJet(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	public FlameJet(Level level, LivingEntity owner) {
		super(EntityRegistry.FLAME_JET.get(), level);
		this.owner = owner.getUUID();
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide()) {
			if (this.tickCount > 20)
				this.discard();
		} else {
			if (this.tickCount == 1) {
				this.spawnFlameJetParticles();
			}
		}
	}

	private void spawnFlameJetParticles() {
		for (double yy = this.getY(); yy < this.getY() + 2.0D; yy += 0.5D) {
			double d0 = this.getX() - 0.075F;
			double d1 = yy;
			double d2 = this.getZ() - 0.075F;
			double d3 = this.getX() + 0.075F;
			double d4 = this.getZ() + 0.075F;
			double d5 = this.getX();
			double d6 = yy + 0.25F;
			double d7 = this.getZ();
			this.level().addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.01D, 0.0D);
			this.level().addParticle(ParticleTypes.FLAME, d0, d1, d4, 0.0D, 0.01D, 0.0D);
			this.level().addParticle(ParticleTypes.FLAME, d3, d1, d2, 0.0D, 0.01D, 0.0D);
			this.level().addParticle(ParticleTypes.FLAME, d3, d1, d4, 0.0D, 0.01D, 0.0D);
			this.level().addParticle(ParticleTypes.FLAME, d5, d6, d7, 0.0D, 0.01D, 0.0D);
		}
	}

	@Override
	public void push(Entity entity) {
		if (!this.level().isClientSide()) {
			if (entity.getBoundingBox().intersects(this.getBoundingBox())) {
				if (entity instanceof LivingEntity) {
					if (!entity.fireImmune()) {
						boolean fire = entity.hurt(this.damageSources().source(DamageTypeRegistry.FLAME_JET, this, this.getOwner()), 5.0F);
						if (fire) entity.igniteForSeconds(5);
					} else {
						if (entity != this.getOwner()) entity.hurt(this.damageSources().mobProjectile(this, this.getOwner()), 2.0F);
					}
				}
			}
			this.discard();
		}
	}

	@Override
	public boolean isInvulnerable() {
		return true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {

	}

	@Override
	public UUID getOwnerUUID() {
		return this.owner;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}
}
