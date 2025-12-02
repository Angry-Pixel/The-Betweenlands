package thebetweenlands.client.audio.ambience;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class AmbienceSoundInstance extends AbstractTickableSoundInstance {

	private boolean fadeOut = false;
	private boolean isLowPriority = false;

	public final Player player;
	public final AmbienceType type;
	public final AmbienceManager manager;

	public AmbienceSoundInstance(SoundEvent sound, SoundSource source, AmbienceType type, Player player, AmbienceManager manager) {
		super(sound, source, SoundInstance.createUnseededRandom());
		this.type = type;
		this.player = player;
		this.looping = true;
		this.attenuation = Attenuation.NONE;
		this.volume = 0.1F; //Start at 0.1 and fade in
		this.pitch = this.type.getPitch();
		this.manager = manager;
	}

	@Override
	public void tick() {
		if (this.player == null) {
			this.stop();
			return;
		}

		this.x = (float) this.player.getX();
		this.y = (float) this.player.getY();
		this.z = (float) this.player.getZ();

		if (!this.isStopped())
			this.pitch = this.type.getPitch();

		float desiredVolume = this.type.getVolume();
		int fadeTicks = Math.max(this.type.getFadeTime(), 1);
		float incr = Math.max((this.volume + Math.abs(desiredVolume - this.volume)) / (float) fadeTicks, 0.001F);

		if (this.isStopped())
			desiredVolume = 0.0F;

		if (this.isLowPriority)
			desiredVolume = Math.min(desiredVolume, this.manager.getLowerPriorityVolume());

		if (this.volume > desiredVolume) {
			this.volume -= incr;
			if (this.volume < desiredVolume) {
				this.volume = desiredVolume;
				if (this.isStopped())
					this.stop();
			}
		} else if (this.volume < desiredVolume) {
			this.volume += incr;
			if (this.volume > desiredVolume) {
				this.volume = desiredVolume;
			}
		}

		if (this.volume <= 0.0F && this.isStopped())
			this.stop();
	}

	public boolean isFadingOut() {
		return this.fadeOut;
	}

	public void cancelFade() {
		this.fadeOut = false;
	}

	@Override
	public boolean isStopped() {
		return super.isStopped() || this.fadeOut || !this.type.isActive();
	}

	void setLowPriority(boolean lowPriority) {
		this.isLowPriority = lowPriority;
	}

	//only necessary as stop is protected and final
	public void stopSound() {
		this.stop();
	}
}
