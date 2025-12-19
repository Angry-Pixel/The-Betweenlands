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

public record BackwardsRefBiomeLayer(String refName) implements BiomeLayer {

	public static final MapCodec<BackwardsRefBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codec.string(1, 64).fieldOf("ref_name").forGetter(BackwardsRefBiomeLayer::refName)
				).apply(instance, BackwardsRefBiomeLayer::new)
		);
	
	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		Optional<BiomeLayerRef> ref = chainState.getBackwardsRef(this.refName());
		if(ref.isPresent()) {
			BiomeLayer refLayer = ref.get().layer();
			BiomeLayerChainState refChainState = ref.get().chainState();
			return refLayer.createAreaFactory(context, refChainState);
		}
		throw new IllegalStateException("Missing reference for BackwardsRefBiomeLayer: \"%s\"".formatted(this.refName()));
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
