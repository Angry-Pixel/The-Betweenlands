package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Mule extends AbstractChestedHorse {
   public Mule(EntityType<? extends Mule> p_30878_, Level p_30879_) {
      super(p_30878_, p_30879_);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.MULE_AMBIENT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.MULE_ANGRY;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.MULE_DEATH;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return SoundEvents.MULE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource p_30886_) {
      super.getHurtSound(p_30886_);
      return SoundEvents.MULE_HURT;
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public AgeableMob getBreedOffspring(ServerLevel p_149549_, AgeableMob p_149550_) {
      return EntityType.MULE.create(p_149549_);
   }
}