package thebetweenlands.common.items.shield;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.common.registries.ToolMaterialRegistry;

import java.util.List;

public class SyrmoriteShieldItem extends SwatShieldItem {
	public SyrmoriteShieldItem(Properties properties) {
		super(ToolMaterialRegistry.SYRMORITE, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable(this.getDescriptionId() + ".desc", Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage().getString(), Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage().getString()).withStyle(ChatFormatting.GRAY));
	}
}
