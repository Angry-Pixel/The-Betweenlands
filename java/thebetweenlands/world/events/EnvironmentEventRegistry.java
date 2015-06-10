package thebetweenlands.world.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EnvironmentEventRegistry {
	public static final EnvironmentEvent DENSE_FOG = new EnvironmentEvent() {
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
	};

	public static void init() {
		register(DENSE_FOG);
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
