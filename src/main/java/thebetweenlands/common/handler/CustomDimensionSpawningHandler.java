package thebetweenlands.common.handler;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.world.gen.SurfaceType;
import thebetweenlands.common.world.spawning.CaveSpawnEntry;
import thebetweenlands.common.world.spawning.ICustomSpawnEntry;
import thebetweenlands.common.world.spawning.PitstoneCaveSpawnEntry;
import thebetweenlands.common.world.spawning.SkySpawnEntry;
import thebetweenlands.common.world.spawning.SurfaceSpawnEntry;
import thebetweenlands.common.world.spawning.TreeSpawnEntry;

public class CustomDimensionSpawningHandler {

	@SubscribeEvent
	public static void onLevelTick(LevelTickEvent.Post event) {
		if (!(event.getLevel() instanceof ServerLevel level))
			return;

		if (!level.dimension().equals(DimensionRegistries.DIMENSION_KEY))
			return;

		if (level.players().isEmpty())
			return;

		for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
			@SuppressWarnings("deprecation")
			var dataMapHolder = entityType.builtInRegistryHolder().getData(DataMapRegistry.CUSTOM_SPAWNS);

			if (dataMapHolder == null || dataMapHolder.isEmpty())
				continue;

			List<ICustomSpawnEntry> spawnEntryList = dataMapHolder;

			for (ICustomSpawnEntry spawnEntry : spawnEntryList) {
				if (level.getGameTime() % spawnEntry.base().spawningInterval() != 0)
					continue;

				if (level.random.nextInt(100) > spawnEntry.base().weight())
					continue;

				ServerPlayer player = level.players().get(level.random.nextInt(level.players().size()));
				BlockPos playerPos = player.blockPosition();

				int offsetX = level.random.nextInt(48) - 24;
				int offsetZ = level.random.nextInt(48) - 24;
				int targetY = level.random.nextInt(spawnEntry.base().maxHeight() - spawnEntry.base().minHeight() + 1) + spawnEntry.base().minHeight();

				BlockPos targetPos = new BlockPos(playerPos.getX() + offsetX, targetY, playerPos.getZ() + offsetZ);
				BlockPos.MutableBlockPos mutablePos = targetPos.mutable();
				int minWorldHeight = level.getMinBuildHeight();
				int maxWorldHeight = level.getMaxBuildHeight();

				if (level.getBlockState(mutablePos).isAir()) {
				    while (mutablePos.getY() > minWorldHeight && level.getBlockState(mutablePos).isAir()) {
				        mutablePos.move(Direction.DOWN);
				    }
				    mutablePos.move(Direction.UP);
				} else {
				    int maxScanUp = Math.min(mutablePos.getY() + 32, maxWorldHeight);
				    while (mutablePos.getY() < maxScanUp && !level.getBlockState(mutablePos).isAir()) {
				        mutablePos.move(Direction.UP);
				    }
				}

				if (!level.getBlockState(mutablePos).isAir()) 
				    continue;

				BlockPos finalSpawnPos = mutablePos.immutable(); 

				if (!isValidGlobalZone(level, finalSpawnPos, spawnEntry, entityType))
				    continue;

				if (!isValidEnvironment(level, finalSpawnPos, spawnEntry))
				    continue;

				executeGroupSpawn(level, finalSpawnPos, spawnEntry, entityType);
			}
		}
	}

	private static boolean isValidGlobalZone(ServerLevel level, BlockPos pos, ICustomSpawnEntry spawnEntry, EntityType<?> type) {
	    Holder<Biome> currentBiomeHolder = level.getBiome(pos);

	    if (!spawnEntry.base().allowedBiomes().contains(currentBiomeHolder))
	        return false;

	    @SuppressWarnings("unchecked")
		EntityType<? extends Mob> mobType = (EntityType<? extends Mob>) type;
	    BlockPos floorPos = pos.below();

	    if (!SpawnPlacements.checkSpawnRules(mobType, level, MobSpawnType.SPAWNER, floorPos, level.getRandom()))
	        return false;

	    double radius = spawnEntry.base().spawnCheckRadius();
	    double rangeY = spawnEntry.base().spawnCheckRangeY();
	    AABB capBox = new AABB(pos).inflate(radius, rangeY, radius);
	    List<? extends Mob> activeMobsNearby = level.getEntitiesOfClass(Mob.class, capBox, e -> e.getType() == type);

	    return activeMobsNearby.size() < spawnEntry.base().maxGroupSize();
	}


	private static boolean isValidEnvironment(ServerLevel level, BlockPos pos, ICustomSpawnEntry spawnEntry) {
	    if (!level.getBlockState(pos).isAir() || !level.getBlockState(pos.above()).isAir())
	        return false;

	    BlockState blockBelow = level.getBlockState(pos.below());
	    //TODO these are just basic checks for testing - need to add proper water checking and surface types
	    if (spawnEntry instanceof CaveSpawnEntry cave) {
	        if (blockBelow.is(Blocks.WATER) && !cave.canSpawnOnWater())
	        	return false;

	        boolean canSeeSky = level.canSeeSky(pos);
	        boolean surfaceMatches = SurfaceType.UNDERGROUND.matches(blockBelow);

	        return !canSeeSky && surfaceMatches;
	        
	    } else if (spawnEntry instanceof PitstoneCaveSpawnEntry pitstone) {
	        if (blockBelow.is(Blocks.WATER) && !pitstone.canSpawnOnWater())
	        	return false;

	        boolean isPitstone = blockBelow.is(BlockRegistry.PITSTONE);
	        boolean canSeeSky = level.canSeeSky(pos);

	        return isPitstone && !canSeeSky;
	        
	    } else if (spawnEntry instanceof SurfaceSpawnEntry surface) {
	        if (blockBelow.is(Blocks.WATER) && !surface.canSpawnOnWater())
	        	return false;

	        boolean surfaceMatches = SurfaceType.MIXED_GROUND.matches(blockBelow);

	        return surfaceMatches;

	    } else if (spawnEntry instanceof TreeSpawnEntry) {

	        boolean isLeavesOrLogs = blockBelow.is(BlockTags.LEAVES) || blockBelow.is(BlockTags.LOGS);

	        return isLeavesOrLogs;
	        
	    } else if (spawnEntry instanceof SkySpawnEntry) {

	        boolean isAboveLayer = pos.getY() > TheBetweenlands.LAYER_HEIGHT;

	        return isAboveLayer;
	    }

	    return false;
	}

	@SuppressWarnings("deprecation")
	private static void executeGroupSpawn(ServerLevel level, BlockPos centerPos, ICustomSpawnEntry spawnEntry, EntityType<?> type) {
	    int packSize = level.random.nextInt((spawnEntry.base().maxGroupSize() - spawnEntry.base().minGroupSize()) + 1) + spawnEntry.base().minGroupSize();
	    double spawnRadius = spawnEntry.base().groupSpawnRadius();
	    int minWorldHeight = level.getMinBuildHeight();

	    for (int i = 0; i < packSize; i++) {
	        double offsetX = (level.random.nextDouble() - 0.5) * spawnRadius * 2;
	        double offsetZ = (level.random.nextDouble() - 0.5) * spawnRadius * 2;
	        int targetX = centerPos.getX() + (int) Math.round(offsetX);
	        int targetZ = centerPos.getZ() + (int) Math.round(offsetZ);
	        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(targetX, centerPos.getY(), targetZ);

	        if (!level.getBlockState(mutablePos).isAir()) {
	            int maxUp = centerPos.getY() + 4;
	            while (mutablePos.getY() < maxUp && !level.getBlockState(mutablePos).isAir()) {
	                mutablePos.move(Direction.UP);
	            }
	        } else {
	            while (mutablePos.getY() > minWorldHeight && level.getBlockState(mutablePos).isAir()) {
	                mutablePos.move(Direction.DOWN);
	            }
	            mutablePos.move(Direction.UP);
	        }

	        BlockPos finalSpawnPos = mutablePos.immutable();

	        if (level.getBlockState(finalSpawnPos).isAir() && level.getBlockState(finalSpawnPos.above()).isAir()) {
	            Entity entity = type.create(level);
	            if (entity instanceof Mob mob) {
	                mob.moveTo(finalSpawnPos.getX() + 0.5, finalSpawnPos.getY(), finalSpawnPos.getZ() + 0.5, level.random.nextFloat() * 360F, 0.0F);
	                mob.finalizeSpawn(level, level.getCurrentDifficultyAt(finalSpawnPos), MobSpawnType.NATURAL, null);
	                if (level.addFreshEntity(mob)) {
	                    System.out.println("Spawned: " + mob.getDisplayName().getString() + " at " + finalSpawnPos.toShortString());
	                } else {
	                    System.out.println("Failed to add entity to world: " + mob.getDisplayName().getString());
	                }
	            }
	        }
	    }
	}

}
