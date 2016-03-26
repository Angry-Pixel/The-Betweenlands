package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.entities.IEntityMusic;

/**
 * Plays a sound at the entity position and cancels the sound when the entity dies
 */
public class EntityMusicSound extends MovingSound {
	public final Entity entity;
	public final IEntityMusic music;
	public final float originalVolume;
	public final float originalPitch;

	public EntityMusicSound(Entity entity, ResourceLocation sound, float volume, float pitch) {
		super(sound);
		this.entity = entity;
		this.music = (IEntityMusic) entity;
		this.field_147666_i = ISound.AttenuationType.NONE;
		this.originalVolume = volume;
		this.volume = 0.1F;
		this.field_147663_c = this.originalPitch = pitch;
	}

	@Override
	public void update() {
		if(this.entity != null && !this.entity.isDead) {
			if(!this.music.isMusicActive(Minecraft.getMinecraft().thePlayer) || this.entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) > this.music.getMusicRange(Minecraft.getMinecraft().thePlayer)) {
				this.volume -= 0.1F;
				if(this.volume <= 0.0F) {
					this.stop();
				}
			} else if(this.volume < this.originalVolume) {
				this.volume += 0.1F;
				if(this.volume > this.originalVolume)
					this.volume = this.originalVolume;
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
