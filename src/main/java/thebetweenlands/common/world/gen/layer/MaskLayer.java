package thebetweenlands.common.world.gen.layer;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.layer.util.Area;
import thebetweenlands.common.world.gen.layer.util.AreaTransformer1;
import thebetweenlands.common.world.gen.layer.util.BigContext;

public class MaskLayer implements AreaTransformer1 {

	private final HolderGetter<Biome> registry;
	private final ResourceKey<Biome> from;
	private final ResourceKey<Biome> to;

	public MaskLayer(HolderGetter<Biome> registry, ResourceKey<Biome> from, ResourceKey<Biome> to) {
		this.registry = registry;
		this.from = from;
		this.to = to;
	}

	@Override
	public int getParentX(int x) {
		return x;
	}

	@Override
	public int getParentY(int y) {
		return y;
	}

	@Override
	public int apply(BigContext<?> context, Area area, int x, int z) {
		if (area.get(x, z) == BetweenlandsBiomeSource.getBiomeId(from, registry)) {
			return BetweenlandsBiomeSource.getBiomeId(to, registry);
		} else {
			return -1;
		}
	}
}
