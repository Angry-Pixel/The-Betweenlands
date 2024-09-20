package thebetweenlands.common.entity.projectile.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class BasiliskArrow extends AbstractArrow {

	public BasiliskArrow(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	public BasiliskArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.BASILISK_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	public BasiliskArrow(LivingEntity owner, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.BASILISK_ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
	}

	@Override
	protected void doPostHurtEffects(LivingEntity target) {
		super.doPostHurtEffects(target);
		if (!target.getType().is(Tags.EntityTypes.BOSSES)) {
			target.addEffect(ElixirEffectRegistry.EFFECT_PETRIFY.get().createEffect(100, 1), this.getEffectSource());
		} else {
			target.addEffect(ElixirEffectRegistry.EFFECT_PETRIFY.get().createEffect(40, 1), this.getEffectSource());
		}
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemRegistry.BASILISK_ARROW.toStack();
	}
}
