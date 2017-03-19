package thebetweenlands.common.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.network.clientbound.MessageSyncStaticAspects;

public class AspectSyncHandler {
	@SubscribeEvent
	public static void joinWorld(EntityJoinWorldEvent event) {
		if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
			TheBetweenlands.networkWrapper.sendTo(new MessageSyncStaticAspects(AspectManager.get(event.getWorld())), (EntityPlayerMP) event.getEntity());
		}
	}
}
