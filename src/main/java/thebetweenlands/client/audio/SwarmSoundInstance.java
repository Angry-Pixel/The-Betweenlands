package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import thebetweenlands.common.component.entity.SwarmedData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class SwarmSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {

	private boolean fadeOut = false;
	private boolean stopped;

	public SwarmSoundInstance(SoundEvent sound, SoundSource category) {
		super(sound, category, SoundInstance.createUnseededRandom());
		this.volume = 0.001f;
		this.looping = true;
		this.attenuation = Attenuation.LINEAR;
	}

	public SwarmSoundInstance() {
		this(SoundRegistry.SWARM_ATTACK.get(), SoundSource.HOSTILE);
	}

	@Override
	public void tick() {
		if (!this.fadeOut) {
			Entity view = Minecraft.getInstance().getCameraEntity();

			if (view != null) {
				this.x = (float) view.getX();
				this.y = (float) view.getY();
				this.z = (float) view.getZ();

				SwarmedData cap = view.getData(AttachmentRegistry.SWARMED);
				float targetVolume = cap.getSwarmedStrength() * 0.5f;

				if (this.volume > targetVolume) {
					this.volume -= 0.05f;

					if (this.volume < targetVolume) {
						this.volume = targetVolume;

						if (targetVolume <= 0) {
							this.stop();
						}
					}
				} else if (this.volume < targetVolume) {
					this.volume += 0.05f;

					if (this.volume > targetVolume) {
						this.volume = targetVolume;
					}
				}
			} else {
				this.stopImmediately();
			}
		}

		if (this.fadeOut) {
			this.volume -= 0.05F;
			if (this.volume <= 0.0F) {
				this.looping = false;
				this.stopped = true;
				this.volume = 0;
			}
		}
	}

	public void stopImmediately() {
		this.stopped = true;
		this.looping = false;
	}

	public void stop() {
		this.fadeOut = true;
	}

	public void cancelFade() {
		this.fadeOut = false;
	}

	@Override
	public boolean isStopped() {
		return this.stopped || this.fadeOut;
	}
}
