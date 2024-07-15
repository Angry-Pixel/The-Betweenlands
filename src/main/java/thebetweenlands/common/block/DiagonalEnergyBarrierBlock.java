package thebetweenlands.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class DiagonalEnergyBarrierBlock extends Block {

	//TODO voxelshapes
	public static final BooleanProperty FLIPPED = BooleanProperty.create("flipped");

	public DiagonalEnergyBarrierBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FLIPPED, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FLIPPED, context.getHorizontalDirection().getAxis() == Direction.Axis.X);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FLIPPED);
	}
}
