package thebetweenlands.common.world.gen.layer;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRef;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public record SequenceBiomeLayer(List<BiomeLayerConfigured> layers) implements BiomeLayer {

	public static final MapCodec<SequenceBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ExtraCodecs.nonEmptyList(BiomeLayerConfigured.CODEC.listOf()).fieldOf("layers").forGetter(SequenceBiomeLayer::layers)
				).apply(instance, SequenceBiomeLayer::new)
		);

	@Override
	public boolean referencesPreviousLayer() {
		return true;
	}
	
	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		// Get all layers to compose
		List<BiomeLayerConfigured> configuredLayers = this.layers();
		
		if(configuredLayers == null || configuredLayers.size() == 0) {
			throw new IllegalStateException("SequenceBiomeLayer cannot have zero child layers");
		}

		// Chain so every element knows what's happening (and can access elements in parent scopes, as well)
		BiomeLayerChain chain = new BiomeLayerChain(chainState.getPreviousLayer(), chainState.getAllBackwardRefs());
		
		// Area factory to return
		BiomeLayer childLayer = null;
		BiomeLayerContext<A> childContext = null;
		
		// Compose every layer in the sequence
		for(BiomeLayerConfigured configuredLayer : configuredLayers) {
			childLayer = configuredLayer.biomeLayer();
			
			// Create new context for child
			childContext = context.useRandomFactory(configuredLayer.contextResolver());
			
			// Next Layer
			chain.nextLayer(childLayer, childContext);
			
			// Compose each child
			childLayer.compose(childContext, chain);
		}
		
		// Finish the chain
		Optional<BiomeLayerRef> finalRef = chain.finish();
		
		// Get the child's chain state
		BiomeLayerChainState childChainState = finalRef.orElseThrow().chainState();

		// Create the final area factory
		return childLayer.createAreaFactory(childContext, childChainState);
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}
}
