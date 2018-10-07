package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class BetweenstoneCaveSpawnEntry extends CaveSpawnEntry {
	public BetweenstoneCaveSpawnEntry(int id, Class<? extends EntityLiving> entityType) {
		super(id, entityType);
	}

	public BetweenstoneCaveSpawnEntry(int id, Class<? extends EntityLiving> entityType, short baseWeight) {
		super(id, entityType, baseWeight);
	}

	@Override
	public void update(World world, BlockPos pos) {
		int surfaceHeight = WorldProviderBetweenlands.CAVE_START;
		int pitstoneHeight = WorldProviderBetweenlands.PITSTONE_HEIGHT;
		short spawnWeight;
		if(pos.getY() > pitstoneHeight && pos.getY() < surfaceHeight) {
			spawnWeight = this.getBaseWeight();
		} else {
			spawnWeight = 0;
		}
		this.setWeight(spawnWeight);
	}
}
