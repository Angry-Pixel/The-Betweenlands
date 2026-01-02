package thebetweenlands.common.world.gen.layer;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.AreaFactory;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerChainState;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContextResolver;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRef;
import thebetweenlands.api.world.biome.layer.util.BiomeLayerChain;

public record RepeatBiomeLayer(BiomeLayer biomeLayer, List<BiomeLayerRandomContextResolver> contextResolvers) implements BiomeLayer {

	public static final MapCodec<RepeatBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BiomeLayer.CODEC.fieldOf("biome_layer").forGetter(RepeatBiomeLayer::biomeLayer),
					ExtraCodecs.nonEmptyList(BiomeLayerRandomContextResolver.CODEC.listOf()).fieldOf("random_seeds").forGetter(RepeatBiomeLayer::contextResolvers)
				).apply(instance, RepeatBiomeLayer::new)
		);
	
	@Override
	public boolean referencesPreviousLayer() {
		return true;
	}
	
	@Override
	public <A extends Area> AreaFactory<A> createAreaFactory(BiomeLayerContext<A> context, BiomeLayerChainState chainState) {
		// The layer we'll configure
		BiomeLayer biomeLayer = this.biomeLayer();
		
		// The seeds we'll use to configure this layer
		List<BiomeLayerRandomContextResolver> contextResolvers = this.contextResolvers();
		
		if(contextResolvers == null || contextResolvers.size() == 0) {
			throw new IllegalStateException("RepeatBiomeLayer cannot have zero seeds");
		}

		// Chain so every element knows what's happening (and can access elements in parent scopes, as well)
		BiomeLayerChain chain = new BiomeLayerChain(chainState.getPreviousLayer(), chainState.getAllBackwardRefs());
		
		// Area factory to return
		BiomeLayerContext<A> childContext = null;
		
		// Compose every layer in the sequence
		for(BiomeLayerRandomContextResolver contextResolver : contextResolvers) {
			// Create new context for child
			childContext = context.useRandomFactory(contextResolver);
			
			// Next Layer
			chain.nextLayer(biomeLayer, childContext);
			
			// Compose each child
			biomeLayer.compose(childContext, chain);
		}
		
		// Finish the chain
		Optional<BiomeLayerRef> finalRef = chain.finish();
		
		// Get the child's chain state
		BiomeLayerChainState childChainState = finalRef.orElseThrow().chainState();

		// Create the final area factory
		return biomeLayer.createAreaFactory(childContext, childChainState);
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
