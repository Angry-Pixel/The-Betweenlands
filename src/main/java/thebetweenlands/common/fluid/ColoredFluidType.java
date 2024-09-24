package thebetweenlands.common.fluid;

public class ColoredFluidType extends BasicFluidType {

	private final int color;

	public ColoredFluidType(int color, String fluidName) {
		super(fluidName);
		this.color = color;
	}

	@Override
	public int getTintColor() {
		return this.color | 0xFF000000;
	}
}
