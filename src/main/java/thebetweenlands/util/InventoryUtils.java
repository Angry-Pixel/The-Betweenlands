package thebetweenlands.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.block.container.BlockFishingTackleBox;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;

public class InventoryUtils {
	public static boolean isDisallowedInInventories(ItemStack stack) {
		Item item = stack.getItem();
		if(!stack.isEmpty()) {
			if(BetweenlandsConfig.GENERAL.pouchBlacklist.isListed(stack)) {
				return true;
			}

			if(item instanceof ItemLurkerSkinPouch || item instanceof ItemShulkerBox) {
				return true;
			}

			if(item instanceof ItemBlock) {
				Block block = ((ItemBlock) item).getBlock();
				return block instanceof BlockFishingTackleBox || block instanceof BlockShulkerBox;
			}
		}
		return false;
	}
}
