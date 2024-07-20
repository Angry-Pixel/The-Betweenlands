package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.MossBedBlockEntity;

public class MossBedBlock extends BedBlock {

	public MossBedBlock(Properties properties) {
		super(DyeColor.GREEN, properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MossBedBlockEntity(pos, state);
	}
}
