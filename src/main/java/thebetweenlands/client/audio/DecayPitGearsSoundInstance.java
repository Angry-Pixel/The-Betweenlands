package thebetweenlands.client.audio;

import net.minecraft.sounds.SoundSource;
import thebetweenlands.common.block.entity.DecayPitControlBlockEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class DecayPitGearsSoundInstance extends BlockEntitySoundInstance<DecayPitControlBlockEntity> {

	public DecayPitGearsSoundInstance(DecayPitControlBlockEntity entity) {
		super(SoundRegistry.GEARS.get(), SoundSource.HOSTILE, entity, DecayPitControlBlockEntity::isUnPlugged);
		this.looping = true;
	}
	@Override
	public float getVolume() {
		return 1.0F;
	}

	@Override
	public float getPitch() {
		return 1.0F;
	}
}
