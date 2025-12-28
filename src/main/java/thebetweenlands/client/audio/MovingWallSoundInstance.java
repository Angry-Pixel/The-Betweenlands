package thebetweenlands.client.audio;

import thebetweenlands.common.entity.MovingWall;
import thebetweenlands.common.registries.SoundRegistry;

public class MovingWallSoundInstance extends DefaultEntitySoundInstance<MovingWall> {

	public MovingWallSoundInstance(MovingWall entity) {
		super(SoundRegistry.WALL_SLIDE.get(), entity.getSoundSource(), entity, MovingWall::isMoving, 1.0F);
		this.looping = true;
	}

	@Override
	public float getVolume() {
		return 0.5F;
	}

	@Override
	public float getPitch() {
		return 1.0F;
	}
}
