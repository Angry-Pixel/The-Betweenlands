package thebetweenlands.world.gen;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.utils.MathUtils;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

import com.google.common.collect.Lists;

public class MapGenCavesBetweenlands extends MapGenBase {
	private static final int CHUNK_SIZE = 16;

	private static final int Y_STRIDE = CHUNK_SIZE * CHUNK_SIZE;

	private static final double XZ_CAVE_SCALE = 0.08;

	private static final double Y_CAVE_SCALE = 0.15;

	private static final double XZ_FORM_SCALE = 0.5;

	private static final double Y_FORM_SCALE = 0.3;

	private static final double FORM_SCALE = 0.4;

	private static final double XZ_BREAK_SCALE = 0.05;

	private static final double BREAK_SCALE = 0.5;

	private static final double BASE_LIMIT = -0.3;

	private static final int LOWER_BOUND = 10;

	private static final int UPPER_BOUND = 20;

	private static final double SHOULDNT_BREAK = 2.5;

	private static final double RIDGE_EXTENTS = 0.5;

	private OpenSimplexNoise cave;

	private OpenSimplexNoise seaLevelBreak;

	private FractalOpenSimplexNoise form;

	private List<BiomeGenBaseBetweenlands> noBreakBiomes;

	public MapGenCavesBetweenlands(long seed) {
		cave = new OpenSimplexNoise(seed);
		seaLevelBreak = new OpenSimplexNoise(seed + 1);
		form = new FractalOpenSimplexNoise(seed + 2, 4, 0.1);
		noBreakBiomes = Lists.newArrayList(
			BLBiomeRegistry.deepWater,
			BLBiomeRegistry.coarseIslands,
			BLBiomeRegistry.marsh1,
			BLBiomeRegistry.marsh2,
			BLBiomeRegistry.patchyIslands,
			BLBiomeRegistry.sludgePlains
		);
	}

	@Override
	public void func_151539_a(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, Block[] blocks) {
		int cx = chunkX * CHUNK_SIZE;
		int cz = chunkZ * CHUNK_SIZE;
		for (int bx = 0; bx < CHUNK_SIZE; bx++) {
			for (int bz = 0; bz < CHUNK_SIZE; bz++) {
				int x = cx + bx, z = cz + bz;
				BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
				boolean shouldntBreak = noBreakBiomes.contains(biome);
				int xzIndex = (bx * CHUNK_SIZE + bz) * Y_STRIDE;
				int level = 0;
				while (!(blocks[xzIndex + level] != null && blocks[xzIndex + level++].getMaterial().isLiquid()) && level <= WorldProviderBetweenlands.LAYER_HEIGHT);
				boolean brokeSurface = false;
				for (int y = 0; y <= level; y++) {
					double noise = cave.eval(x * XZ_CAVE_SCALE, y * Y_CAVE_SCALE, z * XZ_CAVE_SCALE) + form.eval(x * XZ_FORM_SCALE, y * Y_FORM_SCALE, z * XZ_FORM_SCALE) * FORM_SCALE;
					double limit = BASE_LIMIT;
					if (y <= LOWER_BOUND) {
						limit = (limit + 1) / LOWER_BOUND * y - 1;
					}
					int surfaceDist = level - y;
					if (surfaceDist <= UPPER_BOUND) {
						noise += (shouldntBreak ? SHOULDNT_BREAK : MathUtils.linearTransformd(seaLevelBreak.eval(x * XZ_BREAK_SCALE, z * XZ_BREAK_SCALE), -1, 1, 0, 1)) * BREAK_SCALE * (1 - surfaceDist / (float) UPPER_BOUND);
					}
					if (noise < limit) {
						if (y == level) {
							brokeSurface = true;
						}
						int index = xzIndex + y;
						if (blocks[index] == null || blocks[index].getMaterial().isLiquid()) {
							continue;
						}
						blocks[index] = y > WorldProviderBetweenlands.WATER_HEIGHT ? Blocks.air : BLBlockRegistry.swampWater;
					} else if (y == level && noise < limit + RIDGE_EXTENTS) {
						double h = MathUtils.linearTransformd(noise, limit, limit + RIDGE_EXTENTS, 0, 1);
						if (h < 0.5) {
							// This is done so that the inside of the ridge doesn't have water
							brokeSurface = true;
						}
						// https://www.desmos.com/calculator/nhbhw6jwk6
						double f1 = MathUtils.linearTransformd(Math.sin(2 * Math.PI * (Math.pow(1 - h, 2) - 0.25)), -1, 1, 0, 1);
						double f2 = Math.sqrt(Math.pow(0.5, 2) - Math.pow(h - 0.5, 2));
						int height = (int) ((f1 + f2) / 1.4576 * 3);
						for (int dy = -1; dy < height; dy++) {
							blocks[xzIndex + y + dy] = dy < height - 1 ? BLBlockRegistry.swampDirt : BLBlockRegistry.swampGrass;
						}
					}
				}
				if (brokeSurface) {
					int newlevel = WorldProviderBetweenlands.LAYER_HEIGHT;
					while (newlevel > 0) {
						if (blocks[xzIndex + newlevel] != null) {
							Material material = blocks[xzIndex + newlevel].getMaterial();
							if (blocks[xzIndex + newlevel] != BLBlockRegistry.algae && material != Material.air && !material.isLiquid()) {
								break;
							}
						}
						newlevel--;
					}
					if (newlevel > 0) {
						for (int y = newlevel; y < WorldProviderBetweenlands.LAYER_HEIGHT + 2; y++) {
							blocks[xzIndex + y] = Blocks.air;
						}
					}
				}
			}
		}
	}
}
