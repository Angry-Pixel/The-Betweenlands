package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.DecayPitControlBlockEntity;

import javax.annotation.Nullable;

public class DecayPitControlBlock extends BaseEntityBlock {

	public DecayPitControlBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DecayPitControlBlockEntity(pos, state);
	}
}
