package thebetweenlands.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.CaveCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.FractalOpenSimplexNoise;
import thebetweenlands.util.MathUtils;
import thebetweenlands.util.OpenSimplexNoise;

import java.util.function.Function;

public class BLCaveCarver extends WorldCarver<CaveCarverConfiguration> {

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

	private final OpenSimplexNoise cave;
	private final OpenSimplexNoise seaLevelBreak;
	private final FractalOpenSimplexNoise form;

	private final double[] noiseField = new double[9 * 9 * 129];
	private final double[] seaBreakNoiseField = new double[16 * 16];

	public BLCaveCarver(Codec<CaveCarverConfiguration> codec) {
		super(codec);
		this.cave = new OpenSimplexNoise(0);
		this.seaLevelBreak = new OpenSimplexNoise(1);
		this.form = new FractalOpenSimplexNoise(2, 4, 0.1);
	}

	@Override
	public boolean carve(CarvingContext context, CaveCarverConfiguration config, ChunkAccess access, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask mask) {

		// only generate in this chunk check
		if (chunkPos.equals(access.getPos())) {
			int cx = access.getPos().x * CHUNK_SIZE;
			int cz = access.getPos().z * CHUNK_SIZE;

			//Generate cave noise field (9x9x129)
			for (int x = 0; x < 9; x++) {
				for (int z = 0; z < 9; z++) {
					for (int y = 0; y < 129; y++) {
						int index = ((x * 9) + z) * 129 + y;
						int bx = cx + x * 2;
						int bz = cz + z * 2;
						this.noiseField[index] = this.sampleNoise(bx, y, bz);
					}
				}
			}

			//Generate sea break noise field
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					this.seaBreakNoiseField[x * 16 + z] = seaLevelBreak.eval((cx + x) * XZ_BREAK_SCALE, (cz + z) * XZ_BREAK_SCALE);
				}
			}

			for (int x = 0; x < 8; x++) {
				int indexXC = x * 9; //1
				int indexXN = (x + 1) * 9; //2

				for (int z = 0; z < 8; z++) {
					int indexXCZC = (indexXC + z) * 129; //1
					int indexXCZN = (indexXC + z + 1) * 129; //2
					int indexXNZC = (indexXN + z) * 129; //3
					int indexXNZN = (indexXN + z + 1) * 129; //4

					for (int y = 0; y < 128; y++) {
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

								int level = 0;
								while (level <= TheBetweenlands.LAYER_HEIGHT + 20) {
									BlockState state = access.getBlockState(new BlockPos(bx, level, bz));
									@SuppressWarnings("deprecation")
									boolean occupied = state.isAir() || state.liquid() || !state.getFluidState().isEmpty();
									if (occupied) {
										break;
									}
									level++;
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
										noise += (MathUtils.linearTransformd(this.seaBreakNoiseField[bx * 16 + bz], -1, 1, 0, 1)) * BREAK_SCALE * (1 - surfaceDist / (float) UPPER_BOUND);
									}

									BlockPos blockPos = new BlockPos(bx, by, bz);
									BlockState state = access.getBlockState(blockPos);
									if (state.is(BlockRegistry.SWAMP_WATER.get()) && noise < limit + 0.25 && noise > limit) {
										access.setBlockState(blockPos, BlockRegistry.SWAMP_DIRT.get().defaultBlockState(), false);//COARSE_SWAMP_DIRT.getDefaultState());
									} else if (noise < limit && state.getBlock() != BlockRegistry.BETWEENLANDS_BEDROCK.get()) {
										access.setBlockState(blockPos, by > TheBetweenlands.CAVE_WATER_HEIGHT ? Blocks.AIR.defaultBlockState() : BlockRegistry.SWAMP_WATER.get().defaultBlockState(), false);
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

		return true;
	}

	private double sampleNoise(int x, int y, int z) {
		return this.cave.eval(x * XZ_CAVE_SCALE, y * Y_CAVE_SCALE, z * XZ_CAVE_SCALE) + form.eval(x * XZ_FORM_SCALE, y * Y_FORM_SCALE, z * XZ_FORM_SCALE) * FORM_SCALE;
	}

	// Generate once every 10 chunks?
	// Every chunk seems to generate a 5 by 5 radius around it including the same carver
	@Override
	public boolean isStartChunk(CaveCarverConfiguration config, RandomSource random) {
		return true;
	}
}
