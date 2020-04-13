package thebetweenlands.common.world.gen.feature;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeWeights;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.OpenSimplexNoise;

public class MapGenCavesBetweenlands extends MapGenBase {
	private static final int CHUNK_SIZE = 16;

	private static final double XZ_CAVE_SCALE = 0.08;

	private static final double Y_CAVE_SCALE = 0.15;

	private static final double XZ_FORM_SCALE = 0.5;

	private static final double Y_FORM_SCALE = 0.3;

	private static final double FORM_SCALE = 0.4;

	private static final double XZ_BREAK_SCALE = 0.05;

	private static final double BREAK_SCALE = 0.85;

	private static final double BASE_LIMIT = -0.3;

	private static final int LOWER_BOUND = 10;

	private static final int UPPER_BOUND = 20;

	private static final double SHOULDNT_BREAK = 3.5;

	private static final double RIDGE_EXTENTS = 0.5;

	private final OpenSimplexNoise cave;

	private final OpenSimplexNoise seaLevelBreak;

	private final FractalOpenSimplexNoise form;

	private static final Set<BiomeBetweenlands> noBreakBiomes
			= ImmutableSet.of(
				BiomeRegistry.DEEP_WATERS,
				BiomeRegistry.COARSE_ISLANDS,
				BiomeRegistry.RAISED_ISLES,
				BiomeRegistry.MARSH_0,
				BiomeRegistry.MARSH_1,
				BiomeRegistry.PATCHY_ISLANDS,
				BiomeRegistry.SLUDGE_PLAINS,
				BiomeRegistry.SWAMPLANDS_CLEARING,
				BiomeRegistry.SLUDGE_PLAINS_CLEARING);

	private BiomeWeights biomeWeights;

	private final double[] noiseField = new double[9 * 9 * 129];
	private final double[] seaBreakNoiseField = new double[16 * 16];

	public MapGenCavesBetweenlands(long seed) {
		cave = new OpenSimplexNoise(seed);
		seaLevelBreak = new OpenSimplexNoise(seed + 1);
		form = new FractalOpenSimplexNoise(seed + 2, 4, 0.1);
	}

	public void setBiomeTerrainWeights(BiomeWeights biomeWeights) {
		this.biomeWeights = biomeWeights;
	}

	@Override
	public void generate(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
		int cx = chunkX * CHUNK_SIZE;
		int cz = chunkZ * CHUNK_SIZE;
		MutableBlockPos pos = new MutableBlockPos();

		//Generate cave noise field (9x9x129)
		for(int x = 0; x < 9; x++) {
			for(int z = 0; z < 9; z++) {
				for(int y = 0; y < 129; y++) {
					int index = ((x * 9) + z) * 129 + y;
					int bx = cx + x * 2;
					int bz = cz + z * 2;
					int by = y;
					this.noiseField[index] = this.sampleNoise(bx, by, bz);
				}
			}
		}

		//Generate sea break noise field
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				this.seaBreakNoiseField[x * 16 + z] = seaLevelBreak.eval((cx+x) * XZ_BREAK_SCALE, (cz+z) * XZ_BREAK_SCALE);
			}
		}

		for(int x = 0; x < 8; x++) {
			int indexXC = x * 9; //1
			int indexXN = (x + 1) * 9; //2

			for(int z = 0; z < 8; z++) {
				int indexXCZC = (indexXC + z) * 129; //1
				int indexXCZN = (indexXC + z + 1) * 129; //2
				int indexXNZC = (indexXN + z) * 129; //3
				int indexXNZN = (indexXN + z + 1) * 129; //4

				for(int y = 0; y < 128; y++) {
					//Values
					double valXCZCYC = this.noiseField[indexXCZC + y]; //1
					double valXCZNYC = this.noiseField[indexXCZN + y]; //2
					double valXNZCYC = this.noiseField[indexXNZC + y]; //3
					double valXNZNYC = this.noiseField[indexXNZN + y]; //4
					double valXCZCYN = this.noiseField[indexXCZC + y + 1]; //5
					double valXCZNYN = this.noiseField[indexXCZN + y + 1]; //6
					double valXNZCYN = this.noiseField[indexXNZC + y + 1]; //7
					double valXNZNYN = this.noiseField[indexXNZN + y + 1]; //8

					//Step along X axis
					double stepXAxisYCZC = (valXNZCYC - valXCZCYC) * 0.5D;
					double stepXAxisYCZN = (valXNZNYC - valXCZNYC) * 0.5D;
					double stepXAxisYNZC = (valXNZCYN - valXCZCYN) * 0.5D;
					double stepXAxisYNZN = (valXNZNYN - valXCZNYN) * 0.5D;

					double currentValXCZCYC = valXCZCYC;
					double currentValXCZNYC = valXCZNYC;
					double currentValXCZCYN = valXCZCYN;
					double currentValXCZNYN = valXCZNYN;

					//Step X axis
					for (int xo = 0; xo < 2; xo++) {
						double currentValYCZC = currentValXCZCYC;
						double currentValYNZC = currentValXCZCYN;

						//Step along Z axis
						double stepZAxisYC = (currentValXCZNYC - currentValXCZCYC) * 0.5D;
						double stepZAxisYN = (currentValXCZNYN - currentValXCZCYN) * 0.5D;

						int bx = x * 2 + xo;

						//Step Z axis
						for (int zo = 0; zo < 2; zo++) {
							//Step along Y axis
							double stepYAxis = (currentValYNZC - currentValYCZC) * 0.5D;

							double currentValYC = currentValYNZC - stepYAxis;

							int bz = z * 2 + zo;

							Biome biome = world.getBiome(pos.setPos(cx + bx, 0, cz + bz));

							// Only break in correct biomes and don't generate in biome transitions
							double shouldntBreak = noBreakBiomes.contains(biome) ? SHOULDNT_BREAK : (1 - this.biomeWeights.get(bx, bz)) * SHOULDNT_BREAK;

							int level = 0;

							if(biome instanceof BiomeBetweenlands) {
								level = (int) (biome.getBaseHeight() - biome.getHeightVariation());
								while (level <= (int) (biome.getBaseHeight() + biome.getHeightVariation())) {
									IBlockState state = primer.getBlockState(bx, level, bz);
									if (state.getMaterial() == Material.AIR || state.getMaterial().isLiquid()) {
										break;
									}
									level++;
								}
							} else {
								while (level <= WorldProviderBetweenlands.LAYER_HEIGHT + 20) {
									IBlockState state = primer.getBlockState(bx, level, bz);
									if (state.getMaterial() == Material.AIR || state.getMaterial().isLiquid()) {
										break;
									}
									level++;
								}
							}

							//Step Y axis
							for (int yo = 0; yo < 1; yo++) {
								double noise = currentValYC += stepYAxis;

								int by = y + yo;

								double limit = BASE_LIMIT;
								if (by <= LOWER_BOUND) {
									limit = (limit + 1) / LOWER_BOUND * by - 1;
								}
								int surfaceDist = level - by;
								if (surfaceDist <= UPPER_BOUND) {
									noise += (shouldntBreak + MathUtils.linearTransformd(this.seaBreakNoiseField[bx * 16 + bz], -1, 1, 0, 1)) * BREAK_SCALE * (1 - surfaceDist / (float) UPPER_BOUND);
								}
								//if (y == level) {   
								//    primer.setBlockState(bx, 150, bz, Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, EnumDyeColor.byMetadata((int) MathUtils.linearTransformd(noise, -0.5F, 1, 0, 15))));
								//}
								IBlockState state = primer.getBlockState(bx, by, bz);
								if(state.getBlock() == BlockRegistry.SWAMP_WATER && noise < limit + 0.25 && noise > limit) {
									primer.setBlockState(bx, by, bz, BlockRegistry.COARSE_SWAMP_DIRT.getDefaultState());
								} else if (noise < limit && state.getBlock() != BlockRegistry.BETWEENLANDS_BEDROCK) {
									primer.setBlockState(bx, by, bz, by > WorldProviderBetweenlands.CAVE_WATER_HEIGHT ? Blocks.AIR.getDefaultState() : BlockRegistry.SWAMP_WATER.getDefaultState());
								}
							}

							currentValYCZC += stepZAxisYC;
							currentValYNZC += stepZAxisYN;
						}

						currentValXCZCYC += stepXAxisYCZC;
						currentValXCZNYC += stepXAxisYCZN;
						currentValXCZCYN += stepXAxisYNZC;
						currentValXCZNYN += stepXAxisYNZN;
					}
				}
			}
		}
	}

	private double sampleNoise(int x, int y, int z) {
		return this.cave.eval(x * XZ_CAVE_SCALE, y * Y_CAVE_SCALE, z * XZ_CAVE_SCALE) + form.eval(x * XZ_FORM_SCALE, y * Y_FORM_SCALE, z * XZ_FORM_SCALE) * FORM_SCALE;
	}
}
