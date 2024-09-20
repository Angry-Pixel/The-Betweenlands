package thebetweenlands.common.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MiddleGemItem extends Item {
	public MiddleGemItem(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.thebetweenlands.modifiers.tool").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(this.getDescriptionId() + ".tool").withStyle(ChatFormatting.BLUE));
		tooltip.add(Component.translatable("item.thebetweenlands.modifiers.armor").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(this.getDescriptionId() + ".armor").withStyle(ChatFormatting.BLUE));
	}
}
