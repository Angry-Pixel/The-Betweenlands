package thebetweenlands.common.blocks;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BetweenlandsThinLogBlock extends Block {
	
	public static final VoxelShape UP_AABB = Block.box(4.0D, 12.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	public static final VoxelShape DOWN_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 4.0D, 12.0D);
	public static final VoxelShape WEST_AABB = Block.box(0.0D, 4.0D, 4.0D, 4.0D, 12.0D, 12.0D);
	public static final VoxelShape EAST_AABB = Block.box(12.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
	public static final VoxelShape NORTH_AABB = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 12.0D);
	public static final VoxelShape SOUTH_AABB = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 16.0D);
	public static final VoxelShape BASE_AABB = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
	
	// Render Optimisers (unused)
	
	// Connector block (leaves)
	public final Block connectBlock;
	
	public BetweenlandsThinLogBlock(Properties p_49795_, Block connectBlock) {
		super(p_49795_);
		this.connectBlock = connectBlock;
		this.registerDefaultState(this.defaultState());
	}
	
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
		
		VoxelShape outShape = BASE_AABB;
		
		if (p_60555_.getValue(NORTH)) {
			outShape = Shapes.or(outShape, NORTH_AABB);
		}
		if (p_60555_.getValue(SOUTH)) {
			outShape = Shapes.or(outShape, SOUTH_AABB);
		}
		if (p_60555_.getValue(EAST)) {
			outShape = Shapes.or(outShape, EAST_AABB);
		}
		if (p_60555_.getValue(WEST)) {
			outShape = Shapes.or(outShape, WEST_AABB);
		}
		if (p_60555_.getValue(UP)) {
			outShape = Shapes.or(outShape, UP_AABB);
		}
		if (p_60555_.getValue(DOWN)) {
			outShape = Shapes.or(outShape, DOWN_AABB);
		}
		
		return outShape;
	}
	
	public BlockState defaultState() {
		return this.defaultBlockState().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
		Level level = p_49820_.getLevel();
		BlockPos outpos = p_49820_.getClickedPos();
		BlockState outstate = this.defaultBlockState();
		
		for(Direction direction : Direction.values()) {
			boolean outset = (level.getBlockState(outpos.relative(direction)).is(this) || level.getBlockState(outpos.relative(direction)).is(this.connectBlock)) || (direction == Direction.DOWN && level.getBlockState(outpos.relative(direction)).isFaceSturdy(level, outpos.below(), Direction.UP));
			outstate = outstate.setValue(PROPERTY_BY_DIRECTION.get(direction), outset);
		}
		
		return outstate;
	}
	
	@Override
	public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
		BlockState outstate = p_60541_;
		
		for(Direction direction : Direction.values()) {
			boolean outset = (p_60544_.getBlockState(p_60545_.relative(direction)).is(this) || p_60544_.getBlockState(p_60545_.relative(direction)).is(this.connectBlock)) || (direction == Direction.DOWN && p_60544_.getBlockState(p_60545_.relative(direction)).isFaceSturdy(p_60544_, p_60546_.below(), Direction.UP));
			outstate = outstate.setValue(PROPERTY_BY_DIRECTION.get(direction), outset);
		}
		
		return outstate;
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
}
