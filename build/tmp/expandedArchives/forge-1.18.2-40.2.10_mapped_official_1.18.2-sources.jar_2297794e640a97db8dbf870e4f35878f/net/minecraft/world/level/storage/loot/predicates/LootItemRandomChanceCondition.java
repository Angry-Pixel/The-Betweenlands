package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootItemRandomChanceCondition implements LootItemCondition {
   final float probability;

   LootItemRandomChanceCondition(float p_81923_) {
      this.probability = p_81923_;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE;
   }

   public boolean test(LootContext p_81930_) {
      return p_81930_.getRandom().nextFloat() < this.probability;
   }

   public static LootItemCondition.Builder randomChance(float p_81928_) {
      return () -> {
         return new LootItemRandomChanceCondition(p_81928_);
      };
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootItemRandomChanceCondition> {
      public void serialize(JsonObject p_81943_, LootItemRandomChanceCondition p_81944_, JsonSerializationContext p_81945_) {
         p_81943_.addProperty("chance", p_81944_.probability);
      }

      public LootItemRandomChanceCondition deserialize(JsonObject p_81951_, JsonDeserializationContext p_81952_) {
         return new LootItemRandomChanceCondition(GsonHelper.getAsFloat(p_81951_, "chance"));
      }
   }
}