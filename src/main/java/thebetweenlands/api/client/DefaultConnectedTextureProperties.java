package thebetweenlands.api.client;

import net.neoforged.neoforge.client.model.data.ModelProperty;

public class DefaultConnectedTextureProperties {

	//-1, +1, -1, quadrant 0
	public static final ModelProperty<Integer> TOP_NORTH_WEST_INDEX = new ModelProperty<Integer>();
	//+1, +1, -1, quadrant 1
	public static final ModelProperty<Integer> TOP_NORTH_EAST_INDEX = new ModelProperty<Integer>();
	//-1, +1, +1, quadrant 2
	public static final ModelProperty<Integer> TOP_SOUTH_WEST_INDEX = new ModelProperty<Integer>();
	//+1, +1, +1, quadrant 3
	public static final ModelProperty<Integer> TOP_SOUTH_EAST_INDEX = new ModelProperty<Integer>();

	//+1, -1, -1, quadrant 0
	public static final ModelProperty<Integer> BOTTOM_NORTH_EAST_INDEX = new ModelProperty<Integer>();
	//-1, -1, -1, quadrant 1
	public static final ModelProperty<Integer> BOTTOM_NORTH_WEST_INDEX = new ModelProperty<Integer>();
	//+1, -1, +1, quadrant 2
	public static final ModelProperty<Integer> BOTTOM_SOUTH_EAST_INDEX = new ModelProperty<Integer>();
	//-1, -1, +1, quadrant 3
	public static final ModelProperty<Integer> BOTTOM_SOUTH_WEST_INDEX = new ModelProperty<Integer>();

	//-1, +1, -1, quadrant 0
	public static final ModelProperty<Integer> NORTH_UP_WEST_INDEX = new ModelProperty<Integer>();
	//+1, +1, -1, quadrant 1
	public static final ModelProperty<Integer> NORTH_UP_EAST_INDEX = new ModelProperty<Integer>();
	//-1, -1, -1, quadrant 2
	public static final ModelProperty<Integer> NORTH_DOWN_WEST_INDEX = new ModelProperty<Integer>();
	//+1, -1, -1, quadrant 3
	public static final ModelProperty<Integer> NORTH_DOWN_EAST_INDEX = new ModelProperty<Integer>();

	//-1, -1, +1, quadrant 0
	public static final ModelProperty<Integer> SOUTH_DOWN_WEST_INDEX = new ModelProperty<Integer>();
	//+1, -1, +1, quadrant 1
	public static final ModelProperty<Integer> SOUTH_DOWN_EAST_INDEX = new ModelProperty<Integer>();
	//-1, +1, +1, quadrant 2
	public static final ModelProperty<Integer> SOUTH_UP_WEST_INDEX = new ModelProperty<Integer>();
	//+1, +1, +1, quadrant 3
	public static final ModelProperty<Integer> SOUTH_UP_EAST_INDEX = new ModelProperty<Integer>();

	//-1, -1, -1, quadrant 0
	public static final ModelProperty<Integer> WEST_DOWN_NORTH_INDEX = new ModelProperty<Integer>();
	//-1, -1, +1, quadrant 1
	public static final ModelProperty<Integer> WEST_DOWN_SOUTH_INDEX = new ModelProperty<Integer>();
	//-1, +1, -1, quadrant 2
	public static final ModelProperty<Integer> WEST_UP_NORTH_INDEX = new ModelProperty<Integer>();
	//-1, +1, +1, quadrant 3
	public static final ModelProperty<Integer> WEST_UP_SOUTH_INDEX = new ModelProperty<Integer>();

	//+1, -1, +1, quadrant 0
	public static final ModelProperty<Integer> EAST_DOWN_SOUTH_INDEX = new ModelProperty<Integer>();
	//+1, -1, -1, quadrant 1
	public static final ModelProperty<Integer> EAST_DOWN_NORTH_INDEX = new ModelProperty<Integer>();
	//+1, +1, +1, quadrant 2
	public static final ModelProperty<Integer> EAST_UP_SOUTH_INDEX = new ModelProperty<Integer>();
	//+1, +1, -1, quadrant 3
	public static final ModelProperty<Integer> EAST_UP_NORTH_INDEX = new ModelProperty<Integer>();

}
