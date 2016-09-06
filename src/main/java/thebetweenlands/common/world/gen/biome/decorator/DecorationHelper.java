package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.feature.WorldGenBigBulbCappedMushroom;
import thebetweenlands.common.world.gen.feature.WorldGenBladderwortCluster;
import thebetweenlands.common.world.gen.feature.WorldGenCaveGrass;
import thebetweenlands.common.world.gen.feature.WorldGenCaveMoss;
import thebetweenlands.common.world.gen.feature.WorldGenCavePots;
import thebetweenlands.common.world.gen.feature.WorldGenCaveThorns;
import thebetweenlands.common.world.gen.feature.WorldGenFluidPool;
import thebetweenlands.common.world.gen.feature.WorldGenIdolHeads;
import thebetweenlands.common.world.gen.feature.WorldGenMossCluster;
import thebetweenlands.common.world.gen.feature.WorldGenPlantCluster;
import thebetweenlands.common.world.gen.feature.WorldGenRootsCluster;
import thebetweenlands.common.world.gen.feature.WorldGenRubberTree;
import thebetweenlands.common.world.gen.feature.WorldGenSmallHollowLog;
import thebetweenlands.common.world.gen.feature.WorldGenSmallRuins;
import thebetweenlands.common.world.gen.feature.WorldGenSpawnerStructure;
import thebetweenlands.common.world.gen.feature.WorldGenSpeleothem;
import thebetweenlands.common.world.gen.feature.WorldGenSwampKelpCluster;
import thebetweenlands.common.world.gen.feature.WorldGenSwampReedCluster;
import thebetweenlands.common.world.gen.feature.WorldGenWaterRootsCluster;
import thebetweenlands.common.world.gen.feature.WorldGenWeedwoodBush;
import thebetweenlands.common.world.gen.feature.structure.WorldGenCragrockTower;
import thebetweenlands.common.world.gen.feature.structure.WorldGenUndergroundRuins;
import thebetweenlands.common.world.gen.feature.tree.WorldGenSapTree;
import thebetweenlands.common.world.gen.feature.tree.WorldGenWeedwoodTree;
import thebetweenlands.util.CubicBezier;

public class DecorationHelper {
	private static final WorldGenerator GEN_SPELEOTHEM = new WorldGenSpeleothem();
	private static final WorldGenerator GEN_CAVE_POTS = new WorldGenCavePots();
	private static final WorldGenerator GEN_CAVE_THORNS = new WorldGenCaveThorns();
	private static final WorldGenerator GEN_CAVE_MOSS = new WorldGenCaveMoss();
	private static final WorldGenerator GEN_CAVE_GRASS = new WorldGenCaveGrass();
	private static final WorldGenerator GEN_WEEDWOOD_TREE = new WorldGenWeedwoodTree();
	private static final WorldGenerator GEN_SMALL_HOLLOW_LOG = new WorldGenSmallHollowLog();
	private static final WorldGenerator GEN_SWAMP_TALLGRASS = new WorldGenPlantCluster(BlockRegistry.SWAMP_TALLGRASS.getDefaultState());
	private static final WorldGenerator GEN_NETTLES = new WorldGenPlantCluster(BlockRegistry.NETTLE.getDefaultState());
	private static final WorldGenerator GEN_ARROW_ARUM = new WorldGenPlantCluster(BlockRegistry.ARROW_ARUM.getDefaultState());
	private static final WorldGenerator GEN_PICKEREL_WEED = new WorldGenPlantCluster(BlockRegistry.PICKEREL_WEED.getDefaultState());
	private static final WorldGenerator GEN_MARSH_HIBISCUS = new WorldGenPlantCluster(BlockRegistry.MARSH_HIBISCUS.getDefaultState());
	private static final WorldGenerator GEN_MARSH_MALLOW = new WorldGenPlantCluster(BlockRegistry.MARSH_MALLOW.getDefaultState());
	private static final WorldGenerator GEN_BUTTON_BUSH = new WorldGenPlantCluster(BlockRegistry.BUTTON_BUSH.getDefaultState());
	private static final WorldGenerator GEN_SOFT_RUSH = new WorldGenPlantCluster(BlockRegistry.SOFT_RUSH.getDefaultState());
	private static final WorldGenerator GEN_BOTTLE_BRUSH_GRASS = new WorldGenPlantCluster(BlockRegistry.BOTTLE_BRUSH_GRASS.getDefaultState());
	private static final WorldGenerator GEN_STAGNANT_WATER_POOL = new WorldGenFluidPool(BlockRegistry.STAGNANT_WATER);
	private static final WorldGenerator GEN_TAR_POOL_SURFACE = new WorldGenFluidPool(BlockRegistry.TAR).setMinY(WorldProviderBetweenlands.CAVE_START + 5);
	private static final WorldGenerator GEN_SWAMP_REED = new WorldGenSwampReedCluster();
	private static final WorldGenerator GEN_SWAMP_PLANT = new WorldGenPlantCluster(BlockRegistry.SWAMP_PLANT.getDefaultState());
	private static final WorldGenerator GEN_FLAT_HEAD_MUSHROOM = new WorldGenPlantCluster(BlockRegistry.FLAT_HEAD_MUSHROOM.getDefaultState(), 5, 15);
	private static final WorldGenerator GEN_BLACK_HAT_MUSHROOM = new WorldGenPlantCluster(BlockRegistry.BLACK_HAT_MUSHROOM.getDefaultState(), 5, 15);
	private static final WorldGenerator GEN_CATTAIL = new WorldGenPlantCluster(BlockRegistry.CATTAIL.getDefaultState());
	private static final WorldGenerator GEN_VENUS_FLY_TRAP = new WorldGenPlantCluster(BlockRegistry.VENUS_FLY_TRAP.getDefaultState(), 5, 20);
	private static final WorldGenerator GEN_MIRE_CORAL = new WorldGenPlantCluster(BlockRegistry.MIRE_CORAL.getDefaultState(), 4, 10).setUnderwater(true);
	private static final WorldGenerator GEN_DEEP_WATER_CORAL = new WorldGenPlantCluster(BlockRegistry.DEEP_WATER_CORAL.getDefaultState(), 4, 10).setUnderwater(true);
	private static final WorldGenerator GEN_BLADDERWORT = new WorldGenBladderwortCluster();
	private static final WorldGenerator GEN_WATER_ROOTS = new WorldGenWaterRootsCluster();
	private static final WorldGenerator GEN_COPPER_IRIS = new WorldGenPlantCluster(BlockRegistry.COPPER_IRIS.getDefaultState());
	private static final WorldGenerator GEN_BLUE_IRIS = new WorldGenPlantCluster(BlockRegistry.BLUE_IRIS.getDefaultState());
	private static final WorldGenerator GEN_SWAMP_KELP = new WorldGenSwampKelpCluster();
	private static final WorldGenerator GEN_MILKWEED = new WorldGenPlantCluster(BlockRegistry.MILKWEED.getDefaultState());
	private static final WorldGenerator GEN_SHOOTS = new WorldGenPlantCluster(BlockRegistry.SHOOTS.getDefaultState());
	private static final WorldGenerator GEN_BLUE_EYED_GRASS = new WorldGenPlantCluster(BlockRegistry.BLUE_EYED_GRASS.getDefaultState());
	private static final WorldGenerator GEN_BONESET = new WorldGenPlantCluster(BlockRegistry.BONESET.getDefaultState());
	private static final WorldGenerator GEN_SLUDGECREEP = new WorldGenPlantCluster(BlockRegistry.SLUDGECREEP.getDefaultState());
	private static final WorldGenerator GEN_DEAD_WEEDWOOD_BUSH = new WorldGenPlantCluster(BlockRegistry.DEAD_WEEDWOOD_BUSH.getDefaultState());
	private static final WorldGenerator GEN_ROOTS = new WorldGenRootsCluster();
	private static final WorldGenerator GEN_WATER_WEEDS = new WorldGenPlantCluster(BlockRegistry.WATER_WEEDS.getDefaultState()).setUnderwater(true);
	private static final WorldGenerator GEN_UNDERGROUND_RUINS = new WorldGenUndergroundRuins();
	private static final WorldGenerator GEN_CRAGROCK_TOWER = new WorldGenCragrockTower();
	private static final WorldGenerator GEN_WEEDWOOD_BUSH = new WorldGenWeedwoodBush();
	private static final WorldGenerator GEN_SAP_TREE = new WorldGenSapTree();
	private static final WorldGenerator GEN_RUBBER_TREE = new WorldGenRubberTree();
	private static final WorldGenerator GEN_BIG_BULB_CAPPED_MUSHROOM = new WorldGenBigBulbCappedMushroom();
	private static final WorldGenerator GEN_MOSS = new WorldGenMossCluster(BlockRegistry.MOSS.getDefaultState());
	private static final WorldGenerator GEN_LICHEN = new WorldGenMossCluster(BlockRegistry.LICHEN.getDefaultState());
	private static final WorldGenerator GEN_SPAWNER_STRUCTURE = new WorldGenSpawnerStructure();
	private static final WorldGenerator GEN_IDOL_HEAD = new WorldGenIdolHeads();
	private static final WorldGenerator GEN_SMALL_RUINS = new WorldGenSmallRuins();

	private static final CubicBezier SPELEOTHEM_Y_CDF = new CubicBezier(0, 0.5F, 1, 0.2F);
	private static final CubicBezier CAVE_POTS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier THORNS_Y_CDF = new CubicBezier(1, 0.5F, 1, -0.25F);
	private static final CubicBezier CAVE_MOSS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier CAVE_GRASS_Y_CDF = new CubicBezier(0, 1, 0, 1);

	private static boolean canShortThingsGenerateHere(BiomeDecoratorBetweenlands decorator) {
		return decorator.getChunkGenerator().evalTreeNoise(decorator.getX() * 0.01, decorator.getZ() * 0.01) > -0.25;
	}

	private static int getSpeleothemAttemptAdditive(BiomeDecoratorBetweenlands decorator) {
		return (int) ((decorator.getChunkGenerator().evalSpeleothemDensityNoise(decorator.getX() * 0.03, decorator.getZ() * 0.03) * 0.5 + 0.5) * 80);
	}

	public static boolean generatePhragmites(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.PHRAGMITES.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean populateCaves(BiomeDecoratorBetweenlands decorator) {
		decorator.generate(40 + getSpeleothemAttemptAdditive(decorator), DecorationHelper::generateSpeleothem);
		decorator.generate(decorator.getRand().nextInt(3) == 0 ? 2 : 1, DecorationHelper::generateCavePotsCluster);
		decorator.generate(200, DecorationHelper::generateCaveThornsCluster);
		decorator.generate(100, DecorationHelper::generateCaveMossCluster);
		decorator.generate(120, DecorationHelper::generateCaveGrassCluster);
		return true;
	}

	public static boolean generateSpeleothem(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = SPELEOTHEM_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_SPELEOTHEM.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateCavePotsCluster(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_POTS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.CAVE_START - 5.0F - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_POTS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateCaveThornsCluster(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = THORNS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_THORNS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateCaveMossCluster(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_MOSS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_MOSS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateCaveGrassCluster(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_GRASS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.PITSTONE_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_GRASS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	public static boolean generateWeedwoodTree(BiomeDecoratorBetweenlands decorator) {
		if (canShortThingsGenerateHere(decorator)) {
			BlockPos pos = decorator.getRandomPos(14);
			World world = decorator.getWorld();
			if ((world.isAirBlock(pos) && SurfaceType.GRASS.matches(world, pos.down())) ||
					(SurfaceType.WATER.matches(world, pos) && world.getBlockState(pos.down()) == BlockRegistry.MUD))
				return GEN_WEEDWOOD_TREE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSmallHollowLog(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED.matches(decorator.getWorld(), pos.down()))
			return GEN_SMALL_HOLLOW_LOG.generate(decorator.getWorld(), decorator.getRand(), pos);
		return false;
	}

	public static boolean generateSwampTallgrassCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down()))
			return GEN_SWAMP_TALLGRASS.generate(decorator.getWorld(), decorator.getRand(), pos);
		return false;
	}

	public static boolean generateSundew(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.SUNDEW.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateNettlesCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down()))
			return GEN_NETTLES.generate(decorator.getWorld(), decorator.getRand(), pos);
		return false;
	}

	public static boolean generateWeepingBlue(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.WEEPING_BLUE.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateArrowArumCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_ARROW_ARUM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generatePickerelWeedCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_PICKEREL_WEED.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMarshHibiscusCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_MARSH_HIBISCUS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMarshMallowCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_MARSH_MALLOW.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateButtonBushCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BUTTON_BUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSoftRushCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_SOFT_RUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBroomsedge(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.BROOMSEDGE.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateBottleBrushGrassCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BOTTLE_BRUSH_GRASS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateStagnantWaterPool(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		int y = 6 + decorator.getRand().nextInt(WorldProviderBetweenlands.CAVE_START / 2);
		int z = decorator.getRandomPosZ();
		BlockPos pos = new BlockPos(x, y, z);
		if(SurfaceType.UNDERGROUND.matches(decorator.getWorld(), pos)) {
			return GEN_STAGNANT_WATER_POOL.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateTarPoolSurface(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		int y = 6 + decorator.getRand().nextInt(WorldProviderBetweenlands.LAYER_HEIGHT + 20);
		int z = decorator.getRandomPosZ();
		BlockPos pos = new BlockPos(x, y, z);
		if(SurfaceType.UNDERGROUND.matches(decorator.getWorld(), pos) || SurfaceType.MIXED.matches(decorator.getWorld(), pos)) {
			return GEN_TAR_POOL_SURFACE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSwampReedCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		World world = decorator.getWorld();
		if(SurfaceType.WATER.matches(world, pos.up()) && world.getBlockState(pos).getBlock() == BlockRegistry.MUD && world.isAirBlock(pos.up(2))) {
			return GEN_SWAMP_REED.generate(decorator.getWorld(), decorator.getRand(), pos);
		} else if(SurfaceType.MIXED.matches(world, pos) && BlockRegistry.SWAMP_REED.canPlaceBlockAt(world, pos.up())) {
			return GEN_SWAMP_REED.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSwampPlantCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_SWAMP_PLANT.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateVenusFlyTrapCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_VENUS_FLY_TRAP.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generatePitcherPlant(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.PITCHER_PLANT.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateFlatHeadMushroomCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && !decorator.getWorld().isAirBlock(pos.down())) {
			return GEN_FLAT_HEAD_MUSHROOM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBlackHatMushroomCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && !decorator.getWorld().isAirBlock(pos.down())) {
			return GEN_BLACK_HAT_MUSHROOM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateVolarpad(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.VOLARPAD.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateTallCattail(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.TALL_CATTAIL.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateCattailCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_CATTAIL.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMireCoralCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_MIRE_CORAL.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateDeepWaterCoralCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_DEEP_WATER_CORAL.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBladderwortCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BLADDERWORT.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateWaterRootsCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_WATER_ROOTS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateCopperIrisCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_COPPER_IRIS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBlueIrisCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BLUE_IRIS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSwampKelpCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_SWAMP_KELP.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMilkweedCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_MILKWEED.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateShootsCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_SHOOTS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateCardinalFlower(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.CARDINAL_FLOWER.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateBlueEyedGrassCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BLUE_EYED_GRASS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBonesetCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BONESET.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMarshMarigold(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.WATER.matches(decorator.getWorld(), pos.down()) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down(2))) {
			decorator.getWorld().setBlockState(pos.down(), BlockRegistry.MARSH_MARIGOLD_STALK.getDefaultState());
			decorator.getWorld().setBlockState(pos, BlockRegistry.MARSH_MARIGOLD_FLOWER.getDefaultState());
			return true;
		}
		return false;
	}

	public static boolean generateBogBean(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.WATER.matches(decorator.getWorld(), pos.down()) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down(2))) {
			decorator.getWorld().setBlockState(pos.down(), BlockRegistry.BOG_BEAN_STALK.getDefaultState());
			decorator.getWorld().setBlockState(pos, BlockRegistry.BOG_BEAN_FLOWER.getDefaultState());
			return true;
		}
		return false;
	}

	public static boolean generateGoldenClub(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.WATER.matches(decorator.getWorld(), pos.down()) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down(2))) {
			decorator.getWorld().setBlockState(pos.down(), BlockRegistry.GOLDEN_CLUB_STALK.getDefaultState());
			decorator.getWorld().setBlockState(pos, BlockRegistry.GOLDEN_CLUB_FLOWER.getDefaultState());
			return true;
		}
		return false;
	}

	public static boolean generateSludgecreepCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_SLUDGECREEP.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateDeadWeedwoodBushCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_DEAD_WEEDWOOD_BUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateRootsCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED.matches(decorator.getWorld(), pos.down())) {
			return GEN_ROOTS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateWaterWeedsCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround();
		if(SurfaceType.WATER.matches(decorator.getWorld(), pos) && SurfaceType.DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_WATER_WEEDS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateUndergroundRuins(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX(10);
		int y = WorldProviderBetweenlands.CAVE_WATER_HEIGHT + decorator.getRand().nextInt(20);
		int z = decorator.getRandomPosZ(10);
		BlockPos pos = new BlockPos(x, y, z);
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.UNDERGROUND.matches(decorator.getWorld(), pos.down())) {
			return GEN_UNDERGROUND_RUINS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateCragrockTower(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPosSeaGround(10);
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED.matches(decorator.getWorld(), pos.down())) {
			return GEN_CRAGROCK_TOWER.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateWeedwoodBush(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_WEEDWOOD_BUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSapTree(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_SAP_TREE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateRubberTree(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_RUBBER_TREE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateBigBulbCappedMushroom(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS_AND_DIRT.matches(decorator.getWorld(), pos.down())) {
			return GEN_BIG_BULB_CAPPED_MUSHROOM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMossCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && decorator.getWorld().getBlockState(pos.down()).isNormalCube()) {
			return GEN_MOSS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateLichenCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && decorator.getWorld().getBlockState(pos.down()).isNormalCube()) {
			return GEN_LICHEN.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSpawnerStructure(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED.matches(decorator.getWorld(), pos.down())) {
			return GEN_SPAWNER_STRUCTURE.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSunkenIdolHead(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(SurfaceType.MIXED.matches(decorator.getWorld(), pos.down()) &&
				SurfaceType.WATER.matches(decorator.getWorld(), pos.up())) {
			return GEN_IDOL_HEAD.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateSmallRuinsCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.MIXED.matches(decorator.getWorld(), pos.down())) {
			return GEN_SMALL_RUINS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateFallenLeaves(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		Block block = decorator.getWorld().getBlockState(pos.down()).getBlock();
		if (decorator.getRand().nextInt(3) == 0 && decorator.getWorld().isAirBlock(pos) && block == BlockRegistry.DEAD_GRASS || block == BlockRegistry.SWAMP_GRASS || block == BlockRegistry.SWAMP_DIRT || block == BlockRegistry.MUD || block == BlockRegistry.WEEDWOOD) {
			return decorator.getWorld().setBlockState(pos, BlockRegistry.FALLEN_LEAVES.getDefaultState());
		}
		return false;
	}
}
