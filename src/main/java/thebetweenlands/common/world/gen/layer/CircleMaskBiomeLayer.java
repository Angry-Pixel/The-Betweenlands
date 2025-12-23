package thebetweenlands.common.world.gen.layer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.SingleParentBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;

// Looks for a specific biome within a radius, and if found replaces the current biome with it
public class CircleMaskBiomeLayer implements SingleParentBiomeLayer {

	public static final MapCodec<CircleMaskBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RegistryOps.retrieveGetter(Registries.BIOME),
					BiomeLayerConfigured.CODEC.optionalFieldOf("parent", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.parent),
					Codec.INT.fieldOf("check_range").forGetter(o -> o.checkRange),
					Codec.BOOL.fieldOf("mask").forGetter(o -> o.mask),
					Biome.CODEC.fieldOf("biome").forGetter(o -> o.maskBiome)
				).apply(instance, CircleMaskBiomeLayer::new)
		);

	private final HolderGetter<Biome> registry;
	private final BiomeLayerConfigured parent;
	// How many cells to check are surrounded
	private final int checkRange;
	// If false, does not remove cells outside the mask
	private final boolean mask;
	// The biome to search for
	private final Holder<Biome> maskBiome;
	
	public CircleMaskBiomeLayer(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, boolean mask, Holder<Biome> biome) {
		this.registry = registry;
		this.parent = parent;
		this.checkRange = checkRange;
		this.mask = mask;
		this.maskBiome = biome;
	}
	
	@Override
	public BiomeLayerConfigured getParentLayer() {
		return this.parent;
	}

	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, A parentArea, int x, int z) {
		int maskBiomeId = BetweenlandsBiomeSource.getBiomeId(this.maskBiome);

		for(int xo = -this.checkRange; xo <= this.checkRange; xo++) {
			for(int zo = -this.checkRange; zo <= this.checkRange; zo++) {
				if(xo * xo + zo * zo <= this.checkRange * this.checkRange) {
					int biomeID = parentArea.get(x + xo, z + zo);
					if(biomeID == maskBiomeId) {
						return maskBiomeId;
					}
				}
			}
		}
		
		return this.mask ? -1 : parentArea.get(x, z);
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

}
