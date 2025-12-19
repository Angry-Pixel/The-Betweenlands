package thebetweenlands.common.registries;

import com.mojang.serialization.MapCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.layer.BackwardRefBiomeLayer;
import thebetweenlands.common.world.gen.layer.BetweenlandsBiomeLayer;
import thebetweenlands.common.world.gen.layer.MarkerBiomeLayer;
import thebetweenlands.common.world.gen.layer.PreviousLayerBiomeLayer;
import thebetweenlands.common.world.gen.layer.SequenceBiomeLayer;
import thebetweenlands.common.world.gen.layer.ZoomBiomeLayer;

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
	
}
