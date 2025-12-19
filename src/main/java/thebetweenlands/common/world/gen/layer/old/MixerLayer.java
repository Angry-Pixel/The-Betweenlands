package thebetweenlands.common.world.gen.layer.old;

import thebetweenlands.api.world.biome.layer.Area;
import thebetweenlands.common.world.gen.layer.old.util.AreaTransformer2;
import thebetweenlands.common.world.gen.layer.old.util.Context;
import thebetweenlands.common.world.gen.layer.old.util.DimensionOffset0Transformer;

public enum MixerLayer implements AreaTransformer2, DimensionOffset0Transformer {

	INSTANCE;

	@Override
	public int applyPixel(Context context, Area first, Area second, int x, int z) {
		int i = first.get(this.getParentX(x), this.getParentY(z));
		int j = second.get(this.getParentX(x), this.getParentY(z));

		if (j != -1) {
			return j;
		} else {
			return i;
		}
	}
}
