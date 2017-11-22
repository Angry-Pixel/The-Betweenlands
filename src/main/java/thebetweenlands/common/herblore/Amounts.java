package thebetweenlands.common.herblore;

public class Amounts {
	public static float VERY_LOW = 0.5F * 1000.0F;
	public static float LOW = 1.0F * 1000.0F;
	public static float LOW_MEDIUM = 1.5F * 1000.0F;
	public static float MEDIUM = 2.0F * 1000.0F;
	public static float MEDIUM_HIGH = 2.5F * 1000.0F;
	public static float HIGH = 3.0F * 1000.0F;
	public static float VERY_HIGH = 3.5F * 1000.0F;

	//TODO: Adjust amounts
	public static float MAX_ASPECT_AMOUNT = VERY_HIGH * /*TileEntityInfuser.MAX_INGREDIENTS*/6;
	public static float VIAL = MAX_ASPECT_AMOUNT / /*TileEntityInfuser.MAX_INGREDIENTS*/6 * MEDIUM;
}
