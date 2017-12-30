package thebetweenlands.common.herblore;

public class Amounts {
	public static final int VERY_LOW = 500;
	public static final int LOW = 1000;
	public static final int LOW_MEDIUM = 1500;
	public static final int MEDIUM = 2000;
	public static final int MEDIUM_HIGH = 2500;
	public static final int HIGH = 3000;
	public static final int VERY_HIGH = 3500;

	//TODO: Adjust amounts
	public static final int MAX_ASPECT_AMOUNT = VERY_HIGH * /*TileEntityInfuser.MAX_INGREDIENTS*/6;
	public static final int VIAL = MAX_ASPECT_AMOUNT / /*TileEntityInfuser.MAX_INGREDIENTS*/6 * MEDIUM;
}
