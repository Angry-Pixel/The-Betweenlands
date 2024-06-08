package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class LocationTrigger extends SimpleCriterionTrigger<LocationTrigger.TriggerInstance> {
   final ResourceLocation id;

   public LocationTrigger(ResourceLocation p_53643_) {
      this.id = p_53643_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public LocationTrigger.TriggerInstance createInstance(JsonObject p_53653_, EntityPredicate.Composite p_53654_, DeserializationContext p_53655_) {
      JsonObject jsonobject = GsonHelper.getAsJsonObject(p_53653_, "location", p_53653_);
      LocationPredicate locationpredicate = LocationPredicate.fromJson(jsonobject);
      return new LocationTrigger.TriggerInstance(this.id, p_53654_, locationpredicate);
   }

   public void trigger(ServerPlayer p_53646_) {
      this.trigger(p_53646_, (p_53649_) -> {
         return p_53649_.matches(p_53646_.getLevel(), p_53646_.getX(), p_53646_.getY(), p_53646_.getZ());
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate location;

      public TriggerInstance(ResourceLocation p_53662_, EntityPredicate.Composite p_53663_, LocationPredicate p_53664_) {
         super(p_53662_, p_53663_);
         this.location = p_53664_;
      }

      public static LocationTrigger.TriggerInstance located(LocationPredicate p_53671_) {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.LOCATION.id, EntityPredicate.Composite.ANY, p_53671_);
      }

      public static LocationTrigger.TriggerInstance located(EntityPredicate p_154321_) {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.LOCATION.id, EntityPredicate.Composite.wrap(p_154321_), LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance sleptInBed() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.id, EntityPredicate.Composite.ANY, LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance raidWon() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.RAID_WIN.id, EntityPredicate.Composite.ANY, LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance walkOnBlockWithEquipment(Block p_154323_, Item p_154324_) {
         return located(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of(p_154324_).build()).build()).steppingOn(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(p_154323_).build()).build()).build());
      }

      public boolean matches(ServerLevel p_53666_, double p_53667_, double p_53668_, double p_53669_) {
         return this.location.matches(p_53666_, p_53667_, p_53668_, p_53669_);
      }

      public JsonObject serializeToJson(SerializationContext p_53673_) {
         JsonObject jsonobject = super.serializeToJson(p_53673_);
         jsonobject.add("location", this.location.serializeToJson());
         return jsonobject;
      }
   }
}