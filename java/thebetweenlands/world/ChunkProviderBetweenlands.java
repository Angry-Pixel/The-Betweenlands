package thebetweenlands.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import thebetweenlands.world.biomes.WorldGenRedirect;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.feature.structure.WorldGenTarPoolDungeons;
import thebetweenlands.world.gen.MapGenCavesBetweenlands;
import cpw.mods.fml.common.registry.GameRegistry;

public class ChunkProviderBetweenlands implements IChunkProvider
{
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

	//Generates a base block noise. At a certain threshold of this noise, the baseBlock will be the top most block and the fillerBlock and topBlock of the Biome are ignored.
	private NoiseGeneratorPerlin baseBlockPatchNoiseGen;
	//Base block noise at any Y value in the current XZ stack
	private double[] baseBlockPatchNoise = new double[256];

	//Noise at any Y value in the current XZ stack
	private double[] noiseXZ;

	//Holds the octave noise at the current XZ stack
	double[] noise1;
	double[] noise2;
	double[] clampNoise;
	double[] baseNoise;

	//Holds the biome height gradient at the current XZ stack
	private float[] parabolicField;

	//Holds the biomes in the current XZ stack and around
	private BiomeGenBase[] biomesForGeneration;

	//Base block. Vanilla: stone
	public final Block baseBlock;
	//Layer block generated below layerHeight. Vanilla: water
	public final Block layerBlock;
	//layerBlock generates below this height
	private final int layerHeight;


	/////// Terrain gen features ///////
	//private MapGenBase caveGenerator = new MapGenCavesBetweenlands();
	private MapGenBase caveGenerator;

	private NoiseGeneratorSimplex treeNoise;

	private NoiseGeneratorSimplex speleothemDensityNoise;

	public ChunkProviderBetweenlands(World world, long seed, Block baseBlock, Block layerBlock, int layerHeight) {
		this.worldObj = world;
		this.baseBlock = baseBlock;
		this.layerBlock = layerBlock;
		this.layerHeight = layerHeight;

		//Registers the redirection IWorldGenerator that calls decorator#postTerrainGen
		GameRegistry.registerWorldGenerator(new WorldGenRedirect(), 0);
		
		//Initializes the noise generators
		this.initializeNoiseGen(seed);
		caveGenerator = new MapGenCavesBetweenlands(seed);
	}


	@Override
	public void recreateStructures(int x, int z) {
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(x, z);
		return biome == null ? null : biome.getSpawnableList(creatureType);
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
	public void saveExtraData() {
	}

	@Override
	public ChunkPosition func_147416_a(World world, String structureIdentifier, int x, int y, int z) {
		return null;
	}

	@Override
	public Chunk provideChunk(int x, int z) {
		//Set RNG seed
		this.rand.setSeed(x * 341873128712L + z * 132897987541L);

		Block[] chunkBlocks = new Block[65536];
		byte[] blockMeta = new byte[65536];
        //Gnerate base terrain (consist only of baseBlock and layerBlock)
        this.generateBaseTerrain(x, z, chunkBlocks);

		//Get biomes for generation
		this.biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, x * 16, z * 16, 16, 16);

		//Replace blocks for biome (replaces blocks according to the biomes)
		this.replaceBlocksForBiome(x, z, this.rand, chunkBlocks, blockMeta, this.biomesForGeneration);

		//Gen caves
		this.caveGenerator.func_151539_a(this, this.worldObj, x, z, chunkBlocks);

		//Decorate stuff before the chunk is provided
		List<BiomeGenBaseBetweenlands> uniqueBiomes = new ArrayList<BiomeGenBaseBetweenlands>();
		for(BiomeGenBase biome : this.biomesForGeneration) {
			if(!uniqueBiomes.contains(biome) && biome instanceof BiomeGenBaseBetweenlands) {
				uniqueBiomes.add((BiomeGenBaseBetweenlands) biome);
			}
		}
		for(BiomeGenBaseBetweenlands biome : uniqueBiomes) {
			this.rand.setSeed(this.worldObj.getSeed());
			this.rand.setSeed(x * (this.rand.nextLong() / 2L * 2L + 1L) + z * (this.rand.nextLong() / 2L * 2L + 1L) ^ this.worldObj.getSeed());
			biome.preChunkProvide(this.worldObj, this.rand, x, z, chunkBlocks, blockMeta, this.biomesForGeneration);
		}
		
		//Generate chunk
		Chunk chunk = new Chunk(this.worldObj, chunkBlocks, blockMeta, x, z);

		byte[] chunkBiomes = chunk.getBiomeArray();
		for (int k = 0; k < chunkBiomes.length; ++k) {
			chunkBiomes[k] = (byte)this.biomesForGeneration[k].biomeID;
		}

		chunk.generateSkylightMap();
		chunk.resetRelightChecks();

		return chunk;
	}

	@Override
	public Chunk loadChunk(int x, int z) {
		return this.provideChunk(x, z);
	}

	@Override
	public void populate(IChunkProvider cp, int x, int z) {
		BlockFalling.fallInstantly = true;

		int blockX = x * 16;
		int blockZ = z * 16;

		BiomeGenBase biome = worldObj.getBiomeGenForCoords(blockX + 16, blockZ + 16);

		if (biome instanceof BiomeGenBaseBetweenlands) {
			BiomeGenBaseBetweenlands bgbb = (BiomeGenBaseBetweenlands) biome;
			this.rand.setSeed(this.worldObj.getSeed());
			this.rand.setSeed(x * (this.rand.nextLong() / 2L * 2L + 1L) + z * (this.rand.nextLong() / 2L * 2L + 1L) ^ this.worldObj.getSeed());
			bgbb.postChunkPopulate(this.worldObj, this.rand, blockX, blockZ);
		} else {
			biome.decorate(this.worldObj, this.rand, blockX, blockZ);
		}
		for (int attempt = 0; attempt < 3; ++attempt)
			new WorldGenTarPoolDungeons().generate(worldObj, rand, blockX + rand.nextInt(16) + 8, rand.nextInt(70) + 10, blockZ + rand.nextInt(16) + 8);
		BlockFalling.fallInstantly = false;
	}

	/**
	 * Initializes the noise generators
	 *
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

		//Generates a base block noise. At a certain threshold of this noise, the baseBlock will be the top most block and the fillerBlock and topBlock of the Biome are ignored.
		//Used in replaceBlocksForBiome
		this.baseBlockPatchNoiseGen = new NoiseGeneratorPerlin(this.rand, 4);

		//Holds the generated noise XZ stack (4x33x4)
		this.noiseXZ = new double[825];

		//Holds the biome height gradient
		this.parabolicField = new float[25];

		//Generate parabolic field
		for( int j = -2; j <= 2; ++j ) {
			for( int k = -2; k <= 2; ++k ) {
				float f = 10.0F / MathHelper.sqrt_float(j * j + k * k + 0.2F);
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

		treeNoise = new NoiseGeneratorSimplex(rand);

		speleothemDensityNoise = new NoiseGeneratorSimplex(rand);
	}

	/**
	 * Generates the XZ noise stack
	 *
	 * @param noiseArray double[]
	 * @param x          int
	 * @param z          int
	 */
	private void generateNoiseXZStack(double[] noiseArray, int x, int z, int cx, int cz) {
		this.generateNoiseXZStackOld(noiseArray, x, z, cx, cz);

		/*//Last param not used
		this.baseNoise = this.baseNoiseOctave.generateNoiseOctaves(this.baseNoise, x, z, 5, 5, 2000.0D, 2000.0D, 0.0D);

		double noiseScale = 1200.0D;
		double yNoiseScale = 684.412D / noiseScale;

		this.noise1 = this.noiseOctave1.generateNoiseOctaves(this.noise1, x, 0, z, 5, 33, 5, noiseScale, yNoiseScale, noiseScale);

		//Index for base noise
		int baseNoiseIndex = 0;

		//Index for sub noise
		int noiseIndex = 0;

		for( int bxo = 0; bxo < 5; ++bxo ) {
			for( int bzo = 0; bzo < 5; ++bzo ) {
				//int biomeIndex = bxo * 4 + (bzo * 4) * 15;
				int biomeIndex = bxo + 2 + (bzo + 2) * 10;
				System.out.println("BI: " + biomeIndex);
				if(biomeIndex > 255) {
					biomeIndex = 255;
				}
				BiomeGenBase currentBiome = this.biomesForGeneration[biomeIndex];
				float currentBiomeRootHeight = currentBiome.rootHeight;
				float currentBiomeHeightVariation = currentBiome.heightVariation;
				if(currentBiome instanceof BiomeGenBaseBetweenlands) {
					//TODO
					if(!this.biomeGenList.contains(((BiomeGenBaseBetweenlands)currentBiome))) {
						this.biomeGenList.add(((BiomeGenBaseBetweenlands)currentBiome));
						if(!((BiomeGenBaseBetweenlands)currentBiome).isNoiseGenInitialized()) {
							((BiomeGenBaseBetweenlands)currentBiome).initializeNoiseGen(this.rand);
						}
						((BiomeGenBaseBetweenlands)currentBiome).generateNoise(cx, cz);
					}
					float heightVar = (float) ((BiomeGenBaseBetweenlands)currentBiome).getHeightVariation(x+(bxo+2)*4, z+(bzo+2)*4) / 128.0f;
					int currentBiomeRootHeightI = ((BiomeGenBaseBetweenlands)currentBiome).getRootHeight(x+(bxo+2)*4, z+(bzo+2)*4);
					currentBiomeRootHeight = ((float) currentBiomeRootHeightI / 128.0f) * 4.0f - 2.0f;
					currentBiomeHeightVariation = heightVar;
				}

				float avgRootHeight = currentBiomeRootHeight;
				float avgHeightVariation = currentBiomeHeightVariation;

				//Average
				int bc = 1;
				for( int sbbxo = -2; sbbxo <= 2; ++sbbxo ) {
					for( int sbbzo = -2; sbbzo <= 2; ++sbbzo ) {
						BiomeGenBase surroundingBiome = this.biomesForGeneration[bxo + sbbxo + 2 + (bzo + sbbzo + 2) * 10];

                        float surroundingBiomeRootHeight = surroundingBiome.rootHeight;
                        float surroundingBiomeHeightVariation = surroundingBiome.heightVariation;
                        if(surroundingBiome instanceof BiomeGenBaseBetweenlands) {
                        	//TODO
                        	if(!this.biomeGenList.contains(((BiomeGenBaseBetweenlands)surroundingBiome))) {
                        		this.biomeGenList.add(((BiomeGenBaseBetweenlands)surroundingBiome));
                            	if(!((BiomeGenBaseBetweenlands)surroundingBiome).isNoiseGenInitialized()) {
                            		((BiomeGenBaseBetweenlands)surroundingBiome).initializeNoiseGen(this.rand);
                            	}
                            	((BiomeGenBaseBetweenlands)surroundingBiome).generateNoise(cx, cz);
                            }
                        	surroundingBiomeHeightVariation = (float) ((BiomeGenBaseBetweenlands)surroundingBiome).getHeightVariation(x+(bxo+sbbxo+2)*4, z+(bzo+sbbzo+2)*4) / 128.0f;
                        	int surroundingBiomeRootHeightI = ((BiomeGenBaseBetweenlands)currentBiome).getRootHeight(x+(bxo+sbbxo+2)*4, z+(bzo+sbbzo+2)*4);
                        	surroundingBiomeRootHeight = ((float) surroundingBiomeRootHeightI / 128.0f) * 4.0f - 2.0f;

                        	avgRootHeight += surroundingBiomeRootHeight;
                        	avgHeightVariation += surroundingBiomeHeightVariation;
                        	bc++;
                        }
					}
				}

				avgRootHeight = avgRootHeight / (float)bc;
				avgHeightVariation = avgHeightVariation / (float)bc;

				//System.out.println("B: " + this.biomesForGeneration.length);
				//System.out.println("N: " + this.baseNoise.length);

				double fineBaseNoise = this.baseNoise[baseNoiseIndex] / 8000.0F;
				//if( fineBaseNoise < 0.0D ) {
				//    fineBaseNoise = -fineBaseNoise * 0.3D;
				//}
				//fineBaseNoise = fineBaseNoise * 3.0D - 2.0D;
				//if( fineBaseNoise < 0.0D ) {
				//    fineBaseNoise /= 2.0D;
				//
				//    if( fineBaseNoise < -1.0D ) {
				//        fineBaseNoise = -1.0D;
				//    }
				//
				//    fineBaseNoise /= 1.4D;
				//    fineBaseNoise /= 2.0D;
				//} else {
				//    if( fineBaseNoise > 1.0D ) {
				//        fineBaseNoise = 1.0D;
				//    }
				//
				//    fineBaseNoise /= 8.0D;
				//}

				++baseNoiseIndex;

				//System.out.println(baseNoiseIndex);
				//System.out.println(bxo * 5 + bzo);

				//baseNoiseIndex = bxo * 5 + (4-bzo);
				// baseNoiseIndex = bxo * 5 + bzo;

				for( int byo = 0; byo < 33; ++byo ) {
					double cAvgRootHeight = (double) 0.2F;
					double cAvgHeightVariation = (double) avgHeightVariation*10 + 0.005F;
					cAvgRootHeight += fineBaseNoise * 0.2D;
					cAvgRootHeight = cAvgRootHeight * 8.5D / 8.0D;
					//double d5 = 8.5D + cAvgRootHeight * 4.0D;
					cAvgRootHeight = 8.5D + cAvgRootHeight * 4.0D;
					double heightLimiterNoise = ((double) byo - cAvgRootHeight) * 12.0D * 128.0D / 256.0D / cAvgHeightVariation;
					noiseArray[noiseIndex] = this.noise1[noiseIndex]/8000.0D - heightLimiterNoise;
					++noiseIndex;
				}
			}
		}*/
	}

	/**
	 * Will be replaced later on with new worlgen, but I'll keep the old one in here for the others to test out stuff.
	 * @param noiseArray
	 * @param x
	 * @param z
	 * @param cx
	 * @param cz
	 */
	@Deprecated
	private void generateNoiseXZStackOld(double[] noiseArray, int x, int z, int cx, int cz) {
		//Generate noise XZ components
				//this.baseNoise = this.baseNoiseOctave.generateNoiseOctaves(this.baseNoise, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
				//this.noise1 = this.noiseOctave1.generateNoiseOctaves(this.noise1, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
				//this.noise2 = this.noiseOctave2.generateNoiseOctaves(this.noise2, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
				//this.clampNoise = this.noiseOctave3.generateNoiseOctaves(this.clampNoise, x, 0, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);

				/*this.baseNoise = this.baseNoiseOctave.generateNoiseOctaves(this.baseNoise, x, 100, z, 5, 1, 5, 2000.0D, 2000.0D, 2000.0D);

				this.noise1 = this.noiseOctave1.generateNoiseOctaves(this.noise1, x, 10, z, 5, 33, 5, 4000, 4000, 4000);
				this.noise2 = this.noiseOctave2.generateNoiseOctaves(this.noise2, x, 10, z, 5, 33, 5, 0, 0, 0);
				this.noise3 = this.noiseOctave3.generateNoiseOctaves(this.noise3, x, 10, z, 5, 33, 5, 0, 0, 0);*/

				/*this.baseNoise = this.baseNoiseOctave.generateNoiseOctaves(this.baseNoise, x, z, 5, 5, 200.0D, 200.0D, 0.5D);

				this.noise1 = this.noiseOctave1.generateNoiseOctaves(this.noise1, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
				this.noise2 = this.noiseOctave2.generateNoiseOctaves(this.noise2, x, 0, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);

				this.clampNoise = this.noiseOctave3.generateNoiseOctaves(this.clampNoise, x, 0, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);*/

				//TODO Find good noise scale
				double noiseScale = 1200.0D;
		        double yNoiseScale = 684.412D / noiseScale;

		        this.baseNoise = this.baseNoiseOctave.generateNoiseOctaves(this.baseNoise, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
		        this.noise1 = this.noiseOctave1.generateNoiseOctaves(this.noise1, x, 0, z, 5, 33, 5, noiseScale, yNoiseScale, noiseScale);
		        this.noise2 = this.noiseOctave2.generateNoiseOctaves(this.noise2, x, 0, z, 5, 33, 5, noiseScale, yNoiseScale, noiseScale);
		        this.clampNoise = this.noiseOctave3.generateNoiseOctaves(this.clampNoise, x, 0, z, 5, 33, 5, noiseScale / 80.0D, yNoiseScale / 160.0D, noiseScale / 80.0D);

		        int noiseIndex = 0;
		        int baseNoiseIndex = 0;

		        for( int bxo = 0; bxo < 5; ++bxo ) {
		            for( int bzo = 0; bzo < 5; ++bzo ) {
		                float averageHeightVariation = 0.0F;
		                float averageRootHeight = 0.0F;
		                float averageHeightGradient = 0.0F;
		                //The current biome
		                BiomeGenBase currentBiome = this.biomesForGeneration[bxo + 2 + (bzo + 2) * 10];
		                float currentBiomeRootHeight = currentBiome.rootHeight;
		                /*if(currentBiome instanceof BiomeGenBaseBetweenlands) {
		                	//TODO
		                	if(!this.biomeGenList.contains(((BiomeGenBaseBetweenlands)currentBiome))) {
		                		this.biomeGenList.add(((BiomeGenBaseBetweenlands)currentBiome));
		                    	if(!((BiomeGenBaseBetweenlands)currentBiome).isNoiseGenInitialized()) {
		                    		((BiomeGenBaseBetweenlands)currentBiome).initializeNoiseGen(this.rand);
		                    	}
		                    	((BiomeGenBaseBetweenlands)currentBiome).generateNoise(cx, cz);
		                    }
		                	float heightVar = (float) ((BiomeGenBaseBetweenlands)currentBiome).getHeightVariation(x+(bxo+2)*4, z+(bzo+2)*4) / 128.0f;
		                	int currentBiomeRootHeightI = ((BiomeGenBaseBetweenlands)currentBiome).getRootHeight(x+(bxo+2)*4, z+(bzo+2)*4);
		                	currentBiomeRootHeight = ((float) currentBiomeRootHeightI / 128.0f) * 4.0f - 2.0f;
		                }*/
		                //Gets the biomes in a 5x5 area around the current XZ stack to average the height
		                for( int sbbxo = -2; sbbxo <= 2; ++sbbxo ) {
		                    for( int sbbzo = -2; sbbzo <= 2; ++sbbzo ) {
		                        BiomeGenBase surroundingBiome = this.biomesForGeneration[bxo + sbbxo + 2 + (bzo + sbbzo + 2) * 10];

		                        float surroundingBiomeRootHeight = surroundingBiome.rootHeight;
		                        float surroundingBiomeHeightVariation = surroundingBiome.heightVariation;
		                        /*if(surroundingBiome instanceof BiomeGenBaseBetweenlands) {
		                        	//TODO
		                        	if(!this.biomeGenList.contains(((BiomeGenBaseBetweenlands)surroundingBiome))) {
		                        		this.biomeGenList.add(((BiomeGenBaseBetweenlands)surroundingBiome));
		                            	if(!((BiomeGenBaseBetweenlands)surroundingBiome).isNoiseGenInitialized()) {
		                            		((BiomeGenBaseBetweenlands)surroundingBiome).initializeNoiseGen(this.rand);
		                            	}
		                            	((BiomeGenBaseBetweenlands)surroundingBiome).generateNoise(cx, cz);
		                            }
		                        	surroundingBiomeHeightVariation = (float) ((BiomeGenBaseBetweenlands)surroundingBiome).getHeightVariation(x+(bxo+sbbxo+2)*4, z+(bzo+sbbzo+2)*4) / 128.0f;
		                        	int surroundingBiomeRootHeightI = ((BiomeGenBaseBetweenlands)currentBiome).getRootHeight(x+(bxo+sbbxo+2)*4, z+(bzo+sbbzo+2)*4);
		                        	surroundingBiomeRootHeight = ((float) surroundingBiomeRootHeightI / 128.0f) * 4.0f - 2.0f;
		                        }*/

		                        //System.out.println(surroundingBiome.biomeName);

		                        //No amplified terrain in the BL
								//if (this.field_147435_p == WorldType.AMPLIFIED && f3 > 0.0F)
		                        //{
		                        //    f3 = 1.0F + f3 * 2.0F;
		                        //    f4 = 1.0F + f4 * 4.0F;
		                        //}

		                        float heightGradient = this.parabolicField[sbbxo + 2 + (sbbzo + 2) * 5] / (surroundingBiomeRootHeight + 2.0F);

		                        //Use shallow gradient if the surrounding biome has a higher root height than the current biome
		                        if( surroundingBiomeRootHeight > currentBiomeRootHeight ) {
		                            heightGradient /= 2.0F;
		                        }

		                        averageHeightVariation += surroundingBiomeHeightVariation * heightGradient;
		                        averageRootHeight += surroundingBiomeRootHeight * heightGradient;
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
		                if( fineBaseNoise < 0.0D ) {
		                    fineBaseNoise = -fineBaseNoise * 0.3D;
		                }
		                fineBaseNoise = fineBaseNoise * 3.0D - 2.0D;
		                if( fineBaseNoise < 0.0D ) {
		                    fineBaseNoise /= 2.0D;

		                    if( fineBaseNoise < -1.0D ) {
		                        fineBaseNoise = -1.0D;
		                    }

		                    fineBaseNoise /= 1.4D;
		                    fineBaseNoise /= 2.0D;
		                } else {
		                    if( fineBaseNoise > 1.0D ) {
		                        fineBaseNoise = 1.0D;
		                    }

		                    fineBaseNoise /= 8.0D;
		                }

		                //System.out.println(fineBaseNoise);

		                ++baseNoiseIndex;
		                double cAvgRootHeight = averageRootHeight;
		                double cAvgHeightVariation = averageHeightVariation;
		                cAvgRootHeight += fineBaseNoise * 0.2D;
		                cAvgRootHeight = cAvgRootHeight * 8.5D / 8.0D;
		                //double d5 = 8.5D + cAvgRootHeight * 4.0D;
		                cAvgRootHeight = 8.5D + cAvgRootHeight * 4.0D;

		                for( int byo = 0; byo < 33; ++byo ) {
		                    //double d6 = ((double)j2 - d5) * 12.0D * 128.0D / 256.0D / cAvgHeightVariation;
		                    double heightLimiterNoise = (byo - cAvgRootHeight) * 12.0D * 128.0D / 256.0D / cAvgHeightVariation;

		                    //System.out.println("RH: " + cAvgRootHeight);
		                    //System.out.println("HV: " + cAvgHeightVariation);

		                    if( heightLimiterNoise < 0.0D ) {
		                        heightLimiterNoise *= 4.0D;
		                    }

		                    double octaveNoise1 = this.noise1[noiseIndex] / 512.0D;
		                    double octaveNoise2 = this.noise2[noiseIndex] / 512.0D;
		                    double octaveNoise3 = (this.clampNoise[noiseIndex] / 10.0D + 1.0D) / 2.0D;
		                    double finalNoise = MathHelper.denormalizeClamp(octaveNoise1, octaveNoise2, octaveNoise3) - heightLimiterNoise;

		                    if( byo > 29 ) {
		                        double d11 = (byo - 29) / 3.0F;
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
	 *
	 * @param x           int
	 * @param z           int
	 * @param chunkBlocks Block[]
	 */
	private void generateBaseTerrain(int x, int z, Block[] chunkBlocks) {
		//BiomeGenBase[] at the X and Z coordinates
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);

		//Generates the coarse noise XZ stack at the X and Z coordinates (4x33x4)
		this.biomeGenList.clear();
		this.generateNoiseXZStack(this.noiseXZ, x * 4, z * 4, x, z);

		for( int k = 0; k < 4; ++k ) {
			int l = k * 5;
			int i1 = (k + 1) * 5;

			for( int j1 = 0; j1 < 4; ++j1 ) {
				int k1 = (l + j1) * 33;
				int l1 = (l + j1 + 1) * 33;
				int i2 = (i1 + j1) * 33;
				int j2 = (i1 + j1 + 1) * 33;

				for( int k2 = 0; k2 < 32; ++k2 ) {
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
					for( int subOctaveIT = 0; subOctaveIT < 8; ++subOctaveIT ) {
						//Scaling factor for fine sub noise
						double subNoiseVariationFactor = 0.25D;

						//Main sub noise
						double mainSubNoise1 = mainNoise1;
						double mainSubNoise2 = mainNoise2;

						//Fine sub noise
						double fineSubNoise1 = (mainNoise3 - mainNoise1) * subNoiseVariationFactor;
						double fineSubNoise2 = (mainNoise4 - mainNoise2) * subNoiseVariationFactor;

						//Iterating through 8 sub sub octaves. Technically this is generating a fractal within a fractal (fractalception)
						for( int subSubOctaveIT = 0; subSubOctaveIT < 4; ++subSubOctaveIT ) {
							int cHeight = subSubOctaveIT + k * 4 << 12 | j1 * 4 << 8 | k2 * 8 + subOctaveIT;
							short maxHeight = 256;
							cHeight -= maxHeight;

							//Scaling factor for fine sub sub noise
							double subSubNoiseVariationFactor = 0.25D;

							//Fine sub sub noise
							double fineSubSubNoise = (mainSubNoise2 - mainSubNoise1) * subSubNoiseVariationFactor;

							//Main sub sub noise
							double mainSubSubNoise = mainSubNoise1 - fineSubSubNoise;

							//Generate base blocks, changed later on in replaceBlocksForBiome
							//Generates 4 layers
							for( int fillWidth = 0; fillWidth < 4; ++fillWidth ) {
								if( (mainSubSubNoise += fineSubSubNoise) > 0.0D ) {
									chunkBlocks[cHeight += maxHeight] = this.baseBlock;
								} else if( k2 * 8 + subOctaveIT <= this.layerHeight ) {
									chunkBlocks[cHeight += maxHeight] = this.layerBlock;
								} else {
									chunkBlocks[cHeight += maxHeight] = null;
								}
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

	private List<BiomeGenBaseBetweenlands> biomeGenList = new ArrayList<BiomeGenBaseBetweenlands>();
	/**
	 * Replaces the blocks generated by ChunkProviderBetweenlands#generateBaseTerrain
	 * with the according biome blocks.
	 *
	 * @param chunkX              int
	 * @param chunkZ              int
	 * @param rng                 Random
	 * @param chunkBlocks         Block[]
	 * @param blockMeta           byte[]
	 * @param biomesForGeneration BiomeGenBase[]
	 */
	public void replaceBlocksForBiome(int chunkX, int chunkZ, Random rng, Block[] chunkBlocks, byte[] blockMeta, BiomeGenBase[] biomesForGeneration) {
		//Scaling factor for base block noise
		double baseBlockNoiseVariationFactor = 0.03125D;

		//Generate base block noise
		this.baseBlockPatchNoise = this.baseBlockPatchNoiseGen.func_151599_a(this.baseBlockPatchNoise, chunkX * 16, chunkZ * 16, 16, 16, baseBlockNoiseVariationFactor * 2.0D, baseBlockNoiseVariationFactor * 2.0D, 1.0D);

		//Iterate through all stacks (16x16) and replace the blocks according to the biome
		this.biomeGenList.clear();
		for( int bx = 0; bx < 16; ++bx ) {
			for( int bz = 0; bz < 16; ++bz ) {
				BiomeGenBase biome = biomesForGeneration[bz + bx * 16];
				if( !(biome instanceof BiomeGenBaseBetweenlands) ) {
					biome.genTerrainBlocks(this.worldObj, this.rand, chunkBlocks, blockMeta, chunkX * 16 + bx, chunkZ * 16 + bz, this.baseBlockPatchNoise[bz + bx * 16]);
				} else {
					BiomeGenBaseBetweenlands bgbb = (BiomeGenBaseBetweenlands) biome;
					if(!this.biomeGenList.contains(bgbb)) {
						this.biomeGenList.add(bgbb);
						if(!bgbb.isNoiseGenInitialized()) {
							bgbb.initializeNoiseGen(new Random(this.worldObj.getSeed()));
						}
						bgbb.generateNoise(chunkX, chunkZ);
					}
					bgbb.replaceStackBlocks(chunkX * 16 + bx, chunkZ * 16 + bz, bx, bz, this.baseBlockPatchNoise[bz + bx * 16], rng, chunkBlocks, blockMeta, this, biomesForGeneration, this.worldObj);
				}
			}
		}
	}

	public double evalTreeNoise(double x, double z) {
		return treeNoise.func_151605_a(x, z);
	}

	public double evalSpeleothemDensityNoise(double x, double z) {
		return speleothemDensityNoise.func_151605_a(x, z);
	}
}
