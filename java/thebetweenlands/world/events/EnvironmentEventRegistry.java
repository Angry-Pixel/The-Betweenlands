package thebetweenlands.world.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import thebetweenlands.world.events.impl.EventAuroras;
import thebetweenlands.world.events.impl.EventDenseFog;
import thebetweenlands.world.events.impl.EventHeavyRain;

public class EnvironmentEventRegistry {
	public final EnvironmentEvent DENSE_FOG = new EventDenseFog();
	public final EnvironmentEvent HEAVY_RAIN = new EventHeavyRain();
	public final EnvironmentEvent AURORAS = new EventAuroras();
	
	public void init() {
		register(DENSE_FOG);
		register(HEAVY_RAIN);
		register(AURORAS);
	}
	
	private final Map<String, EnvironmentEvent> REGISTERED_EVENTS = new HashMap<String, EnvironmentEvent>();
	
	public void register(EnvironmentEvent event) {
		REGISTERED_EVENTS.put(event.getEventName(), event);
	}
	
	public Map<String, EnvironmentEvent> getEvents() {
		return Collections.unmodifiableMap(REGISTERED_EVENTS);
	}
	
	public EnvironmentEvent forName(String eventName) {
		return REGISTERED_EVENTS.get(eventName);
	}
}
