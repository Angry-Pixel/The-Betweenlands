package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class LootingEnchantFunction extends LootItemConditionalFunction {
   public static final int NO_LIMIT = 0;
   final NumberProvider value;
   final int limit;

   LootingEnchantFunction(LootItemCondition[] p_165226_, NumberProvider p_165227_, int p_165228_) {
      super(p_165226_);
      this.value = p_165227_;
      this.limit = p_165228_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.LOOTING_ENCHANT;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(ImmutableSet.of(LootContextParams.KILLER_ENTITY), this.value.getReferencedContextParams());
   }

   boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack run(ItemStack p_80789_, LootContext p_80790_) {
      Entity entity = p_80790_.getParamOrNull(LootContextParams.KILLER_ENTITY);
      if (entity instanceof LivingEntity) {
         int i = p_80790_.getLootingModifier();
         if (i == 0) {
            return p_80789_;
         }

         float f = (float)i * this.value.getFloat(p_80790_);
         p_80789_.grow(Math.round(f));
         if (this.hasLimit() && p_80789_.getCount() > this.limit) {
            p_80789_.setCount(this.limit);
         }
      }

      return p_80789_;
   }

   public static LootingEnchantFunction.Builder lootingMultiplier(NumberProvider p_165230_) {
      return new LootingEnchantFunction.Builder(p_165230_);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<LootingEnchantFunction.Builder> {
      private final NumberProvider count;
      private int limit = 0;

      public Builder(NumberProvider p_165232_) {
         this.count = p_165232_;
      }

      protected LootingEnchantFunction.Builder getThis() {
         return this;
      }

      public LootingEnchantFunction.Builder setLimit(int p_80807_) {
         this.limit = p_80807_;
         return this;
      }

      public LootItemFunction build() {
         return new LootingEnchantFunction(this.getConditions(), this.count, this.limit);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<LootingEnchantFunction> {
      public void serialize(JsonObject p_80820_, LootingEnchantFunction p_80821_, JsonSerializationContext p_80822_) {
         super.serialize(p_80820_, p_80821_, p_80822_);
         p_80820_.add("count", p_80822_.serialize(p_80821_.value));
         if (p_80821_.hasLimit()) {
            p_80820_.add("limit", p_80822_.serialize(p_80821_.limit));
         }

      }

      public LootingEnchantFunction deserialize(JsonObject p_80812_, JsonDeserializationContext p_80813_, LootItemCondition[] p_80814_) {
         int i = GsonHelper.getAsInt(p_80812_, "limit", 0);
         return new LootingEnchantFunction(p_80814_, GsonHelper.getAsObject(p_80812_, "count", p_80813_, NumberProvider.class), i);
      }
   }
}
