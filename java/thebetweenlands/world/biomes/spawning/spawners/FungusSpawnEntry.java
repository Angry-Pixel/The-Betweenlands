package thebetweenlands.world.biomes.spawning.spawners;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.spawning.MobSpawnHandler.BLSpawnEntry;

/**
 * Used for sporeling.
 */
public class FungusSpawnEntry extends BLSpawnEntry {
	private final short baseWeight;

	public FungusSpawnEntry(Class<? extends EntityLiving> entityType) {
		super(entityType);
		this.baseWeight = this.getWeight();
	}

	public FungusSpawnEntry(Class<? extends EntityLiving> entityType, short weight) {
		super(entityType, weight);
		this.baseWeight = this.getWeight();
	}

	@Override
	protected void update(World world, int x, int y, int z) {
		int surfaceHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
		short spawnWeight = this.baseWeight;
		if(y < surfaceHeight) {
			spawnWeight = 0;
		} else {
			this.setWeight(spawnWeight);
		}
	}

	@Override
	protected boolean canSpawn(World world, int x, int y, int z, Block surfaceBlock) {
		return surfaceBlock == BLBlockRegistry.treeFungus;
	}
}
