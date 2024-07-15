package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;

public class CircleGemBlock extends Block {

	private final CircleGemType type;

	public CircleGemBlock(CircleGemType type, Properties properties) {
		super(properties);
		this.type = type;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(state.getBlock())) {
			BetweenlandsChunkStorage.markGem(level, pos, this.type.gemSingerTarget);
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!newState.is(state.getBlock())) {
			BetweenlandsChunkStorage.unmarkGem(level, pos, this.type.gemSingerTarget);
		}
	}
}
