package thebetweenlands.common.registries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.include.com.google.common.collect.ImmutableList;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerConfigured;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.layer.BackwardRefBiomeLayer;
import thebetweenlands.common.world.gen.layer.BetweenlandsBiomeLayer;
import thebetweenlands.common.world.gen.layer.MarkerBiomeLayer;
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
					
					multiZoom(registry, biomeSize - 1, 2345L)
					
//				) // Alternative version of multiZoom
//				.addAll(multiZoomList(registry, biomeSize - 1, 2345L))
//				.add(
					
					// Here you'd put the swamplands clearing and sludge plains clearing mixers
				)
				.build()
			);
	}
	
	
}
