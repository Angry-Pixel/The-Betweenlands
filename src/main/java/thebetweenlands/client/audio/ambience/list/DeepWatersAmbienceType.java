package thebetweenlands.client.audio.ambience.list;

import net.minecraft.sounds.SoundEvent;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class DeepWatersAmbienceType extends SurfaceAmbienceType {

	@Override
	public boolean isActive() {
		return super.isActive() && this.getPlayer().level().getBiome(this.getPlayer().blockPosition()).is(BiomeRegistry.DEEP_WATERS);
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_DEEP_WATERS.get();
	}

	@Override
	public float getLowerPriorityVolume() {
		return -1.0F;
	}
}
