package thebetweenlands.api.capability;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;

public interface IPuppetCapability {
	/**
	 * Sets the entity's puppetteer
	 * @param puppeteer
	 */
	public void setPuppeteer(@Nullable Entity puppeteer);

	/**
	 * Returns whether this entity has a puppetteer
	 * @return
	 */
	public boolean hasPuppeteer();
	
	/**
	 * Returns this entity's puppetteer
	 * @return
	 */
	public Entity getPuppeteer();

	/**
	 * Sets how many ticks remaining
	 * @param ticks
	 */
	public void setRemainingTicks(int ticks);

	/**
	 * Returns how many ticks remaining
	 * @return
	 */
	public int getRemainingTicks();
}
