package thebetweenlands.api.entity;

import net.minecraft.entity.Entity;

public interface IEntityScreenShake {
	@Deprecated
	public float getShakeIntensity(Entity viewer, float partialTicks);
	
	public default float getShakeIntensity(Entity viewer) {
		return this.getShakeIntensity(viewer, 0);
	}
}
