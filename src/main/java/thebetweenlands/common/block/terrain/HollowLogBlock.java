package thebetweenlands.common.block.terrain;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HollowLogBlock extends HorizontalDirectionalBlock {

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	// originally CLOCKWISE_FACE and ANTICLOCKWISE_FACE, but this is easier to work with on the collisions side of thing
	public static final BooleanProperty POSITIVE_FACE = BooleanProperty.create("positive_face");
	public static final BooleanProperty NEGATIVE_FACE = BooleanProperty.create("negative_face");
	
	private static final VoxelShape COLLISION_SHAPE_X = Shapes.join(Shapes.block(), Block.box(0, 1, 1, 16, 15, 15), BooleanOp.ONLY_FIRST);
	private static final VoxelShape COLLISION_SHAPE_Z = Shapes.join(Shapes.block(), Block.box(1, 1, 0, 15, 15, 16), BooleanOp.ONLY_FIRST);

	// No horizontal faces
	private static final VoxelShape COLLISION_SHAPE_NONE = Shapes.join(COLLISION_SHAPE_X, Block.box(1, 1, 0, 15, 15, 16), BooleanOp.ONLY_FIRST);

	private static final VoxelShape[] COLLISION_MAP_X = new VoxelShape[] {
			COLLISION_SHAPE_X,
			Shapes.join(COLLISION_SHAPE_X, Block.box(1, 1, 8, 15, 15, 16), BooleanOp.ONLY_FIRST), // no +z face
			Shapes.join(COLLISION_SHAPE_X, Block.box(1, 1, 0, 15, 15, 7), BooleanOp.ONLY_FIRST), // no -z face,
			COLLISION_SHAPE_NONE // no horizontal faces at all
	};

	private static final VoxelShape[] COLLISION_MAP_Z = new VoxelShape[] {
			COLLISION_SHAPE_Z,
			Shapes.join(COLLISION_SHAPE_Z, Block.box(8, 1, 1, 16, 15, 15), BooleanOp.ONLY_FIRST), // no +x face
			Shapes.join(COLLISION_SHAPE_Z, Block.box(0, 1, 1, 7, 15, 15), BooleanOp.ONLY_FIRST), // no -x face,
			COLLISION_SHAPE_NONE // no horizontal faces at all
	};
	
	public HollowLogBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(POSITIVE_FACE, true).setValue(NEGATIVE_FACE, true));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return null;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		final Axis axis = state.getValue(FACING).getAxis();
		if(!axis.isHorizontal()) return Shapes.empty();
		int mapIndex = 0;
		mapIndex |= !state.getValue(POSITIVE_FACE) ? 1 : 0;
		mapIndex |= !state.getValue(NEGATIVE_FACE) ? 2 : 0;
		
		return switch (axis) {
			case X -> COLLISION_MAP_X[mapIndex];
			case Z -> COLLISION_MAP_Z[mapIndex];
			default -> Shapes.empty(); // Should never be reached
		};
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final Direction facing = context.getHorizontalDirection().getOpposite();
		BlockState state = this.defaultBlockState().setValue(FACING, facing);
		
		final Axis oppositeAxis = facing.getAxis() == Axis.X ? Axis.Z : Axis.X;
		final Level level = context.getLevel();
		final BlockPos pos = context.getClickedPos();
		
		final BlockState positiveState = level.getBlockState(pos.relative(Direction.get(AxisDirection.POSITIVE, oppositeAxis)));
		if(positiveState.is(this) && positiveState.getValue(FACING).getAxis() == oppositeAxis) {
			state = state.setValue(POSITIVE_FACE, false);
		}
		
		final BlockState negativeState = level.getBlockState(pos.relative(Direction.get(AxisDirection.NEGATIVE, oppositeAxis)));
		if(negativeState.is(this) && negativeState.getValue(FACING).getAxis() == oppositeAxis) {
			state = state.setValue(NEGATIVE_FACE, false);
		}
		
		return state;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		BlockState newState = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
		
		if(state.is(this)) {
			final Axis oppositeAxis = state.getValue(FACING).getAxis() == Axis.X ? Axis.Z : Axis.X;

			final BlockState positiveState = level.getBlockState(pos.relative(Direction.get(AxisDirection.POSITIVE, oppositeAxis)));
			newState = newState.setValue(POSITIVE_FACE, !positiveState.is(this) || positiveState.getValue(FACING).getAxis() != oppositeAxis);
			
			final BlockState negativeState = level.getBlockState(pos.relative(Direction.get(AxisDirection.NEGATIVE, oppositeAxis)));
			newState = newState.setValue(NEGATIVE_FACE, !negativeState.is(this) || negativeState.getValue(FACING).getAxis() != oppositeAxis);
		}
		
		return newState;
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}
	
	@Override
	protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}
	
	@Override
	protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
		return super.skipRendering(state, adjacentState, direction) || adjacentState.is(this) && direction.getAxis() == adjacentState.getValue(FACING).getAxis();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING).add(POSITIVE_FACE).add(NEGATIVE_FACE);
	}
}
