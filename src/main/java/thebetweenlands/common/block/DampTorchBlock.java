package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;

public class DampTorchBlock extends TorchBlock {
	public DampTorchBlock(Properties properties) {
		super(ParticleTypes.SMOKE, properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(4) == 0) {
			double x = (double) pos.getX() + 0.5D;
			double y = (double) pos.getY() + 0.7D;
			double z = (double) pos.getZ() + 0.5D;
//			BLParticles.SMOKE.spawn(level, x, y, z, ParticleArgs.get().withColor(0.2F, 0.2F, 0.2F, 1.0F));
		}
	}
}
