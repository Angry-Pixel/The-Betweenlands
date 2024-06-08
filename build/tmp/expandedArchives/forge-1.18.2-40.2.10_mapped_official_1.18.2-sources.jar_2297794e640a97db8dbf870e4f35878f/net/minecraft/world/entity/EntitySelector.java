package net.minecraft.world.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Team;

public final class EntitySelector {
   public static final Predicate<Entity> ENTITY_STILL_ALIVE = Entity::isAlive;
   public static final Predicate<Entity> LIVING_ENTITY_STILL_ALIVE = (p_20442_) -> {
      return p_20442_.isAlive() && p_20442_ instanceof LivingEntity;
   };
   public static final Predicate<Entity> ENTITY_NOT_BEING_RIDDEN = (p_20440_) -> {
      return p_20440_.isAlive() && !p_20440_.isVehicle() && !p_20440_.isPassenger();
   };
   public static final Predicate<Entity> CONTAINER_ENTITY_SELECTOR = (p_20438_) -> {
      return p_20438_ instanceof Container && p_20438_.isAlive();
   };
   public static final Predicate<Entity> NO_CREATIVE_OR_SPECTATOR = (p_20436_) -> {
      return !(p_20436_ instanceof Player) || !p_20436_.isSpectator() && !((Player)p_20436_).isCreative();
   };
   public static final Predicate<Entity> NO_SPECTATORS = (p_20434_) -> {
      return !p_20434_.isSpectator();
   };
   public static final Predicate<Entity> CAN_BE_COLLIDED_WITH = NO_SPECTATORS.and(Entity::canBeCollidedWith);

   private EntitySelector() {
   }

   public static Predicate<Entity> withinDistance(double p_20411_, double p_20412_, double p_20413_, double p_20414_) {
      double d0 = p_20414_ * p_20414_;
      return (p_20420_) -> {
         return p_20420_ != null && p_20420_.distanceToSqr(p_20411_, p_20412_, p_20413_) <= d0;
      };
   }

   public static Predicate<Entity> pushableBy(Entity p_20422_) {
      Team team = p_20422_.getTeam();
      Team.CollisionRule team$collisionrule = team == null ? Team.CollisionRule.ALWAYS : team.getCollisionRule();
      return (Predicate<Entity>)(team$collisionrule == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NO_SPECTATORS.and((p_20430_) -> {
         if (!p_20430_.isPushable()) {
            return false;
         } else if (!p_20422_.level.isClientSide || p_20430_ instanceof Player && ((Player)p_20430_).isLocalPlayer()) {
            Team team1 = p_20430_.getTeam();
            Team.CollisionRule team$collisionrule1 = team1 == null ? Team.CollisionRule.ALWAYS : team1.getCollisionRule();
            if (team$collisionrule1 == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean flag = team != null && team.isAlliedTo(team1);
               if ((team$collisionrule == Team.CollisionRule.PUSH_OWN_TEAM || team$collisionrule1 == Team.CollisionRule.PUSH_OWN_TEAM) && flag) {
                  return false;
               } else {
                  return team$collisionrule != Team.CollisionRule.PUSH_OTHER_TEAMS && team$collisionrule1 != Team.CollisionRule.PUSH_OTHER_TEAMS || flag;
               }
            }
         } else {
            return false;
         }
      }));
   }

   public static Predicate<Entity> notRiding(Entity p_20432_) {
      return (p_20425_) -> {
         while(true) {
            if (p_20425_.isPassenger()) {
               p_20425_ = p_20425_.getVehicle();
               if (p_20425_ != p_20432_) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   public static class MobCanWearArmorEntitySelector implements Predicate<Entity> {
      private final ItemStack itemStack;

      public MobCanWearArmorEntitySelector(ItemStack p_20445_) {
         this.itemStack = p_20445_;
      }

      public boolean test(@Nullable Entity p_20447_) {
         if (!p_20447_.isAlive()) {
            return false;
         } else if (!(p_20447_ instanceof LivingEntity)) {
            return false;
         } else {
            LivingEntity livingentity = (LivingEntity)p_20447_;
            return livingentity.canTakeItem(this.itemStack);
         }
      }
   }
}