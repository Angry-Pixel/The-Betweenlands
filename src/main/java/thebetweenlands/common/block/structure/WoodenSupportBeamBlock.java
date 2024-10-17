package thebetweenlands.common.block.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;

import javax.annotation.Nullable;
import java.util.Map;

public class WoodenSupportBeamBlock extends HorizontalDirectionalBlock implements SwampWaterLoggable {

	public static final BooleanProperty TOP = BooleanProperty.create("top");
	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D),
		Direction.WEST, Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D),
		Direction.SOUTH, Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D),
		Direction.EAST, Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D)
	));

	public WoodenSupportBeamBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.getOrDefault(state.getValue(FACING), Shapes.empty());
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = this.defaultBlockState().setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
		LevelReader levelreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Direction[] adirection = context.getNearestLookingDirections();

		for (Direction direction : adirection) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction1 = direction.getOpposite();
				blockstate = blockstate.setValue(FACING, direction1);
				if (blockstate.canSurvive(levelreader, blockpos)) {
					return blockstate.setValue(TOP, context.getClickLocation().y() - context.getClickedPos().getY() > 0.5D);
				}
			}
		}
		return null;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = level.getBlockState(blockpos);
		return direction.getAxis().isHorizontal() && blockstate.isFaceSturdy(level, blockpos, direction);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}
		return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, TOP, WATER_TYPE);
	}
}
