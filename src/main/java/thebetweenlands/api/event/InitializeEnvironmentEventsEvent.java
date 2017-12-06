package thebetweenlands.api.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;

public class InitializeEnvironmentEventsEvent extends Event {
	private final EnvironmentEventRegistry registry;

	public InitializeEnvironmentEventsEvent(EnvironmentEventRegistry registry) {
		this.registry = registry;
	}

	public EnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.registry;
	}
}
