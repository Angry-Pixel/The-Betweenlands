package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LocationCheck implements LootItemCondition {
   final LocationPredicate predicate;
   final BlockPos offset;

   LocationCheck(LocationPredicate p_81719_, BlockPos p_81720_) {
      this.predicate = p_81719_;
      this.offset = p_81720_;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.LOCATION_CHECK;
   }

   public boolean test(LootContext p_81731_) {
      Vec3 vec3 = p_81731_.getParamOrNull(LootContextParams.ORIGIN);
      return vec3 != null && this.predicate.matches(p_81731_.getLevel(), vec3.x() + (double)this.offset.getX(), vec3.y() + (double)this.offset.getY(), vec3.z() + (double)this.offset.getZ());
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder p_81726_) {
      return () -> {
         return new LocationCheck(p_81726_.build(), BlockPos.ZERO);
      };
   }

   public static LootItemCondition.Builder checkLocation(LocationPredicate.Builder p_81728_, BlockPos p_81729_) {
      return () -> {
         return new LocationCheck(p_81728_.build(), p_81729_);
      };
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LocationCheck> {
      public void serialize(JsonObject p_81749_, LocationCheck p_81750_, JsonSerializationContext p_81751_) {
         p_81749_.add("predicate", p_81750_.predicate.serializeToJson());
         if (p_81750_.offset.getX() != 0) {
            p_81749_.addProperty("offsetX", p_81750_.offset.getX());
         }

         if (p_81750_.offset.getY() != 0) {
            p_81749_.addProperty("offsetY", p_81750_.offset.getY());
         }

         if (p_81750_.offset.getZ() != 0) {
            p_81749_.addProperty("offsetZ", p_81750_.offset.getZ());
         }

      }

      public LocationCheck deserialize(JsonObject p_81757_, JsonDeserializationContext p_81758_) {
         LocationPredicate locationpredicate = LocationPredicate.fromJson(p_81757_.get("predicate"));
         int i = GsonHelper.getAsInt(p_81757_, "offsetX", 0);
         int j = GsonHelper.getAsInt(p_81757_, "offsetY", 0);
         int k = GsonHelper.getAsInt(p_81757_, "offsetZ", 0);
         return new LocationCheck(locationpredicate, new BlockPos(i, j, k));
      }
   }
}