package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class LifeCrystalStalactiteBlock extends StalactiteBlock {
	
	public final boolean isOre;

	public LifeCrystalStalactiteBlock(Properties properties, boolean isOre) {
		super(properties);
		this.isOre = isOre;
	}
	
	@Override
	public boolean doesConnect(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		return block == BlockRegistry.LIFE_CRYSTAL_ORE_STALACTITE.get() || block == BlockRegistry.LIFE_CRYSTAL_STALACTITE.get();
	}
	
	@Override
	public boolean doesRenderOverlay(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return state.getBlock() == this ? this.isOre : super.doesRenderOverlay(level, pos, state);
	}
}
