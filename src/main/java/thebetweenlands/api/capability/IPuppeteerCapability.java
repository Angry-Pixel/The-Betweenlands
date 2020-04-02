package thebetweenlands.api.capability;

import java.util.List;

import javax.annotation.Nullable;

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
	
	/**
	 * PM protection shield
	 * @return
	 */
	@Nullable
	public default ProtectionShield getShield() {
		return null;
	}
	
	/**
	 * Updates the shield rotation ticks
	 */
	public default void updateShield() {
		
	}
	
	/**
	 * Shield rotation ticks
	 * @return
	 */
	public default int getShieldRotationTicks() {
		return 0;
	}
	
	/**
	 * Previous tick shield rotation ticks
	 * @return
	 */
	public default int getPrevShieldRotationTicks() {
		return 0;
	}
}
