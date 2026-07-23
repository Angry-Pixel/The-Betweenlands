package thebetweenlands.common.world.spawning;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.world.gen.SurfaceType;

public class CustomWorldSpawner {

	public static void spawnNearPlayer(ServerLevel level, BlockPos playerPos) {
	    Holder<Biome> playerBiomeHolder = level.getBiome(playerPos);
	    var optionalKey = playerBiomeHolder.unwrapKey();
	    if (optionalKey.isEmpty())
	        return;

	    var biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
	    Holder<Biome> holder = biomeRegistry.getHolderOrThrow(optionalKey.get());
	    BiomeSpawnerList spawnerList = holder.getData(DataMapRegistry.BIOME_SPAWNS);

	    if (spawnerList == null || spawnerList.zones().isEmpty())
	        return;

	    for (BiomeSpawnerZone zone : spawnerList.zones()) {
	        List<WeightedEntry.Wrapper<BaseSpawnProperties>> pool = new ArrayList<>();
	        for (BaseSpawnProperties mobSpec : zone.weightedMobPool()) {
	            if (playerPos.getY() < mobSpec.minHeight() - mobSpec.spawnCheckRangeY() || playerPos.getY() > mobSpec.maxHeight() + mobSpec.spawnCheckRangeY())
	                continue;

	            if (level.getRandom().nextInt(Math.max(1, mobSpec.spawningInterval())) != 0)
	                continue;

	            pool.add(WeightedEntry.wrap(mobSpec, mobSpec.weight()));
	        }

	        if (pool.isEmpty())
	            continue;

	        Optional<WeightedEntry.Wrapper<BaseSpawnProperties>> selectedSpec = WeightedRandom.getRandomItem(level.getRandom(), pool);

	        if (selectedSpec.isPresent()) {
	            BaseSpawnProperties mobSpanwPops = selectedSpec.get().data();

	            double minRadius = 24.0; 
	            double maxRadius = Math.max(minRadius + 16.0, mobSpanwPops.spawnCheckRadius());
	            double angle = level.getRandom().nextDouble() * 2.0 * Math.PI;
	            double distance = minRadius + (level.getRandom().nextDouble() * (maxRadius - minRadius));
	            int targetX = playerPos.getX() + (int) (Math.cos(angle) * distance);
	            int targetZ = playerPos.getZ() + (int) (Math.sin(angle) * distance);
	            int minAllowed = mobSpanwPops.minHeight();
	            int maxAllowed = mobSpanwPops.maxHeight();

	            if (minAllowed > maxAllowed) {
	                int temp = minAllowed;
	                minAllowed = maxAllowed;
	                maxAllowed = temp;
	            }

	            int targetY = level.getRandom().nextInt((maxAllowed - minAllowed) + 1) + minAllowed;
	            BlockPos potentialSpawnPos = new BlockPos(targetX, targetY, targetZ);
	            BlockPos finalSpawnPos = isValidLocationForZone(level, potentialSpawnPos, zone.locationType());

	            if (finalSpawnPos == null)
	                continue;

	            if (finalSpawnPos.getY() < mobSpanwPops.minHeight() || finalSpawnPos.getY() > mobSpanwPops.maxHeight())
	                continue;

	            boolean isWater = level.getBlockState(finalSpawnPos).getFluidState().isSource();

	            if (isWater && !mobSpanwPops.canSpawnInWater())
	                continue;

	            if (!isWater && !mobSpanwPops.canSpawnOnWater() && level.getBlockState(finalSpawnPos.below()).isAir())
	                continue;

	            if (isDensityCapReached(level, finalSpawnPos, mobSpanwPops))
	                continue;

	            executeGroupSpawn(level, finalSpawnPos, mobSpanwPops, zone.locationType());
	            return;
	        }
	    }
	}

	private static BlockPos isValidLocationForZone(ServerLevel level, BlockPos pos, ICustomSpawnEntry spawnEntry) {
	    BlockPos activePos = pos;
	    if (!level.getBlockState(activePos).isAir()) {
	        for (int i = 0; i < 16; i++) {
	            activePos = activePos.above();
	            if (level.getBlockState(activePos).isAir())
	                break;
	        }
	    }

	    BlockPos groundPos = activePos;

		while ((level.getBlockState(groundPos).isAir() || !level.getFluidState(groundPos).isEmpty()) && groundPos.getY() > level.getMinBuildHeight())
			groundPos = groundPos.below();

	    BlockState blockBelow = level.getBlockState(groundPos);
	    
	    if (groundPos.getY() <= level.getMinBuildHeight())
	        return null;
	    
	    BlockPos lightCheckPos = groundPos.above();
	    boolean exposedToSky = level.getBrightness(LightLayer.SKY, lightCheckPos) > 4;

	    if (spawnEntry instanceof SurfaceSpawnEntry) {
	        boolean surfaceMatches = SurfaceType.MIXED_GROUND.matches(blockBelow); //TODO use tags for this
	        return (exposedToSky && surfaceMatches) ? activePos : null;
	    }

	    else if (spawnEntry instanceof CaveSpawnEntry cave) {
	        boolean surfaceMatches = SurfaceType.UNDERGROUND.matches(blockBelow);
	        
	        return (!exposedToSky && surfaceMatches) ? activePos : null;
	    }

	    else if (spawnEntry instanceof PitstoneCaveSpawnEntry pitstone) {
	        boolean isPitstone = SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(blockBelow);
	        return (isPitstone && !exposedToSky) ? activePos : null;
	    }

	    else if (spawnEntry instanceof TreeSpawnEntry) {
	        //boolean isLeavesOrLogs = blockBelow.is(BlockTags.LEAVES) || blockBelow.is(BlockTags.LOGS);
	    	boolean isShelfFungus = blockBelow.is(BlockRegistry.SHELF_FUNGUS.get());
	        return /*isLeavesOrLogs*/ isShelfFungus ? activePos : null;
	        
	    } 

	    else if (spawnEntry instanceof SkySpawnEntry) {
	        boolean isAboveLayer = pos.getY() > TheBetweenlands.LAYER_HEIGHT + 30;
	        return (level.canSeeSky(pos) && isAboveLayer) ? pos : null;
	    }

	    return null;
	}

	private static boolean isDensityCapReached(ServerLevel level, BlockPos pos, BaseSpawnProperties spec) {
		double radius = spec.spawnCheckRadius();
		double rangeY = spec.spawnCheckRangeY();
		AABB checkArea = new AABB(pos).inflate(radius, rangeY, radius);
		List<? extends Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, checkArea, entity -> entity.getType() == spec.mobType());

		return nearbyMobs.size() >= spec.maxGroupSize();
	}

	private static void executeGroupSpawn(ServerLevel level, BlockPos originPos, BaseSpawnProperties spec, ICustomSpawnEntry zone) {
		int groupSize = Mth.nextInt(level.getRandom(), spec.minGroupSize(), spec.maxGroupSize());
		double spawnRadius = spec.groupSpawnRadius();
		int minWorldHeight = level.getMinBuildHeight();
		int spawnedCount = 0;
	    System.out.println("[SPAWNER DEBUG] Preparing to spawn a group of size " + groupSize + " for: " + spec.mobType().getDescriptionId());

		for (int i = 0; i < groupSize * 2 && spawnedCount < groupSize; i++) {
			double offsetX = (level.random.nextDouble() - 0.5) * spawnRadius * 2;
			double offsetZ = (level.random.nextDouble() - 0.5) * spawnRadius * 2;
			int targetX = originPos.getX() + (int) Math.round(offsetX);
			int targetZ = originPos.getZ() + (int) Math.round(offsetZ);
			BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(targetX, originPos.getY(), targetZ);

			BlockPos finalSpawnPos;
			EntityType<?> type = spec.mobType();
			boolean isWaterMob = type.getCategory() == MobCategory.WATER_CREATURE || type.getCategory() == MobCategory.WATER_AMBIENT || type.getCategory() == MobCategory.UNDERGROUND_WATER_CREATURE;

			if (isWaterMob) {
			    // Water Mob 
			    boolean isOpenSpace = level.getBlockState(mutablePos).isAir() || level.getBlockState(mutablePos).getFluidState().isSource();
			    if (!isOpenSpace) {
			        int maxUp = originPos.getY() + 4;
			        while (mutablePos.getY() < maxUp && !(level.getBlockState(mutablePos).isAir() || level.getBlockState(mutablePos).getFluidState().isSource()))
			            mutablePos.move(Direction.UP);
			    } else {
			        while (mutablePos.getY() > minWorldHeight && (level.getBlockState(mutablePos).isAir() || level.getBlockState(mutablePos).getFluidState().isSource()))
			            mutablePos.move(Direction.DOWN);

			        mutablePos.move(Direction.UP);
			    }
			} else {
			    // Land Mob 
			    if (!level.getBlockState(mutablePos).isAir()) {
			        int maxUp = originPos.getY() + 4;
			        while (mutablePos.getY() < maxUp && !level.getBlockState(mutablePos).isAir())
			            mutablePos.move(Direction.UP);
			    } else {
			        boolean hitFluid = false;
			        while (mutablePos.getY() > minWorldHeight && level.getBlockState(mutablePos).isAir()) {
			            mutablePos.move(Direction.DOWN);
			            if (!level.getBlockState(mutablePos).getFluidState().isEmpty()) {
			                hitFluid = true;
			                break;
			            }
			        }
			        
			        if (hitFluid)
			            continue;
			        
			        mutablePos.move(Direction.UP);
			    }
			    
			    if (!level.getBlockState(mutablePos.below()).getFluidState().isEmpty())
			        continue; 
			}

			finalSpawnPos = mutablePos.immutable();

			if (!level.getWorldBorder().isWithinBounds(finalSpawnPos))
				continue;

			if (SpawnPlacements.checkSpawnRules(type, level, MobSpawnType.NATURAL, finalSpawnPos, level.getRandom())) {
				Entity entity = type.create(level);
				if (entity instanceof Mob mob) {
					mob.moveTo(finalSpawnPos.getX() + 0.5, finalSpawnPos.getY(), finalSpawnPos.getZ() + 0.5, level.random.nextFloat() * 360F, 0.0F);
					mob.finalizeSpawn(level, level.getCurrentDifficultyAt(finalSpawnPos), MobSpawnType.NATURAL, null);
					if (level.addFreshEntity(mob)) {
						System.out.println("Spawned: " + mob.getDisplayName().getString() + " at " + finalSpawnPos.toShortString() + " Zone: " + zone.getType().getSerializedName());
						spawnedCount++;
					} else {
						System.out.println("Failed to add entity to world: " + mob.getDisplayName().getString());
					}
				}

			}
			else {
				System.out.println("Spawn Placements check Failed");
			}
		}
	}
}
