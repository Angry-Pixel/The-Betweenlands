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

public record BackwardRefBiomeLayer(String refName) implements BiomeLayer {

	public static final MapCodec<BackwardRefBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					Codec.string(1, 64).fieldOf("ref_name").forGetter(BackwardRefBiomeLayer::refName)
				).apply(instance, BackwardRefBiomeLayer::new)
		);
	
	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		Optional<BiomeLayerRef> ref = chainState.getBackwardRef(this.refName());
		if(ref.isPresent()) {
			return ref.get().createAreaFactory(context.areaContext());
		}
		// TODO error in some other way here
		throw new IllegalStateException("Missing reference for BackwardRefBiomeLayer: \"%s\"".formatted(this.refName()));
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
