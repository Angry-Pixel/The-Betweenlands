package thebetweenlands.world.biomes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorBaseBetweenlands;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author The Erebus Team + TCB
 *
 */
public class BiomeGenBaseBetweenlands extends BiomeGenBase {

	private final BiomeDecoratorBaseBetweenlands decorator;
	private int grassColor, foliageColor;
	private short[] fogColorRGB = new short[] {255, 255, 255};
	private Block bottomBlock = Blocks.bedrock;
	private Block underLayerTopBlock, baseBlock;
	private int bottomBlockHeight = 0;
	private int bottomBlockFuzz = 5;
	private boolean hasBaseBlockPatches = false;
	private byte topBlockMeta = 0, fillerBlockMeta = 0, baseBlockMeta = 0, topBlockUnderLayerMeta = 0;
	private byte fillerBlockHeight = 2;
	
	/**
	 * Creates a new Betweenlands biome.
	 * @param biomeID int
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
	 * Sets the filler block height
	 * @param fillerBlockHeight byte
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setFillerBlockHeight(byte fillerBlockHeight) {
		this.fillerBlockHeight = fillerBlockHeight;
		return this;
	}
	
	/**
	 * Sets the biome main blocks.
	 * @param baseBlock Block
	 * @param fillerBlock Block
	 * @param topBlock Block
	 * @param underLayerTopBlock Block
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBlocks(Block baseBlock, Block fillerBlock, Block topBlock, Block underLayerTopBlock) {
		this.baseBlock = baseBlock;
		this.fillerBlock = fillerBlock;
		this.topBlock = topBlock;
		this.underLayerTopBlock = underLayerTopBlock;
		return this;
	}
	
	/**
	 * Sets the block meta data.
	 * @param topBlockMeta byte
	 * @param fillerBlockMeta byte
	 * @param baseBlockMeta byte
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBlockMeta(byte topBlockMeta, byte fillerBlockMeta, byte baseBlockMeta, byte topBlockUnderLayerMeta) {
		this.topBlockMeta = topBlockMeta;
		this.baseBlockMeta = baseBlockMeta;
		this.fillerBlockMeta = fillerBlockMeta;
		this.topBlockUnderLayerMeta = topBlockUnderLayerMeta;
		return this;
	}
	
	/**
	 * If set to true, the biome will occasionally generate patches of the base block without replacing
	 * it with the topBlock or fillerBlock.
	 * @param enabled boolean
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBaseBlockPatches(boolean enabled) {
		this.hasBaseBlockPatches = enabled;
		return this;
	}
	
	/**
	 * Sets the bottom block layer height. Fuzz defines how far above it can start to generate randomly.
	 * @param height int
	 * @param fuzz int
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBottomBlockHeight(int height, int fuzz) {
		this.bottomBlockHeight = height;
		this.bottomBlockFuzz = fuzz;
		return this;
	}
	
	/**
	 * Sets the bottom block. By default bedrock.
	 * @param block Block
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setBottomBlock(Block block) {
		this.bottomBlock = block;
		return this;
	}
	
	/**
	 * Sets the average biome height and height variation.
	 * @param height int
	 * @param variation int
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setHeightAndVariation(int height, int variation) {
		this.heightVariation = (float)variation / 128.0f;
		this.rootHeight = ((float)height / 128.0f + this.heightVariation) * 4.0f - 2.0f;
		return this;
	}
	
	/**
	 * Sets the biome grass and foliage colors.
	 * @param grassColor int
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
	 * @param red short
	 * @param green short
	 * @param blue short
	 * @return BiomeGenBaseBetweenlands
	 */
	protected final BiomeGenBaseBetweenlands setFogColor(short red, short green, short blue) {
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
	 * @param world
	 * @param rand
	 * @param x
	 * @param z
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
	 * @return short[3]
	 */
	public final short[] getFogRGB() {
		return this.fogColorRGB;
	}
	
	/**
	 * Replaces all blocks in the stack with the blocks of this biome.
	 * @param x int
	 * @param z int
	 * @param baseBlockNoise double
	 * @param rng Random
	 * @param chunkBlocks Block[]
	 * @param blockMeta byte[]
	 * @param chunkProvider ChunkProviderBetweenlands
	 * @param currentBiome BiomeGenBaseBetweenlands
	 * @param biomesForGeneration BiomeGenBase[]
	 */
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, Random rng, Block[] chunkBlocks, byte[] blockMeta, ChunkProviderBetweenlands chunkProvider, BiomeGenBaseBetweenlands currentBiome, BiomeGenBase[] biomesForGeneration) {
		//X and Z coordinates clamped to 0-15
		int cx = x & 15;
        int cz = z & 15;
        
        //Chunk width * height
        int sliceSize = chunkBlocks.length / 256;
        
        //Random number for base block patch generation based on the base block noise
        int baseBlockNoiseRN = (int)(baseBlockNoise / 3.0D + 3.0D + rng.nextDouble() * 0.25D);
        
        //Amount of blocks below the surface
        int blocksBelow = -1;
        
        //Iterate through XZ stack
		for(int y = 255; y >= 0; --y) {
			//Block array index of the current x, y, z position
			int cIndex = this.getBlockArrayIndex(cx, y, cz, sliceSize);
			
			//Generate bottom block
			if(y <= this.bottomBlockHeight + rng.nextInt(this.bottomBlockFuzz)) {
				chunkBlocks[cIndex] = this.bottomBlock;
				continue;
			}
			
			//Block of the current x, y, z position
			Block currentBlock = chunkBlocks[cIndex];
			
			//Block is either null or air
			if(currentBlock == null || currentBlock.getMaterial() == Material.air ||
					currentBlock == chunkProvider.layerBlock){
				blocksBelow = -1;
				continue;
			} else {
				blocksBelow++;
			}
			
			//Generate base block patch
			if(this.hasBaseBlockPatches && baseBlockNoiseRN <= 0) {
				chunkBlocks[cIndex] = this.baseBlock;
				blockMeta[cIndex] = this.baseBlockMeta;
				continue;
			}
			
			//Block above current block
			int aboveIndex = this.getBlockArrayIndex(cx, y + 1, cz, sliceSize);
			Block blockAbove = chunkBlocks[aboveIndex];
			
			if(blocksBelow == 0) {
				if(blockAbove == chunkProvider.layerBlock) {
					//Generate under layer top block
					chunkBlocks[cIndex] = this.underLayerTopBlock;
					blockMeta[cIndex] = this.topBlockUnderLayerMeta;
				} else {
					//Generate top block
					chunkBlocks[cIndex] = this.topBlock;
					blockMeta[cIndex] = this.topBlockMeta;
				}
			} else if(blocksBelow > 0 && blocksBelow <= this.fillerBlockHeight) {
				//Generate filler block
				chunkBlocks[cIndex] = this.fillerBlock;
				blockMeta[cIndex] = this.fillerBlockMeta;
			} else if(currentBlock == chunkProvider.baseBlock) {
				//Generate base block
				chunkBlocks[cIndex] = this.baseBlock;
				blockMeta[cIndex] = this.baseBlockMeta;
			}
		}
	}
	
	private int getBlockArrayIndex(int x, int y, int z, int sliceSize) {
		return (z * 16 + x) * sliceSize + y;
	}
}
