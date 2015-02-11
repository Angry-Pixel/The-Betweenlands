package thebetweenlands.client.sound;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.sound.SoundLoadEvent;

public class BLSoundRegistry {
	public static final String[] SOUND_FILES = { "DarkDruidDie.ogg", "DarkDruidHit.ogg", "DarkDruidIdle.ogg", "DarkDruidLiving.ogg", "DarkDruidLiving1.ogg", "DarkDruidLiving2.ogg"};

	@SubscribeEvent
	public void loadSoundEvents(SoundLoadEvent event) {
		for (int i = 0; i < SOUND_FILES.length; i++) {
			//TODO: Fix
			//event.manager.soundPoolSounds.addSound("thebl" + ":" + SOUND_FILES[i]);
			//event.manager.soundPoolSounds.("thebl" + ":" + SOUND_FILES[i]);
			//event.manager.soundPoolSounds.addSound("thebl" + ":" + "thergsugoartaertae/raertaeyay/atwtawe.ogg");
		}
	}
}