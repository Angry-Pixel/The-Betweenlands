package thebetweenlands.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class BetweenlandsDecayFluid extends LiquidBlock implements BucketPickup{
	// A class for fuids that apply decay effect
	
	// Legacy function (unused but forge likes to think its required)
	@SuppressWarnings("deprecation")
	public BetweenlandsDecayFluid(FlowingFluid p_54694_, Properties p_54695_) {
		super(p_54694_, p_54695_);
	}
	
	public BetweenlandsDecayFluid(java.util.function.Supplier<? extends FlowingFluid> p_54694_, Properties p_54695_) {
		super(p_54694_, p_54695_);
	}
	
	public int getColorMultiplier(BlockState state, LevelReader worldIn, BlockPos pos, int tintIndex) {
		return -1;
	}
}