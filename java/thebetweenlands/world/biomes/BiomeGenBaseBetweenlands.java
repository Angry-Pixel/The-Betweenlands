package thebetweenlands.world.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.BiomeNoiseFeature;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 *
 * @author The Erebus Team + TCB
 *
 */
public class BiomeGenBaseBetweenlands
extends BiomeGenBase
{
	protected final BiomeDecoratorBaseBetweenlands decorator;
	protected int grassColor, foliageColor;
	protected byte[] fogColorRGB = new byte[]{(byte) 255, (byte) 255, (byte) 255};
	protected Block bottomBlock = Blocks.bedrock;
	protected Block underLayerTopBlock, baseBlock;
	protected int bottomBlockHeight = 0;
	protected int bottomBlockFuzz = 5;
	protected boolean hasBaseBlockPatches = true;
	protected byte topBlockMeta = 0, fillerBlockMeta = 0, baseBlockMeta = 0, topBlockUnderLayerMeta = 0, bottomBlockMeta = 0;
	protected byte fillerBlockHeight = 2;
	protected boolean isNoiseGenInitialized = false;
	protected List<BiomeNoiseFeature> featureList = new ArrayList<BiomeNoiseFeature>();

	/**
	 * Creates a new Betweenlands biome.
	 *
	 * @param biomeID   int
	 * @param decorator BiomeDecoratorBaseBetweenlands
	 */
	public BiomeGenBaseBetweenlands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID);
		this.decorator = decorator;
		this.fillerBlock = Blocks.dirt;
		this.topBlock = Blocks.grass;
		this.baseBlock = Blocks.stone;
		this.underLayerTopBlock = Blocks.dirt;
	}

	/**
	 * Adds a generation feature to this biome.
	 * @param feature BiomeFeatureBaseBetweenlands
	 * @return
	 */
	protected final BiomeGenBaseBetweenlands addFeature(BiomeNoiseFeature feature) {
		this.featureList.add(feature);
		return this;
	}
	
	/**
	 * Sets the filler block height
	 *
	 * @param fillerBlockHeight byte
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setFillerBlockHeight(byte fillerBlockHeight) {
		this.fillerBlockHeight = fillerBlockHeight;
		return this;
	}

	/**
	 * Sets the biome main blocks.
	 *
	 * @param baseBlock          Block
	 * @param fillerBlock        Block
	 * @param topBlock           Block
	 * @param underLayerTopBlock Block
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBlocks(Block baseBlock, Block fillerBlock, Block topBlock, Block underLayerTopBlock, Block bottomBlock) {
		this.baseBlock = baseBlock;
		this.fillerBlock = fillerBlock;
		this.topBlock = topBlock;
		this.underLayerTopBlock = underLayerTopBlock;
		this.bottomBlock = bottomBlock;
		return this;
	}

	/**
	 * Sets the block meta data.
	 *
	 * @param topBlockMeta    byte
	 * @param fillerBlockMeta byte
	 * @param baseBlockMeta   byte
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBlockMeta(byte topBlockMeta, byte fillerBlockMeta, byte baseBlockMeta, byte topBlockUnderLayerMeta, byte bottomBlockMeta) {
		this.topBlockMeta = topBlockMeta;
		this.baseBlockMeta = baseBlockMeta;
		this.fillerBlockMeta = fillerBlockMeta;
		this.topBlockUnderLayerMeta = topBlockUnderLayerMeta;
		this.bottomBlockMeta = bottomBlockMeta;
		return this;
	}

	/**
	 * If set to true, the biome will occasionally generate patches of the base block without replacing
	 * it with the topBlock or fillerBlock.
	 *
	 * @param enabled boolean
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBaseBlockPatches(boolean enabled) {
		this.hasBaseBlockPatches = enabled;
		return this;
	}

	/**
	 * Sets the bottom block layer height. Fuzz defines how far above it can start to generate randomly.
	 *
	 * @param height int
	 * @param fuzz   int
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBottomBlockHeight(int height, int fuzz) {
		this.bottomBlockHeight = height;
		this.bottomBlockFuzz = fuzz;
		return this;
	}

	/**
	 * Sets the average biome height and height variation.
	 *
	 * @param height    int
	 * @param variation int
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setHeightAndVariation(int height, int variation) {
		this.heightVariation = (float) variation / 128.0f;
		this.rootHeight = ((float) height / 128.0f + this.heightVariation) * 4.0f - 2.0f;
		return this;
	}

	/**
	 * Sets the biome grass and foliage colors.
	 *
	 * @param grassColor   int
	 * @param foliageColor int
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setColors(int grassColor, int foliageColor) {
		this.grassColor = grassColor;
		this.foliageColor = foliageColor;
		return this;
	}

	/**
	 * Sets the biome fog color.
	 *
	 * @param red   short
	 * @param green short
	 * @param blue  short
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setFogColor(byte red, byte green, byte blue) {
		this.fogColorRGB[0] = red;
		this.fogColorRGB[1] = green;
		this.fogColorRGB[2] = blue;
		return this;
	}

	@Override
	/**
	 * Decorates the biome, called after BiomeGenBaseBetweenlands#populate.
	 * Used to generate features such as trees.
	 */
	public void decorate(World world, Random rand, int x, int z) {
		this.decorator.decorate(world, rand, x, z);
	}

	/**
	 * Populates the biome, called before BiomeGenBasebetweenlands#decorate.
	 * Used to generate features such as lakes.
	 */
	public void populate(World world, Random rand, int x, int z) {
		this.decorator.populate(world, rand, x, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Returns the biome grass color.
	 * @return int
	 */
	public final int getBiomeGrassColor(int x, int y, int z) {
		return this.grassColor;
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Returns the biome foliage color.
	 * @return int
	 */
	public final int getBiomeFoliageColor(int x, int y, int z) {
		return this.foliageColor;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Returns the fog RGB color.
	 * @return byte[3]
	 */
	public final byte[] getFogRGB() {
		return this.fogColorRGB;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Returns the distance where the fog starts to build up.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	public float getFogStart(float farPlaneDistance) {
		return 10;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Returns the distance where the fog is fully opaque.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	public float getFogEnd(float farPlaneDistance) {
		return farPlaneDistance / 1.25f;
	}

	/**
	 * Returns true if the noise generators have been initialized already.
	 * @return boolean
	 */
	public final boolean isNoiseGenInitialized() {
		return this.isNoiseGenInitialized;
	}

	/**
	 * Initializes additional noise generators.
	 * @param rng Seeded Random
	 */
	public final void initializeNoiseGen(Random rng) {
		this.isNoiseGenInitialized = true;
		for(BiomeNoiseFeature feature : this.featureList) {
			feature.initializeNoiseGen(rng, this);
		}
	}
	
	/**
	 * Generates the noise fields.
	 * @param chunkX
	 * @param chunkZ
	 */
	public final void generateNoise(int chunkX, int chunkZ) { 
		for(BiomeNoiseFeature feature : this.featureList) {
			feature.generateNoise(chunkX, chunkZ, this);
		}
	}

	/**
	 * Returns the base block of this biome
	 * @return Block
	 */
	public final Block getBaseBlock() {
		return this.baseBlock;
	}

	/**
	 * Returns the under layer block of this biome
	 * @return Block
	 */
	public final Block getUnderLayerBlock() {
		return this.underLayerTopBlock;
	}
	
	protected Block[] currentChunkBlocks;
	protected byte[] currentChunkMeta;
	protected ChunkProviderBetweenlands currentProvider;
	protected BiomeGenBase[] currentBiomesForGeneration;
	
	/**
	 * Replaces the default terrain gen blocks with the blocks of this biome.
	 *
	 * @param blockX              int                         Block X coordinate in world
	 * @param blockZ              int                         Block Z coordinate in world
	 * @param inChunkX            int                         Block X coordinate in chunk
	 * @param inChunkZ            int                         Block Z coordinate in chunk
	 * @param baseBlockNoise      double
	 * @param rng                 Random
	 * @param chunkBlocks         Block[]
	 * @param blockMeta           byte[]
	 * @param chunkProvider       ChunkProviderBetweenlands
	 * @param currentBiome        BiomeGenBaseBetweenlands
	 * @param biomesForGeneration BiomeGenBase[]
	 */
	public final void replaceStackBlocks(int blockX, int blockZ, int inChunkX, int inChunkZ, 
			double baseBlockNoise, Random rng, Block[] chunkBlocks, byte[] blockMeta, 
			ChunkProviderBetweenlands chunkProvider, BiomeGenBase[] biomesForGeneration) {
		this.currentChunkBlocks = chunkBlocks;
		this.currentChunkMeta = blockMeta;
		this.currentProvider = chunkProvider;
		this.currentBiomesForGeneration = biomesForGeneration;
		
		for(BiomeNoiseFeature feature : this.featureList) {
			feature.preReplaceStackBlocks(inChunkX, inChunkZ, this.currentChunkBlocks, this.currentChunkMeta, this, chunkProvider);
		}
		
		if(!this.preReplaceStackBlocks(blockX, blockZ, inChunkX, inChunkZ)) {
			return;
		}
		
		//Chunk width * height
		int sliceSize = chunkBlocks.length / 256;

		//Random number for base block patch generation based on the base block noise
		int baseBlockNoiseRN = (int) (baseBlockNoise / 3.0D + 3.0D + rng.nextDouble() * 0.25D);

		//Amount of blocks below the surface
		int blocksBelow = -1;
		
		for( int y = 255; y >= 0; --y ) {
			//Block array index of the current x, y, z position
			int cIndex = this.getBlockArrayIndex(inChunkX, y, inChunkZ, sliceSize);

			//Generate bottom block
			if( y <= this.bottomBlockHeight + rng.nextInt(this.bottomBlockFuzz) ) {
				chunkBlocks[cIndex] = this.bottomBlock;
				blockMeta[cIndex] = this.bottomBlockMeta;
				continue;
			}

			//Block of the current x, y, z position
			Block currentBlock = chunkBlocks[cIndex];

			//Block is either null, air or the layer block
			if( currentBlock == null || currentBlock.getMaterial() == Material.air ||
					currentBlock == chunkProvider.layerBlock ) {
				blocksBelow = -1;
				continue;
			} else {
				blocksBelow++;
			}

			//Generate base block patch
			if( this.hasBaseBlockPatches && baseBlockNoiseRN <= 0 ) {
				chunkBlocks[cIndex] = this.baseBlock;
				blockMeta[cIndex] = this.baseBlockMeta;
				return;
			}

			//Block above current block
			int aboveIndex = this.getBlockArrayIndex(inChunkX, y + 1, inChunkZ, sliceSize);
			Block blockAbove = chunkBlocks[aboveIndex];

			if( blocksBelow == 0 ) {
				if( blockAbove == chunkProvider.layerBlock ) {
					//Generate under layer top block
					chunkBlocks[cIndex] = this.underLayerTopBlock;
					blockMeta[cIndex] = this.topBlockUnderLayerMeta;
				} else {
					//Generate top block
					chunkBlocks[cIndex] = this.topBlock;
					blockMeta[cIndex] = this.topBlockMeta;
				}
			} else if( blocksBelow > 0 && blocksBelow <= this.fillerBlockHeight ) {
				//Generate filler block
				chunkBlocks[cIndex] = this.fillerBlock;
				blockMeta[cIndex] = this.fillerBlockMeta;
			} else if( currentBlock == chunkProvider.baseBlock ) {
				//Generate base block
				chunkBlocks[cIndex] = this.baseBlock;
				blockMeta[cIndex] = this.baseBlockMeta;
			}
		}
		
		for(BiomeNoiseFeature feature : this.featureList) {
			feature.postReplaceStackBlocks(inChunkX, inChunkZ, this.currentChunkBlocks, this.currentChunkMeta, this);
		}
		
		this.postReplaceStackBlocks(blockX, blockZ, inChunkX, inChunkZ);
	}
	
	/**
	 * Called before the base terrain blocks are replaced with the blocks of this biome. Return false to cancel block replacement.
	 * @param blockX
	 * @param blockZ
	 * @param inChunkX
	 * @param inChunkZ
	 * @return boolean
	 */
	protected boolean preReplaceStackBlocks(int blockX, int blockZ, int inChunkX, int inChunkZ) {
		return true;
	}
	
	/**
	 * Called after the base terrain blocks have been replaced with the blocks of this biome.
	 * @param blockX
	 * @param blockZ
	 * @param inChunkX
	 * @param inChunkZ
	 */
	protected void postReplaceStackBlocks(int blockX, int blockZ, int inChunkX, int inChunkZ) { }

	/**
	 * Returns the index in the block array.
	 * @param x
	 * @param y
	 * @param z
	 * @param sliceSize
	 * @return int
	 */
	public static int getBlockArrayIndex(int x, int y, int z, int sliceSize) {
		return (z * 16 + x) * sliceSize + y;
	}
}
