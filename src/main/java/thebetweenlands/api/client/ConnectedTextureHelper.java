package thebetweenlands.api.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import thebetweenlands.common.TheBetweenlands;

public class ConnectedTextureHelper {


	@FunctionalInterface
	public interface ConnectedTexturesPropertyProvider {
		/**
		 * Takes the supplied {@link ModelData} and applies any custom cullface or face index properties and applies them to the builder
		 */
		void applyModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData initialModelData, ModelData.Builder builder);
	}

	public static final Map<ResourceLocation, ModelProperty<Integer>> INDEX_REGISTRY = new HashMap<>();
	public static final Map<ResourceLocation, ModelProperty<Boolean>> CULLFACE_REGISTRY = new HashMap<>();

	protected static final Map<ResourceLocation, ConnectedTexturesPropertyProvider> PROPERTY_PROVIDERS = new Object2ObjectArrayMap<>();

	static {
		INDEX_REGISTRY.put(TheBetweenlands.prefix("top_north_west_index"), DefaultConnectedTextureProperties.TOP_NORTH_WEST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("top_north_east_index"), DefaultConnectedTextureProperties.TOP_NORTH_EAST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("top_south_west_index"), DefaultConnectedTextureProperties.TOP_SOUTH_WEST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("top_south_east_index"), DefaultConnectedTextureProperties.TOP_SOUTH_EAST_INDEX);

		INDEX_REGISTRY.put(TheBetweenlands.prefix("bottom_north_east_index"), DefaultConnectedTextureProperties.BOTTOM_NORTH_EAST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("bottom_north_west_index"), DefaultConnectedTextureProperties.BOTTOM_NORTH_WEST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("bottom_south_east_index"), DefaultConnectedTextureProperties.BOTTOM_SOUTH_EAST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("bottom_south_west_index"), DefaultConnectedTextureProperties.BOTTOM_SOUTH_WEST_INDEX);

		INDEX_REGISTRY.put(TheBetweenlands.prefix("north_up_west_index"), DefaultConnectedTextureProperties.NORTH_UP_WEST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("north_up_east_index"), DefaultConnectedTextureProperties.NORTH_UP_EAST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("north_down_west_index"), DefaultConnectedTextureProperties.NORTH_DOWN_WEST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("north_down_east_index"), DefaultConnectedTextureProperties.NORTH_DOWN_EAST_INDEX);

		INDEX_REGISTRY.put(TheBetweenlands.prefix("south_down_west_index"), DefaultConnectedTextureProperties.SOUTH_DOWN_WEST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("south_down_east_index"), DefaultConnectedTextureProperties.SOUTH_DOWN_EAST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("south_up_west_index"), DefaultConnectedTextureProperties.SOUTH_UP_WEST_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("south_up_east_index"), DefaultConnectedTextureProperties.SOUTH_UP_EAST_INDEX);

		INDEX_REGISTRY.put(TheBetweenlands.prefix("west_down_north_index"), DefaultConnectedTextureProperties.WEST_DOWN_NORTH_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("west_down_south_index"), DefaultConnectedTextureProperties.WEST_DOWN_SOUTH_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("west_up_north_index"), DefaultConnectedTextureProperties.WEST_UP_NORTH_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("west_up_south_index"), DefaultConnectedTextureProperties.WEST_UP_SOUTH_INDEX);

		INDEX_REGISTRY.put(TheBetweenlands.prefix("east_down_south_index"), DefaultConnectedTextureProperties.EAST_DOWN_SOUTH_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("east_down_north_index"), DefaultConnectedTextureProperties.EAST_DOWN_NORTH_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("east_up_south_index"), DefaultConnectedTextureProperties.EAST_UP_SOUTH_INDEX);
		INDEX_REGISTRY.put(TheBetweenlands.prefix("east_up_north_index"), DefaultConnectedTextureProperties.EAST_UP_NORTH_INDEX);
	}

	/**
	 * Register a new {@link ConnectedTexturesPropertyProvider} to apply properties to model data
	 * @param name
	 * @param property
	 */
	public static void registerPropertyProvider(ResourceLocation name, ConnectedTexturesPropertyProvider provider) {
		assert(!PROPERTY_PROVIDERS.containsKey(name));
		PROPERTY_PROVIDERS.put(name, provider);
	}

	/**
	 * Removes and returns the {@link ConnectedTexturesPropertyProvider} with the provided name
	 * @param name
	 * @param property
	 * @return the {@link ConnectedTexturesPropertyProvider}, or {@code null} if there was none registered under that name
	 */
	public static ConnectedTexturesPropertyProvider removePropertyProvider(ResourceLocation name) {
		return PROPERTY_PROVIDERS.remove(name);
	}

	public static ConnectedTexturesPropertyProvider getPropertyProvider(ResourceLocation name) {
		return PROPERTY_PROVIDERS.get(name);
	}

	public static boolean hasPropertyProvider(ResourceLocation name) {
		return PROPERTY_PROVIDERS.containsKey(name);
	}

	/**
	 * Register a {@link ModelProperty} as an index that can be used in connected texture models
	 * @param name
	 * @param property
	 */
	public static void registerIndexProperty(ResourceLocation name, ModelProperty<Integer> property) {
		if(INDEX_REGISTRY.containsKey(name))
			TheBetweenlands.LOGGER.warn("Index property {} is being overwritten", name);
		INDEX_REGISTRY.put(name, property);
	}

	/**
	 * Register a {@link ModelProperty} as a cullface that can be used in connected texture models
	 * @param name
	 * @param property
	 */
	public static void registerCullfaceProperty(ResourceLocation name, ModelProperty<Boolean> property) {
		if(CULLFACE_REGISTRY.containsKey(name))
			TheBetweenlands.LOGGER.warn("Cullface property {} is being overwritten", name);
		CULLFACE_REGISTRY.put(name, property);
	}

	@Nullable
	public static ModelProperty<Integer> getIndexPropertyNullable(ResourceLocation name) {
		return INDEX_REGISTRY.getOrDefault(name, null);
	}

	@Nullable
	public static ModelProperty<Boolean> getCullfacePropertyNullable(ResourceLocation name) {
		return CULLFACE_REGISTRY.getOrDefault(name, null);
	}

	@Nonnull
	public static ModelProperty<Integer> getIndexPropertyOrThrow(ResourceLocation name) {
		ModelProperty<Integer> property = getIndexPropertyNullable(name);
		if(property == null) {
			throw new RuntimeException();
		}
		return property;
	}

	@Nonnull
	public static ModelProperty<Boolean> getCullfacePropertyOrThrow(ResourceLocation name) {
		ModelProperty<Boolean> property = getCullfacePropertyNullable(name);
		if(property == null) {
			throw new RuntimeException();
		}
		return property;
	}

	public static ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
		if(state.getBlock() instanceof ConnectedTextureBlock ctblock) {
			modelData = ctblock.getModelData(level, pos, state, modelData);
		}

		if(PROPERTY_PROVIDERS.isEmpty()) return modelData;

		ModelData.Builder builder = modelData.derive();
		for(ConnectedTexturesPropertyProvider provider : PROPERTY_PROVIDERS.values()) {
			provider.applyModelData(level, pos, state, modelData, builder);
		}

		return builder.build();
	}




	public static ConnectionRules createDefaultConnectionRules(BlockAndTintGetter level, BlockPos pos, BlockState state, Predicate<BlockPos> canConnectTo, boolean connectToSelf) {
		return new ConnectionRules() {
			@Override
			public boolean canTextureConnectTo(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to) {
				return canConnectTo.test(to);
			}

			@Override
			public boolean canConnectThrough(BlockAndTintGetter world, BlockPos pos, Direction face, BlockPos to) {
				if(connectToSelf) {
				//	return ConnectionRules.super.canConnectThrough(world, pos, face, to);
				}
				Axis axis = face.getAxis();
				//Tries to connect through the block that is next to the connected texture face. This should always be true
				//if the block can't connect to its own faces because otherwise it wouldn't be able to connect to anything
				return (axis == Axis.X && to.getX() - pos.getX() != 0) || (axis == Axis.Y && to.getY() - pos.getY() != 0) || (axis == Axis.Z && to.getZ() - pos.getZ() != 0);
			}
		};
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
	 * @param flipXZ Flips the X and Z axis, used for EAST, WEST and DOWN face
	 * @return <p>Returned index positions:
	 * <pre>
	 *  ------->
	 * | 0-----1
	 * | |     |
	 * | |     |
	 * | 2-----3
	 * \/
	 * Which corner corresponds to which coordinates depends on the face the connectionArray was created for.
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
	public static int[] getQuadrantIndices(boolean[] connectionArray, boolean flipXZ) {
		int tls = 0;
		int trs = 0;
		int bls = 0;
		int brs = 0;
		for (int xo = 0; xo <= 2; xo++) {
			for (int zo = 0; zo <= 2; zo++) {
				boolean currentNeighbourState = connectionArray[getIndex(xo, zo, 3)];
				int segXPath = 1;
				int segZPath = 2;
				if(flipXZ) {
					segXPath = 2;
					segZPath = 1;
				}
				if ((xo != 1 && zo == 1) || (xo == 1 && zo != 1)) {
					//Adjacent neighbour
					if (currentNeighbourState) {
						if (xo == 0) {
							if (!connectionArray[getIndex(1, 2, 3)]) bls = segXPath;
							if (!connectionArray[getIndex(1, 0, 3)]) tls = segXPath;
						} else if (xo == 2) {
							if (!connectionArray[getIndex(1, 2, 3)]) brs = segXPath;
							if (!connectionArray[getIndex(1, 0, 3)]) trs = segXPath;
						} else if (zo == 0) {
							if (!connectionArray[getIndex(0, 1, 3)]) tls = segZPath;
							if (!connectionArray[getIndex(2, 1, 3)]) trs = segZPath;
						} else if (zo == 2) {
							if (!connectionArray[getIndex(0, 1, 3)]) bls = segZPath;
							if (!connectionArray[getIndex(2, 1, 3)]) brs = segZPath;
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
	 * @param pos
	 * @param dir         Face
	 * @param canConnectTo Returns whether this block can connect to the specified block pos
	 * @param canConnectThrough Returns whether this block can connect through the specified block pos;
	 * @return Connection array
	 */
	@SuppressWarnings("fallthrough")
	public static boolean[] getConnectionArray(BlockAndTintGetter world, BlockPos pos, Direction dir, ConnectionRules connectionRules) {
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
		BlockPos.MutableBlockPos checkPos = new MutableBlockPos();
		for (int xo = xr ? -1 : 0; xo <= (xr ? 1 : 0); xo++) {
			for (int yo = yr ? -1 : 0; yo <= (yr ? 1 : 0); yo++) {
				for (int zo = zr ? -1 : 0; zo <= (zr ? 1 : 0); zo++) {
					int mx = (xr ? xo : yo) + 1;
					int my = (zr ? zo : (xr ? yo : zo)) + 1;
					int blockIndex = getIndex(xp ? mx : 2 - mx, yp ? my : 2 - my, 3);

					if(connectionRules.canConnectThrough(world, pos, dir, checkPos.set(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ()))) {
						Axis axis = dir.getAxis();
						if((axis == Axis.X && (yo != 0 || zo != 0)) || (axis == Axis.Y && (xo != 0 || zo != 0)) || (axis == Axis.Z && (xo != 0 || yo != 0))) {
							MutableBlockPos diagPos = checkPos.set(axis == Axis.X ? (x + dir.getStepX()) : (x + xo), axis == Axis.Y ? (y + dir.getStepY()) : (y + yo), axis == Axis.Z ? (z + dir.getStepZ()) : (z + zo));
							boolean isDiagConnectable = connectionRules.canTextureConnectTo(world, pos, dir, diagPos);
							if(isDiagConnectable || connectionRules.canConnectThrough(world, pos, dir, diagPos)) {
								MutableBlockPos obstructionPos = checkPos.set(axis == Axis.X ? x : (x + xo), axis == Axis.Y ? y : (y + yo), axis == Axis.Z ? z : (z + zo));
								if(isDiagConnectable || connectionRules.canConnectThrough(world, pos, dir, obstructionPos)) {
									connectionArray[blockIndex] = true;
								} else {
									connectionArray[blockIndex] = connectionRules.canTextureConnectTo(world, pos, dir, checkPos.set(x + xo, y + yo, z + zo));
								}
							}
						}
					}
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
