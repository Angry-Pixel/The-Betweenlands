package thebetweenlands.common.item.armor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.common.item.UnbreakableItem;
import thebetweenlands.common.registries.ArmorMaterialRegistry;

import java.util.List;

public class AncientArmorItem extends ArmorItem implements UnbreakableItem {
	public AncientArmorItem(Type type, Properties properties) {
		super(ArmorMaterialRegistry.ANCIENT, type, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		if (UnbreakableItem.isStackBroken(stack)) {
			tooltip.add(Component.translatable("item.thebetweenlands.broken", stack.getDisplayName()).withStyle(ChatFormatting.RED));
		} else {
			tooltip.add(Component.translatable("item.thebetweenlands.ancient_armor.desc").withStyle(ChatFormatting.GRAY));
		}
	}
}
