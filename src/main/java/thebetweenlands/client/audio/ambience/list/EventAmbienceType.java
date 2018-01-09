package thebetweenlands.client.audio.ambience.list;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

public class EventAmbienceType extends AmbienceType {
	private final Class<? extends IEnvironmentEvent> event;
	private final SoundEvent sound;
	private int priority = 0;
	private int delay = 0;

	public EventAmbienceType(Class<? extends IEnvironmentEvent> event, SoundEvent sound, int priority) {
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
		BetweenlandsWorldStorage worldData = BetweenlandsWorldStorage.forWorld(this.getPlayer().world);
		if(worldData != null) {
			BLEnvironmentEventRegistry eventRegistry = worldData.getEnvironmentEventRegistry();
			for(IEnvironmentEvent event : eventRegistry.getActiveEvents())
				if(this.event.isAssignableFrom(event.getClass()))
					return true;
		}
		return false;
	}

	@Override
	public AmbienceLayer getAmbienceLayer() {
		return AmbienceRegistry.BASE_LAYER;
	}

	@Override
	public int getPriority() {
		return 50 + this.priority;
	}

	@Override
	public boolean stopsMusic() {
		return true;
	}

	@Override
	public int getDelay() {
		return this.delay;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return this.sound;
	}
}
