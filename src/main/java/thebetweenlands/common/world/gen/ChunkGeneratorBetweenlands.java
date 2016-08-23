package thebetweenlands.common.world.gen;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.event.terraingen.InitNoiseGensEvent;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeGenerator;

public class ChunkGeneratorBetweenlands implements IChunkGenerator {
	/**
	 * Base block. Vanilla: stone
	 */
	public final Block baseBlock;

	/**
	 * Layer block generated below layerHeight. Vanilla: water
	 */
	public final Block layerBlock;





	private final Random rand;
	private NoiseGeneratorOctaves minLimitPerlinNoise;
	private NoiseGeneratorOctaves maxLimitPerlinNoise;
	private NoiseGeneratorOctaves mainPerlinNoise;
	private NoiseGeneratorPerlin surfaceNoise;
	public NoiseGeneratorOctaves scaleNoise;
	public NoiseGeneratorOctaves depthNoise;
	public NoiseGeneratorOctaves forestNoise;
	private final World worldObj;
	/**
	 * Technically this isn't heightmap, it's a 3D density map
	 */
	private final double[] heightMap;
	private final float[] biomeWeights;
	//private ChunkProviderSettings settings;
	private IBlockState oceanBlock = Blocks.WATER.getDefaultState();
	private double[] depthBuffer = new double[256];
	private float[] terrainBiomeWeights = new float[256];
	private Biome[] biomesForGeneration;
	private double[] mainNoiseRegion;
	private double[] minLimitRegion;
	private double[] maxLimitRegion;
	private double[] depthRegion;
	private final long seed;
	private final int layerHeight;






	public ChunkGeneratorBetweenlands(World world, long seed, Block baseBlock, Block layerBlock, int layerHeight) {
		this.baseBlock = baseBlock;
		this.layerBlock = layerBlock;
		this.layerHeight = layerHeight;
		this.worldObj = world;
		this.seed = seed;
		this.rand = new Random(seed);
		this.heightMap = new double[825];
		this.biomeWeights = new float[25];
		for (int i = -2; i <= 2; ++i) {
			for (int j = -2; j <= 2; ++j) {
				float f = 10.0F / MathHelper.sqrt_float((float)(i * i + j * j) + 0.2F);
				this.biomeWeights[i + 2 + (j + 2) * 5] = f;
			}
		}
		this.minLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
		this.maxLimitPerlinNoise = new NoiseGeneratorOctaves(this.rand, 16);
		this.mainPerlinNoise = new NoiseGeneratorOctaves(this.rand, 8);
		this.surfaceNoise = new NoiseGeneratorPerlin(this.rand, 4);
		this.scaleNoise = new NoiseGeneratorOctaves(this.rand, 10);
		this.depthNoise = new NoiseGeneratorOctaves(this.rand, 16);
		this.forestNoise = new NoiseGeneratorOctaves(this.rand, 8);
		InitNoiseGensEvent.ContextOverworld ctx = new InitNoiseGensEvent.ContextOverworld(minLimitPerlinNoise, maxLimitPerlinNoise, mainPerlinNoise, surfaceNoise, scaleNoise, depthNoise, forestNoise);
		ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(world, this.rand, ctx);
		this.minLimitPerlinNoise = ctx.getLPerlin1();
		this.maxLimitPerlinNoise = ctx.getLPerlin2();
		this.mainPerlinNoise = ctx.getPerlin();
		this.surfaceNoise = ctx.getHeight();
		this.scaleNoise = ctx.getScale();
		this.depthNoise = ctx.getDepth();
		this.forestNoise = ctx.getForest();
		//this.settings = new ChunkProviderSettings.Factory().build();
		world.setSeaLevel(layerHeight);
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ) {
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		this.setBlocksInChunk(chunkX, chunkZ, chunkprimer);
		this.biomesForGeneration = this.worldObj.getBiomeProvider().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		this.replaceBiomeBlocks(chunkX, chunkZ, chunkprimer, this.biomesForGeneration);

		Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
		byte[] biomeArray = chunk.getBiomeArray();

		for (int i = 0; i < biomeArray.length; ++i) {
			biomeArray[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	/**
	 * Generates the base terrain
	 * @param chunkX
	 * @param chunkZ
	 * @param primer
	 */
	public void setBlocksInChunk(int chunkX, int chunkZ, ChunkPrimer primer) {
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
		this.generateHeightmap(chunkX * 4, 0, chunkZ * 4);

		//X
		for (int heightMapX = 0; heightMapX < 4; ++heightMapX) {
			int indexXC = heightMapX * 5; //1
			int indexXN = (heightMapX + 1) * 5; //2

			/*
			 * 1 2
			 */

			//Z
			for (int heightMapZ = 0; heightMapZ < 4; ++heightMapZ) {
				int indexXCZC = (indexXC + heightMapZ) * 33; //1
				int indexXCZN = (indexXC + heightMapZ + 1) * 33; //2
				int indexXNZC = (indexXN + heightMapZ) * 33; //3
				int indexXNZN = (indexXN + heightMapZ + 1) * 33; //4

				/*
				 * 1 3
				 * 2 4
				 */

				//Y
				for (int heightMapY = 0; heightMapY < 32; ++heightMapY) {
					//Values
					double valXCZCYC = this.heightMap[indexXCZC + heightMapY]; //1
					double valXCZNYC = this.heightMap[indexXCZN + heightMapY]; //2
					double valXNZCYC = this.heightMap[indexXNZC + heightMapY]; //3
					double valXNZNYC = this.heightMap[indexXNZN + heightMapY]; //4
					double valXCZCYN = this.heightMap[indexXCZC + heightMapY + 1]; //5
					double valXCZNYN = this.heightMap[indexXCZN + heightMapY + 1]; //6
					double valXNZCYN = this.heightMap[indexXNZC + heightMapY + 1]; //7
					double valXNZNYN = this.heightMap[indexXNZN + heightMapY + 1]; //8

					//Step along Y axis (1/8 of the difference)
					double stepYAxisXCZC = (valXCZCYN - valXCZCYC) * 0.125D;
					double stepYAxisXCZN = (valXCZNYN - valXCZNYC) * 0.125D;
					double stepYAxisXNZC = (valXNZCYN - valXNZCYC) * 0.125D;
					double stepYAxisXNZN = (valXNZNYN - valXNZNYC) * 0.125D;

					double currentValXCZCYC = valXCZCYC;
					double currentValXCZNYC = valXCZNYC;
					double currentValXNZCYC = valXNZCYC;
					double currentValXNZNYC = valXNZNYC;

					/*
					 * At this point we have the values of a 2x2x2 cube and their difference along the Y axis (e.g. 5 - 1, 6 - 2, 7 - 3, 8 - 4)
					 * 
					 * Y:
					 * 1 3
					 * 2 4
					 * 
					 * Y+1:
					 * 5 7
					 * 6 8
					 */

					//Now it expands that cube into a 8x4x4 area and linearly interpolates the values

					//Step Y axis
					for (int blockY = 0; blockY < 8; ++blockY) {
						double currentValXCZC = currentValXCZCYC;
						double currentValXCZN = currentValXCZNYC;

						//Step along X axis
						double stepXAxisZC = (currentValXNZCYC - currentValXCZCYC) * 0.25D;
						double stepXAxisZN = (currentValXNZNYC - currentValXCZNYC) * 0.25D;

						//Step X axis
						for (int blockX = 0; blockX < 4; ++blockX) {
							//Step along Z axis
							double stepZAxis = (currentValXCZN - currentValXCZC) * 0.25D;

							double currentValZC = currentValXCZC - stepZAxis;

							//Step Z axis
							for (int blockZ = 0; blockZ < 4; ++blockZ) {
								if ((currentValZC += stepZAxis) > 0.0D) {
									primer.setBlockState(heightMapX * 4 + blockX, heightMapY * 8 + blockY, heightMapZ * 4 + blockZ, this.baseBlock.getDefaultState());
								} else if (heightMapY * 8 + blockY < this.layerHeight) {
									primer.setBlockState(heightMapX * 4 + blockX, heightMapY * 8 + blockY, heightMapZ * 4 + blockZ, this.layerBlock.getDefaultState());
								}
							}

							currentValXCZC += stepXAxisZC;
							currentValXCZN += stepXAxisZN;
						}

						currentValXCZCYC += stepYAxisXCZC;
						currentValXCZNYC += stepYAxisXCZN;
						currentValXNZCYC += stepYAxisXNZC;
						currentValXNZNYC += stepYAxisXNZN;
					}
				}
			}
		}
	}

	/**
	 * Generates a 33x5x5 (Y*X*Z) heightmap
	 * @param x
	 * @param y
	 * @param z
	 */
	private void generateHeightmap(int x, int y, int z) {
		this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, x, z, 5, 5, /*(double)this.settings.depthNoiseScaleX*/200.0D, /*(double)this.settings.depthNoiseScaleZ*/200.0D, /*(double)this.settings.depthNoiseScaleExponent*/0.5D);
		float f = /*this.settings.coordinateScale*/684.412F * 8;
		float f1 = /*this.settings.heightScale*/684.412F * 8;
		this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, x, y, z, 5, 33, 5, (double)(f / /*this.settings.mainNoiseScaleX*/80.0F), (double)(f1 / /*this.settings.mainNoiseScaleY*/160.0F), (double)(f / /*this.settings.mainNoiseScaleZ*/80.0F));
		this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, x, y, z, 5, 33, 5, (double)f, (double)f1, (double)f);
		this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, x, y, z, 5, 33, 5, (double)f, (double)f1, (double)f);

		int noiseIndex = 0;
		int heightMapIndex = 0;

		for (int heightMapX = 0; heightMapX < 5; ++heightMapX) {
			for (int heightMapZ = 0; heightMapZ < 5; ++heightMapZ) {
				float biomeVariation = 0.0F;
				float biomeDepth = 0.0F;
				float centerBiomeWeight = 0.0F;
				Biome centerBiome = this.biomesForGeneration[heightMapX + 2 + (heightMapZ + 2) * 10];

				float centerBiomeWeightTest = 0.0F;
				
				//Averages biome height and variation in a 5x5 area
				for (int offsetX = -2; offsetX <= 2; ++offsetX) {
					for (int offsetZ = -2; offsetZ <= 2; ++offsetZ) {
						Biome nearbyBiome = this.biomesForGeneration[heightMapX + offsetX + 2 + (heightMapZ + offsetZ + 2) * 10];
						float nearbyBiomeDepth = nearbyBiome.getBaseHeight();
						/*if(nearbyBiome instanceof BiomeSwamplands) {
							//nearbyBiomeDepth += 5.0F;
							System.out.println(nearbyBiome + " " + nearbyBiomeDepth);
						}*/
						float nearbyBiomeVariation = nearbyBiome.getHeightVariation();

						//No amplified terrain
						/*if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0F)
                        {
                            f5 = 1.0F + f5 * 2.0F;
                            f6 = 1.0F + f6 * 4.0F;
                        }*/

						float weight = this.biomeWeights[offsetX + 2 + (offsetZ + 2) * 5];

						if (nearbyBiome.getBaseHeight() > centerBiome.getBaseHeight()) {
							weight /= 2.0F;
						}

						biomeVariation += nearbyBiomeVariation * weight;
						biomeDepth += nearbyBiomeDepth * weight;
						centerBiomeWeight += weight;
						
						if(nearbyBiome == centerBiome)
							centerBiomeWeightTest += 1.0F;
					}
				}

				/*//TODO: Do this in constructor when generating biomeWeights
				float maxBiomeTerrainWeight = 0.0F;
				for(int xo = -2; xo <= 2; xo++) {
					for(int zo = -2; zo <= 2; zo++) {
						maxBiomeTerrainWeight += (float) (10.0F / Math.sqrt(xo*xo+zo*zo+0.2F));
					}
				}*/

				this.terrainBiomeWeights[heightMapX + heightMapZ * 5] = /*centerBiomeWeight / maxBiomeTerrainWeight*/centerBiomeWeightTest/25.0F;

				biomeVariation = biomeVariation / centerBiomeWeight;
				biomeDepth = biomeDepth / centerBiomeWeight;

				//Small offset for biome depth?
				double depthPerturbation = this.depthRegion[heightMapIndex] / 8000.0D;

				//TODO: Check this
				depthPerturbation = 0.0D;

				if (depthPerturbation < 0.0D) {
					depthPerturbation = -depthPerturbation * 0.3D;
				}

				depthPerturbation = depthPerturbation * 3.0D - 2.0D;

				if (depthPerturbation < 0.0D) {
					depthPerturbation = depthPerturbation / 2.0D;

					if (depthPerturbation < -1.0D)
					{
						depthPerturbation = -1.0D;
					}

					depthPerturbation = depthPerturbation / 1.4D;
					depthPerturbation = depthPerturbation / 2.0D;
				} else {
					if (depthPerturbation > 1.0D) {
						depthPerturbation = 1.0D;
					}

					depthPerturbation = depthPerturbation / 8.0D;
				}

				++heightMapIndex;

				double depth = (biomeDepth * this.layerHeight) / 256.0D /*- depthPerturbation * 10*/;

				for (int heightMapY = 0; heightMapY < 33; ++heightMapY) {
					//					double densityOffset = ((double)heightMapY - depth * 32.0D) * 4.0D;
					//
					//					/*if (densityOffset < 0.0D) {
					//						densityOffset *= 4.0D;
					//					}*/
					//
					//					/*double maxGenDensity = 0.0D;
					//					for(int i = 0; i < octaves - 1; i++) {
					//						maxGenDensity += 2 * Math.pow(2.0D, i);
					//					}
					//					maxGenDensity /= 2.0D;*/
					//					
					//					double minDensity = (this.minLimitRegion[noiseIndex] / 65535.0D + 1.0D) / 2.0D * (256.0D - this.layerHeight) + this.layerHeight;
					//					double maxDensity = (this.maxLimitRegion[noiseIndex] / 65535.0D + 1.0D) / 2.0D * (256.0D - this.layerHeight) + this.layerHeight;
					//					double mainDensity = (this.mainNoiseRegion[noiseIndex] / 255.0D + 1.0D) / 2.0D;
					//					
					//					double clampedDensity = MathHelper.denormalizeClamp(minDensity, maxDensity, mainDensity) / 256.0D / 4.0D - densityOffset;
					//
					//					//System.out.println(clampedDensity);
					//					
					//					/*if (heightMapY > 29) {
					//						double multiplier = (double)((float)(heightMapY - 29) / 3.0F);
					//						clampedDensity = clampedDensity * (1.0D - multiplier) + -10.0D * multiplier;
					//					}*/
					//
					//					this.heightMap[noiseIndex] = clampedDensity;

					double densityOffset = ((double)heightMapY * 8.0D - biomeDepth - (depthPerturbation * biomeVariation / 256.0D)) / 256.0D;

					double maxGenDensity16 = 0.0D;
					for(int i = 0; i < 16 - 1; i++) {
						maxGenDensity16 += 2 * Math.pow(2.0D, i);
					}
					maxGenDensity16 /= 2.0D;

					double maxGenDensity8 = 0.0D;
					for(int i = 0; i < 8 - 1; i++) {
						maxGenDensity8 += 2 * Math.pow(2.0D, i);
					}
					maxGenDensity8 /= 2.0D;

					double minDensity = (this.minLimitRegion[noiseIndex] / maxGenDensity16) * biomeVariation / 256.0D;
					double maxDensity = (this.maxLimitRegion[noiseIndex] / maxGenDensity16) * biomeVariation / 256.0D;
					double mainDensity = (this.mainNoiseRegion[noiseIndex] / maxGenDensity8);

					this.heightMap[noiseIndex] = MathHelper.denormalizeClamp(minDensity, maxDensity, mainDensity) - densityOffset;

					++noiseIndex;
				}
			}
		}

		//		this.depthRegion = this.depthNoise.generateNoiseOctaves(this.depthRegion, x, z, 5, 5, (double)this.settings.depthNoiseScaleX, (double)this.settings.depthNoiseScaleZ, (double)this.settings.depthNoiseScaleExponent);
		//		float f = this.settings.coordinateScale;
		//		float f1 = this.settings.heightScale;
		//		this.mainNoiseRegion = this.mainPerlinNoise.generateNoiseOctaves(this.mainNoiseRegion, x, y, z, 5, 33, 5, (double)(f / this.settings.mainNoiseScaleX), (double)(f1 / this.settings.mainNoiseScaleY), (double)(f / this.settings.mainNoiseScaleZ));
		//		this.minLimitRegion = this.minLimitPerlinNoise.generateNoiseOctaves(this.minLimitRegion, x, y, z, 5, 33, 5, (double)f, (double)f1, (double)f);
		//		this.maxLimitRegion = this.maxLimitPerlinNoise.generateNoiseOctaves(this.maxLimitRegion, x, y, z, 5, 33, 5, (double)f, (double)f1, (double)f);
		//		int noiseIndex = 0;
		//		int heightMapIndex = 0;
		//
		//		for (int heightMapX = 0; heightMapX < 5; ++heightMapX) {
		//			for (int heightMapZ = 0; heightMapZ < 5; ++heightMapZ) {
		//				float biomeVariation = 0.0F;
		//				float biomeDepth = 0.0F;
		//				float centerBiomeWeight = 0.0F;
		//				Biome centerBiome = this.biomesForGeneration[heightMapX + 2 + (heightMapZ + 2) * 10];
		//
		//				//Averages biome height and variation in a 5x5 area
		//				for (int offsetX = -2; offsetX <= 2; ++offsetX) {
		//					for (int offsetZ = -2; offsetZ <= 2; ++offsetZ) {
		//						Biome nearbyBiome = this.biomesForGeneration[heightMapX + offsetX + 2 + (heightMapZ + offsetZ + 2) * 10];
		//						float nearbyBiomeDepth = this.settings.biomeDepthOffSet + nearbyBiome.getBaseHeight() * this.settings.biomeDepthWeight;
		//						float nearbyBiomeVariation = this.settings.biomeScaleOffset + nearbyBiome.getHeightVariation() * this.settings.biomeScaleWeight;
		//
		//						//No amplified terrain
		//						/*if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0F)
		//                        {
		//                            f5 = 1.0F + f5 * 2.0F;
		//                            f6 = 1.0F + f6 * 4.0F;
		//                        }*/
		//
		//						float weight = this.biomeWeights[offsetX + 2 + (offsetZ + 2) * 5] / (nearbyBiomeDepth + 2.0F);
		//
		//						if (nearbyBiome.getBaseHeight() > centerBiome.getBaseHeight()) {
		//							weight /= 2.0F;
		//						}
		//
		//						biomeVariation += nearbyBiomeVariation * weight;
		//						biomeDepth += nearbyBiomeDepth * weight;
		//						centerBiomeWeight += weight;
		//					}
		//				}
		//
		//				biomeVariation = biomeVariation / centerBiomeWeight;
		//				biomeDepth = biomeDepth / centerBiomeWeight;
		//				biomeVariation = biomeVariation * 0.9F + 0.1F; //Magic numbers?
		//				biomeDepth = (biomeDepth * 4.0F - 1.0F) / 8.0F; //More magic numbers??
		//
		//				//Small offset for biome depth?
		//				double depthPerturbation = this.depthRegion[heightMapIndex] / 8000.0D;
		//
		//				if (depthPerturbation < 0.0D) {
		//					depthPerturbation = -depthPerturbation * 0.3D;
		//				}
		//
		//				depthPerturbation = depthPerturbation * 3.0D - 2.0D;
		//
		//				if (depthPerturbation < 0.0D) {
		//					depthPerturbation = depthPerturbation / 2.0D;
		//
		//					if (depthPerturbation < -1.0D)
		//					{
		//						depthPerturbation = -1.0D;
		//					}
		//
		//					depthPerturbation = depthPerturbation / 1.4D;
		//					depthPerturbation = depthPerturbation / 2.0D;
		//				} else {
		//					if (depthPerturbation > 1.0D) {
		//						depthPerturbation = 1.0D;
		//					}
		//
		//					depthPerturbation = depthPerturbation / 8.0D;
		//				}
		//
		//				++heightMapIndex;
		//
		//				double biomeDepthDouble = (double)biomeDepth;
		//				biomeDepthDouble = biomeDepthDouble + depthPerturbation * 0.2D;
		//				biomeDepthDouble = biomeDepthDouble * (double)this.settings.baseSize / 8.0D;
		//				double depth = (double)this.settings.baseSize + biomeDepthDouble * 4.0D;
		//
		//				for (int heightMapY = 0; heightMapY < 33; ++heightMapY) {
		//					double densityOffset = ((double)heightMapY - depth) * (double)this.settings.stretchY * 128.0D / 256.0D / (double)biomeVariation;
		//
		//					if (densityOffset < 0.0D) {
		//						densityOffset *= 4.0D;
		//					}
		//
		//					double minDensity = this.minLimitRegion[noiseIndex] / (double)this.settings.lowerLimitScale;
		//					double maxDensity = this.maxLimitRegion[noiseIndex] / (double)this.settings.upperLimitScale;
		//					double mainDensity = (this.mainNoiseRegion[noiseIndex] / 10.0D + 1.0D) / 2.0D;
		//					//TODO: Figure out noise ranges
		//					double tv = this.mainNoiseRegion[noiseIndex];
		//					if(tv >= 200F || tv <= -200F)
		//						System.out.println("L: " + tv);
		//					double clampedDensity = MathHelper.denormalizeClamp(minDensity, maxDensity, mainDensity) - densityOffset;
		//
		//					if (heightMapY > 29) {
		//						double multiplier = (double)((float)(heightMapY - 29) / 3.0F);
		//						clampedDensity = clampedDensity * (1.0D - multiplier) + -10.0D * multiplier;
		//					}
		//
		//					this.heightMap[noiseIndex] = clampedDensity;
		//					++noiseIndex;
		//				}
		//			}
		//		}
	}

	/**
	 * Modifies the terrain with biome specific features
	 * @param chunkX
	 * @param chunkZ
	 * @param primer
	 * @param biomesIn
	 */
	public void replaceBiomeBlocks(int chunkX, int chunkZ, ChunkPrimer primer, Biome[] biomesIn) {
		if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, this.worldObj))
			return;

		this.depthBuffer = this.surfaceNoise.getRegion(this.depthBuffer, (double)(chunkX * 16), (double)(chunkZ * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);

		/*for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				double baseBlockNoise = this.depthBuffer[z + x * 16];
				Biome biome = biomesIn[z + x * 16];
				if(biome instanceof BiomeBetweenlands) {
					BiomeGenerator generator = ((BiomeBetweenlands)biome).getBiomeGenerator();
					generator.initializeGenerators(this.seed);
					generator.generateNoise(chunkX, chunkZ);
					generator.replaceBiomeBlocks(chunkX * 16 + x, chunkZ * 16 + z, x, z, baseBlockNoise, this.rand, this.seed, primer, this, biomesIn, this.terrainBiomeWeights[x + z * 16]);
				} else {
					biome.genTerrainBlocks(this.worldObj, this.rand, primer, chunkX * 16 + x, chunkZ * 16 + z, baseBlockNoise);
				}
			}
		}*/

		//		for (int x = 0; x < 4; ++x) {
		//			for (int z = 0; z < 4; ++z) {
		//
		//				/*//Step along X axis
		//					double stepXAxisZC = (currentValXNZCYC - currentValXCZCYC) * 0.25D;
		//					double stepXAxisZN = (currentValXNZNYC - currentValXCZNYC) * 0.25D;
		//
		//					//Step X axis
		//					for (int blockX = 0; blockX < 4; ++blockX) {
		//						//Step along Z axis
		//						double stepZAxis = (currentValXCZN - currentValXCZC) * 0.25D;
		//
		//						double currentValZC = currentValXCZC - stepZAxis;
		//
		//						//Step Z axis
		//						for (int blockZ = 0; blockZ < 4; ++blockZ) {
		//							if ((currentValZC += stepZAxis) > 0.0D) {
		//
		//							}
		//						}
		//					}*/
		//
		//				float terrainBiomeWeight = 0.0F;
		//
		//				try {
		//					float currentValXNZC = this.terrainBiomeWeights[x + 1 + z * 16 + 2];
		//					float currentValXCZC = this.terrainBiomeWeights[x + z * 16 + 2];
		//					float currentValXNZN = this.terrainBiomeWeights[x + 1 + z * 16 + 16 + 2];
		//					float currentValXCZN = this.terrainBiomeWeights[x + z * 16 + 16 + 2];
		//
		//					float stepXAxisZC = (currentValXNZC - currentValXCZC) * 0.25F;
		//					float stepXAxisZN = (currentValXNZN - currentValXCZN) * 0.25F;
		//
		//					for(int stepX = 0; stepX < 4; ++stepX) {
		//						float stepZAxis = (currentValXCZN - currentValXCZC) * 0.25F;
		//
		//						float currentValZC = currentValXCZC - stepZAxis;
		//
		//						for (int stepZ = 0; stepZ < 4; ++stepZ) {
		//							terrainBiomeWeight = currentValZC += stepZAxis;
		//
		//							int blockX = x * 4 + stepX;
		//							int blockZ = z * 4 + stepZ;
		//							
		//							double baseBlockNoise = this.depthBuffer[blockZ + blockX * 16];
		//							Biome biome = biomesIn[blockZ + blockX * 16];
		//							if(biome instanceof BiomeBetweenlands) {
		//								BiomeGenerator generator = ((BiomeBetweenlands)biome).getBiomeGenerator();
		//								generator.initializeGenerators(this.seed);
		//								generator.generateNoise(chunkX, chunkZ);
		//
		//								generator.replaceBiomeBlocks(chunkX * 16 + blockX, chunkZ * 16 + blockZ, blockX, blockZ, baseBlockNoise, this.rand, this.seed, primer, this, biomesIn, terrainBiomeWeight);
		//							} else {
		//								biome.genTerrainBlocks(this.worldObj, this.rand, primer, chunkX * 16 + blockX, chunkZ * 16 + blockZ, baseBlockNoise);
		//							}
		//						}
		//					}
		//				} catch(Exception ex) {
		//
		//				}
		//			}
		//		}

		try {
			for(int weightStepX = 0; weightStepX < 4; weightStepX++) {
				for(int weightStepZ = 0; weightStepZ < 4; weightStepZ++) {
					float weightXCZC = this.terrainBiomeWeights[weightStepZ + weightStepX * 5];
					float weightXNZC = this.terrainBiomeWeights[weightStepZ+1 + weightStepX * 5];
					float weightXCZN = this.terrainBiomeWeights[weightStepZ + (weightStepX+1) * 5];
					float weightXNZN = this.terrainBiomeWeights[weightStepZ+1 + (weightStepX+1) * 5];

					float stepXAxisXC = (weightXCZN - weightXCZC) * 0.25F;
					float stepXAxisXN = (weightXNZN - weightXNZC) * 0.25F;

					float currentValXAxisXC = weightXCZC;
					float currentValXAxisXN = weightXNZC;

					for(int interpStepX = 0; interpStepX < 4; interpStepX++) {
						float stepZAxis = (currentValXAxisXN - currentValXAxisXC) * 0.25F;

						float currentVal = currentValXAxisXN/* - stepZAxis*/;

						for(int interpStepZ = 0; interpStepZ < 4; interpStepZ++) {
							int x = weightStepX * 4 + interpStepX;
							int z = weightStepZ * 4 + interpStepZ;

							double baseBlockNoise = this.depthBuffer[z + x * 16];
							Biome biome = biomesIn[z + x * 16];
							if(biome instanceof BiomeBetweenlands) {
								BiomeGenerator generator = ((BiomeBetweenlands)biome).getBiomeGenerator();
								generator.initializeGenerators(this.seed);
								generator.generateNoise(chunkX, chunkZ);
								if(currentVal != 1.0D) {
									System.out.println(currentVal);
								}
								generator.replaceBiomeBlocks(chunkX * 16 + x, chunkZ * 16 + z, x, z, baseBlockNoise, this.rand, this.seed, primer, this, biomesIn, currentVal);
							} else {
								biome.genTerrainBlocks(this.worldObj, this.rand, primer, chunkX * 16 + x, chunkZ * 16 + z, baseBlockNoise);
							}
							
							currentVal += stepZAxis;
						}

						currentValXAxisXC += stepXAxisXC;
						currentValXAxisXN += stepXAxisXN;
					}
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void populate(int x, int z) {

	}

	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false; //No structures
	}


	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return ImmutableList.of(); //Spawning is handled by MobSpawnHandler
	}


	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
		return null; //No strongholds
	}


	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		//No structures
	}
}
