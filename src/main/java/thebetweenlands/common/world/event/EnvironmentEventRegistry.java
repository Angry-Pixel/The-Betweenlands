package thebetweenlands.common.world.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.World;

public class EnvironmentEventRegistry {
	public final EventDenseFog DENSE_FOG;
	public final EnvironmentEvent HEAVY_RAIN;
	public final EventAuroras AURORAS;
	public final EventBloodSky BLOODSKY;
	public final EventSpoopy SPOOPY;

	private World world;

	public EnvironmentEventRegistry(World world) {
		this.world = world;

		DENSE_FOG = new EventDenseFog(this);
		HEAVY_RAIN = new EventHeavyRain(this);
		AURORAS = new EventAuroras(this);
		BLOODSKY = new EventBloodSky(this);
		SPOOPY = new EventSpoopy(this);
	}

	public World getWorld() {
		return this.world;
	}

	public void init() {
		register(DENSE_FOG);
		register(HEAVY_RAIN);
		register(AURORAS);
		register(BLOODSKY);
		register(SPOOPY);
	}

	private final Map<String, EnvironmentEvent> REGISTERED_EVENTS = new HashMap<String, EnvironmentEvent>();

	private boolean disabled = false;

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
		return getEventsOfState(true);
	}

	public List<EnvironmentEvent> getEventsOfState(boolean isActive) {
		List<EnvironmentEvent> list = new ArrayList<EnvironmentEvent>();
		for (EnvironmentEvent event : REGISTERED_EVENTS.values()) {
			if (event.isActive() == isActive) {
				list.add(event);
			}
		}
		return list;
	}

	public List<String> getEventNames() {
		List<String> eventNames = new ArrayList<String>();
		for (EnvironmentEvent event : REGISTERED_EVENTS.values()) {
			eventNames.add(event.getEventName());
		}
		return eventNames;
	}

	public List<String> getEventNamesOfState(boolean isActive) {
		List<String> eventNames = new ArrayList<String>();
		for (EnvironmentEvent event : REGISTERED_EVENTS.values()) {
			if (event.isActive() == isActive) {
				eventNames.add(event.getEventName());
			}
		}
		return eventNames;
	}

	public String getGrammaticalActiveEventNameList() {
		StringBuilder list = new StringBuilder();
		EnvironmentEvent[] events = REGISTERED_EVENTS.values().toArray(new EnvironmentEvent[0]);
		for (int i = 0; i < events.length; i++) {
			String eventName = events[i].getEventName();
			if (i > 0) {
				list.append(", ");
				if (i == events.length - 1) {
					list.append("and ");
				}
			}
			list.append(eventName);
		}
		return list.toString();
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void enable() {
		disabled = false;
	}

	public void disable() {
		disabled = true;
	}

	public boolean isEnabled() {
		return !disabled;
	}

	public boolean isDisabled() {
		return disabled;
	}
}
