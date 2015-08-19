package thebetweenlands.world.biomes;

import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.mobs.*;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.biomes.decorators.BiomeDecoratorCoarseIslands;
import thebetweenlands.world.biomes.decorators.base.BiomeDecoratorBaseBetweenlands;
import thebetweenlands.world.biomes.feature.AlgaeNoiseFeature;
import thebetweenlands.world.biomes.feature.CoarseIslandNoiseFeature;
import thebetweenlands.world.biomes.feature.SiltNoiseFeature;

public class BiomeCoarseIslands
extends BiomeGenBaseBetweenlands
{
	public BiomeCoarseIslands(int biomeID) {
		this(biomeID, new BiomeDecoratorCoarseIslands());
	}
	
	public BiomeCoarseIslands(int biomeID, BiomeDecoratorBaseBetweenlands decorator) {
		super(biomeID, decorator);
		this.setFogColor((byte)10, (byte)30, (byte)12);
		setColors(0x314D31, 0x314D31);
		setWeight(20);
		this.setHeightAndVariation(WorldProviderBetweenlands.CAVE_START, 0);
		this.setBiomeName("Coarse Islands");
		this.setBlocks(BLBlockRegistry.betweenstone, BLBlockRegistry.swampDirt, BLBlockRegistry.swampGrass, BLBlockRegistry.mud, BLBlockRegistry.betweenlandsBedrock);
		this.setFillerBlockHeight((byte)1);
		this.addFeature(new CoarseIslandNoiseFeature())
		.addFeature(new SiltNoiseFeature())
		.addFeature(new AlgaeNoiseFeature());
		this.waterColorMultiplier = 0x1b3944;

		spawnableMonsterList.add(new SpawnListEntry(EntitySwampHag.class, 15, 1, 1));
		// spawnableMonsterList.add(new SpawnListEntry(EntityTarBeast.class, 15, 1, 1)); TODO
		spawnableMonsterList.add(new SpawnListEntry(EntityWight.class, 5, 0, 0));
		spawnableWaterCreatureList.add(new SpawnListEntry(EntityAngler.class, 20, 1, 2));
        spawnableMonsterList.add(new SpawnListEntry(EntityDragonFly.class, 25, 2, 4));
        spawnableMonsterList.add(new SpawnListEntry(EntityFirefly.class, 100, 10, 20));
    	spawnableWaterCreatureList.add(new SpawnListEntry(EntityLurker.class, 20, 1, 1));
		spawnableMonsterList.add(new SpawnListEntry(EntitySporeling.class, 100, 5, 8));
	}

	/*private NoiseGeneratorPerlin islandNoiseGen;
	private double[] islandNoise = new double[256];
	
	@Override
	protected void initializeNoiseGenBiome(Random rng) { 
		this.islandNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}
	
	@Override
	protected void generateNoiseBiome(int chunkX, int chunkZ) { 
		this.islandNoise = this.islandNoiseGen.func_151599_a(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.08D * 2.0D, 0.08D * 2.0D, 1.0D);
	}
	
	@Override
	public int getRootHeight(int x, int z) {
		return WorldProviderBetweenlands.LAYER_HEIGHT + 10;
	}
	
	@Override
	public int getHeightVariation(int x, int z) {
		int cx = x % 16;
		if(cx < 0) {
			cx = (16 + cx);
		}
		int cz = z % 16;
		if(cz < 0) {
			cz = (16 + cz);
		}
		
		//System.out.println(cx + " " + cz);
		
		double noise = this.islandNoise[cx * 16 + cz] / 1.4f + 1.8f;
		int layerHeight = WorldProviderBetweenlands.LAYER_HEIGHT;
		if(noise <= 0) {
			return 80;
		}
		return 0;
	}*/
}
