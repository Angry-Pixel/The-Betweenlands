package thebetweenlands.common.block.farming;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.misc.TarredHearthgroveLogBlock;
import thebetweenlands.common.registries.FluidRegistry;

import javax.annotation.Nullable;
import java.util.Map;

public class BarnacleBlock extends DirectionalBlock implements LiquidBlockContainer {

	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 1, 4);
	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(4.0D, 4.0D, 12.0D, 12.0D, 12.0D, 16.0D),
		Direction.WEST, Block.box(12.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D),
		Direction.SOUTH, Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 4.0D),
		Direction.EAST, Block.box(0.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D),
		Direction.UP, Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D),
		Direction.DOWN, Block.box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D)
	));

	public BarnacleBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(STAGE, 1).setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.getOrDefault(state.getValue(FACING), Shapes.empty());
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return Block.canSupportCenter(level, pos.relative(state.getValue(FACING).getOpposite()), state.getValue(FACING));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.canSurvive(level, pos) ? super.updateShape(state, direction, neighborState, level, pos, neighborPos) : Blocks.AIR.defaultBlockState();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return fluidstate.is(FluidRegistry.SWAMP_WATER_STILL.get()) && fluidstate.getAmount() == 8 ? super.getStateForPlacement(context).setValue(FACING, context.getNearestLookingDirection()) : null;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!level.isClientSide() && this.checkForLog(level, pos, state)) {
			level.scheduleTick(pos, this, 100);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide() && this.checkForLog(level, pos, state)) {
			this.randomTick(state, level, pos, random);
			level.scheduleTick(pos, this, 100);
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide() && state.getValue(STAGE) < 4 && random.nextInt(4) == 0) {
			level.setBlockAndUpdate(pos, state.cycle(STAGE));
		}
	}

	private boolean checkForLog(Level level, BlockPos pos, BlockState state) {
		BlockState offsetState = level.getBlockState(pos.relative(state.getValue(FACING).getOpposite()));
		return offsetState.getBlock() instanceof TarredHearthgroveLogBlock;
	}

	@Override
	public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return FluidRegistry.SWAMP_WATER_STILL.get().getSource(false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, STAGE);
	}
}
