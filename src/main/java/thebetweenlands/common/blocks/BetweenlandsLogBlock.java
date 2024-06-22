package thebetweenlands.common.blocks;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class BetweenlandsLogBlock extends BetweenlandsBlock {

	public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

	public BetweenlandsLogBlock(Properties p_48756_) {
		super(p_48756_);
		this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Axis.Y));
	}

	// Set angle at placment
	public BlockState getStateForPlacement(BlockPlaceContext p_153824_) {

		return this.defaultBlockState().setValue(AXIS, p_153824_.getClickedFace().getAxis());
	}

	// Add propertys to block
	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_153309_) {
		p_153309_.add(AXIS);
	}
}
