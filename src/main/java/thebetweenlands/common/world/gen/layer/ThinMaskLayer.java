package thebetweenlands.common.world.gen.layer;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.layer.util.Area;
import thebetweenlands.common.world.gen.layer.util.AreaTransformer1;
import thebetweenlands.common.world.gen.layer.util.BigContext;

public class ThinMaskLayer implements AreaTransformer1 {

	private final HolderGetter<Biome> registry;
	private final ResourceKey<Biome> id;
	private final int range;
	private final float removeChance;

	public ThinMaskLayer(HolderGetter<Biome> registry, ResourceKey<Biome> id, int range, float chance) {
		this.registry = registry;
		this.id = id;
		this.range = range;
		this.removeChance = chance;
	}

	@Override
	public int getParentX(int x) {
		return x - range;
	}

	@Override
	public int getParentY(int y) {
		return y - range;
	}

	@Override
	public int apply(BigContext<?> context, Area area, int x, int z) {
		for (int xo = 0; xo <= this.range; xo++) {
			for (int zo = 0; zo <= this.range; zo++) {
				if ((xo != 0 || zo != 0) && xo * xo + zo * zo <= this.range * this.range + 1) {
					if (area.get(getParentX(x + xo), getParentY(z + zo)) == BetweenlandsBiomeSource.getBiomeId(id, registry) && context.nextRandom(10000) <= removeChance * 10000) {
						return -1;
					}
				}
			}
		}

		return area.get(x, z);
	}
}
