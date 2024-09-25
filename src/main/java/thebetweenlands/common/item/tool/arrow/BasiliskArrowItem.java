package thebetweenlands.common.item.tool.arrow;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import thebetweenlands.common.entity.projectile.arrow.BasiliskArrow;

import java.util.List;

public class BasiliskArrowItem extends ArrowItem {
	public BasiliskArrowItem(Properties properties) {
		super(properties);
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		BasiliskArrow arrow = new BasiliskArrow(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
		arrow.pickup = AbstractArrow.Pickup.ALLOWED;
		return arrow;
	}

	@Override
	public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
		return new BasiliskArrow(shooter, level, ammo.copyWithCount(1), weapon);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
	}
}
