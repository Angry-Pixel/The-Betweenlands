package thebetweenlands.common.world.gen.biome.feature;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeWeights;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator.EnumGeneratorPass;

/**
 * Adds additional middle gems to the terrain
 */
public class MiddleGemFeature extends BiomeFeature {
	private Random rand = new Random();
	private long seed;

	@Override
	public void initializeGenerators(long seed, Biome biome) {
		this.seed = seed;
	}

	@Override
	public void generateNoise(int chunkX, int chunkZ, Biome biome) {
		this.rand.setSeed(this.seed);
		long seedX = this.rand.nextLong() / 2L * 2L + 1L;
		long seedZ = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long)chunkX * seedX + (long)chunkZ * seedZ ^ this.seed);
	}

	@Override
	public void replaceStackBlocks(int x, int z, double baseBlockNoise, ChunkPrimer chunkPrimer,
			ChunkGeneratorBetweenlands chunkGenerator, Biome[] biomesForGeneration, Biome biome, BiomeWeights biomeWeights,
			EnumGeneratorPass pass) {
		if(pass == EnumGeneratorPass.POST_GEN_CAVES) {
			float biomeWeight = biomeWeights.get(x, z);
			if(biomeWeight >= 1.0F && this.rand.nextInt(180) == 0) {
				int y = 255;
				for(;y > 1; y--) {
					IBlockState state = chunkPrimer.getBlockState(x, y, z);
					if(state.getBlock() != Blocks.AIR && state.getBlock() != BlockRegistry.SWAMP_WATER)
						break;
				}
				if(y <= 1 && y >= 255)
					return;
				IBlockState blockState = chunkPrimer.getBlockState(x, y, z);
				IBlockState blockStateAbove = chunkPrimer.getBlockState(x, y+1, z);
				if(blockState.getBlock() == BlockRegistry.MUD && blockStateAbove.getBlock() == BlockRegistry.SWAMP_WATER) {
					IBlockState gem;
					switch(this.rand.nextInt(3)) {
					default:
					case 0:
						gem = BlockRegistry.AQUA_MIDDLE_GEM_ORE.getDefaultState();
						break;
					case 1:
						gem = BlockRegistry.CRIMSON_MIDDLE_GEM_ORE.getDefaultState();
						break;
					case 2:
						gem = BlockRegistry.GREEN_MIDDLE_GEM_ORE.getDefaultState();
						break;
					}
					chunkPrimer.setBlockState(x, y, z, gem);
				}
			}
		}
	}
}
