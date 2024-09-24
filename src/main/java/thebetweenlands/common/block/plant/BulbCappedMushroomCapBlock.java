package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.ParticleRegistry;

public class BulbCappedMushroomCapBlock extends HalfTransparentBlock {

	public BulbCappedMushroomCapBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);

		if (random.nextInt(150) == 0) {
			switch(random.nextInt(3)) {
				case 0 -> TheBetweenlands.createParticle(ParticleRegistry.MOSQUITO.get(), level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				case 1 -> TheBetweenlands.createParticle(ParticleRegistry.FLY.get(), level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
				default -> TheBetweenlands.createParticle(ParticleRegistry.MOTH.get(), level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			}
		}
	}
}
