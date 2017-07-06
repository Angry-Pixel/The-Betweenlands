package thebetweenlands.client.audio.ambience.list;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class CaveAmbienceType extends AmbienceType {
	@Override
	public boolean isActive() {
		return this.getPlayer().posY <= WorldProviderBetweenlands.CAVE_START;
	}

	@Override
	public AmbienceLayer getAmbienceLayer() {
		return AmbienceRegistry.BASE_LAYER;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_CAVE;
	}

	@Override
	public float getVolume() {
		if(this.getPlayer().posY <= WorldProviderBetweenlands.CAVE_START) {
			return MathHelper.clamp((float)(WorldProviderBetweenlands.CAVE_START - this.getPlayer().posY) / 15.0F, 0.0F, 1.0F);
		} else {
			return 1.0F;
		}
	}

	@Override
	public float getLowerPriorityVolume() {
		//Don't stop other lower priority ambiences from playing
		return 1.0F;
	}
}
