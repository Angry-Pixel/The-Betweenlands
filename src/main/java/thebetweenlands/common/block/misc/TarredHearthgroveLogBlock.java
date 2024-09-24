package thebetweenlands.common.block.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import thebetweenlands.common.block.terrain.HearthgroveLogBlock;

import java.util.List;

public class TarredHearthgroveLogBlock extends HearthgroveLogBlock {
	public TarredHearthgroveLogBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("block.thebetweenlands.good_fuel"));
	}
}
