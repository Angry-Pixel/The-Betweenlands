package thebetweenlands.api.world.biome.layer;

import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public interface DoubleParentBiomeLayer extends BiomeLayer {

	public BiomeLayerConfigured getFirstParentLayer();

	public BiomeLayerConfigured getSecondParentLayer();
	
	@Override
	public default <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		AreaFactoryContext<A> areaContext = context.areaContext().get();
		AreaFactory<A> firstParentAreaFactory = this.getFirstParentLayer().createAreaFactory(context, chainState);
		AreaFactory<A> secondParentAreaFactory = this.getSecondParentLayer().createAreaFactory(context, chainState);
		return () -> {
			A firstParentArea = firstParentAreaFactory.make();
			A secondParentArea = secondParentAreaFactory.make();
			return areaContext.createResult((x, z) -> {
				return this.apply(context, firstParentArea, secondParentArea, x, z);
			}, firstParentArea, secondParentArea);
		};
	}

	public <A extends Area> int apply(BiomeLayerContext<A> context, A firstParentArea, A secondParentArea, int x, int z);
}
