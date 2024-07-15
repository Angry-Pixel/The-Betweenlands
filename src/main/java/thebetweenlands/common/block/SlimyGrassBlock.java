package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

public class SlimyGrassBlock extends Block {

	public SlimyGrassBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isClientSide()) {
			if (level.getMaxLocalRawBrightness(pos.above()) < 4 && level.getBrightness(LightLayer.BLOCK, pos.above()) > 2) {
				level.setBlockAndUpdate(pos, BlockRegistry.SLIMY_DIRT.get().defaultBlockState());
			} else if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
				for (int l = 0; l < 4; ++l) {
					BlockPos target = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

					if (level.getBlockState(target).is(Blocks.DIRT)
						&& level.getMaxLocalRawBrightness(target.above()) >= 4
						&& level.getBrightness(LightLayer.BLOCK, target.above()) <= 2) {
						level.setBlockAndUpdate(target, BlockRegistry.SLIMY_GRASS.get().defaultBlockState());
					}
				}
			}
		}
	}
}
