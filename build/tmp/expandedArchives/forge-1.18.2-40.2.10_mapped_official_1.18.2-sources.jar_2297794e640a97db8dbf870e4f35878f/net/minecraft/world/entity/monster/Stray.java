package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class Stray extends AbstractSkeleton {
   public Stray(EntityType<? extends Stray> p_33836_, Level p_33837_) {
      super(p_33836_, p_33837_);
   }

   public static boolean checkStraySpawnRules(EntityType<Stray> p_33840_, ServerLevelAccessor p_33841_, MobSpawnType p_33842_, BlockPos p_33843_, Random p_33844_) {
      BlockPos blockpos = p_33843_;

      do {
         blockpos = blockpos.above();
      } while(p_33841_.getBlockState(blockpos).is(Blocks.POWDER_SNOW));

      return checkMonsterSpawnRules(p_33840_, p_33841_, p_33842_, p_33843_, p_33844_) && (p_33842_ == MobSpawnType.SPAWNER || p_33841_.canSeeSky(blockpos.below()));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.STRAY_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_33850_) {
      return SoundEvents.STRAY_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.STRAY_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.STRAY_STEP;
   }

   protected AbstractArrow getArrow(ItemStack p_33846_, float p_33847_) {
      AbstractArrow abstractarrow = super.getArrow(p_33846_, p_33847_);
      if (abstractarrow instanceof Arrow) {
         ((Arrow)abstractarrow).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
      }

      return abstractarrow;
   }
}