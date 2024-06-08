package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitherRoseBlock extends FlowerBlock {
   public WitherRoseBlock(MobEffect p_58235_, BlockBehaviour.Properties p_58236_) {
      super(p_58235_, 8, p_58236_);
   }

   protected boolean mayPlaceOn(BlockState p_58248_, BlockGetter p_58249_, BlockPos p_58250_) {
      return super.mayPlaceOn(p_58248_, p_58249_, p_58250_) || p_58248_.is(Blocks.NETHERRACK) || p_58248_.is(Blocks.SOUL_SAND) || p_58248_.is(Blocks.SOUL_SOIL);
   }

   public void animateTick(BlockState p_58243_, Level p_58244_, BlockPos p_58245_, Random p_58246_) {
      VoxelShape voxelshape = this.getShape(p_58243_, p_58244_, p_58245_, CollisionContext.empty());
      Vec3 vec3 = voxelshape.bounds().getCenter();
      double d0 = (double)p_58245_.getX() + vec3.x;
      double d1 = (double)p_58245_.getZ() + vec3.z;

      for(int i = 0; i < 3; ++i) {
         if (p_58246_.nextBoolean()) {
            p_58244_.addParticle(ParticleTypes.SMOKE, d0 + p_58246_.nextDouble() / 5.0D, (double)p_58245_.getY() + (0.5D - p_58246_.nextDouble()), d1 + p_58246_.nextDouble() / 5.0D, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public void entityInside(BlockState p_58238_, Level p_58239_, BlockPos p_58240_, Entity p_58241_) {
      if (!p_58239_.isClientSide && p_58239_.getDifficulty() != Difficulty.PEACEFUL) {
         if (p_58241_ instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)p_58241_;
            if (!livingentity.isInvulnerableTo(DamageSource.WITHER)) {
               livingentity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
            }
         }

      }
   }
}