package thebetweenlands.common.block.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import javax.annotation.Nullable;

import java.util.Map;

public class DecayedMudTilesBlock extends Block {

	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), p_55164_ -> {
		p_55164_.put(Direction.NORTH, NORTH);
		p_55164_.put(Direction.EAST, EAST);
		p_55164_.put(Direction.SOUTH, SOUTH);
		p_55164_.put(Direction.WEST, WEST);
		p_55164_.put(Direction.UP, UP);
		p_55164_.put(Direction.DOWN, DOWN);
	}));

	public DecayedMudTilesBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any()
			.setValue(NORTH, false).setValue(EAST, false)
			.setValue(SOUTH, false).setValue(WEST, false)
			.setValue(UP, false).setValue(DOWN, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState();
		for (Direction dir : Direction.values()) {
			if (context.getLevel().getBlockState(context.getClickedPos().relative(dir)).getBlock() instanceof DecayedMudTilesBlock) {
				state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), true);
			}
		}
		return state;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return state.setValue(PROPERTY_BY_DIRECTION.get(direction), neighborState.getBlock() instanceof DecayedMudTilesBlock);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
	}
}
