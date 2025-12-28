package thebetweenlands.api.world;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;

public interface IBetweenlandsBiomeSource extends BiomeResolver {
	float getSurfaceDepth();

	float getGlobalFactor();

	float getBiomeDepth(int x, int y, int z, Climate.Sampler sampler);

	float getBiomeDepth(Holder<Biome> biome);

	float getBiomeScale(int x, int y, int z, Climate.Sampler sampler);

	float getBiomeScale(Holder<Biome> biome);

	HolderSet<ConfiguredEarlyGenerator<?, ?>> getBiomeGenerators(Holder<Biome> biome);
}
