package thebetweenlands.common.world.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.environment.IEnvironmentEventRegistry;
import thebetweenlands.api.event.InitializeEnvironmentEventsEvent;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class BLEnvironmentEventRegistry implements IEnvironmentEventRegistry {
	public final EventDenseFog denseFog;
	public final EventHeavyRain heavyRain;
	public final EventAuroras auroras;
	public final EventBloodSky bloodSky;
	public final EventSpoopy spoopy;
	public final EventWinter winter;
	public final EventSnowfall snowfall;
	public final EventThunderstorm thunderstorm;
	public final EventRift rift;
	
	private World world;

	public BLEnvironmentEventRegistry(World world) {
		this.world = world;

		denseFog = new EventDenseFog(this);
		heavyRain = new EventHeavyRain(this);
		auroras = new EventAuroras(this);
		bloodSky = new EventBloodSky(this);
		spoopy = new EventSpoopy(this);
		winter = new EventWinter(this);
		snowfall = new EventSnowfall(this);
		thunderstorm = new EventThunderstorm(this);
		rift = new EventRift(this);
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	public void init() {
		//Only add events to the dimension
		if(this.world.provider instanceof WorldProviderBetweenlands) {
			register(denseFog);
			register(heavyRain);
			register(auroras);
			register(bloodSky);
			register(spoopy);
			register(winter);
			register(snowfall);
			register(thunderstorm);
			register(rift);
		}

		MinecraftForge.EVENT_BUS.post(new InitializeEnvironmentEventsEvent(this));
	}

	private final Map<ResourceLocation, IEnvironmentEvent> registeredEvents = new HashMap<>();

	private boolean disabled = false;

	@Override
	public void register(IEnvironmentEvent event) {
		if(registeredEvents.containsKey(event.getEventName())) {
			throw new RuntimeException("Duplicate environment event name: " + event.getEventName());
		}
		if(event.getRegistry() != this) {
			throw new RuntimeException(String.format("Environment event %s is already registered in another registry: %s", event.getEventName(), this));
		}
		registeredEvents.put(event.getEventName(), event);
	}

	@Override
	public IEnvironmentEvent unregister(IEnvironmentEvent event) {
		return registeredEvents.remove(event.getEventName());
	}

	@Override
	public Map<ResourceLocation, IEnvironmentEvent> getEvents() {
		return Collections.unmodifiableMap(registeredEvents);
	}

	public IEnvironmentEvent forName(ResourceLocation eventName) {
		return registeredEvents.get(eventName);
	}

	public List<IEnvironmentEvent> getActiveEvents() {
		return getEventsOfState(true);
	}

	@Override
	public List<IEnvironmentEvent> getEventsOfState(boolean isActive) {
		List<IEnvironmentEvent> list = new ArrayList<IEnvironmentEvent>();
		for (IEnvironmentEvent event : registeredEvents.values()) {
			if (event.isActive() == isActive) {
				list.add(event);
			}
		}
		return list;
	}

	@Override
	public List<IEnvironmentEvent> getEventsOfStateAt(double x, double y, double z, boolean active) {
		List<IEnvironmentEvent> list = new ArrayList<IEnvironmentEvent>();
		for (IEnvironmentEvent event : this.getEvents().values()) {
			if (event.isActiveAt(x, y, z) == active) {
				list.add(event);
			}
		}
		return list;
	}

	public List<ResourceLocation> getEventNames() {
		List<ResourceLocation> eventNames = new ArrayList<>();
		for (IEnvironmentEvent event : registeredEvents.values()) {
			eventNames.add(event.getEventName());
		}
		return eventNames;
	}

	public List<ResourceLocation> getEventNamesOfState(boolean isActive) {
		List<ResourceLocation> eventNames = new ArrayList<>();
		for (IEnvironmentEvent event : registeredEvents.values()) {
			if (event.isActive() == isActive) {
				eventNames.add(event.getEventName());
			}
		}
		return eventNames;
	}

	public String getGrammaticalActiveEventNameList() {
		StringBuilder list = new StringBuilder();
		IEnvironmentEvent[] events = registeredEvents.values().toArray(new IEnvironmentEvent[0]);
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

	@Override
	public boolean isEnabled() {
		return !disabled;
	}

	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public boolean setEnabled(boolean enabled) {
		return this.disabled = !enabled;
	}

	@Override
	public IEnvironmentEvent getEvent(ResourceLocation eventId) {
		return this.registeredEvents.get(eventId);
	}

	@Override
	public boolean isEventActive(ResourceLocation eventId) {
		IEnvironmentEvent event = this.getEvent(eventId);
		return event != null && event.isActive();
	}

	@Override
	public boolean isEventActiveAt(double x, double y, double z, ResourceLocation eventId) {
		IEnvironmentEvent event = this.getEvent(eventId);
		return event != null && event.isActiveAt(x, y, z);
	}
}
