package thebetweenlands.common.block.farming;

import net.minecraft.world.level.ItemLike;
import thebetweenlands.common.registries.ItemRegistry;

public class FungusCropBlock extends DecayableCropBlock {
    public FungusCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int getMaxHeight() {
		return 1;
	}

	@Override
	protected ItemLike getBaseSeedId() {
        return ItemRegistry.SPORES;
	}
}
