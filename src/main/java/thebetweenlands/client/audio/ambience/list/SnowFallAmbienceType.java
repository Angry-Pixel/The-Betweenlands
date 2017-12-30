package thebetweenlands.client.audio.ambience.list;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EventSnowfall;

public class SnowFallAmbienceType extends AmbienceType {
	@Override
	public boolean isActive() {
		return EventSnowfall.getSnowingStrength(this.getPlayer().world) > 0;
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
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_SNOWFALL;
	}

	@Override
	public float getVolume() {
		float volume;
		if(this.getPlayer().posY <= WorldProviderBetweenlands.CAVE_START) {
			//Fade out when going down
			volume = MathHelper.clamp(1.0F - (float)(WorldProviderBetweenlands.CAVE_START - this.getPlayer().posY) / 15.0F, 0.0F, 1.0F);
		} else if(this.getPlayer().posY >= WorldProviderBetweenlands.LAYER_HEIGHT + 10) {
			//Fade out when going up
			volume = MathHelper.clamp(1.0F - (float)(this.getPlayer().posY - (WorldProviderBetweenlands.LAYER_HEIGHT + 10)) / 40.0F, 0.0F, 1.0F);
		} else {
			volume = 1.0F;
		}
		float strength = EventSnowfall.getSnowingStrength(this.getPlayer().world) / 8.0F;
		return volume * (0.08F + Math.min(strength * strength * 0.22F, 0.22F));
	}

	@Override
	public float getLowerPriorityVolume() {
		return 1.0F - Math.min(EventSnowfall.getSnowingStrength(this.getPlayer().world) / 8.0F, 0.9F);
	}
}
