package thebetweenlands.common.world.gen.layer;

import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.SingleParentBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;

public class ThinningMaskBiomeLayer implements SingleParentBiomeLayer {

	public static final MapCodec<ThinningMaskBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RegistryOps.retrieveGetter(Registries.BIOME),
					BiomeLayerConfigured.CODEC.optionalFieldOf("parent", PreviousLayerBiomeLayer.CONFIGURED_INSTANCE).forGetter(o -> o.parent),
					Codec.INT.fieldOf("check_range").forGetter(o -> o.checkRange),
					Codec.xor(Codec.INT, Codec.FLOAT).fieldOf("removal_chance").<Integer>xmap(t -> t.map(Function.identity(), (f) -> (int)(f * 10000)), t -> Either.left(t)).forGetter(o -> o.removalChance),
					Codec.BOOL.fieldOf("mask").forGetter(o -> o.mask),
					Biome.CODEC.fieldOf("biome").forGetter(o -> o.biome),
					Biome.CODEC.fieldOf("remove_when_near").forGetter(o -> o.removingBiome)
				).apply(instance, ThinningMaskBiomeLayer::new)
		);

	private final HolderGetter<Biome> registry;
	private final BiomeLayerConfigured parent;
	// How many cells to check are surrounded
	private final int checkRange;
	// The chance to remove biome when it's within checkRange of removingBiome
	private final int removalChance;
	// Whether to return the original value (mask = false) or -1 (mask = true) when biome isn't found
	private final boolean mask;
	private final Holder<Biome> biome;
	private final Holder<Biome> removingBiome;

	public ThinningMaskBiomeLayer(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, float removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome) {
		this(registry, parent, checkRange, (int)(removalChance * 10000), mask, biome, removingBiome);
	}
	
	public ThinningMaskBiomeLayer(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome) {
		this.registry = registry;
		this.parent = parent;
		this.checkRange = checkRange;
		this.removalChance = removalChance;
		this.mask = mask;
		this.biome = biome;
		this.removingBiome = removingBiome;
	}

	@Override
	public BiomeLayerConfigured getParentLayer() {
		return this.parent;
	}

	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, A parentArea, int x, int z) {
		int existingBiome = parentArea.get(x, z);
		
		int biomeId = BetweenlandsBiomeSource.getBiomeId(this.biome);
		if(existingBiome != biomeId) {
			return this.mask ? -1 : existingBiome;
		}
		
		int removingBiomeId = BetweenlandsBiomeSource.getBiomeId(this.removingBiome);

		RandomSource random = context.createRandom(x, z);
		
		for(int xo = -this.checkRange; xo <= this.checkRange; xo++) {
			for(int zo = -this.checkRange; zo <= this.checkRange; zo++) {
				// Don't check against ourselves
				if(xo == 0 && zo == 0) {
					continue;
				}
				
				// If we're within range
				if(xo * xo + zo * zo <= this.checkRange * this.checkRange + 1) {
					int maybeRemovingBiomeId = parentArea.get(x + xo, z + zo);
					
					if(
						maybeRemovingBiomeId == removingBiomeId // And the biome is removingBiome
						&& random.nextInt(10000) <= this.removalChance // And the removal chance passes
					 ) {
						// Remove this biome
						return -1;
					}
				}
			}
		}
		
		return existingBiome;
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}
}
