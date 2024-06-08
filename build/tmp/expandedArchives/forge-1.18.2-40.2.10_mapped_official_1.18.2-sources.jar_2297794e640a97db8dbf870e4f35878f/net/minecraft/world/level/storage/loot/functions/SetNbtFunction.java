package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetNbtFunction extends LootItemConditionalFunction {
   final CompoundTag tag;

   SetNbtFunction(LootItemCondition[] p_81176_, CompoundTag p_81177_) {
      super(p_81176_);
      this.tag = p_81177_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_NBT;
   }

   public ItemStack run(ItemStack p_81183_, LootContext p_81184_) {
      p_81183_.getOrCreateTag().merge(this.tag);
      return p_81183_;
   }

   /** @deprecated */
   @Deprecated
   public static LootItemConditionalFunction.Builder<?> setTag(CompoundTag p_81188_) {
      return simpleBuilder((p_81191_) -> {
         return new SetNbtFunction(p_81191_, p_81188_);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetNbtFunction> {
      public void serialize(JsonObject p_81203_, SetNbtFunction p_81204_, JsonSerializationContext p_81205_) {
         super.serialize(p_81203_, p_81204_, p_81205_);
         p_81203_.addProperty("tag", p_81204_.tag.toString());
      }

      public SetNbtFunction deserialize(JsonObject p_81195_, JsonDeserializationContext p_81196_, LootItemCondition[] p_81197_) {
         try {
            CompoundTag compoundtag = TagParser.parseTag(GsonHelper.getAsString(p_81195_, "tag"));
            return new SetNbtFunction(p_81197_, compoundtag);
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException(commandsyntaxexception.getMessage());
         }
      }
   }
}