package thebetweenlands.client.event.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.client.audio.DruidAltarSound;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityDruidAltar;

public class DruidAltarSoundHandler {
	private static Map<TileEntityDruidAltar, DruidAltarSound> playingSounds = new HashMap<TileEntityDruidAltar, DruidAltarSound>();

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.START) {
			World world = TheBetweenlands.proxy.getClientWorld();
			if(world != null && !Minecraft.getMinecraft().isGamePaused()) {
				Iterator<Entry<TileEntityDruidAltar, DruidAltarSound>> it = playingSounds.entrySet().iterator();
				while(it.hasNext()) {
					Entry<TileEntityDruidAltar, DruidAltarSound> sound = it.next();
					if(sound.getValue().isDonePlaying()) {
						it.remove();
					}
				}
			}
		}
	}

	public static void playAltarSound(TileEntityDruidAltar altar) {
		if(!playingSounds.containsKey(altar)) {
			DruidAltarSound sound = new DruidAltarSound(SoundRegistry.DRUID_CHANT, SoundCategory.BLOCKS, altar);
			playingSounds.put(altar, sound);
			Minecraft.getMinecraft().getSoundHandler().playSound(sound);
		}
	}

	public static void stopAltarSound(TileEntityDruidAltar altar) {
		DruidAltarSound sound = playingSounds.get(altar);
		if(sound != null && !sound.isStopping()) {
			sound.stop();
		}
	}
}
