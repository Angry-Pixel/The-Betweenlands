package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyExplosionDecay extends LootItemConditionalFunction {
   ApplyExplosionDecay(LootItemCondition[] p_80029_) {
      super(p_80029_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.EXPLOSION_DECAY;
   }

   public ItemStack run(ItemStack p_80034_, LootContext p_80035_) {
      Float f = p_80035_.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
      if (f != null) {
         Random random = p_80035_.getRandom();
         float f1 = 1.0F / f;
         int i = p_80034_.getCount();
         int j = 0;

         for(int k = 0; k < i; ++k) {
            if (random.nextFloat() <= f1) {
               ++j;
            }
         }

         p_80034_.setCount(j);
      }

      return p_80034_;
   }

   public static LootItemConditionalFunction.Builder<?> explosionDecay() {
      return simpleBuilder(ApplyExplosionDecay::new);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<ApplyExplosionDecay> {
      public ApplyExplosionDecay deserialize(JsonObject p_80040_, JsonDeserializationContext p_80041_, LootItemCondition[] p_80042_) {
         return new ApplyExplosionDecay(p_80042_);
      }
   }
}