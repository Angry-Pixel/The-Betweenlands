package thebetweenlands.api.world.biome.layer.context;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;

public record BiomeLayerRef<A extends Area>(BiomeLayer layer, BiomeLayerContext<A> context, BiomeLayerChainState chainState) {

	public AreaFactory<A> createAreaFactory() {
		return this.layer.createAreaFactory(this.context, this.chainState);
	}
	
}
