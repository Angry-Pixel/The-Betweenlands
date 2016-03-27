package thebetweenlands.client.audio.ambience;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class AmbienceSound extends MovingSound {
	private boolean fadeOut = false;
	private boolean isLowPriority = false;

	public final EntityPlayer player;
	public final AmbienceType type;
	public final AmbienceManager mgr;

	public AmbienceSound(AmbienceType type, EntityPlayer player, ResourceLocation sound, AmbienceManager mgr) {
		super(sound);
		this.type = type;
		this.player = player;
		this.repeat = true;
		this.field_147666_i = AttenuationType.NONE;
		this.volume = 0.1F; //Start at 0.1 and fade in
		this.field_147663_c = this.type.getPitch();
		this.mgr = mgr;
	}

	@Override
	public void update() {
		if(!this.donePlaying)
			this.field_147663_c = this.type.getPitch();

		float desiredVolume = this.type.getVolume();
		int fadeTicks = Math.max(this.type.getFadeTime(), 1);
		float incr = Math.max(desiredVolume / (float)fadeTicks, 0.001F);
		if(this.isStopping())
			desiredVolume = 0.0F;
		if(this.isLowPriority)
			desiredVolume = Math.min(desiredVolume, this.mgr.getLowerPriorityVolume());

		if(this.volume > desiredVolume) {
			this.volume -= incr;
			if(this.volume < desiredVolume) {
				this.volume = desiredVolume;
				if(this.isStopping())
					this.donePlaying = true;
			}
		} else if(this.volume < desiredVolume) {
			this.volume += incr;
			if(this.volume > desiredVolume) {
				this.volume = desiredVolume;
			}
		}
	}

	/**
	 * Stops the ambience and makes it fade out
	 */
	public void stop() {
		this.fadeOut = true;
	}

	/**
	 * Returns whether this sound is currently fading out
	 */
	public boolean isFadingOut() {
		return this.fadeOut;
	}

	/**
	 * Cancels the fade out
	 */
	public void cancelFade() {
		this.fadeOut = false;
	}

	/**
	 * Stops the ambience immediately without fading out
	 */
	public void stopImmediately() {
		this.donePlaying = true;
	}

	/**
	 * Returns whether this ambience is currently stopped or fading out
	 * @return
	 */
	public boolean isStopping() {
		return this.donePlaying || this.fadeOut || !this.type.isActive();
	}

	void setLowPriority(boolean lowPriority) {
		this.isLowPriority = lowPriority;
	}
}
