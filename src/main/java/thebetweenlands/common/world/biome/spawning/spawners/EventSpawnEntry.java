package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.common.world.event.EnvironmentEvent;

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
	protected void update(World world, BlockPos pos) {
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
