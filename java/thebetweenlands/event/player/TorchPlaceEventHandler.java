package thebetweenlands.event.player;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.confighandler.ConfigHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
	/*@SubscribeEvent
	public void onPlayerTorchPlacement(PlayerInteractEvent event) {
		if (event.world.isRemote)
			return;

		ItemStack itemstack = event.entityPlayer.inventory.getCurrentItem();
		if (itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.torch)) {
			if (event.entityPlayer.dimension == ConfigHandler.DIMENSION_ID)
				if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {

					if (event.face == 1 && BLBlockRegistry.dampTorch.canPlaceBlockAt(event.world, event.x, event.y + 1, event.z)) {
						if (event.world.getBlock(event.x, event.y + 1, event.z) == Blocks.air) {
							event.world.setBlock(event.x, event.y + 1, event.z, BLBlockRegistry.dampTorch, 5, 3);
							event.world.playSoundEffect(event.x, event.y + 1, event.z, "random.fizz", 1.0F, 1.0F);
						}
					}

					if (event.face == 2 && event.world.isSideSolid(event.x, event.y, event.z, NORTH, true))
						if (event.world.getBlock(event.x, event.y, event.z - 1) == Blocks.air) {
							event.world.setBlock(event.x, event.y, event.z - 1, BLBlockRegistry.dampTorch, 4, 3);
							event.world.playSoundEffect(event.x, event.y, event.z - 1, "random.fizz", 1.0F, 1.0F);
						}

					if (event.face == 3 && event.world.isSideSolid(event.x, event.y, event.z, SOUTH, true))
						if (event.world.getBlock(event.x, event.y, event.z + 1) == Blocks.air) {
							event.world.setBlock(event.x, event.y, event.z + 1, BLBlockRegistry.dampTorch, 3, 3);
							event.world.playSoundEffect(event.x, event.y, event.z + 1, "random.fizz", 1.0F, 1.0F);
						}

					if (event.face == 4 && event.world.isSideSolid(event.x, event.y, event.z, WEST, true))
						if (event.world.getBlock(event.x - 1, event.y, event.z) == Blocks.air) {
							event.world.setBlock(event.x - 1, event.y, event.z, BLBlockRegistry.dampTorch, 2, 3);
							event.world.playSoundEffect(event.x - 1, event.y, event.z, "random.fizz", 1.0F, 1.0F);
						}

					if (event.face == 5 && event.world.isSideSolid(event.x, event.y, event.z, EAST, true))
						if (event.world.getBlock(event.x + 1, event.y, event.z) == Blocks.air) {
							event.world.setBlock(event.x + 1, event.y, event.z, BLBlockRegistry.dampTorch, 1, 3);
							event.world.playSoundEffect(event.x + 1, event.y, event.z, "random.fizz", 1.0F, 1.0F);
						}
				}
		}
	}*/
}