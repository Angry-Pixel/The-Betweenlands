package thebetweenlands.api.world.biome.layer;

import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public interface SingleParentBiomeLayer extends BiomeLayer {

	public BiomeLayerConfigured getParentLayer();
	
	@Override
	public default <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		AreaFactoryContext<A> areaContext = context.areaContext().get();
		AreaFactory<A> parentAreaFactory = this.getParentLayer().createAreaFactory(context, chainState);
		return () -> {
			A parentArea = parentAreaFactory.make();
			return areaContext.createResult((x, z) -> {
				return this.apply(context, parentArea, x, z);
			}, parentArea);
		};
	}

	public <A extends Area> int apply(BiomeLayerContext<A> context, A parentArea, int x, int z);
}
