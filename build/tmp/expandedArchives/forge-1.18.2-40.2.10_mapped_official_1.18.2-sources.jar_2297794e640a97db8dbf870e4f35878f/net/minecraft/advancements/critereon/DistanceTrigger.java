package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class DistanceTrigger extends SimpleCriterionTrigger<DistanceTrigger.TriggerInstance> {
   final ResourceLocation id;

   public DistanceTrigger(ResourceLocation p_186163_) {
      this.id = p_186163_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public DistanceTrigger.TriggerInstance createInstance(JsonObject p_186174_, EntityPredicate.Composite p_186175_, DeserializationContext p_186176_) {
      LocationPredicate locationpredicate = LocationPredicate.fromJson(p_186174_.get("start_position"));
      DistancePredicate distancepredicate = DistancePredicate.fromJson(p_186174_.get("distance"));
      return new DistanceTrigger.TriggerInstance(this.id, p_186175_, locationpredicate, distancepredicate);
   }

   public void trigger(ServerPlayer p_186166_, Vec3 p_186167_) {
      Vec3 vec3 = p_186166_.position();
      this.trigger(p_186166_, (p_186172_) -> {
         return p_186172_.matches(p_186166_.getLevel(), p_186167_, vec3);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate startPosition;
      private final DistancePredicate distance;

      public TriggerInstance(ResourceLocation p_186184_, EntityPredicate.Composite p_186185_, LocationPredicate p_186186_, DistancePredicate p_186187_) {
         super(p_186184_, p_186185_);
         this.startPosition = p_186186_;
         this.distance = p_186187_;
      }

      public static DistanceTrigger.TriggerInstance fallFromHeight(EntityPredicate.Builder p_186198_, DistancePredicate p_186199_, LocationPredicate p_186200_) {
         return new DistanceTrigger.TriggerInstance(CriteriaTriggers.FALL_FROM_HEIGHT.id, EntityPredicate.Composite.wrap(p_186198_.build()), p_186200_, p_186199_);
      }

      public static DistanceTrigger.TriggerInstance rideEntityInLava(EntityPredicate.Builder p_186195_, DistancePredicate p_186196_) {
         return new DistanceTrigger.TriggerInstance(CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.id, EntityPredicate.Composite.wrap(p_186195_.build()), LocationPredicate.ANY, p_186196_);
      }

      public static DistanceTrigger.TriggerInstance travelledThroughNether(DistancePredicate p_186193_) {
         return new DistanceTrigger.TriggerInstance(CriteriaTriggers.NETHER_TRAVEL.id, EntityPredicate.Composite.ANY, LocationPredicate.ANY, p_186193_);
      }

      public JsonObject serializeToJson(SerializationContext p_186202_) {
         JsonObject jsonobject = super.serializeToJson(p_186202_);
         jsonobject.add("start_position", this.startPosition.serializeToJson());
         jsonobject.add("distance", this.distance.serializeToJson());
         return jsonobject;
      }

      public boolean matches(ServerLevel p_186189_, Vec3 p_186190_, Vec3 p_186191_) {
         if (!this.startPosition.matches(p_186189_, p_186190_.x, p_186190_.y, p_186190_.z)) {
            return false;
         } else {
            return this.distance.matches(p_186190_.x, p_186190_.y, p_186190_.z, p_186191_.x, p_186191_.y, p_186191_.z);
         }
      }
   }
}