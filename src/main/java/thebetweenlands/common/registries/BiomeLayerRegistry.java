package thebetweenlands.common.registries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.include.com.google.common.collect.ImmutableList;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.layer.BackwardRefBiomeLayer;
import thebetweenlands.common.world.gen.layer.BetweenlandsBiomeLayer;
import thebetweenlands.common.world.gen.layer.CircleMaskBiomeLayer;
import thebetweenlands.common.world.gen.layer.MarkerBiomeLayer;
import thebetweenlands.common.world.gen.layer.MaskMixerBiomeLayer;
import thebetweenlands.common.world.gen.layer.PreviousLayerBiomeLayer;
import thebetweenlands.common.world.gen.layer.SequenceBiomeLayer;
import thebetweenlands.common.world.gen.layer.SurroundedBiomeLayer;
import thebetweenlands.common.world.gen.layer.ThinningMaskBiomeLayer;
import thebetweenlands.common.world.gen.layer.ZoomBiomeLayer;
import thebetweenlands.common.world.gen.warp.BLBiomeData;

public class BiomeLayerRegistry {
	public static final DeferredRegister<MapCodec<? extends BiomeLayer>> BIOME_LAYER_TYPE = DeferredRegister.create(BLRegistries.Keys.BIOME_LAYER_TYPE, TheBetweenlands.ID);

	// Layers used solely to reference other layers
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<MarkerBiomeLayer>> MARKER_LAYER = BIOME_LAYER_TYPE.register("marker", () -> MarkerBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<PreviousLayerBiomeLayer>> PREVIOUS_LAYER = BIOME_LAYER_TYPE.register("previous", () -> PreviousLayerBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<BackwardRefBiomeLayer>> BACKWARD_REF_LAYER = BIOME_LAYER_TYPE.register("reference", () -> BackwardRefBiomeLayer.CODEC);
	
	// Sequence, this is where chaining comes from
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<SequenceBiomeLayer>> SEQUENCE_LAYER = BIOME_LAYER_TYPE.register("sequence", () -> SequenceBiomeLayer.CODEC);
	
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

	
	
	public static BiomeLayerConfigured betweenlands(HolderGetter<Biome> registry, List<BLBiomeData> biomes, long seed) {
		return BiomeLayerConfigured.of(new BetweenlandsBiomeLayer(registry, biomes), seed);
	}

	
	
	public static BiomeLayerConfigured legacyZoom(HolderGetter<Biome> registry, BiomeLayerConfigured parent, long seed) {
		return BiomeLayerConfigured.of(new ZoomBiomeLayer(registry, parent), seed);
	}

	public static BiomeLayerConfigured legacyZoom(HolderGetter<Biome> registry, long seed) {
		return legacyZoom(registry, previous(), seed);
	}

	
	
	public static BiomeLayerConfigured multiZoom(HolderGetter<Biome> registry, int zoom, long seed) {
		BiomeLayerConfigured parent = previous();
		
		for(int i = 0; i < zoom; ++i) {
			parent = legacyZoom(registry, parent, seed + i);
		}
		
		return parent;
	}

	public static List<BiomeLayerConfigured> multiZoomList(HolderGetter<Biome> registry, int zoom, long seed) {
		List<BiomeLayerConfigured> list = new ArrayList<>(zoom);
		
		for(int i = 0; i < zoom; ++i) {
			list.add(legacyZoom(registry, seed + i));
		}
		
		return list;
	}
	
	

	public static BiomeLayerConfigured surrounded(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, int spawnChance, boolean mask, Holder<Biome> biome, Holder<Biome> surroundingBiome, long seed) {
		return BiomeLayerConfigured.of(new SurroundedBiomeLayer(registry, parent, checkRange, spawnChance, mask, biome, surroundingBiome), seed);
	}
	
	public static BiomeLayerConfigured surrounded(HolderGetter<Biome> registry, int checkRange, int spawnChance, boolean mask, Holder<Biome> biome, Holder<Biome> surroundingBiome, long seed) {
		return surrounded(registry, previous(), checkRange, spawnChance, mask, biome, surroundingBiome, seed);
	}
	
	public static BiomeLayerConfigured surrounded(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, int spawnChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> surroundingBiome, long seed) {
		return surrounded(registry, parent, checkRange, spawnChance, mask, registry.getOrThrow(biome), registry.getOrThrow(surroundingBiome), seed);
	}
	
	public static BiomeLayerConfigured surrounded(HolderGetter<Biome> registry, int checkRange, int spawnChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> surroundingBiome, long seed) {
		return surrounded(registry, previous(), checkRange, spawnChance, mask, registry.getOrThrow(biome), registry.getOrThrow(surroundingBiome), seed);
	}

	
	
	public static BiomeLayerConfigured thin(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, long seed) {
		return BiomeLayerConfigured.of(new ThinningMaskBiomeLayer(registry, parent, checkRange, removalChance, mask, biome, removingBiome), seed);
	}
	
	public static BiomeLayerConfigured thin(HolderGetter<Biome> registry, int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, long seed) {
		return thin(registry, previous(), checkRange, removalChance, mask, biome, removingBiome, seed);
	}
	
	public static BiomeLayerConfigured thin(HolderGetter<Biome> registry, int checkRange, int removalChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> removingBiome, long seed) {
		return thin(registry, previous(), checkRange, removalChance, mask, registry.getOrThrow(biome), registry.getOrThrow(removingBiome), seed);
	}

	public static BiomeLayerConfigured repeatThin(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, int count, long seed) {
		for(int i = 0; i < count; ++i) {
			parent = thin(registry, parent, checkRange, removalChance, mask, biome, removingBiome, seed + i);
		}
		
		return parent;
	}

	public static BiomeLayerConfigured repeatThin(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, int removalChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> removingBiome, int count, long seed) {
		return repeatThin(registry, parent, checkRange, removalChance, mask, registry.getOrThrow(biome), registry.getOrThrow(removingBiome), count, seed);
	}
	
	public static BiomeLayerConfigured repeatThin(HolderGetter<Biome> registry, int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, int count, long seed) {
		return repeatThin(registry, previous(), checkRange, removalChance, mask, biome, removingBiome, count, seed);
	}

	public static BiomeLayerConfigured repeatThin(HolderGetter<Biome> registry, int checkRange, int removalChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> removingBiome, int count, long seed) {
		return repeatThin(registry, checkRange, removalChance, mask, registry.getOrThrow(biome), registry.getOrThrow(removingBiome), count, seed);
	}

	public static List<BiomeLayerConfigured> repeatThinList(HolderGetter<Biome> registry, int checkRange, int removalChance, boolean mask, Holder<Biome> biome, Holder<Biome> removingBiome, int count, long seed) {
		List<BiomeLayerConfigured> list = new ArrayList<>(count);
		
		for(int i = 0; i < count; ++i) {
			list.add(thin(registry, previous(), checkRange, removalChance, mask, biome, removingBiome, seed + i));
		}
		
		return list;
	}

	public static List<BiomeLayerConfigured> repeatThinList(HolderGetter<Biome> registry, int checkRange, int removalChance, boolean mask, ResourceKey<Biome> biome, ResourceKey<Biome> removingBiome, int count, long seed) {
		return repeatThinList(registry, checkRange, removalChance, mask, registry.getOrThrow(biome), registry.getOrThrow(removingBiome), count, seed);
	}
	

	public static BiomeLayerConfigured circleMask(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, Holder<Biome> biome) {
		return BiomeLayerConfigured.unconfigured(new CircleMaskBiomeLayer(registry, parent, checkRange, biome));
	}

	public static BiomeLayerConfigured circleMask(HolderGetter<Biome> registry, BiomeLayerConfigured parent, int checkRange, ResourceKey<Biome> biome) {
		return circleMask(registry, parent, checkRange, registry.getOrThrow(biome));
	}

	public static BiomeLayerConfigured circleMask(HolderGetter<Biome> registry, int checkRange, Holder<Biome> biome) {
		return circleMask(registry, previous(), checkRange, biome);
	}

	public static BiomeLayerConfigured circleMask(HolderGetter<Biome> registry, int checkRange, ResourceKey<Biome> biome) {
		return circleMask(registry, previous(), checkRange, registry.getOrThrow(biome));
	}
	
	
	public static BiomeLayerConfigured mix(BiomeLayerConfigured parentLayer, BiomeLayerConfigured maskLayer) {
		return BiomeLayerConfigured.unconfigured(new MaskMixerBiomeLayer(parentLayer, maskLayer));
	}
	
	public static BiomeLayerConfigured mix(BiomeLayerConfigured maskLayer) {
		return BiomeLayerConfigured.unconfigured(new MaskMixerBiomeLayer(previous(), maskLayer));
	}
	
	public static BiomeLayerConfigured betweenlandsBiomeLayers(HolderGetter<Biome> registry, List<BLBiomeData> biomeParameters, int biomeSize) {
		return sequence(
				ImmutableList.<BiomeLayerConfigured>builder()
				.add(
					betweenlands(registry, biomeParameters, 100L),
					legacyZoom(registry, 2000L),
					legacyZoom(registry, 2001L),
					marker("swamplands_clearing_zoom"),
					
					legacyZoom(registry, 2345L),
					marker("sludge_plains_clearing_zoom"),
					
					multiZoom(registry, biomeSize - 1, 2345L),
					
//				) // Alternative version of multiZoom
//				.addAll(multiZoomList(registry, biomeSize - 1, 2345L))
//				.add(
					// Here you'd put the swamplands clearing and sludge plains clearing mixers
					
					// Swamplands Clearing mixer
					mix(
							sequence(
								surrounded(
										registry,
											reference("swamplands_clearing_zoom"),
										1, // 1 check radius
										10000, // 100.00% placement chance
										true, // mask (anything that wasn't placed by this is removed)
										BiomeRegistry.SWAMPLANDS_CLEARING, // place a swamplands clearing
										BiomeRegistry.SWAMPLANDS, // when it's surrounded by swamplands
										102L // seed offset
									),
								repeatThin(
										registry,
										3, // 3 check radius
										2500, // 25.00% removal chance
										false, // Don't treat this as a mask (though it doesn't matter here)
										BiomeRegistry.SWAMPLANDS_CLEARING, // when you find a swamplands clearing
										BiomeRegistry.SWAMPLANDS_CLEARING, // maybe remove if there's a nearby swamplands clearing
										10, // repeat 10 times
										105L // seed offset
									),
								legacyZoom(registry, 2345L),
								multiZoom(registry, biomeSize - 1, 2345L),
								circleMask(registry, 10, BiomeRegistry.SWAMPLANDS_CLEARING)
							)
						)
				)
				.build()
			);
	}
	
	
}
