package net.minecraft.world.item;

import net.minecraft.world.item.crafting.Ingredient;

public interface Tier {
   int getUses();

   float getSpeed();

   float getAttackDamageBonus();

   @Deprecated // FORGE: Use TierSortingRegistry to define which tiers are better than others
   int getLevel();

   int getEnchantmentValue();

   Ingredient getRepairIngredient();

   @javax.annotation.Nullable default net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getTag() { return null; }
}
