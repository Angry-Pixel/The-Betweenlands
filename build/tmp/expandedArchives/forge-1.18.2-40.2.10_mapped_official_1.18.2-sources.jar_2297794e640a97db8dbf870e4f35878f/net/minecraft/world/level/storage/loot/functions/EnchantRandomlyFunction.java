package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class EnchantRandomlyFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   final List<Enchantment> enchantments;

   EnchantRandomlyFunction(LootItemCondition[] p_80418_, Collection<Enchantment> p_80419_) {
      super(p_80418_);
      this.enchantments = ImmutableList.copyOf(p_80419_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.ENCHANT_RANDOMLY;
   }

   public ItemStack run(ItemStack p_80429_, LootContext p_80430_) {
      Random random = p_80430_.getRandom();
      Enchantment enchantment;
      if (this.enchantments.isEmpty()) {
         boolean flag = p_80429_.is(Items.BOOK);
         List<Enchantment> list = Registry.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter((p_80436_) -> {
            return flag || p_80436_.canEnchant(p_80429_);
         }).collect(Collectors.toList());
         if (list.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)p_80429_);
            return p_80429_;
         }

         enchantment = list.get(random.nextInt(list.size()));
      } else {
         enchantment = this.enchantments.get(random.nextInt(this.enchantments.size()));
      }

      return enchantItem(p_80429_, enchantment, random);
   }

   private static ItemStack enchantItem(ItemStack p_80425_, Enchantment p_80426_, Random p_80427_) {
      int i = Mth.nextInt(p_80427_, p_80426_.getMinLevel(), p_80426_.getMaxLevel());
      if (p_80425_.is(Items.BOOK)) {
         p_80425_ = new ItemStack(Items.ENCHANTED_BOOK);
         EnchantedBookItem.addEnchantment(p_80425_, new EnchantmentInstance(p_80426_, i));
      } else {
         p_80425_.enchant(p_80426_, i);
      }

      return p_80425_;
   }

   public static EnchantRandomlyFunction.Builder randomEnchantment() {
      return new EnchantRandomlyFunction.Builder();
   }

   public static LootItemConditionalFunction.Builder<?> randomApplicableEnchantment() {
      return simpleBuilder((p_80438_) -> {
         return new EnchantRandomlyFunction(p_80438_, ImmutableList.of());
      });
   }

   public static class Builder extends LootItemConditionalFunction.Builder<EnchantRandomlyFunction.Builder> {
      private final Set<Enchantment> enchantments = Sets.newHashSet();

      protected EnchantRandomlyFunction.Builder getThis() {
         return this;
      }

      public EnchantRandomlyFunction.Builder withEnchantment(Enchantment p_80445_) {
         this.enchantments.add(p_80445_);
         return this;
      }

      public LootItemFunction build() {
         return new EnchantRandomlyFunction(this.getConditions(), this.enchantments);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantRandomlyFunction> {
      public void serialize(JsonObject p_80454_, EnchantRandomlyFunction p_80455_, JsonSerializationContext p_80456_) {
         super.serialize(p_80454_, p_80455_, p_80456_);
         if (!p_80455_.enchantments.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(Enchantment enchantment : p_80455_.enchantments) {
               ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(enchantment);
               if (resourcelocation == null) {
                  throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
               }

               jsonarray.add(new JsonPrimitive(resourcelocation.toString()));
            }

            p_80454_.add("enchantments", jsonarray);
         }

      }

      public EnchantRandomlyFunction deserialize(JsonObject p_80450_, JsonDeserializationContext p_80451_, LootItemCondition[] p_80452_) {
         List<Enchantment> list = Lists.newArrayList();
         if (p_80450_.has("enchantments")) {
            for(JsonElement jsonelement : GsonHelper.getAsJsonArray(p_80450_, "enchantments")) {
               String s = GsonHelper.convertToString(jsonelement, "enchantment");
               Enchantment enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown enchantment '" + s + "'");
               });
               list.add(enchantment);
            }
         }

         return new EnchantRandomlyFunction(p_80452_, list);
      }
   }
}