package thebetweenlands.common.block.farming;

import net.minecraft.world.level.ItemLike;
import thebetweenlands.common.registries.ItemRegistry;

public class MiddleFruitBushBlock extends DecayableCropBlock {
	public MiddleFruitBushBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int getMaxAge() {
		return 5;
	}

	@Override
	public int getMaxHeight() {
		return 1;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS;
	}
}
