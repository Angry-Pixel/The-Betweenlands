package thebetweenlands.common.entity;

import net.minecraft.entity.Entity;

public interface IEntityScreenShake {
	public float getShakeIntensity(Entity viewer, float partialTicks);
}
