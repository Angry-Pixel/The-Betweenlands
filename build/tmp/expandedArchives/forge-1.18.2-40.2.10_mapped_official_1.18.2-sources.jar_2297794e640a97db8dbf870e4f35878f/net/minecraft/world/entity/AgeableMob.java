package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class AgeableMob extends PathfinderMob {
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(AgeableMob.class, EntityDataSerializers.BOOLEAN);
   public static final int BABY_START_AGE = -24000;
   private static final int FORCED_AGE_PARTICLE_TICKS = 40;
   protected int age;
   protected int forcedAge;
   protected int forcedAgeTimer;

   protected AgeableMob(EntityType<? extends AgeableMob> p_146738_, Level p_146739_) {
      super(p_146738_, p_146739_);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
      if (p_146749_ == null) {
         p_146749_ = new AgeableMob.AgeableMobGroupData(true);
      }

      AgeableMob.AgeableMobGroupData ageablemob$ageablemobgroupdata = (AgeableMob.AgeableMobGroupData)p_146749_;
      if (ageablemob$ageablemobgroupdata.isShouldSpawnBaby() && ageablemob$ageablemobgroupdata.getGroupSize() > 0 && this.random.nextFloat() <= ageablemob$ageablemobgroupdata.getBabySpawnChance()) {
         this.setAge(-24000);
      }

      ageablemob$ageablemobgroupdata.increaseGroupSizeByOne();
      return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
   }

   @Nullable
   public abstract AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_);

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BABY_ID, false);
   }

   public boolean canBreed() {
      return false;
   }

   public int getAge() {
      if (this.level.isClientSide) {
         return this.entityData.get(DATA_BABY_ID) ? -1 : 1;
      } else {
         return this.age;
      }
   }

   public void ageUp(int p_146741_, boolean p_146742_) {
      int i = this.getAge();
      i += p_146741_ * 20;
      if (i > 0) {
         i = 0;
      }

      int j = i - i;
      this.setAge(i);
      if (p_146742_) {
         this.forcedAge += j;
         if (this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
         }
      }

      if (this.getAge() == 0) {
         this.setAge(this.forcedAge);
      }

   }

   public void ageUp(int p_146759_) {
      this.ageUp(p_146759_, false);
   }

   public void setAge(int p_146763_) {
      int i = this.age;
      this.age = p_146763_;
      if (i < 0 && p_146763_ >= 0 || i >= 0 && p_146763_ < 0) {
         this.entityData.set(DATA_BABY_ID, p_146763_ < 0);
         this.ageBoundaryReached();
      }

   }

   public void addAdditionalSaveData(CompoundTag p_146761_) {
      super.addAdditionalSaveData(p_146761_);
      p_146761_.putInt("Age", this.getAge());
      p_146761_.putInt("ForcedAge", this.forcedAge);
   }

   public void readAdditionalSaveData(CompoundTag p_146752_) {
      super.readAdditionalSaveData(p_146752_);
      this.setAge(p_146752_.getInt("Age"));
      this.forcedAge = p_146752_.getInt("ForcedAge");
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_146754_) {
      if (DATA_BABY_ID.equals(p_146754_)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(p_146754_);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
               this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
            }

            --this.forcedAgeTimer;
         }
      } else if (this.isAlive()) {
         int i = this.getAge();
         if (i < 0) {
            ++i;
            this.setAge(i);
         } else if (i > 0) {
            --i;
            this.setAge(i);
         }
      }

   }

   protected void ageBoundaryReached() {
   }

   public boolean isBaby() {
      return this.getAge() < 0;
   }

   public void setBaby(boolean p_146756_) {
      this.setAge(p_146756_ ? -24000 : 0);
   }

   public static class AgeableMobGroupData implements SpawnGroupData {
      private int groupSize;
      private final boolean shouldSpawnBaby;
      private final float babySpawnChance;

      private AgeableMobGroupData(boolean p_146775_, float p_146776_) {
         this.shouldSpawnBaby = p_146775_;
         this.babySpawnChance = p_146776_;
      }

      public AgeableMobGroupData(boolean p_146773_) {
         this(p_146773_, 0.05F);
      }

      public AgeableMobGroupData(float p_146771_) {
         this(true, p_146771_);
      }

      public int getGroupSize() {
         return this.groupSize;
      }

      public void increaseGroupSizeByOne() {
         ++this.groupSize;
      }

      public boolean isShouldSpawnBaby() {
         return this.shouldSpawnBaby;
      }

      public float getBabySpawnChance() {
         return this.babySpawnChance;
      }
   }
}