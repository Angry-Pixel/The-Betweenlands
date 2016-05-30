package thebetweenlands.world.biomes.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.EntityFirefly;
import thebetweenlands.entities.mobs.EntityPeatMummy;
import thebetweenlands.entities.mobs.EntityPyrad;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.utils.IWeightProvider;
import thebetweenlands.world.ChunkProviderBetweenlands;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.base.BiomeNoiseFeature;
import thebetweenlands.world.biomes.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.EventSpawnEntry;
import thebetweenlands.world.biomes.spawning.spawners.LocationSpawnEntry;
import thebetweenlands.world.storage.chunk.storage.location.EnumLocationType;

/**
 *
 * @author The Erebus Team + TCB
 *
 */
public abstract class BiomeGenBaseBetweenlands extends BiomeGenBase implements IWeightProvider {
	protected final BiomeDecoratorBaseBetweenlands decorator;
	protected int grassColor, foliageColor;
	protected int[] fogColorRGB = new int[]{(int) 255, (int) 255, (int) 255};
	protected Block bottomBlock = Blocks.bedrock;
	protected Block underLayerTopBlock, baseBlock;
	protected int bottomBlockHeight = 0;
	protected int bottomBlockFuzz = 5;
	protected boolean hasBaseBlockPatches = true;
	protected byte topBlockMeta = 0, fillerBlockMeta = 0, baseBlockMeta = 0, topBlockUnderLayerMeta = 0, bottomBlockMeta = 0;
	protected byte fillerBlockHeight = 2;
	protected byte underLayerBlockHeight = 2;
	protected boolean isNoiseGenInitialized = false;
	protected List<BiomeNoiseFeature> featureList = new ArrayList<BiomeNoiseFeature>();
	protected List<BLSpawnEntry> blSpawnEntries = new ArrayList<BLSpawnEntry>();
	private short biomeWeight;

	/**
	 * Creates a new Betweenlands biome.
	 *
	 * @param biomeID   int
	 * @param decorator BiomeDecoratorBaseBetweenlands
	 */
	public BiomeGenBaseBetweenlands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID);
		this.decorator = decorator;
		this.fillerBlock = BLBlockRegistry.swampDirt;
		this.topBlock = BLBlockRegistry.swampGrass;
		this.baseBlock = BLBlockRegistry.betweenstone;
		this.underLayerTopBlock = Blocks.dirt;
		this.spawnableCaveCreatureList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableMonsterList.clear();
		this.spawnableWaterCreatureList.clear();
		//this.setDisableRain();
		this.setTemperatureRainfall(2.0f, 0.0f);

		this.blSpawnEntries.add(new EventSpawnEntry(EntityFirefly.class, (short) 150, "bloodSky").setSpawnCheckRadius(32.0D));
		this.blSpawnEntries.add(new EventSpawnEntry(EntitySwampHag.class, (short) 175, "bloodSky").setHostile(true));
		this.blSpawnEntries.add(new EventSpawnEntry(EntityPeatMummy.class, (short) 65, "bloodSky").setHostile(true).setSpawnCheckRadius(20.0D));
		this.blSpawnEntries.add(new LocationSpawnEntry(EntityPyrad.class, (short) 120, EnumLocationType.GIANT_TREE).setHostile(true).setSpawnCheckRadius(26.0D).setSpawningInterval(500));
	}

	/**
	 * Returns the BL spawn entries
	 * @return
	 */
	public List<BLSpawnEntry> getSpawnEntries() {
		return this.blSpawnEntries;
	}

	/**
	 * Adds a generation feature to this biome.
	 * @param feature BiomeFeatureBaseBetweenlands
	 * @return
	 */
	public final BiomeGenBaseBetweenlands addFeature(BiomeNoiseFeature feature) {
		this.featureList.add(feature);
		return this;
	}

	/**
	 * Sets the filler block height
	 *
	 * @param fillerBlockHeight byte
	 * @return BiomeGenBaseBetweenlands
	 */
	public final BiomeGenBaseBetweenlands setFillerBlockHeight(byte fillerBlockHeight) {
		this.fillerBlockHeight = fillerBlockHeight;
		return this;
	}

	/**
	 * Sets the under layer block height
	 *
	 * @param underLayerBlockHeight byte
	 * @return BiomeGenBaseBetweenlands
	 */
	public final BiomeGenBaseBetweenlands setUnderLayerBlockHeight(byte underLayerBlockHeight) {
		this.underLayerBlockHeight = underLayerBlockHeight;
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
	public final BiomeGenBaseBetweenlands setBlocks(Block baseBlock, Block fillerBlock, Block topBlock, Block underLayerTopBlock, Block bottomBlock) {
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
	public final BiomeGenBaseBetweenlands setBlockMeta(byte topBlockMeta, byte fillerBlockMeta, byte baseBlockMeta, byte topBlockUnderLayerMeta, byte bottomBlockMeta) {
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
	public final BiomeGenBaseBetweenlands setBaseBlockPatches(boolean enabled) {
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
	public final BiomeGenBaseBetweenlands setBottomBlockHeight(int height, int fuzz) {
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
	public final BiomeGenBaseBetweenlands setHeightAndVariation(int height, int variation) {
		this.heightVariation = (float) variation / 128.0f;
		this.rootHeight = ((float) height / 128.0f + this.heightVariation) * 4.0f - 2.0f;
		return this;
	}

	//public abstract int getRootHeight(int x, int z);
	//public abstract int getHeightVariation(int x, int z);

	/**
	 * Sets the biome grass and foliage colors.
	 *
	 * @param grassColor   int
	 * @param foliageColor int
	 * @return BiomeGenBaseBetweenlands
	 */
	public final BiomeGenBaseBetweenlands setColors(int grassColor, int foliageColor) {
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
	public final BiomeGenBaseBetweenlands setFogColor(byte red, byte green, byte blue) {
		this.fogColorRGB[0] = red;
		this.fogColorRGB[1] = green;
		this.fogColorRGB[2] = blue;
		return this;
	}

	/**
	 * Decorates the biome, called after BiomeGenBaseBetweenlands#populate.
	 * Used to generate features such as trees.
	 */
	public void postChunkPopulate(World world, Random rand, int x, int z) {
		this.decorator.postChunkPopulate(world, rand, x, z);
	}

	/**
	 * Generates additional biome content after decoration and population
	 * Called from an IWorldGenerator.
	 */
	public void postChunkGen(World world, Random rand, int x, int z) {
		this.decorator.postChunkGen(world, rand, x, z);
	}

	/**
	 * Called before the chunk is provided.
	 * @param world
	 * @param rand
	 * @param chunkX
	 * @param chunkZ
	 * @param blocks
	 * @param metadata
	 * @param biomes
	 */
	public final void preChunkProvide(World world, Random rand, int chunkX, int chunkZ, Block[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
		this.decorator.preChunkProvide(world, rand, chunkX, chunkZ, blocks, metadata, biomes);
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
	public int[] getFogRGB() {
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
		if(this.baseBlockLayerVariationNoiseGen == null) {
			this.baseBlockLayerVariationNoiseGen = new NoiseGeneratorPerlin(rng, 4);
		}
		this.isNoiseGenInitialized = true;
		for(BiomeNoiseFeature feature : this.featureList) {
			feature.initializeNoiseGen(rng, this);
		}
		this.initializeNoiseGenBiome(rng);
	}


	/**
	 * Initializes additional noise generators. Can be overridden by the biome.
	 * @param rng Seeded Random
	 */
	protected void initializeNoiseGenBiome(Random rng) { }

	/**
	 * Generates the noise fields.
	 * @param chunkX
	 * @param chunkZ
	 */
	public final void generateNoise(int chunkX, int chunkZ) { 
		this.baseBlockLayerVariationNoise = this.baseBlockLayerVariationNoiseGen.func_151599_a(this.baseBlockLayerVariationNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
		for(BiomeNoiseFeature feature : this.featureList) {
			feature.generateNoise(chunkX, chunkZ, this);
		}
		this.generateNoiseBiome(chunkX, chunkZ);
	}


	/**
	 * Generates the noise fields. Can be overridden by the biome.
	 * @param chunkX
	 * @param chunkZ
	 */
	protected void generateNoiseBiome(int chunkX, int chunkZ) { }

	/**
	 * Returns the base block of this biome
	 * @return Block
	 */
	public Block getBaseBlock(int y) {
		return y > WorldProviderBetweenlands.PITSTONE_HEIGHT ? this.baseBlock : BLBlockRegistry.pitstone;
	}

	/**
	 * Returns the base block meta of this biome
	 * @return Block
	 */
	public byte getBaseBlockMeta(int y) {
		return y > WorldProviderBetweenlands.PITSTONE_HEIGHT ? this.baseBlockMeta : 0;
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
	private NoiseGeneratorPerlin baseBlockLayerVariationNoiseGen;
	private double[] baseBlockLayerVariationNoise = new double[256];

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
			ChunkProviderBetweenlands chunkProvider, BiomeGenBase[] biomesForGeneration,
			World world) {
		this.currentChunkBlocks = chunkBlocks;
		this.currentChunkMeta = blockMeta;
		this.currentProvider = chunkProvider;
		this.currentBiomesForGeneration = biomesForGeneration;

		for(BiomeNoiseFeature feature : this.featureList) {
			feature.currentBiomesForGeneration = biomesForGeneration;
			feature.currentProvider = chunkProvider;
			feature.currentRNG = rng;
			feature.setChunkAndWorld((blockX-(blockX&15))/16, (blockZ-(blockZ&15))/16, world);
			feature.preReplaceStackBlocks(inChunkX, inChunkZ, this.currentChunkBlocks, this.currentChunkMeta, this, chunkProvider, biomesForGeneration, rng);
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
		//Amount of blocks below the first block under the layer
		int blocksBelowLayer = -1;

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
					currentBlock == chunkProvider.layerBlock) {
				blocksBelow = -1;
				continue;
			} else {
				blocksBelow++;
			}

			if(currentBlock != chunkProvider.baseBlock) {
				continue;
			}

			int baseBlockVariationLayer = (int) (Math.abs(this.baseBlockLayerVariationNoise[inChunkX * 16 + inChunkZ] * 0.7F));
			int layerBlockY = y - baseBlockVariationLayer;
			if(layerBlockY < 0) {
				layerBlockY = 0;
			}

			//Generate base block patch
			if( this.hasBaseBlockPatches && baseBlockNoiseRN <= 0 ) {
				chunkBlocks[cIndex] = this.getBaseBlock(layerBlockY);
				blockMeta[cIndex] = this.getBaseBlockMeta(layerBlockY);
				return;
			}

			//Block above current block
			int aboveIndex = this.getBlockArrayIndex(inChunkX, y + 1, inChunkZ, sliceSize);
			Block blockAbove = chunkBlocks[aboveIndex];

			if(blocksBelowLayer >= 0) {
				blocksBelowLayer++;
			}
			if(currentBlock == chunkProvider.baseBlock && blockAbove == chunkProvider.layerBlock) {
				blocksBelowLayer++;
			}

			if(blocksBelowLayer <= this.underLayerBlockHeight && blocksBelowLayer >= 0) {
				//Generate under layer top block
				chunkBlocks[cIndex] = this.underLayerTopBlock;
				blockMeta[cIndex] = this.topBlockUnderLayerMeta;
			}  else if( blocksBelow == 0 && currentBlock == chunkProvider.baseBlock) {
				//Generate top block
				chunkBlocks[cIndex] = this.topBlock;
				blockMeta[cIndex] = this.topBlockMeta;
			} else if( blocksBelow > 0 && blocksBelow <= this.fillerBlockHeight && currentBlock == chunkProvider.baseBlock) {
				//Generate filler block
				chunkBlocks[cIndex] = this.fillerBlock;
				blockMeta[cIndex] = this.fillerBlockMeta;
			} else if( currentBlock == chunkProvider.baseBlock) {
				//Generate base block
				chunkBlocks[cIndex] = this.getBaseBlock(layerBlockY);
				blockMeta[cIndex] = this.getBaseBlockMeta(layerBlockY);
			}
		}

		for(BiomeNoiseFeature feature : this.featureList) {
			feature.postReplaceStackBlocks(inChunkX, inChunkZ, this.currentChunkBlocks, this.currentChunkMeta, this, chunkProvider, biomesForGeneration, rng);
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

	/**
	 * Sets Biome specific weighted probability.
	 * @param weight
	 */
	protected final BiomeGenBaseBetweenlands setWeight(int weight) {
		if (biomeWeight != 0)
			throw new RuntimeException("Cannot set biome weight twice!");
		biomeWeight = (short) weight;
		if (getClass().getGenericSuperclass() == BiomeGenBaseBetweenlands.class)
			BLBiomeRegistry.biomeList.add(this); // add to list once weight is known
		return this;
	}

	/**
	 * Returns Biome specific weighted probability.
	 */
	@Override
	public final short getWeight() {
		return biomeWeight;
	}


	@Override
	public void addDefaultFlowers() {
		addFlower(BLBlockRegistry.boneset, 0, 10);
		addFlower(BLBlockRegistry.marshMallow, 0, 10);
		addFlower(BLBlockRegistry.nettle, 0, 10);
		addFlower(BLBlockRegistry.nettleFlowered, 0, 10);
		addFlower(BLBlockRegistry.buttonBush, 0, 10);
		addFlower(BLBlockRegistry.milkweed, 0, 10);
		addFlower(BLBlockRegistry.copperIris, 0, 10);
		addFlower(BLBlockRegistry.blueIris, 0, 10);
		addFlower(BLBlockRegistry.waterFlower, 0, 10);
		addFlower(BLBlockRegistry.marshHibiscus, 0, 10);
		addFlower(BLBlockRegistry.pickerelWeed, 0, 10);
	}

}
