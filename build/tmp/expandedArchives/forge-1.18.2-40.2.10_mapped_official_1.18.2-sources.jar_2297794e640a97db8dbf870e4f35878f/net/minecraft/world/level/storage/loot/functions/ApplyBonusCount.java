package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyBonusCount extends LootItemConditionalFunction {
   static final Map<ResourceLocation, ApplyBonusCount.FormulaDeserializer> FORMULAS = Maps.newHashMap();
   final Enchantment enchantment;
   final ApplyBonusCount.Formula formula;

   ApplyBonusCount(LootItemCondition[] p_79903_, Enchantment p_79904_, ApplyBonusCount.Formula p_79905_) {
      super(p_79903_);
      this.enchantment = p_79904_;
      this.formula = p_79905_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.APPLY_BONUS;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public ItemStack run(ItemStack p_79913_, LootContext p_79914_) {
      ItemStack itemstack = p_79914_.getParamOrNull(LootContextParams.TOOL);
      if (itemstack != null) {
         int i = EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, itemstack);
         int j = this.formula.calculateNewCount(p_79914_.getRandom(), p_79913_.getCount(), i);
         p_79913_.setCount(j);
      }

      return p_79913_;
   }

   public static LootItemConditionalFunction.Builder<?> addBonusBinomialDistributionCount(Enchantment p_79918_, float p_79919_, int p_79920_) {
      return simpleBuilder((p_79928_) -> {
         return new ApplyBonusCount(p_79928_, p_79918_, new ApplyBonusCount.BinomialWithBonusCount(p_79920_, p_79919_));
      });
   }

   public static LootItemConditionalFunction.Builder<?> addOreBonusCount(Enchantment p_79916_) {
      return simpleBuilder((p_79943_) -> {
         return new ApplyBonusCount(p_79943_, p_79916_, new ApplyBonusCount.OreDrops());
      });
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment p_79940_) {
      return simpleBuilder((p_79935_) -> {
         return new ApplyBonusCount(p_79935_, p_79940_, new ApplyBonusCount.UniformBonusCount(1));
      });
   }

   public static LootItemConditionalFunction.Builder<?> addUniformBonusCount(Enchantment p_79922_, int p_79923_) {
      return simpleBuilder((p_79932_) -> {
         return new ApplyBonusCount(p_79932_, p_79922_, new ApplyBonusCount.UniformBonusCount(p_79923_));
      });
   }

   static {
      FORMULAS.put(ApplyBonusCount.BinomialWithBonusCount.TYPE, ApplyBonusCount.BinomialWithBonusCount::deserialize);
      FORMULAS.put(ApplyBonusCount.OreDrops.TYPE, ApplyBonusCount.OreDrops::deserialize);
      FORMULAS.put(ApplyBonusCount.UniformBonusCount.TYPE, ApplyBonusCount.UniformBonusCount::deserialize);
   }

   static final class BinomialWithBonusCount implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("binomial_with_bonus_count");
      private final int extraRounds;
      private final float probability;

      public BinomialWithBonusCount(int p_79952_, float p_79953_) {
         this.extraRounds = p_79952_;
         this.probability = p_79953_;
      }

      public int calculateNewCount(Random p_79962_, int p_79963_, int p_79964_) {
         for(int i = 0; i < p_79964_ + this.extraRounds; ++i) {
            if (p_79962_.nextFloat() < this.probability) {
               ++p_79963_;
            }
         }

         return p_79963_;
      }

      public void serializeParams(JsonObject p_79959_, JsonSerializationContext p_79960_) {
         p_79959_.addProperty("extra", this.extraRounds);
         p_79959_.addProperty("probability", this.probability);
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject p_79956_, JsonDeserializationContext p_79957_) {
         int i = GsonHelper.getAsInt(p_79956_, "extra");
         float f = GsonHelper.getAsFloat(p_79956_, "probability");
         return new ApplyBonusCount.BinomialWithBonusCount(i, f);
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }

   interface Formula {
      int calculateNewCount(Random p_79967_, int p_79968_, int p_79969_);

      void serializeParams(JsonObject p_79965_, JsonSerializationContext p_79966_);

      ResourceLocation getType();
   }

   interface FormulaDeserializer {
      ApplyBonusCount.Formula deserialize(JsonObject p_79971_, JsonDeserializationContext p_79972_);
   }

   static final class OreDrops implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("ore_drops");

      public int calculateNewCount(Random p_79986_, int p_79987_, int p_79988_) {
         if (p_79988_ > 0) {
            int i = p_79986_.nextInt(p_79988_ + 2) - 1;
            if (i < 0) {
               i = 0;
            }

            return p_79987_ * (i + 1);
         } else {
            return p_79987_;
         }
      }

      public void serializeParams(JsonObject p_79983_, JsonSerializationContext p_79984_) {
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject p_79980_, JsonDeserializationContext p_79981_) {
         return new ApplyBonusCount.OreDrops();
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyBonusCount> {
      public void serialize(JsonObject p_79995_, ApplyBonusCount p_79996_, JsonSerializationContext p_79997_) {
         super.serialize(p_79995_, p_79996_, p_79997_);
         p_79995_.addProperty("enchantment", Registry.ENCHANTMENT.getKey(p_79996_.enchantment).toString());
         p_79995_.addProperty("formula", p_79996_.formula.getType().toString());
         JsonObject jsonobject = new JsonObject();
         p_79996_.formula.serializeParams(jsonobject, p_79997_);
         if (jsonobject.size() > 0) {
            p_79995_.add("parameters", jsonobject);
         }

      }

      public ApplyBonusCount deserialize(JsonObject p_79991_, JsonDeserializationContext p_79992_, LootItemCondition[] p_79993_) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_79991_, "enchantment"));
         Enchantment enchantment = Registry.ENCHANTMENT.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + resourcelocation);
         });
         ResourceLocation resourcelocation1 = new ResourceLocation(GsonHelper.getAsString(p_79991_, "formula"));
         ApplyBonusCount.FormulaDeserializer applybonuscount$formuladeserializer = ApplyBonusCount.FORMULAS.get(resourcelocation1);
         if (applybonuscount$formuladeserializer == null) {
            throw new JsonParseException("Invalid formula id: " + resourcelocation1);
         } else {
            ApplyBonusCount.Formula applybonuscount$formula;
            if (p_79991_.has("parameters")) {
               applybonuscount$formula = applybonuscount$formuladeserializer.deserialize(GsonHelper.getAsJsonObject(p_79991_, "parameters"), p_79992_);
            } else {
               applybonuscount$formula = applybonuscount$formuladeserializer.deserialize(new JsonObject(), p_79992_);
            }

            return new ApplyBonusCount(p_79993_, enchantment, applybonuscount$formula);
         }
      }
   }

   static final class UniformBonusCount implements ApplyBonusCount.Formula {
      public static final ResourceLocation TYPE = new ResourceLocation("uniform_bonus_count");
      private final int bonusMultiplier;

      public UniformBonusCount(int p_80016_) {
         this.bonusMultiplier = p_80016_;
      }

      public int calculateNewCount(Random p_80025_, int p_80026_, int p_80027_) {
         return p_80026_ + p_80025_.nextInt(this.bonusMultiplier * p_80027_ + 1);
      }

      public void serializeParams(JsonObject p_80022_, JsonSerializationContext p_80023_) {
         p_80022_.addProperty("bonusMultiplier", this.bonusMultiplier);
      }

      public static ApplyBonusCount.Formula deserialize(JsonObject p_80019_, JsonDeserializationContext p_80020_) {
         int i = GsonHelper.getAsInt(p_80019_, "bonusMultiplier");
         return new ApplyBonusCount.UniformBonusCount(i);
      }

      public ResourceLocation getType() {
         return TYPE;
      }
   }
}