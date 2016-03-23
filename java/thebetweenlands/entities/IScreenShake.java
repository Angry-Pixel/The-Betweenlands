package thebetweenlands.entities;

import net.minecraft.entity.EntityLivingBase;

public interface IScreenShake {
	public float getShakeIntensity(EntityLivingBase viewer, float partialTicks);
}
