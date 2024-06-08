package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class ValueCheckCondition implements LootItemCondition {
   final NumberProvider provider;
   final IntRange range;

   ValueCheckCondition(NumberProvider p_165523_, IntRange p_165524_) {
      this.provider = p_165523_;
      this.range = p_165524_;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.VALUE_CHECK;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.provider.getReferencedContextParams(), this.range.getReferencedContextParams());
   }

   public boolean test(LootContext p_165527_) {
      return this.range.test(p_165527_, this.provider.getInt(p_165527_));
   }

   public static LootItemCondition.Builder hasValue(NumberProvider p_165529_, IntRange p_165530_) {
      return () -> {
         return new ValueCheckCondition(p_165529_, p_165530_);
      };
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ValueCheckCondition> {
      public void serialize(JsonObject p_165542_, ValueCheckCondition p_165543_, JsonSerializationContext p_165544_) {
         p_165542_.add("value", p_165544_.serialize(p_165543_.provider));
         p_165542_.add("range", p_165544_.serialize(p_165543_.range));
      }

      public ValueCheckCondition deserialize(JsonObject p_165550_, JsonDeserializationContext p_165551_) {
         NumberProvider numberprovider = GsonHelper.getAsObject(p_165550_, "value", p_165551_, NumberProvider.class);
         IntRange intrange = GsonHelper.getAsObject(p_165550_, "range", p_165551_, IntRange.class);
         return new ValueCheckCondition(numberprovider, intrange);
      }
   }
}