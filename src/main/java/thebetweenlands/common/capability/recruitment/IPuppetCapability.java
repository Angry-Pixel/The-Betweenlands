package thebetweenlands.common.capability.recruitment;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;

public interface IPuppetCapability {
	public void setPuppeteer(@Nullable Entity puppeteer);

	public boolean hasPuppeteer();
	
	public Entity getPuppeteer();

	public void setRemainingTicks(int ticks);

	public int getRemainingTicks();
}
