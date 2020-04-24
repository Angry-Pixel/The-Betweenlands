package thebetweenlands.client.audio.ambience.list;

import net.minecraft.util.SoundEvent;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.event.EventDenseFog;
import thebetweenlands.common.world.event.EventWinter;

public class DenseFogSurfaceAmbienceType extends SurfaceAmbienceType {
	@Override
	public boolean isActive() {
		return EventDenseFog.isDenseFog(this.getPlayer().world) && !EventWinter.isFroooosty(this.getPlayer().world) && this.getPlayer().posY > WorldProviderBetweenlands.CAVE_START - 15;
	}
	
	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_SWAMP_DENSE_FOG;
	}
}
