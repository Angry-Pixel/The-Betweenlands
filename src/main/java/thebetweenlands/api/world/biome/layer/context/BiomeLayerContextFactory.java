package thebetweenlands.api.world.biome.layer.context;

import net.minecraft.resources.ResourceLocation;
import thebetweenlands.api.world.biome.layer.Area;

public interface BiomeLayerContextFactory<A extends Area> {
	public SeedMixer getSeedMixer();

	public BiomeLayerContext<A> mixSeed(ResourceLocation location);
	
	public BiomeLayerContext<A> mixSeed(String string);
	
	public BiomeLayerContext<A> mixSeed(long seedModifier);
}
