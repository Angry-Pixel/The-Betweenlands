package thebetweenlands.common.entity.projectile;

import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import thebetweenlands.common.component.item.ElixirContents;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ThrownElixir extends ThrowableItemProjectile implements ItemSupplier {

	public ThrownElixir(EntityType<? extends ThrowableItemProjectile> type, Level level) {
		super(type, level);
	}

	public ThrownElixir(Level level, LivingEntity shooter) {
		super(EntityRegistry.ELIXIR.get(), shooter, level);
	}

	public ThrownElixir(Level level, double x, double y, double z) {
		super(EntityRegistry.ELIXIR.get(), x, y, z, level);
	}

	@Override
	protected Item getDefaultItem() {
		return ItemRegistry.GREEN_ELIXIR.asItem();
	}

	@Override
	protected double getDefaultGravity() {
		return 0.05D;
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!this.level().isClientSide()) {
			ItemStack itemstack = this.getItem();
			ElixirContents contents = itemstack.getOrDefault(DataComponentRegistry.ELIXIR_CONTENTS, ElixirContents.EMPTY);
			if (contents.elixir().isPresent()) {
				this.applySplash(contents.createEffect(contents.elixir().get(), 1.0D), result.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)result).getEntity() : null);
			}

			this.level().levelEvent(2002, this.blockPosition(), contents.getElixirColor());
			this.discard();
		}
	}

	private void applySplash(MobEffectInstance effect, @Nullable Entity p_entity) {
		AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
		if (!list.isEmpty()) {
			Entity entity = this.getEffectSource();

			for (LivingEntity livingentity : list) {
				if (livingentity.isAffectedByPotions()) {
					double d0 = this.distanceToSqr(livingentity);
					if (d0 < 16.0) {
						double d1;
						if (livingentity == p_entity) {
							d1 = 1.0;
						} else {
							d1 = 1.0 - Math.sqrt(d0) / 4.0;
						}

						Holder<MobEffect> holder = effect.getEffect();
						if (holder.value().isInstantenous()) {
							holder.value().applyInstantenousEffect(this, this.getOwner(), livingentity, effect.getAmplifier(), d1);
						} else {
							int i = effect.mapDuration(p_267930_ -> (int)(d1 * (double)p_267930_ + 0.5));
							MobEffectInstance mobeffectinstance1 = new MobEffectInstance(
								holder, i, effect.getAmplifier(), effect.isAmbient(), effect.isVisible()
							);
							if (!mobeffectinstance1.endsWithin(20)) {
								livingentity.addEffect(mobeffectinstance1, entity);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public DoubleDoubleImmutablePair calculateHorizontalHurtKnockbackDirection(LivingEntity entity, DamageSource damageSource) {
		double d0 = entity.position().x - this.position().x;
		double d1 = entity.position().z - this.position().z;
		return DoubleDoubleImmutablePair.of(d0, d1);
	}
}
