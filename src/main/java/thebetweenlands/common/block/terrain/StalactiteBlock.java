package thebetweenlands.common.block.terrain;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.util.StalactiteHelper.IStalactite;

public class StalactiteBlock extends Block implements SwampWaterLoggable, IStalactite {

	public static final int MAX_LENGTH = 32;

	public StalactiteBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATER_TYPE, WaterType.NONE));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);

		//tell clients to re-render all stalactites
		if(neighborPos.getX() == pos.getX() && neighborPos.getZ() == pos.getZ()) {
			level.blockEvent(pos, state.getBlock(), 0, neighborPos.getY() < pos.getY() ? 1 : 0);
		}
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
		builder.add(WATER_TYPE);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		//re-render all stalactites
		if(id == 0) {
			if(!level.isClientSide())
				return true;

			boolean down = param == 1;

			final int y = pos.getY();
			MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), y, pos.getZ());
			BlockState mutableBlockState = state;

			int direction = down ? -1 : 1; //was this or a ternary

			for (int dist = 1; dist <= MAX_LENGTH; dist++) {
				mutablePos.setY(y + (1 + dist) * direction);
				mutableBlockState = level.getBlockState(mutablePos);
				if (this.doesConnect(level, mutablePos, mutableBlockState)) {
					level.sendBlockUpdated(mutablePos, mutableBlockState, mutableBlockState, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
				} else {
					break;
				}
			}
			return true;
		}

		return super.triggerEvent(state, level, pos, id, param);
	}

}
