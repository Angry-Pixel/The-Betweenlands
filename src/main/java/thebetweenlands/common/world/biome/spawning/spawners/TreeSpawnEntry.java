package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;

/**
 * Spawns entities above the surface, usually on trees.
 * Used for sporeling.
 */
public class TreeSpawnEntry extends BLSpawnEntry {
	public TreeSpawnEntry(Class<? extends EntityLiving> entityType) {
		super(entityType);
	}

	public TreeSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
		super(entityType, weight);
	}

	@Override
	public void update(World world, BlockPos pos) {
		int treeHeight = WorldProviderBetweenlands.LAYER_HEIGHT + 8;
		short spawnWeight = this.getBaseWeight();
		if(pos.getY() < treeHeight) {
			spawnWeight = 0;
		} else {
			this.setWeight(spawnWeight);
		}
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState blockState, IBlockState surfaceBlockState) {
		return /*surfaceBlockState.getBlock() == BlockRegistry.TREE_FUNGUS*/false;//TODO: Requires tree fungus
	}
}
