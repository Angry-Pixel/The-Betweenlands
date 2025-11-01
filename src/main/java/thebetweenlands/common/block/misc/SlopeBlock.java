package thebetweenlands.common.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.waterlog.SwampStairBlock;

public class SlopeBlock extends SwampStairBlock {
	public SlopeBlock(BlockState baseState, Properties properties) {
		super(baseState, properties);
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}
}
