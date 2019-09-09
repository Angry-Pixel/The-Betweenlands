package thebetweenlands.common.world.biome.spawning.spawners;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

/**
 * Prevents entities from spawning in caves.
 */
public class SurfaceSpawnEntry extends BLSpawnEntry {
	private boolean canSpawnOnWater = false;
	private boolean canSpawnInWater = false;
	private Predicate<IBlockState> surfaceBlockPredicate = new Predicate<IBlockState>() {
		@Override
		public boolean apply(IBlockState input) {
			return SurfaceType.MIXED_GROUND.matches(input);
		}
	};

	public SurfaceSpawnEntry(int id, Class<? extends EntityLiving> entityType) {
		super(id, entityType);
	}

	public SurfaceSpawnEntry(int id, Class<? extends EntityLiving> entityType, short weight) {
		super(id, entityType, weight);
	}

	public SurfaceSpawnEntry setSurfacePredicate(Predicate<IBlockState> surfacePredicate) {
		this.surfaceBlockPredicate = surfacePredicate;
		return this;
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
		return (this.surfaceBlockPredicate.apply(surfaceBlockState) && !spawnBlockState.getMaterial().isLiquid()) ||
				(this.canSpawnInWater && spawnBlockState.getMaterial().isLiquid()) || 
				(this.canSpawnOnWater && surfaceBlockState.getMaterial().isLiquid() && !spawnBlockState.getMaterial().isLiquid());
	}
}
