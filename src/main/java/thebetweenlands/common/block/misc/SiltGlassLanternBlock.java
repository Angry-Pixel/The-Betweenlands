package thebetweenlands.common.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SiltGlassLanternBlock extends BLLanternBlock {
	public SiltGlassLanternBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);

//		BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING,
//			BLParticles.WISP.create(level, pos.getX() + 0.5f, pos.getY() + 0.325f, pos.getZ() + 0.5f,
//				ParticleArgs.get().withMotion(0, -0.0015f, 0).withScale(0.3F + random.nextFloat() * 0.4f).withColor(1.0f, 0.5f + random.nextFloat() * 0.2f, 0.1f + random.nextFloat() * 0.2f, 0.6f).withData(255, false)));
	}
}
