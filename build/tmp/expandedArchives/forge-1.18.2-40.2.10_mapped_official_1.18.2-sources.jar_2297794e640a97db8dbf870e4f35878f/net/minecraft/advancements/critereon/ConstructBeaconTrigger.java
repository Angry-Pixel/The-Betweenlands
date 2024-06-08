package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<ConstructBeaconTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ResourceLocation getId() {
      return ID;
   }

   public ConstructBeaconTrigger.TriggerInstance createInstance(JsonObject p_22753_, EntityPredicate.Composite p_22754_, DeserializationContext p_22755_) {
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_22753_.get("level"));
      return new ConstructBeaconTrigger.TriggerInstance(p_22754_, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer p_148030_, int p_148031_) {
      this.trigger(p_148030_, (p_148028_) -> {
         return p_148028_.matches(p_148031_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(EntityPredicate.Composite p_22763_, MinMaxBounds.Ints p_22764_) {
         super(ConstructBeaconTrigger.ID, p_22763_);
         this.level = p_22764_;
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon() {
         return new ConstructBeaconTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY);
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon(MinMaxBounds.Ints p_22766_) {
         return new ConstructBeaconTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_22766_);
      }

      public boolean matches(int p_148033_) {
         return this.level.matches(p_148033_);
      }

      public JsonObject serializeToJson(SerializationContext p_22770_) {
         JsonObject jsonobject = super.serializeToJson(p_22770_);
         jsonobject.add("level", this.level.serializeToJson());
         return jsonobject;
      }
   }
}