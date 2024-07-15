package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class WeepingBlueBlock extends FarmableDoublePlantBlock {
	public WeepingBlueBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(4) == 0 && level.getBlockState(pos).getValue(HALF) == DoubleBlockHalf.UPPER) {
			level.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.25D + random.nextFloat() * 0.5D, pos.getY() + 0.6D, pos.getZ() + 0.25D + random.nextFloat() * 0.5D, 0.0D, 0.0D, 0.0D);
		}
	}
}
