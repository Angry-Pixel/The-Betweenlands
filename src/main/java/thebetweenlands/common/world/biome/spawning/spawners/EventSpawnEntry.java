package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;

public class EventSpawnEntry extends BLSpawnEntry {
	private final BLSpawnEntry parent;
	private final ResourceLocation eventName;

	/**
	 * The parent spawn entry defines the spawning conditions and base weight
	 * @param parent
	 * @param eventName
	 */
	public EventSpawnEntry(int id, BLSpawnEntry parent, ResourceLocation eventName) {
		super(id, parent.getEntityType(), parent.getBaseWeight());
		this.parent = parent;
		this.eventName = eventName;
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState blockState, IBlockState surfaceBlockState) {
		return this.parent.canSpawn(world, chunk, pos, blockState, surfaceBlockState);
	}

	@Override
	public void update(World world, BlockPos pos) {
		this.setWeight((short) 0);
		if(world.provider instanceof WorldProviderBetweenlands) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
			IEnvironmentEvent event = provider.getEnvironmentEventRegistry().forName(this.eventName);
			if(event != null && event.isActive()) {
				this.setWeight(this.getBaseWeight());
			}
		}
	}
}
