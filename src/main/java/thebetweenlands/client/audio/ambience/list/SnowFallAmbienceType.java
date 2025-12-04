package thebetweenlands.client.audio.ambience.list;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.event.SnowfallEvent;

public class SnowFallAmbienceType extends AmbienceType {

	@Override
	public boolean isActive() {
		return SnowfallEvent.getSnowingStrength(this.getPlayer().level()) > 0;
	}

	@Override
	public AmbienceLayer getAmbienceLayer() {
		return AmbienceRegistry.BASE_LAYER;
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public SoundSource getCategory() {
		return SoundSource.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_SNOWFALL.get();
	}

	@Override
	public float getVolume() {
		float volume;
		if(this.getPlayer().getY() <= TheBetweenlands.CAVE_START) {
			//Fade out when going down
			volume = Mth.clamp(1.0F - (float)(TheBetweenlands.CAVE_START - this.getPlayer().getY()) / 15.0F, 0.0F, 1.0F);
		} else if(this.getPlayer().getY() >= TheBetweenlands.LAYER_HEIGHT + 10) {
			//Fade out when going up
			volume = Mth.clamp(1.0F - (float)(this.getPlayer().getY() - (TheBetweenlands.LAYER_HEIGHT + 10)) / 40.0F, 0.0F, 1.0F);
		} else {
			volume = 1.0F;
		}
		float strength = SnowfallEvent.getSnowingStrength(this.getPlayer().level()) / 8.0F;
		return volume * (0.08F + Math.min(strength * strength * 0.22F, 0.22F));
	}

	@Override
	public float getLowerPriorityVolume() {
		return 1.0F - Math.min(SnowfallEvent.getSnowingStrength(this.getPlayer().level()) / 8.0F, 0.9F);
	}
}
