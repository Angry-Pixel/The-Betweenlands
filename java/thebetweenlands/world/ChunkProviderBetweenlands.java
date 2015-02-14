package thebetweenlands.world;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class ChunkProviderBetweenlands implements IChunkProvider {
	//TODO: Everything

	//The world
	private World worldObj;

	//RNG with the world seed as seed
	private Random rand;

	//The base noise for base generation
	private NoiseGeneratorOctaves baseNoiseOctave;

	//The octaves for the base generation
	//Octaves are used for additional features for the base noise
	private NoiseGeneratorOctaves noiseOctave1;
	private NoiseGeneratorOctaves noiseOctave2;
	private NoiseGeneratorOctaves noiseOctave3;
	//5th octave not used
	//private NoiseGeneratorOctaves octave5;

	//Generates a stone noise. At a certain threshold of this noise, the baseBlock will be the top most block and the fillerBlock and topBlock of the Biome are ignored.
	private NoiseGeneratorPerlin stoneNoiseGen;
	//Stone noise at any Y value in the current XZ stack
	private double[] stoneNoise = new double[256];

	//Noise at any Y value in the current XZ stack
	private double[] noiseXZ;

	//Holds the octave noise at the current XZ stack
	double[] noise1;
	double[] noise2;
	double[] noise3;
	double[] baseNoise;

	//Holds the biome height gradient at the current XZ stack
	private float[] parabolicField;

	//Holds the biomes in the current XZ stack and around
	private BiomeGenBase[] biomesForGeneration;

	//Base block. Vanilla: stone
	private final Block baseBlock;
	//Layer block generated below layerHeight. Vanilla: water
	private final Block layerBlock;
	//layerBlock generates below this height
	private final int layerHeight;

	public ChunkProviderBetweenlands(World world, long seed, Block baseBlock, Block layerBlock, int layerHeight) {
		this.worldObj = world;
		this.baseBlock = baseBlock;
		this.layerBlock = layerBlock;
		this.layerHeight = layerHeight;
		
		//Initializes the noise generators
		this.initializeNoiseGen(seed);
	}


	@Override
	public void recreateStructures(int x, int z) { }

	@Override
	@SuppressWarnings("rawtypes")
	public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
		//TODO: Impl
		//BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);
		//return biome == null ? null : biome.getSpawnableList(creatureType);
		return null;
	}

	@Override
	public String makeString() {
		return "";
	}

	@Override
	public boolean chunkExists(int x, int z) {
		return true;
	}

	@Override
	public boolean saveChunks(boolean mode, IProgressUpdate progressUpdate) {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	@Override
	public void saveExtraData() { }

	@Override
	public ChunkPosition func_147416_a(World world, String structureIdentifier, int x, int y, int z) {
		return null;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		return null;
	}

	@Override
	public Chunk loadChunk(int x, int z) {
		return this.provideChunk(x, z);
	}

	@Override
	public void populate(IChunkProvider cp, int x, int z) {

	}

	/**
	 * Initializes the noise generators
	 * @param seed long
	 */
	private void initializeNoiseGen(long seed) {
		//Generate seeded RNG
		this.rand = new Random(seed);

		//Initialize noise octaves
		this.baseNoiseOctave = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseOctave1 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseOctave2 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseOctave3 = new NoiseGeneratorOctaves(this.rand, 8);

		//5th octave not used
		//this.octave5 = new NoiseGeneratorOctaves(this.rand, 10);

		//Generates a stone noise. At a certain threshold of this noise, the baseBlock will be the top most block and the fillerBlock and topBlock of the Biome are ignored.
		//Used in replaceBlocksForBiome
		this.stoneNoiseGen = new NoiseGeneratorPerlin(this.rand, 4);

		//Holds the generated noise XZ stack
		this.noiseXZ = new double[825];

		//Holds the biome height gradient
		this.parabolicField = new float[25];

		//Generate parabolic field
		for (int j = -2; j <= 2; ++j) {
			for (int k = -2; k <= 2; ++k) {
				float f = 10.0F / MathHelper.sqrt_float((float)(j * j + k * k) + 0.2F);
				this.parabolicField[j + 2 + (k + 2) * 5] = f;
			}
		}

		//Allows mods to change the noise gen, not needed for the BL right now
		/*NoiseGenerator[] noiseGens = {field_147431_j, field_147432_k, field_147429_l, field_147430_m, noiseGen5, noiseGen6};
        noiseGens = TerrainGen.getModdedNoiseGenerators(this.worldObj, this.rand, noiseGens);
        this.field_147431_j = (NoiseGeneratorOctaves)noiseGens[0];
        this.field_147432_k = (NoiseGeneratorOctaves)noiseGens[1];
        this.field_147429_l = (NoiseGeneratorOctaves)noiseGens[2];
        this.field_147430_m = (NoiseGeneratorPerlin)noiseGens[3];
        this.noiseGen5 = (NoiseGeneratorOctaves)noiseGens[4];
        this.noiseGen6 = (NoiseGeneratorOctaves)noiseGens[5];*/
	}

	/**
	 * Generates the XZ noise stack
	 * @param noiseArray double[]
	 * @param x int
	 * @param z int
	 */
	private void generateNoiseXZStack(double[] noiseArray, int x, int z) {
		//Generate noise XZ components
		this.baseNoise = this.baseNoiseOctave.generateNoiseOctaves(this.baseNoise, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
		this.noise3 = this.noiseOctave3.generateNoiseOctaves(this.noise3, x, 0, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
		this.noise1 = this.noiseOctave1.generateNoiseOctaves(this.noise1, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		this.noise2 = this.noiseOctave2.generateNoiseOctaves(this.noise2, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);

		int noiseIndex = 0;
		int baseNoiseIndex = 0;

		for (int bxo = 0; bxo < 5; ++bxo) {
			for (int bzo = 0; bzo < 5; ++bzo) {
				float averageHeightVariation = 0.0F;
				float averageRootHeight = 0.0F;
				float averageHeightGradient = 0.0F;
				
				//The current biome
				BiomeGenBase currentBiome = this.biomesForGeneration[bxo + 2 + (bzo + 2) * 10];

				//Gets the biomes in a 5x5 area around the current XZ stack to average the height
				for (int sbbxo = -2; sbbxo <= 2; ++sbbxo) {
					for (int sbbzo = -2; sbbzo <= 2; ++sbbzo) {
						BiomeGenBase surroundingBiome = this.biomesForGeneration[bxo + sbbxo + 2 + (bzo + sbbzo + 2) * 10];

						//No amplified terrain in the BL
						/*if (this.field_147435_p == WorldType.AMPLIFIED && f3 > 0.0F)
                        {
                            f3 = 1.0F + f3 * 2.0F;
                            f4 = 1.0F + f4 * 4.0F;
                        }*/

						float heightGradient = this.parabolicField[sbbxo + 2 + (sbbzo + 2) * 5] / (surroundingBiome.rootHeight + 2.0F);

						//Use shallow gradient if the surrounding biome has a higher root height than the current biome
						if (surroundingBiome.rootHeight > currentBiome.rootHeight) {
							heightGradient /= 2.0F;
						}

						averageHeightVariation += surroundingBiome.heightVariation * heightGradient;
						averageRootHeight += surroundingBiome.rootHeight * heightGradient;
						averageHeightGradient += heightGradient;
					}
				}

				//Calculate average root height and height variation
				averageHeightVariation /= averageHeightGradient;
				averageRootHeight /= averageHeightGradient;
				averageHeightVariation = averageHeightVariation * 0.9F + 0.1F;
				averageRootHeight = (averageRootHeight * 4.0F - 1.0F) / 8.0F;
				
				double fineBaseNoise = this.baseNoise[baseNoiseIndex] / 8000.0D;

				//Calculate fine noise
				if (fineBaseNoise < 0.0D) {
					fineBaseNoise = -fineBaseNoise * 0.3D;
				}
				fineBaseNoise = fineBaseNoise * 3.0D - 2.0D;
				if (fineBaseNoise < 0.0D) {
					fineBaseNoise /= 2.0D;

					if (fineBaseNoise < -1.0D) {
						fineBaseNoise = -1.0D;
					}

					fineBaseNoise /= 1.4D;
					fineBaseNoise /= 2.0D;
				} else {
					if (fineBaseNoise > 1.0D) {
						fineBaseNoise = 1.0D;
					}

					fineBaseNoise /= 8.0D;
				}

				++baseNoiseIndex;
				double cAvgRootHeight = (double)averageRootHeight;
				double cAvgHeightVariation = (double)averageHeightVariation;
				cAvgRootHeight += fineBaseNoise * 0.2D;
				cAvgRootHeight = cAvgRootHeight * 8.5D / 8.0D;
				//double d5 = 8.5D + cAvgRootHeight * 4.0D;
				cAvgRootHeight = 8.5D + cAvgRootHeight * 4.0D;

				for (int j2 = 0; j2 < 33; ++j2) {
					//double d6 = ((double)j2 - d5) * 12.0D * 128.0D / 256.0D / cAvgHeightVariation;
					double d6 = ((double)j2 - cAvgRootHeight) * 12.0D * 128.0D / 256.0D / cAvgHeightVariation;
					
					if (d6 < 0.0D) {
						d6 *= 4.0D;
					}

					double octaveNoise1 = this.noise1[noiseIndex] / 512.0D;
					double octaveNoise2 = this.noise2[noiseIndex] / 512.0D;
					double octaveNouse3 = (this.noise3[noiseIndex] / 10.0D + 1.0D) / 2.0D;
					double finalNoise = MathHelper.denormalizeClamp(octaveNoise1, octaveNoise2, octaveNouse3) - d6;

					if (j2 > 29) {
						double d11 = (double)((float)(j2 - 29) / 3.0F);
						finalNoise = finalNoise * (1.0D - d11) + -10.0D * d11;
					}

					//this.field_147434_q[l] = d10;
					noiseArray[noiseIndex] = finalNoise;
					++noiseIndex;
				}
			}
		}
	}

	/**
	 * Generates the base terrain with baseBlock and layerBlock
	 * @param x int
	 * @param z int
	 * @param chunkBlocks Block[]
	 */
	private void generateBaseTerrain(int x, int z, Block[] chunkBlocks) {
		//BiomeGenBase[] at the X and Z coordinates
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);

		//Generates the coarse noise XZ stack at the X and Z coordinates
		this.generateNoiseXZStack(this.noiseXZ, x * 4, z * 4);

		for (int k = 0; k < 4; ++k) {
			int l = k * 5;
			int i1 = (k + 1) * 5;

			for (int j1 = 0; j1 < 4; ++j1) {
				int k1 = (l + j1) * 33;
				int l1 = (l + j1 + 1) * 33;
				int i2 = (i1 + j1) * 33;
				int j2 = (i1 + j1 + 1) * 33;

				for (int k2 = 0; k2 < 32; ++k2) {
					//Scaling factor for fine noise
					double noiseVariationFactor = 0.125D;

					//Main noise
					double mainNoise1 = this.noiseXZ[k1 + k2];
					double mainNoise2 = this.noiseXZ[l1 + k2];
					double mainNoise3 = this.noiseXZ[i2 + k2];
					double mainNoise4 = this.noiseXZ[j2 + k2];

					//Fine noise
					double fineNoise1 = (this.noiseXZ[k1 + k2 + 1] - mainNoise1) * noiseVariationFactor;
					double fineNoise2 = (this.noiseXZ[l1 + k2 + 1] - mainNoise2) * noiseVariationFactor;
					double fineNoise3 = (this.noiseXZ[i2 + k2 + 1] - mainNoise3) * noiseVariationFactor;
					double fineNoise4 = (this.noiseXZ[j2 + k2 + 1] - mainNoise4) * noiseVariationFactor;

					//Iterating through 8 sub octaves. Technically this is generating a fractal
					for (int subOctaveIT = 0; subOctaveIT < 8; ++subOctaveIT) {
						//Scaling factor for fine sub noise
						double subNoiseVariationFactor = 0.25D;
						
						//Main sub noise
						double mainSubNoise1 = mainNoise1;
						double mainSubNoise2 = mainNoise2;
						
						//Fine sub noise
						double fineSubNoise1 = (mainNoise3 - mainNoise1) * subNoiseVariationFactor;
						double fineSubNoise2 = (mainNoise4 - mainNoise2) * subNoiseVariationFactor;

						//Iterating through 8 sub sub octaves. Technically this is generating a fractal within a fractal (fractalception)
						for (int subSubOctaveIT = 0; subSubOctaveIT < 4; ++subSubOctaveIT) {
							int cHeight = subSubOctaveIT + k * 4 << 12 | 0 + j1 * 4 << 8 | k2 * 8 + subOctaveIT;
							short maxHeight = 256;
							cHeight -= maxHeight;
							
							//Scaling factor for fine sub sub noise
							double subSubNoiseVariationFactor = 0.25D;
							
							//Fine sub sub noise
							double fineSubSubNoise = (mainSubNoise2 - mainSubNoise1) * subSubNoiseVariationFactor;
							
							//Main sub sub noise
							double mainSubSubNoise = mainSubNoise1 - fineSubSubNoise;

							//Generate base blocks, changed later on in replaceBlocksForBiome
							if ((mainSubSubNoise += fineSubSubNoise) > 0.0D) {
								chunkBlocks[cHeight += maxHeight] = this.baseBlock;
							} else if (k2 * 8 + subOctaveIT < this.layerHeight) {
								chunkBlocks[cHeight += maxHeight] = this.layerBlock;
							} else {
								chunkBlocks[cHeight += maxHeight] = null;
							}

							mainSubNoise1 += fineSubNoise1;
							mainSubNoise2 += fineSubNoise2;
						}

						//Adding fine noise to the main noise
						mainNoise1 += fineNoise1;
						mainNoise2 += fineNoise2;
						mainNoise3 += fineNoise3;
						mainNoise4 += fineNoise4;
					}
				}
			}
		}
	}
}
