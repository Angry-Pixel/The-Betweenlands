package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.DugSoilBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class DugDirtBlock extends DugSoilBlock {

	public DugDirtBlock(boolean purified, Properties properties) {
		super(purified, properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);

		if (!level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity soil && !soil.isComposted() && random.nextInt(20) == 0) {
				if (this.isPurified(level, pos, state)) {
					level.setBlockAndUpdate(pos, BlockRegistry.PURIFIED_SWAMP_DIRT.get().defaultBlockState());
				} else {
					level.setBlockAndUpdate(pos, BlockRegistry.SWAMP_DIRT.get().defaultBlockState());
				}
			}
		}
	}

	@Override
	public BlockState getUnpurifiedDugSoil(Level level, BlockPos pos, BlockState state) {
		return BlockRegistry.DUG_SWAMP_DIRT.get().defaultBlockState().setValue(COMPOSTED, state.getValue(COMPOSTED)).setValue(DECAYED, state.getValue(DECAYED));
	}
}
