package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetPotionFunction extends LootItemConditionalFunction {
   final Potion potion;

   SetPotionFunction(LootItemCondition[] p_193069_, Potion p_193070_) {
      super(p_193069_);
      this.potion = p_193070_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_POTION;
   }

   public ItemStack run(ItemStack p_193073_, LootContext p_193074_) {
      PotionUtils.setPotion(p_193073_, this.potion);
      return p_193073_;
   }

   public static LootItemConditionalFunction.Builder<?> setPotion(Potion p_193076_) {
      return simpleBuilder((p_193079_) -> {
         return new SetPotionFunction(p_193079_, p_193076_);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetPotionFunction> {
      public void serialize(JsonObject p_193090_, SetPotionFunction p_193091_, JsonSerializationContext p_193092_) {
         super.serialize(p_193090_, p_193091_, p_193092_);
         p_193090_.addProperty("id", Registry.POTION.getKey(p_193091_.potion).toString());
      }

      public SetPotionFunction deserialize(JsonObject p_193082_, JsonDeserializationContext p_193083_, LootItemCondition[] p_193084_) {
         String s = GsonHelper.getAsString(p_193082_, "id");
         Potion potion = Registry.POTION.getOptional(ResourceLocation.tryParse(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + s + "'");
         });
         return new SetPotionFunction(p_193084_, potion);
      }
   }
}