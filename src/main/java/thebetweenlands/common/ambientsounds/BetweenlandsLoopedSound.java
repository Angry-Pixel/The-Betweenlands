package thebetweenlands.common.ambientsounds;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class BetweenlandsLoopedSound extends SimpleSoundInstance {

	// A static version of the looping sound
	public BetweenlandsLoopedSound(SoundEvent sound, SoundSource soundsource, float volume, float pitch) {
			super(sound.getLocation(), soundsource, volume, pitch, true, 0, Attenuation.LINEAR, 0, 0, 0, true);
	}

}
