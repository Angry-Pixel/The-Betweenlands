package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public class Donkey extends AbstractChestedHorse {
   public Donkey(EntityType<? extends Donkey> p_30672_, Level p_30673_) {
      super(p_30672_, p_30673_);
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.DONKEY_AMBIENT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.DONKEY_ANGRY;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.DONKEY_DEATH;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return SoundEvents.DONKEY_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource p_30682_) {
      super.getHurtSound(p_30682_);
      return SoundEvents.DONKEY_HURT;
   }

   public boolean canMate(Animal p_30679_) {
      if (p_30679_ == this) {
         return false;
      } else if (!(p_30679_ instanceof Donkey) && !(p_30679_ instanceof Horse)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorse)p_30679_).canParent();
      }
   }

   public AgeableMob getBreedOffspring(ServerLevel p_149530_, AgeableMob p_149531_) {
      EntityType<? extends AbstractHorse> entitytype = p_149531_ instanceof Horse ? EntityType.MULE : EntityType.DONKEY;
      AbstractHorse abstracthorse = entitytype.create(p_149530_);
      this.setOffspringAttributes(p_149531_, abstracthorse);
      return abstracthorse;
   }
}