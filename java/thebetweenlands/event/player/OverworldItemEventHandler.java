package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class OverworldItemEventHandler {
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

	@SubscribeEvent
	public void onUseItem(PlayerInteractEvent event) {
		if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.entityPlayer.getHeldItem();
			if(item != null && event.entityPlayer.dimension == ConfigHandler.DIMENSION_ID) {
				if(item.getItem() == Items.flint_and_steel) {
					event.useItem = Result.DENY;
					event.setCanceled(true);
					if(event.world.isRemote) {
						event.entityPlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("chat.flintandsteel").replaceAll("%s", item.getDisplayName())));
					}
				}
			}
		}
	}
}