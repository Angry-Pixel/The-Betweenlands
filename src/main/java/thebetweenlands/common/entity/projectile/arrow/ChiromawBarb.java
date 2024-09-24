package thebetweenlands.common.entity.projectile.arrow;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;

public class ChiromawBarb extends AbstractArrow {

	public ChiromawBarb(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	public ChiromawBarb(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.CHIROMAW_BARB.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	public ChiromawBarb(LivingEntity owner, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.CHIROMAW_BARB.get(), owner, level, pickupItemStack, firedFromWeapon);
	}


	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide() && this.pickup != Pickup.ALLOWED && this.inGroundTime > 100) {
			this.discard();
		}
	}

	@Override
	protected void doPostHurtEffects(LivingEntity target) {
		super.doPostHurtEffects(target);
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
		return ItemRegistry.CHIROMAW_BARB.toStack();
	}
}
