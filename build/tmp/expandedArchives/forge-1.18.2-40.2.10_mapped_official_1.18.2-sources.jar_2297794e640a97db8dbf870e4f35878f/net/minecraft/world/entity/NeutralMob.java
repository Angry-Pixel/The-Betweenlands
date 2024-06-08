package net.minecraft.world.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface NeutralMob {
   String TAG_ANGER_TIME = "AngerTime";
   String TAG_ANGRY_AT = "AngryAt";

   int getRemainingPersistentAngerTime();

   void setRemainingPersistentAngerTime(int p_21673_);

   @Nullable
   UUID getPersistentAngerTarget();

   void setPersistentAngerTarget(@Nullable UUID p_21672_);

   void startPersistentAngerTimer();

   default void addPersistentAngerSaveData(CompoundTag p_21679_) {
      p_21679_.putInt("AngerTime", this.getRemainingPersistentAngerTime());
      if (this.getPersistentAngerTarget() != null) {
         p_21679_.putUUID("AngryAt", this.getPersistentAngerTarget());
      }

   }

   default void readPersistentAngerSaveData(Level p_147286_, CompoundTag p_147287_) {
      this.setRemainingPersistentAngerTime(p_147287_.getInt("AngerTime"));
      if (p_147286_ instanceof ServerLevel) {
         if (!p_147287_.hasUUID("AngryAt")) {
            this.setPersistentAngerTarget((UUID)null);
         } else {
            UUID uuid = p_147287_.getUUID("AngryAt");
            this.setPersistentAngerTarget(uuid);
            Entity entity = ((ServerLevel)p_147286_).getEntity(uuid);
            if (entity != null) {
               if (entity instanceof Mob) {
                  this.setLastHurtByMob((Mob)entity);
               }

               if (entity.getType() == EntityType.PLAYER) {
                  this.setLastHurtByPlayer((Player)entity);
               }

            }
         }
      }
   }

   default void updatePersistentAnger(ServerLevel p_21667_, boolean p_21668_) {
      LivingEntity livingentity = this.getTarget();
      UUID uuid = this.getPersistentAngerTarget();
      if ((livingentity == null || livingentity.isDeadOrDying()) && uuid != null && p_21667_.getEntity(uuid) instanceof Mob) {
         this.stopBeingAngry();
      } else {
         if (livingentity != null && !Objects.equals(uuid, livingentity.getUUID())) {
            this.setPersistentAngerTarget(livingentity.getUUID());
            this.startPersistentAngerTimer();
         }

         if (this.getRemainingPersistentAngerTime() > 0 && (livingentity == null || livingentity.getType() != EntityType.PLAYER || !p_21668_)) {
            this.setRemainingPersistentAngerTime(this.getRemainingPersistentAngerTime() - 1);
            if (this.getRemainingPersistentAngerTime() == 0) {
               this.stopBeingAngry();
            }
         }

      }
   }

   default boolean isAngryAt(LivingEntity p_21675_) {
      if (!this.canAttack(p_21675_)) {
         return false;
      } else {
         return p_21675_.getType() == EntityType.PLAYER && this.isAngryAtAllPlayers(p_21675_.level) ? true : p_21675_.getUUID().equals(this.getPersistentAngerTarget());
      }
   }

   default boolean isAngryAtAllPlayers(Level p_21671_) {
      return p_21671_.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.isAngry() && this.getPersistentAngerTarget() == null;
   }

   default boolean isAngry() {
      return this.getRemainingPersistentAngerTime() > 0;
   }

   default void playerDied(Player p_21677_) {
      if (p_21677_.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
         if (p_21677_.getUUID().equals(this.getPersistentAngerTarget())) {
            this.stopBeingAngry();
         }
      }
   }

   default void forgetCurrentTargetAndRefreshUniversalAnger() {
      this.stopBeingAngry();
      this.startPersistentAngerTimer();
   }

   default void stopBeingAngry() {
      this.setLastHurtByMob((LivingEntity)null);
      this.setPersistentAngerTarget((UUID)null);
      this.setTarget((LivingEntity)null);
      this.setRemainingPersistentAngerTime(0);
   }

   @Nullable
   LivingEntity getLastHurtByMob();

   void setLastHurtByMob(@Nullable LivingEntity p_21669_);

   void setLastHurtByPlayer(@Nullable Player p_21680_);

   void setTarget(@Nullable LivingEntity p_21681_);

   boolean canAttack(LivingEntity p_181126_);

   @Nullable
   LivingEntity getTarget();
}