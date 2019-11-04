package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import thebetweenlands.common.entity.EntityMovingWall;
import thebetweenlands.common.registries.SoundRegistry;

public class MovingWallSound extends EntitySound<EntityMovingWall> {
	public MovingWallSound(EntityMovingWall wall) {
		super(SoundRegistry.WALL_SLIDE, SoundCategory.HOSTILE, wall, EntityMovingWall::isMoving);
		repeat = true;
	}
	@Override
	public float getVolume() {
		return 0.5F;
	}
	
	@Override
	public float getPitch() {
		return 1F;
	}
}