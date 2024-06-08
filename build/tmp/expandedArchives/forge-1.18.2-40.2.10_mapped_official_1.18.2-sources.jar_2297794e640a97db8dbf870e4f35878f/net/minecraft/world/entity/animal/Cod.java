package net.minecraft.world.entity.animal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Cod extends AbstractSchoolingFish {
   public Cod(EntityType<? extends Cod> p_28276_, Level p_28277_) {
      super(p_28276_, p_28277_);
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.COD_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COD_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_28281_) {
      return SoundEvents.COD_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.COD_FLOP;
   }
}