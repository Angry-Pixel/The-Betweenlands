package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PhragmitesBlock extends FarmableDoublePlantBlock {
	public PhragmitesBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(15) == 0) {
//			if (random.nextInt(6) != 0) {
//				BLParticles.FLY.spawn(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
//			} else {
//				BLParticles.MOTH.spawn(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
//			}
		}
	}
}
