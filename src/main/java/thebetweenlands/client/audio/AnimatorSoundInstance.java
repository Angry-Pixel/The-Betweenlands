package thebetweenlands.client.audio;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;

public class AnimatorSoundInstance extends BlockEntitySoundInstance<AnimatorBlockEntity> {

	public AnimatorSoundInstance(SoundEvent sound, SoundSource source, AnimatorBlockEntity animator) {
		super(sound, source, animator, AnimatorBlockEntity::isRunning);
		this.volume = 0.01F;
	}

	@Override
	public void tick() {
		if(this.volume < 0.5F && !this.isStopping()) {
			this.volume += 0.05F;
		}
		super.tick();
	}
}
