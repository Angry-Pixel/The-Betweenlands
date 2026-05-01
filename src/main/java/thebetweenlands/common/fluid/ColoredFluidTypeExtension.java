package thebetweenlands.common.fluid;

public class ColoredFluidTypeExtension extends BasicFluidTypeExtension {

	private final int color;

	public ColoredFluidTypeExtension(int color, String fluidName) {
		super(fluidName);
		this.color = color;
	}

	@Override
	public int getTintColor() {
		return this.color | 0xFF000000;
	}
}
