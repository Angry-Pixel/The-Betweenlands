package thebetweenlands.common.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.ItemAspectContainer;

/**
 * Just for testing
 */
public class AspectTestHandler {
	public static final AspectTestHandler INSTANCE = new AspectTestHandler();

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if(!player.worldObj.isRemote) {
			ItemStack heldItem = player.getHeldItemMainhand();
			if(heldItem != null) {
				AspectManager manager = AspectManager.get(player.worldObj);
				ItemAspectContainer container = ItemAspectContainer.fromItem(heldItem, manager);
				//container.clear();
				//container.addAmount(AspectRegistry.AZUWYNN, 1);
				//				System.out.println("Amount: " + container.getAmount(AspectRegistry.YEOWYNN));
				//				System.out.println("Drained: " + container.drainAmount(AspectRegistry.YEOWYNN, 10));
				//				System.out.println("Aspects: " + container.getAspects(null));
			}
		}
	}
}
