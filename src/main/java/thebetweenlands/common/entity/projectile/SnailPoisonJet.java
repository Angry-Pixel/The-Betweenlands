package thebetweenlands.common.entity.projectile;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.EntityRegistry;

public class SnailPoisonJet extends ThrowableProjectile {

	public SnailPoisonJet(EntityType<? extends ThrowableProjectile> type, Level level) {
		super(type, level);
	}

	public SnailPoisonJet(Level level, LivingEntity shooter) {
		super(EntityRegistry.SNAIL_POISON_JET.get(), shooter, level);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			for (int count = 0; count < 5; ++count) {
				this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 1.0F, 0.0F, 0.0F), this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}

		if (this.tickCount > 140) {
			this.discard();
		}
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		this.discard();
	}

	@Override
	protected void onHitEntity(EntityHitResult result) {
		if (result.getEntity() instanceof LivingEntity entity) {
			if (entity.hurt(this.damageSources().mobProjectile(this, (LivingEntity) this.getOwner()), 1.0F)) {
				entity.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
			} else {
				this.deflect(ProjectileDeflection.MOMENTUM_DEFLECT, this, this.getOwner(), false);
			}
		}
	}

	@Override
	protected double getDefaultGravity() {
		return 0.02D;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}
}
