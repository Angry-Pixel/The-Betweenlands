package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class BetweenlandsSwampWater extends LiquidBlock {

	public BetweenlandsSwampWater(FlowingFluid p_54694_, Properties p_54695_) {
		super(p_54694_, p_54695_);
	}

	public int getColorMultiplier(BlockState state, LevelReader worldIn, BlockPos pos, int tintIndex) {
		return -1;
	}

	// implements BlockColor
	//@Override
	//public int getColor(BlockState p_92567_, BlockAndTintGetter p_92568_, BlockPos p_92569_, int p_92570_) {
	//	return 0Xff4f4f4f;
	//}
}
