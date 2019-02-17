package thebetweenlands.common.world.gen.biome;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.storage.WorldInfo;
import thebetweenlands.common.world.DimensionBetweenlands;
import thebetweenlands.common.world.gen.layer.GenLayerBetweenlands;

public class BiomeProviderBetweenlands extends BiomeProvider {
	public static final List<Biome> ALLOWED_SPAWN_BIOMES = Lists.newArrayList(/*TODO: Add biomes suitable for spawning*/);

	protected final DimensionBetweenlands provider;
	
	public BiomeProviderBetweenlands(DimensionBetweenlands provider, WorldInfo worldInfo) {
		super(worldInfo);
		this.dimension = provider;
	}

	@Override
	public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
		original = GenLayerBetweenlands.initializeAllBiomeGenerators(seed, worldType);
		return super.getModdedBiomeGenerators(worldType, seed, original);
	}

	@Override
	public List<Biome> getBiomesToSpawnIn() {
		return ALLOWED_SPAWN_BIOMES;
	}
	
	@Override
	public float getTemperatureAtHeight(float biomeTemp, int y) {
		if(this.dimension.getEnvironmentEventRegistry().winter.isActive()) {
			return 0.1F;
		}
        return biomeTemp;
    }
}
