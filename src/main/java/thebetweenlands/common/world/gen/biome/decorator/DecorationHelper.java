package thebetweenlands.common.world.gen.biome.decorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.terrain.BlockWisp;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.feature.WorldGenBarnacleCluster;
import thebetweenlands.common.world.gen.feature.WorldGenBigBulbCappedMushroom;
import thebetweenlands.common.world.gen.feature.WorldGenBladderwortCluster;
import thebetweenlands.common.world.gen.feature.WorldGenCaveGrass;
import thebetweenlands.common.world.gen.feature.WorldGenCaveHangers;
import thebetweenlands.common.world.gen.feature.WorldGenCaveMoss;
import thebetweenlands.common.world.gen.feature.WorldGenCavePots;
import thebetweenlands.common.world.gen.feature.WorldGenCaveThorns;
import thebetweenlands.common.world.gen.feature.WorldGenDeepmanSimulacrum;
import thebetweenlands.common.world.gen.feature.WorldGenFluidPool;
import thebetweenlands.common.world.gen.feature.WorldGenLakeCavernSimulacrum;
import thebetweenlands.common.world.gen.feature.WorldGenMossCluster;
import thebetweenlands.common.world.gen.feature.WorldGenPebbleCluster;
import thebetweenlands.common.world.gen.feature.WorldGenPlantCluster;
import thebetweenlands.common.world.gen.feature.WorldGenRootmanSimulacrum;
import thebetweenlands.common.world.gen.feature.WorldGenRootsCluster;
import thebetweenlands.common.world.gen.feature.WorldGenRottenLogs;
import thebetweenlands.common.world.gen.feature.WorldGenRottenWeedwoodTree;
import thebetweenlands.common.world.gen.feature.WorldGenSmallHollowLog;
import thebetweenlands.common.world.gen.feature.WorldGenSpeleothem;
import thebetweenlands.common.world.gen.feature.WorldGenSwampKelpCluster;
import thebetweenlands.common.world.gen.feature.WorldGenSwampReedCluster;
import thebetweenlands.common.world.gen.feature.WorldGenWaterRootsCluster;
import thebetweenlands.common.world.gen.feature.WorldGenWeedwoodBush;
import thebetweenlands.common.world.gen.feature.structure.WorldGenChiromawNest;
import thebetweenlands.common.world.gen.feature.structure.WorldGenCragrockTower;
import thebetweenlands.common.world.gen.feature.structure.WorldGenIdolHeads;
import thebetweenlands.common.world.gen.feature.structure.WorldGenMudStructures;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSludgeWormDungeon;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSmallRuins;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSpawner;
import thebetweenlands.common.world.gen.feature.structure.WorldGenSpawnerStructure;
import thebetweenlands.common.world.gen.feature.structure.WorldGenTarPoolDungeon;
import thebetweenlands.common.world.gen.feature.structure.WorldGenUndergroundRuins;
import thebetweenlands.common.world.gen.feature.structure.WorldGenWightFortress;
import thebetweenlands.common.world.gen.feature.tree.WorldGenGiantTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenGiantTreeDead;
import thebetweenlands.common.world.gen.feature.tree.WorldGenHearthgroveTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenNibbletwigTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenRubberTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSapTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSpiritTreeStructure;
import thebetweenlands.common.world.gen.feature.tree.WorldGenWeedwoodTree;
import thebetweenlands.util.CubicBezier;

public class DecorationHelper {
	public static final WorldGenerator GEN_SPELEOTHEM = new WorldGenSpeleothem();
	public static final WorldGenerator GEN_CAVE_POTS = new WorldGenCavePots();
	public static final WorldGenerator GEN_CAVE_THORNS = new WorldGenCaveThorns();
	public static final WorldGenerator GEN_CAVE_MOSS = new WorldGenCaveMoss();
	public static final WorldGenerator GEN_CAVE_HANGERS = new WorldGenCaveHangers();
	public static final WorldGenerator GEN_CAVE_GRASS = new WorldGenCaveGrass();
	public static final WorldGenerator GEN_WEEDWOOD_TREE = new WorldGenWeedwoodTree();
	public static final WorldGenerator GEN_ROTTEN_WEEDWOOD_TREE = new WorldGenRottenWeedwoodTree();
	public static final WorldGenerator GEN_SMALL_HOLLOW_LOG = new WorldGenSmallHollowLog();
	public static final WorldGenerator GEN_SWAMP_TALLGRASS = new WorldGenPlantCluster(BlockRegistry.SWAMP_TALLGRASS.getDefaultState());
	public static final WorldGenerator GEN_NETTLES = new WorldGenPlantCluster(BlockRegistry.NETTLE.getDefaultState(), 3, 128);
	public static final WorldGenerator GEN_ARROW_ARUM = new WorldGenPlantCluster(BlockRegistry.ARROW_ARUM.getDefaultState());
	public static final WorldGenerator GEN_PICKEREL_WEED = new WorldGenPlantCluster(BlockRegistry.PICKEREL_WEED.getDefaultState());
	public static final WorldGenerator GEN_MARSH_HIBISCUS = new WorldGenPlantCluster(BlockRegistry.MARSH_HIBISCUS.getDefaultState());
	public static final WorldGenerator GEN_MARSH_MALLOW = new WorldGenPlantCluster(BlockRegistry.MARSH_MALLOW.getDefaultState());
	public static final WorldGenerator GEN_BUTTON_BUSH = new WorldGenPlantCluster(BlockRegistry.BUTTON_BUSH.getDefaultState());
	public static final WorldGenerator GEN_SOFT_RUSH = new WorldGenPlantCluster(BlockRegistry.SOFT_RUSH.getDefaultState());
	public static final WorldGenerator GEN_BOTTLE_BRUSH_GRASS = new WorldGenPlantCluster(BlockRegistry.BOTTLE_BRUSH_GRASS.getDefaultState());
	public static final WorldGenerator GEN_STAGNANT_WATER_POOL = new WorldGenFluidPool(BlockRegistry.STAGNANT_WATER);
	public static final WorldGenerator GEN_TAR_POOL_SURFACE = new WorldGenFluidPool(BlockRegistry.TAR).setMinY(WorldProviderBetweenlands.CAVE_START + 5);
	public static final WorldGenerator GEN_SWAMP_REED = new WorldGenSwampReedCluster();
	public static final WorldGenerator GEN_SWAMP_PLANT = new WorldGenPlantCluster(BlockRegistry.SWAMP_PLANT.getDefaultState(), 8, 256);
	public static final WorldGenerator GEN_FLAT_HEAD_MUSHROOM = new WorldGenPlantCluster(BlockRegistry.FLAT_HEAD_MUSHROOM.getDefaultState(), 5, 40);
	public static final WorldGenerator GEN_BLACK_HAT_MUSHROOM = new WorldGenPlantCluster(BlockRegistry.BLACK_HAT_MUSHROOM.getDefaultState(), 5, 40);
	public static final WorldGenerator GEN_CATTAIL = new WorldGenPlantCluster(BlockRegistry.CATTAIL.getDefaultState());
	public static final WorldGenerator GEN_VENUS_FLY_TRAP = new WorldGenPlantCluster(BlockRegistry.VENUS_FLY_TRAP.getDefaultState(), 5, 64);
	public static final WorldGenerator GEN_MIRE_CORAL = new WorldGenPlantCluster(BlockRegistry.MIRE_CORAL.getDefaultState(), 4, 10).setUnderwater(true);
	public static final WorldGenerator GEN_DEEP_WATER_CORAL = new WorldGenPlantCluster(BlockRegistry.DEEP_WATER_CORAL.getDefaultState(), 4, 10).setUnderwater(true);
	public static final WorldGenerator GEN_BLADDERWORT = new WorldGenBladderwortCluster();
	public static final WorldGenerator GEN_WATER_ROOTS = new WorldGenWaterRootsCluster();
	public static final WorldGenerator GEN_COPPER_IRIS = new WorldGenPlantCluster(BlockRegistry.COPPER_IRIS.getDefaultState());
	public static final WorldGenerator GEN_BLUE_IRIS = new WorldGenPlantCluster(BlockRegistry.BLUE_IRIS.getDefaultState());
	public static final WorldGenerator GEN_SWAMP_KELP = new WorldGenSwampKelpCluster();
	public static final WorldGenerator GEN_MILKWEED = new WorldGenPlantCluster(BlockRegistry.MILKWEED.getDefaultState());
	public static final WorldGenerator GEN_SHOOTS = new WorldGenPlantCluster(BlockRegistry.SHOOTS.getDefaultState());
	public static final WorldGenerator GEN_BLUE_EYED_GRASS = new WorldGenPlantCluster(BlockRegistry.BLUE_EYED_GRASS.getDefaultState());
	public static final WorldGenerator GEN_BONESET = new WorldGenPlantCluster(BlockRegistry.BONESET.getDefaultState());
	public static final WorldGenerator GEN_SLUDGECREEP = new WorldGenPlantCluster(BlockRegistry.SLUDGECREEP.getDefaultState());
	public static final WorldGenerator GEN_DEAD_WEEDWOOD_BUSH = new WorldGenPlantCluster(BlockRegistry.DEAD_WEEDWOOD_BUSH.getDefaultState());
	public static final WorldGenerator GEN_ROOTS = new WorldGenRootsCluster();
	public static final WorldGenerator GEN_WATER_WEEDS = new WorldGenPlantCluster(BlockRegistry.WATER_WEEDS.getDefaultState()).setUnderwater(true);
	public static final WorldGenerator GEN_UNDERGROUND_RUINS = new WorldGenUndergroundRuins();
	public static final WorldGenerator GEN_WEEDWOOD_BUSH = new WorldGenWeedwoodBush();
	public static final WorldGenerator GEN_SAP_TREE = new WorldGenSapTree();
	public static final WorldGenerator GEN_RUBBER_TREE = new WorldGenRubberTree();
	public static final WorldGenerator GEN_BIG_BULB_CAPPED_MUSHROOM = new WorldGenBigBulbCappedMushroom();
	public static final WorldGenerator GEN_MOSS = new WorldGenMossCluster(BlockRegistry.MOSS.getDefaultState());
	public static final WorldGenerator GEN_LICHEN = new WorldGenMossCluster(BlockRegistry.LICHEN.getDefaultState());
	public static final WorldGenerator GEN_MOSS_UNDERGROUND = new WorldGenMossCluster(BlockRegistry.MOSS.getDefaultState(), 4, 80);
	public static final WorldGenerator GEN_LICHEN_UNDERGROUND = new WorldGenMossCluster(BlockRegistry.LICHEN.getDefaultState(), 3, 40);
	public static final WorldGenerator GEN_SPAWNER_STRUCTURE = new WorldGenSpawnerStructure();
	public static final WorldGenerator GEN_IDOL_HEAD = new WorldGenIdolHeads();
	public static final WorldGenerator GEN_SMALL_RUINS = new WorldGenSmallRuins();
	public static final WorldGenGiantTree GEN_GIANT_TREE = new WorldGenGiantTree();
	public static final WorldGenerator GEN_NIBBLETWIG_TREE = new WorldGenNibbletwigTree();
	public static final WorldGenerator GEN_HEARTHGROVE_TREE = new WorldGenHearthgroveTree();
	public static final WorldGenerator GEN_BULB_CAPPED_MUSHROOMS = new WorldGenPlantCluster(BlockRegistry.BULB_CAPPED_MUSHROOM.getDefaultState(), 5, 40);
	public static final WorldGenerator GEN_SPAWNER = new WorldGenSpawner();
	public static final WorldGenerator GEN_DEAD_TRUNK = new WorldGenGiantTreeDead();
	public static final WorldGenerator GEN_MUD_STRUCTURES = new WorldGenMudStructures();
	public static final WorldGenerator GEN_TAR_POOL_DUNGEON = new WorldGenTarPoolDungeon();
	public static final WorldGenerator GEN_SPIRIT_TREE_STRUCTURE = new WorldGenSpiritTreeStructure();
	public static final WorldGenerator GEN_SLUDGE_WORM_DUNGEON = new WorldGenSludgeWormDungeon();
	public static final WorldGenerator GEN_CHIROMAW_MATRIARCH_NEST = new WorldGenChiromawNest();
	public static final WorldGenerator GEN_LAKE_CAVERN_SIMULACRUM = new WorldGenLakeCavernSimulacrum();
	public static final WorldGenerator GEN_DEEPMAN_SIMULACRUM = new WorldGenDeepmanSimulacrum();
	public static final WorldGenerator GEN_ROOTMAN_SIMULACRUM = new WorldGenRootmanSimulacrum();
	public static final WorldGenerator GEN_BARNACLE_CLUSTER = new WorldGenBarnacleCluster(BlockRegistry.BARNACLE_1_2.getDefaultState());
	public static final WorldGenerator GEN_PEBBLE_CLUSTER_LAND = new WorldGenPebbleCluster(BlockRegistry.BETWEENSTONE_PEBBLE_PILE.getDefaultState());
	public static final WorldGenerator GEN_PEBBLE_CLUSTER_WATER = new WorldGenPebbleCluster(BlockRegistry.BETWEENSTONE_PEBBLE_PILE_WATER.getDefaultState()).setUnderwater(true);
	
	private static final CubicBezier SPELEOTHEM_Y_CDF = new CubicBezier(0, 0.5F, 1, 0.2F);
	private static final CubicBezier CAVE_POTS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier THORNS_Y_CDF = new CubicBezier(1, 0.5F, 1, -0.25F);
	private static final CubicBezier CAVE_MOSS_Y_CDF = new CubicBezier(0.5F, 1, 0.8F, 1);
	private static final CubicBezier CAVE_HANGERS_Y_CDF = new CubicBezier(0.8F, 1, 0.5F, 0);
	private static final CubicBezier CAVE_GRASS_Y_CDF = new CubicBezier(0.25F, 1, 0.9F, 1);
	private static final CubicBezier PEBBLE_Y_CDF = new CubicBezier(0, 1, 0, 1);

	private static boolean canShortThingsGenerateHere(DecoratorPositionProvider decorator) {
		return ((ChunkGeneratorBetweenlands)decorator.getChunkGenerator()).evalTreeNoise(decorator.getX() * 0.01, decorator.getZ() * 0.01) > -0.25;
	}

	private static int getSpeleothemAttemptAdditive(DecoratorPositionProvider decorator) {
		return (int) ((((ChunkGeneratorBetweenlands)decorator.getChunkGenerator()).evalSpeleothemDensityNoise(decorator.getX() * 0.03, decorator.getZ() * 0.03) * 0.5 + 0.5) * 25);
	}

	public static boolean generatePhragmites(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.PHRAGMITES.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean populateCaves(BiomeDecoratorBetweenlands decorator) {
		decorator.generate(2 + getSpeleothemAttemptAdditive(decorator), DecorationHelper::generateSpeleothemCluster);
		decorator.generate(decorator.getRand().nextInt(3) == 0 ? 2 : 1, DecorationHelper::generateCavePotsCluster);
		decorator.generate(140, DecorationHelper::generateCaveThornsCluster);
		decorator.generate(120, DecorationHelper::generateCaveMossCluster);
		decorator.generate(120, DecorationHelper::generateCaveHangersCluster);
		decorator.generate(25, DecorationHelper::generateUndergroundMossCluster);
		decorator.generate(5, DecorationHelper::generateUndergroundLichenCluster);
		decorator.generate(120, DecorationHelper::generateCaveGrassCluster);
		decorator.generate(5, DecorationHelper::generateDeepmanSimulacrum);
		decorator.generate(0.6f, DecorationHelper::generateLakeCavernSimulacrum);
		decorator.generate(10, DecorationHelper::generateUndergroundPebbleClusterLand);
		decorator.generate(10, DecorationHelper::generateUndergroundPebbleClusterWater);
		return true;
	}

	public static boolean generateSpeleothemCluster(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = SPELEOTHEM_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		boolean generated = false;
		for(int i = 0; i < 35; i++) {
			int gx = x + decorator.getRand().nextInt(7) - 3;
			int gy = y + decorator.getRand().nextInt(7) - 3;
			int gz = z + decorator.getRand().nextInt(7) - 3;
			generated |= GEN_SPELEOTHEM.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(gx, gy, gz));
		}
		return generated;
	}

	public static boolean generateSpeleothem(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = SPELEOTHEM_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_SPELEOTHEM.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateCavePotsCluster(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_POTS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.CAVE_START - 5.0F - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_POTS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateCaveThornsCluster(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = THORNS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_THORNS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateCaveMossCluster(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_MOSS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_MOSS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}
	
	public static boolean generateCaveHangersCluster(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX(9);
		float v = CAVE_HANGERS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ(9);
		return GEN_CAVE_HANGERS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateUndergroundMossCluster(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_MOSS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.CAVE_START - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_MOSS_UNDERGROUND.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateUndergroundLichenCluster(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_MOSS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.CAVE_START - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_LICHEN_UNDERGROUND.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateCaveGrassCluster(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_GRASS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.PITSTONE_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_GRASS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateDeepmanSimulacrum(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		int y = WorldProviderBetweenlands.CAVE_START - 10 + decorator.getRand().nextInt(WorldProviderBetweenlands.CAVE_START - 10 - WorldProviderBetweenlands.PITSTONE_HEIGHT);
		int z = decorator.getRandomPosZ();
		return GEN_DEEPMAN_SIMULACRUM.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}
	
	public static boolean generateLakeCavernSimulacrum(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		int y = WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 3;
		int z = decorator.getRandomPosZ();
		return GEN_LAKE_CAVERN_SIMULACRUM.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}
	
	public static boolean generateRootmanSimulacrum(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		int y = WorldProviderBetweenlands.LAYER_HEIGHT + 16 + decorator.getRand().nextInt(32);
		int z = decorator.getRandomPosZ();
		return GEN_ROOTMAN_SIMULACRUM.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}
	
	public static boolean generateSpawner(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX(13);
		int y = decorator.getRand().nextInt(WorldProviderBetweenlands.CAVE_START - 20);
		int z = decorator.getRandomPosZ(13);
		if(GEN_SPAWNER.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z))) {
			return true;
		}
		return false;
	}
	
	public static boolean generateTarPoolDungeon(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX(10);
		int y = decorator.getRand().nextInt(WorldProviderBetweenlands.CAVE_START - 20);
		int z = decorator.getRandomPosZ(10);
		if(GEN_TAR_POOL_DUNGEON.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z))) {
			return true;
		}
		return false;
	}

	public static boolean generateWeedwoodTree(DecoratorPositionProvider decorator) {
		if (canShortThingsGenerateHere(decorator)) {
			BlockPos pos = decorator.getRandomPos(14);
			World world = decorator.getWorld();
			if ((world.isAirBlock(pos) && SurfaceType.GRASS.matches(world, pos.down())) ||
					(SurfaceType.WATER.matches(world, pos) && SurfaceType.DIRT.matches(world, pos.down())))
				return GEN_WEEDWOOD_TREE.generate(decorator.getWorld(), decorator.getRand(), pos.down());
		}
		return false;
	}
	
	public static boolean generateNibbletwigTree(DecoratorPositionProvider decorator) {
		if (canShortThingsGenerateHere(decorator)) {
			BlockPos pos = decorator.getRandomPos(10);
			World world = decorator.getWorld();
			if (world.isAirBlock(pos) && SurfaceType.GRASS.matches(world, pos.down()))
				return GEN_NIBBLETWIG_TREE.generate(decorator.getWorld(), decorator.getRand(), pos.down());
		}
		return false;
	}
	
	public static boolean generateHearthgroveTree(DecoratorPositionProvider decorator) {
		if (canShortThingsGenerateHere(decorator)) {
			BlockPos pos = decorator.getRandomPos(12);
			World world = decorator.getWorld();
			if ((world.isAirBlock(pos) && SurfaceType.GRASS.matches(world, pos.down())) ||
					(SurfaceType.WATER.matches(world, pos) && SurfaceType.DIRT.matches(world, pos.down())))
				return GEN_HEARTHGROVE_TREE.generate(decorator.getWorld(), decorator.getRand(), pos.down());
		}
		return false;
	}

	public static boolean generateRottenWeedwoodTree(DecoratorPositionProvider decorator) {
		if (canShortThingsGenerateHere(decorator)) {
			BlockPos pos = decorator.getRandomPos(14);
			World world = decorator.getWorld();
			if (world.isAirBlock(pos) && world.getBlockState(pos.down()).getBlock() == BlockRegistry.MUD)
				return GEN_ROTTEN_WEEDWOOD_TREE.generate(decorator.getWorld(), decorator.getRand(), pos.down());
		}
		return false;
	}

	public static boolean generateSmallHollowLog(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down()))
			return GEN_SMALL_HOLLOW_LOG.generate(decorator.getWorld(), decorator.getRand(), pos);
		return false;
	}

	public static boolean generateSwampTallgrassCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down()))
			return GEN_SWAMP_TALLGRASS.generate(decorator.getWorld(), decorator.getRand(), pos);
		return false;
	}

	public static boolean generateSundew(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.SUNDEW.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateNettlesCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down()))
			return GEN_NETTLES.generate(decorator.getWorld(), decorator.getRand(), pos);
		return false;
	}

	public static boolean generateWeepingBlue(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.WEEPING_BLUE.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateArrowArumCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_ARROW_ARUM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generatePickerelWeedCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_PICKEREL_WEED.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMarshHibiscusCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_MARSH_HIBISCUS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMarshMallowCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_MARSH_MALLOW.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateButtonBushCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_BUTTON_BUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSoftRushCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_SOFT_RUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBroomsedge(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.BROOMSEDGE.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateBottleBrushGrassCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_BOTTLE_BRUSH_GRASS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateStagnantWaterPool(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		int y = 6 + decorator.getRand().nextInt(WorldProviderBetweenlands.CAVE_START / 2);
		int z = decorator.getRandomPosZ();
		BlockPos pos = new BlockPos(x, y, z);
		if(SurfaceType.UNDERGROUND.matches(decorator.getWorld(), pos)) {
			return GEN_STAGNANT_WATER_POOL.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateTarPoolSurface(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		int y = 6 + decorator.getRand().nextInt(WorldProviderBetweenlands.LAYER_HEIGHT + 20);
		int z = decorator.getRandomPosZ();
		BlockPos pos = new BlockPos(x, y, z);
		if(SurfaceType.UNDERGROUND.matches(decorator.getWorld(), pos) || SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos)) {
			return GEN_TAR_POOL_SURFACE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSwampReedCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround(10);
		World world = decorator.getWorld();

		if(SurfaceType.WATER.matches(world, pos.up()) && world.getBlockState(pos).getBlock() == BlockRegistry.MUD && world.isAirBlock(pos.up(2))) {
			return GEN_SWAMP_REED.generate(decorator.getWorld(), decorator.getRand(), pos);
		} else if(SurfaceType.MIXED_GROUND.matches(world, pos) && BlockRegistry.SWAMP_REED.canPlaceBlockAt(world, pos.up())) {
			return GEN_SWAMP_REED.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSwampPlantCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_SWAMP_PLANT.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateVenusFlyTrapCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_VENUS_FLY_TRAP.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generatePitcherPlant(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.PITCHER_PLANT.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateFlatHeadMushroomCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && !decorator.getWorld().isAirBlock(pos.down())) {
			return GEN_FLAT_HEAD_MUSHROOM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBlackHatMushroomCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && !decorator.getWorld().isAirBlock(pos.down())) {
			return GEN_BLACK_HAT_MUSHROOM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateVolarpad(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.VOLARPAD.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateTallCattail(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.TALL_CATTAIL.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateSwampDoubleTallgrass(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.SWAMP_DOUBLE_TALLGRASS.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateCattailCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_CATTAIL.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMireCoralCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_MIRE_CORAL.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateDeepWaterCoralCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_DEEP_WATER_CORAL.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBladderwortCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround(10);
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BLADDERWORT.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateWaterRootsCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround(10);
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_WATER_ROOTS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateCopperIrisCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_COPPER_IRIS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBlueIrisCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_BLUE_IRIS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSwampKelpCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround(10);
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_SWAMP_KELP.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMilkweedCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_MILKWEED.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateShootsCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_SHOOTS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateCardinalFlower(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.CARDINAL_FLOWER.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateBlueEyedGrassCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_BLUE_EYED_GRASS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBonesetCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_BONESET.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMarshMarigold(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.WATER.matches(decorator.getWorld(), pos.down()) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down(2))) {
			decorator.getWorld().setBlockState(pos.down(), BlockRegistry.MARSH_MARIGOLD_STALK.getDefaultState());
			decorator.getWorld().setBlockState(pos, BlockRegistry.MARSH_MARIGOLD_FLOWER.getDefaultState());
			return true;
		}
		return false;
	}

	public static boolean generateBogBean(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.WATER.matches(decorator.getWorld(), pos.down()) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down(2))) {
			decorator.getWorld().setBlockState(pos.down(), BlockRegistry.BOG_BEAN_STALK.getDefaultState());
			decorator.getWorld().setBlockState(pos, BlockRegistry.BOG_BEAN_FLOWER.getDefaultState());
			return true;
		}
		return false;
	}

	public static boolean generateGoldenClub(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.WATER.matches(decorator.getWorld(), pos.down()) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down(2))) {
			decorator.getWorld().setBlockState(pos.down(), BlockRegistry.GOLDEN_CLUB_STALK.getDefaultState());
			decorator.getWorld().setBlockState(pos, BlockRegistry.GOLDEN_CLUB_FLOWER.getDefaultState());
			return true;
		}
		return false;
	}

	public static boolean generateSludgecreepCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_SLUDGECREEP.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateDeadWeedwoodBushCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_DEAD_WEEDWOOD_BUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateRootsCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos(10);
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_ROOTS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateWaterWeedsCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_WATER_WEEDS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateUndergroundRuins(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX(10);
		int y = WorldProviderBetweenlands.CAVE_WATER_HEIGHT + decorator.getRand().nextInt(20);
		int z = decorator.getRandomPosZ(10);
		BlockPos pos = new BlockPos(x, y, z);
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.UNDERGROUND.matches(decorator.getWorld(), pos.down())) {
			return GEN_UNDERGROUND_RUINS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateCragrockTower(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround(10);
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down())) {
			return new WorldGenCragrockTower().generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateWeedwoodBush(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.PLANT_DECORATION_SOIL.matches(decorator.getWorld(), pos.down())) {
			return GEN_WEEDWOOD_BUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSapTree(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_SAP_TREE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateRubberTree(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_RUBBER_TREE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBigBulbCappedMushroom(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BIG_BULB_CAPPED_MUSHROOM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMossCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && decorator.getWorld().getBlockState(pos.down()).isNormalCube()) {
			return GEN_MOSS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateLichenCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && decorator.getWorld().getBlockState(pos.down()).isNormalCube()) {
			return GEN_LICHEN.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSpawnerStructure(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down())) {
			return GEN_SPAWNER_STRUCTURE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSunkenIdolHead(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down()) &&
				SurfaceType.WATER.matches(decorator.getWorld(), pos.up())) {
			return GEN_IDOL_HEAD.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSmallRuinsCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down())) {
			return GEN_SMALL_RUINS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateFallenLeaves(DecoratorPositionProvider decorator) {
		World world = decorator.getWorld();
		MutableBlockPos checkPos = new MutableBlockPos();
		boolean generated = false;
		for(int xo = 0; xo < 32; xo++) {
			for(int zo = 0; zo < 32; zo++) {
				int px = decorator.getX() + xo;
				int pz = decorator.getZ() + zo;
				int py = world.getHeight(px, pz) - 1;
				Block surfaceBlock = world.getBlockState(checkPos.setPos(px, py, pz)).getBlock();
				if(surfaceBlock instanceof BlockLeaves) {
					int yo = 0;
					boolean hasLeaves = true;
					for(int i = 0; i < 128; i++) {
						yo++;
						if(py-yo <= WorldProviderBetweenlands.CAVE_START) {
							break;
						}
						IBlockState cBlockState = world.getBlockState(checkPos.setPos(px, py-yo, pz));
						boolean isBlockLeaves = cBlockState.getBlock() instanceof BlockLeaves;
						if(isBlockLeaves) {
							hasLeaves = true;
						}
						if(hasLeaves && (SurfaceType.GRASS_AND_DIRT.matches(cBlockState) || cBlockState.getBlock() == BlockRegistry.LOG_WEEDWOOD)) {
							if(world.isAirBlock(checkPos.setPos(px, py-yo+1, pz)) && decorator.getRand().nextInt(3) == 0) {
								world.setBlockState(new BlockPos(px, py-yo+1, pz), BlockRegistry.FALLEN_LEAVES.getDefaultState(), 2 | 16);
								generated = true;
							}
						}
						if(!isBlockLeaves && cBlockState.isOpaqueCube()) {
							hasLeaves = false;
						}
					}
				}
			}
		}
		return generated;
	}
	
	public static boolean generateWisp(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.WATER.matches(decorator.getWorld(), pos.down())) {
			Collection<Integer> allowedValues = BlockWisp.COLOR.getAllowedValues();
			int color = 0;
			int rand = decorator.getRand().nextInt(allowedValues.size());
			for(Integer c : allowedValues) {
				if(rand-- < 0) {
					color = c;
				}
			}
			return decorator.getWorld().setBlockState(pos, BlockRegistry.WISP.getDefaultState().withProperty(BlockWisp.COLOR, color));
		}
		return false;
	}

	public static boolean generateGiantTree(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld().getBlockState(pos.down()))) {
			pos = pos.add(0, -8, 0);
			return GEN_GIANT_TREE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateWightFortress(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos(1);
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down())) {
			Biome biome = decorator.getWorld().getBiome(pos);
			WorldGenWightFortress fortress = new WorldGenWightFortress();
			if(fortress.isBiomeValid(biome)) {
				return fortress.generate(decorator.getWorld(), decorator.getRand(), pos);
			}
		}
		return false;
	}

	public static boolean generateChiromawMatriarchNest(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos(6);
		pos = decorator.getWorld().getHeight(pos);
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down()) && pos.getY() >= WorldProviderBetweenlands.LAYER_HEIGHT + 25) {
			return GEN_CHIROMAW_MATRIARCH_NEST.generate(decorator.getWorld(), decorator.getRand(), pos.down());
		}
		return false;
	}
	
	public static boolean generateRottenLogs(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down())) {
			WorldGenRottenLogs gen = new WorldGenRottenLogs(decorator.getRand().nextInt(5) + 4, decorator.getRand().nextInt(3) + 2, (byte)decorator.getRand().nextInt(2));
			gen.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateDeadTrunk(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down())) {
			GEN_DEAD_TRUNK.generate(decorator.getWorld(), decorator.getRand(), pos.down(8 + decorator.getRand().nextInt(5)));
		}
		return false;
	}
	
	public static boolean generateMudStructures(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos(10);
		if(decorator.getWorld().isAirBlock(pos) && decorator.getWorld().getBlockState(pos.down()).getBlock() == BlockRegistry.MUD) {
			return GEN_MUD_STRUCTURES.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}
	
	private static boolean generateSubBiomeStructure(DecoratorPositionProvider decorator, WorldGenerator generator, int offsetX, int offsetZ, int biomeCheckRange, Biome biome, SurfaceType surface) {
		MutableBlockPos pos = new MutableBlockPos();
		List<BlockPos> potentialPositions = new ArrayList<>();
		for(int xo = 0; xo < 32; xo += 3) {
			for(int zo = 0; zo < 32; zo += 3) {
				Biome biomeN = decorator.getWorld().getBiome(pos.setPos(decorator.getX() + xo, decorator.getY(), decorator.getZ() + zo - biomeCheckRange));
				Biome biomeE = decorator.getWorld().getBiome(pos.setPos(decorator.getX() + xo + biomeCheckRange, decorator.getY(), decorator.getZ() + zo));
				Biome biomeS = decorator.getWorld().getBiome(pos.setPos(decorator.getX() + xo, decorator.getY(), decorator.getZ() + zo + biomeCheckRange));
				Biome biomeW = decorator.getWorld().getBiome(pos.setPos(decorator.getX() + xo - biomeCheckRange, decorator.getY(), decorator.getZ() + zo));
				if(biomeN == biome && biomeE == biome && biomeS == biome && biomeW == biome) {
					pos.setPos(decorator.getX() + xo, decorator.getY(), decorator.getZ() + zo);
					pos.setY(decorator.getWorld().getChunk(pos).getHeightValue(pos.getX() & 15, pos.getY() & 15));
					BlockPos genPos = null;
					for(int i = 0; i > -8; i--) {
						if(surface.matches(decorator.getWorld().getBlockState(pos))) {
							genPos = pos.toImmutable().up();
							break;
						}
						pos.setY(pos.getY() - 1);
					}
					if(genPos != null) {
						potentialPositions.add(genPos);
					}
				}
			}
		}
		Collections.shuffle(potentialPositions, decorator.getRand());
		for(BlockPos genPos : potentialPositions) {
			if(generator.generate(decorator.getWorld(), decorator.getRand(), genPos.add(-offsetX, 0, -offsetZ))) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean generateSwamplandsClearingSpiritTree(DecoratorPositionProvider decorator) {
		return generateSubBiomeStructure(decorator, GEN_SPIRIT_TREE_STRUCTURE, 0, 0, 36, BiomeRegistry.SWAMPLANDS_CLEARING, SurfaceType.MIXED_GROUND);
	}
	
	public static boolean generateSludgePlainsClearingDungeon(DecoratorPositionProvider decorator) {
		return generateSubBiomeStructure(decorator, GEN_SLUDGE_WORM_DUNGEON, 16, 16, 45, BiomeRegistry.SLUDGE_PLAINS_CLEARING, SurfaceType.MIXED_GROUND);
	}
	
	public static boolean generateBarnacleCluster(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && decorator.getWorld().getBlockState(pos.down()).isNormalCube()) {
			return GEN_BARNACLE_CLUSTER.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generatePebbleClusterLand(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND_AND_UNDERGROUND.matches(decorator.getWorld(), pos.down())) {
			return GEN_PEBBLE_CLUSTER_LAND.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generatePebbleClusterWater(DecoratorPositionProvider decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED_GROUND.matches(decorator.getWorld(), pos.down())) {
			return GEN_PEBBLE_CLUSTER_WATER.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateUndergroundPebbleClusterLand(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = PEBBLE_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.CAVE_START - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_PEBBLE_CLUSTER_LAND.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}
	
	public static boolean generateUndergroundPebbleClusterWater(DecoratorPositionProvider decorator) {
		int x = decorator.getRandomPosX();
		float v = PEBBLE_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.CAVE_START - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_PEBBLE_CLUSTER_WATER.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}
}
