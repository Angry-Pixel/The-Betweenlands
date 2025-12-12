package thebetweenlands.api.world;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;

public interface IBetweenlandsBiomeSource extends BiomeResolver {
	public float getSurfaceDepth();
	
	public float getGlobalFactor();
	
	public float getBiomeDepth(int x, int y, int z, Climate.Sampler sampler);
	
	public float getBiomeDepth(Holder<Biome> biome);

	public float getBiomeScale(int x, int y, int z, Climate.Sampler sampler);

	public float getBiomeScale(Holder<Biome> biome);

	public List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> getBiomeGenerators(Holder<Biome> biome);
}
