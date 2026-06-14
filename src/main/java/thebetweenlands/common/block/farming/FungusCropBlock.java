package thebetweenlands.common.block.farming;

public class FungusCropBlock extends DecayableCropBlock {
    public FungusCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int getMaxHeight() {
		return 1;
	}
}
