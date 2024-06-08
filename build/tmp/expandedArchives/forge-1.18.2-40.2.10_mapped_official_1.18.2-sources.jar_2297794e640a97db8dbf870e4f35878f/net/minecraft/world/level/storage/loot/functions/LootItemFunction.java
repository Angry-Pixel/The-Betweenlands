package net.minecraft.world.level.storage.loot.functions;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

public interface LootItemFunction extends LootContextUser, BiFunction<ItemStack, LootContext, ItemStack> {
   LootItemFunctionType getType();

   static Consumer<ItemStack> decorate(BiFunction<ItemStack, LootContext, ItemStack> p_80725_, Consumer<ItemStack> p_80726_, LootContext p_80727_) {
      return (p_80732_) -> {
         p_80726_.accept(p_80725_.apply(p_80732_, p_80727_));
      };
   }

   public interface Builder {
      LootItemFunction build();
   }
}