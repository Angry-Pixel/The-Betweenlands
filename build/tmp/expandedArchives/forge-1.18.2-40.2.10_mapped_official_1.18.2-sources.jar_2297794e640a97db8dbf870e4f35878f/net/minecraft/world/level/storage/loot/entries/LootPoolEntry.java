package net.minecraft.world.level.storage.loot.entries;

import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public interface LootPoolEntry {
   int getWeight(float p_79632_);

   void createItemStack(Consumer<ItemStack> p_79633_, LootContext p_79634_);
}