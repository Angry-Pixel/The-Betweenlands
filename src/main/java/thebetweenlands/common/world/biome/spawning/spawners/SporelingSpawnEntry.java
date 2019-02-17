package thebetweenlands.common.world.biome.spawning.spawners;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.entity.mobs.EntityGecko;

public class SporelingSpawnEntry extends TreeSpawnEntry {
	public SporelingSpawnEntry(int id, Class<? extends EntityLiving> entityType, short weight) {
		super(id, entityType, weight);
	}

	@Override
	public void onSpawned(EntityLivingBase entity) {
		if(entity.isEntityAlive() && entity.world.rand.nextInt(10) == 0) {
			EntityGecko gecko = new EntityGecko(entity.world);
			gecko.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.world.rand.nextFloat() * 360, 0);
			if(!entity.world.containsAnyLiquid(gecko.getBoundingBox()) && entity.world.getCollisionBoxes(gecko, gecko.getBoundingBox()).isEmpty()) {
				gecko.onInitialSpawn(entity.world.getDifficultyForLocation(new BlockPos(entity.posX, entity.posY, entity.posZ)), null);
				entity.world.spawnEntity(gecko);
				entity.startRiding(gecko);
			}
		}
	}
}
