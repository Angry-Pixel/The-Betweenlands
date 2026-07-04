package thebetweenlands.common.world.gen.generators;

import java.util.BitSet;
import java.util.EnumSet;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import thebetweenlands.api.world.ExtraChunkInfoTypes;
import thebetweenlands.api.world.biome.BiomeWeights;
import thebetweenlands.api.world.biome.CarvingMasks;
import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerationContext.ChunkHeightmaps;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.world.gen.generators.config.BetweenlandsCavesGeneratorConfiguration;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper.NoiseSampler2D;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper.NoiseSampler3D;
import thebetweenlands.common.world.gen.util.BlockHeightSelectors.BlockHeightSelector;
import thebetweenlands.common.world.gen.util.TriFractalOpenSimplexData;
import thebetweenlands.common.world.gen.warp.BLNoiseInterpolator;
import thebetweenlands.util.FractalOpenSimplexNoise;

public class BetweenlandsCavesGenerator extends EarlyGenerator<BetweenlandsCavesGeneratorConfiguration> {

	public BetweenlandsCavesGenerator(Codec<BetweenlandsCavesGeneratorConfiguration> codec) {
		super(codec);
	}
	
	@Override
	public EnumSet<ExtraChunkInfoTypes> getRequiredExtraInfo(BetweenlandsCavesGeneratorConfiguration config) {
		return EnumSet.of(ExtraChunkInfoTypes.BIOME_WEIGHTS, ExtraChunkInfoTypes.CARVING_MASKS);
	}

	@Override
	public boolean place(EarlyGenerationContext<BetweenlandsCavesGeneratorConfiguration> context) {
		final BetweenlandsCavesGeneratorConfiguration config = context.config();

		// Get chunk access info
		final ChunkAccess access = context.chunkAccess();
		final ChunkPos chunkPos = access.getPos();
		final ChunkHeightmaps chunkHeightmaps = context.chunkHeightmaps();
		final int chunkMinHeight = access.getMinBuildHeight();

		// Default states to replace with
		BlockState defaultTerrainState = context.blockGenerator().defaultTerrainState();

		// Which blocks can we replace?
		final HolderSet<Block> replaceable = config.replaceable();
		
		// Get BiomeWeights and Carving Masks
		final BiomeWeights biomeWeights = context.extraChunkInfo().biomeWeights().get();
		final CarvingMasks carvingMasks = context.extraChunkInfo().carvingMasks().get();
		final CarvingMask airCarvingMask = carvingMasks.airCarvingMask();
		final CarvingMask liquidCarvingMask = carvingMasks.liquidCarvingMask();
		
		
		// Get noise data
		final TriFractalOpenSimplexData noiseCache = config.noiseCache().getNoise(context.worldSeed());
		final FractalOpenSimplexNoise caveNoise = noiseCache.noise1();
		final FractalOpenSimplexNoise surfaceOpeningNoise = noiseCache.noise2();
		final FractalOpenSimplexNoise formNoise = noiseCache.noise3();
		
		// Get generation bounds
		final VerticalAnchor minHeight = config.minNoiseHeight();
		final VerticalAnchor maxHeight = config.maxNoiseHeight();

		// Get min cave height values
		final BlockHeightSelector minCaveHeightSampler = config.minCaveHeight();
		final int minCaveHeightTaperDistance = config.minCaveHeightTaperDistance();
		
		// Get max cave height values
		final BlockHeightSelector maxCaveHeightSampler = config.maxCaveHeight();
		final int maxCaveHeightTaperDistance = config.maxCaveHeightTaperDistance();

		// Get base noise limit
		final double baseNoiseLimit = config.defaultNoiseLimit();
		
		// Cave water height
		final int caveWaterHeight = config.caveWaterHeight();

		// Get buffer info (for replacing water that is too close to the caves)
		final HolderSet<Block> bufferReplaceable = config.bufferReplaceable();
		final double bufferNoiseLimit = config.bufferNoiseLimit();

		// Get surface opening biome info
		final HolderSet<Biome> biomesWithoutSurfaceOpenings = config.biomesWithoutSurfaceOpenings();
		final double noSurfaceOpeningNoiseOffset = config.noSurfaceOpeningNoiseOffset();
		
		
		// Calculate vertical size of chunk noise field
		WorldGenerationContext worldgenContext = new WorldGenerationContext(context.chunkGenerator(), access);
		final int noiseMin = minHeight.resolveY(worldgenContext);
		final int noiseMax = maxHeight.resolveY(worldgenContext);
		final int fieldHeight = noiseMax - noiseMin;
		
		// Calculate noise values
		final double[] surfaceOpeningNoiseField = sampleSurfaceOpeningNoiseField(chunkPos, config, surfaceOpeningNoise);
		final BLNoiseInterpolator noiseInterpolator = createNoiseInterpolator(chunkPos, config, caveNoise, formNoise, fieldHeight, noiseMin);
		
		// Buffer for setting buffer placed blocks
		final BitSet bufferPlacedBlocksMask = new BitSet(16 * 16 * fieldHeight);

		noiseInterpolator.initialiseFirstX();
		
		for (int x = 0; x < 8; x++) {
			noiseInterpolator.advanceX(x);

			for (int z = 0; z < 8; z++) {
				// Don't recalculate min/max heights for every single y value of every single column
				int[] caveMinHeights = new int[2 * 2];
				int[] caveMaxHeights = new int[2 * 2];
				// Don't recalculate surface opening offset for every single y value of every single column
				double[] nearSurfaceNoiseOffsets = new double[2 * 2];
				for(int xo = 0; xo < 2; ++xo) {
					int bx = x * 2 + xo;
					for(int zo = 0; zo < 2; ++zo) {
						int bz = z * 2 + zo;
						
						// Get cave bottom in this column
						int minCaveHeight = minCaveHeightSampler.getHeightWG(bx, bz, chunkPos, chunkHeightmaps);

						caveMinHeights[xo * 2 + zo] = minCaveHeight;
						
						// Get height of surface in this column
						int surfaceLevel = maxCaveHeightSampler.getHeightWG(bx, bz, chunkPos, chunkHeightmaps) + 1;
						
						caveMaxHeights[xo * 2 + zo] = surfaceLevel;
						
						// Get surface opening offset in this column
						double nearSurfaceNoiseOffset = biomesWithoutSurfaceOpenings.contains(biomeWeights.getBiome(bx, bz)) ? noSurfaceOpeningNoiseOffset : (1 - biomeWeights.get(bx, bz)) * noSurfaceOpeningNoiseOffset;
						
						nearSurfaceNoiseOffsets[xo * 2 + zo] = nearSurfaceNoiseOffset;
					}
				}
				
				for (int y = 0; y < fieldHeight; y++) {
					noiseInterpolator.selectYZ(y, z);
					noiseInterpolator.updateY(1.0);

					//Step X axis
					for (int xo = 0; xo < 2; xo++) {
						noiseInterpolator.updateX((double)xo / 2.0);

						int bx = x * 2 + xo;

						//Step Z axis
						for (int zo = 0; zo < 2; zo++) {
							double noise = noiseInterpolator.updateZ((double)zo / 2.0);

							int bz = z * 2 + zo;

							int minCaveHeight = caveMinHeights[xo * 2 + zo];
							int surfaceLevel = caveMaxHeights[xo * 2 + zo];

							// This would be "Step Y axis", but the step is always zero
							int yo = 0;

							int by = y + yo;
							
							double limit = baseNoiseLimit;
							int bottomDist = by - minCaveHeight;
							if (bottomDist <= minCaveHeightTaperDistance) {
								limit = (limit + 1) / minCaveHeightTaperDistance * bottomDist - 1;
							}
							int surfaceDist = surfaceLevel - by;
							if (surfaceDist <= maxCaveHeightTaperDistance) {
								final double nearSurfaceNoiseOffset = nearSurfaceNoiseOffsets[xo * 2 + zo];
								final double surfaceOpeningNoiseValue = surfaceOpeningNoiseField[bx * 16 + bz];
								noise += (nearSurfaceNoiseOffset + surfaceOpeningNoiseValue) * (1 - surfaceDist / (float) maxCaveHeightTaperDistance);
							}

							BlockPos blockPos = new BlockPos(bx, by, bz);
							BlockState state = access.getBlockState(blockPos);
							
							if (noise < limit + bufferNoiseLimit && noise > limit && state.is(bufferReplaceable)) {
								final int maskBitIndex = (bx & 15) | (bz & 15) << 4 | (by) << 8;
								bufferPlacedBlocksMask.set(maskBitIndex);
								// Update heightmaps (even though we haven't actually placed the block yet)
								chunkHeightmaps.update(bx, by, bz, defaultTerrainState);
								// Update min/max heights for this column
								caveMinHeights[xo * 2 + zo] = minCaveHeightSampler.getHeightWG(bx, bz, chunkPos, chunkHeightmaps);
								caveMaxHeights[xo * 2 + zo] = maxCaveHeightSampler.getHeightWG(bx, bz, chunkPos, chunkHeightmaps) + 1;
							} else if (noise < limit && state.is(replaceable)) {
								final int my = by + chunkMinHeight;
								airCarvingMask.set(bx, my, bz);
								if(by <= caveWaterHeight) {
									liquidCarvingMask.set(bx, my, bz);
								}
							}
						}
					}
				}
			}
			
			noiseInterpolator.swapSlices();
		}

		int minSection = access.getSectionIndex(noiseMin);
		int maxSection = access.getSectionIndex(noiseMax);
		
		// Set all buffer blocks
		// Note: separated from main code because ChunkAccess.setBlockState() will deadlock
		for(int sectionIndex = minSection; sectionIndex <= maxSection; ++sectionIndex) {
			LevelChunkSection section = access.getSection(sectionIndex);

			int sectionMinY = SectionPos.sectionToBlockCoord(sectionIndex);
			
			for(int y = 0; y < SectionPos.SECTION_SIZE; ++y) {
				for(int x = 0; x < SectionPos.SECTION_SIZE; ++x) {
					for(int z = 0; z < SectionPos.SECTION_SIZE; ++z) {
						final int bitIndex = (x & 15) | (z & 15) << 4 | (sectionMinY + y + noiseMin) << 8;
						
						if(bufferPlacedBlocksMask.get(bitIndex)) {
							section.setBlockState(x, y, z, defaultTerrainState, false);
						}
					}
				}
			}
		}
		
		return true;
	}
	
	protected BLNoiseInterpolator createNoiseInterpolator(ChunkPos chunkPos, BetweenlandsCavesGeneratorConfiguration config, FractalOpenSimplexNoise caveNoise, FractalOpenSimplexNoise formNoise, int fieldHeight, int noiseMin) {
		NoiseSampler3D caveNoiseSampler = EarlyGeneratorHelper.createConfiguredSampler3D(caveNoise, config.caveNoiseSettings());
		NoiseSampler3D formNoiseSampler = EarlyGeneratorHelper.createConfiguredSampler3D(formNoise, config.formNoiseSettings());
		
		return new BLNoiseInterpolator(8, fieldHeight, 8, chunkPos, noiseMin,
				(column, cellX, cellZ, minYOffset, columnSize) -> this.sampleNoiseColumn(column, cellX, cellZ, minYOffset, columnSize, caveNoiseSampler, formNoiseSampler));
	}
	
	protected double[] sampleNoiseColumn(double[] column, int cellX, int cellZ, int minYOffset, int columnSize, NoiseSampler3D caveNoiseSampler, NoiseSampler3D formNoiseSampler) {
		for (int y = 0; y < columnSize; y++) {
			int bx = cellX * 2;
			int bz = cellZ * 2;
			int by = y;
			column[y] = caveNoiseSampler.eval(bx, by, bz) + formNoiseSampler.eval(bx, by, bz);
		}
		return column;
	}

	protected double[] sampleSurfaceOpeningNoiseField(ChunkPos chunkPos, BetweenlandsCavesGeneratorConfiguration config, FractalOpenSimplexNoise surfaceOpeningNoise) {
		final double[] surfaceOpeningNoiseField = new double[16 * 16];
		final int cx = chunkPos.x * 16;
		final int cz = chunkPos.z * 16;

		NoiseSampler2D surfaceOpeningNoiseSampler = EarlyGeneratorHelper.createConfiguredSampler2DNormalized(surfaceOpeningNoise, config.surfaceOpeningNoiseSettings());

		//Generate sea break noise field
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				surfaceOpeningNoiseField[x * 16 + z] = surfaceOpeningNoiseSampler.eval(cx + x, cz + z);
			}
		}
		
		return surfaceOpeningNoiseField;
	}
}
