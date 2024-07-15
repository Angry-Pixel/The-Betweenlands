package thebetweenlands.client.audio;

import net.minecraft.sounds.SoundSource;
import thebetweenlands.common.block.entity.DecayPitHangingChainBlockEntity;
import thebetweenlands.common.registries.SoundRegistry;

public class DecayPitChainSoundInstance extends BlockEntitySoundInstance<DecayPitHangingChainBlockEntity> {
	public DecayPitChainSoundInstance(DecayPitHangingChainBlockEntity entity) {
		super(SoundRegistry.CHAIN_LONG.get(), SoundSource.HOSTILE, entity, DecayPitHangingChainBlockEntity::isSlow);
	}

	@Override
	public float getVolume() {
		return 2.0F;
	}

	@Override
	public float getPitch() {
		return 1.2F;
	}
}
