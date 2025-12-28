package thebetweenlands.api.world.generator;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import thebetweenlands.api.world.ExtraChunkInfo;

public record EarlyGenerationContext<GC extends EarlyGeneratorConfiguration>(
		Optional<ConfiguredEarlyGenerator<?, ?>> parentGenerator,
		Optional<Holder<Biome>> biome,
		ChunkGenerator chunkGenerator,
		long worldSeed,
		ChunkAccess chunkAccess,
		ChunkHeightmaps chunkHeightmaps,
		BlockGenerator blockGenerator,
		GC config,
		ExtraChunkInfo extraChunkInfo
	) {

	// Set config
	public <GC2 extends EarlyGeneratorConfiguration> EarlyGenerationContext<GC2> withConfig(GC2 config) {
		return new EarlyGenerationContext<>(this.parentGenerator, this.biome, this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, config, this.extraChunkInfo);
	}

	// Set extra chunk info
	public EarlyGenerationContext<GC> withExtraChunkInfo(ExtraChunkInfo extraChunkInfo) {
		return new EarlyGenerationContext<>(this.parentGenerator, this.biome, this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, extraChunkInfo);
	}

	// Update parent generator
	public EarlyGenerationContext<GC> withParentGenerator(ConfiguredEarlyGenerator<?, ?> parent) {
		return new EarlyGenerationContext<>(Optional.of(parent), this.biome, this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, this.extraChunkInfo);
	}

	public EarlyGenerationContext<GC> withoutParentGenerator() {
		return new EarlyGenerationContext<>(Optional.empty(), this.biome, this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, this.extraChunkInfo);
	}

	public EarlyGenerationContext<GC> withParentGenerator(Optional<ConfiguredEarlyGenerator<?, ?>> parentOptional) {
		return new EarlyGenerationContext<>(parentOptional, this.biome, this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, this.extraChunkInfo);
	}

	// Update biome
	public EarlyGenerationContext<GC> withBiome(Holder<Biome> biome) {
		return new EarlyGenerationContext<>(this.parentGenerator, Optional.of(biome), this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, this.extraChunkInfo);
	}

	public EarlyGenerationContext<GC> withoutBiome() {
		return new EarlyGenerationContext<>(this.parentGenerator, Optional.empty(), this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, this.extraChunkInfo);
	}

	public EarlyGenerationContext<GC> withBiome(Optional<Holder<Biome>> biomeOptional) {
		return new EarlyGenerationContext<>(this.parentGenerator, biomeOptional, this.chunkGenerator, this.worldSeed, this.chunkAccess, this.chunkHeightmaps, this.blockGenerator, this.config, this.extraChunkInfo);
	}

	public record BlockGenerator(BlockState defaultTerrainState, BlockState defaultLiquidState, BlockGeneratorFunction generatorFunction) {

		public BlockState getBlockState(double density, double y) {
			return this.generatorFunction.getBlockState(density, y);
		}

		@FunctionalInterface
		public interface BlockGeneratorFunction {
			BlockState getBlockState(double density, double y);
		}
	}

	public record ChunkHeightmaps(Heightmap oceanfloorHeightmap, Heightmap surfaceHeightmap) {
		public void update(int x, int y, int z, BlockState state) {
			this.oceanfloorHeightmap.update(x, y, z, state);
			this.surfaceHeightmap.update(x, y, z, state);
		}
	}
}
