package thebetweenlands.event.world;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.network.message.MessageLoadAspects;
import thebetweenlands.world.storage.BetweenlandsWorldData;

public class AspectLoadHandler {
	public static AspectLoadHandler INSTANCE = new AspectLoadHandler();

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(!event.world.isRemote) {
			//long aspectsSeed = getAspectsSeed(event.world.getSeed());
			//AspectRecipes.REGISTRY.generateAspects(aspectsSeed);
			AspectRecipes.REGISTRY.loadAspects(event.world);
		}
	}

	@SubscribeEvent
	public void joinWorld(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
			TheBetweenlands.networkWrapper.sendTo(new MessageLoadAspects(BetweenlandsWorldData.forWorld(event.world).getData().getCompoundTag("aspects")), (EntityPlayerMP) event.entity);
		}
	}
}
