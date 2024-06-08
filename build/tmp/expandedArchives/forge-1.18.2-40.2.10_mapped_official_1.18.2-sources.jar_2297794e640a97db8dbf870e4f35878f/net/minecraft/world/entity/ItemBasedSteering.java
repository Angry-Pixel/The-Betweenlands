package net.minecraft.world.entity;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

public class ItemBasedSteering {
   private static final int MIN_BOOST_TIME = 140;
   private static final int MAX_BOOST_TIME = 700;
   private final SynchedEntityData entityData;
   private final EntityDataAccessor<Integer> boostTimeAccessor;
   private final EntityDataAccessor<Boolean> hasSaddleAccessor;
   public boolean boosting;
   public int boostTime;
   public int boostTimeTotal;

   public ItemBasedSteering(SynchedEntityData p_20841_, EntityDataAccessor<Integer> p_20842_, EntityDataAccessor<Boolean> p_20843_) {
      this.entityData = p_20841_;
      this.boostTimeAccessor = p_20842_;
      this.hasSaddleAccessor = p_20843_;
   }

   public void onSynced() {
      this.boosting = true;
      this.boostTime = 0;
      this.boostTimeTotal = this.entityData.get(this.boostTimeAccessor);
   }

   public boolean boost(Random p_20846_) {
      if (this.boosting) {
         return false;
      } else {
         this.boosting = true;
         this.boostTime = 0;
         this.boostTimeTotal = p_20846_.nextInt(841) + 140;
         this.entityData.set(this.boostTimeAccessor, this.boostTimeTotal);
         return true;
      }
   }

   public void addAdditionalSaveData(CompoundTag p_20848_) {
      p_20848_.putBoolean("Saddle", this.hasSaddle());
   }

   public void readAdditionalSaveData(CompoundTag p_20853_) {
      this.setSaddle(p_20853_.getBoolean("Saddle"));
   }

   public void setSaddle(boolean p_20850_) {
      this.entityData.set(this.hasSaddleAccessor, p_20850_);
   }

   public boolean hasSaddle() {
      return this.entityData.get(this.hasSaddleAccessor);
   }
}