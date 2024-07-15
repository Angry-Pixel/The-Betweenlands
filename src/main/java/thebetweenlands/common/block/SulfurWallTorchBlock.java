package thebetweenlands.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.Map;

public class SulfurWallTorchBlock extends BaseTorchBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D),
		Direction.SOUTH, Block.box(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D),
		Direction.WEST, Block.box(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D),
		Direction.EAST, Block.box(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

	public SulfurWallTorchBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends BaseTorchBlock> codec() {
		return null;
	}

	@Override
	public String getDescriptionId() {
		return this.asItem().getDescriptionId();
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShape(state);
	}

	public static VoxelShape getShape(BlockState state) {
		return AABBS.get(state.getValue(FACING));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return canSurvive(level, pos, state.getValue(FACING));
	}

	public static boolean canSurvive(LevelReader level, BlockPos pos, Direction facing) {
		BlockPos blockpos = pos.relative(facing.getOpposite());
		BlockState blockstate = level.getBlockState(blockpos);
		return blockstate.isFaceSturdy(level, blockpos, facing);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = this.defaultBlockState();
		LevelReader levelreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		Direction[] adirection = context.getNearestLookingDirections();

		for (Direction direction : adirection) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction1 = direction.getOpposite();
				blockstate = blockstate.setValue(FACING, direction1);
				if (blockstate.canSurvive(levelreader, blockpos)) {
					return blockstate;
				}
			}
		}

		return null;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : state;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide() && level.isRainingAt(pos)) {
			level.setBlockAndUpdate(pos, BlockRegistry.EXTINGUISHED_SULFUR_WALL_TORCH.get().defaultBlockState().setValue(FACING, state.getValue(FACING)));
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		Direction direction = state.getValue(FACING);
		double d0 = (double) pos.getX() + 0.5D;
		double d1 = (double) pos.getY() + 0.7D;
		double d2 = (double) pos.getZ() + 0.5D;
		Direction direction1 = direction.getOpposite();
//		BLParticles.SULFUR_TORCH.spawn(world, d0 + 0.27D * direction1.getStepX(), d1 + 0.22D, d2 + 0.27D * direction1.getStepZ());
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
