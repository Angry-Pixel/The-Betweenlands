package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import thebetweenlands.common.entity.mobs.EntityGreeblingVolarpadFloater;
import thebetweenlands.common.registries.SoundRegistry;

public class GreeblingFallSound extends EntitySound<EntityGreeblingVolarpadFloater> {
	public GreeblingFallSound(EntityGreeblingVolarpadFloater entity) {
		super(SoundRegistry.GREEBLING_FALL, SoundCategory.HOSTILE, entity, EntityGreeblingVolarpadFloater::isFloating);
		repeat = false;
	}
	@Override
	public float getVolume() {
		return 1F;
	}
	
	@Override
	public float getPitch() {
		return 1F;
	}
}
