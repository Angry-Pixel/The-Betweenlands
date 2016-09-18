package thebetweenlands.client.audio.ambience.list;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class SurfaceAmbienceType extends AmbienceType {
	@Override
	public boolean isActive() {
		return this.getPlayer().posY > WorldProviderBetweenlands.CAVE_START - 15;
	}

	@Override
	public EnumAmbienceLayer getAmbienceLayer() {
		return EnumAmbienceLayer.OVERLAY;
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
		return SoundRegistry.AMBIENT_SWAMP;
	}

	@Override
	public float getVolume() {
		if(this.getPlayer().posY <= WorldProviderBetweenlands.CAVE_START) {
			return Math.max(1.0F - (float)(WorldProviderBetweenlands.CAVE_START - this.getPlayer().posY) / 15.0F, 0.0F);
		} else {
			return 1.0F;
		}
	}
}
