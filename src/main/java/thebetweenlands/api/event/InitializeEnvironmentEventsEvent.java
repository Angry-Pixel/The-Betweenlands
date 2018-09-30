package thebetweenlands.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import thebetweenlands.api.environment.IEnvironmentEventRegistry;

public class InitializeEnvironmentEventsEvent extends Event {
	private final IEnvironmentEventRegistry registry;

	public InitializeEnvironmentEventsEvent(IEnvironmentEventRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Returns the environment event registry
	 * @return
	 */
	public IEnvironmentEventRegistry getRegistry() {
		return this.registry;
	}
}
