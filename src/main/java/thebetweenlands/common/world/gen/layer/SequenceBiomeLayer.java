package thebetweenlands.common.world.gen.layer;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContextResolver;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

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

		// Chain so every element knows what's happening (and can access elements in parent scopes, as well)
		BiomeLayerChain chain = new BiomeLayerChain(chainState.getPreviousLayer(), chainState.getAllBackwardRefs());
		
		// Area factory to return
		AreaFactory<A> areaFactory = null;
		
		// Compose every layer in the sequence
		for(BiomeLayerConfigured configuredLayer : configuredLayers) {
			BiomeLayer layer = configuredLayer.biomeLayer();
			BiomeLayerContextResolver resolver = configuredLayer.contextResolver();
			BiomeLayerContext<A> childContext = resolver.createContext(context.getContextFactory());
			chain.nextLayer(layer, childContext);
			
			layer.compose(childContext, chain);
			areaFactory = layer.createAreaFactory(childContext, chain);
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
