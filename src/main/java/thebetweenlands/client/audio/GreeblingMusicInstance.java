package thebetweenlands.client.audio;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.OpenAlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import thebetweenlands.api.audio.EntitySoundInstance;
import thebetweenlands.client.handler.MusicHandler;
import thebetweenlands.common.entity.creature.Greebling;
import thebetweenlands.common.registries.SoundRegistry;

public class GreeblingMusicInstance extends EntityMusicSoundInstance<Greebling> {
	public final int type;

	private boolean synced = false;

	private int syncTimer = 0;

	private volatile boolean isSoundReady = false;

	public GreeblingMusicInstance(int type, Greebling entity, float volume) {
		super(type == 0 ? SoundRegistry.GREEBLING_MUSIC_1.get() : SoundRegistry.GREEBLING_MUSIC_2.get(), SoundSource.NEUTRAL, entity, entity, volume, Attenuation.NONE);
		this.type = type;
	}

	@Override
	public void tick() {
		if (!this.synced) {
			SoundEngine engine = Minecraft.getInstance().getSoundManager().soundEngine;
			if (this.isSoundReady) {
				final EntitySoundInstance otherSound = MusicHandler.getEntityMusic(this.type == 0 ? EntityMusicLayers.GREEBLING_2 : EntityMusicLayers.GREEBLING_1);

				if (otherSound instanceof GreeblingMusicInstance) {
					var channel = engine.instanceToChannel.get(this).channel;
					if (channel != null) {
						float seconds = this.getOffsetSeconds(engine, otherSound);
						if (seconds >= 0.0f) {
							AL10.alSourcef(channel.source, AL11.AL_SEC_OFFSET, seconds);
						}
					}
				}

				this.synced = true;
			} else {
				if (this.syncTimer % 10 == 0) {
					var channel = engine.instanceToChannel.get(this).channel;
					if (channel != null) {
						int source = channel.source;
						if (source >= 0) {
							int state = AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE);
							if (state == AL10.AL_PLAYING || state == AL10.AL_PAUSED) {
								this.isSoundReady = true;
							}
						}
					}
				}

				this.syncTimer++;
			}
		}

		super.tick();

		if (!this.mustFadeOut && !this.fadeOut) {
			Player player = Minecraft.getInstance().player;
			if (player != null) {
				double range = ((Greebling) this.getEntity()).getMusicRange(player);
				this.volume = (float) Mth.clamp(this.originalVolume * (1.0D - player.distanceToSqr(this.x, this.y, this.z) / (range * range)), 0, 1);
			}
		}
	}

	public float getOffsetSeconds(SoundEngine engine, SoundInstance sound) {
		var handle = engine.instanceToChannel.get(sound);
		if (handle != null && handle.channel != null) {
			int sourceId = handle.channel.source;
			if (sourceId >= 0) {
				return AL10.alGetSourcef(sourceId, AL11.AL_SEC_OFFSET);
			}
		}

		return -1.0F;
	}
}
