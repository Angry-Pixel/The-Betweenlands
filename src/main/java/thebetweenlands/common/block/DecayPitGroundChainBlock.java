package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.DecayPitGroundChainBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class DecayPitGroundChainBlock extends HorizontalBaseEntityBlock {

	public DecayPitGroundChainBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DecayPitGroundChainBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.DECAY_PIT_GROUND_CHAIN.get(), DecayPitGroundChainBlockEntity::tick);
	}
}
