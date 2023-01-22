package thebetweenlands.client.audio;

import org.lwjgl.openal.AL10;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import paulscode.sound.libraries.ChannelLWJGLOpenAL;
import thebetweenlands.api.audio.IEntitySound;
import thebetweenlands.client.handler.MusicHandler;
import thebetweenlands.common.entity.mobs.EntityGreebling;
import thebetweenlands.common.registries.SoundRegistry;

public class GreeblingMusicSound extends EntityMusicSound<EntityGreebling> {
	public final int type;

	private int ticksPlayed = 0;

	private boolean synced = false;

	private int syncTimer = 0;

	private volatile boolean isSoundReady = false;

	public GreeblingMusicSound(int type, EntityGreebling entity, float volume) {
		super(type == 0 ? SoundRegistry.GREEBLING_MUSIC_1 : SoundRegistry.GREEBLING_MUSIC_2, SoundCategory.NEUTRAL, entity, entity, volume, AttenuationType.NONE);
		this.type = type;
	}

	@Override
	public void update() {
		if(!this.synced) {
			final SoundSystemOpenALAccess openALAccess = MusicHandler.INSTANCE.getOpenALAccess();

			if(this.isSoundReady) {
				final IEntitySound otherSound = MusicHandler.INSTANCE.getEntityMusic(this.type == 0 ? EntityMusicLayers.GREEBLING_2 : EntityMusicLayers.GREEBLING_1);

				if(otherSound instanceof GreeblingMusicSound) {
					openALAccess.submitToSoundSystem(() -> {
						float otherSeconds = openALAccess.getOffsetSecondsAsync(otherSound);
						if(otherSeconds >= 0.0f) {
							openALAccess.setOffsetSecondsAsync(this, otherSeconds);
						}
						return null;
					});
				}

				this.synced = true;
			} else {
				if(this.syncTimer % 10 == 0) {
					openALAccess.submitToSoundSystem(() -> {
						ChannelLWJGLOpenAL channel = openALAccess.getChannelAsync(this);

						if(channel != null && channel.ALSource != null) {
							int sourceId = channel.ALSource.get(0);
							if(sourceId >= 0) {
								int state = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE);
								if(state == AL10.AL_PLAYING || state == AL10.AL_PAUSED) {
									this.isSoundReady = true;
								}
							}
						}
						return null;
					});
				}

				this.syncTimer++;
			}
		}

		this.ticksPlayed++;

		super.update();

		if(!this.mustFadeOut && !this.fadeOut) {
			EntityPlayer player = MusicHandler.INSTANCE.getPlayer();
			if(player != null) {
				this.volume = (float) MathHelper.clamp(this.originalVolume * (1.0D - player.getDistance(this.xPosF, this.yPosF, this.zPosF) / ((EntityGreebling) this.getMusicEntity()).getMusicRange(player)), 0, 1);
			}
		}
	}
}
