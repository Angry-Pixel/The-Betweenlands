package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetStewEffectFunction extends LootItemConditionalFunction {
   final Map<MobEffect, NumberProvider> effectDurationMap;

   SetStewEffectFunction(LootItemCondition[] p_81216_, Map<MobEffect, NumberProvider> p_81217_) {
      super(p_81216_);
      this.effectDurationMap = ImmutableMap.copyOf(p_81217_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_STEW_EFFECT;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.effectDurationMap.values().stream().flatMap((p_165470_) -> {
         return p_165470_.getReferencedContextParams().stream();
      }).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack run(ItemStack p_81223_, LootContext p_81224_) {
      if (p_81223_.is(Items.SUSPICIOUS_STEW) && !this.effectDurationMap.isEmpty()) {
         Random random = p_81224_.getRandom();
         int i = random.nextInt(this.effectDurationMap.size());
         Entry<MobEffect, NumberProvider> entry = Iterables.get(this.effectDurationMap.entrySet(), i);
         MobEffect mobeffect = entry.getKey();
         int j = entry.getValue().getInt(p_81224_);
         if (!mobeffect.isInstantenous()) {
            j *= 20;
         }

         SuspiciousStewItem.saveMobEffect(p_81223_, mobeffect, j);
         return p_81223_;
      } else {
         return p_81223_;
      }
   }

   public static SetStewEffectFunction.Builder stewEffect() {
      return new SetStewEffectFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<SetStewEffectFunction.Builder> {
      private final Map<MobEffect, NumberProvider> effectDurationMap = Maps.newHashMap();

      protected SetStewEffectFunction.Builder getThis() {
         return this;
      }

      public SetStewEffectFunction.Builder withEffect(MobEffect p_165473_, NumberProvider p_165474_) {
         this.effectDurationMap.put(p_165473_, p_165474_);
         return this;
      }

      public LootItemFunction build() {
         return new SetStewEffectFunction(this.getConditions(), this.effectDurationMap);
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetStewEffectFunction> {
      public void serialize(JsonObject p_81247_, SetStewEffectFunction p_81248_, JsonSerializationContext p_81249_) {
         super.serialize(p_81247_, p_81248_, p_81249_);
         if (!p_81248_.effectDurationMap.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(MobEffect mobeffect : p_81248_.effectDurationMap.keySet()) {
               JsonObject jsonobject = new JsonObject();
               ResourceLocation resourcelocation = Registry.MOB_EFFECT.getKey(mobeffect);
               if (resourcelocation == null) {
                  throw new IllegalArgumentException("Don't know how to serialize mob effect " + mobeffect);
               }

               jsonobject.add("type", new JsonPrimitive(resourcelocation.toString()));
               jsonobject.add("duration", p_81249_.serialize(p_81248_.effectDurationMap.get(mobeffect)));
               jsonarray.add(jsonobject);
            }

            p_81247_.add("effects", jsonarray);
         }

      }

      public SetStewEffectFunction deserialize(JsonObject p_81239_, JsonDeserializationContext p_81240_, LootItemCondition[] p_81241_) {
         Map<MobEffect, NumberProvider> map = Maps.newHashMap();
         if (p_81239_.has("effects")) {
            for(JsonElement jsonelement : GsonHelper.getAsJsonArray(p_81239_, "effects")) {
               String s = GsonHelper.getAsString(jsonelement.getAsJsonObject(), "type");
               MobEffect mobeffect = Registry.MOB_EFFECT.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown mob effect '" + s + "'");
               });
               NumberProvider numberprovider = GsonHelper.getAsObject(jsonelement.getAsJsonObject(), "duration", p_81240_, NumberProvider.class);
               map.put(mobeffect, numberprovider);
            }
         }

         return new SetStewEffectFunction(p_81241_, map);
      }
   }
}