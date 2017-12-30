package thebetweenlands.api.capability;

import java.util.List;

import net.minecraft.entity.Entity;

public interface IPuppeteerCapability {
	/**
	 * Returns a list of controlled entities
	 * @return
	 */
	public List<Entity> getPuppets();

	/**
	 * Sets which entity is currently being taken control of
	 * @param entity
	 */
	public void setActivatingEntity(Entity entity);

	/**
	 * Returns which entity is currently being taken control of
	 * @return
	 */
	public Entity getActivatingEntity();

	/**
	 * Returns how many ticks until the entity is taken control of
	 * @return
	 */
	public int getActivatingTicks();

	/**
	 * Sets how many ticks until the entity is taken control of
	 * @param ticks
	 */
	public void setActivatingTicks(int ticks);
}
