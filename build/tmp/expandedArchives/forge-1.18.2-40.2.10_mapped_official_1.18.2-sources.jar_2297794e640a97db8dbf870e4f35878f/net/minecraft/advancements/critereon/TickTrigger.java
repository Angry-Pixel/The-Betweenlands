package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TickTrigger extends SimpleCriterionTrigger<TickTrigger.TriggerInstance> {
   public static final ResourceLocation ID = new ResourceLocation("tick");

   public ResourceLocation getId() {
      return ID;
   }

   public TickTrigger.TriggerInstance createInstance(JsonObject p_70644_, EntityPredicate.Composite p_70645_, DeserializationContext p_70646_) {
      return new TickTrigger.TriggerInstance(p_70645_);
   }

   public void trigger(ServerPlayer p_70642_) {
      this.trigger(p_70642_, (p_70648_) -> {
         return true;
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      public TriggerInstance(EntityPredicate.Composite p_70654_) {
         super(TickTrigger.ID, p_70654_);
      }
   }
}