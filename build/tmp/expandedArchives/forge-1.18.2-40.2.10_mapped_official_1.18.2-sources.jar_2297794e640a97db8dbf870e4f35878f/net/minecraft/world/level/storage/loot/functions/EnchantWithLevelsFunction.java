package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
   final NumberProvider levels;
   final boolean treasure;

   EnchantWithLevelsFunction(LootItemCondition[] p_165193_, NumberProvider p_165194_, boolean p_165195_) {
      super(p_165193_);
      this.levels = p_165194_;
      this.treasure = p_165195_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.ENCHANT_WITH_LEVELS;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.levels.getReferencedContextParams();
   }

   public ItemStack run(ItemStack p_80483_, LootContext p_80484_) {
      Random random = p_80484_.getRandom();
      return EnchantmentHelper.enchantItem(random, p_80483_, this.levels.getInt(p_80484_), this.treasure);
   }

   public static EnchantWithLevelsFunction.Builder enchantWithLevels(NumberProvider p_165197_) {
      return new EnchantWithLevelsFunction.Builder(p_165197_);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<EnchantWithLevelsFunction.Builder> {
      private final NumberProvider levels;
      private boolean treasure;

      public Builder(NumberProvider p_165200_) {
         this.levels = p_165200_;
      }

      protected EnchantWithLevelsFunction.Builder getThis() {
         return this;
      }

      public EnchantWithLevelsFunction.Builder allowTreasure() {
         this.treasure = true;
         return this;
      }

      public LootItemFunction build() {
         return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<EnchantWithLevelsFunction> {
      public void serialize(JsonObject p_80506_, EnchantWithLevelsFunction p_80507_, JsonSerializationContext p_80508_) {
         super.serialize(p_80506_, p_80507_, p_80508_);
         p_80506_.add("levels", p_80508_.serialize(p_80507_.levels));
         p_80506_.addProperty("treasure", p_80507_.treasure);
      }

      public EnchantWithLevelsFunction deserialize(JsonObject p_80502_, JsonDeserializationContext p_80503_, LootItemCondition[] p_80504_) {
         NumberProvider numberprovider = GsonHelper.getAsObject(p_80502_, "levels", p_80503_, NumberProvider.class);
         boolean flag = GsonHelper.getAsBoolean(p_80502_, "treasure", false);
         return new EnchantWithLevelsFunction(p_80504_, numberprovider, flag);
      }
   }
}