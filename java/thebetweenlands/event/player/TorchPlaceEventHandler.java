package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class TorchPlaceEventHandler {
	@SubscribeEvent
	public void onPlayerTorchPlacement(PlaceEvent event) {
		ItemStack itemstack = event.player.inventory.getCurrentItem();
		if (itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.torch)) {
			if (event.player.dimension == ConfigHandler.DIMENSION_ID) {
				for(int x = -2; x <= 2; x++) {
					for(int y = -2; y <= 2; y++) {
						for(int z = -2; z <= 2; z++) {
							Block block = event.world.getBlock(event.x + x, event.y + y, event.z + z);
							if(block == Blocks.torch) {
								int meta = event.world.getBlockMetadata(event.x + x, event.y + y, event.z + z);
								event.world.setBlock(event.x + x, event.y + y, event.z + z, BLBlockRegistry.dampTorch, meta, 3);
								event.world.playSoundEffect(event.x, event.y + 1, event.z, "random.fizz", 1.0F, 1.0F);
							}
						}
					}
				}
			}
		}
	}
}