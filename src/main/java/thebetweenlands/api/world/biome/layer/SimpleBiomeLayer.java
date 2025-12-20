package thebetweenlands.api.world.biome.layer;

import net.minecraft.util.RandomSource;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public interface SimpleBiomeLayer extends BiomeLayer {

	@Override
    public default <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		AreaFactoryContext<A> areaContext = context.areaContext().get();
        return () -> areaContext.createResult((x, z) -> {
            return this.apply(context, context.createRandom(x, z), x, z);
        });
    }

    public <A extends Area> int apply(BiomeLayerContext<A> context, RandomSource random, int x, int z);
}
