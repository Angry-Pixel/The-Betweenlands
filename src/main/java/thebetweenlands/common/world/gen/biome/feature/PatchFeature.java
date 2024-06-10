package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.BiomeWeights;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorFeature;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;
import thebetweenlands.common.world.noisegenerators.NoiseGeneratorPerlin;

/**
 * Generates block patches at the surface of the terrain
 */
public class PatchFeature extends DecoratorFeature {
	private NoiseGeneratorPerlin mudNoiseGen;
	private double[] mudNoise = new double[256];
	private double scaleX, scaleY;
	private double mult = 1.0D / 1.6D;
	private double offset = 1.5D;
	private BlockState block;

	public PatchFeature(double scaleX, double scaleY, BlockState block) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.block = block;
	}

	public PatchFeature(double scaleX, double scaleY, BlockState block, double mult, double offset) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.block = block;
		this.mult = mult;
		this.offset = offset;
	}

	@Override
	public void initializeGenerators(long seed, int biome) {
		Random rng = new Random(seed);
		this.mudNoiseGen = new NoiseGeneratorPerlin(rng, 4);
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, int biome) {
		this.mudNoise = this.mudNoiseGen.getRegion(this.mudNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 16, 16, this.scaleX, this.scaleY, 1.0D);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise,
								   ChunkAccess chunkPrimer, ChunkGeneratorBetweenlands chunkGenerator, int[] biomesForGeneration,
								   int biome, BiomeWeights biomeWeights, EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.POST_GEN_CAVES) {
			float biomeWeight = biomeWeights.get(x, z);
			if(this.mudNoise[x * 16 + z] * this.mult * biomeWeight + this.offset <= 0) {
				int y = TheBetweenlands.LAYER_HEIGHT + 20;
				for(int yo = 0; yo < TheBetweenlands.LAYER_HEIGHT + 20; yo++) {
					Block currentBlock = chunkPrimer.getBlockState(new BlockPos(x, y - yo, z)).getBlock();
					Block blockAbove = chunkPrimer.getBlockState(new BlockPos(x, y - yo + 1, z)).getBlock();
					if(currentBlock == chunkGenerator.betweenlandsBiomeProvider.BiomeFromID(biome).topBlock() && blockAbove == Blocks.AIR) {
						chunkPrimer.setBlockState(new BlockPos(x, y - yo, z), this.block, false);
						break;
					}
				}
			}
		}
	}
}
