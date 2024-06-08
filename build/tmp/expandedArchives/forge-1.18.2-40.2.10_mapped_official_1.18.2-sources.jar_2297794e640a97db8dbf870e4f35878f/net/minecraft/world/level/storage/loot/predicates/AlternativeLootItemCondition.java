package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public class AlternativeLootItemCondition implements LootItemCondition {
   final LootItemCondition[] terms;
   private final Predicate<LootContext> composedPredicate;

   AlternativeLootItemCondition(LootItemCondition[] p_81471_) {
      this.terms = p_81471_;
      this.composedPredicate = LootItemConditions.orConditions(p_81471_);
   }

   public LootItemConditionType getType() {
      return LootItemConditions.ALTERNATIVE;
   }

   public final boolean test(LootContext p_81476_) {
      return this.composedPredicate.test(p_81476_);
   }

   public void validate(ValidationContext p_81478_) {
      LootItemCondition.super.validate(p_81478_);

      for(int i = 0; i < this.terms.length; ++i) {
         this.terms[i].validate(p_81478_.forChild(".term[" + i + "]"));
      }

   }

   public static AlternativeLootItemCondition.Builder alternative(LootItemCondition.Builder... p_81482_) {
      return new AlternativeLootItemCondition.Builder(p_81482_);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final List<LootItemCondition> terms = Lists.newArrayList();

      public Builder(LootItemCondition.Builder... p_81488_) {
         for(LootItemCondition.Builder lootitemcondition$builder : p_81488_) {
            this.terms.add(lootitemcondition$builder.build());
         }

      }

      public AlternativeLootItemCondition.Builder or(LootItemCondition.Builder p_81490_) {
         this.terms.add(p_81490_.build());
         return this;
      }

      public LootItemCondition build() {
         return new AlternativeLootItemCondition(this.terms.toArray(new LootItemCondition[0]));
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<AlternativeLootItemCondition> {
      public void serialize(JsonObject p_81497_, AlternativeLootItemCondition p_81498_, JsonSerializationContext p_81499_) {
         p_81497_.add("terms", p_81499_.serialize(p_81498_.terms));
      }

      public AlternativeLootItemCondition deserialize(JsonObject p_81505_, JsonDeserializationContext p_81506_) {
         LootItemCondition[] alootitemcondition = GsonHelper.getAsObject(p_81505_, "terms", p_81506_, LootItemCondition[].class);
         return new AlternativeLootItemCondition(alootitemcondition);
      }
   }
}