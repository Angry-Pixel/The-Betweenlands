package thebetweenlands.api.world.generator;

import java.util.Optional;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import thebetweenlands.api.world.ExtraChunkInfo;

public record EarlyGenerationContext<GC extends EarlyGeneratorConfiguration>(
		Optional<ConfiguredEarlyGenerator<?, ?>> parentGenerator,
		ChunkGenerator chunkGenerator,
		long worldSeed,
		ChunkAccess chunkAccess,
		ChunkHeightmaps chunkHeightmaps,
		BlockGenerator blockGenerator,
		GC config,
		ExtraChunkInfo extraChunkInfo
	) {

	public <GC2 extends EarlyGeneratorConfiguration> EarlyGenerationContext<GC2> withConfig(GC2 config) {
		return new EarlyGenerationContext<GC2>(this.parentGenerator, this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, config, this.extraChunkInfo);
	}
	
	public EarlyGenerationContext<GC> withExtraChunkInfo(ExtraChunkInfo extraChunkInfo) {
		return new EarlyGenerationContext<GC>(this.parentGenerator, this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, extraChunkInfo);
	}

	public EarlyGenerationContext<GC> withParentGenerator(ConfiguredEarlyGenerator<?, ?> parent) {
		return new EarlyGenerationContext<GC>(Optional.of(parent), this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, this.extraChunkInfo);
	}

	public EarlyGenerationContext<GC> withoutParentGenerator() {
		return new EarlyGenerationContext<GC>(Optional.empty(), this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, this.extraChunkInfo);
	}
	
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
