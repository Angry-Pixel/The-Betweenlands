package thebetweenlands.common.world.gen.biome.feature;


public class ErodedMarshFeature extends MarshFeature {

	@Override
	public void generateNoise(int chunkX, int chunkZ, int biome) {
		this.islandNoise = this.islandNoiseGen.getRegion(this.islandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 0.5D, 0.5D, 1.0D);
		this.fuzzNoise = this.fuzzNoiseGen.getRegion(this.fuzzNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, 100.5D, 100.5D, 1.0D);
	}

}
