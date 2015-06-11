package thebetweenlands.world.events.impl;

import java.util.Random;

import thebetweenlands.world.events.TimedEnvironmentEvent;

public class EventDenseFog extends TimedEnvironmentEvent {
	@Override
	public String getEventName() {
		return "Dense Fog";
	}
	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(60000) + 6000;
	}
	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(12000) + 6000;
	}
}
