package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import thebetweenlands.api.entity.MusicPlayer;

public class EntityMusicSoundInstance<T extends Entity> extends DefaultEntitySoundInstance<T> {
	public final MusicPlayer music;
	public final float originalVolume;

	protected boolean mustFadeOut = false;

	public EntityMusicSoundInstance(SoundEvent sound, SoundSource category, T entity, MusicPlayer music, float volume, Attenuation attenuation) {
		super(sound, category, entity, e -> true);
		this.looping = true;
		this.attenuation = attenuation;

		this.music = music;
		this.x = (float) this.entity.getX();
		this.y = (float) this.entity.getY();
		this.z = (float) this.entity.getZ();
		this.originalVolume = volume;
		this.volume = 0.1F;
	}

	@Override
	public void tick() {
		if(this.entity != null && !this.entity.isRemoved()) {
			if(this.mustFadeOut) {
				if(this.volume > 0) {
					this.volume -= 0.1F;
					if(this.volume <= 0.0F) {
						this.volume = 0;
						this.stopped = true;
					}
				}
			} else {
				if(!this.music.isMusicActive(Minecraft.getInstance().player) || this.entity.distanceTo(Minecraft.getInstance().player) > this.music.getMusicRange(Minecraft.getInstance().player)) {
					this.looping = false;
					this.fadeOut = true;

					if(this.volume > 0) {
						this.volume -= 0.1F;
						if(this.volume <= 0.0F) {
							this.volume = 0;
							this.stopped = true;
						}
					}
				} else {
					if(this.fadeOut) {
						this.cancelFade();
						this.looping = true;
					}
					if(this.volume < this.originalVolume) {
						this.volume += 0.1F;
						if(this.volume > this.originalVolume) {
							this.volume = this.originalVolume;
						}
					}
				}
				this.x = (float) this.entity.getX();
				this.y = (float) this.entity.getY();
				this.z = (float) this.entity.getZ();
			}
		} else {
			this.stopped = true;
			this.volume = 0;
		}
	}

	@Override
	public void stopEntityMusic() {
		super.stopEntityMusic();
		this.mustFadeOut = true;
	}
}
