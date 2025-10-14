package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import thebetweenlands.api.audio.EntitySoundInstance;
import thebetweenlands.client.audio.EntityMusicSoundInstance;

public interface MusicPlayer {

	@Nullable
	SoundEvent getMusicFile(@Nullable Player listener);

	double getMusicRange(@Nullable Player listener);

	boolean isMusicActive(@Nullable Player listener);

	@Nullable
	default EntitySoundInstance getMusicSound(@Nullable Player listener) {
		SoundEvent sound = this.getMusicFile(listener);
		if (sound != null) {
			return new EntityMusicSoundInstance<>(sound, SoundSource.MUSIC, (Entity) this, this, 1, SoundInstance.Attenuation.NONE);
		}
		return null;
	}

	default int getMusicLayer(@Nullable Player listener) {
		return 0;
	}

	default boolean canInterruptOtherEntityMusic(@Nullable Player listener) {
		return true;
	}
}
