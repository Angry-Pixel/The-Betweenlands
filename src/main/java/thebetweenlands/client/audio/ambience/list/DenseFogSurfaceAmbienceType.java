package thebetweenlands.client.audio.ambience.list;

import net.minecraft.sounds.SoundEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.event.DenseFogEvent;
import thebetweenlands.common.world.event.WinterEvent;

public class DenseFogSurfaceAmbienceType extends SurfaceAmbienceType {

	@Override
	public boolean isActive() {
		return DenseFogEvent.isDenseFog(this.getPlayer().level()) && !WinterEvent.isFroooosty(this.getPlayer().level()) && this.getPlayer().getY() > TheBetweenlands.CAVE_START - 15;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_SWAMP_DENSE_FOG.get();
	}
}
