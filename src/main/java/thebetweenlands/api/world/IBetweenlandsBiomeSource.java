package thebetweenlands.api.world;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

public interface IBetweenlandsBiomeSource {
	public float getSurfaceDepth();
	
	public float getGlobalFactor();
	
	public float getBiomeDepth(int x, int y, int z, Climate.Sampler sampler);
	
	public float getBiomeDepth(Holder<Biome> biome);

	public float getBiomeScale(int x, int y, int z, Climate.Sampler sampler);

	public float getBiomeScale(Holder<Biome> biome);
}
