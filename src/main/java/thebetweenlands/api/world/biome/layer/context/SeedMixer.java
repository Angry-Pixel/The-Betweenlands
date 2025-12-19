package thebetweenlands.api.world.biome.layer.context;

import net.minecraft.resources.ResourceLocation;

/**
 * Mixes a seed and seed modifiers to produce new seeds
 */
public interface SeedMixer {
	public default long mixSeed(ResourceLocation location) {
		return this.mixSeed(location.toString());
	}
	
	public default long mixSeed(String string) {
		return this.mixSeed(string.hashCode());
	}
	
	public long mixSeed(long seedModifier);
}
