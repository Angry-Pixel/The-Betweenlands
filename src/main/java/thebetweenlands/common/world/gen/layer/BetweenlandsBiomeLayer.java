package thebetweenlands.common.world.gen.layer;

import java.util.List;

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
import thebetweenlands.api.world.biome.layer.SimpleBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.warp.BLBiomeData;

public class BetweenlandsBiomeLayer implements SimpleBiomeLayer {
	
	public static final MapCodec<BetweenlandsBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					RegistryOps.retrieveGetter(Registries.BIOME),
					BLBiomeData.CODEC.listOf().fieldOf("biomes").forGetter(o -> o.biomes)
				).apply(instance, BetweenlandsBiomeLayer::new)
		);
	
	private final HolderGetter<Biome> registry;
	private final List<BLBiomeData> biomes;
	private int totalWeight = 0;

	public BetweenlandsBiomeLayer(HolderGetter<Biome> registry, List<BLBiomeData> biomes) {
		this.registry = registry;
		this.biomes = biomes;

		for (BLBiomeData biome : biomes) {
			if (biome.terrainPoint().weight() > 0 && !BetweenlandsConfig.debug) {
				this.totalWeight += biome.terrainPoint().weight();
			}
		}
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, RandomSource random, int x, int z) {
		return BetweenlandsBiomeSource.getBiomeId(this.getRandomItem(biomes, random.nextInt(totalWeight)));
	}
	
	public Holder<Biome> getRandomItem(List<BLBiomeData> list, int weight) {
		if (list.isEmpty())
			return null;

		if(this.totalWeight == 0)
			return list.getFirst().biome();

		for (BLBiomeData obj : list) {
			weight -= obj.terrainPoint().weight();
			if (weight < 0)
				return obj.biome();
		}
		return null;
	}
}