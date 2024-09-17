package thebetweenlands.common.world.gen.layer;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.layer.util.Area;
import thebetweenlands.common.world.gen.layer.util.AreaTransformer1;
import thebetweenlands.common.world.gen.layer.util.BigContext;

public class CircleMaskLayer implements AreaTransformer1 {

	private final HolderGetter<Biome> registry;
	private final ResourceKey<Biome> biome;
	private final int radius;

	public CircleMaskLayer(HolderGetter<Biome> registry, ResourceKey<Biome> biome, int radius) {
		this.registry = registry;
		this.biome = biome;
		this.radius = radius;
	}

	@Override
	public int getParentX(int x) {
		return x - radius;
	}

	@Override
	public int getParentY(int y) {
		return y - radius;
	}

	@Override
	public int apply(BigContext<?> context, Area area, int x, int z) {
		for(int xo = -this.radius; xo <= this.radius; xo++) {
			for(int zo = -this.radius; zo <= this.radius; zo++) {
				if(xo * xo + zo * zo <= this.radius * this.radius) {
					int id = area.get(getParentX(x + xo), getParentY(z + zo));
					if(id == BetweenlandsBiomeSource.getBiomeId(biome, registry)) {
						return id;
					}
				}
			}
		}

		return -1;
	}
}
