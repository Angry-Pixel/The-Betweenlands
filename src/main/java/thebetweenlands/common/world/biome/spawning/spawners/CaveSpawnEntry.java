package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.AreaMobSpawner.BLSpawnEntry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

/**
 * Makes entities spawn only spawn below cave level. 
 * Spawning weight gradually increases as the y value decreases. 
 * Spawning weight at bedrock level is baseWeight.
 * Mostly used for hostile entities.
 */
public class CaveSpawnEntry extends BLSpawnEntry {
	protected boolean canSpawnOnWater = false;
	protected boolean canSpawnInWater = false;

	public CaveSpawnEntry(int id, Class<? extends EntityLiving> entityType) {
		super(id, entityType);
	}

	public CaveSpawnEntry(int id, Class<? extends EntityLiving> entityType, short baseWeight) {
		super(id, entityType, baseWeight);
	}

	public CaveSpawnEntry setCanSpawnOnWater(boolean spawnOnWater) {
		this.canSpawnOnWater = spawnOnWater;
		return this;
	}

	public CaveSpawnEntry setCanSpawnInWater(boolean spawnInWater) {
		this.canSpawnInWater = spawnInWater;
		return this;
	}

	@Override
	public void update(World world, BlockPos pos) {
		int surfaceHeight = WorldProviderBetweenlands.CAVE_START;
		short spawnWeight = (short) (this.getBaseWeight() / 3);
		if(pos.getY() < surfaceHeight) {
			double percentage = 1.0D - ((double)(surfaceHeight - pos.getY()) / (double)surfaceHeight);
			spawnWeight = (short) MathHelper.ceil(this.getBaseWeight() / (2.0D * percentage + 1.0D));
		} else {
			spawnWeight = 0;
		}
		this.setWeight(spawnWeight);
	}

	@Override
	public boolean canSpawn(World world, Chunk chunk, BlockPos pos, IBlockState spawnBlockState, IBlockState surfaceBlockState) {
		return (SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(surfaceBlockState) && !spawnBlockState.getMaterial().isLiquid()) ||
				(this.canSpawnInWater && spawnBlockState.getMaterial().isLiquid()) || 
				(this.canSpawnOnWater && surfaceBlockState.getMaterial().isLiquid() && !spawnBlockState.getMaterial().isLiquid());
	}
}
