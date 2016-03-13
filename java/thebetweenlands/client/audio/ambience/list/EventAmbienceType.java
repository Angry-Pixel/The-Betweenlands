package thebetweenlands.client.audio.ambience.list;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import thebetweenlands.world.storage.world.BetweenlandsWorldData;

public class EventAmbienceType extends AmbienceType {
	private final Class<? extends EnvironmentEvent> event;
	private final ResourceLocation sound;
	private int priority = 0;
	private int delay = 0;

	public EventAmbienceType(Class<? extends EnvironmentEvent> event, ResourceLocation sound, int priority) {
		this.event = event;
		this.sound = sound;
		this.priority = priority;
	}

	public EventAmbienceType setDelay(int delay) {
		this.delay = delay;
		return this;
	}

	@Override
	public boolean isActive() {
		BetweenlandsWorldData worldData = BetweenlandsWorldData.forWorld(this.getPlayer().worldObj);
		if(worldData != null) {
			EnvironmentEventRegistry eventRegistry = worldData.getEnvironmentEventRegistry();
			for(EnvironmentEvent event : eventRegistry.getActiveEvents())
				if(this.event.isAssignableFrom(event.getClass()))
					return true;
		}
		return false;
	}

	@Override
	public EnumAmbienceLayer getAmbienceLayer() {
		return EnumAmbienceLayer.LAYER1;
	}

	@Override
	public int getPriority() {
		return 50 + this.priority;
	}

	@Override
	public ResourceLocation getSound() {
		return this.sound;
	}

	@Override
	public boolean stopsMusic() {
		return true;
	}

	@Override
	public int getDelay() {
		return this.delay;
	}
}
