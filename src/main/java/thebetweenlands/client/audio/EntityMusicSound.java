package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityMusic;

@SideOnly(Side.CLIENT)
public class EntityMusicSound<T extends Entity> extends EntitySound<T> {
	public final IEntityMusic music;
	public final float originalVolume;

	protected boolean mustFadeOut = false;
	
	public EntityMusicSound(SoundEvent sound, SoundCategory category, T entity, IEntityMusic music, float volume, AttenuationType attenuationType) {
		super(sound, category, entity, e -> true);
		this.repeat = true;
		this.attenuationType = attenuationType;

		this.music = music;
		this.xPosF = (float) this.entity.posX;
		this.yPosF = (float) this.entity.posY;
		this.zPosF = (float) this.entity.posZ;
		this.originalVolume = volume;
		this.volume = 0.1F;
	}

	@Override
	public void update() {
		this.updateSafeStreamSound();

		if(this.entity != null && !this.entity.isDead) {
			if(this.mustFadeOut) {
				if(this.volume > 0) {
					this.volume -= 0.1F;
					if(this.volume <= 0.0F) {
						this.volume = 0;
						this.donePlaying = true;
					}
				}
			} else {
				if(!this.music.isMusicActive(Minecraft.getMinecraft().player) || this.entity.getDistance(Minecraft.getMinecraft().player) > this.music.getMusicRange(Minecraft.getMinecraft().player)) {
					this.repeat = false;
					this.fadeOut = true;
	
					if(this.volume > 0) {
						this.volume -= 0.1F;
						if(this.volume <= 0.0F) {
							this.volume = 0;
							this.donePlaying = true;
						}
					}
				} else {
					if(this.fadeOut) {
						this.cancelFade();
						this.repeat = true;
					}
					if(this.volume < this.originalVolume) {
						this.volume += 0.1F;
						if(this.volume > this.originalVolume) {
							this.volume = this.originalVolume;
						}
					}
				}
				this.xPosF = (float) this.entity.posX;
				this.yPosF = (float) this.entity.posY;
				this.zPosF = (float) this.entity.posZ;
			}
		} else {
			this.donePlaying = true;
			this.volume = 0;
		}
	}
	
	@Override
	public void stopEntityMusic() {
		super.stopEntityMusic();
		this.mustFadeOut = true;
	}
}
