package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityMusic;

import java.util.function.Predicate;

@SideOnly(Side.CLIENT)
public class EntityMusicSound<T extends Entity> extends EntitySound<T> {
	public final IEntityMusic music;
	public final float originalVolume;

	public EntityMusicSound(SoundEvent sound, SoundCategory category, T entity, float volume, AttenuationType attenuationType) {
		super(sound, category, entity, e -> true);
		this.repeat = true;
		this.attenuationType = attenuationType;

		this.music = (IEntityMusic) entity;
		this.xPosF = (float) this.entity.posX;
		this.yPosF = (float) this.entity.posY;
		this.zPosF = (float) this.entity.posZ;
		this.originalVolume = volume;
		this.volume = 0.1F;
	}

	@Override
	public void update() {
		if(this.entity != null && !this.entity.isDead) {
			if(fadeOut || !this.music.isMusicActive(Minecraft.getMinecraft().player) || this.entity.getDistance(Minecraft.getMinecraft().player) > this.music.getMusicRange(Minecraft.getMinecraft().player)) {
				this.repeat = false;
				this.fadeOut = true;

				this.volume -= 0.1F;
				if(this.volume <= 0.0F) {
					this.donePlaying = true;
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
			this.donePlaying = true;
		}
	}

}
