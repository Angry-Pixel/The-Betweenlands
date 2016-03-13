package thebetweenlands.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.audio.ambience.AmbienceManager;
import thebetweenlands.lib.ModInfo;

public class AmbienceSoundPlayHandler {
	@SubscribeEvent
	public void onPlayerCltTick(PlayerTickEvent event) {
		if( event.phase == Phase.START && event.side == Side.CLIENT && event.player == TheBetweenlands.proxy.getClientPlayer()) {
			if( event.player.dimension == ModInfo.DIMENSION_ID ) {
				AmbienceManager.INSTANCE.update();
			}
		}
	}

	@SubscribeEvent
	public void onPlaySound(SoundSourceEvent event) {
		SoundEventAccessorComposite soundeventaccessorcomposite = Minecraft.getMinecraft().getSoundHandler().getSound(event.sound.getPositionedSoundLocation());
		if(soundeventaccessorcomposite.getSoundCategory() == SoundCategory.MUSIC) {
			boolean muteSound = /*false*/AmbienceManager.INSTANCE.shouldStopMusic();
			World world = Minecraft.getMinecraft().theWorld;
			if(muteSound) {
				Minecraft.getMinecraft().getSoundHandler().stopSound(event.sound);
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if(event.world.isRemote) {
			AmbienceManager.INSTANCE.stopAll();
		}
	}
}
