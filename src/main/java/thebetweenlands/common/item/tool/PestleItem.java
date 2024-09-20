package thebetweenlands.common.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class PestleItem extends Item {
	public PestleItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.thebetweenlands.pestle.desc"));
		tooltip.add(Component.translatable("item.thebetweenlands.pestle.remaining", Math.round(100F - 100F / stack.getMaxDamage() * stack.getDamageValue()), (stack.getMaxDamage() - stack.getDamageValue())));
	}
}
