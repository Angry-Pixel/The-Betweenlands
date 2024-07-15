package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import thebetweenlands.common.registries.BlockRegistry;

public class RubberLogBlock extends PipeBlock {

	public static final MapCodec<RubberLogBlock> CODEC = simpleCodec(RubberLogBlock::new);
	public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

	public RubberLogBlock(Properties properties) {
		super(0.25F, properties);
		this.registerDefaultState(
			this.getStateDefinition().any().setValue(NATURAL, false)
				.setValue(NORTH, false).setValue(EAST, false)
				.setValue(SOUTH, false).setValue(WEST, false)
				.setValue(UP, false).setValue(DOWN, false));
	}

	@Override
	protected MapCodec<? extends PipeBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.getStateWithConnections(context.getLevel(), context.getClickedPos(), this.defaultBlockState());
	}

	public BlockState getStateWithConnections(BlockGetter level, BlockPos pos, BlockState state) {
		return state.trySetValue(DOWN, level.getBlockState(pos.below()).isFaceSturdy(level, pos, Direction.UP) || this.canConnectTo(level, pos.below()))
			.trySetValue(UP, this.canConnectTo(level, pos.above()))
			.trySetValue(NORTH, this.canConnectTo(level, pos.north()))
			.trySetValue(EAST, this.canConnectTo(level, pos.east()))
			.trySetValue(SOUTH, this.canConnectTo(level, pos.south()))
			.trySetValue(WEST, this.canConnectTo(level, pos.west()));
	}

	public boolean canConnectTo(BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.is(this) || state.is(BlockRegistry.RUBBER_TREE_LEAVES);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, NATURAL);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}
}
