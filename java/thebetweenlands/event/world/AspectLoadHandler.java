package thebetweenlands.event.world;

import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.network.message.MessageLoadAspects;

public class AspectLoadHandler {
	public static AspectLoadHandler INSTANCE = new AspectLoadHandler();

	public static long getAspectsSeed(long worldSeed) {
		Random rnd = new Random();
		rnd.setSeed(worldSeed);
		return rnd.nextLong();
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(!event.world.isRemote) {
			long aspectsSeed = getAspectsSeed(event.world.getSeed());
			AspectRecipes.REGISTRY.loadAspects(aspectsSeed);
		}
	}

	@SubscribeEvent
	public void joinWorld(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
			long aspectsSeed = getAspectsSeed(event.world.getSeed());
			TheBetweenlands.networkWrapper.sendTo(new MessageLoadAspects(aspectsSeed), (EntityPlayerMP) event.entity);
		}
	}
}
