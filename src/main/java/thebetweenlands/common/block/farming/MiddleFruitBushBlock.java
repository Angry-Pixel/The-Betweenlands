package thebetweenlands.common.block.farming;

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
}
