package thebetweenlands.client.audio.ambience.list;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class CaveAmbienceType extends AmbienceType {

	@Override
	public boolean isActive() {
		return this.getPlayer().getY() <= TheBetweenlands.CAVE_START;
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
	public SoundSource getCategory() {
		return SoundSource.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_CAVE.get();
	}

	@Override
	public float getVolume() {
		if(this.getPlayer().getY() <= TheBetweenlands.CAVE_START) {
			return Mth.clamp((float)(TheBetweenlands.CAVE_START - this.getPlayer().getY()) / 15.0F, 0.0F, 1.0F);
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
