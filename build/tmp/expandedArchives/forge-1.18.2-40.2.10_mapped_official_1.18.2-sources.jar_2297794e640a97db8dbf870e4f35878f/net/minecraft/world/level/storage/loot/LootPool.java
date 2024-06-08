package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
   private final String name;
   final LootPoolEntryContainer[] entries;
   final LootItemCondition[] conditions;
   private final Predicate<LootContext> compositeCondition;
   final LootItemFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
   NumberProvider rolls;
   NumberProvider bonusRolls;

   LootPool(LootPoolEntryContainer[] p_165128_, LootItemCondition[] p_165129_, LootItemFunction[] p_165130_, NumberProvider p_165131_, NumberProvider p_165132_, String name) {
      this.name = name;
      this.entries = p_165128_;
      this.conditions = p_165129_;
      this.compositeCondition = LootItemConditions.andConditions(p_165129_);
      this.functions = p_165130_;
      this.compositeFunction = LootItemFunctions.compose(p_165130_);
      this.rolls = p_165131_;
      this.bonusRolls = p_165132_;
   }

   private void addRandomItem(Consumer<ItemStack> p_79059_, LootContext p_79060_) {
      Random random = p_79060_.getRandom();
      List<LootPoolEntry> list = Lists.newArrayList();
      MutableInt mutableint = new MutableInt();

      for(LootPoolEntryContainer lootpoolentrycontainer : this.entries) {
         lootpoolentrycontainer.expand(p_79060_, (p_79048_) -> {
            int k = p_79048_.getWeight(p_79060_.getLuck());
            if (k > 0) {
               list.add(p_79048_);
               mutableint.add(k);
            }

         });
      }

      int i = list.size();
      if (mutableint.intValue() != 0 && i != 0) {
         if (i == 1) {
            list.get(0).createItemStack(p_79059_, p_79060_);
         } else {
            int j = random.nextInt(mutableint.intValue());

            for(LootPoolEntry lootpoolentry : list) {
               j -= lootpoolentry.getWeight(p_79060_.getLuck());
               if (j < 0) {
                  lootpoolentry.createItemStack(p_79059_, p_79060_);
                  return;
               }
            }

         }
      }
   }

   public void addRandomItems(Consumer<ItemStack> p_79054_, LootContext p_79055_) {
      if (this.compositeCondition.test(p_79055_)) {
         Consumer<ItemStack> consumer = LootItemFunction.decorate(this.compositeFunction, p_79054_, p_79055_);
         int i = this.rolls.getInt(p_79055_) + Mth.floor(this.bonusRolls.getFloat(p_79055_) * p_79055_.getLuck());

         for(int j = 0; j < i; ++j) {
            this.addRandomItem(consumer, p_79055_);
         }

      }
   }

   public void validate(ValidationContext p_79052_) {
      for(int i = 0; i < this.conditions.length; ++i) {
         this.conditions[i].validate(p_79052_.forChild(".condition[" + i + "]"));
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].validate(p_79052_.forChild(".functions[" + j + "]"));
      }

      for(int k = 0; k < this.entries.length; ++k) {
         this.entries[k].validate(p_79052_.forChild(".entries[" + k + "]"));
      }

      this.rolls.validate(p_79052_.forChild(".rolls"));
      this.bonusRolls.validate(p_79052_.forChild(".bonusRolls"));
   }
   //======================== FORGE START =============================================
   private boolean isFrozen = false;
   public void freeze() { this.isFrozen = true; }
   public boolean isFrozen(){ return this.isFrozen; }
   private void checkFrozen() {
      if (this.isFrozen())
         throw new RuntimeException("Attempted to modify LootPool after being frozen!");
   }
   public String getName(){ return this.name; }
   public NumberProvider getRolls()      { return this.rolls; }
   public NumberProvider getBonusRolls() { return this.bonusRolls; }
   public void setRolls     (NumberProvider v){ checkFrozen(); this.rolls = v; }
   public void setBonusRolls(NumberProvider v){ checkFrozen(); this.bonusRolls = v; }
   //======================== FORGE END ===============================================

   public static LootPool.Builder lootPool() {
      return new LootPool.Builder();
   }

   public static class Builder implements FunctionUserBuilder<LootPool.Builder>, ConditionUserBuilder<LootPool.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();
      private final List<LootItemCondition> conditions = Lists.newArrayList();
      private final List<LootItemFunction> functions = Lists.newArrayList();
      private NumberProvider rolls = ConstantValue.exactly(1.0F);
      private NumberProvider bonusRolls = ConstantValue.exactly(0.0F);
      private String name;

      public LootPool.Builder setRolls(NumberProvider p_165134_) {
         this.rolls = p_165134_;
         return this;
      }

      public LootPool.Builder unwrap() {
         return this;
      }

      public LootPool.Builder setBonusRolls(NumberProvider p_165136_) {
         this.bonusRolls = p_165136_;
         return this;
      }

      public LootPool.Builder add(LootPoolEntryContainer.Builder<?> p_79077_) {
         this.entries.add(p_79077_.build());
         return this;
      }

      public LootPool.Builder when(LootItemCondition.Builder p_79081_) {
         this.conditions.add(p_79081_.build());
         return this;
      }

      public LootPool.Builder apply(LootItemFunction.Builder p_79079_) {
         this.functions.add(p_79079_.build());
         return this;
      }

      public LootPool.Builder name(String name) {
         this.name = name;
         return this;
      }

      public LootPool build() {
         if (this.rolls == null) {
            throw new IllegalArgumentException("Rolls not set");
         } else {
            return new LootPool(this.entries.toArray(new LootPoolEntryContainer[0]), this.conditions.toArray(new LootItemCondition[0]), this.functions.toArray(new LootItemFunction[0]), this.rolls, this.bonusRolls, name);
         }
      }
   }

   public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
      public LootPool deserialize(JsonElement p_79090_, Type p_79091_, JsonDeserializationContext p_79092_) throws JsonParseException {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_79090_, "loot pool");
         LootPoolEntryContainer[] alootpoolentrycontainer = GsonHelper.getAsObject(jsonobject, "entries", p_79092_, LootPoolEntryContainer[].class);
         LootItemCondition[] alootitemcondition = GsonHelper.getAsObject(jsonobject, "conditions", new LootItemCondition[0], p_79092_, LootItemCondition[].class);
         LootItemFunction[] alootitemfunction = GsonHelper.getAsObject(jsonobject, "functions", new LootItemFunction[0], p_79092_, LootItemFunction[].class);
         NumberProvider numberprovider = GsonHelper.getAsObject(jsonobject, "rolls", p_79092_, NumberProvider.class);
         NumberProvider numberprovider1 = GsonHelper.getAsObject(jsonobject, "bonus_rolls", ConstantValue.exactly(0.0F), p_79092_, NumberProvider.class);
         return new LootPool(alootpoolentrycontainer, alootitemcondition, alootitemfunction, numberprovider, numberprovider1, net.minecraftforge.common.ForgeHooks.readPoolName(jsonobject));
      }

      public JsonElement serialize(LootPool p_79094_, Type p_79095_, JsonSerializationContext p_79096_) {
         JsonObject jsonobject = new JsonObject();
         if (p_79094_.name != null && !p_79094_.name.startsWith("custom#"))
            jsonobject.add("name", p_79096_.serialize(p_79094_.name));
         jsonobject.add("rolls", p_79096_.serialize(p_79094_.rolls));
         jsonobject.add("bonus_rolls", p_79096_.serialize(p_79094_.bonusRolls));
         jsonobject.add("entries", p_79096_.serialize(p_79094_.entries));
         if (!ArrayUtils.isEmpty((Object[])p_79094_.conditions)) {
            jsonobject.add("conditions", p_79096_.serialize(p_79094_.conditions));
         }

         if (!ArrayUtils.isEmpty((Object[])p_79094_.functions)) {
            jsonobject.add("functions", p_79096_.serialize(p_79094_.functions));
         }

         return jsonobject;
      }
   }
}
