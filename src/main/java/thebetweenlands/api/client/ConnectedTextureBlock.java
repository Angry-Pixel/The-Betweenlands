package thebetweenlands.api.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public interface ConnectedTextureBlock {

	default ConnectionRules createConnectionRules(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return ConnectedTextureHelper.createDefaultConnectionRules(level, pos, state, targetPos -> level.getBlockState(targetPos).getBlock() == this, true);
	}

	/**
	 * Can be used to cache connection rules
	 * @param level
	 * @param pos
	 * @param state
	 * @return
	 */
	default ConnectionRules getConnectionRules(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return this.createConnectionRules(level, pos, state);
	}

	/**
	 * Takes the supplied {@link ModelData} and applies any custom cullface or face index properties
	 * @param level
	 * @param pos
	 * @param state
	 * @param modelData
	 * @return modified ModelData
	 */
	default ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {

		ConnectionRules connectionRules = this.getConnectionRules(level, pos, state);

		ModelData.Builder builder = modelData.derive();

		if(this.isFaceConnectedTexture(level, pos, state, Direction.UP)) {
			final int[] quadrantIndicesUp = ConnectedTextureHelper.getQuadrantIndices(ConnectedTextureHelper.getConnectionArray(level, pos, Direction.UP, connectionRules), false);
			builder.with(DefaultConnectedTextureProperties.TOP_NORTH_WEST_INDEX, quadrantIndicesUp[0]);
			builder.with(DefaultConnectedTextureProperties.TOP_NORTH_EAST_INDEX, quadrantIndicesUp[1]);
			builder.with(DefaultConnectedTextureProperties.TOP_SOUTH_WEST_INDEX, quadrantIndicesUp[2]);
			builder.with(DefaultConnectedTextureProperties.TOP_SOUTH_EAST_INDEX, quadrantIndicesUp[3]);

			final boolean cullUp = connectionRules.doesOccludeSide(level, pos, Direction.UP, pos.relative(Direction.UP), Direction.DOWN);
			builder.with(DefaultConnectedTextureProperties.NATIVE_CULL_UP, !cullUp);
		}

		if(this.isFaceConnectedTexture(level, pos, state, Direction.DOWN)) {
			int[] quadrantIndicesDown = ConnectedTextureHelper.getQuadrantIndices(ConnectedTextureHelper.getConnectionArray(level, pos, Direction.DOWN, connectionRules), true);
			builder.with(DefaultConnectedTextureProperties.BOTTOM_NORTH_EAST_INDEX, quadrantIndicesDown[0]);
			builder.with(DefaultConnectedTextureProperties.BOTTOM_NORTH_WEST_INDEX, quadrantIndicesDown[1]);
			builder.with(DefaultConnectedTextureProperties.BOTTOM_SOUTH_EAST_INDEX, quadrantIndicesDown[2]);
			builder.with(DefaultConnectedTextureProperties.BOTTOM_SOUTH_WEST_INDEX, quadrantIndicesDown[3]);

			final boolean cullDown = connectionRules.doesOccludeSide(level, pos, Direction.DOWN, pos.relative(Direction.DOWN), Direction.UP);
			builder.with(DefaultConnectedTextureProperties.NATIVE_CULL_DOWN, !cullDown);
		}

		if(this.isFaceConnectedTexture(level, pos, state, Direction.NORTH)) {
			int[] quadrantIndicesNorth = ConnectedTextureHelper.getQuadrantIndices(ConnectedTextureHelper.getConnectionArray(level, pos, Direction.NORTH, connectionRules), false);
			builder.with(DefaultConnectedTextureProperties.NORTH_UP_WEST_INDEX, quadrantIndicesNorth[0]);
			builder.with(DefaultConnectedTextureProperties.NORTH_UP_EAST_INDEX, quadrantIndicesNorth[1]);
			builder.with(DefaultConnectedTextureProperties.NORTH_DOWN_WEST_INDEX, quadrantIndicesNorth[2]);
			builder.with(DefaultConnectedTextureProperties.NORTH_DOWN_EAST_INDEX, quadrantIndicesNorth[3]);

			final boolean cullNorth = connectionRules.doesOccludeSide(level, pos, Direction.NORTH, pos.relative(Direction.NORTH), Direction.SOUTH);
			builder.with(DefaultConnectedTextureProperties.NATIVE_CULL_NORTH, !cullNorth);
		}

		if(this.isFaceConnectedTexture(level, pos, state, Direction.SOUTH)) {
			int[] quadrantIndicesSouth = ConnectedTextureHelper.getQuadrantIndices(ConnectedTextureHelper.getConnectionArray(level, pos, Direction.SOUTH, connectionRules), false);
			builder.with(DefaultConnectedTextureProperties.SOUTH_DOWN_WEST_INDEX, quadrantIndicesSouth[0]);
			builder.with(DefaultConnectedTextureProperties.SOUTH_DOWN_EAST_INDEX, quadrantIndicesSouth[1]);
			builder.with(DefaultConnectedTextureProperties.SOUTH_UP_WEST_INDEX, quadrantIndicesSouth[2]);
			builder.with(DefaultConnectedTextureProperties.SOUTH_UP_EAST_INDEX, quadrantIndicesSouth[3]);

			final boolean cullSouth = connectionRules.doesOccludeSide(level, pos, Direction.SOUTH, pos.relative(Direction.SOUTH), Direction.NORTH);
			builder.with(DefaultConnectedTextureProperties.NATIVE_CULL_SOUTH, !cullSouth);
		}

		if(this.isFaceConnectedTexture(level, pos, state, Direction.WEST)) {
			int[] quadrantIndicesWest = ConnectedTextureHelper.getQuadrantIndices(ConnectedTextureHelper.getConnectionArray(level, pos, Direction.WEST, connectionRules), true);
			builder.with(DefaultConnectedTextureProperties.WEST_DOWN_NORTH_INDEX, quadrantIndicesWest[0]);
			builder.with(DefaultConnectedTextureProperties.WEST_UP_NORTH_INDEX, quadrantIndicesWest[1]);
			builder.with(DefaultConnectedTextureProperties.WEST_DOWN_SOUTH_INDEX, quadrantIndicesWest[2]);
			builder.with(DefaultConnectedTextureProperties.WEST_UP_SOUTH_INDEX, quadrantIndicesWest[3]);

			final boolean cullWest = connectionRules.doesOccludeSide(level, pos, Direction.WEST, pos.relative(Direction.WEST), Direction.EAST);
			builder.with(DefaultConnectedTextureProperties.NATIVE_CULL_WEST, !cullWest);
		}

		if(this.isFaceConnectedTexture(level, pos, state, Direction.EAST)) {
			int[] quadrantIndicesEast = ConnectedTextureHelper.getQuadrantIndices(ConnectedTextureHelper.getConnectionArray(level, pos, Direction.EAST, connectionRules), true);
			builder.with(DefaultConnectedTextureProperties.EAST_UP_NORTH_INDEX, quadrantIndicesEast[0]);
			builder.with(DefaultConnectedTextureProperties.EAST_DOWN_NORTH_INDEX, quadrantIndicesEast[1]);
			builder.with(DefaultConnectedTextureProperties.EAST_UP_SOUTH_INDEX, quadrantIndicesEast[2]);
			builder.with(DefaultConnectedTextureProperties.EAST_DOWN_SOUTH_INDEX, quadrantIndicesEast[3]);

			final boolean cullEast = connectionRules.doesOccludeSide(level, pos, Direction.EAST, pos.relative(Direction.EAST), Direction.WEST);
			builder.with(DefaultConnectedTextureProperties.NATIVE_CULL_EAST, !cullEast);
		}

		return builder.build();
	}

	/**
	 * Returns whether the specified face has a connected texture
	 * @param face
	 * @return
	 */
	default boolean isFaceConnectedTexture(BlockAndTintGetter level, BlockPos pos, BlockState builder, Direction face) {
		return true;
	}

}
