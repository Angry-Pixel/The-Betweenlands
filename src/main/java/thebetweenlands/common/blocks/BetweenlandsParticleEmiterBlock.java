package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.ParticleRegistry;

import java.util.Random;

public class BetweenlandsParticleEmiterBlock extends BetweenlandsBlock {

	public BetweenlandsParticleEmiterBlock(Properties p_48756_) {
		super(p_48756_);
	}

	public void animateTick(BlockState p_55479_, Level p_55480_, BlockPos p_55481_, RandomSource p_55482_) {
		if (p_55482_.nextInt(15) == 0) {
			spawnParticles(p_55480_, p_55481_);
		}
	}

	private static void spawnParticles(Level p_55455_, BlockPos p_55456_) {
		RandomSource random = p_55455_.random;

		for (Direction direction : Direction.values()) {
			BlockPos blockpos = p_55456_.relative(direction);
			if (!p_55455_.getBlockState(blockpos).isSolidRender(p_55455_, blockpos)) {
				Direction.Axis direction$axis = direction.getAxis();
				double d1 = direction$axis == Direction.Axis.X ? 0.5D + 0.5625D * (double) direction.getStepX() : (double) random.nextFloat();
				double d2 = direction$axis == Direction.Axis.Y ? 0.5D + 0.5625D * (double) direction.getStepY() : (double) random.nextFloat();
				double d3 = direction$axis == Direction.Axis.Z ? 0.5D + 0.5625D * (double) direction.getStepZ() : (double) random.nextFloat();
				p_55455_.addParticle(ParticleRegistry.SULFUR_GENERIC.get(), (double) p_55456_.getX() + d1, (double) p_55456_.getY() + d2, (double) p_55456_.getZ() + d3, 0.125D - random.nextFloat(0.25f), 0.125D - random.nextFloat(0.25f), 0.125D - random.nextFloat(0.25f));
			}
		}
	}
}
