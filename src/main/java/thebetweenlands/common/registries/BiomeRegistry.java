package thebetweenlands.common.registries;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.warp.TerrainPoint;

import java.util.List;

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
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
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
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());

		context.register(DEEP_WATERS, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x1B3944)
				.foliageColorOverride(0xE5F745)
				.grassColorOverride(0xE5F745)
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());

		context.register(COARSE_ISLANDS, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x1b3944)
				//2nd foliage color: 0xA87800
				.foliageColorOverride(0xA8A800)
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());

		context.register(RAISED_ISLES, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x1b3944)
				//2nd foliage color: 0xA87800
				.foliageColorOverride(0xA8A800)
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());

		context.register(SLUDGE_PLAINS, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x3A2F0B)
				.grassColorOverride(0x5B3522)
				.foliageColorOverride(0xD36423)
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());

		context.register(ERODED_MARSH, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x485E18)
				.grassColorOverride(0x627017)
				.foliageColorOverride(0x63B581)
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());

		context.register(MARSH, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x485E18)
				.grassColorOverride(0x627017)
				.foliageColorOverride(0x63B581)
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());

		context.register(SWAMPLANDS_CLEARING, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x184220)
				.foliageColorOverride(0x52AF5A)
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());

		context.register(SLUDGE_PLAINS_CLEARING, new Biome.BiomeBuilder()
			.temperature(0.8F)
			.downfall(0.9F)
			.specialEffects(new BiomeSpecialEffects.Builder()
				.waterColor(0x3A2F0B)
				.grassColorOverride(0x5B3522)
				.foliageColorOverride(0xD36423)
				.waterFogColor(0x184220) // TODO Upgrade to BetweenlandsSpecialEffects?
				.fogColor(0xC0D8FF) // TODO Upgrade to BetweenlandsSpecialEffects?
				.skyColor(0)
				.build())
			.mobSpawnSettings(MobSpawnSettings.EMPTY) // TODO do biomes differently??
			.generationSettings(BiomeGenerationSettings.EMPTY) // TODO do biomes differently??
			.build());
	}

	public static List<Pair<TerrainPoint, Holder<Biome>>> biomeParameters(HolderGetter<Biome> registry) {
		return List.of(
			pairBiome(registry, 20, -0.125F, 0.475F, PATCHY_ISLANDS),
			pairBiome(registry, 25, -0.10F, 0.10F, SWAMPLANDS),
			pairBiome(registry, 12, -1.20F, 0.05F, DEEP_WATERS),
			pairBiome(registry, 16, -0.50F, 0.40F, COARSE_ISLANDS),
			pairBiome(registry, 16, -0.50F, 0.40F, RAISED_ISLES),
			pairBiome(registry, 5, -0.50F, 0.30F, SLUDGE_PLAINS),
			pairBiome(registry, 4, -0.10F, 0.11F, ERODED_MARSH),
			pairBiome(registry, 10, -0.10F, 0.11F, MARSH),
			pairBiome(registry, 0, -0.20F, 0.10F, SWAMPLANDS_CLEARING),
			pairBiome(registry, 0, -0.40F, 0.05F, SLUDGE_PLAINS_CLEARING)
		);
	}

	private static Pair<TerrainPoint, Holder<Biome>> pairBiome(HolderGetter<Biome> registry, int weight, float depth, float scale, ResourceKey<Biome> biome) {
		return Pair.of(new TerrainPoint((short)weight, depth, scale), registry.getOrThrow(biome));
	}
}
