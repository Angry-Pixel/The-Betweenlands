package thebetweenlands.common.world.biome.spawning.spawners;

import java.util.function.Function;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.entity.mobs.EntityGecko;

public class SporelingSpawnEntry extends TreeSpawnEntry {
	public SporelingSpawnEntry(int id, Class<? extends EntityLiving> entityType, Function<World, ? extends EntityLiving> entityCtor, short weight) {
		super(id, entityType, entityCtor, weight);
	}

	@Override
	public void onSpawned(EntityLivingBase entity) {
		if(entity.isEntityAlive() && entity.world.rand.nextInt(10) == 0) {
			EntityGecko gecko = new EntityGecko(entity.world);
			gecko.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.world.rand.nextFloat() * 360, 0);
			if(!entity.world.containsAnyLiquid(gecko.getEntityBoundingBox()) && entity.world.getCollisionBoxes(gecko, gecko.getEntityBoundingBox()).isEmpty()) {
				gecko.onInitialSpawn(entity.world.getDifficultyForLocation(new BlockPos(entity.posX, entity.posY, entity.posZ)), null);
				entity.world.spawnEntity(gecko);
				entity.startRiding(gecko);
			}
		}
	}
}
