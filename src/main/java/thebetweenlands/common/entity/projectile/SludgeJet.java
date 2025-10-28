package thebetweenlands.common.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.FastColor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

public class SludgeJet extends Entity {

	public SludgeJet(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide()) {
			if (this.tickCount > 20)
				this.discard();
		} else {
			if (this.firstTick) {
				this.spawnSludgeJetParticles(0.0F, 0.125F, true);
				this.spawnSludgeJetParticles(0.0F, 0.175F, true);
				this.spawnSludgeJetParticles(0.025F, 0.2F, true);
				this.spawnSludgeJetParticles(0.2F, 0.225F, false);
			}
		}
		super.tick();
	}

	private void spawnSludgeJetParticles(float spread, float spurtStrength, boolean randomizeStartPos) {
		for (double yy = this.getY(); yy < this.getY() + 2.5D; yy += (this.random.nextDouble() * 0.15D)) {
			double d0 = this.getX() - (randomizeStartPos ? this.random.nextFloat() * 0.075F : 0.075F);
			double d2 = this.getZ() - (randomizeStartPos ? this.random.nextFloat() * 0.075F : 0.075F);
			double d3 = this.getX() + (randomizeStartPos ? this.random.nextFloat() * 0.075F : 0.075F);
			double d4 = this.getZ() + (randomizeStartPos ? this.random.nextFloat() * 0.075F : 0.075F);
			double accel = (yy - this.getY()) * spread;

			int color = FastColor.ARGB32.color(105, 70, 40);

			TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d0, this.getY(), d4, ParticleFactory.ParticleArgs.get().withMotion(accel * (this.getRandom().nextFloat() - 0.5f), (yy - this.getY()) * spurtStrength, accel * (this.getRandom().nextFloat() - 0.5f)).withScale(2.5F).withData(false).withColor(color));
			TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d3, this.getY(), d2, ParticleFactory.ParticleArgs.get().withMotion(accel * (this.getRandom().nextFloat() - 0.5f), (yy - this.getY()) * spurtStrength, accel * (this.getRandom().nextFloat() - 0.5f)).withScale(2.5F).withData(false).withColor(color));
			TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d3, this.getY(), d4, ParticleFactory.ParticleArgs.get().withMotion(accel * (this.getRandom().nextFloat() - 0.5f), (yy - this.getY()) * spurtStrength, accel * (this.getRandom().nextFloat() - 0.5f)).withScale(2.5F).withData(false).withColor(color));
			TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d0, this.getY(), d2, ParticleFactory.ParticleArgs.get().withMotion(accel * (this.getRandom().nextFloat() - 0.5f), (yy - this.getY()) * spurtStrength, accel * (this.getRandom().nextFloat() - 0.5f)).withScale(2.5F).withData(false).withColor(color));
			TheBetweenlands.createParticle(ParticleRegistry.FALLING_FLUID.get(), this.level(), d0, this.getY(), d2, ParticleFactory.ParticleArgs.get().withMotion(accel * (this.getRandom().nextFloat() - 0.5f), (yy - this.getY()) * spurtStrength, accel * (this.getRandom().nextFloat() - 0.5f)).withScale(2.5F).withData(false).withColor(color));
		}
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public boolean isInvulnerable() {
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		return false;
	}

	@Override
	public void playerTouch(Player player) {
		if (player.getBoundingBox().intersects(this.getBoundingBox())) {
			if (!this.level().isClientSide() && !player.isCreative() && !player.isSpectator()) {
				if (!ElixirEffectRegistry.EFFECT_DECAY.get().isActive(player)) {
					player.addEffect(ElixirEffectRegistry.EFFECT_DECAY.get().createEffect(60, 3));
				}
				player.setData(AttachmentRegistry.DECAY, player.getData(AttachmentRegistry.DECAY).addDecayAcceleration(0.1F));
			}
			if (this.level().isClientSide())
				player.push(0, 0.2D, 0);
		}
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
}
