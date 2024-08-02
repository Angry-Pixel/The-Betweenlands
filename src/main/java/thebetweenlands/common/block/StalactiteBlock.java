package thebetweenlands.common.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import thebetweenlands.util.StalactiteHelper.IStalactite;

public class StalactiteBlock extends Block implements SimpleWaterloggedBlock, IStalactite {
	
	public static final int MAX_LENGTH = 32;

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public StalactiteBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
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
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
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
