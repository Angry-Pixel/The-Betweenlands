package thebetweenlands.common.block.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.WeedwoodChestBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class WeedwoodChestBlock extends ChestBlock {
	public WeedwoodChestBlock(Properties properties) {
		super(properties, BlockEntityRegistry.WEEDWOOD_CHEST::get);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new WeedwoodChestBlockEntity(pos, state);
	}
}
