package thebetweenlands.common.world.event;

import thebetweenlands.api.environment.EnvironmentEvent;

public abstract class BLEnvironmentEvent extends EnvironmentEvent {
	protected final BLEnvironmentEventRegistry blRegistry;

	public BLEnvironmentEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
		this.blRegistry = registry;
	}

	@Override
	public BLEnvironmentEventRegistry getRegistry() {
		return this.blRegistry;
	}
}
