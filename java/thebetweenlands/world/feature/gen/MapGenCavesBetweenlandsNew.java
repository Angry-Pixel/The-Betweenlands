package thebetweenlands.world.feature.gen;

import java.util.Random;

import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

public class MapGenCavesBetweenlandsNew extends MapGenBase {
	private NoiseGeneratorOctaves caveNoiseGen;
	private double[] caveNoise = new double[8448];

	@Override
	public void func_151539_a(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, Block[] blockArray) {
		//if(this.caveNoiseGen == null) {
			this.caveNoiseGen = new NoiseGeneratorOctaves(new Random(world.getSeed()), 2);
		//}
		int sliceSize = blockArray.length / 256;
		this.caveNoise = this.caveNoiseGen.generateNoiseOctaves(this.caveNoise, chunkX * 16, 0, chunkZ * 16, 16, 33, 16, 0.1D, 0.05D, 0.1D);
		for(int zo = 0; zo < 16; zo++) {
			for(int xo = 0; xo < 16; xo++) {
				for(int yo = 0; yo < 80; yo++) {
					int noiseIndex = zo*16*33 + xo*33 + (int)Math.floor(((float)yo / 80.0F * 33.0F));
					double noise = this.caveNoise[noiseIndex];
					int bx = xo;
					int by = yo;
					int bz = zo;
					if(noise <= 0.08D) {
						blockArray[BiomeGenBaseBetweenlands.getBlockArrayIndex(bx, by, bz, sliceSize)] = Blocks.air;
					}
				}
			}
		}
	}
}
