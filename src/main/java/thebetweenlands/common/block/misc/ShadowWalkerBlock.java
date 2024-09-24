package thebetweenlands.common.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShadowWalkerBlock extends Block {
	public ShadowWalkerBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity living) {
			if (pos.getY() >= living.getEyeY()) {
				living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20));
			}
			living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1));
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if(random.nextInt(10) == 0) {
			double x = pos.getX() + 0.5D;
			double y = pos.getY();
			double z = pos.getZ() + 0.5D;
//			BLParticles.SHADOW_GHOSTS.spawn(level, x, y, z, ParticleArgs.get().withColor(0.5F, 0.5F, 0.5F, 0.75F).withMotion((random.nextFloat() - 0.5F) * 0.08F, random.nextFloat() * 0.01F + 0.01F, (random.nextFloat() - 0.5F) * 0.08F));
		}

		if (level.isEmptyBlock(pos.above())) {
			for(int i = 0; i < 3 + random.nextInt(5); i++) {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
//					ParticleArgs.get()
//						.withMotion((random.nextFloat() - 0.5F) * 0.08F, random.nextFloat() * 0.01F + 0.01F, (random.nextFloat() - 0.5F) * 0.08F)
//						.withScale(1.0F + random.nextFloat() * 8.0F)
//						.withColor(0F, 0F, 0F, 0.05F)
//						.withData(80, true, 0.01F, true)));
			}
		}
	}
}
