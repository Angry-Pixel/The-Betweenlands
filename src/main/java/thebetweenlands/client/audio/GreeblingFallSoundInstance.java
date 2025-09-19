package thebetweenlands.client.audio;

import thebetweenlands.common.entity.creature.GreeblingVolarpadFloater;
import thebetweenlands.common.registries.SoundRegistry;

public class GreeblingFallSoundInstance extends DefaultEntitySoundInstance<GreeblingVolarpadFloater>{

	public GreeblingFallSoundInstance(GreeblingVolarpadFloater entity) {
		super(SoundRegistry.GREEBLING_FALL.get(), entity.getSoundSource(), entity, GreeblingVolarpadFloater::isFloating, 1.0F);
		this.looping = false;
	}
}
