package thebetweenlands.world.genlayer;

import java.util.ArrayList;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import scala.util.Random;
import thebetweenlands.world.biomes.BiomeGenBaseBetweenlands;

public class GenLayerBetweenlandsBiome extends GenLayerBetweenlands {
	public static final ArrayList<BiomeGenBaseBetweenlands> biomesToGenerate = new ArrayList<BiomeGenBaseBetweenlands>();
	
	public GenLayerBetweenlandsBiome(long seed, GenLayer parentGenLayer) {
		super(seed);
		//biomesToGenerate = ModBiomes.biomeList;
		parent = parentGenLayer;
	}

	@Override
	public int[] getInts(int x, int z, int sizeX, int sizeZ) {
		parent.getInts(x, z, sizeX, sizeZ);
		int[] ints = IntCache.getIntCache(sizeX * sizeZ);
		for (int zz = 0; zz < sizeZ; ++zz)
			for (int xx = 0; xx < sizeX; ++xx) {
				initChunkSeed(xx + x, zz + z);
				ints[xx + zz * sizeX] = this.biomesToGenerate.get(this.nextInt(this.biomesToGenerate.size())).biomeID;
			}

		return ints;
	}
}
