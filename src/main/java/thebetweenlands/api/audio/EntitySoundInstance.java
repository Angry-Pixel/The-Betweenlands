package thebetweenlands.api.audio;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.world.entity.Entity;

public interface EntitySoundInstance extends SoundInstance {
	Entity getEntity();

	void stopEntityMusic();
}
