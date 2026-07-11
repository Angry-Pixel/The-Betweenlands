package thebetweenlands.common.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.common.entity.BLEntityWithSpawnRules;
import thebetweenlands.common.registries.EntityRegistry;

public class EntitySpawnPlacementsHandler {
	public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> holder : EntityRegistry.ENTITY_TYPES.getEntries()) {
			EntityType<?> type = holder.get();
			// general catch all, I know it is a load of dupes - but until we refine for our mobs... (will sort out later I promise)
			switch (type.getCategory()) {
			case AXOLOTLS:
			case MISC:
				break;
			case AMBIENT:
			case CREATURE:
				registerSpecialCases(event, castType(type), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySpawnPlacementsHandler::passiveFallback);
				break;
			case MONSTER:
				registerSpecialCases(event, castType(type), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySpawnPlacementsHandler::hostileFallback);
				break;
			case UNDERGROUND_WATER_CREATURE:
				registerSpecialCases(event, castType(type), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySpawnPlacementsHandler::undergroundWaterFallback);
				break;
			case WATER_AMBIENT:
			case WATER_CREATURE:
				registerSpecialCases(event, castType(type), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntitySpawnPlacementsHandler::waterFallback);
				break;
			default:
				break;
			}
		}
	}

	private static <T extends Mob> void registerSpecialCases(RegisterSpawnPlacementsEvent event, EntityType<T> type, SpawnPlacementType placementType, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> fallbackPredicate) {
		event.register(type, placementType, heightmap, (entityType, level, spawnType, pos, random) -> {
			
			if (entityType.create(level.getLevel()) instanceof BLEntityWithSpawnRules<?> mob) {
				@SuppressWarnings("unchecked")
				BLEntityWithSpawnRules<T> mobToCheck = (BLEntityWithSpawnRules<T>) mob;
				return mobToCheck.canSpawnHere(entityType, level, spawnType, pos, random);
			}

			return fallbackPredicate.test(entityType, level, spawnType, pos, random);

		}, RegisterSpawnPlacementsEvent.Operation.OR);
	}

    @SuppressWarnings("unchecked")
    private static boolean hostileFallback(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource rand) {
        return Monster.checkMonsterSpawnRules((EntityType<? extends Monster>) type, (ServerLevelAccessor) level, spawnType, pos, rand);
    }

    @SuppressWarnings("unchecked")
    private static boolean passiveFallback(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource rand) {
        return Animal.checkAnimalSpawnRules((EntityType<? extends Animal>) type, level, spawnType, pos, rand);
    }

    @SuppressWarnings({ "deprecation" }) //Just what vanilla does atm
    private static boolean undergroundWaterFallback(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource rand) {
        return pos.getY() <= level.getSeaLevel() - 33  && level.getRawBrightness(pos, 0) == 0  && level.getBlockState(pos).is(Blocks.WATER);
    }

    @SuppressWarnings("unchecked")
    private static boolean waterFallback(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource rand) {
        return WaterAnimal.checkSurfaceWaterAnimalSpawnRules((EntityType<? extends WaterAnimal>) type, level, spawnType, pos, rand);
    }

    @SuppressWarnings("unchecked")
	private static <T extends Mob> EntityType<T> castType(EntityType<?> type) {
        return (EntityType<T>) type;
    }

}
