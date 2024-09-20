package thebetweenlands.common.entity.projectile.arrow;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.common.entity.projectile.ElectricShock;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class ChiromawShockBarb extends AbstractArrow {

	public ChiromawShockBarb(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	public ChiromawShockBarb(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.CHIROMAW_SHOCK_BARB.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	public ChiromawShockBarb(LivingEntity owner, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.CHIROMAW_SHOCK_BARB.get(), owner, level, pickupItemStack, firedFromWeapon);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.level().isClientSide()) {
			this.spawnLightningArcs();
		} else {
			if (this.pickup != Pickup.ALLOWED && this.inGroundTime > 100) {
				this.discard();
			}
		}
	}

	private void spawnLightningArcs() {
		Entity view = Minecraft.getInstance().getCameraEntity();
		if(view != null && view.distanceTo(this) < 16 && this.level().getRandom().nextInt(!this.inGround ? 2 : 20) == 0) {
			float ox = this.level().getRandom().nextFloat() - 0.5f + (!this.inGround ? (float)this.getDeltaMovement().x() : 0);
			float oy = this.level().getRandom().nextFloat() - 0.5f + (!this.inGround ? (float)this.getDeltaMovement().y() : 0);
			float oz = this.level().getRandom().nextFloat() - 0.5f + (!this.inGround ? (float)this.getDeltaMovement().z() : 0);

//			Particle particle = TheBetweenlands.createParticle(ParticleRegistry.LIGHTNING_ARCS.get(), this.level(), this.getX(), this.getY(), this.getZ(),
//				ParticleFactory.ParticleArgs.get()
//					.withMotion(!this.inGround ? this.getDeltaMovement().x() : 0, !this.inGround ? this.getDeltaMovement().y() : 0, !this.inGround ? this.getDeltaMovement().z() : 0)
//					.withColor(0.3f, 0.5f, 1.0f, 0.9f)
//					.withData(new Vec3(this.getX() + ox, this.getY() + oy, this.getZ() + oz)));
//
//			BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.BEAM, particle);
		}
	}

	@Override
	protected void doPostHurtEffects(LivingEntity target) {
		super.doPostHurtEffects(target);
		if (!this.level().isClientSide()) {
			this.level().addFreshEntity(new ElectricShock(this.level(), this, target, 0, this.isInWaterOrRain()));
		}
		if (!target.getType().is(Tags.EntityTypes.BOSSES)) {
			target.addEffect(ElixirEffectRegistry.EFFECT_PETRIFY.get().createEffect(40, 1), this.getEffectSource());
		}
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundRegistry.CHIROMAW_MATRIARCH_BARB_HIT.get();
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemStack.EMPTY;
	}
}
