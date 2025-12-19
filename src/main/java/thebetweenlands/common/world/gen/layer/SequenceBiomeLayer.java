package thebetweenlands.common.world.gen.layer;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChain;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContextResolver;

public record SequenceBiomeLayer(List<BiomeLayerConfigured> layers) implements BiomeLayer {

	public static final MapCodec<SequenceBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ExtraCodecs.nonEmptyList(BiomeLayerConfigured.CODEC.listOf()).fieldOf("layers").forGetter(SequenceBiomeLayer::layers)
				).apply(instance, SequenceBiomeLayer::new)
		);

	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		// Get all layers to compose
		List<BiomeLayerConfigured> configuredLayers = this.layers();

		// Chain so every element knows what's happening
		BiomeLayerChain chain = new BiomeLayerChain(Optional.empty(), chainState.getAllBackwardRefs());
		
		// Area factory to return
		AreaFactory<A> areaFactory = null;
		
		// Compose every layer in the sequence
		for(BiomeLayerConfigured configuredLayer : configuredLayers) {
			BiomeLayer layer = configuredLayer.biomeLayer();
			BiomeLayerContextResolver resolver = configuredLayer.contextResolver();
			BiomeLayerContext<A> childContext = resolver.createContext(context.getContextFactory());
			chain.nextLayer(layer);
			
			layer.compose(childContext, chain);
			areaFactory = layer.createAreaFactory(context, chainState);
		}
		
		// Finish the chain
		chain.finish();
		
		return areaFactory;
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
