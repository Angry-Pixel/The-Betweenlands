package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.logging.LogUtils;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class LootTable {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final LootTable EMPTY = new LootTable(LootContextParamSets.EMPTY, new LootPool[0], new LootItemFunction[0]);
   public static final LootContextParamSet DEFAULT_PARAM_SET = LootContextParamSets.ALL_PARAMS;
   final LootContextParamSet paramSet;
   private final List<LootPool> pools;
   final LootItemFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;

   LootTable(LootContextParamSet p_79114_, LootPool[] p_79115_, LootItemFunction[] p_79116_) {
      this.paramSet = p_79114_;
      this.pools = Lists.newArrayList(p_79115_);
      this.functions = p_79116_;
      this.compositeFunction = LootItemFunctions.compose(p_79116_);
   }

   public static Consumer<ItemStack> createStackSplitter(Consumer<ItemStack> p_79143_) {
      return (p_79146_) -> {
         if (p_79146_.getCount() < p_79146_.getMaxStackSize()) {
            p_79143_.accept(p_79146_);
         } else {
            int i = p_79146_.getCount();

            while(i > 0) {
               ItemStack itemstack = p_79146_.copy();
               itemstack.setCount(Math.min(p_79146_.getMaxStackSize(), i));
               i -= itemstack.getCount();
               p_79143_.accept(itemstack);
            }
         }

      };
   }

   public void getRandomItemsRaw(LootContext p_79132_, Consumer<ItemStack> p_79133_) {
      if (p_79132_.addVisitedTable(this)) {
         Consumer<ItemStack> consumer = LootItemFunction.decorate(this.compositeFunction, p_79133_, p_79132_);

         for(LootPool lootpool : this.pools) {
            lootpool.addRandomItems(consumer, p_79132_);
         }

         p_79132_.removeVisitedTable(this);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   @Deprecated //Use other method or manually call ForgeHooks.modifyLoot
   public void getRandomItems(LootContext p_79149_, Consumer<ItemStack> p_79150_) {
      this.getRandomItemsRaw(p_79149_, createStackSplitter(p_79150_));
   }

   public List<ItemStack> getRandomItems(LootContext p_79130_) {
      List<ItemStack> list = Lists.newArrayList();
      this.getRandomItems(p_79130_, list::add);
      list = net.minecraftforge.common.ForgeHooks.modifyLoot(this.getLootTableId(), list, p_79130_);
      return list;
   }

   public LootContextParamSet getParamSet() {
      return this.paramSet;
   }

   public void validate(ValidationContext p_79137_) {
      for(int i = 0; i < this.pools.size(); ++i) {
         this.pools.get(i).validate(p_79137_.forChild(".pools[" + i + "]"));
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].validate(p_79137_.forChild(".functions[" + j + "]"));
      }

   }

   public void fill(Container p_79124_, LootContext p_79125_) {
      List<ItemStack> list = this.getRandomItems(p_79125_);
      Random random = p_79125_.getRandom();
      List<Integer> list1 = this.getAvailableSlots(p_79124_, random);
      this.shuffleAndSplitItems(list, list1.size(), random);

      for(ItemStack itemstack : list) {
         if (list1.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (itemstack.isEmpty()) {
            p_79124_.setItem(list1.remove(list1.size() - 1), ItemStack.EMPTY);
         } else {
            p_79124_.setItem(list1.remove(list1.size() - 1), itemstack);
         }
      }

   }

   private void shuffleAndSplitItems(List<ItemStack> p_79139_, int p_79140_, Random p_79141_) {
      List<ItemStack> list = Lists.newArrayList();
      Iterator<ItemStack> iterator = p_79139_.iterator();

      while(iterator.hasNext()) {
         ItemStack itemstack = iterator.next();
         if (itemstack.isEmpty()) {
            iterator.remove();
         } else if (itemstack.getCount() > 1) {
            list.add(itemstack);
            iterator.remove();
         }
      }

      while(p_79140_ - p_79139_.size() - list.size() > 0 && !list.isEmpty()) {
         ItemStack itemstack2 = list.remove(Mth.nextInt(p_79141_, 0, list.size() - 1));
         int i = Mth.nextInt(p_79141_, 1, itemstack2.getCount() / 2);
         ItemStack itemstack1 = itemstack2.split(i);
         if (itemstack2.getCount() > 1 && p_79141_.nextBoolean()) {
            list.add(itemstack2);
         } else {
            p_79139_.add(itemstack2);
         }

         if (itemstack1.getCount() > 1 && p_79141_.nextBoolean()) {
            list.add(itemstack1);
         } else {
            p_79139_.add(itemstack1);
         }
      }

      p_79139_.addAll(list);
      Collections.shuffle(p_79139_, p_79141_);
   }

   private List<Integer> getAvailableSlots(Container p_79127_, Random p_79128_) {
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < p_79127_.getContainerSize(); ++i) {
         if (p_79127_.getItem(i).isEmpty()) {
            list.add(i);
         }
      }

      Collections.shuffle(list, p_79128_);
      return list;
   }

   public static LootTable.Builder lootTable() {
      return new LootTable.Builder();
   }

   //======================== FORGE START =============================================
   private boolean isFrozen = false;
   public void freeze() {
      this.isFrozen = true;
      this.pools.forEach(LootPool::freeze);
   }
   public boolean isFrozen(){ return this.isFrozen; }
   private void checkFrozen() {
      if (this.isFrozen())
         throw new RuntimeException("Attempted to modify LootTable after being finalized!");
   }

   private ResourceLocation lootTableId;
   public void setLootTableId(final ResourceLocation id) {
      if (this.lootTableId != null) throw new IllegalStateException("Attempted to rename loot table from '" + this.lootTableId + "' to '" + id + "': this is not supported");
      this.lootTableId = java.util.Objects.requireNonNull(id);
   }
   public ResourceLocation getLootTableId() { return this.lootTableId; }

   public LootPool getPool(String name) {
      return pools.stream().filter(e -> name.equals(e.getName())).findFirst().orElse(null);
   }

   public LootPool removePool(String name) {
      checkFrozen();
      for (LootPool pool : this.pools) {
         if (name.equals(pool.getName())) {
            this.pools.remove(pool);
            return pool;
         }
      }
      return null;
   }

   public void addPool(LootPool pool) {
      checkFrozen();
      if (pools.stream().anyMatch(e -> e == pool || e.getName() != null && e.getName().equals(pool.getName())))
         throw new RuntimeException("Attempted to add a duplicate pool to loot table: " + pool.getName());
      this.pools.add(pool);
   }
   //======================== FORGE END ===============================================

   public static class Builder implements FunctionUserBuilder<LootTable.Builder> {
      private final List<LootPool> pools = Lists.newArrayList();
      private final List<LootItemFunction> functions = Lists.newArrayList();
      private LootContextParamSet paramSet = LootTable.DEFAULT_PARAM_SET;

      public LootTable.Builder withPool(LootPool.Builder p_79162_) {
         this.pools.add(p_79162_.build());
         return this;
      }

      public LootTable.Builder setParamSet(LootContextParamSet p_79166_) {
         this.paramSet = p_79166_;
         return this;
      }

      public LootTable.Builder apply(LootItemFunction.Builder p_79164_) {
         this.functions.add(p_79164_.build());
         return this;
      }

      public LootTable.Builder unwrap() {
         return this;
      }

      public LootTable build() {
         return new LootTable(this.paramSet, this.pools.toArray(new LootPool[0]), this.functions.toArray(new LootItemFunction[0]));
      }
   }

   public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
      public LootTable deserialize(JsonElement p_79173_, Type p_79174_, JsonDeserializationContext p_79175_) throws JsonParseException {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_79173_, "loot table");
         LootPool[] alootpool = GsonHelper.getAsObject(jsonobject, "pools", new LootPool[0], p_79175_, LootPool[].class);
         LootContextParamSet lootcontextparamset = null;
         if (jsonobject.has("type")) {
            String s = GsonHelper.getAsString(jsonobject, "type");
            lootcontextparamset = LootContextParamSets.get(new ResourceLocation(s));
         }

         LootItemFunction[] alootitemfunction = GsonHelper.getAsObject(jsonobject, "functions", new LootItemFunction[0], p_79175_, LootItemFunction[].class);
         return new LootTable(lootcontextparamset != null ? lootcontextparamset : LootContextParamSets.ALL_PARAMS, alootpool, alootitemfunction);
      }

      public JsonElement serialize(LootTable p_79177_, Type p_79178_, JsonSerializationContext p_79179_) {
         JsonObject jsonobject = new JsonObject();
         if (p_79177_.paramSet != LootTable.DEFAULT_PARAM_SET) {
            ResourceLocation resourcelocation = LootContextParamSets.getKey(p_79177_.paramSet);
            if (resourcelocation != null) {
               jsonobject.addProperty("type", resourcelocation.toString());
            } else {
               LootTable.LOGGER.warn("Failed to find id for param set {}", (Object)p_79177_.paramSet);
            }
         }

         if (!p_79177_.pools.isEmpty()) {
            jsonobject.add("pools", p_79179_.serialize(p_79177_.pools));
         }

         if (!ArrayUtils.isEmpty((Object[])p_79177_.functions)) {
            jsonobject.add("functions", p_79179_.serialize(p_79177_.functions));
         }

         return jsonobject;
      }
   }
}
