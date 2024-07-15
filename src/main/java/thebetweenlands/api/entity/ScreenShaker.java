package thebetweenlands.api.entity;

import net.minecraft.world.entity.Entity;

public interface ScreenShaker {
	float getShakeIntensity(Entity viewer);
}
