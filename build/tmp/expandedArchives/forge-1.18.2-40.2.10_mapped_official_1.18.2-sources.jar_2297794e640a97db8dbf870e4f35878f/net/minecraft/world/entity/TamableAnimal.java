package net.minecraft.world.entity;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;

public abstract class TamableAnimal extends Animal implements OwnableEntity {
   protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(TamableAnimal.class, EntityDataSerializers.OPTIONAL_UUID);
   private boolean orderedToSit;

   protected TamableAnimal(EntityType<? extends TamableAnimal> p_21803_, Level p_21804_) {
      super(p_21803_, p_21804_);
      this.reassessTameGoals();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
   }

   public void addAdditionalSaveData(CompoundTag p_21819_) {
      super.addAdditionalSaveData(p_21819_);
      if (this.getOwnerUUID() != null) {
         p_21819_.putUUID("Owner", this.getOwnerUUID());
      }

      p_21819_.putBoolean("Sitting", this.orderedToSit);
   }

   public void readAdditionalSaveData(CompoundTag p_21815_) {
      super.readAdditionalSaveData(p_21815_);
      UUID uuid;
      if (p_21815_.hasUUID("Owner")) {
         uuid = p_21815_.getUUID("Owner");
      } else {
         String s = p_21815_.getString("Owner");
         uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
      }

      if (uuid != null) {
         try {
            this.setOwnerUUID(uuid);
            this.setTame(true);
         } catch (Throwable throwable) {
            this.setTame(false);
         }
      }

      this.orderedToSit = p_21815_.getBoolean("Sitting");
      this.setInSittingPose(this.orderedToSit);
   }

   public boolean canBeLeashed(Player p_21813_) {
      return !this.isLeashed();
   }

   protected void spawnTamingParticles(boolean p_21835_) {
      ParticleOptions particleoptions = ParticleTypes.HEART;
      if (!p_21835_) {
         particleoptions = ParticleTypes.SMOKE;
      }

      for(int i = 0; i < 7; ++i) {
         double d0 = this.random.nextGaussian() * 0.02D;
         double d1 = this.random.nextGaussian() * 0.02D;
         double d2 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
      }

   }

   public void handleEntityEvent(byte p_21807_) {
      if (p_21807_ == 7) {
         this.spawnTamingParticles(true);
      } else if (p_21807_ == 6) {
         this.spawnTamingParticles(false);
      } else {
         super.handleEntityEvent(p_21807_);
      }

   }

   public boolean isTame() {
      return (this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
   }

   public void setTame(boolean p_21836_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_21836_) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 4));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -5));
      }

      this.reassessTameGoals();
   }

   protected void reassessTameGoals() {
   }

   public boolean isInSittingPose() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   public void setInSittingPose(boolean p_21838_) {
      byte b0 = this.entityData.get(DATA_FLAGS_ID);
      if (p_21838_) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
      }

   }

   @Nullable
   public UUID getOwnerUUID() {
      return this.entityData.get(DATA_OWNERUUID_ID).orElse((UUID)null);
   }

   public void setOwnerUUID(@Nullable UUID p_21817_) {
      this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(p_21817_));
   }

   public void tame(Player p_21829_) {
      this.setTame(true);
      this.setOwnerUUID(p_21829_.getUUID());
      if (p_21829_ instanceof ServerPlayer) {
         CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)p_21829_, this);
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      try {
         UUID uuid = this.getOwnerUUID();
         return uuid == null ? null : this.level.getPlayerByUUID(uuid);
      } catch (IllegalArgumentException illegalargumentexception) {
         return null;
      }
   }

   public boolean canAttack(LivingEntity p_21822_) {
      return this.isOwnedBy(p_21822_) ? false : super.canAttack(p_21822_);
   }

   public boolean isOwnedBy(LivingEntity p_21831_) {
      return p_21831_ == this.getOwner();
   }

   public boolean wantsToAttack(LivingEntity p_21810_, LivingEntity p_21811_) {
      return true;
   }

   public Team getTeam() {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (livingentity != null) {
            return livingentity.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isAlliedTo(Entity p_21833_) {
      if (this.isTame()) {
         LivingEntity livingentity = this.getOwner();
         if (p_21833_ == livingentity) {
            return true;
         }

         if (livingentity != null) {
            return livingentity.isAlliedTo(p_21833_);
         }
      }

      return super.isAlliedTo(p_21833_);
   }

   public void die(DamageSource p_21809_) {
      // FORGE: Super moved to top so that death message would be cancelled properly
      net.minecraft.network.chat.Component deathMessage = this.getCombatTracker().getDeathMessage();
      super.die(p_21809_);

      if (this.dead)
      if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
         this.getOwner().sendMessage(deathMessage, Util.NIL_UUID);
      }

   }

   public boolean isOrderedToSit() {
      return this.orderedToSit;
   }

   public void setOrderedToSit(boolean p_21840_) {
      this.orderedToSit = p_21840_;
   }
}
