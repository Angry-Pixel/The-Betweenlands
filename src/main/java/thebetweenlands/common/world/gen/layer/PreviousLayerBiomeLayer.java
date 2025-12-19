package thebetweenlands.common.world.gen.layer;

import java.util.Optional;

import com.mojang.serialization.MapCodec;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRef;

public class PreviousLayerBiomeLayer implements BiomeLayer {
	public static final PreviousLayerBiomeLayer INSTANCE = new PreviousLayerBiomeLayer();

	public static final MapCodec<PreviousLayerBiomeLayer> CODEC = MapCodec.unit(INSTANCE);
	
	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		Optional<BiomeLayerRef> previous = chainState.getPreviousLayer();
		if(previous.isPresent()) {
			BiomeLayer previousLayer = previous.get().layer();
			BiomeLayerChainState previousChainState = previous.get().chainState();
			return previousLayer.createAreaFactory(context, previousChainState);
		}
		throw new IllegalStateException("A PreviousBiomeLayer layer should always be preceeded by at least one layer");
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
