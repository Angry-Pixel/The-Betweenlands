package thebetweenlands.api.world.biome.layer.context;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.AreaFactoryContext;
import thebetweenlands.api.world.biome.layer.BiomeLayer;

public record BiomeLayerRef(BiomeLayer layer, BiomeLayerRandomContext randomContext, BiomeLayerChainState chainState) {

	public <A extends Area> AreaFactory<A> createAreaFactory(AreaFactoryContext<A> areaContext) {
		return this.layer.createAreaFactory(new BiomeLayerContext<A>(areaContext, this.randomContext), this.chainState);
	}

	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context) {
		return this.layer.createAreaFactory(context.useRandomContext(this.randomContext), this.chainState);
	}

}
