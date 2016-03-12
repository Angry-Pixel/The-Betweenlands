package thebetweenlands.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Plays a sound at the entity position and cancels the sound when the entity dies
 */
public class EntityIdleSound extends MovingSound {
	public final Entity entity;
	public final float originalVolume;
	public final float originalPitch;

	public EntityIdleSound(Entity entity, ResourceLocation sound, float volume, float pitch) {
		super(sound);
		this.entity = entity;
		this.field_147666_i = ISound.AttenuationType.LINEAR;
		this.volume = this.originalVolume = volume;
		this.field_147663_c = this.originalPitch = pitch;
	}

	@Override
	public void update() {
		if(this.entity != null) {
			if(!this.entity.isEntityAlive()) {
				this.volume -= 0.1F;
				if(this.volume <= 0.0F) {
					this.stop();
				}
			}
			this.xPosF = (float) this.entity.posX;
			this.yPosF = (float) this.entity.posY;
			this.zPosF = (float) this.entity.posZ;
		} else {
			this.stop();
		}
	}

	public void stop() {
		this.donePlaying = true;
	}
}
