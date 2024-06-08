package net.minecraft.world.entity.ai.village;

public interface ReputationEventType {
   ReputationEventType ZOMBIE_VILLAGER_CURED = register("zombie_villager_cured");
   ReputationEventType GOLEM_KILLED = register("golem_killed");
   ReputationEventType VILLAGER_HURT = register("villager_hurt");
   ReputationEventType VILLAGER_KILLED = register("villager_killed");
   ReputationEventType TRADE = register("trade");

   static ReputationEventType register(final String p_26992_) {
      return new ReputationEventType() {
         public String toString() {
            return p_26992_;
         }
      };
   }
}