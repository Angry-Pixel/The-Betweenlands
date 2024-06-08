package net.minecraft.world.level.storage.loot.predicates;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class InvertedLootItemCondition implements LootItemCondition {
   final LootItemCondition term;

   InvertedLootItemCondition(LootItemCondition p_81683_) {
      this.term = p_81683_;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.INVERTED;
   }

   public final boolean test(LootContext p_81689_) {
      return !this.term.test(p_81689_);
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.term.getReferencedContextParams();
   }

   public void validate(ValidationContext p_81691_) {
      LootItemCondition.super.validate(p_81691_);
      this.term.validate(p_81691_);
   }

   public static LootItemCondition.Builder invert(LootItemCondition.Builder p_81695_) {
      InvertedLootItemCondition invertedlootitemcondition = new InvertedLootItemCondition(p_81695_.build());
      return () -> {
         return invertedlootitemcondition;
      };
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<InvertedLootItemCondition> {
      public void serialize(JsonObject p_81706_, InvertedLootItemCondition p_81707_, JsonSerializationContext p_81708_) {
         p_81706_.add("term", p_81708_.serialize(p_81707_.term));
      }

      public InvertedLootItemCondition deserialize(JsonObject p_81714_, JsonDeserializationContext p_81715_) {
         LootItemCondition lootitemcondition = GsonHelper.getAsObject(p_81714_, "term", p_81715_, LootItemCondition.class);
         return new InvertedLootItemCondition(lootitemcondition);
      }
   }
}