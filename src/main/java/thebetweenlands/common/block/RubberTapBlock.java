package thebetweenlands.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.AlembicBlockEntity;
import thebetweenlands.common.block.entity.RubberTapBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;
import java.util.Map;

public class RubberTapBlock extends HorizontalBaseEntityBlock {

	public static final MapCodec<RubberTapBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("ticks_per_step").forGetter(p_304953_ -> p_304953_.ticksPerStep),
		propertiesCodec()
	).apply(instance, RubberTapBlock::new));

	private static final Map<Direction, VoxelShape> SHAPE_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(
		Direction.NORTH, Block.box(2.0D, 0.0D, 6.0D, 14.0D, 16.0D, 16.0D),
		Direction.WEST, Block.box(6.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D),
		Direction.SOUTH, Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 10.0D),
		Direction.EAST, Block.box(0.0D, 0.0D, 2.0D, 10.0D, 16.0D, 14.0D)
	));

	private final int ticksPerStep;

	public RubberTapBlock(int ticksPerStep, Properties properties) {
		super(properties);
		this.ticksPerStep = ticksPerStep;
	}

	public int getTicksPerStep() {
		return this.ticksPerStep;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE_BY_DIRECTION.getOrDefault(state.getValue(FACING), Shapes.empty());
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
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = level.getBlockState(blockpos);
		return direction.getAxis().isHorizontal() && blockstate.is(BlockRegistry.RUBBER_LOG);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RubberTapBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.RUBBER_TAP.get(), RubberTapBlockEntity::tick);
	}
}
