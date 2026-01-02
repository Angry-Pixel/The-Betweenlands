package thebetweenlands.common.world.gen.layer.old;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.layer.old.util.AreaTransformer0;
import thebetweenlands.common.world.gen.layer.old.util.Context;
import thebetweenlands.common.world.gen.layer.util.BLWeightPoint;
import thebetweenlands.common.world.gen.warp.BLBiomeData;

public class BetweenlandsBiomeLayerOld implements AreaTransformer0 {
	private final HolderGetter<Biome> registry;
	private final List<BLWeightPoint> biomes;
	private int totalWeight = 0;

	public BetweenlandsBiomeLayerOld(HolderGetter<Biome> registry, List<BLWeightPoint> biomes) {
		this.registry = registry;
		this.biomes = biomes;

		for (BLWeightPoint biome : biomes) {
			if (biome.weight() > 0 && !BetweenlandsConfig.debug) {
				this.totalWeight += biome.weight();
			}
		}
	}

	@Override
	public int apply(Context context, int x, int z) {
		return BetweenlandsBiomeSource.getBiomeId(this.getRandomItem(biomes, context.nextRandom(totalWeight)).getKey(), registry);
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
