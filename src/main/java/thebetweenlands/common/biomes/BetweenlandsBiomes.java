package thebetweenlands.common.biomes;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.sounds.Music;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.*;

import javax.annotation.Nullable;

public class BetweenlandsBiomes {

	// Betweenlands sky color
	protected static int calculateSkyColor(float p_194844_) {
		float $$1 = p_194844_ / 3.0F;
		$$1 = Mth.clamp($$1, -1.0F, 1.0F);
		return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
	}

	private static Biome biome(Biome.Precipitation p_194862_, Biome.BiomeCategory p_194863_, float p_194864_, float p_194865_, MobSpawnSettings.Builder p_194866_, BiomeGenerationSettings.Builder p_194867_, @Nullable Music p_194868_) {
		return biome(p_194862_, p_194863_, p_194864_, p_194865_, 4159204, 329011, p_194866_, p_194867_, p_194868_);
	}

	private static Biome biome(Biome.Precipitation p_194852_, Biome.BiomeCategory p_194853_, float p_194854_, float p_194855_, int p_194856_, int p_194857_, MobSpawnSettings.Builder p_194858_, BiomeGenerationSettings.Builder p_194859_, @Nullable Music p_194860_) {
		return (new Biome.BiomeBuilder()).precipitation(p_194852_).biomeCategory(p_194853_).temperature(p_194854_).downfall(p_194855_).specialEffects((new BiomeSpecialEffects.Builder()).waterColor(p_194856_).waterFogColor(p_194857_).fogColor(12638463).skyColor(calculateSkyColor(p_194854_)).ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS).backgroundMusic(p_194860_).build()).mobSpawnSettings(p_194858_.build()).generationSettings(p_194859_.build()).build();
	}

	private static void globalBetweenlandsGeneration(BiomeGenerationSettings.Builder p_194870_) {
		BiomeDefaultFeatures.addDefaultCarversAndLakes(p_194870_);
		BiomeDefaultFeatures.addDefaultCrystalFormations(p_194870_);
		BiomeDefaultFeatures.addDefaultMonsterRoom(p_194870_);
		BiomeDefaultFeatures.addDefaultUndergroundVariety(p_194870_);
		BiomeDefaultFeatures.addDefaultSprings(p_194870_);
		BiomeDefaultFeatures.addSurfaceFreezing(p_194870_);
	}
}
