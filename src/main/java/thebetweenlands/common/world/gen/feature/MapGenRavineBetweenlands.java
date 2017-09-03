package thebetweenlands.common.world.gen.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class MapGenRavineBetweenlands extends MapGenRavine {
	protected static final IBlockState SWAMP_WATER = BlockRegistry.SWAMP_WATER.getDefaultState();

	@Override
	protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {
		if (this.rand.nextInt(60) == 0) {
			double rx = (double)(chunkX * 16 + this.rand.nextInt(16));
			double ry = (double)(this.rand.nextInt(this.rand.nextInt(80) + 8) + 20);
			double rz = (double)(chunkZ * 16 + this.rand.nextInt(16));

			for (int j = 0; j < 1 + (this.rand.nextInt(5) == 0 ? 2 : 0); ++j) {
				float f = this.rand.nextFloat() * ((float)Math.PI * 2F);
				float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float f2 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
				this.addTunnel(this.rand.nextLong(), p_180701_4_, p_180701_5_, chunkPrimerIn, rx, ry, rz, f2, f, f1, 0, 0, 3.0D);
			}
		}
	}

	@Override
	protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
		if(y > WorldProviderBetweenlands.CAVE_WATER_HEIGHT) {
			Block block = data.getBlockState(x, y, z).getBlock();
			return block == BlockRegistry.SWAMP_WATER;
		}
		return false;
	}

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
		Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
		IBlockState state = data.getBlockState(x, y, z);
		IBlockState top = biome.topBlock;
		IBlockState filler = biome.fillerBlock;

		if (state.getBlock() == BlockRegistry.BETWEENSTONE || state.getBlock() == BlockRegistry.PITSTONE || state.getBlock() == top.getBlock() || state.getBlock() == filler.getBlock()) {
			if (y - 1 < WorldProviderBetweenlands.CAVE_WATER_HEIGHT) {
				data.setBlockState(x, y, z, SWAMP_WATER);
			} else {
				data.setBlockState(x, y, z, AIR);

				if (foundTop && data.getBlockState(x, y - 1, z).getBlock() == filler.getBlock()) {
					data.setBlockState(x, y - 1, z, top.getBlock().getDefaultState());
				}
			}
		}
	}
}
