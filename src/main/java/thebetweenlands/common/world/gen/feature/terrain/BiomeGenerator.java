package thebetweenlands.common.world.gen.feature.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class BiomeGenerator {
	protected final Biome biome;

	protected IBlockState bottomBlockState = BlockRegistry.BETWEENLANDS_BEDROCK.getDefaultState();
	protected IBlockState underLayerTopBlockState, baseBlockState;

	protected int bottomBlockHeight = 0;
	protected int bottomBlockFuzz = 5;
	protected int fillerBlockHeight = 2;
	protected int underLayerBlockHeight = 2;

	protected boolean hasBaseBlockPatches = true;
	private NoiseGeneratorPerlin baseBlockLayerVariationNoiseGen;
	protected double[] baseBlockLayerVariationNoise = new double[256];
	private boolean isNoiseGenInitialized = false;

	private final List<BiomeFeature> biomeFeatures = new ArrayList<BiomeFeature>();

	public BiomeGenerator(Biome biome) {
		this.biome = biome;
	}

	/**
	 * Sets the bottom block state (e.g. bedrock)
	 * @param state
	 * @return
	 */
	public BiomeGenerator setBottomBlockState(IBlockState state) {
		this.bottomBlockState = state;
		return this;
	}

	/**
	 * Sets how far above 0 the bottom block should generate and how fuzzy the layer is
	 * @param height
	 * @param fuzz
	 * @return
	 */
	public BiomeGenerator setBottomBlockHeightAndFuzz(int height, int fuzz) {
		this.bottomBlockHeight = height;
		this.bottomBlockFuzz = fuzz;
		return this;
	}

	/**
	 * Sets the block state that generates right under the layer block (e.g. swamp water)
	 * @param state
	 * @return
	 */
	public BiomeGenerator setUnderLayerBlockState(IBlockState state) {
		this.underLayerTopBlockState = state;
		return this;
	}

	/**
	 * Sets how far below the layer the block should generate
	 * @param height
	 * @return
	 */
	public BiomeGenerator setUnderLayerBlockHeight(int height) {
		this.underLayerBlockHeight = height;
		return this;
	}

	/**
	 * Sets how far below the surface the filler blokc should generate
	 * @param height
	 * @return
	 */
	public BiomeGenerator setFillerBlockHeight(int height) {
		this.fillerBlockHeight = height;
		return this;
	}

	/**
	 * Sets whether the terrain should occasionally have base block patches
	 * @param hasPatches
	 * @return
	 */
	public BiomeGenerator setBaseBlockPatches(boolean hasPatches) {
		this.hasBaseBlockPatches = hasPatches;
		return this;
	}

	/**
	 * Initializes additional noise generators.
	 * @param rng Seeded Random
	 */
	public void initializeNoiseGenerators(Random rng) {
		if(this.baseBlockLayerVariationNoiseGen == null) {
			this.baseBlockLayerVariationNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		}
		this.isNoiseGenInitialized = true;
		for(BiomeFeature feature : this.biomeFeatures) {
			feature.initializeNoiseGenerators(rng, this.biome);
		}
	}

	/**
	 * Generates the noise fields.
	 * @param chunkX
	 * @param chunkZ
	 */
	public void generateNoise(int chunkX, int chunkZ) { 
		this.baseBlockLayerVariationNoise = this.baseBlockLayerVariationNoiseGen.getRegion(this.baseBlockLayerVariationNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
		for(BiomeFeature feature : this.biomeFeatures) {
			feature.generateNoise(chunkX, chunkZ, this.biome);
		}
	}

	/**
	 * Runs the stack primer at the specified stack.
	 * Modifies the terrain with biome and {@link BiomeFeature} specific features.
	 * @param blockX
	 * @param blockZ
	 * @param inChunkX
	 * @param inChunkZ
	 * @param baseBlockNoise
	 * @param rng
	 * @param chunkPrimer
	 * @param chunkProvider
	 * @param biomesForGeneration
	 * @param blockAccess
	 */
	public final void runStackPrimer(
			int blockX, int blockZ, int inChunkX, int inChunkZ, 
			double baseBlockNoise, Random rng, ChunkPrimer chunkPrimer, 
			/*ChunkProviderBetweenlands*/IChunkProvider chunkProvider, Biome[] biomesForGeneration,
			IBlockAccess blockAccess) {

		for(BiomeFeature feature : this.biomeFeatures) {
			feature.replaceStackBlocks(blockX, blockZ, inChunkX, inChunkZ, baseBlockNoise, rng, chunkPrimer, chunkProvider, biomesForGeneration, blockAccess, 0);
		}

		if(!this.replaceStackBlocks(blockX, blockZ, inChunkX, inChunkZ, baseBlockNoise, rng, chunkPrimer, chunkProvider, biomesForGeneration, blockAccess, 0)) {
			return;
		}

		//Random number for base block patch generation based on the base block noise
		int baseBlockNoiseRN = (int) (baseBlockNoise / 3.0D + 3.0D + rng.nextDouble() * 0.25D);

		//Amount of blocks below the surface
		int blocksBelow = -1;
		//Amount of blocks below the first block under the layer
		int blocksBelowLayer = -1;

		for( int y = 255; y >= 0; --y ) {
			//Generate bottom block
			if( y <= this.bottomBlockHeight + rng.nextInt(this.bottomBlockFuzz) ) {
				chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.bottomBlockState);
				continue;
			}

			//Block state of the current x, y, z position
			IBlockState currentBlockState = chunkPrimer.getBlockState(inChunkX, y, inChunkX);

			//Block is either null, air or the layer block
			if(currentBlockState == null || currentBlockState.getMaterial() == Material.AIR ||
					currentBlockState == chunkProvider.layerBlock) {
				blocksBelow = -1;
				continue;
			} else {
				blocksBelow++;
			}

			if(currentBlockState != chunkProvider.baseBlock) {
				continue;
			}

			int baseBlockVariationLayer = (int) (Math.abs(this.baseBlockLayerVariationNoise[inChunkX * 16 + inChunkZ] * 0.7F));
			int layerBlockY = y - baseBlockVariationLayer;
			if(layerBlockY < 0) {
				layerBlockY = 0;
			}

			//Generate base block patch
			if(this.hasBaseBlockPatches && baseBlockNoiseRN <= 0) {
				chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.getBaseBlockState(layerBlockY));
				return;
			}

			//Block above current block
			IBlockState blockAboveState = chunkPrimer.getBlockState(inChunkX, y + 1, inChunkZ);

			if(blocksBelowLayer >= 0) {
				blocksBelowLayer++;
			}
			if(currentBlockState == chunkProvider.baseBlock && blockAboveState == chunkProvider.layerBlock) {
				blocksBelowLayer++;
			}

			if(blocksBelowLayer <= this.underLayerBlockHeight && blocksBelowLayer >= 0) {
				//Generate under layer top block
				chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.underLayerTopBlockState);
			}  else if(blocksBelow == 0 && currentBlockState == chunkProvider.baseBlock) {
				//Generate top block
				chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.biome.topBlock);
			} else if(blocksBelow > 0 && blocksBelow <= this.fillerBlockHeight && currentBlockState == chunkProvider.baseBlock) {
				//Generate filler block
				chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.biome.fillerBlock);
			} else if(currentBlockState == chunkProvider.baseBlock) {
				//Generate base block
				chunkPrimer.setBlockState(inChunkX, y, inChunkZ, this.getBaseBlockState(layerBlockY));
			}
		}

		for(BiomeFeature feature : this.biomeFeatures) {
			feature.replaceStackBlocks(blockX, blockZ, inChunkX, inChunkZ, baseBlockNoise, rng, chunkPrimer, chunkProvider, biomesForGeneration, blockAccess, 1);
		}

		this.replaceStackBlocks(blockX, blockZ, inChunkX, inChunkZ, baseBlockNoise, rng, chunkPrimer, chunkProvider, biomesForGeneration, blockAccess, 1);
	}

	/**
	 * Returns the base block state of this biome
	 * @return Block
	 */
	public IBlockState getBaseBlockState(int y) {
		return y > WorldProviderBetweenlands.PITSTONE_HEIGHT ? this.baseBlockState : BlockRegistry.PITSTONE.getDefaultState();
	}

	/**
	 * Modifies the terrain at the specified block stack.
	 * <p><b>Note:</b> Do not generate outside of the specified block stack!
	 * @param blockX
	 * @param blockZ
	 * @param inChunkX
	 * @param inChunkZ
	 * @param baseBlockNoise
	 * @param rng
	 * @param chunkPrimer
	 * @param chunkProvider
	 * @param biomesForGeneration
	 * @param blockAccess
	 * @param pass
	 * @return
	 */
	protected boolean replaceStackBlocks(int blockX, int blockZ, int inChunkX, int inChunkZ, 
			double baseBlockNoise, Random rng, ChunkPrimer chunkPrimer, 
			/*ChunkProviderBetweenlands*/IChunkProvider chunkProvider, Biome[] biomesForGeneration,
			IBlockAccess blockAccess, int pass) {
		return true;
	}
}
