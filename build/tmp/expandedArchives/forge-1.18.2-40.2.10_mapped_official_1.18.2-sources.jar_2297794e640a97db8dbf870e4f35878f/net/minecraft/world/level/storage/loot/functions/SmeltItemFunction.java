package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Optional;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class SmeltItemFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();

   SmeltItemFunction(LootItemCondition[] p_81263_) {
      super(p_81263_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.FURNACE_SMELT;
   }

   public ItemStack run(ItemStack p_81268_, LootContext p_81269_) {
      if (p_81268_.isEmpty()) {
         return p_81268_;
      } else {
         Optional<SmeltingRecipe> optional = p_81269_.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(p_81268_), p_81269_.getLevel());
         if (optional.isPresent()) {
            ItemStack itemstack = optional.get().getResultItem();
            if (!itemstack.isEmpty()) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(p_81268_.getCount() * itemstack.getCount()); //Forge: Support smelting returning multiple
               return itemstack1;
            }
         }

         LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object)p_81268_);
         return p_81268_;
      }
   }

   public static LootItemConditionalFunction.Builder<?> smelted() {
      return simpleBuilder(SmeltItemFunction::new);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SmeltItemFunction> {
      public SmeltItemFunction deserialize(JsonObject p_81274_, JsonDeserializationContext p_81275_, LootItemCondition[] p_81276_) {
         return new SmeltItemFunction(p_81276_);
      }
   }
}
