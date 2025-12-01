package thebetweenlands.common.registries;

import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.warp.BLBiomeData;
import thebetweenlands.common.world.gen.warp.TerrainPoint;

public class BiomeRegistry {

	public static final ResourceKey<Biome> PATCHY_ISLANDS = makeKey("patchy_islands");
	public static final ResourceKey<Biome> SWAMPLANDS = makeKey("swamplands");
	public static final ResourceKey<Biome> DEEP_WATERS = makeKey("deep_waters");
	public static final ResourceKey<Biome> COARSE_ISLANDS = makeKey("coarse_islands");
	public static final ResourceKey<Biome> RAISED_ISLES = makeKey("raised_isles");
	public static final ResourceKey<Biome> SLUDGE_PLAINS = makeKey("sludge_plains");
	public static final ResourceKey<Biome> ERODED_MARSH = makeKey("eroded_marsh");
	public static final ResourceKey<Biome> MARSH = makeKey("marsh");
	public static final ResourceKey<Biome> SWAMPLANDS_CLEARING = makeKey("swamplands_clearing");
	public static final ResourceKey<Biome> SLUDGE_PLAINS_CLEARING = makeKey("sludge_plains_clearing");

	private static ResourceKey<Biome> makeKey(String name) {
		return ResourceKey.create(Registries.BIOME, TheBetweenlands.prefix(name));
	}

	private static BiomeGenerationSettings.Builder addUniversalFeatures(BiomeGenerationSettings.Builder builder) {
		return addBetweenlandsOres(builder);
	}

	private static BiomeGenerationSettings.Builder addBetweenlandsOres(BiomeGenerationSettings.Builder builder) {
		return builder
			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.SULFUR)
			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.SYRMORITE)
			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.BONE_ORE)
			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.OCTINE)
			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.SWAMP_DIRT)
//			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.LIMESTONE)
			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.VALONITE)
			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.SCABYST)
			.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, PlacedFeatureRegistry.LIFE_GEM);
	}

	// TODO figure out how to do spawns
	public static void bootstrap(BootstrapContext<Biome> context) {
		HolderGetter<PlacedFeature> featureGetter = context.lookup(Registries.PLACED_FEATURE);
		HolderGetter<ConfiguredWorldCarver<?>> carverGetter = context.lookup(Registries.CONFIGURED_CARVER);

		context.register(PATCHY_ISLANDS, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x184220)
				.foliageColorOverride(0x00AD7C)
				//2nd grass color: 0x1fC63D
				.grassColorOverride(0x1FC66D)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.WEEDWOOD_TREE_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SAP_TREE_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.HEARTHGROVE_TREE)

				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SHORT_SWAMP_GRASS_PATCH_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.TALL_CATTAIL_COMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.CATTAIL_PATCH_UNCOMMON)
				.build()
			)
			.build());

		context.register(SWAMPLANDS, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x184220)
				//2nd foliage color: 0x85AF51
				.foliageColorOverride(0x52AF5A)
				//2nd grass color: 0x50A040
				.grassColorOverride(0x2AFF00)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.WEEDWOOD_TREE_COMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SAP_TREE_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.RUBBER_TREE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.NIBBLETWIG_TREE)

				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SHORT_SWAMP_GRASS_PATCH_COMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.NETTLE_PATCH_COMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SWAMP_PLANT_PATCH_COMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.VENUS_FLY_TRAP_PATCH_COMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.PITCHER_PLANT)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.FLAT_HEAD_MUSHROOM_PATCH_COMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.BLACK_HAT_MUSHROOM_PATCH_COMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.VOLARPAD)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.TALL_CATTAIL_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.TALL_SWAMP_GRASS)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.CATTAIL_PATCH_UNCOMMON)
				.build()
			)
			.build());

		context.register(DEEP_WATERS, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x1B3944)
				.foliageColorOverride(0xE5F745)
				.grassColorOverride(0xE5F745)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.build()
			)
			.build());

		context.register(COARSE_ISLANDS, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x1b3944)
				//2nd foliage color: 0xA87800
				.foliageColorOverride(0xA8A800)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.WEEDWOOD_TREE_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SAP_TREE_COMMON)

				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SHORT_SWAMP_GRASS_PATCH_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.NETTLE_PATCH_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.VOLARPAD)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.CATTAIL_PATCH_COMMON)
				.build()
			)
			.build());

		context.register(RAISED_ISLES, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x1b3944)
				//2nd foliage color: 0xA87800
				.foliageColorOverride(0xA8A800)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.WEEDWOOD_TREE_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SAP_TREE_COMMON)

				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SHORT_SWAMP_GRASS_PATCH_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.NETTLE_PATCH_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.VOLARPAD)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.CATTAIL_PATCH_COMMON)
				.build()
			)
			.build());

		context.register(SLUDGE_PLAINS, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x3A2F0B)
				.grassColorOverride(0x5B3522)
				.foliageColorOverride(0xD36423)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.ROTTEN_WEEDWOOD_TREE)
				.build()
			)
			.build());

		context.register(ERODED_MARSH, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x485E18)
				.grassColorOverride(0x627017)
				.foliageColorOverride(0x63B581)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.WEEDWOOD_TREE_SUPER_RARE)

				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SHORT_SWAMP_GRASS_PATCH_MORE_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.NETTLE_PATCH_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.ARROW_ARUM_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.PICKERELWEED_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.MARSH_HIBISCUS_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.MARSH_MALLOW_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.BUTTON_BUSH_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SOFT_RUSH_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.BROOMSEDGE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.BOTTLE_BRUSH_GRASS_PATCH)
				.build()
			)
			.build());

		context.register(MARSH, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x485E18)
				.grassColorOverride(0x627017)
				.foliageColorOverride(0x63B581)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.WEEDWOOD_TREE_SUPER_RARE)

				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SHORT_SWAMP_GRASS_PATCH_MORE_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.NETTLE_PATCH_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.ARROW_ARUM_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.PICKERELWEED_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.MARSH_HIBISCUS_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.MARSH_MALLOW_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.BUTTON_BUSH_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SOFT_RUSH_PATCH)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.BROOMSEDGE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.BOTTLE_BRUSH_GRASS_PATCH)
				.build()
			)
			.build());

		context.register(SWAMPLANDS_CLEARING, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x184220)
				.foliageColorOverride(0x52AF5A)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SHORT_SWAMP_GRASS_PATCH_SUPER_RARE)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.SWAMP_PLANT_PATCH_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.VENUS_FLY_TRAP_PATCH_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.PITCHER_PLANT)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.FLAT_HEAD_MUSHROOM_PATCH_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.BLACK_HAT_MUSHROOM_PATCH_UNCOMMON)
				.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, PlacedFeatureRegistry.CATTAIL_PATCH_UNCOMMON)
				.build()
			)
			.build());

		context.register(SLUDGE_PLAINS_CLEARING, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x3A2F0B)
				.grassColorOverride(0x5B3522)
				.foliageColorOverride(0xD36423)
				.waterFogColor(0x184220)
				.fogColor(0xC0D8FF)
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY)
			.generationSettings(addUniversalFeatures(new BiomeGenerationSettings.Builder(featureGetter, carverGetter))
				.build()
			)
			.build());
	}

	// TODO make a builder so this looks nicer
	public static List<BLBiomeData> biomeParameters(HolderGetter<Biome> registry, HolderGetter<ConfiguredEarlyGenerator<?, ?>> configuredGenerators) {
		return List.of(
			pairBiome(registry, 20, -0.125F, 0.475F, PATCHY_ISLANDS),
			pairBiome(registry, 25, -0.2F, 0.1F, SWAMPLANDS, HolderSet.direct(configuredGenerators.getOrThrow(EarlyGeneratorRegistry.Configured.FLAT_LAND_SWAMPLANDS))),
			pairBiome(registry, 12, -1.2F, 0.5F, DEEP_WATERS, HolderSet.direct(configuredGenerators.getOrThrow(EarlyGeneratorRegistry.Configured.DEEP_WATERS_SIMPLEX_TERRAIN))),
			pairBiome(registry, 16, -0.5F, 0.4F, COARSE_ISLANDS),
			pairBiome(registry, 16, -0.5F, 0.4F, RAISED_ISLES),
			pairBiome(registry, 5, -0.5F, 0.3F, SLUDGE_PLAINS, HolderSet.direct(configuredGenerators.getOrThrow(EarlyGeneratorRegistry.Configured.FLAT_LAND_SLUDGE_PLAINS))),
			pairBiome(registry, 4, -0.1F, 0.11F, ERODED_MARSH, HolderSet.direct(configuredGenerators.getOrThrow(EarlyGeneratorRegistry.Configured.ERODED_MARSH_ISLANDS))),
			pairBiome(registry, 10, -0.1F, 0.11F, MARSH, HolderSet.direct(configuredGenerators.getOrThrow(EarlyGeneratorRegistry.Configured.MARSH_ISLANDS))),
			pairBiome(registry, 0, 0.2F, 0.1F, SWAMPLANDS_CLEARING),
			pairBiome(registry, 0, 0.4F, 0.05F, SLUDGE_PLAINS_CLEARING)
		);
	}

	private static BLBiomeData pairBiome(HolderGetter<Biome> registry, int weight, float depth, float scale, ResourceKey<Biome> biome, HolderSet<ConfiguredEarlyGenerator<?, ?>> generators) {
		return new BLBiomeData(registry.getOrThrow(biome), new TerrainPoint((short)weight, depth, scale), generators);
	}

	private static BLBiomeData pairBiome(HolderGetter<Biome> registry, int weight, float depth, float scale, ResourceKey<Biome> biome) {
		return pairBiome(registry, weight, depth, scale, biome, HolderSet.empty());
	}
}
