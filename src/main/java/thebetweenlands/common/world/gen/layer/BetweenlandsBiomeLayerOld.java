package thebetweenlands.common.world.gen.layer;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.layer.util.AreaTransformer0;
import thebetweenlands.common.world.gen.layer.util.Context;
import thebetweenlands.common.world.gen.warp.BLBiomeData;

public class BetweenlandsBiomeLayerOld implements AreaTransformer0 {
	private final HolderGetter<Biome> registry;
	private final List<BLBiomeData> biomes;
	private int totalWeight = 0;

	public BetweenlandsBiomeLayerOld(HolderGetter<Biome> registry, List<BLBiomeData> biomes) {
		this.registry = registry;
		this.biomes = biomes;

		for (BLBiomeData biome : biomes) {
			if (biome.terrainPoint().weight() > 0 && !BetweenlandsConfig.debug) {
				this.totalWeight += biome.terrainPoint().weight();
			}
		}
	}

	@Override
	public int apply(Context context, int x, int z) {
		return BetweenlandsBiomeSource.getBiomeId(this.getRandomItem(biomes, context.nextRandom(totalWeight)).getKey(), registry);
	}

	public Holder<Biome> getRandomItem(List<BLBiomeData> list, int weight) {
		if (list.isEmpty())
			return null;

		if(totalWeight == 0)
			return list.getFirst().biome();

		for (BLBiomeData obj : list) {
			weight -= obj.terrainPoint().weight();
			if (weight < 0)
				return obj.biome();
		}
		return null;
	}
}
