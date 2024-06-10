package thebetweenlands.client.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.CubicSpline;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.dimension.BetweenlandsDimensionType;
import thebetweenlands.common.dimension.BetweenlandsSurfaceRuleData;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.TagRegistry;
import thebetweenlands.common.world.BetweenlandsBiomeProvider;
import thebetweenlands.common.world.BetweenlandsServerLevel;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayerBetweenlands;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.Executor;

// Mixin server side betweenlands dimension and type registration
// Json custom dimension interface sucks
@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Final
    @Shadow private Map<ResourceKey<Level>, ServerLevel> levels;
    @Final
    @Shadow private Executor executor;
    @Final
    @Shadow protected  LevelStorageSource.LevelStorageAccess storageSource;
    @Final
    @Shadow protected WorldData worldData;

    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Inject(method = "createLevels", at = @At("RETURN"))
    protected void createLevels(ChunkProgressListener listener, CallbackInfo ci) {

        TheBetweenlands.LOGGER.info("Beginning dimension injection");
        // Register dim type to registry
        ServerLevelData serverleveldata = this.worldData.overworldData();
        WorldGenSettings worldgensettings = this.worldData.worldGenSettings();
        long worldSeed = worldgensettings.seed();               // this is the only way I found that can make a dimension using the world seed

        // Extra world data
        DerivedLevelData derivedleveldata = new DerivedLevelData(this.worldData, serverleveldata);
        boolean debugFlag = worldgensettings.isDebug();
        long biomeSeed = BiomeManager.obfuscateSeed(worldSeed);

        // Biome provider
        BetweenlandsBiomeProvider biomeProvider = new BetweenlandsBiomeProvider(new Climate.ParameterList<>(BiomeRegistry.BETWEENLANDS_DIM_BIOME_REGISTRY.stream().map((obj) -> {
            return Pair.of(Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                    registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOrCreateHolder(obj.biome.getKey()));}).toList()), Optional.empty(), new ProviderGenLayerBetweenlands());

        NormalNoise.NoiseParameters noiseParameters = new NormalNoise.NoiseParameters(0, List.of(0.0));

        // Chunk gen
        ChunkGeneratorBetweenlands chunkGenerator = new ChunkGeneratorBetweenlands(registryAccess().registryOrThrow(Registry.STRUCTURE_SET_REGISTRY), registryAccess().registryOrThrow(Registry.NOISE_REGISTRY), biomeProvider, worldSeed,
                new Holder.Direct<>(new NoiseGeneratorSettings(new NoiseSettings(0, 256, new NoiseSamplingSettings(0, 0, 0, 0),
                        new NoiseSlider(0, 0, 0), new NoiseSlider(0, 0, 0), 1, 1, new TerrainShaper(CubicSpline.constant(0), CubicSpline.constant(0), CubicSpline.constant(0))),
                        BlockRegistry.BETWEENSTONE.get().defaultBlockState(), BlockRegistry.SWAMP_WATER_BLOCK.get().defaultBlockState(),
                        new NoiseRouterWithOnlyNoises(DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0)),
                        BetweenlandsSurfaceRuleData.betweenlands(),
                        120,
                        false,
                        false,
                        false,
                        false)),
                new ChunkGeneratorBetweenlands.BetweenlandsGeneratorSettings(
                        -28,
                        false,
                        true));

        // Dimension type and get holder
        BetweenlandsDimensionType dimensionType = new BetweenlandsDimensionType(
                OptionalLong.empty(),
                true,
                false,
                false,
                true,
                1,
                false,
                false,
                false,
                false,
                false,
                0,
                256,
                256,
                TagRegistry.Blocks.BETWEENSTONE_ORE_REPLACEABLE,
                new ResourceLocation(TheBetweenlands.ID, "the_betweenlands"),
                0);

        Holder<DimensionType> dimensionTypeHolder = new Holder.Direct<>(dimensionType);

        // Create server level then add to dim list
        BetweenlandsServerLevel betweenlandsLevel = new BetweenlandsServerLevel((MinecraftServer) (Object) this, this.executor, this.storageSource, derivedleveldata, DimensionRegistries.BETWEENLANDS_DIMENSION_KEY, dimensionTypeHolder, listener, chunkGenerator, debugFlag, biomeSeed, ImmutableList.of(), false);
        levels.put(DimensionRegistries.BETWEENLANDS_DIMENSION_KEY, betweenlandsLevel);
        TheBetweenlands.LOGGER.info("Dimension injection complete");
    }
}
