package thebetweenlands.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

public interface ICameraOffset {
	public boolean applyOffset(EntityLivingBase view, float partialTicks);
}
