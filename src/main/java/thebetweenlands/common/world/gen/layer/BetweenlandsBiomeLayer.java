package thebetweenlands.common.world.gen.layer;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.layer.util.*;
import thebetweenlands.common.world.gen.warp.TerrainPoint;

import java.util.List;

public class BetweenlandsBiomeLayer implements AreaTransformer0 {
	private final HolderGetter<Biome> registry;
	private final List<Pair<TerrainPoint, Holder<Biome>>> biomes;
	private int totalWeight = 0;

	public BetweenlandsBiomeLayer(HolderGetter<Biome> registry, List<Pair<TerrainPoint, Holder<Biome>>> biomes) {
		this.registry = registry;
		this.biomes = biomes;

		for (Pair<TerrainPoint, Holder<Biome>> biome : biomes) {
			if (biome.getFirst().weight() > 0 && !BetweenlandsConfig.debug) {
				this.totalWeight += biome.getFirst().weight();
			}
		}
	}

	@Override
	public int apply(Context context, int x, int z) {
		return BetweenlandsBiomeSource.getBiomeId(BiomeRegistry.DEEP_WATERS, registry);
//		return BetweenlandsBiomeSource.getBiomeId(this.getRandomItem(biomes, context.nextRandom(totalWeight)).getKey(), registry);
	}

	public Holder<Biome> getRandomItem(List<Pair<TerrainPoint, Holder<Biome>>> list, int weight) {
		if (list.isEmpty())
			return null;

		if(totalWeight == 0)
			return list.getFirst().getSecond();

		for (Pair<TerrainPoint, Holder<Biome>> obj : list) {
			weight -= obj.getFirst().weight();
			if (weight < 0)
				return obj.getSecond();
		}
		return null;
	}
}
