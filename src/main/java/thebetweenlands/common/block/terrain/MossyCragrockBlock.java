package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class MossyCragrockBlock extends Block {

	public MossyCragrockBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide()) {
			BlockPos newPos = pos.offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1);
			if (newPos.getY() >= level.getMinBuildHeight() && newPos.getY() < level.getMaxBuildHeight() && level.isLoaded(newPos)) {
				BlockState blockState = level.getBlockState(newPos);
				if (blockState.is(BlockRegistry.CRAGROCK)) {
					if ((level.getBlockState(newPos.above()).is(BlockRegistry.CRAGROCK) || level.getBlockState(newPos.above()).is(BlockRegistry.MOSSY_CRAGROCK_TOP))
						&& level.getBlockState(newPos.above(2)).isAir()
						&& !blockState.is(BlockRegistry.MOSSY_CRAGROCK_BOTTOM)) {
						level.setBlockAndUpdate(newPos, BlockRegistry.MOSSY_CRAGROCK_BOTTOM.get().defaultBlockState().setValue(MossyCragrockBottomBlock.IS_BOTTOM, !level.getBlockState(pos.below()).is(BlockRegistry.MOSSY_CRAGROCK_BOTTOM)));
					} else if (level.getBlockState(newPos).is(BlockRegistry.CRAGROCK)
						&& level.getBlockState(newPos.above()).isAir()) {
						level.setBlock(newPos, BlockRegistry.MOSSY_CRAGROCK_TOP.get().defaultBlockState(), 2);
					}
				}
			}
		}
	}
}
