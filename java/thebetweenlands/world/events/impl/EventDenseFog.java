package thebetweenlands.world.events.impl;

import java.util.Random;

import thebetweenlands.world.events.EnvironmentEventRegistry;
import thebetweenlands.world.events.TimedEnvironmentEvent;

public class EventDenseFog extends TimedEnvironmentEvent {
	public EventDenseFog(EnvironmentEventRegistry registry) {
		super(registry);
	}
	@Override
	public String getEventName() {
		return "denseFog";
	}
	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(40000) + 6000;
	}
	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(20000) + 6000;
	}
}
