package thebetweenlands.common.world.gen.layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.SimpleBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;

public class ZoomBiomeLayer implements SimpleBiomeLayer {

	public static final MapCodec<ZoomBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RegistryOps.retrieveGetter(Registries.BIOME),
					BiomeLayerConfigured.CODEC.optionalFieldOf("parent", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.parent),
					Codec.INT.fieldOf("zoom").forGetter(o -> o.zoom)
				).apply(instance, ZoomBiomeLayer::new)
		);

	private final BiomeLayerConfigured parent;
	private final int zoom;
	
	public ZoomBiomeLayer(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int zoom) {
		this.parent = parent;
		this.zoom = zoom;
	}
	
	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}
	
	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, RandomSource random, int x, int z) {
		return 0;
	}
	
}
