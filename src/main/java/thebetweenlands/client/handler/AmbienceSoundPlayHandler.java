package thebetweenlands.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import thebetweenlands.client.audio.ambience.AmbienceManager;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;

public class AmbienceSoundPlayHandler {
	@SubscribeEvent
	public static void onPlayerCltTick(PlayerTickEvent event) {
		if(event.phase == Phase.START && event.side == Side.CLIENT && event.player == TheBetweenlands.proxy.getClientPlayer()) {
			AmbienceManager.INSTANCE.update();
		}
	}

	@SubscribeEvent
	public static void onPlaySound(SoundSourceEvent event) {
		if(event.getSound().getCategory() == SoundCategory.MUSIC && AmbienceManager.INSTANCE.shouldStopMusic()) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(event.getSound());
		}
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		if(event.getWorld().isRemote) {
			AmbienceManager.INSTANCE.stopAll();
		}
	}
}
