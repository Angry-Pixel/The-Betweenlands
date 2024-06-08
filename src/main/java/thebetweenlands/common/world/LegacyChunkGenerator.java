package thebetweenlands.common.world;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.gen.biome.decorator.BiomeDecorator;
import thebetweenlands.common.world.gen.biome.decorator.DecoratorFeature;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class LegacyChunkGenerator extends NoiseBasedChunkGenerator {

    // Decorator registry
    public List<BiomeGenerator> decorators;

    public LegacyChunkGenerator(Registry<StructureSet> p_209106_, Registry<NormalNoise.NoiseParameters> p_209107_, BiomeSource p_209108_, long p_209109_, Holder<NoiseGeneratorSettings> p_209110_) {
        super(p_209106_, p_209107_, p_209108_, p_209109_, p_209110_);

        // TODO: use world seed if set to 0 (don't know why this isant vanilla)

        // TODO: port and replace with vanilla features
        // Default, use betweenlands biome decorators
        this.decorators = BiomeRegistry.BETWEENLANDS_DIM_BIOME_REGISTRY.stream().map((obj) -> {return obj.biomeGenerator;}).toList();
    }

    // Just do standard chunk gen biome generation
    public CompletableFuture<ChunkAccess> createBiomes(Registry<Biome> p_196743_, Executor p_196744_, Blender p_196745_, StructureFeatureManager p_196746_, ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("init_biomes", () -> {
            chunk.fillBiomesFromNoise(this.getBiomeSource(), this.climateSampler());
            return chunk;
        }), Util.backgroundExecutor());
    }

    // Standard fillFromNoise with our doFill instead
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor ex, Blender blend, StructureFeatureManager structure, ChunkAccess chunk) {
        NoiseSettings noisesettings = this.settings.value().noiseSettings();
        LevelHeightAccessor levelheightaccessor = chunk.getHeightAccessorForGeneration();
        int i = Math.max(noisesettings.minY(), levelheightaccessor.getMinBuildHeight());
        int j = Math.min(noisesettings.minY() + noisesettings.height(), levelheightaccessor.getMaxBuildHeight());
        int k = Mth.intFloorDiv(i, noisesettings.getCellHeight());
        int l = Mth.intFloorDiv(j - i, noisesettings.getCellHeight());
        if (l <= 0) {
            return CompletableFuture.completedFuture(chunk);
        } else {
            int i1 = chunk.getSectionIndex(l * noisesettings.getCellHeight() - 1 + i);
            int j1 = chunk.getSectionIndex(i);
            Set<LevelChunkSection> set = Sets.newHashSet();

            for(int k1 = i1; k1 >= j1; --k1) {
                LevelChunkSection levelchunksection = chunk.getSection(k1);
                levelchunksection.acquire();
                set.add(levelchunksection);
            }

            return CompletableFuture.supplyAsync(Util.wrapThreadWithTaskName("wgen_fill_noise", () -> {
                return this.doFill(blend, structure, chunk, k, l);
            }), Util.backgroundExecutor()).whenCompleteAsync((p_209132_, p_209133_) -> {
                for(LevelChunkSection levelchunksection1 : set) {
                    levelchunksection1.release();
                }

            }, ex);
        }
    }

    // Made doFill public because im not a sadist
    public abstract ChunkAccess doFill(Blender blend, StructureFeatureManager structure, ChunkAccess chunk, int min, int max);
}
