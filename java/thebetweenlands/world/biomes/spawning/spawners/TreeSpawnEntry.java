package thebetweenlands.world.biomes.spawning.spawners;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.spawning.MobSpawnHandler.BLSpawnEntry;

/**
 * Spawns entities above the surface, usually on trees.
 * Used for sporeling.
 */
public class TreeSpawnEntry extends BLSpawnEntry {
	private final short baseWeight;

	public TreeSpawnEntry(Class<? extends EntityLiving> entityType) {
		super(entityType);
		this.baseWeight = this.getWeight();
	}

	public TreeSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
		super(entityType, weight);
		this.baseWeight = this.getWeight();
	}

	@Override
	protected void update(World world, int x, int y, int z) {
		int treeHeight = WorldProviderBetweenlands.LAYER_HEIGHT + 10;
		short spawnWeight = this.baseWeight;
		if(y < treeHeight) {
			spawnWeight = 0;
		} else {
			this.setWeight(spawnWeight);
		}
	}

	@Override
	protected boolean canSpawn(World world, Chunk chunk, int x, int y, int z, Block block, Block surfaceBlock) {
		return surfaceBlock == BLBlockRegistry.treeFungus;
	}
}
