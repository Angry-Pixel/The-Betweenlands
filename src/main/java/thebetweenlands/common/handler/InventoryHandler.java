package thebetweenlands.common.handler;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.common.inventory.container.ItemContainer;
import thebetweenlands.common.registries.DataComponentRegistry;

public class InventoryHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(InventoryHandler::onPlayerTick);
		NeoForge.EVENT_BUS.addListener(InventoryHandler::onPlayerThrow);
	}
	
	// Force-close containers when the player no longer has the pouch/bundle in their inventory
	public static void onPlayerTick(PlayerTickEvent.Pre event) {
		final Player player = event.getEntity();
		final ItemContainer container;
		if(player != null && !player.level().isClientSide() && player.hasContainerOpen() && (container = ItemContainer.getOpenItemContainer(player)) != null) {
			if(!container.stillValid(player)) {
				container.setChanged();
				container.stopOpen(player);
				player.closeContainer();
//				System.out.println("Force-closing container!");
			} else {
				// unfortunately, the only sure-fire way of not causing dupes, look into maybe giving the stacks a transitive int that goes up each time it's saved or smth
				container.saveTo(container.getContainerStack(player));
			}
		}
	}

	// Force-close containers when the player throws the pouch/bundle
	public static void onPlayerThrow(ItemTossEvent event) {
		final Player player = event.getPlayer();
		final ItemContainer container;
		if(player != null && !player.level().isClientSide() && player.hasContainerOpen() && (container = ItemContainer.getOpenItemContainer(player)) != null) {
			if(!container.stillValid(player)) {
				container.setChanged();
				container.releaseStack(event.getEntity().getItem(), true);
				player.closeContainer();
//				System.out.println("Force-closing container on toss!!");
			}
		}
	}

	
}
