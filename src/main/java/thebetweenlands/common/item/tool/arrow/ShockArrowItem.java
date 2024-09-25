package thebetweenlands.common.item.tool.arrow;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import thebetweenlands.common.entity.projectile.arrow.ShockArrow;

public class ShockArrowItem extends ArrowItem {
	public ShockArrowItem(Properties properties) {
		super(properties);
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		ShockArrow arrow = new ShockArrow(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
		arrow.pickup = AbstractArrow.Pickup.ALLOWED;
		return arrow;
	}

	@Override
	public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
		return new ShockArrow(shooter, level, ammo.copyWithCount(1), weapon);
	}
}
