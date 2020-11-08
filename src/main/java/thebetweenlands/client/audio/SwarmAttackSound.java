package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.ISwarmedCapability;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

@SideOnly(Side.CLIENT)
public class SwarmAttackSound extends SafeStreamSound {
	private boolean fadeOut = false;

	public SwarmAttackSound(SoundEvent sound, SoundCategory category) {
		super(sound, category);
		this.volume = 0.001f;
		this.repeat = true;
		this.attenuationType = AttenuationType.LINEAR;
	}

	public SwarmAttackSound() {
		this(SoundRegistry.SWARM_ATTACK, SoundCategory.HOSTILE);
	}

	@Override
	public void update() {
		super.update();

		boolean shouldFadeOut = false;

		if(!this.fadeOut) {
			Entity view = Minecraft.getMinecraft().getRenderViewEntity();

			if(view != null) {
				this.xPosF = (float) view.posX;
				this.yPosF = (float) view.posY;
				this.zPosF = (float) view.posZ;

				float targetVolume = 0;

				ISwarmedCapability cap = view.getCapability(CapabilityRegistry.CAPABILITY_SWARMED, null);

				if(cap != null) {
					targetVolume = cap.getSwarmedStrength() * 0.5f;
				}

				if(this.volume > targetVolume) {
					this.volume -= 0.05f;

					if(this.volume < targetVolume) {
						this.volume = targetVolume;

						if(targetVolume <= 0) {
							this.repeat = false;
							this.donePlaying = true;
						}
					}
				} else if(this.volume < targetVolume) {
					this.volume += 0.05f;

					if(this.volume > targetVolume) {
						this.volume = targetVolume;
					}
				}
			} else {
				this.stopImmediately();
			}
		}

		if(this.fadeOut || shouldFadeOut) {
			this.volume -= 0.05F;
			if(this.volume <= 0.0F) {
				this.repeat = false;
				this.donePlaying = true;
				this.volume = 0;
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
