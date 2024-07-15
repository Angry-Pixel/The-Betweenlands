package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BulbCappedMushroomCapBlock extends HalfTransparentBlock {

	public BulbCappedMushroomCapBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);

		if (random.nextInt(150) == 0) {
//			switch(random.nextInt(3)) {
//				case 0 -> BLParticles.MOSQUITO.spawn(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
//				case 1 -> BLParticles.FLY.spawn(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
//				default -> BLParticles.MOTH.spawn(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
//			}
		}
	}
}
