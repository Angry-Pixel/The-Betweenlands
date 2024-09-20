package thebetweenlands.common.block.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.registries.ParticleRegistry;

public class EnergyBarrierBlock extends Block {

	public static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

	public EnergyBarrierBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

//	@Override
//	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
//		if (entity instanceof Player player) {
//			InteractionHand swordHand = null;
//			for (InteractionHand hand : InteractionHand.values()) {
//				ItemStack stack = player.getItemInHand(hand);
//				if (stack.is(ItemRegistry.SHOCKWAVE_SWORD)) {
//					swordHand = hand;
//					break;
//				}
//			}
//			if (swordHand != null) {
//				int data = Block.getId(level.getBlockState(pos));
//				if (!level.isClientSide())
//					level.levelEvent(null, 2001, pos, data);
//				int range = 7;
//				for (int x = -range; x < range; x++) {
//					for (int y = -range; y < range; y++) {
//						for (int z = -range; z < range; z++) {
//							BlockPos offset = pos.offset(x, y, z);
//							BlockState blockState = level.getBlockState(offset);
//							if (blockState.getBlock() == this) {
//								if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
//									for (int i = 0; i < 8; i++) {
//										level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), offset.getX() + (double) level.getRandom().nextFloat(), offset.getY() + (double) level.getRandom().nextFloat(), offset.getZ() + (double) level.getRandom().nextFloat(), (double) level.getRandom().nextFloat() - 0.5D, (double) level.getRandom().nextFloat() - 0.5D, (double) level.getRandom().nextFloat() - 0.5D);
//									}
//								}
//
//								if (!level.isClientSide())
//									level.removeBlock(offset, false);
//							}
//						}
//					}
//				}
//			} else if (!player.isSpectator()) {
//				entity.hurt(level.damageSources().magic(), 1);
//				double dx = (entity.getX() - (pos.getX())) * 2 - 1;
//				double dz = (entity.getZ() - (pos.getZ())) * 2 - 1;
//				if (Math.abs(dx) > Math.abs(dz))
//					dz = 0;
//				else
//					dx = 0;
//				dx = (int) dx;
//				dz = (int) dz;
//				entity.push(dx * 0.85D, 0.08D, dz * 0.85D);
//				entity.playSound(SoundRegistry.REJECTED.get(), 0.5F, 1F);
//			}
//		}
//	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		double particleX = pos.getX() + random.nextFloat();
		double particleY = pos.getY() + random.nextFloat();
		double particleZ = pos.getZ() + random.nextFloat();
		double motionX = (random.nextFloat() - 0.5D) * 0.5D;
		double motionY = (random.nextFloat() - 0.5D) * 0.5D;
		double motionZ = (random.nextFloat() - 0.5D) * 0.5D;
		int multiplier = random.nextInt(2) * 2 - 1;
		if (level.getBlockState(pos.offset(-1, 0, 0)).getBlock() != this && level.getBlockState(pos.offset(1, 0, 0)).getBlock() != this) {
			particleX = pos.getX() + 0.5D + 0.25D * multiplier;
			motionX = random.nextFloat() * 2.0F * multiplier;
		} else {
			particleZ = pos.getZ() + 0.5D + 0.25D * multiplier;
			motionZ = random.nextFloat() * 2.0F * multiplier;
		}
		level.addParticle(ParticleRegistry.PORTAL_EFFECT.get(), particleX, particleY, particleZ, motionX * 0.2F, motionY, motionZ * 0.2F);
	}
}
