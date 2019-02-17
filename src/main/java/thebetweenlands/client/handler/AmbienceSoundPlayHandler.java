package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thebetweenlands.client.audio.ambience.AmbienceManager;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;

public class AmbienceSoundPlayHandler {
	@SubscribeEvent
	public static void onPlayerCltTick(PlayerTickEvent event) {
		if(event.phase == Phase.START && event.side == Dist.CLIENT && event.player == TheBetweenlands.proxy.getClientPlayer()) {
			if(event.player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
				AmbienceManager.INSTANCE.update();
			}
		}
	}

	@SubscribeEvent
	public static void onPlaySound(SoundSourceEvent event) {
		if(event.getSound().getCategory() == SoundCategory.MUSIC && AmbienceManager.INSTANCE.shouldStopMusic()) {
			Minecraft.getInstance().getSoundHandler().stop(event.getSound());
		}
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		if(event.getWorld().isRemote) {
			AmbienceManager.INSTANCE.stopAll();
		}
	}
}
