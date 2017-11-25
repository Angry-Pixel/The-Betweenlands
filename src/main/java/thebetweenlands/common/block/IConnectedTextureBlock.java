package thebetweenlands.common.block;

import com.google.common.base.Predicate;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import thebetweenlands.common.block.property.PropertyIntegerUnlisted;

public interface IConnectedTextureBlock {
	//-1, +1, -1, quadrant 0
	public static final IUnlistedProperty<Integer> TOP_NORTH_WEST_INDEX = new PropertyIntegerUnlisted("top_north_west_index");
	//+1, +1, -1, quadrant 1
	public static final IUnlistedProperty<Integer> TOP_NORTH_EAST_INDEX = new PropertyIntegerUnlisted("top_north_east_index");
	//-1, +1, +1, quadrant 2
	public static final IUnlistedProperty<Integer> TOP_SOUTH_WEST_INDEX = new PropertyIntegerUnlisted("top_south_west_index");
	//+1, +1, +1, quadrant 3
	public static final IUnlistedProperty<Integer> TOP_SOUTH_EAST_INDEX = new PropertyIntegerUnlisted("top_south_east_index");

	/**
	 * Adds the connected texture properties to the block state container
	 * @param container
	 * @return
	 */
	public default ExtendedBlockState getConnectedTextureBlockStateContainer(ExtendedBlockState container) {
		return BlockStateContainerHelper.extendBlockstateContainer(container, new IProperty[0], new IUnlistedProperty[]{TOP_NORTH_WEST_INDEX, TOP_NORTH_EAST_INDEX, TOP_SOUTH_WEST_INDEX, TOP_SOUTH_EAST_INDEX});
	}

	/**
	 * Updates and sets the state of the connectedx texture properties
	 * @param oldState
	 * @param world
	 * @param pos
	 * @param matcher
	 * @return
	 */
	public default IBlockState getExtendedConnectedTextureState(IExtendedBlockState state, IBlockAccess world, BlockPos pos, Predicate<BlockPos> matcher) {
		boolean[] connectionArray = getConnectionArray(world, pos, EnumFacing.UP, matcher);
		int[] quadrantIndices = getQuadrantIndices(connectionArray);
		state = state.withProperty(TOP_NORTH_WEST_INDEX, quadrantIndices[0]);
		state = state.withProperty(TOP_NORTH_EAST_INDEX, quadrantIndices[1]);
		state = state.withProperty(TOP_SOUTH_WEST_INDEX, quadrantIndices[2]);
		state = state.withProperty(TOP_SOUTH_EAST_INDEX, quadrantIndices[3]);
		return state;
	}

	/**
	 * Returns the quadrant indices<p>
	 * <p>
	 *
	 * @param connectionArray <p>Connection states, index 4 is the center:
	 *                        <pre>
	 *                                                -------
	 *                                               | 0 1 2 |
	 *                                               | 3 4 5 |
	 *                                               | 6 7 8 |
	 *                                                ------- </pre>
	 * @return <p>Returned index positions:
	 * <pre>
	 *  -------> +x
	 * |  -----
	 * | | 0 1 |
	 * | | 2 3 |
	 * |  -----
	 * \/
	 * +z
	 * </pre>
	 * <p>Texture segment indices (arrangement depends on the texture size):
	 * <pre>
	 *  -------
	 * | 0 1 2 |
	 * | 3 4 . |
	 * | . . . |
	 *  ------- </pre>
	 * <ol start = "0">
	 * <li>No connections</li>
	 * <li>Straight connection to the left and right</li>
	 * <li>Straight connection to the top and bottom</li>
	 * <li>Sharp corner</li>
	 * <li>Smooth corner</li>
	 * </ol>
	 */
	public static int[] getQuadrantIndices(boolean[] connectionArray) {
		int tls = 0;
		int trs = 0;
		int bls = 0;
		int brs = 0;
		for (int xo = 0; xo <= 2; xo++) {
			for (int zo = 0; zo <= 2; zo++) {
				boolean currentNeighbourState = connectionArray[getIndex(xo, zo, 3)];
				if ((xo != 1 && zo == 1) || (xo == 1 && zo != 1)) {
					//Adjacent neighbour
					if (currentNeighbourState) {
						if (xo == 0) {
							if (!connectionArray[getIndex(1, 2, 3)]) bls = 1;
							if (!connectionArray[getIndex(1, 0, 3)]) tls = 1;
						} else if (xo == 2) {
							if (!connectionArray[getIndex(1, 2, 3)]) brs = 1;
							if (!connectionArray[getIndex(1, 0, 3)]) trs = 1;
						} else if (zo == 0) {
							if (!connectionArray[getIndex(0, 1, 3)]) tls = 2;
							if (!connectionArray[getIndex(2, 1, 3)]) trs = 2;
						} else if (zo == 2) {
							if (!connectionArray[getIndex(0, 1, 3)]) bls = 2;
							if (!connectionArray[getIndex(2, 1, 3)]) brs = 2;
						}
					}
				} else if (xo != 1 && zo != 1) {
					//Diagonal neighbour
					if (connectionArray[getIndex(xo, 1, 3)] && connectionArray[getIndex(1, zo, 3)]) {
						int segment;
						if (currentNeighbourState) {
							//Full sharp corner
							segment = 3;
						} else {
							//Smooth half corner
							segment = 4;
						}
						if (xo == 2 && zo == 0) {
							trs = segment;
						} else if (xo == 2 && zo == 2) {
							brs = segment;
						} else if (xo == 0 && zo == 2) {
							bls = segment;
						} else {
							tls = segment;
						}
					}
				}
			}
		}
		return new int[]{tls, trs, bls, brs};
	}

	/**
	 * Creates the connection array
	 *
	 * @param blockAccess Block access
	 * @param pos
	 * @param dir         Face
	 * @return Connection array
	 */
	public static boolean[] getConnectionArray(IBlockAccess blockAccess, BlockPos pos, EnumFacing dir, Predicate<BlockPos> matcher) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		boolean xp = true;
		boolean yp = true;
		boolean xr = false;
		boolean yr = false;
		boolean zr = false;
		boolean[] connectionArray = new boolean[9];
		switch (dir) {
		case DOWN:
			xp = false;
		case UP:
			xr = true;
			zr = true;
			break;
		case NORTH:
			yp = false;
		case SOUTH:
			xr = true;
			yr = true;
			break;
		case EAST:
			xp = false;
		case WEST:
			zr = true;
			yr = true;
			break;
		default:
			return connectionArray;
		}
		for (int xo = xr ? -1 : 0; xo <= (xr ? 1 : 0); xo++) {
			for (int yo = yr ? -1 : 0; yo <= (yr ? 1 : 0); yo++) {
				for (int zo = zr ? -1 : 0; zo <= (zr ? 1 : 0); zo++) {
					int mx = (xr ? xo : yo) + 1;
					int my = (zr ? zo : (xr ? yo : zo)) + 1;
					int blockIndex = getIndex(xp ? mx : 2 - mx, yp ? my : 2 - my, 3);
					connectionArray[blockIndex] = matcher.apply(new BlockPos(x + xo, y + yo, z + zo));
				}
			}
		}
		return connectionArray;
	}

	/**
	 * Calculates an index for the given coordinates and the matrix width
	 *
	 * @param x     X Coordinate
	 * @param y     Y Coordinate
	 * @param width Matrix width
	 * @return Index
	 */
	public static int getIndex(int x, int y, int width) {
		return x % width + y * width;
	}
}
