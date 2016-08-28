package thebetweenlands.common.world.gen.biome.decorator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thebetweenlands.common.block.plant.BlockDoublePlantBL;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.gen.feature.WorldGenCaveGrass;
import thebetweenlands.common.world.gen.feature.WorldGenCaveMoss;
import thebetweenlands.common.world.gen.feature.WorldGenCavePots;
import thebetweenlands.common.world.gen.feature.WorldGenCaveThorns;
import thebetweenlands.common.world.gen.feature.WorldGenSpeleothem;
import thebetweenlands.util.CubicBezier;

public class DecorationHelper {
	private final static WorldGenerator GEN_SPELEOTHEM = new WorldGenSpeleothem();
	private final static WorldGenerator GEN_CAVE_POTS = new WorldGenCavePots();
	private final static WorldGenerator GEN_CAVE_THORNS = new WorldGenCaveThorns();
	private final static WorldGenerator GEN_CAVE_MOSS = new WorldGenCaveMoss();
	private final static WorldGenerator GEN_CAVE_GRASS = new WorldGenCaveGrass();

	private static final CubicBezier SPELEOTHEM_Y_CDF = new CubicBezier(0, 0.5F, 1, 0.2F);
	private static final CubicBezier CAVE_POTS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier THORNS_Y_CDF = new CubicBezier(1, 0.5F, 1, -0.25F);
	private static final CubicBezier CAVE_MOSS_Y_CDF = new CubicBezier(0, 1, 0, 1);
	private static final CubicBezier CAVE_GRASS_Y_CDF = new CubicBezier(0, 1, 0, 1);

	/**
	 * Generates a phragmite plant
	 * @param decorator
	 * @return
	 */
	public static boolean generatePhragmites(BiomeDecoratorBetweenlands decorator) {
		World world = decorator.getWorld();
		BlockPos pos = decorator.getRandomPos(0);
		if (BlockRegistry.PHRAGMITES.canBlockStay(world, pos, world.getBlockState(pos)) && world.isAirBlock(pos) && world.isAirBlock(pos.up())) {
			IBlockState state = BlockRegistry.PHRAGMITES.getDefaultState();
			world.setBlockState(pos, state.withProperty(BlockDoublePlantBL.HALF, BlockDoublePlantBL.EnumBlockHalf.LOWER));
			world.setBlockState(pos.up(), state.withProperty(BlockDoublePlantBL.HALF, BlockDoublePlantBL.EnumBlockHalf.UPPER));
			return true;
		}
		return false;
	}

	/**
	 * Populates the cave with several plants, speleothems and cave pots
	 * @param decorator
	 * @return
	 */
	public static boolean populateCaves(BiomeDecoratorBetweenlands decorator) {
		decorator.generate(60, DecorationHelper::generateSpeleothem);
		decorator.generate(decorator.getRand().nextInt(3) == 0 ? 2 : 1, DecorationHelper::generateCavePots);
		decorator.generate(200, DecorationHelper::generateCaveThorns);
		decorator.generate(100, DecorationHelper::generateCaveMoss);
		decorator.generate(120, DecorationHelper::generateCaveGrass);
		return true;
	}

	/**
	 * Generates a speleothem
	 * @param decorator
	 * @return
	 */
	public static boolean generateSpeleothem(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = SPELEOTHEM_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_SPELEOTHEM.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	/**
	 * Generates a cave pot cluster
	 * @param decorator
	 * @return
	 */
	public static boolean generateCavePots(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_POTS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.CAVE_START - 5.0F - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_POTS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	/**
	 * Generates a cave thorns cluster
	 * @param decorator
	 * @return
	 */
	public static boolean generateCaveThorns(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = THORNS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_THORNS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	/**
	 * Generates a cave moss cluster
	 * @param decorator
	 * @return
	 */
	public static boolean generateCaveMoss(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_MOSS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.LAYER_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_MOSS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}

	/**
	 * Generates a cave grass cluster
	 * @param decorator
	 * @return
	 */
	public static boolean generateCaveGrass(BiomeDecoratorBetweenlands decorator) {
		int x = decorator.getRandomPosX();
		float v = CAVE_GRASS_Y_CDF.eval(decorator.getRand().nextFloat());
		int y = (int) (v * (WorldProviderBetweenlands.PITSTONE_HEIGHT - WorldProviderBetweenlands.CAVE_WATER_HEIGHT) + WorldProviderBetweenlands.CAVE_WATER_HEIGHT + 0.5F);
		int z = decorator.getRandomPosZ();
		return GEN_CAVE_GRASS.generate(decorator.getWorld(), decorator.getRand(), new BlockPos(x, y, z));
	}
}
