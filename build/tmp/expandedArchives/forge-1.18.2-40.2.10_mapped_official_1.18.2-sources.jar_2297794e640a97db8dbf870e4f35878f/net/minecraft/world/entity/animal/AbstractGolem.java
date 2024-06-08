package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

public abstract class AbstractGolem extends PathfinderMob {
   protected AbstractGolem(EntityType<? extends AbstractGolem> p_27508_, Level p_27509_) {
      super(p_27508_, p_27509_);
   }

   public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_27517_) {
      return null;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return null;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean removeWhenFarAway(double p_27519_) {
      return false;
   }
}