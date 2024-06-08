package net.minecraft.world.entity.ai.goal.target;

import java.util.List;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public class ResetUniversalAngerTargetGoal<T extends Mob & NeutralMob> extends Goal {
   private static final int ALERT_RANGE_Y = 10;
   private final T mob;
   private final boolean alertOthersOfSameType;
   private int lastHurtByPlayerTimestamp;

   public ResetUniversalAngerTargetGoal(T p_26121_, boolean p_26122_) {
      this.mob = p_26121_;
      this.alertOthersOfSameType = p_26122_;
   }

   public boolean canUse() {
      return this.mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.wasHurtByPlayer();
   }

   private boolean wasHurtByPlayer() {
      return this.mob.getLastHurtByMob() != null && this.mob.getLastHurtByMob().getType() == EntityType.PLAYER && this.mob.getLastHurtByMobTimestamp() > this.lastHurtByPlayerTimestamp;
   }

   public void start() {
      this.lastHurtByPlayerTimestamp = this.mob.getLastHurtByMobTimestamp();
      this.mob.forgetCurrentTargetAndRefreshUniversalAnger();
      if (this.alertOthersOfSameType) {
         this.getNearbyMobsOfSameType().stream().filter((p_26127_) -> {
            return p_26127_ != this.mob;
         }).map((p_26125_) -> {
            return (NeutralMob)p_26125_;
         }).forEach(NeutralMob::forgetCurrentTargetAndRefreshUniversalAnger);
      }

      super.start();
   }

   private List<? extends Mob> getNearbyMobsOfSameType() {
      double d0 = this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
      AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
      return this.mob.level.getEntitiesOfClass(this.mob.getClass(), aabb, EntitySelector.NO_SPECTATORS);
   }
}