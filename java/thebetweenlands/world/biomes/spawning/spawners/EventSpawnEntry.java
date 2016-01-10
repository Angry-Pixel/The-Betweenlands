package thebetweenlands.world.biomes.spawning.spawners;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.world.events.EnvironmentEvent;

public class EventSpawnEntry extends BLSpawnEntry {
	private final String eventName;

	public EventSpawnEntry(Class<? extends EntityLiving> entityType, String eventName) {
		super(entityType);
		this.eventName = eventName;
	}

	public EventSpawnEntry(Class<? extends EntityLiving> entityType, short weight, String eventName) {
		super(entityType, weight);
		this.eventName = eventName;
	}

	@Override
	protected void update(World world, int x, int y, int z) {
		this.setWeight((short) 0);
		if(world.provider instanceof WorldProviderBetweenlands) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
			EnvironmentEvent event = provider.getEnvironmentEventRegistry().forName(this.eventName);
			if(event != null && event.isActive()) {
				this.setWeight(this.getBaseWeight());
			}
		}
	}
}
