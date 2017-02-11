package thebetweenlands.api.entity;

import net.minecraft.entity.Entity;

public interface IEntityCameraOffset {
	public boolean applyOffset(Entity view, float partialTicks);
}
