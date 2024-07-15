package thebetweenlands.client.audio;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Predicate;

public class BlockEntitySoundInstance<T extends BlockEntity> extends AbstractSoundInstance implements TickableSoundInstance {
	public final T entity;
	public final BlockPos pos;
	public final Predicate<T> isPlaying;

	private boolean stopped;
	private boolean fadeOut = false;

	public BlockEntitySoundInstance(SoundEvent sound, SoundSource category, T entity, Predicate<T> isPlaying) {
		super(sound, category, SoundInstance.createUnseededRandom());
		this.looping = true;
		this.attenuation = Attenuation.LINEAR;
		this.entity = entity;
		this.isPlaying = isPlaying;
		this.pos = entity.getBlockPos();
		this.x = this.pos.getX() + 0.5F;
		this.y = this.pos.getY() + 0.5F;
		this.z = this.pos.getZ() + 0.5F;
	}

	@Override
	public void tick() {
		if(this.fadeOut || this.entity == null || this.entity.getLevel() == null || !this.entity.getLevel().isLoaded(this.entity.getBlockPos())
			|| this.entity.getLevel().getBlockEntity(this.entity.getBlockPos()) != this.entity || !this.isPlaying.test(this.entity)) {
			this.looping = false;
			this.fadeOut = true;

			this.volume -= 0.05F;
			if(this.volume <= 0.0F) {
				this.stopped = true;
				this.volume = 0;
			}
		}
	}

	@Override
	public boolean isStopped() {
		return this.stopped;
	}

	/**
	 * Stops the sound immediately without fading out
	 */
	public void stopImmediately() {
		this.stopped = true;
		this.looping = false;
	}

	/**
	 * Stops the sound and makes it fade out
	 */
	public void stop() {
		this.fadeOut = true;
	}

	/**
	 * Cancels the fade out
	 */
	public void cancelFade() {
		this.fadeOut = false;
	}

	/**
	 * Returns whether this sound is currently stopped or fading out
	 * @return
	 */
	public boolean isStopping() {
		return this.stopped || this.fadeOut;
	}
}
