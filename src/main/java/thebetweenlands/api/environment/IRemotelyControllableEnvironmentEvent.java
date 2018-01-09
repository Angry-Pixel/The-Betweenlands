package thebetweenlands.api.environment;

import com.google.gson.JsonObject;

public interface IRemotelyControllableEnvironmentEvent extends IEnvironmentEvent {
	/**
	 * Returns whether this event can be controlled by the remote
	 * @return
	 */
	public default boolean isRemotelyControllable() {
		return true;
	}

	/**
	 * Called <i>once</i> when the remote no longer affects this event's state and {@link #isCurrentStateFromRemote()} returns true.
	 * This will only be called when the remote's data was successfully downloaded,
	 * the event is responsible handling its own remote reset ticks received from
	 * {@link #updateStateFromRemote(boolean, int, JsonObject)} and resetting the state
	 * if the data was not downloaded and the remote reset ticks run out
	 */
	public void resetStateFromRemote();

	/**
	 * Returns whether the current event state was set by the remote.
	 * Should be false after {@link #resetStateFromRemote()} was called
	 * or if the event's state was overridden (e.g. by a player, command, etc)
	 * @return
	 */
	public boolean isCurrentStateFromRemote();

	/**
	 * Called when the data was downloaded from the remote but no
	 * data was available for this event
	 */
	public void updateNoStateFromRemote();

	/**
	 * Updates this event's state from the remote. Called every time the
	 * data is downloaded from the remote
	 * @param active The event's active state
	 * @param remoteResetTicks If the client/server doesn't pull the data within two
	 * attempts the reset timeout ticks will start to count down. 
	 * Once it runs out the events reset their state to their default values. 
	 * Default timeout ticks value is 3600 (3 min.).
	 * @param data Json for additional data
	 */
	public void updateStateFromRemote(boolean active, int remoteResetTicks, JsonObject data);
}
