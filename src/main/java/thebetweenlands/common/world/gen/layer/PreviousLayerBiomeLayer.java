package thebetweenlands.common.world.gen.layer;

import java.util.Optional;

import com.mojang.serialization.MapCodec;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRef;

public class PreviousLayerBiomeLayer implements BiomeLayer {
	public static final PreviousLayerBiomeLayer INSTANCE = new PreviousLayerBiomeLayer();
	public static final BiomeLayerConfigured CONFIGURED_INSTANCE = BiomeLayerConfigured.unconfigured(INSTANCE);

	public static final MapCodec<PreviousLayerBiomeLayer> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public boolean referencesPreviousLayer() {
		return true;
	}
	
	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		Optional<BiomeLayerRef> previousLayer = chainState.getPreviousLayer();
		if(previousLayer.isPresent()) {
			return previousLayer.get().createAreaFactory(context.areaContext());
		}
		throw new IllegalStateException("A PreviousBiomeLayer layer should always be preceeded by at least one layer");
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
