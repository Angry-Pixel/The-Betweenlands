package thebetweenlands.common.world.gen.layer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.DoubleParentBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public class MaskMixerBiomeLayer implements DoubleParentBiomeLayer {

	public static final MapCodec<MaskMixerBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BiomeLayerConfigured.CODEC.optionalFieldOf("parent", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.parentLayer),
					BiomeLayerConfigured.CODEC.optionalFieldOf("mask_layer", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.maskLayer)
				).apply(instance, MaskMixerBiomeLayer::new)
		);

	private final BiomeLayerConfigured parentLayer;
	private final BiomeLayerConfigured maskLayer;
	
	public MaskMixerBiomeLayer(BiomeLayerConfigured parentLayer, BiomeLayerConfigured maskLayer) {
		this.parentLayer = parentLayer;
		this.maskLayer = maskLayer;
	}

	@Override
	public BiomeLayerConfigured getFirstParentLayer() {
		return this.parentLayer;
	}

	@Override
	public BiomeLayerConfigured getSecondParentLayer() {
		return this.maskLayer;
	}

	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, A parentArea, A maskLayerArea, int x, int z) {
		int maskBiome = maskLayerArea.get(x, z);
		return maskBiome != -1 ? maskBiome : parentArea.get(x, z);
	}
	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
