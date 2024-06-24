package thebetweenlands.api.environment;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface IEnvironmentEventRegistry {
	/**
	 * Registers an environment event
	 * @param event
	 */
	void register(IEnvironmentEvent event);

	/**
	 * Unregisters an environment event
	 * @param event
	 * @return
	 */
	IEnvironmentEvent unregister(IEnvironmentEvent event);

	/**
	 * Returns the world this environment event registry belongs to
	 * @return
	 */
	Level getLevel();

	/**
	 * Returns an unmodifiable map of all registered environment events
	 * @return
	 */
	Map<ResourceLocation, IEnvironmentEvent> getEvents();

	/**
	 * Returns the event with the specified ID or null if that event is not registered
	 * @param eventId
	 * @return
	 */
	@Nullable
	IEnvironmentEvent getEvent(ResourceLocation eventId);

	/**
	 * Returns whether the specified event is registered and {@link IEnvironmentEvent#isActive()} == true
	 * @param eventId
	 * @return
	 */
	boolean isEventActive(ResourceLocation eventId);

	/**
	 * Returns whether the specified event is registered and {@link IEnvironmentEvent#isActiveAt(double, double, double)} == true
	 * @param x
	 * @param y
	 * @param z
	 * @param eventId
	 * @return
	 */
	boolean isEventActiveAt(double x, double y, double z, ResourceLocation eventId);

	/**
	 * Returns a list of all registered events whose {@link IEnvironmentEvent#isActive()} == active
	 * @param active
	 * @return
	 */
	List<IEnvironmentEvent> getEventsOfState(boolean active);

	/**
	 * Returns a list of all registered events whose {@link IEnvironmentEvent#isActiveAt(double, double, double)} == active
	 * @param x
	 * @param y
	 * @param z
	 * @param active
	 * @return
	 */
	List<IEnvironmentEvent> getEventsOfStateAt(double x, double y, double z, boolean active);

	/**
	 * Sets whether the registry is enabled. Environment events are only updated
	 * if the registry is enabled
	 * @param enabled
	 * @return
	 */
	boolean setEnabled(boolean enabled);

	/**
	 * Returns whether the registry is enabled
	 * @return
	 */
	boolean isEnabled();
}
