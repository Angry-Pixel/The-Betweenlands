package thebetweenlands.world.feature.gen;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;

import java.util.Random;

public class MapGenCavesBetweenlandsNew extends MapGenBase {
	private NoiseGeneratorOctaves caveNoiseGen;
	private double[] caveNoise = new double[16*16];

	private NoiseGeneratorOctaves caveNoiseGenY;
	private double[] caveNoiseY = new double[60];
	
	@Override
	public void func_151539_a(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, Block[] blockArray) {
		/*if(this.caveNoiseGen == null) {
			this.caveNoiseGen = new NoiseGeneratorOctaves(new Random(world.getSeed()), 3);
		}
		int sliceSize = blockArray.length / 256;
		this.caveNoise = this.caveNoiseGen.generateNoiseOctaves(this.caveNoise, chunkX * 16, 0, chunkZ * 16, 16, 60, 16, 0.1D, 0.3D, 0.1D);
		for(int zo = 0; zo < 16; zo++) {
			for(int xo = 0; xo < 16; xo++) {
				for(int yo = 0; yo < 80; yo++) {
					int noiseIndex = zo*16*60 + xo*60 + (int)Math.floor(((float)yo / 80.0F * 60.0F));
					double noise = Math.abs(this.caveNoise[noiseIndex] - 0.65D);
					int bx = xo;
					int by = yo;
					int bz = zo;
					if(noise <= 0.5D) {
						blockArray[BiomeGenBaseBetweenlands.getBlockArrayIndex(bx, by, bz, sliceSize)] = Blocks.air;
					}
				}
			}
		}*/
		if(this.caveNoiseGen == null) {
			this.caveNoiseGen = new NoiseGeneratorOctaves(new Random(world.getSeed()), 3);
		}
		if(this.caveNoiseGenY == null) {
			this.caveNoiseGenY = new NoiseGeneratorOctaves(new Random(world.getSeed()), 3);
		}
		int sliceSize = blockArray.length / 256;
		this.caveNoise = this.caveNoiseGen.generateNoiseOctaves(this.caveNoise, chunkX * 16, 0, chunkZ * 16, 16, 1, 16, 0.1D, 0.3D, 0.1D);
		this.caveNoiseY = this.caveNoiseGenY.generateNoiseOctaves(this.caveNoiseY, chunkX * 16, 0, chunkZ * 16, 1, 60, 1, 0.1D, 0.3D, 0.1D);
		for(int zo = 0; zo < 16; zo++) {
			for(int xo = 0; xo < 16; xo++) {
				for(int yo = 0; yo < 80; yo++) {
					int noiseIndex = zo*16 + xo;
					double yNoise = this.caveNoiseY[(int)Math.floor(((float)yo / 80.0F * 60.0F))];
					double noise = Math.abs(this.caveNoise[noiseIndex] + yNoise - 0.65D);
					int bx = xo;
					int by = yo;
					int bz = zo;
					if(noise <= 0.5D) {
						blockArray[BiomeGenBaseBetweenlands.getBlockArrayIndex(bx, by, bz, sliceSize)] = Blocks.air;
					}
				}
			}
		}
	}
}
