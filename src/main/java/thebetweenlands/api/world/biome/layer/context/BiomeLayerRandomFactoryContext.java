package thebetweenlands.api.world.biome.layer.context;

import net.minecraft.resources.ResourceLocation;

public interface BiomeLayerRandomFactoryContext {
	public SeedMixer getSeedMixer();

	public BiomeLayerRandomContext createContext(ResourceLocation location);
	
	public BiomeLayerRandomContext createContext(String string);
	
	public BiomeLayerRandomContext createContext(long seedModifier);
}
