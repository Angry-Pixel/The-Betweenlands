package thebetweenlands.api.environment;

import com.google.common.collect.ImmutableMap;

public interface IRemotelyControllableEnvironmentEvent extends IEnvironmentEvent {
	/**
	 * Returns whether this event can be controlled by the remote
	 * @return
	 */
	default boolean isRemotelyControllable() {
		return true;
	}

	/**
	 * Called <i>once</i> when the remote no longer affects this event's state and {@link #isCurrentStateFromRemote()} returns true.
	 * This will only be called when the remote states are updated,
	 * the event is responsible handling its own remote reset ticks received from
	 * {@link #updateStateFromRemote(boolean, int, ImmutableMap)} and resetting the state
	 * if the data was not downloaded and the remote reset ticks run out
	 */
	void resetStateFromRemote();

	/**
	 * Returns whether the current event state was set by the remote.
	 * Should be false after {@link #resetStateFromRemote()} was called
	 * or if the event's state was overridden (e.g. by a player, command, etc)
	 * @return
	 */
	boolean isCurrentStateFromRemote();

	/**
	 * Called when the remote states are updated but no
	 * data was available for this event
	 */
	void updateNoStateFromRemote();

	/**
	 * Updates this event's state from the remote. Called when the
	 * remote states are updated
	 * @param active The event's active state
	 * @param remoteResetTicks If the client/server doesn't pull the data within two
	 * attempts the reset timeout ticks will start to count down.
	 * Once it runs out the events reset their state to their default values.
	 * Default timeout ticks value is 3600 (3 min.).
	 * @param data Additional json entries
	 */
	void updateStateFromRemote(boolean active, int remoteResetTicks, ImmutableMap<String, String> data);
}
