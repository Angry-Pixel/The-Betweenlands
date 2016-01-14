package thebetweenlands.event.world;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.network.message.MessageLoadAspects;
import thebetweenlands.world.storage.BetweenlandsWorldData;

public class AspectLoadHandler {
	public static AspectLoadHandler INSTANCE = new AspectLoadHandler();

	@SubscribeEvent
	public void joinWorld(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
			TheBetweenlands.networkWrapper.sendTo(new MessageLoadAspects(BetweenlandsWorldData.forWorld(event.world).getData().getCompoundTag("aspects")), (EntityPlayerMP) event.entity);
		}
	}
}