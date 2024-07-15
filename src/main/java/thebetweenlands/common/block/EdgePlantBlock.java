package thebetweenlands.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

public class EdgePlantBlock extends SludgeDungeonPlantBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(0.0D, 0.0D, 8.0D, 16.0D, 0.25D, 16.0D),
		Direction.WEST, Block.box(8.0D, 0.0D, 0.0D, 16.0D, 0.25D, 16.0D),
		Direction.SOUTH, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 0.25D, 8.0D),
		Direction.EAST, Block.box(8.0D, 0.0D, 0.0D, 8.0D, 0.25D, 16.0D)
	));

	public EdgePlantBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.get(state.getValue(FACING));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		return level.getBlockState(pos.relative(facing.getOpposite())).isFaceSturdy(level, pos.relative(facing.getOpposite()), facing);
	}

	@Override
	public void spreadTo(Level level, BlockPos pos, BlockState state, BlockPos targetPos, RandomSource random) {
		level.setBlockAndUpdate(targetPos, this.defaultBlockState().setValue(FACING, state.getValue(FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
