package thebetweenlands.common.registries;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerRandomContextResolver.ContextResolverHolder;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.layer.BackwardRefBiomeLayer;
import thebetweenlands.common.world.gen.layer.BetweenlandsBiomeLayer;
import thebetweenlands.common.world.gen.layer.CircleMaskBiomeLayer;
import thebetweenlands.common.world.gen.layer.MarkerBiomeLayer;
import thebetweenlands.common.world.gen.layer.MaskMixerBiomeLayer;
import thebetweenlands.common.world.gen.layer.PreviousLayerBiomeLayer;
import thebetweenlands.common.world.gen.layer.RepeatBiomeLayer;
import thebetweenlands.common.world.gen.layer.SequenceBiomeLayer;
import thebetweenlands.common.world.gen.layer.SpreadBiomeLayer;
import thebetweenlands.common.world.gen.layer.SpreadBiomeLayer.Quadrant;
import thebetweenlands.common.world.gen.layer.SurroundedBiomeLayer;
import thebetweenlands.common.world.gen.layer.ThinningMaskBiomeLayer;
import thebetweenlands.common.world.gen.layer.ZoomBiomeLayer;
import thebetweenlands.common.world.gen.layer.util.BLWeightPoint;

public class BiomeLayerRegistry {
	public static final DeferredRegister<MapCodec<? extends BiomeLayer>> BIOME_LAYER_TYPE = DeferredRegister.create(BLRegistries.Keys.BIOME_LAYER_TYPE, TheBetweenlands.ID);

	// Layers used solely to reference other layers
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<MarkerBiomeLayer>> MARKER_LAYER = BIOME_LAYER_TYPE.register("marker", () -> MarkerBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<PreviousLayerBiomeLayer>> PREVIOUS_LAYER = BIOME_LAYER_TYPE.register("previous", () -> PreviousLayerBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<BackwardRefBiomeLayer>> BACKWARD_REF_LAYER = BIOME_LAYER_TYPE.register("reference", () -> BackwardRefBiomeLayer.CODEC);

	// Sequence, this is where chaining comes from
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<SequenceBiomeLayer>> SEQUENCE_LAYER = BIOME_LAYER_TYPE.register("sequence", () -> SequenceBiomeLayer.CODEC);

	// Repeat, calls the same biome layer multiple times with multiple different seeds
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<RepeatBiomeLayer>> REPEAT_LAYER = BIOME_LAYER_TYPE.register("repeat", () -> RepeatBiomeLayer.CODEC);
	
	static {
		// Add an alias from thebetweenlands:chain to thebetweenlands:sequence (so "thebetweenlands:chain" gets treated as "thebetweenlands:sequence")
		BIOME_LAYER_TYPE.addAlias(TheBetweenlands.prefix("chain"), SEQUENCE_LAYER.getId());
	}
	
	// Layers that actually place biomes
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<BetweenlandsBiomeLayer>> BETWEENLANDS_BIOME_LAYER = BIOME_LAYER_TYPE.register("betweenlands", () -> BetweenlandsBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<ZoomBiomeLayer>> ZOOM_BIOME_LAYER = BIOME_LAYER_TYPE.register("zoom", () -> ZoomBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<SurroundedBiomeLayer>> SURROUNDED_BIOME_LAYER = BIOME_LAYER_TYPE.register("surrounded", () -> SurroundedBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<ThinningMaskBiomeLayer>> THINNING_MASK_BIOME_LAYER = BIOME_LAYER_TYPE.register("thinning_mask", () -> ThinningMaskBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<MaskMixerBiomeLayer>> MASK_MIXER_BIOME_LAYER = BIOME_LAYER_TYPE.register("mask_mixer", () -> MaskMixerBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<CircleMaskBiomeLayer>> CIRCLE_MASK_BIOME_LAYER = BIOME_LAYER_TYPE.register("circle_mask", () -> CircleMaskBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<SpreadBiomeLayer>> SPREAD_BIOME_LAYER = BIOME_LAYER_TYPE.register("spread", () -> SpreadBiomeLayer.CODEC);


	// Util methods for repeating layers
	
	
	public static long[] createSeeds(int count, long seed, boolean incrementSeed) {
		if(incrementSeed) {
			return IntStream.range(0, count)
					.mapToLong(i -> seed + (long)i)
					.toArray();
		} else {
			long[] seeds = new long[count];
			Arrays.fill(seeds, seed);
			return seeds;
		}
	}

	/**
	 * Repeats a biome layer with the specified {@code long} seeds
	 * @param biomeLayer the biome layer to be repeated
	 * @param seeds the seeds to use
	 * @return a configured RepeatBiomeLayer with the specified layer and seeds
	 */
	public static BiomeLayerConfigured repeatLayer(BiomeLayer biomeLayer, long[] seeds) {
		if(seeds.length == 0) {
			throw new IllegalArgumentException("repeatLayer requires at least one seed");
		}
		
		// Only zooming in once means we don't need the repeater
		if(seeds.length == 1) {
			return BiomeLayerConfigured.of(biomeLayer, seeds[0]);
		}

		// Seeds to use
		List<ContextResolverHolder> contextResolvers = Arrays.stream(seeds)
				.mapToObj(ContextResolverHolder::ofLong)
				.toList();
		
		// Repeat original layer with seeds
		BiomeLayer repeatLayer = new RepeatBiomeLayer(biomeLayer, contextResolvers);
		
		return BiomeLayerConfigured.unconfigured(repeatLayer);
	}

	
	// Methods for creating biome layers
	
	
	public static BiomeLayerConfigured sequence(BiomeLayerConfigured ...layers) {
		return BiomeLayerConfigured.unconfigured(new SequenceBiomeLayer(Arrays.asList(layers)));
	}

	public static BiomeLayerConfigured sequence(List<BiomeLayerConfigured> layers) {
		return BiomeLayerConfigured.unconfigured(new SequenceBiomeLayer(layers));
	}
	
	
	
	public static BiomeLayerConfigured previous() {
		return PreviousLayerBiomeLayer.CONFIGURED_INSTANCE;
	}
	
	
	
	public static BiomeLayerConfigured marker(String name) {
		return BiomeLayerConfigured.unconfigured(new MarkerBiomeLayer(name));
	}

	
	
	public static BiomeLayerConfigured reference(String name) {
		return BiomeLayerConfigured.unconfigured(new BackwardRefBiomeLayer(name));
	}

	
	
	public static BiomeLayerConfigured betweenlands(List<BLWeightPoint> biomes, long seed) {
		return BiomeLayerConfigured.of(new BetweenlandsBiomeLayer(biomes), seed);
	}



	public static BiomeLayerConfigured legacyZoom(BiomeLayerConfigured parent, long seed) {
		return BiomeLayerConfigured.of(new ZoomBiomeLayer(parent), seed);
	}

	public static BiomeLayerConfigured legacyZoom(long seed) {
		return legacyZoom(previous(), seed);
	}

	// Multi zoom
	public static BiomeLayerConfigured multiZoomVar(long[] seeds) {
		if(seeds.length == 0) {
			throw new IllegalArgumentException("must zoom in at least once");
		}
		
		return repeatLayer(new ZoomBiomeLayer(previous()), seeds);
	}

	public static BiomeLayerConfigured multiZoom(int zoom, long seed, boolean incrementSeed) {
		return multiZoomVar(createSeeds(zoom, seed, incrementSeed));
	}

	public static BiomeLayerConfigured multiZoom(int zoom, long seed) {
		return multiZoom(zoom, seed, false);
	}


	// Stand-in for legacy ZoomIncrementLayer with increment = true
	public static BiomeLayerConfigured legacySpread(BiomeLayerConfigured parent, long seed) {
		return BiomeLayerConfigured.of(new SpreadBiomeLayer(parent, Quadrant.XNZN), seed);
	}

	public static BiomeLayerConfigured legacySpread(long seed) {
		return legacySpread(previous(), seed);
	}

	// Multi spread
	public static BiomeLayerConfigured multiSpreadVar(Quadrant quadrant, long[] seeds) {
		if(seeds.length == 0) {
			throw new IllegalArgumentException("must spread at least once");
		}
		
		return repeatLayer(new SpreadBiomeLayer(previous(), quadrant), seeds);
	}

	public static BiomeLayerConfigured multiSpread(Quadrant quadrant, int count, long seed, boolean incrementSeed) {
		return multiSpreadVar(quadrant, createSeeds(count, seed, incrementSeed));
	}
	
	public static BiomeLayerConfigured multiSpread(Quadrant quadrant, int count, long seed) {
		return multiSpread(quadrant, count, seed, false);
	}
	
	public static BiomeLayerConfigured multiSpreadVar(long[] seeds) {
		return multiSpreadVar(Quadrant.XNZN, seeds);
	}

	public static BiomeLayerConfigured multiSpread(int count, long seed, boolean incrementSeed) {
		return multiSpread(Quadrant.XNZN, count, seed, incrementSeed);
	}

	public static BiomeLayerConfigured multiSpread(int count, long seed) {
		return multiSpread(Quadrant.XNZN, count, seed);
	}
	

	public static BiomeLayerConfigured surrounded(BiomeLayerConfigured parent, int checkRange, int spawnChance, boolean mask, Holder<Biome> biome, Holder<Biome> surroundingBiome, long seed) {
		return BiomeLayerConfigured.of(new SurroundedBiomeLayer(parent, checkRange, spawnChance, mask, biome, surroundingBiome), seed);
	}
	
	public static BiomeLayerConfigured surrounded(int checkRange, int spawnChance, boolean mask, Holder<Biome> biome, Holder<Biome> surroundingBiome, long seed) {
		return surrounded(previous(), checkRange, spawnChance, mask, biome, surroundingBiome, seed);
	}
	
	public static BiomeLayerConfigured surrounded(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, int spawnChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> surroundingBiome, long seed) {
		return surrounded(parent, checkRange, spawnChance, mask, registry.getOrThrow(biome), registry.getOrThrow(surroundingBiome), seed);
	}
	
	public static BiomeLayerConfigured surrounded(HolderGetter<Biome> registry, int checkRange, int spawnChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> surroundingBiome, long seed) {
		return surrounded(previous(), checkRange, spawnChance, mask, registry.getOrThrow(biome), registry.getOrThrow(surroundingBiome), seed);
	}

	
	
	public static BiomeLayerConfigured thin(BiomeLayerConfigured parent, int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, long seed) {
		return BiomeLayerConfigured.of(new ThinningMaskBiomeLayer(parent, checkRange, removalChance, mask, biome, removingBiome), seed);
	}
	
	public static BiomeLayerConfigured thin(int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, long seed) {
		return thin(previous(), checkRange, removalChance, mask, biome, removingBiome, seed);
	}
	
	public static BiomeLayerConfigured thin(HolderGetter<Biome> registry, int checkRange, int removalChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> removingBiome, long seed) {
		return thin(previous(), checkRange, removalChance, mask, registry.getOrThrow(biome), registry.getOrThrow(removingBiome), seed);
	}

	public static BiomeLayerConfigured repeatThinVar(int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, long[] seeds) {
		if(seeds.length == 0) {
			throw new IllegalArgumentException("must thin at least once");
		}
		
		return repeatLayer(new ThinningMaskBiomeLayer(previous(), checkRange, removalChance, mask, biome, removingBiome), seeds);
	}
	
	public static BiomeLayerConfigured repeatThin(int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, int count, long seed, boolean incrementSeed) {
		return repeatThinVar(checkRange, removalChance, mask, biome, removingBiome, createSeeds(count, seed, incrementSeed));
	}

	public static BiomeLayerConfigured repeatThin(HolderGetter<Biome> registry, int checkRange, int removalChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> removingBiome, int count, long seed, boolean incrementSeed) {
		return repeatThin(checkRange, removalChance, mask, registry.getOrThrow(biome), registry.getOrThrow(removingBiome), count, seed, incrementSeed);
	}
	

	public static BiomeLayerConfigured circleMask(BiomeLayerConfigured parent, int checkRange, boolean mask, Holder<Biome> biome) {
		return BiomeLayerConfigured.unconfigured(new CircleMaskBiomeLayer(parent, checkRange, mask, biome));
	}

	public static BiomeLayerConfigured circleMask(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, boolean mask, ResourceKey<Biome> biome) {
		return circleMask(parent, checkRange, mask, registry.getOrThrow(biome));
	}

	public static BiomeLayerConfigured circleMask(int checkRange, boolean mask, Holder<Biome> biome) {
		return circleMask(previous(), checkRange, mask, biome);
	}

	public static BiomeLayerConfigured circleMask(HolderGetter<Biome> registry, int checkRange, boolean mask, ResourceKey<Biome> biome) {
		return circleMask(previous(), checkRange, mask, registry.getOrThrow(biome));
	}
	
	
	public static BiomeLayerConfigured mix(BiomeLayerConfigured parentLayer, BiomeLayerConfigured maskLayer) {
		return BiomeLayerConfigured.unconfigured(new MaskMixerBiomeLayer(parentLayer, maskLayer));
	}
	
	public static BiomeLayerConfigured mix(BiomeLayerConfigured maskLayer) {
		return BiomeLayerConfigured.unconfigured(new MaskMixerBiomeLayer(previous(), maskLayer));
	}
	
	public static BiomeLayerConfigured betweenlandsBiomeLayers(HolderGetter<Biome> registry, List<BLWeightPoint> biomeParameters, int biomeSize) {
		
		return sequence(
			// Base layers
			betweenlands(biomeParameters, 100L),
			multiZoom(2, 2000L, false),
			marker("swamplands_clearing_zoom"),
			
			// Zoom biomeSize times
			legacyZoom(2345L),
			marker("sludge_plains_clearing_zoom"),
			multiZoom(biomeSize - 1, 2345L, false),
			
			// Swamplands Clearing mixer
			mix(
					sequence(
						surrounded(
								registry,
									reference("swamplands_clearing_zoom"),
								1, // 1 check radius
								100_00, // 100.00% placement chance
								true, // mask (anything that wasn't placed by this is removed)
								BiomeRegistry.SWAMPLANDS_CLEARING, // place a swamplands clearing
								BiomeRegistry.SWAMPLANDS, // when it's surrounded by swamplands
								102L // seed offset
							),
						repeatThin(
								registry,
								3, // 3 check radius
								25_00, // 25.00% removal chance
								false, // Don't treat this as a mask (though it doesn't matter here)
								BiomeRegistry.SWAMPLANDS_CLEARING, // when you find a swamplands clearing
								BiomeRegistry.SWAMPLANDS_CLEARING, // maybe remove if there's a nearby swamplands clearing
								10, // repeat 10 times
								105L, // seed offset
								true
							),
						multiSpread(biomeSize, 2345L, false),
						circleMask(registry, 10, true, BiomeRegistry.SWAMPLANDS_CLEARING)
					)
				),

			// Sludge Plains Clearing mixer
			mix(
					sequence(
						surrounded(
								registry,
									reference("sludge_plains_clearing_zoom"),
								2, // 2 check radius
								100_00, // 100.00% placement chance
								true, // mask (anything that wasn't placed by this is removed)
								BiomeRegistry.SLUDGE_PLAINS_CLEARING, // place a sludge plains clearing
								BiomeRegistry.SLUDGE_PLAINS, // when it's surrounded by sludge plains
								351L // seed offset
							),
						repeatThin(
								registry,
								4, // 4 check radius
								15_00, // 15.00% removal chance
								false, // Don't treat this as a mask (though it doesn't matter here)
								BiomeRegistry.SLUDGE_PLAINS_CLEARING, // when you find a swamplands clearing
								BiomeRegistry.SLUDGE_PLAINS_CLEARING, // maybe remove if there's a nearby swamplands clearing
								20, // repeat 20 times
								214L, // seed offset
								true
							),
						multiSpread(biomeSize - 1 - 2, 2345L, false),
						circleMask(registry, 3, true, BiomeRegistry.SLUDGE_PLAINS_CLEARING),
						multiZoom(2, 2542L, false)
					)
				)
		);
	}
	
	
}
