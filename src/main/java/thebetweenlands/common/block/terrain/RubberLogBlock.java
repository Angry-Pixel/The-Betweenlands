package thebetweenlands.common.block.terrain;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.BlockRegistry;

public class RubberLogBlock extends PipeBlock implements SwampWaterLoggable {

	public static final MapCodec<RubberLogBlock> CODEC = simpleCodec(RubberLogBlock::new);
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

	public RubberLogBlock(Properties properties) {
		super(0.25F, properties);
		this.registerDefaultState(
			this.getStateDefinition().any().setValue(AXIS, Direction.Axis.Y).setValue(NATURAL, false)
				.setValue(NORTH, false).setValue(EAST, false)
				.setValue(SOUTH, false).setValue(WEST, false)
				.setValue(UP, false).setValue(DOWN, false).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected MapCodec<? extends PipeBlock> codec() {
		return CODEC;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.getStateWithConnections(context.getLevel(), context.getClickedPos(), this.defaultBlockState()).setValue(AXIS, context.getClickedFace().getAxis()).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
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
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor accessor, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			accessor.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(accessor));
		}
		return state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.canConnectTo(accessor, neighborPos));
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS, NORTH, EAST, SOUTH, WEST, UP, DOWN, WATER_TYPE, NATURAL);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType type) {
		return false;
	}
}
