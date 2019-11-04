package thebetweenlands.common.handler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.block.terrain.BlockHearthgroveLog;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.BlockRegistry;

public class FuelHandler {
	@SubscribeEvent
	public static void onFuelEvent(FurnaceFuelBurnTimeEvent event) {
		ItemStack stack = event.getItemStack();
		if(stack.getItem() instanceof ItemMisc && stack.getItemDamage() == EnumItemMisc.SULFUR.getID()) {
			event.setBurnTime(1600);
		}
		else if(stack.getItem() instanceof ItemMisc && stack.getItemDamage() == EnumItemMisc.UNDYING_EMBER.getID()) {
			event.setBurnTime(20000);
		}
		else if(stack.getItem() == Item.getItemFromBlock(BlockRegistry.LOG_HEARTHGROVE)) {
			IBlockState state = BlockRegistry.LOG_HEARTHGROVE.getStateFromMeta(stack.getMetadata());
			if(state.getValue(BlockHearthgroveLog.TARRED)) {
				event.setBurnTime(4800);
			} else {
				event.setBurnTime(800);
			}
		}
	}
}