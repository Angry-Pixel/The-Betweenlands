package thebetweenlands.common.world.gen.layer;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.common.world.gen.BetweenlandsBiomeSource;
import thebetweenlands.common.world.gen.layer.util.*;

public class SurroundedLayer implements AreaTransformer1 {

	private final HolderGetter<Biome> registry;
	private final ResourceKey<Biome> surrounding;
	private final ResourceKey<Biome> biome;
	private final int range;
	private final int spawnChance;

	public SurroundedLayer(HolderGetter<Biome> registry, ResourceKey<Biome> surround, ResourceKey<Biome> biome, int range, int spawnChance) {
		this.registry = registry;
		this.surrounding = surround;
		this.biome = biome;
		this.range = range;
		this.spawnChance = spawnChance;
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
		boolean surrounded = true;

		for(int xo = 0; xo <= this.range; xo++) {
			for(int zo = 0; zo <= this.range; zo++) {
				if(xo * xo + zo * zo <= this.range * this.range + 1) {
					int biomeID = area.get(this.getParentX(x + xo), this.getParentY(z + zo));
					if(biomeID != BetweenlandsBiomeSource.getBiomeId(this.surrounding, this.registry)) {
						surrounded = false;
						break;
					}
				}
			}
		}

		if(surrounded && context.nextRandom(10000) <= this.spawnChance * 10000) {
			return BetweenlandsBiomeSource.getBiomeId(this.biome, this.registry);
		}

		return area.get(x, z);
	}
}
