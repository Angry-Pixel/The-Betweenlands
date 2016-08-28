package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.feature.WorldGenCaveGrass;
import thebetweenlands.common.world.gen.feature.WorldGenCaveMoss;
import thebetweenlands.common.world.gen.feature.WorldGenCavePots;
import thebetweenlands.common.world.gen.feature.WorldGenCaveThorns;
import thebetweenlands.common.world.gen.feature.WorldGenPlantCluster;
import thebetweenlands.common.world.gen.feature.WorldGenSmallHollowLog;
import thebetweenlands.common.world.gen.feature.WorldGenSpeleothem;
import thebetweenlands.common.world.gen.feature.tree.WorldGenWeedwoodTree;
import thebetweenlands.util.CubicBezier;

public class DecorationHelper {
	private final static WorldGenerator GEN_SPELEOTHEM = new WorldGenSpeleothem();
	private final static WorldGenerator GEN_CAVE_POTS = new WorldGenCavePots();
	private final static WorldGenerator GEN_CAVE_THORNS = new WorldGenCaveThorns();
	private final static WorldGenerator GEN_CAVE_MOSS = new WorldGenCaveMoss();
	private final static WorldGenerator GEN_CAVE_GRASS = new WorldGenCaveGrass();
	private final static WorldGenerator GEN_WEEDWOOD_TREE = new WorldGenWeedwoodTree();
	private final static WorldGenerator GEN_SMALL_HOLLOW_LOG = new WorldGenSmallHollowLog();
	private final static WorldGenerator GEN_SWAMP_TALLGRASS = new WorldGenPlantCluster(BlockRegistry.SWAMP_TALLGRASS.getDefaultState());
	private final static WorldGenerator GEN_NETTLES = new WorldGenPlantCluster(BlockRegistry.NETTLE.getDefaultState());
	private final static WorldGenerator GEN_ARROW_ARUM = new WorldGenPlantCluster(BlockRegistry.ARROW_ARUM.getDefaultState());
	private final static WorldGenerator GEN_PICKEREL_WEED = new WorldGenPlantCluster(BlockRegistry.PICKEREL_WEED.getDefaultState());
	private final static WorldGenerator GEN_MARSH_HIBISCUS = new WorldGenPlantCluster(BlockRegistry.MARSH_HIBISCUS.getDefaultState());
	private final static WorldGenerator GEN_MARSH_MALLOW = new WorldGenPlantCluster(BlockRegistry.MARSH_MALLOW.getDefaultState());
	private final static WorldGenerator GEN_BUTTON_BUSH = new WorldGenPlantCluster(BlockRegistry.BUTTON_BUSH.getDefaultState());
	private final static WorldGenerator GEN_SOFT_RUSH = new WorldGenPlantCluster(BlockRegistry.SOFT_RUSH.getDefaultState());
	private final static WorldGenerator GEN_BOTTLE_BRUSH_GRASS = new WorldGenPlantCluster(BlockRegistry.BOTTLE_BRUSH_GRASS.getDefaultState());
	
	private static final CubicBezier SPELEOTHEM_Y_CDF = new CubicBezier(0, 0.5F, 1, 0.2F);
	private static final CubicBezier CAVE_POTS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier THORNS_Y_CDF = new CubicBezier(1, 0.5F, 1, -0.25F);
	private static final CubicBezier CAVE_MOSS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier CAVE_GRASS_Y_CDF = new CubicBezier(0, 1, 0, 1);

	private static boolean canShortThingsGenerateHere(BiomeDecoratorBetweenlands decorator) {
		return decorator.getChunkGenerator().evalTreeNoise(decorator.getX() * 0.01, decorator.getZ() * 0.01) > -0.25;
	}

	private static int getSpeleothemAttemptAdditive(BiomeDecoratorBetweenlands decorator) {
		return (int) ((decorator.getChunkGenerator().evalSpeleothemDensityNoise(decorator.getX() * 0.03, decorator.getZ() * 0.03) * 0.5 + 0.5) * 20);
	}

	public static boolean generatePhragmites(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.PHRAGMITES.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean populateCaves(BiomeDecoratorBetweenlands decorator) {
		decorator.generate(60, DecorationHelper::generateSpeleothem);
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
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down()))
			return GEN_SWAMP_TALLGRASS.generate(decorator.getWorld(), decorator.getRand(), pos);
		return false;
	}

	public static boolean generateSundew(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.SUNDEW.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateNettlesCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down()))
			return GEN_NETTLES.generate(decorator.getWorld(), decorator.getRand(), pos);
		return false;
	}

	public static boolean generateWeepingBlue(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.WEEPING_BLUE.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}

	public static boolean generateArrowArumCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			return GEN_ARROW_ARUM.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generatePickerelWeedCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			return GEN_PICKEREL_WEED.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMarshHibiscusCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			return GEN_MARSH_HIBISCUS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}

	public static boolean generateMarshMallowCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			return GEN_MARSH_MALLOW.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}
	
	public static boolean generateButtonBushCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			return GEN_BUTTON_BUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}
	
	public static boolean generateSoftRushCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			return GEN_SOFT_RUSH.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}
	
	public static boolean generateBroomsedge(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			BlockRegistry.BROOMSEDGE.placeAt(decorator.getWorld(), pos, 2);
			return true;
		}
		return false;
	}
	
	public static boolean generateBottleBrushGrassCluster(BiomeDecoratorBetweenlands decorator) {
		BlockPos pos = decorator.getRandomPos();
		if(decorator.getWorld().isAirBlock(pos) && SurfaceType.GRASS.matches(decorator.getWorld(), pos.down())) {
			return GEN_BOTTLE_BRUSH_GRASS.generate(decorator.getWorld(), decorator.getRand(), pos);
		}
		return false;
	}
}
