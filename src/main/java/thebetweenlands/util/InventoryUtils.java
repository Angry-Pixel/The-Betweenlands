package thebetweenlands.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.item.equipment.ItemLurkerSkinPouch;
import thebetweenlands.common.registries.BlockRegistry;

public class InventoryUtils {
	public static boolean isDisallowedInInventories(ItemStack stack) {
		Item item = stack.getItem();
		return !stack.isEmpty() && (BetweenlandsConfig.GENERAL.pouchBlacklist.isListed(stack) || (item instanceof ItemLurkerSkinPouch || (item instanceof ItemBlock && ((ItemBlock) item).getBlock() == BlockRegistry.FISHING_TACKLE_BOX)));
	}
}
