package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class WaterWeedsBlock extends UnderwaterPlantBlock {
	public WaterWeedsBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (random.nextInt(120) == 0) {
			//BLParticles.WATER_BUG.spawn(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		}
	}
}
