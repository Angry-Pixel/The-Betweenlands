package thebetweenlands.common.world.gen.layer;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.api.world.biome.layer.BiomeLayer;
import thebetweenlands.api.world.biome.layer.SimpleBiomeLayer;
import thebetweenlands.api.world.biome.layer.context.BiomeLayerContext;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.layer.util.BLWeightPoint;

public class BetweenlandsBiomeLayer implements SimpleBiomeLayer {
	
	public static final MapCodec<BetweenlandsBiomeLayer> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BLWeightPoint.CODEC.listOf().fieldOf("biomes").forGetter(o -> o.biomes)
				).apply(instance, BetweenlandsBiomeLayer::new)
		);
	
	private final List<BLWeightPoint> biomes;
	private int totalWeight = 0;

	public BetweenlandsBiomeLayer(List<BLWeightPoint> biomes) {
		this.biomes = biomes;

		for (BLWeightPoint biome : biomes) {
			if (biome.weight() > 0 && !BetweenlandsConfig.debug) {
				this.totalWeight += biome.weight();
			}
		}
	}

	@Override
	public MapCodec<? extends BiomeLayer> codec() {
		return CODEC;
	}

	@Override
	public <A extends Area> int apply(BiomeLayerContext<A> context, RandomSource random, int x, int z) {
		return BetweenlandsBiomeSource.getBiomeId(this.getRandomItem(this.biomes, random.nextInt(this.totalWeight)));
	}
	
	public Holder<Biome> getRandomItem(List<BLWeightPoint> list, int weight) {
		if (list.isEmpty())
			return null;

		if(this.totalWeight == 0)
			return list.getFirst().biome();

		for (BLWeightPoint obj : list) {
			weight -= obj.weight();
			if (weight < 0)
				return obj.biome();
		}
		return null;
	}
}