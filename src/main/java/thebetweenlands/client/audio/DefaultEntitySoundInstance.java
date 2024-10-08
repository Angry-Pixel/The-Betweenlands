package thebetweenlands.client.audio;

import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import thebetweenlands.api.audio.EntitySoundInstance;

public class DefaultEntitySoundInstance<T extends Entity> extends AbstractSoundInstance implements EntitySoundInstance, TickableSoundInstance {
	public final T entity;
	public final Predicate<T> isPlaying;

	protected boolean stopped;
	protected boolean fadeOut = false;

	public DefaultEntitySoundInstance(SoundEvent sound, SoundSource category, T entity, Predicate<T> isPlaying) {
		this(sound, category, entity, isPlaying, 0.4f);
	}

	public DefaultEntitySoundInstance(SoundEvent sound, SoundSource category, T entity, Predicate<T> isPlaying, float volume) {
		super(sound, category, SoundInstance.createUnseededRandom());
		this.looping = true;
		this.attenuation = Attenuation.LINEAR;
		this.entity = entity;
		this.isPlaying = isPlaying;
		this.x = (float) this.entity.getX();
		this.y = (float) this.entity.getY();
		this.z = (float) this.entity.getZ();
		this.volume = volume;
	}

	@Override
	public void tick() {
		this.x = (float) this.entity.getX();
		this.y = (float) this.entity.getY();
		this.z = (float) this.entity.getZ();

		Entity view = Minecraft.getInstance().getCameraEntity();

		if(this.fadeOut || this.entity == null || !this.entity.isAlive() || !this.entity.level().isLoaded(this.entity.blockPosition())
				|| !this.isPlaying.test(this.entity) || view == null || this.entity.distanceTo(view) > Math.max(16, this.volume * 16)) {
			this.fadeOut = true;

			this.volume -= 0.05F;
			if(this.volume <= 0.0F) {
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

	@Override
	public Entity getEntity() {
		return this.entity;
	}

	@Override
	public void stopEntityMusic() {
		this.stop();
	}
}
