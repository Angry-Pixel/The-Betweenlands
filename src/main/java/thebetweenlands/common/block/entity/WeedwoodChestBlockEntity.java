package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class WeedwoodChestBlockEntity extends ChestBlockEntity {

	public WeedwoodChestBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityRegistry.WEEDWOOD_CHEST.get(), pos, blockState);
	}
}
