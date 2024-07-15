package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class HollowLogBlock extends Block {

	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	private static final VoxelShape COLLISION_SHAPE_X = Shapes.join(Shapes.block(), Block.box(0, 1, 1, 16, 15, 15), BooleanOp.ONLY_FIRST);
	private static final VoxelShape COLLISION_SHAPE_Z = Shapes.join(Shapes.block(), Block.box(1, 1, 0, 15, 15, 16), BooleanOp.ONLY_FIRST);

	public HollowLogBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(AXIS, Direction.Axis.X));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(AXIS)) {
			case X -> COLLISION_SHAPE_X;
			case Z -> COLLISION_SHAPE_Z;
			default -> Shapes.empty(); // Should never be reached
		};
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rot) {
		return switch (rot) {
			case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
				case Z -> state.setValue(AXIS, Direction.Axis.X);
				case X -> state.setValue(AXIS, Direction.Axis.Z);
				default -> state;
			};
			default -> state;
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}
}
