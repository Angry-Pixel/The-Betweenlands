package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetItemCountFunction extends LootItemConditionalFunction {
   final NumberProvider value;
   final boolean add;

   SetItemCountFunction(LootItemCondition[] p_165409_, NumberProvider p_165410_, boolean p_165411_) {
      super(p_165409_);
      this.value = p_165410_;
      this.add = p_165411_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_COUNT;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.value.getReferencedContextParams();
   }

   public ItemStack run(ItemStack p_81006_, LootContext p_81007_) {
      int i = this.add ? p_81006_.getCount() : 0;
      p_81006_.setCount(Mth.clamp(i + this.value.getInt(p_81007_), 0, p_81006_.getMaxStackSize()));
      return p_81006_;
   }

   public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider p_165413_) {
      return simpleBuilder((p_165423_) -> {
         return new SetItemCountFunction(p_165423_, p_165413_, false);
      });
   }

   public static LootItemConditionalFunction.Builder<?> setCount(NumberProvider p_165415_, boolean p_165416_) {
      return simpleBuilder((p_165420_) -> {
         return new SetItemCountFunction(p_165420_, p_165415_, p_165416_);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemCountFunction> {
      public void serialize(JsonObject p_81026_, SetItemCountFunction p_81027_, JsonSerializationContext p_81028_) {
         super.serialize(p_81026_, p_81027_, p_81028_);
         p_81026_.add("count", p_81028_.serialize(p_81027_.value));
         p_81026_.addProperty("add", p_81027_.add);
      }

      public SetItemCountFunction deserialize(JsonObject p_81018_, JsonDeserializationContext p_81019_, LootItemCondition[] p_81020_) {
         NumberProvider numberprovider = GsonHelper.getAsObject(p_81018_, "count", p_81019_, NumberProvider.class);
         boolean flag = GsonHelper.getAsBoolean(p_81018_, "add", false);
         return new SetItemCountFunction(p_81020_, numberprovider, flag);
      }
   }
}