package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;

/**
 * Prevents entities from spawning in caves.
 */
public class SurfaceSpawnEntry extends BLSpawnEntry {
	private boolean canSpawnOnWater = false;

	public SurfaceSpawnEntry(Class<? extends EntityLiving> entityType) {
		super(entityType);
	}

	public SurfaceSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
		super(entityType, weight);
	}

	public SurfaceSpawnEntry setCanSpawnOnWater(boolean spawnOnWater) {
		this.canSpawnOnWater = spawnOnWater;
		return this;
	}

	@Override
	protected void update(World world, BlockPos pos) {
		int caveHeight = WorldProviderBetweenlands.CAVE_START;
		if(pos.getY() <= caveHeight) {
			this.setWeight((short) 0);
		} else {
			this.setWeight(this.getBaseWeight());
		}
	}

	@Override
	protected boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState spawnBlockState, IBlockState surfaceBlockState) {
		return spawnBlockState.isNormalCube() || (this.canSpawnOnWater && spawnBlockState.getMaterial().isLiquid());
	}
}
