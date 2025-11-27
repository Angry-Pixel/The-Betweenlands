package thebetweenlands.api.world;

import java.util.Optional;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

// TODO extra chunk data
public record EarlyGenerationContext<GC extends EarlyGeneratorConfiguration>(
		Optional<ConfiguredEarlyGenerator<?, ?>> parentGenerator,
		ChunkGenerator chunkGenerator,
		long worldSeed,
		ChunkAccess chunkAccess,
		ChunkHeightmaps chunkHeightmaps,
		BlockGenerator blockGenerator,
		GC config
	) {

	public static record BlockGenerator(BlockState defaultTerrainState, BlockState defaultLiquidState, BlockGeneratorFunction generatorFunction) {

		public BlockState getBlockState(double density, double y) {
			return this.generatorFunction.getBlockState(density, y);
		}
		
		@FunctionalInterface
		public static interface BlockGeneratorFunction {
			public BlockState getBlockState(double density, double y);
		}
	}
	
	public static record ChunkHeightmaps(Heightmap oceanfloorHeightmap, Heightmap surfaceHeightmap) {
		public void update(int x, int y, int z, BlockState state) {
			this.oceanfloorHeightmap.update(x, y, z, state);
			this.surfaceHeightmap.update(x, y, z, state);
		}
	}
}
