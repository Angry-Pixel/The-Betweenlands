package thebetweenlands.api.client;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.data.ModelProperty;

public class DefaultConnectedTextureProperties {

	//-1, +1, -1, quadrant 0
	public static final ModelProperty<Integer> TOP_NORTH_WEST_INDEX = new ModelProperty<>();
	//+1, +1, -1, quadrant 1
	public static final ModelProperty<Integer> TOP_NORTH_EAST_INDEX = new ModelProperty<>();
	//-1, +1, +1, quadrant 2
	public static final ModelProperty<Integer> TOP_SOUTH_WEST_INDEX = new ModelProperty<>();
	//+1, +1, +1, quadrant 3
	public static final ModelProperty<Integer> TOP_SOUTH_EAST_INDEX = new ModelProperty<>();

	//+1, -1, -1, quadrant 0
	public static final ModelProperty<Integer> BOTTOM_NORTH_EAST_INDEX = new ModelProperty<>();
	//-1, -1, -1, quadrant 1
	public static final ModelProperty<Integer> BOTTOM_NORTH_WEST_INDEX = new ModelProperty<>();
	//+1, -1, +1, quadrant 2
	public static final ModelProperty<Integer> BOTTOM_SOUTH_EAST_INDEX = new ModelProperty<>();
	//-1, -1, +1, quadrant 3
	public static final ModelProperty<Integer> BOTTOM_SOUTH_WEST_INDEX = new ModelProperty<>();

	//-1, +1, -1, quadrant 0
	public static final ModelProperty<Integer> NORTH_UP_WEST_INDEX = new ModelProperty<>();
	//+1, +1, -1, quadrant 1
	public static final ModelProperty<Integer> NORTH_UP_EAST_INDEX = new ModelProperty<>();
	//-1, -1, -1, quadrant 2
	public static final ModelProperty<Integer> NORTH_DOWN_WEST_INDEX = new ModelProperty<>();
	//+1, -1, -1, quadrant 3
	public static final ModelProperty<Integer> NORTH_DOWN_EAST_INDEX = new ModelProperty<>();

	//-1, -1, +1, quadrant 0
	public static final ModelProperty<Integer> SOUTH_DOWN_WEST_INDEX = new ModelProperty<>();
	//+1, -1, +1, quadrant 1
	public static final ModelProperty<Integer> SOUTH_DOWN_EAST_INDEX = new ModelProperty<>();
	//-1, +1, +1, quadrant 2
	public static final ModelProperty<Integer> SOUTH_UP_WEST_INDEX = new ModelProperty<>();
	//+1, +1, +1, quadrant 3
	public static final ModelProperty<Integer> SOUTH_UP_EAST_INDEX = new ModelProperty<>();

	//-1, -1, -1, quadrant 0
	public static final ModelProperty<Integer> WEST_DOWN_NORTH_INDEX = new ModelProperty<>();
	//-1, -1, +1, quadrant 1
	public static final ModelProperty<Integer> WEST_DOWN_SOUTH_INDEX = new ModelProperty<>();
	//-1, +1, -1, quadrant 2
	public static final ModelProperty<Integer> WEST_UP_NORTH_INDEX = new ModelProperty<>();
	//-1, +1, +1, quadrant 3
	public static final ModelProperty<Integer> WEST_UP_SOUTH_INDEX = new ModelProperty<>();

	//+1, -1, +1, quadrant 0
	public static final ModelProperty<Integer> EAST_DOWN_SOUTH_INDEX = new ModelProperty<>();
	//+1, -1, -1, quadrant 1
	public static final ModelProperty<Integer> EAST_DOWN_NORTH_INDEX = new ModelProperty<>();
	//+1, +1, +1, quadrant 2
	public static final ModelProperty<Integer> EAST_UP_SOUTH_INDEX = new ModelProperty<>();
	//+1, +1, -1, quadrant 3
	public static final ModelProperty<Integer> EAST_UP_NORTH_INDEX = new ModelProperty<>();

	//Autoculls for the default six cullface properties, set based on IConnectionRules.doesOccludeSide()
	public static final ModelProperty<Boolean> NATIVE_CULL_DOWN = new ModelProperty<>();
	public static final ModelProperty<Boolean> NATIVE_CULL_UP = new ModelProperty<>();
	public static final ModelProperty<Boolean> NATIVE_CULL_NORTH = new ModelProperty<>();
	public static final ModelProperty<Boolean> NATIVE_CULL_SOUTH = new ModelProperty<>();
	public static final ModelProperty<Boolean> NATIVE_CULL_EAST = new ModelProperty<>();
	public static final ModelProperty<Boolean> NATIVE_CULL_WEST = new ModelProperty<>();

	@SuppressWarnings("unchecked")
	public static final ModelProperty<Boolean>[] NATIVE_CULLS = (ModelProperty<Boolean>[])(new ModelProperty<?>[Direction.values().length]);

	static {
		NATIVE_CULLS[Direction.DOWN.get3DDataValue()] = NATIVE_CULL_DOWN;
		NATIVE_CULLS[Direction.UP.get3DDataValue()] = NATIVE_CULL_UP;
		NATIVE_CULLS[Direction.NORTH.get3DDataValue()] = NATIVE_CULL_NORTH;
		NATIVE_CULLS[Direction.SOUTH.get3DDataValue()] = NATIVE_CULL_SOUTH;
		NATIVE_CULLS[Direction.EAST.get3DDataValue()] = NATIVE_CULL_EAST;
		NATIVE_CULLS[Direction.WEST.get3DDataValue()] = NATIVE_CULL_WEST;
	}
}
