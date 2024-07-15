package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class WispBlockEntity extends BlockEntity {

	public long lastSpawn = 0;

	public WispBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.WISP.get(), pos, state);
	}
}
