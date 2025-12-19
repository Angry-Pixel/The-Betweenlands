package thebetweenlands.common.registries;

import com.mojang.serialization.MapCodec;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.gen.layer.BetweenlandsBiomeLayer;

public class BiomeLayerRegistry {
	public static final DeferredRegister<MapCodec<? extends BiomeLayer>> BIOME_LAYER_TYPE = DeferredRegister.create(BLRegistries.Keys.BIOME_LAYER_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<MapCodec<? extends BiomeLayer>, MapCodec<BetweenlandsBiomeLayer>> BETWEENLANDS_BIOME_LAYER = BIOME_LAYER_TYPE.register("betweenlands", () -> BetweenlandsBiomeLayer.CODEC);
}
