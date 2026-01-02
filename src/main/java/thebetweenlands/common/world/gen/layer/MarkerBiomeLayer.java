package thebetweenlands.common.world.gen.layer;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRef;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public record MarkerBiomeLayer(String refName) implements BiomeLayer {

	public static final MapCodec<MarkerBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codec.string(1, 64).fieldOf("ref_name").forGetter(MarkerBiomeLayer::refName)
				).apply(instance, MarkerBiomeLayer::new)
		);

	@Override
	public boolean referencesPreviousLayer() {
		return true;
	}
	
	@Override
	public <A extends Area> void compose(BiomeLayerContext<A> context, BiomeLayerChain biomeLayerChain) {
		Optional<BiomeLayerRef> previousLayer = biomeLayerChain.getPreviousLayer();
		if(previousLayer.isPresent()) {
			biomeLayerChain.addBackwardRef(this.refName(), previousLayer.get());
		}
	}
	
	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		Optional<BiomeLayerRef> previousLayer = chainState.getPreviousLayer();
		if(previousLayer.isPresent()) {
			return previousLayer.get().createAreaFactory(context.areaContext());
		}
		throw new IllegalStateException("A MarkerBiomeLayer layer should always be preceeded by at least one layer");
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
