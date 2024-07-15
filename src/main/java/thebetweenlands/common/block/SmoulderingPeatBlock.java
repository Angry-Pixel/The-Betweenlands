package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

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
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F,
//					ParticleArgs.get()
//						.withMotion((random.nextFloat() - 0.5f) * 0.08f, random.nextFloat() * 0.01F + 0.01F, (random.nextFloat() - 0.5f) * 0.08f)
//						.withScale(1f + rand.nextFloat() * 8.0F)
//						.withColor(1F, 1.0F, 1.0F, 0.05f)
//						.withData(80, true, 0.01F, true)));
			}
		}

//		if (level.getBlockState(pos.above()).getBlock() instanceof BlockChipPath) {
//			for(int i = 0; i < 3 + random.nextInt(5); i++) {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F,
//					ParticleArgs.get()
//						.withMotion((rand.nextFloat() - 0.5f) * 0.04f, rand.nextFloat() * 0.1F + 0.05F, (rand.nextFloat() - 0.5f) * 0.04f)
//						.withScale(2f + rand.nextFloat() * 2.0F)
//						.withColor(0F, 0.0F, 0.0F, 0.5f)
//						.withData(80, true, 0.01F, true)));
//			}

//			switch(random.nextInt(3)) {
//				default:
//				case 0:
//					BLParticles.EMBER_1.spawn(level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
//					break;
//				case 1:
//					BLParticles.EMBER_2.spawn(level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
//					break;
//				case 2:
//					BLParticles.EMBER_3.spawn(level, pos.getX() + 0.5D, pos.getY() + 1D, pos.getZ() + 0.5D);
//					break;
//			}
//		}

//		if (level.getBlockState(pos.above()).getBlock() instanceof BlockSmokingRack) {
//			TileEntitySmokingRack tile = (TileEntitySmokingRack) level.getBlockEntity(pos.above());
//			if (tile != null && tile.updateFuelState()) {
//				for(int i = 0; i < 3 + random.nextInt(5); i++) {
//					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F,
//						ParticleArgs.get()
//							.withMotion((random.nextFloat() - 0.5f) * 0.04f, random.nextFloat() * 0.2F + 0.01F, (random.nextFloat() - 0.5f) * 0.04f)
//							.withScale(1f + random.nextFloat() * 2.0F)
//							.withColor(1F, 1F, 1F, 0.5f)
//							.withData(80, true, 0.01F, true)));
//
//					BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5F, pos.getY() + 2.125F, pos.getZ() + 0.5F,
//						ParticleArgs.get()
//							.withMotion((random.nextFloat() - 0.5f) * 0.04f, random.nextFloat() * 0.02F + 0.01F, (random.nextFloat() - 0.5f) * 0.04f)
//							.withScale(1f + random.nextFloat() * 2.0F)
//							.withColor(1F, 1F, 1F, 0.5f)
//							.withData(80, true, 0.01F, true)));
//				}
//			}
//		}
	}
}
