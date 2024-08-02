package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class LifeCrystalStalactiteBlock extends StalactiteBlock {


	public LifeCrystalStalactiteBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean doesConnect(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		return block == BlockRegistry.LIFE_CRYSTAL_ORE_STALACTITE.get() || block == BlockRegistry.LIFE_CRYSTAL_STALACTITE.get();
	}
	
	
}
