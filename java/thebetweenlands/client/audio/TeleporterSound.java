package thebetweenlands.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.entities.mobs.boss.fortress.EntityFortressBossTeleporter;

/**
 * Plays a sound at the entity position and cancels the sound when the teleporter stops
 */
public class TeleporterSound extends MovingSound {
	public final EntityFortressBossTeleporter entity;
	public final Entity target;
	public final float originalVolume;
	public final float originalPitch;

	public TeleporterSound(EntityFortressBossTeleporter entity, float volume, float pitch) {
		super(new ResourceLocation("thebetweenlands:fortressTeleport"));
		this.target = entity.getTarget();
		this.entity = entity;
		this.field_147666_i = ISound.AttenuationType.LINEAR;
		this.volume = this.originalVolume = volume;
		this.field_147663_c = this.originalPitch = pitch;
	}

	@Override
	public void update() {
		if(this.entity != null && this.target != null) {
			if(this.entity.getTarget() == null || this.entity.getTarget() != this.target) {
				this.stop();
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
