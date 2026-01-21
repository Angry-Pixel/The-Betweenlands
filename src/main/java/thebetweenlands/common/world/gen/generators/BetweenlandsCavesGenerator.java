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
		final int chunkHeight = access.getHeight();
		final int chunkMinHeight = access.getMinBuildHeight();

		// Default states to replace with
		BlockState defaultTerrainState = context.blockGenerator().defaultTerrainState();
//		BlockState defaultLiquidState  = context.blockGenerator().defaultLiquidState();

		// Which blocks can we replace?
		final HolderSet<Block> replaceable = config.replaceable();
		
		// Get BiomeWeights and Carving Masks
		final BiomeWeights biomeWeights = context.extraChunkInfo().biomeWeights().get();
		final CarvingMasks carvingMasks = context.extraChunkInfo().carvingMasks().get();
		final CarvingMask airCarvingMask = carvingMasks.airCarvingMask();
		final CarvingMask liquidCarvingMask = carvingMasks.liquidCarvingMask();
		
		// Calculate vertical size of chunk noise field
		final int fieldHeight = chunkHeight >> 1;
		final int noiseHeight = fieldHeight + 1;
		
		// Get noise data
		final TriFractalOpenSimplexData noiseCache = config.noiseCache().getNoise(context.worldSeed());
		final FractalOpenSimplexNoise caveNoise = noiseCache.noise1();
		final FractalOpenSimplexNoise surfaceOpeningNoise = noiseCache.noise2();
		final FractalOpenSimplexNoise formNoise = noiseCache.noise3();
		
		// Calculate noise values
		final double[] noiseField = sampleNoiseField(chunkPos, config, caveNoise, formNoise, fieldHeight);
		final double[] surfaceOpeningNoiseField = sampleSurfaceOpeningNoiseField(chunkPos, config, surfaceOpeningNoise);

		// Get min cave height values
		final BlockHeightSelector minCaveHeightSampler = config.minCaveHeight();
		final int minCaveHeightTaperDistance = config.minCaveHeightTaperDistance();
		
		// Get max cave height values
		final BlockHeightSelector maxCaveHeightSampler = config.maxCaveHeight();
		final int maxCaveHeightTaperDistance = config.maxCaveHeightTaperDistance();
		
		// Get base limit
		final double baseNoiseLimit = config.defaultNoiseLimit();
		
		// Cave water height
		final int caveWaterHeight = config.caveWaterHeight();
		
		// Get buffer info (for replacing water that is too close to the caves)
		final HolderSet<Block> bufferReplaceable = config.bufferReplaceable();
		final double bufferNoiseLimit = config.bufferNoiseLimit();
		
		// Get surface opening biome info
		final HolderSet<Biome> biomesWithoutSurfaceOpenings = config.biomesWithoutSurfaceOpenings();
		final double noSurfaceOpeningNoiseOffset = config.noSurfaceOpeningNoiseOffset();

		final BitSet bufferPlacedBlocksMask = new BitSet(16 * 16 * chunkHeight);
//		final BitSet carvedLiquidBlocksMask = new BitSet(16 * 16 * chunkHeight);
//		final BitSet carvedTerrainBlocksMask = new BitSet(16 * 16 * chunkHeight);
		
		for (int x = 0; x < 8; x++) {
			int indexXC = x * 9; //1
			int indexXN = (x + 1) * 9; //2

			for (int z = 0; z < 8; z++) {
				int indexXCZC = (indexXC + z) * noiseHeight; //1
				int indexXCZN = (indexXC + z + 1) * noiseHeight; //2
				int indexXNZC = (indexXN + z) * noiseHeight; //3
				int indexXNZN = (indexXN + z + 1) * noiseHeight; //4

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
						int surfaceLevel = maxCaveHeightSampler.getHeightWG(bx, bz, chunkPos, chunkHeightmaps);
						
						caveMaxHeights[xo * 2 + zo] = surfaceLevel;
						
						// Get surface opening offset in this column
						double nearSurfaceNoiseOffset = biomesWithoutSurfaceOpenings.contains(biomeWeights.getBiome(bx, bz)) ? noSurfaceOpeningNoiseOffset : (1 - biomeWeights.get(bx, bz)) * noSurfaceOpeningNoiseOffset;
						
						nearSurfaceNoiseOffsets[xo * 2 + zo] = nearSurfaceNoiseOffset;
					}
				}
				
				for (int y = 0; y < fieldHeight; y++) {
					//Values
					double valXCZCYC = noiseField[indexXCZC + y]; //1
					double valXCZNYC = noiseField[indexXCZN + y]; //2
					double valXNZCYC = noiseField[indexXNZC + y]; //3
					double valXNZNYC = noiseField[indexXNZN + y]; //4
					double valXCZCYN = noiseField[indexXCZC + y + 1]; //5
					double valXCZNYN = noiseField[indexXCZN + y + 1]; //6
					double valXNZCYN = noiseField[indexXNZC + y + 1]; //7
					double valXNZNYN = noiseField[indexXNZN + y + 1]; //8

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

							int minCaveHeight = caveMinHeights[xo * 2 + zo];
							int surfaceLevel = caveMaxHeights[xo * 2 + zo];

							//Step Y axis
							for (int yo = 0; yo < 1; yo++) { // yo < 1? should this be yo <= 1 or yo < 2? it only runs one time currently
								double noise = currentValYC += stepYAxis;

								int by = y + yo;

								final int maskBitIndex = (bx & 15) | (bz & 15) << 4 | (by) << 8;
								
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
									bufferPlacedBlocksMask.set(maskBitIndex);
									// Update heightmaps (even though we haven't actually placed the block yet)
									chunkHeightmaps.update(bx, by, bz, defaultTerrainState);
									// Update min/max heights for this column
									caveMinHeights[xo * 2 + zo] = minCaveHeight = minCaveHeightSampler.getHeightWG(bx, bz, chunkPos, chunkHeightmaps);
									caveMaxHeights[xo * 2 + zo] = surfaceLevel = maxCaveHeightSampler.getHeightWG(bx, bz, chunkPos, chunkHeightmaps);
								} else if (noise < limit && state.is(replaceable)) {
									if(by <= caveWaterHeight) {
										liquidCarvingMask.set(bx, by + chunkMinHeight, bz);
//										carvedLiquidBlocksMask.set(maskBitIndex);
									} else {
										airCarvingMask.set(bx, by + chunkMinHeight, bz);
//										carvedTerrainBlocksMask.set(maskBitIndex);
									}
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

		// Set all buffer blocks
		// Note: separated from main code because ChunkAccess.setBlockState() will deadlock
		for(int sectionIndex = 0; sectionIndex < access.getSectionsCount(); ++sectionIndex) {
			LevelChunkSection section = access.getSection(sectionIndex);

//			int sectionMinY = SectionPos.sectionToBlockCoord(access.getSectionYFromSectionIndex(sectionIndex));
			int sectionMinY = SectionPos.sectionToBlockCoord(sectionIndex);
			
			for(int y = 0; y < SectionPos.SECTION_SIZE; ++y) {
				for(int x = 0; x < SectionPos.SECTION_SIZE; ++x) {
					for(int z = 0; z < SectionPos.SECTION_SIZE; ++z) {
						final int bitIndex = (x & 15) | (z & 15) << 4 | (sectionMinY + y) << 8;
						
//						if(carvedLiquidBlocksMask.get(bitIndex)) {
//							section.setBlockState(x, y, z, defaultLiquidState, false);
//						} else if(carvedTerrainBlocksMask.get(bitIndex)) {
//							section.setBlockState(x, y, z, Blocks.CAVE_AIR.defaultBlockState(), false);
//						} else
						if(bufferPlacedBlocksMask.get(bitIndex)) {
							section.setBlockState(x, y, z, defaultTerrainState, false);
						}
					}
				}
			}
		}
		
		return true;
	}
	
	protected double[] sampleNoiseField(ChunkPos chunkPos, BetweenlandsCavesGeneratorConfiguration config, FractalOpenSimplexNoise caveNoise, FractalOpenSimplexNoise formNoise, int fieldHeight) {
		final int noiseHeight = fieldHeight + 1;
		final double[] noiseField = new double[9 * 9 * noiseHeight];

		final int cx = chunkPos.x * 16;
		final int cz = chunkPos.z * 16;

		NoiseSampler3D caveNoiseSampler = EarlyGeneratorHelper.createConfiguredSampler3D(caveNoise, config.caveNoiseSettings());
		NoiseSampler3D formNoiseSampler = EarlyGeneratorHelper.createConfiguredSampler3D(formNoise, config.formNoiseSettings());
		
//		boolean shouldLog = chunkPos.x == -34 && chunkPos.z == 34;
		
		//Generate cave noise field (9 x 9 x noiseHeight)
		for (int x = 0; x < 9; x++) {
			for (int z = 0; z < 9; z++) {
				for (int y = 0; y < noiseHeight; y++) {
					int index = ((x * 9) + z) * noiseHeight + y;
					int bx = cx + x * 2;
					int bz = cz + z * 2;
//					if(shouldLog) {
//						TheBetweenlands.LOGGER.info("Cave Noise at {}, {}, {}: {}", bx, y, bz, caveNoiseSampler.eval(bx, y, bz));
//						TheBetweenlands.LOGGER.info("Form Noise at {}, {}, {}: {}", bx, y, bz, formNoiseSampler.eval(bx, y, bz));
//					}
					noiseField[index] = caveNoiseSampler.eval(bx, y, bz) + formNoiseSampler.eval(bx, y, bz);
				}
			}
		}
		
		return noiseField;
	}

	protected double[] sampleSurfaceOpeningNoiseField(ChunkPos chunkPos, BetweenlandsCavesGeneratorConfiguration config, FractalOpenSimplexNoise surfaceOpeningNoise) {
		final double[] surfaceOpeningNoiseField = new double[16 * 16];
		final int cx = chunkPos.x * 16;
		final int cz = chunkPos.z * 16;

		NoiseSampler2D surfaceOpeningNoiseSampler = EarlyGeneratorHelper.createConfiguredSampler2DNormalized(surfaceOpeningNoise, config.surfaceOpeningNoiseSettings());

//		boolean shouldLog = chunkPos.x == -34 && chunkPos.z == 34;
		
		//Generate sea break noise field
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
//				if(shouldLog) {
//					TheBetweenlands.LOGGER.info("Weighted Opening noise at {}, {}: {}", (cx+x), (cz+z), surfaceOpeningNoiseSampler.eval(cx + x, cz + z));
//				}
				surfaceOpeningNoiseField[x * 16 + z] = surfaceOpeningNoiseSampler.eval(cx + x, cz + z);
			}
		}
		
		return surfaceOpeningNoiseField;
	}
}
