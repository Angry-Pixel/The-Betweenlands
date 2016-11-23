package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

/**
 * Prevents entities from spawning in caves.
 */
public class SurfaceSpawnEntry extends BLSpawnEntry {
	private boolean canSpawnOnWater = false;
	private boolean canSpawnInWater = false;

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

	public SurfaceSpawnEntry setCanSpawnInWater(boolean spawnInWater) {
		this.canSpawnInWater = spawnInWater;
		return this;
	}

	@Override
	public void update(World world, BlockPos pos) {
		int caveHeight = WorldProviderBetweenlands.CAVE_START;
		if(pos.getY() <= caveHeight) {
			this.setWeight((short) 0);
		} else {
			this.setWeight(this.getBaseWeight());
		}
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState spawnBlockState, IBlockState surfaceBlockState) {
		return SurfaceType.MIXED.matches(spawnBlockState) || 
				(this.canSpawnInWater && surfaceBlockState.getMaterial().isLiquid()) || 
				(this.canSpawnOnWater && surfaceBlockState.getMaterial().isLiquid() && chunk.getBlockState(pos.up()).getBlock() == Blocks.AIR);
	}
}
