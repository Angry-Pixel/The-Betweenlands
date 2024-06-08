package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetEnchantmentsFunction extends LootItemConditionalFunction {
   final Map<Enchantment, NumberProvider> enchantments;
   final boolean add;

   SetEnchantmentsFunction(LootItemCondition[] p_165337_, Map<Enchantment, NumberProvider> p_165338_, boolean p_165339_) {
      super(p_165337_);
      this.enchantments = ImmutableMap.copyOf(p_165338_);
      this.add = p_165339_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_ENCHANTMENTS;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.enchantments.values().stream().flatMap((p_165349_) -> {
         return p_165349_.getReferencedContextParams().stream();
      }).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack run(ItemStack p_165346_, LootContext p_165347_) {
      Object2IntMap<Enchantment> object2intmap = new Object2IntOpenHashMap<>();
      this.enchantments.forEach((p_165353_, p_165354_) -> {
         object2intmap.put(p_165353_, p_165354_.getInt(p_165347_));
      });
      if (p_165346_.getItem() == Items.BOOK) {
         ItemStack itemstack = new ItemStack(Items.ENCHANTED_BOOK);
         object2intmap.forEach((p_165343_, p_165344_) -> {
            EnchantedBookItem.addEnchantment(itemstack, new EnchantmentInstance(p_165343_, p_165344_));
         });
         return itemstack;
      } else {
         Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(p_165346_);
         if (this.add) {
            object2intmap.forEach((p_165366_, p_165367_) -> {
               updateEnchantment(map, p_165366_, Math.max(map.getOrDefault(p_165366_, 0) + p_165367_, 0));
            });
         } else {
            object2intmap.forEach((p_165361_, p_165362_) -> {
               updateEnchantment(map, p_165361_, Math.max(p_165362_, 0));
            });
         }

         EnchantmentHelper.setEnchantments(map, p_165346_);
         return p_165346_;
      }
   }

   private static void updateEnchantment(Map<Enchantment, Integer> p_165356_, Enchantment p_165357_, int p_165358_) {
      if (p_165358_ == 0) {
         p_165356_.remove(p_165357_);
      } else {
         p_165356_.put(p_165357_, p_165358_);
      }

   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetEnchantmentsFunction.Builder> {
      private final Map<Enchantment, NumberProvider> enchantments = Maps.newHashMap();
      private final boolean add;

      public Builder() {
         this(false);
      }

      public Builder(boolean p_165372_) {
         this.add = p_165372_;
      }

      protected SetEnchantmentsFunction.Builder getThis() {
         return this;
      }

      public SetEnchantmentsFunction.Builder withEnchantment(Enchantment p_165375_, NumberProvider p_165376_) {
         this.enchantments.put(p_165375_, p_165376_);
         return this;
      }

      public LootItemFunction build() {
         return new SetEnchantmentsFunction(this.getConditions(), this.enchantments, this.add);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetEnchantmentsFunction> {
      public void serialize(JsonObject p_165394_, SetEnchantmentsFunction p_165395_, JsonSerializationContext p_165396_) {
         super.serialize(p_165394_, p_165395_, p_165396_);
         JsonObject jsonobject = new JsonObject();
         p_165395_.enchantments.forEach((p_165387_, p_165388_) -> {
            ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(p_165387_);
            if (resourcelocation == null) {
               throw new IllegalArgumentException("Don't know how to serialize enchantment " + p_165387_);
            } else {
               jsonobject.add(resourcelocation.toString(), p_165396_.serialize(p_165388_));
            }
         });
         p_165394_.add("enchantments", jsonobject);
         p_165394_.addProperty("add", p_165395_.add);
      }

      public SetEnchantmentsFunction deserialize(JsonObject p_165381_, JsonDeserializationContext p_165382_, LootItemCondition[] p_165383_) {
         Map<Enchantment, NumberProvider> map = Maps.newHashMap();
         if (p_165381_.has("enchantments")) {
            JsonObject jsonobject = GsonHelper.getAsJsonObject(p_165381_, "enchantments");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               String s = entry.getKey();
               JsonElement jsonelement = entry.getValue();
               Enchantment enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown enchantment '" + s + "'");
               });
               NumberProvider numberprovider = p_165382_.deserialize(jsonelement, NumberProvider.class);
               map.put(enchantment, numberprovider);
            }
         }

         boolean flag = GsonHelper.getAsBoolean(p_165381_, "add", false);
         return new SetEnchantmentsFunction(p_165383_, map, flag);
      }
   }
}