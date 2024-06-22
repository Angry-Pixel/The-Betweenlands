package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class BetweenlandsMultifaceBlock extends BetweenlandsBlock {

	public static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	public static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
	public static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
	public static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	public static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
	public static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

	public BetweenlandsMultifaceBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultState());
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {

		VoxelShape outShape = null;

		if (p_60555_.getValue(NORTH)) {
			outShape = NORTH_AABB;
		}
		if (p_60555_.getValue(SOUTH)) {

			if (outShape == null) {
				outShape = SOUTH_AABB;
			} else {
				outShape = Shapes.or(outShape, SOUTH_AABB);
			}
		}
		if (p_60555_.getValue(EAST)) {
			if (outShape == null) {
				outShape = EAST_AABB;
			} else {
				outShape = Shapes.or(outShape, EAST_AABB);
			}
		}
		if (p_60555_.getValue(WEST)) {
			if (outShape == null) {
				outShape = WEST_AABB;
			} else {
				outShape = Shapes.or(outShape, WEST_AABB);
			}
		}
		if (p_60555_.getValue(UP)) {
			if (outShape == null) {
				outShape = UP_AABB;
			} else {
				outShape = Shapes.or(outShape, UP_AABB);
			}
		}
		if (p_60555_.getValue(DOWN)) {
			if (outShape == null) {
				outShape = DOWN_AABB;
			} else {
				outShape = Shapes.or(outShape, DOWN_AABB);
			}
		}

		if (outShape == null) {
			// debug if block has no faces return full box
			return super.getShape(p_60555_, p_60556_, p_60557_, p_60558_);
		}

		return outShape;
	}

	public BlockState defaultState() {
		return this.defaultBlockState().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos blockpos) {
		return true;
	}

	// Find a clear face
	public boolean canBeReplaced(BlockState p_153848_, BlockPlaceContext p_153849_) {
		return Arrays.stream(p_153849_.getNearestLookingDirections()).map((p_153865_) -> {
			if (clearFace(p_153848_, p_153865_)) {
				return true;
			}
			return null;
		}).filter(Objects::nonNull).findFirst().orElse((boolean) true);
	}

	// WIP: faces to be destroyed once at a time
	/*@Override
	public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		world.setBlock(pos, state.setValue(PROPERTY_BY_DIRECTION.get(player.getDirection()), false), UPDATE_ALL);

		// Check if all faces are destroyed
		if (Arrays.stream(Direction.values()).map((p_153865_) -> {
			return state.getValue(PROPERTY_BY_DIRECTION.get(p_153865_));
		}).filter(out -> true).findFirst().orElse((boolean)false)) {
			return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
		}

		return false;
	}*/

	@Override
	public BlockState updateShape(BlockState blockstate, Direction p_60542_, BlockState p_60543_, LevelAccessor level, BlockPos blockpos, BlockPos p_60546_) {

		level.scheduleTick(blockpos, this, 0);

		// Find unsuported faces
		for (Direction direction : Direction.values()) {
			if (blockstate.getValue(PROPERTY_BY_DIRECTION.get(direction)) && !level.getBlockState(blockpos.relative(direction)).isFaceSturdy(level, blockpos.relative(direction), direction.getOpposite())) {
				blockstate = blockstate.setValue(PROPERTY_BY_DIRECTION.get(direction), false);
			}
		}

		// Apply all changes at once
		level.setBlock(blockpos, blockstate, UPDATE_ALL);

		return blockstate;
	}

	public BlockState getStateForPlacement(BlockPlaceContext p_153824_) {
		Level level = p_153824_.getLevel();
		BlockPos blockpos = p_153824_.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);

		// debug code just to make sure only called on this type of block
		if (!level.getBlockState(blockpos).is(this)) {
			blockstate = this.defaultBlockState();
		}

		// Update block value to direction
		final BlockState outstate = blockstate;
		return Arrays.stream(p_153824_.getNearestLookingDirections()).map((p_153865_) -> {
			if (level.getBlockState(blockpos.relative(p_153865_)).isFaceSturdy(level, blockpos.relative(p_153865_), p_153865_.getOpposite())) {
				return outstate.setValue(PROPERTY_BY_DIRECTION.get(p_153865_), true);
			}
			return null;
		}).filter(Objects::nonNull).findFirst().orElse((BlockState) null);
	}

	// On block update check all faces have a solid face
	@Override
	public void tick(BlockState p_60462_, ServerLevel p_60463_, BlockPos p_60464_, Random p_60465_) {

		// Destroy if out of faces
		if (this.countFaces(p_60462_) == 0) {
			p_60463_.destroyBlock(p_60464_, false);
			return;
		}
	}

	public int countFaces(BlockState p_57910_) {
		int count = 0;
		for (BooleanProperty direction : PROPERTY_BY_DIRECTION.values()) {
			if (p_57910_.getValue(direction)) {
				++count;
			}
		}
		return count;
	}

	// Add propertys to block
	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153309_) {
		p_153309_.add(NORTH);
		p_153309_.add(EAST);
		p_153309_.add(SOUTH);
		p_153309_.add(WEST);
		p_153309_.add(UP);
		p_153309_.add(DOWN);
	}

	// check if face is unset
	public boolean clearFace(BlockState state, Direction direction) {
		if (!state.getValue(PROPERTY_BY_DIRECTION.get(direction))) {
			return true;
		}
		return false;
	}
}
