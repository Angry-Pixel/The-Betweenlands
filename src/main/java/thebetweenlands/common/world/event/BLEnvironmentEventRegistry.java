package thebetweenlands.common.world.event;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.environment.EnvironmentEvent;

import javax.annotation.Nullable;
import java.util.*;

public class BLEnvironmentEventRegistry {

	private final Map<ResourceLocation, EnvironmentEvent> registeredEvents = new HashMap<>();
	private boolean disabled = false;

	public BLEnvironmentEventRegistry() {
		for (EnvironmentEvent event : BLRegistries.ENVIRONMENT_EVENTS) {
			this.registeredEvents.put(BLRegistries.ENVIRONMENT_EVENTS.getKey(event), event);
		}
	}

	public Map<ResourceLocation, EnvironmentEvent> getEvents() {
		return Collections.unmodifiableMap(this.registeredEvents);
	}

	public EnvironmentEvent forName(ResourceLocation eventName) {
		return this.registeredEvents.get(eventName);
	}

	public List<EnvironmentEvent> getActiveEvents() {
		return this.getEventsOfState(true);
	}

	public List<EnvironmentEvent> getEventsOfState(boolean isActive) {
		List<EnvironmentEvent> list = new ArrayList<>();
		for (EnvironmentEvent event : this.registeredEvents.values()) {
			if (event.isActive() == isActive) {
				list.add(event);
			}
		}
		return list;
	}

	public List<EnvironmentEvent> getEventsOfStateAt(double x, double y, double z, boolean active) {
		List<EnvironmentEvent> list = new ArrayList<>();
		for (EnvironmentEvent event : this.getEvents().values()) {
			if (event.isActiveAt(x, y, z) == active) {
				list.add(event);
			}
		}
		return list;
	}

	public List<ResourceLocation> getEventNames() {
		List<ResourceLocation> eventNames = new ArrayList<>();
		for (Map.Entry<ResourceLocation, EnvironmentEvent> event : this.registeredEvents.entrySet()) {
			eventNames.add(event.getKey());
		}
		return eventNames;
	}

	public List<ResourceLocation> getEventNamesOfState(boolean isActive) {
		List<ResourceLocation> eventNames = new ArrayList<>();
		for (Map.Entry<ResourceLocation, EnvironmentEvent> event : this.registeredEvents.entrySet()) {
			if (event.getValue().isActive() == isActive) {
				eventNames.add(event.getKey());
			}
		}
		return eventNames;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public String getGrammaticalActiveEventNameList() {
		StringBuilder list = new StringBuilder();
		Map.Entry<ResourceLocation, EnvironmentEvent>[] events = this.registeredEvents.entrySet().toArray(new Map.Entry[0]);
		for (int i = 0; i < events.length; i++) {
			ResourceLocation eventName = events[i].getKey();
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
		this.disabled = false;
	}

	public void disable() {
		this.disabled = true;
	}

	public boolean isEnabled() {
		return !this.disabled;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public boolean setEnabled(boolean enabled) {
		return this.disabled = !enabled;
	}

	@Nullable
	public EnvironmentEvent getEvent(ResourceLocation eventId) {
		return this.registeredEvents.get(eventId);
	}

	public boolean isEventActive(ResourceLocation eventId) {
		EnvironmentEvent event = this.getEvent(eventId);
		return event != null && event.isActive();
	}

	public boolean isEventActiveAt(double x, double y, double z, ResourceLocation eventId) {
		EnvironmentEvent event = this.getEvent(eventId);
		return event != null && event.isActiveAt(x, y, z);
	}
}
