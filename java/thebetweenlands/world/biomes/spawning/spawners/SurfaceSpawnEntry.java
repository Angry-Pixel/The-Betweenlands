package thebetweenlands.world.biomes.spawning.spawners;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.biomes.spawning.MobSpawnHandler.BLSpawnEntry;

/**
 * Prevents entities from spawning in caves.
 */
public class SurfaceSpawnEntry extends BLSpawnEntry {
	public SurfaceSpawnEntry(Class<? extends EntityLiving> entityType) {
		super(entityType);
	}

	public SurfaceSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
		super(entityType, weight);
	}

	@Override
	protected void update(World world, int x, int y, int z) {
		int caveHeight = WorldProviderBetweenlands.CAVE_START;
		if(y <= caveHeight) {
			this.setWeight((short) 0);
		} else {
			this.setWeight(this.getBaseWeight());
		}
	}

	@Override
	protected boolean canSpawn(World world, Chunk chunk, int x, int y, int z, Block spawnBlock, Block surfaceBlock) {
		return surfaceBlock.isNormalCube() || SurfaceType.MIXED.matchBlock(surfaceBlock);
	}
}
