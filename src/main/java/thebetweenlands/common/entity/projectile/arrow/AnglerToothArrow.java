package thebetweenlands.common.entity.projectile.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class AnglerToothArrow extends AbstractArrow {

	public AnglerToothArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
		super(entityType, level);
	}

	public AnglerToothArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.ANGLER_TOOTH_ARROW.get(), x, y, z, level, pickupItemStack, firedFromWeapon);
	}

	public AnglerToothArrow(LivingEntity owner, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
		super(EntityRegistry.ANGLER_TOOTH_ARROW.get(), owner, level, pickupItemStack, firedFromWeapon);
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemRegistry.ANGLER_TOOTH_ARROW.toStack();
	}
}
