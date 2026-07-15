package thebetweenlands.api.world.biome.layer;

import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerReferenceAcquirer;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public interface SingleParentBiomeLayer extends BiomeLayer {

	public BiomeLayerConfigured getParentLayer();

	@Override
	public default <A extends Area> void compose(BiomeLayerContext<A> context, BiomeLayerChain biomeLayerChain) {
		BiomeLayer.super.compose(context, biomeLayerChain);
		this.getParentLayer().compose(context, biomeLayerChain);
	}
	
	@Override
	public default <A extends Area> void acquireReferences(BiomeLayerContext<A> context, BiomeLayerReferenceAcquirer referenceAcquirer) {
		BiomeLayer.super.acquireReferences(context, referenceAcquirer);
		this.getParentLayer().acquireReferences(context, referenceAcquirer);
	}
	
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
