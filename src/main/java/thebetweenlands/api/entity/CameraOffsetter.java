package thebetweenlands.api.entity;

import net.minecraft.world.entity.Entity;

public interface CameraOffsetter {
	boolean applyOffset(Entity view, float partialTicks);
}
