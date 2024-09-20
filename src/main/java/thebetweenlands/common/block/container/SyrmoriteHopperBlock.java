package thebetweenlands.common.block.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.SyrmoriteHopperBlockEntity;

public class SyrmoriteHopperBlock extends HopperBlock {
	public SyrmoriteHopperBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SyrmoriteHopperBlockEntity(pos, state);
	}
}
