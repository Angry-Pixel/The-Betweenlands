package thebetweenlands.common.block.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.terrain.SwampGrassBlock;
import thebetweenlands.common.block.entity.DugSoilBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class DugGrassBlock extends DugSoilBlock {

	public DugGrassBlock(boolean purified, Properties properties) {
		super(purified, properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);

		if (!level.isClientSide()) {
			if (level.getBlockEntity(pos) instanceof DugSoilBlockEntity soil) {
				if (!soil.isComposted() && random.nextInt(20) == 0) {
					if (this.isPurified(level, pos, state)) {
						level.setBlockAndUpdate(pos, BlockRegistry.PURIFIED_DUG_SWAMP_GRASS.get().defaultBlockState());
					} else {
						level.setBlockAndUpdate(pos, BlockRegistry.SWAMP_GRASS.get().defaultBlockState());
					}
				} else {
					if (level.getBlockState(pos.above()).getLightBlock(level, pos.above()) > 2) {
						level.setBlockAndUpdate(pos, BlockRegistry.DUG_SWAMP_DIRT.get().defaultBlockState());
						DugSoilBlock.copy(level, pos, soil);
					}
				}
			}
			SwampGrassBlock.updateGrass(level, pos, random);
		}
	}

	@Override
	public BlockState getUnpurifiedDugSoil(Level level, BlockPos pos, BlockState state) {
		return BlockRegistry.DUG_SWAMP_GRASS.get().defaultBlockState().setValue(COMPOSTED, state.getValue(COMPOSTED)).setValue(DECAYED, state.getValue(DECAYED));
	}

	@Override
	public int getPurifiedHarvests(Level level, BlockPos pos, BlockState state) {
		return 6;
	}
}
