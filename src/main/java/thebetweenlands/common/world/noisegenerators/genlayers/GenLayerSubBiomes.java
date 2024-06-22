package thebetweenlands.common.world.noisegenerators.genlayers;


public class GenLayerSubBiomes extends GenLayerBetweenlands {

	private static final byte[] offsetX = new byte[]{0, 1, -1, 0, 0}, offsetZ = new byte[]{0, 0, 0, 1, -1};

	public GenLayerSubBiomes(InstancedIntCache cache, long seed, GenLayer parentGenLayer) {
		super(cache, seed);
		parent = parentGenLayer;
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ) {
		int[] currentBiomeInts = parent.getInts(x - 2, z - 2, sizeX + 4, sizeZ + 4);
		int[] biomeInts = this.cache.getIntCache(sizeX * sizeZ);

		for (int zz = 0; zz < sizeZ; ++zz) {
			for (int xx = 0; xx < sizeX; ++xx) {
				initChunkSeed(xx + x, zz + z);
				biomeInts[xx + zz * sizeX] = currentBiomeInts[xx + 2 + (zz + 2) * (sizeX + 4)];
			}
		}

		initChunkSeed(x, z);

		//TODO: Not sure why sub biomes were never implemented, we should definitely implement it in the future though
		/*for (int attempt = 0, xx, zz; attempt < 6; attempt++) {
			xx = 1 + nextInt(sizeX - 2);
			zz = 1 + nextInt(sizeZ - 2);

			int biomeID = currentBiomeInts[xx + 2 + (zz + 2) * (sizeX + 4)];

			BiomeGenBaseBetweenlands biome = (BiomeGenBaseBetweenlands) BiomeGenBase.getBiomeGenArray()[biomeID];
			BiomeGenBaseBetweenlands subBiome = biome.getRandomSubBiome(nextInt(101));

			if (subBiome != null && biome != subBiome)
				for (int a = 0, bx1, bx2, bz1, bz2, nx, nz; a < 5; a++) {
					nx = xx + offsetX[a];
					nz = zz + offsetZ[a];
					bz1 = currentBiomeInts[nx + 2 + (nz + 2 - 1) * (sizeX + 4)];
					bx1 = currentBiomeInts[nx + 2 + 1 + (nz + 2) * (sizeX + 4)];
					bx2 = currentBiomeInts[nx + 2 - 1 + (nz + 2) * (sizeX + 4)];
					bz2 = currentBiomeInts[nx + 2 + (nz + 2 + 1) * (sizeX + 4)];

					if (bx1 == biomeID && bx2 == biomeID && bz1 == biomeID && bz2 == biomeID && (a == 0 || nextInt(3) != 0)) {
						biomeInts[nx + nz * sizeX] = subBiome.biomeID;
						attempt = 999;
					} else if (a == 0)
						break;
				}
		}*/

		return biomeInts;
	}
}
