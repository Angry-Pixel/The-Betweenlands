package thebetweenlands.world.biomes.spawning.spawners;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.data.SurfaceType;
import thebetweenlands.world.biomes.spawning.MobSpawnHandler.BLSpawnEntry;

/**
 * Prevents entities from spawning in caves.
 */
public class SurfaceSpawnEntry extends BLSpawnEntry {
	private final short baseWeight;

	public SurfaceSpawnEntry(Class<? extends EntityLiving> entityType) {
		super(entityType);
		this.baseWeight = this.getWeight();
	}

	public SurfaceSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
		super(entityType, weight);
		this.baseWeight = this.getWeight();
	}

	@Override
	protected void update(World world, int x, int y, int z) {
		int caveHeight = WorldProviderBetweenlands.CAVE_START;
		if(y <= caveHeight) {
			this.setWeight((short) 0);
		} else {
			this.setWeight(this.baseWeight);
		}
	}

	@Override
	protected boolean canSpawn(World world, int x, int y, int z, Block surfaceBlock) {
		return surfaceBlock.isNormalCube() && SurfaceType.MIXED.matchBlock(surfaceBlock);
	}
}
