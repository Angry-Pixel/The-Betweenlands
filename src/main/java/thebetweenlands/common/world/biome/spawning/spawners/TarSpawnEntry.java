package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.registries.BlockRegistry;

/**
 * For entities that spawn in/on tar
 *
 */
public class TarSpawnEntry extends SurfaceSpawnEntry {
	public TarSpawnEntry(int id, Class<? extends EntityLiving> entityType) {
		super(id, entityType);
	}

	public TarSpawnEntry(int id, Class<? extends EntityLiving> entityType, short weight) {
		super(id, entityType, weight);
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState blockState, IBlockState surfaceBlockState) {
		return blockState.getBlock() == BlockRegistry.TAR;
	}
}
