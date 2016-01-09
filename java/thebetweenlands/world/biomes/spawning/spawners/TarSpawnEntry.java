package thebetweenlands.world.biomes.spawning.spawners;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.blocks.BLBlockRegistry;

/**
 * For entities that spawn in/on tar
 *
 */
public class TarSpawnEntry extends SurfaceSpawnEntry {
	public TarSpawnEntry(Class<? extends EntityLiving> entityType) {
		super(entityType);
	}

	public TarSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
		super(entityType, weight);
	}

	@Override
	protected boolean canSpawn(World world, Chunk chunk, int x, int y, int z, Block block, Block surfaceBlock) {
		return block == BLBlockRegistry.tarFluid;
	}
}
