package thebetweenlands.client.audio;

import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntitySound<T extends Entity> extends SafeStreamSound {
	public final T entity;
	public final Predicate<T> isPlaying;

	private boolean fadeOut = false;

	public EntitySound(SoundEvent sound, SoundCategory category, T entity, Predicate<T> isPlaying) {
		super(sound, category);
		this.repeat = true;
		this.attenuationType = AttenuationType.LINEAR;
		this.entity = entity;
		this.isPlaying = isPlaying;
		this.xPosF = (float) this.entity.posX;
		this.yPosF = (float) this.entity.posY;
		this.zPosF = (float) this.entity.posZ;
		this.volume = 0.4F;
	}

	@Override
	public void update() {
		this.xPosF = (float) this.entity.posX;
		this.yPosF = (float) this.entity.posY;
		this.zPosF = (float) this.entity.posZ;

		if(this.fadeOut || this.entity == null || !this.entity.isEntityAlive() || this.entity.isDead || !this.entity.world.isBlockLoaded(this.entity.getPosition())
				|| !this.isPlaying.test(this.entity)) {
			this.repeat = false;
			this.fadeOut = true;

			this.volume -= 0.05F;
			if(this.volume <= 0.0F) {
				this.donePlaying = true;
			}
		}
	}

	/**
	 * Stops the sound immediately without fading out
	 */
	public void stopImmediately() {
		this.donePlaying = true;
		this.repeat = false;
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
		return this.donePlaying || this.fadeOut;
	}
}
