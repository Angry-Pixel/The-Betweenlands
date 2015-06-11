package thebetweenlands.world.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import thebetweenlands.world.events.impl.EventDenseFog;
import thebetweenlands.world.events.impl.EventHeavyRain;

public class EnvironmentEventRegistry {
	public static EnvironmentEvent DENSE_FOG = new EventDenseFog();
	public static EnvironmentEvent HEAVY_RAIN = new EventHeavyRain();
	
	public static void init() {
		register(DENSE_FOG);
		register(HEAVY_RAIN);
	}
	
	private static final Map<String, EnvironmentEvent> REGISTERED_EVENTS = new HashMap<String, EnvironmentEvent>();
	
	public static void register(EnvironmentEvent event) {
		REGISTERED_EVENTS.put(event.getEventName(), event);
	}
	
	public static Map<String, EnvironmentEvent> getEvents() {
		return Collections.unmodifiableMap(REGISTERED_EVENTS);
	}
	
	public static EnvironmentEvent forName(String eventName) {
		return REGISTERED_EVENTS.get(eventName);
	}
}
