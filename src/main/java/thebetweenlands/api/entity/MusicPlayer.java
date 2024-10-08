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
	SoundEvent getMusicFile(Player listener);

	double getMusicRange(Player listener);

	boolean isMusicActive(Player listener);

	@Nullable
	default EntitySoundInstance getMusicSound(Player listener) {
		SoundEvent sound = this.getMusicFile(listener);
		return new EntityMusicSoundInstance<>(sound, SoundSource.MUSIC, (Entity) this, this, 1, SoundInstance.Attenuation.NONE);
	}

	default int getMusicLayer(Player listener) {
		return 0;
	}

	default boolean canInterruptOtherEntityMusic(Player listener) {
		return true;
	}
}
