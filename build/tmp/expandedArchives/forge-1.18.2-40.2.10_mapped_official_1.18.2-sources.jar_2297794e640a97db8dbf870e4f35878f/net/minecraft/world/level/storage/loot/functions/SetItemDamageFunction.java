package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import java.util.Set;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.slf4j.Logger;

public class SetItemDamageFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   final NumberProvider damage;
   final boolean add;

   SetItemDamageFunction(LootItemCondition[] p_165427_, NumberProvider p_165428_, boolean p_165429_) {
      super(p_165427_);
      this.damage = p_165428_;
      this.add = p_165429_;
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_DAMAGE;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.damage.getReferencedContextParams();
   }

   public ItemStack run(ItemStack p_81048_, LootContext p_81049_) {
      if (p_81048_.isDamageableItem()) {
         int i = p_81048_.getMaxDamage();
         float f = this.add ? 1.0F - (float)p_81048_.getDamageValue() / (float)i : 0.0F;
         float f1 = 1.0F - Mth.clamp(this.damage.getFloat(p_81049_) + f, 0.0F, 1.0F);
         p_81048_.setDamageValue(Mth.floor(f1 * (float)i));
      } else {
         LOGGER.warn("Couldn't set damage of loot item {}", (Object)p_81048_);
      }

      return p_81048_;
   }

   public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider p_165431_) {
      return simpleBuilder((p_165441_) -> {
         return new SetItemDamageFunction(p_165441_, p_165431_, false);
      });
   }

   public static LootItemConditionalFunction.Builder<?> setDamage(NumberProvider p_165433_, boolean p_165434_) {
      return simpleBuilder((p_165438_) -> {
         return new SetItemDamageFunction(p_165438_, p_165433_, p_165434_);
      });
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetItemDamageFunction> {
      public void serialize(JsonObject p_81068_, SetItemDamageFunction p_81069_, JsonSerializationContext p_81070_) {
         super.serialize(p_81068_, p_81069_, p_81070_);
         p_81068_.add("damage", p_81070_.serialize(p_81069_.damage));
         p_81068_.addProperty("add", p_81069_.add);
      }

      public SetItemDamageFunction deserialize(JsonObject p_81060_, JsonDeserializationContext p_81061_, LootItemCondition[] p_81062_) {
         NumberProvider numberprovider = GsonHelper.getAsObject(p_81060_, "damage", p_81061_, NumberProvider.class);
         boolean flag = GsonHelper.getAsBoolean(p_81060_, "add", false);
         return new SetItemDamageFunction(p_81062_, numberprovider, flag);
      }
   }
}