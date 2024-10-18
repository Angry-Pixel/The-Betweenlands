package thebetweenlands.common.item.misc;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;

import java.util.List;

public class MiddleGemItem extends Item {

	private final CircleGemType type;

	public MiddleGemItem(CircleGemType type, Properties properties) {
		super(properties);
		this.type = type;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.thebetweenlands.modifiers.tool").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(this.getDescriptionId() + ".tool").withStyle(ChatFormatting.BLUE));
		tooltip.add(Component.translatable("item.thebetweenlands.modifiers.armor").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(this.getDescriptionId() + ".armor").withStyle(ChatFormatting.BLUE));
	}

	public CircleGemType getType() {
		return this.type;
	}
}
