package thebetweenlands.common.block.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;

import javax.annotation.Nullable;

public class BeamSupportBlock extends RotatedPillarBlock implements SwampWaterLoggable {

	public static final VoxelShape Y_SHAPE = Shapes.or(
		Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 3.0D),
		Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D),
		Block.box(13.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D),
		Block.box(0.0D, 0.0D, 13.0D, 3.0D, 16.0D, 16.0D)
	);

	public static final VoxelShape Z_SHAPE = Shapes.or(
		Block.box(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 16.0D),
		Block.box(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
		Block.box(13.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D),
		Block.box(0.0D, 13.0D, 0.0D, 3.0D, 16.0D, 16.0D)
	);

	public static final VoxelShape X_SHAPE = Shapes.or(
		Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D),
		Block.box(0.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D),
		Block.box(0.0D, 13.0D, 13.0D, 16.0D, 16.0D, 16.0D),
		Block.box(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 3.0D)
	);

	public BeamSupportBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(AXIS)) {
			case X -> X_SHAPE;
			case Y -> Y_SHAPE;
			case Z -> Z_SHAPE;
		};
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(WATER_TYPE));
	}
}
