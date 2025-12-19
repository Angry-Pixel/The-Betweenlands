package thebetweenlands.common.registries;

import com.mojang.serialization.MapCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.layer.BackwardsRefBiomeLayer;
import thebetweenlands.common.world.gen.layer.BetweenlandsBiomeLayer;
import thebetweenlands.common.world.gen.layer.MarkerBiomeLayer;
import thebetweenlands.common.world.gen.layer.PreviousLayerBiomeLayer;
import thebetweenlands.common.world.gen.layer.ZoomBiomeLayer;

public class BiomeLayerRegistry {
	public static final DeferredRegister<MapCodec<? extends BiomeLayer>> BIOME_LAYER_TYPE = DeferredRegister.create(BLRegistries.Keys.BIOME_LAYER_TYPE, TheBetweenlands.ID);

	// Layers used solely to reference other layers
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<MarkerBiomeLayer>> MARKER_LAYER = BIOME_LAYER_TYPE.register("marker", () -> MarkerBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<PreviousLayerBiomeLayer>> PREVIOUS_LAYER = BIOME_LAYER_TYPE.register("previous", () -> PreviousLayerBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<BackwardsRefBiomeLayer>> BACKWARDS_REF_LAYER = BIOME_LAYER_TYPE.register("reference", () -> BackwardsRefBiomeLayer.CODEC);
	
	// Layers that actually place biomes
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<BetweenlandsBiomeLayer>> BETWEENLANDS_BIOME_LAYER = BIOME_LAYER_TYPE.register("betweenlands", () -> BetweenlandsBiomeLayer.CODEC);
	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<ZoomBiomeLayer>> ZOOM_BIOME_LAYER = BIOME_LAYER_TYPE.register("zoom", () -> ZoomBiomeLayer.CODEC);
	
}
