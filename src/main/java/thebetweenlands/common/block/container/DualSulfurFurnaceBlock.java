package thebetweenlands.common.block.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.SulfurFurnaceBlockEntity;

import javax.annotation.Nullable;

public class DualSulfurFurnaceBlock extends SulfurFurnaceBlock {

	public DualSulfurFurnaceBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SulfurFurnaceBlockEntity(pos, state, 2);
	}
}
