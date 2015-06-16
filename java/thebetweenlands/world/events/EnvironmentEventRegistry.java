package thebetweenlands.world.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thebetweenlands.world.events.impl.EventAuroras;
import thebetweenlands.world.events.impl.EventDenseFog;
import thebetweenlands.world.events.impl.EventHeavyRain;

public class EnvironmentEventRegistry {
	public final EnvironmentEvent DENSE_FOG = new EventDenseFog(this);
	public final EnvironmentEvent HEAVY_RAIN = new EventHeavyRain(this);
	public final EventAuroras AURORAS = new EventAuroras(this);
	
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
	
	public List<EnvironmentEvent> getActiveEvents() {
		List<EnvironmentEvent> list = new ArrayList<EnvironmentEvent>();
		for(EnvironmentEvent event : this.REGISTERED_EVENTS.values()) {
			if(event.isActive()) list.add(event);
		}
		return list;
	}
}
