package thebetweenlands.common.world.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.api.event.InitializeEnvironmentEventsEvent;

public class EnvironmentEventRegistry {
	public final EventDenseFog denseFog;
	public final EnvironmentEvent heavyRain;
	public final EventAuroras auroras;
	public final EventBloodSky bloodSky;
	public final EventSpoopy spoopy;
	public final EventWinter winter;

	private World world;

	public EnvironmentEventRegistry(World world) {
		this.world = world;

		denseFog = new EventDenseFog(this);
		heavyRain = new EventHeavyRain(this);
		auroras = new EventAuroras(this);
		bloodSky = new EventBloodSky(this);
		spoopy = new EventSpoopy(this);
		winter = new EventWinter(this);
	}

	public World getWorld() {
		return this.world;
	}

	public void init() {
		register(denseFog);
		register(heavyRain);
		register(auroras);
		register(bloodSky);
		register(spoopy);
		register(winter);
		
		MinecraftForge.EVENT_BUS.post(new InitializeEnvironmentEventsEvent(this));
	}

	private final Map<ResourceLocation, EnvironmentEvent> registeredEvents = new HashMap<>();

	private boolean disabled = false;

	public void register(EnvironmentEvent event) {
		if(registeredEvents.containsKey(event.getEventName())) {
			throw new RuntimeException("Duplicate environment event name: " + event.getEventName());
		}
		registeredEvents.put(event.getEventName(), event);
	}
	
	public EnvironmentEvent unregister(EnvironmentEvent event) {
		return registeredEvents.remove(event.getEventName());
	}

	public Map<ResourceLocation, EnvironmentEvent> getEvents() {
		return Collections.unmodifiableMap(registeredEvents);
	}

	public EnvironmentEvent forName(ResourceLocation eventName) {
		return registeredEvents.get(eventName);
	}

	public List<EnvironmentEvent> getActiveEvents() {
		return getEventsOfState(true);
	}

	public List<EnvironmentEvent> getEventsOfState(boolean isActive) {
		List<EnvironmentEvent> list = new ArrayList<EnvironmentEvent>();
		for (EnvironmentEvent event : registeredEvents.values()) {
			if (event.isActive() == isActive) {
				list.add(event);
			}
		}
		return list;
	}

	public List<ResourceLocation> getEventNames() {
		List<ResourceLocation> eventNames = new ArrayList<>();
		for (EnvironmentEvent event : registeredEvents.values()) {
			eventNames.add(event.getEventName());
		}
		return eventNames;
	}

	public List<ResourceLocation> getEventNamesOfState(boolean isActive) {
		List<ResourceLocation> eventNames = new ArrayList<>();
		for (EnvironmentEvent event : registeredEvents.values()) {
			if (event.isActive() == isActive) {
				eventNames.add(event.getEventName());
			}
		}
		return eventNames;
	}

	public String getGrammaticalActiveEventNameList() {
		StringBuilder list = new StringBuilder();
		EnvironmentEvent[] events = registeredEvents.values().toArray(new EnvironmentEvent[0]);
		for (int i = 0; i < events.length; i++) {
			ResourceLocation eventName = events[i].getEventName();
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
