package thebetweenlands.common.world.gen.layer;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChain;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public record SequenceBiomeLayer(List<BiomeLayerConfigured> layers) implements BiomeLayer {

	public static final MapCodec<SequenceBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BiomeLayerConfigured.CODEC.listOf().fieldOf("layers").forGetter(SequenceBiomeLayer::layers)
				).apply(instance, SequenceBiomeLayer::new)
		);

	
	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		BiomeLayerChain chain = new BiomeLayerChain(Optional.empty(), chainState.getAllBackwardRefs());
		
		AreaFactory<A> areaFactory = null;
		
		for(BiomeLayerConfigured layer : this.layers()) {
			
		}
		
		return areaFactory;
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
