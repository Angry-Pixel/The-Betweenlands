package thebetweenlands.common.entity.projectile.arrow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class PoisonAnglerToothArrow extends AbstractArrow {

	public PoisonAnglerToothArrow(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	public PoisonAnglerToothArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.POISON_ANGLER_TOOTH_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	public PoisonAnglerToothArrow(LivingEntity owner, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.POISON_ANGLER_TOOTH_ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
	}


	@Override
	protected void doPostHurtEffects(LivingEntity target) {
		super.doPostHurtEffects(target);
		target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 2), this.getEffectSource());
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemRegistry.POISONED_ANGLER_TOOTH_ARROW.toStack();
	}
}
