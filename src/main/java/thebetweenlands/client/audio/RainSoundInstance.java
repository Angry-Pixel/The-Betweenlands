package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import thebetweenlands.client.handler.AmbienceHandler;

public class RainSoundInstance extends AbstractTickableSoundInstance {

	public RainSoundInstance(SoundEvent sound, SoundSource source) {
		super(sound, source, SoundInstance.createUnseededRandom());
		this.attenuation = Attenuation.NONE;
	}

	@Override
	public void tick() {
		Entity view = Minecraft.getInstance().getCameraEntity();
		if(view != null) {
			this.x = AmbienceHandler.getRelativeRainX() + (float)view.getX();
			this.y = AmbienceHandler.getRelativeRainY() + (float)view.getY();
			this.z = AmbienceHandler.getRelativeRainZ() + (float)view.getZ();
			this.pitch = 1.0F - AmbienceHandler.getRainAbove() * 0.5F;
			this.volume = (0.5F - AmbienceHandler.getRainAbove() * 0.4F) * AmbienceHandler.getRainVolume();
		}
	}
}
