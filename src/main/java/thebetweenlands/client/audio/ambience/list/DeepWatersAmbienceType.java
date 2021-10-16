package thebetweenlands.client.audio.ambience.list;

import net.minecraft.util.SoundEvent;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EventDenseFog;
import thebetweenlands.common.world.event.EventWinter;

public class DeepWatersAmbienceType extends SurfaceAmbienceType {
	@Override
	public boolean isActive() {
		return super.isActive() && this.getPlayer().world.getBiome(this.getPlayer().getPosition()) == BiomeRegistry.DEEP_WATERS;
	}
	
	@Override
	public int getPriority() {
		return 1;
	}
	
	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_DEEP_WATERS;
	}
	
	@Override
	public float getLowerPriorityVolume() {
		return -1.0f;
	}
}
