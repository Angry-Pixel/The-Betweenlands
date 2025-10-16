package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.SmokingRackBlock;
import thebetweenlands.common.block.entity.SmokingRackBlockEntity;
import thebetweenlands.common.block.misc.ChipPathBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ParticleRegistry;

public class SmoulderingPeatBlock extends PeatBlock {
	public SmoulderingPeatBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide() && level.isEmptyBlock(pos.above())) {
			level.setBlockAndUpdate(pos, BlockRegistry.PEAT.get().defaultBlockState());
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		if (!level.isClientSide() && level.getBlockState(pos.above()).is(BlockTags.FIRE)) {
			level.setBlockAndUpdate(pos, BlockRegistry.PEAT.get().defaultBlockState());
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(24) == 0)
			level.playSound(null, pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);

		if (level.isEmptyBlock(pos.above())) {
			for (int i = 0; i < 3 + random.nextInt(5); i++) {
				TheBetweenlands.createParticle(ParticleRegistry.SMOOTH_SMOKE.get(), level, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F,
					ParticleFactory.ParticleArgs.get()
						.withMotion((random.nextFloat() - 0.5F) * 0.08F, random.nextFloat() * 0.01F + 0.01F, (random.nextFloat() - 0.5F) * 0.08F)
						.withScale(random.nextFloat() * 8.0F)
						.withColor(1.0F, 1.0F, 1.0F, 0.05F)
						.withData(80, true, 0.01F, true));
			}
		}

		if (level.getBlockState(pos.above()).getBlock() instanceof ChipPathBlock) {
			for(int i = 0; i < 3 + random.nextInt(5); i++) {
				TheBetweenlands.createParticle(ParticleRegistry.SMOOTH_SMOKE.get(), level, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F,
					ParticleFactory.ParticleArgs.get()
						.withMotion((random.nextFloat() - 0.5F) * 0.04F, random.nextFloat() * 0.1F + 0.05F, (random.nextFloat() - 0.5F) * 0.04F)
						.withScale(2.0F + random.nextFloat() * 2.0F)
						.withColor(0.0F, 0.0F, 0.0F, 0.5F)
						.withData(80, true, 0.01F, true));
			}

			TheBetweenlands.createParticle(ParticleRegistry.EMBER.get(), level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
		}

		if (level.getBlockState(pos.above()).getBlock() instanceof SmokingRackBlock) {
			SmokingRackBlockEntity tile = (SmokingRackBlockEntity) level.getBlockEntity(pos.above());
			if (tile != null && tile.isSmoking()) {
				for(int i = 0; i < 3 + random.nextInt(5); i++) {
					TheBetweenlands.createParticle(ParticleRegistry.SMOOTH_SMOKE.get(), level, pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F,
						ParticleFactory.ParticleArgs.get()
							.withMotion((random.nextFloat() - 0.5F) * 0.04F, random.nextFloat() * 0.2F + 0.01F, (random.nextFloat() - 0.5F) * 0.04F)
							.withScale(random.nextFloat() * 2.0F)
							.withColor(1.0F, 1.0F, 1.0F, 0.5F)
							.withData(80, true, 0.01F, true));

					TheBetweenlands.createParticle(ParticleRegistry.SMOOTH_SMOKE.get(), level, pos.getX() + 0.5F, pos.getY() + 2.125F, pos.getZ() + 0.5F,
						ParticleFactory.ParticleArgs.get()
							.withMotion((random.nextFloat() - 0.5F) * 0.04F, random.nextFloat() * 0.02F + 0.01F, (random.nextFloat() - 0.5F) * 0.04F)
							.withScale(1f + random.nextFloat() * 2.0F)
							.withColor(1.0F, 1.0F, 1.0F, 0.5F)
							.withData(80, true, 0.01F, true));
				}
			}
		}
	}
}
